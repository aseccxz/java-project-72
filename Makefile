run-dist:
	./build/install/app/bin/app
report:
	./gradlew jacocoTestReport
make build:
	./gradlew build
.PHONY: build
