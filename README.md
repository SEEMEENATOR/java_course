1. Добавлены зависимости для SLF4J и Logback
2. Создан файл logback.xml который определяет, куда отправлять логи
3. В классе ChecRunner создан экземпляр логгера, с помощью него вызываются методы для логгирования
4. Запуск: java -jar build/libs/clevertec-check-0.1.0.jar 3-1 2-5 5-1 discountCard=1111 balanceDebitCard=100 saveToFile=./result.csv datasource.url=jdbc:postgresql://localhost:5432/check datasource.username=postgres datasource.password=75980
