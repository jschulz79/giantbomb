Giant Bomb search and rent application

This app is built with Vaadin, Spring Boot, Kotlin, and Gradle.

Note, to run locally, you will need to put your GiantBomb api key
in a file named `application-local.properties` files under `src/main/resources`
in the format `api-key=yourkeyvaluehere`

There are a few ways to run it locally:

1. Via IntelliJ (run the Main.kt file)
2. Via the Spring Boot plugin for Gradle 
``` shell
 /gradlew -Dspring.active.profiles=local bootRun
```
3. Build the project via gradle and run the jar file
``` shell
./gradlew clean build
java -jar -Dspring.profiles.active=local ./build/libs/giantbomb.jar 
```