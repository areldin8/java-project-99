FROM gradle:8.7-jdk21

WORKDIR /app

COPY /app .

RUN ./gradlew installDist

CMD ./build/istall/app/bin/app -- spring.profiles.active=production