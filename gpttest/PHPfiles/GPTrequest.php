
<?php
if(isset($_GET['horo'])){
    $promt = (isset($_GET['horo'])) ? (trim($_GET['horo'])) : "";
	
    $error = "";
    if(mb_strlen($promt) < 1){
        $error = "Короткий промт";
    }
    if(!$error){
        // Отправляем запрос к ChatGPT
        $openai_key = "/////";
      $url = 'https://api.openai.com/v1/chat/completions';
        $data = array(
            'messages' => array(
                array('role' => 'system', 'content' => "ОТВЕЧАЙ НА ЯЗЫКЕ, КОТОРЫЙ ТЕБЕ УКАЗАН! Формат ввода ПОЛЬЗОВАТЕЛЯ: 'ЯЗЫК ТВОЕГО ОТВЕТА';'знак_зодиака';'категория_гадания';'Дата Рождения'.
				ты - профессиональный квалифицированный астролог. Напиши гороскоп сдержанно и осторожно, представляя его максимально серъезно, но при этом оставляя место для интерпретации. Не пиши заголовок. Избегай конкретики и крайностей, гороскоп должен быть спокойным. Используй emoji, заголовок пиши большим. Пользуйся всеми вашими знаниями астрологии для составления гороскопа на ТЕКУШУЮ ДАТУ:". date(DATE_ATOM) .". "),
                array('role' => 'user', 'content' => $promt)
            ),
            'model' => 'gpt-3.5-turbo',
            'temperature' => 1,
            'max_tokens' => 2000
        );
        $options = array(
            'http' => array(
                'header'  => "Content-Type: application/json\r\nAuthorization: Bearer $openai_key",
                'method'  => 'POST',
                'content' => json_encode($data)
            )
        );
        $context  = stream_context_create($options);
        
        $response = file_get_contents($url, false, $context);
        if ($response !== false) {
           
            // Парсинг JSON-ответа
            $json_response = json_decode($response, true);
            $choices = $json_response['choices'];
            if (!empty($choices)) {
                $message = $choices[0]['message']['content'];
                // Форматирование текста с помощью HTML-тегов абзаца
                $formatted_message = "<p>" . str_replace("\n", "</p><p>", $message) . "</p>";
                // Вывод отформатированного текста
                echo $formatted_message;
            } else {
                echo "Ошибка: Не удалось получить гороскоп.";
            }
        } else {
            echo "Ошибка: Не удалось получить ответ от ChatGPT.";
        }
    } else {
        echo "Ошибка: Не удалось подключиться к ChatGPT API.";
    }
}
?>