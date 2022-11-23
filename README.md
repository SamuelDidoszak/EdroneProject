# EdroneProject
A unique string generator project made for the Edrone recruitment process

## Running
The app can be ran either by 
- calling `mvn spring-boot:run` at the root of the downloaded package
- calling `java -jar target/EdroneProject-1.0.jar`

## Endpoints
### /files
Returns all of generated files as a multipart response

### /threads
Returns the number of currently running threads

### /new
Creates a new file of unique strings either by generating it, or fetching from the database
#### required parameters
- amount Integer
- minLength Integer
- maxLength Integer
- chars String

## Resolving issues
If a problem with not existant mysql database arises, database strings has to be created

Schema for the database can be copied from below or downloaded as a .sql file at the root of the package

```
CREATE DATABASE IF NOT EXISTS `strings`;
USE `strings`;
CREATE TABLE IF NOT EXISTS `strings` (
  `params` varchar(50) COLLATE utf8_polish_ci NOT NULL,
  `file` blob NOT NULL,
  PRIMARY KEY (`params`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;
```

Credentials used by the spring-boot:
- username=springuser
- password=spring
