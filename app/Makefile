setup:
		./gradlew wrapper --gradle-version 8.7
		./gradlew build

start-dev:
		./gradlew bootRun --args='--spring.profiles.active=dev'

clean:
	./gradlew clean

build:
	./gradlew clean build

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

install:
	./gradlew installDist

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

clean:
	./gradlew clean

local:
	./gradlew bootRun --args='--spring.profiles.active=file'

.PHONY: build