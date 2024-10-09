<?php
// Подключение к базе данных
$servername = "////";
$username = "///";
$password = "///";
$dbname = "////";

$conn = new mysqli($servername, $username, $password, $dbname);

// Проверка соединения
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

// Генерация проверочного кода
$authCode = generateAuthCode();

// Сохранение проверочного кода в базе данных
$sql = "INSERT INTO auth_codes (auth_code) VALUES ('$authCode')";
if ($conn->query($sql) === TRUE) {
  echo "Auth code generated successfully";
} else {
  echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();

function generateAuthCode() {
  
  return rand(10000, 99999); 
}
?>