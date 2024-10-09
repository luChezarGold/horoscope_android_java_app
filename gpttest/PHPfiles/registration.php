<?php
// Подключаемся к базе данных
$servername = "localhost";
$username = "////";
$password = "/////";
$dbname = "////";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// API для регистрации пользователей и отправки кода подтверждения
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['register'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];

    // Проверяем, существует ли пользователь с указанной почтой
    $sql_check_user = "SELECT * FROM users WHERE email='$email'";
    $result_check_user = $conn->query($sql_check_user);
    if ($result_check_user->num_rows > 0) {
        // Пользователь с указанной почтой существует, проверяем правильность пароля
        $user_data = $result_check_user->fetch_assoc();
        if (password_verify($password, $user_data['password'])) {
            $is_verified = $user_data['isVerified']; // Предположим, что это поле хранит статус подтверждения
            if ($is_verified) {
                // Если аккаунт подтвержден, выполните вход
                echo "Вход успешен.";
				exit();
            } else {
                // Если аккаунт не подтвержден, отправьте новый код подтверждения
				
                $new_verification_code = mt_rand(100000, 999999); // Генерация нового кода подтверждения
                $to = $email;
                $subject = "Новый код подтверждения";
                $message = "Ваш новый код подтверждения: $new_verification_code";
                mail($to, $subject, $message);

                // Обновление записи в базе данных с новым кодом подтверждения
                $update_sql = "UPDATE users SET verification_code = '$new_verification_code' WHERE email = '$email'";
                if ($conn->query($update_sql) === TRUE) {
                    echo "Account not verified.";
                    exit(); // Останавливаем выполнение скрипта после отправки кода подтверждения
                } else {
                    echo "Ошибка при отправке нового кода подтверждения: " . $conn->error;
                }
            }
        } else {
            echo "Неверный пароль.";
        }
    } else {
        // Пользователь с указанной почтой не существует, регистрируем нового пользователя
        $hashed_password = password_hash($password, PASSWORD_DEFAULT);
        $verification_code = mt_rand(100000, 999999);
        $sql_register_user = "INSERT INTO users (email, password, verification_code) VALUES ('$email', '$hashed_password', '$verification_code')";
        if ($conn->query($sql_register_user) === TRUE) {
            $to = $email;
            $subject = "Код подтверждения";
            $message = "Ваш код подтверждения: $verification_code";
            mail($to, $subject, $message);
            echo "Пользователь зарегистрирован. Код подтверждения отправлен на указанный email.";
			exit(); // Останавливаем выполнение скрипта после отправки кода подтверждения
        } else {
            echo "Ошибка при регистрации: " . $conn->error;
        }
    }
}

// API для получения lastDate из базы данных
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['get_last_date'])) {
    $email = $_POST['email'];

    // Получаем lastDate пользователя из базы данных
    $sql_get_last_date = "SELECT lastDate FROM users WHERE email='$email'";
    $result_get_last_date = $conn->query($sql_get_last_date);
    if ($result_get_last_date->num_rows > 0) {
        // Если пользователь найден, возвращаем lastDate
        $row = $result_get_last_date->fetch_assoc();
        $lastDate = $row['lastDate'];
        echo $lastDate;
    } else {
        echo "Дата не найдена для данного пользователя.";
    }
}

// API для обновления даты в базе данных
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['update_date'])) {
    $email = $_POST['email'];
    $newDate = $_POST['new_date'];

    // Обновляем дату пользователя в базе данных
    $update_sql = "UPDATE users SET lastDate = '$newDate' WHERE email = '$email'";
    if ($conn->query($update_sql) === TRUE) {
        echo "Дата успешно обновлена в базе данных.";
    } else {
        echo "Ошибка при обновлении даты: " . $conn->error;
    }
} else {
    echo "Ошибка: Неправильный метод запроса или параметры не переданы.";
}


// API для проверки кода подтверждения
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['verify'])) {
    $email = $_POST['email'];
    $verification_code = $_POST['verification_code'];
    $sql = "SELECT * FROM users WHERE email='$email' AND verification_code='$verification_code'";
    $result = $conn->query($sql);
    if ($result->num_rows > 0) {
        // Если код подтверждения верный, обновляем статус isVerified
        $update_sql = "UPDATE users SET isVerified = 1 WHERE email = '$email'";
        if ($conn->query($update_sql) === TRUE) {
            echo "Код подтверждения верный. Аккаунт успешно подтвержден.";
        } else {
            echo "Ошибка при обновлении статуса подтверждения: " . $conn->error;
        }
    } else {
        echo "Неверный код подтверждения.";
    }
} else {
    echo "Ошибка: Неправильный метод запроса или параметры не переданы.";
}

$conn->close();
?>
