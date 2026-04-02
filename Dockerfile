FROM gradle:9.0.0-jdk21

WORKDIR /app

COPY . .

RUN ./gradlew installDist

CMD ["./build/install/app/bin/app"]
