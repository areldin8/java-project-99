build:
	./gradlew clean build

install:
	./gradlew installDist

test:
	./gradlew test

lint:
	./gradlew checkstyleMain

report:
	./gradlew jacocoTestReport

dev:
	./gradlew bootRun --args='--spring.profiles.active=dev'

prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

.PHONY: build