FROM gradle:8.7-jdk21

WORKDIR /

COPY / .

RUN ./gradlew installDist

CMD ./build/install/app/bin/app