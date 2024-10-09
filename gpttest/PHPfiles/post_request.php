<?php
$servername = "localhost";
$username = "////";
$password = "////";
$dbname = "/////";

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Подключаемся к базе данных
    $conn = new mysqli($servername, $username, $password, $dbname);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // Получаем данные из POST-запроса
    $sign = $_POST['promt'];

    // Вставляем данные в базу данных
    $sql = "INSERT INTO Test_prompts (prompt_zodiac) VALUES ('$sign')";
    if ($conn->query($sql) === TRUE) {
        echo "Данные успешно отправлены";
    } else {
        echo "Ошибка: " . $conn->error;
    }

    $conn->close();
} else {
    // Выполняем запрос к базе данных
    $conn = new mysqli($servername, $username, $password, $dbname);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // Выбираем последние 5 записей из базы данных
    $sql = "SELECT * FROM Test_prompts ORDER BY id DESC LIMIT 5";
    $result = $conn->query($sql);

    // Проверяем, есть ли результат
    if ($result->num_rows > 0) {
        // Создаем массив для хранения всех записей
        $response = array();
        // Проходимся по результатам запроса и добавляем их в массив
        while ($row = $result->fetch_assoc()) {
            $response[] = $row;
        }
        // Возвращаем результат в формате JSON
        echo json_encode($response);
    } else {
        echo "0 results";
    }
    $conn->close();
}
?>
