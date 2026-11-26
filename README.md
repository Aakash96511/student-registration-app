# Student Registration App

Simple Student Registration Form built with HTML and Java 21 / Maven, demonstrated with GitHub, Jenkins, GitHub Actions, and Docker.

## Run Locally with Maven
```bash
mvn package
java -jar target/student-registration-1.0.0.jar
```
Open `http://localhost:8085` in a browser.

## Run with Docker
```bash
docker build -t student-registration:1.0.0 .
docker run -d -p 8085:8085 --name student-registration-app student-registration:1.0.0
```
Open `http://localhost:8085` in a browser.
