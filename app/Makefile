build:
	./gradlew clean build

install:
	./gradlew installDist

start:
	./gradlew bootRun --args='--spring.profiles.active=development'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=production'

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

dev:
	./gradlew run

checkstyleMain:
	./gradlew checkstyleMain

.PHONY: build