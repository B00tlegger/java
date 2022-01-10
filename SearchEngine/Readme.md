<h1 align="center">Search Engine</h1>
<h5 align="center">(Дипломный проект Skillbox)</h5>

Поисковый движок реализованный на Java в качестве дипломной работы.</br>
В разработке проекта исмользовались:</br>

- Spring Framework:</br>
    - Spring Boot
    - Spring Web
    - Spring Boot
    - Spring Data JPA
    - Spring Thymeleaf
- MySQL
- Jsoup
- Lombok
- Apache Lucene Morphology

Движок сканирует сайты перечисленные в application.yml, заносит в базу MySQL и производит по ним поиск. Для добывления
сайта необходимо внеси его в application.yml</br>
<pre>application:
 sites: </pre>
В настройках необходимо указать URL и название сайта в колонках в одноименных полях.</br>

<h2 align="center">Интерфейс</h2>

Графический инетфейс разделён на 3 части.</br>

![image](https://user-images.githubusercontent.com/88447317/147097514-0904867d-c1a4-4961-b87d-2a4f3103e707.png) </br>

Отоброжает состояние просканированных сайтов, когда сайты сканируются отображается синим значком и статусом "INDEXING"
,</br>

![image](https://user-images.githubusercontent.com/88447317/147097548-96ca0a54-5e19-4698-83e9-f5515513b1ef.png) </br>

или когда они просканированны отображается зелёным и статусом "INDEXED", так же если сайт полностью заблокирован для
сканера, отображается красным значком со статусом "FAILED".</br>

![image](https://user-images.githubusercontent.com/88447317/147097597-fbbd6423-9763-4ef1-b178-74f0b32f1361.png)</br>

![image](https://user-images.githubusercontent.com/88447317/147097628-f35345da-1dde-4f90-b9d8-301b9b62128f.png)</br>

Отвечает за сканирование сайтов и отдельных страниц.

![image](https://user-images.githubusercontent.com/88447317/147097677-559c811b-c381-4562-855e-f6b435a9ce5e.png)</br>

если по какой-то причине страница сайта была не отсканированна, её можно добавить в ручную или заменить, если она была
изменена.</br>

![image](https://user-images.githubusercontent.com/88447317/147097778-6ea0b1ae-4475-4421-9686-51fc609159f3.png)</br>

Производит поиск на данном этапе пока только по всем сайтом, из-за недоработки граффического интерфейса и выдаёт
их в отсортированном виде по релевантности разбивая на страницы.</br>

![image](https://user-images.githubusercontent.com/88447317/147097824-19fa5e38-9527-4417-b659-d5320359d069.png)</br>

![image](https://user-images.githubusercontent.com/88447317/147097856-ccb24066-905c-4622-a5cc-c50d7e7a6b20.png)</br>

<h2 align="center">Демо</h2>

Производит поиск по сайту https://nikoartgallery.com 

https://bulgakov-search-engine.herokuapp.com/
