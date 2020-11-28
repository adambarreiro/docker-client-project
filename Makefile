jacocoReportPath := "target/site/jacoco/index.html"
checkstyleReportPath := "target/site/checkstyle.html"
dockerClientModule := "docker-java-client"
ubuntuStatsApp := "ubuntu-stats-app"
mvn := "./mvnw"

.PHONY: build
build: check
	@$(mvn) install

.PHONY: run
run: check
	@echo 'Once the application starts, open http://localhost:8080'
	@java -Xmx512m -Xms256m -jar $(ubuntuStatsApp)/target/ubuntu-stats-app-*.jar

.PHONY: test
test: check clean
	@$(mvn) test

.PHONY: coverage-check
coverage-check: check
	@if [[ ! -f "$(dockerClientModule)/$(jacocoReportPath)" ]] || [[ ! -f "$(ubuntuStatsApp)/$(jacocoReportPath)" ]]; then $(mvn) clean install; fi
	@open $(dockerClientModule)/$(jacocoReportPath) &
	@open $(ubuntuStatsApp)/$(jacocoReportPath) &

.PHONY: bugs
bugs: check
	@$(mvn) spotbugs:check spotbugs:gui

.PHONY: docker-build
docker-build: check
	@docker build . -t docker-client-project:1.0.0

.PHONY: docker-run
docker-run: check
	@echo 'Once the container starts, open http://localhost:8080'
	@docker run -p 8080:8080 -v /var/run/docker.sock:/var/run/docker.sock docker-client-project:1.0.0

.PHONY: clean
clean:
	@./mvnw -q clean

check:
	@check=docker info > /dev/null 2>&1; echo $?;
	@if [[ $check -ne 0 ]]; then echo "Please start Docker engine for this to work"; exit 1; fi

