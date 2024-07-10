Получать чек (в ответе должен быть CSV-файл)
POST http://localhost:8080/check

{
"products": [
{
"id": 1,
"quantity": 5
},
{
"id": 1,
"quantity": 5
}
],
"discountCard": 1234,
"balanceDebitCard": 100
}

● Вернуть товар из БД
GET http://localhost:8080/products?id=1

● Добавить товар в БД
POST http://localhost:8080/products

{
"description": "Eat 100g.",
"price": 3.25,
"quantity": 5,
"isWholesale": true
}

● Обновить товар в БД
PUT http://localhost:8080/products?id=1

{
"description": "Chocolate Ritter sport 100g.",
"price": 3.25,
"quantity": 5,
"isWholesale ": true
}

● Удалить товар из БД
DELETE http://localhost:8080/products?id=1

● Вернуть дисконтную карту из БД
GET http://localhost:8080/discountcards?id=1

● Добавить дисконтную карту в БД
POST http://localhost:8080/discountcards

{
"discountCard": 5265,
"discountAmount": 2
}

● Обновить дисконтную карту в БД по id.
PUT http://localhost:8080/discountcards?id=1

{
"discountCard": 6786,
"discountAmount": 3
}

● Удалить дисконтную карту из БД
DELETE http://localhost:8080/discountcards?id=1
