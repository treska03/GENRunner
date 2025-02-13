# Changelog

## [0.0.1] - 2024-12-11
### Added
- Dodano konfigurację MongoDB w MongoConfiguration.java.
- Dodano kontroler eksperymentów w ExperimentController.java.
- Dodano DTO dla tworzenia eksperymentów w CreateExperimentRequest.java i CreateExperimentResponse.java.
- Dodano DTO dla wyników eksperymentów w ExperimentResultsResponse.java.
- Dodano DTO dla statusu eksperymentów w ExperimentStatusResponse.java.
- Dodano klasę główną aplikacji w GenRunnerApplication.java.
- Dodano model eksperymentu w Experiment.java.
- Dodano model wyników eksperymentu w ExperimentResult.java.
- Dodano model statusu eksperymentu w ExperimentStatus.java.
- Dodano model wyników iteracji w IterationResult.java.
- Dodano wyjątek dla braku metryk do zbierania w NoExperimentsMetricsToCollectException.java.
- Dodano repozytorium eksperymentów w ExperimentRepository.java.
- Dodano repozytorium wyników eksperymentów w ExperimentResultRepository.java.
- Dodano serwis do zarządzania eksperymentami w ExperimentService.java.
- Dodano serwis do uruchamiania eksperymentów w ExperimentRunnerService.java.
- Dodano konfigurację Gradle w [`build.gradle.kts`](build.gradle.kts ) i [`settings.gradle.kts`](settings.gradle.kts ).
- Dodano konfigurację Docker Compose dla MongoDB w docker-compose.yml.
- Dodano dokumentację dla MongoDB w mongodb.md.
- Dodano dokumentację dla Dockera w docker.md.
- Dodano plik [`README.md`](README.md ) z instrukcjami uruchomienia projektu.

### Changed
- Przeniesiono pliki DTO w module CLI do odpowiednich pakietów.
- Zaktualizowano plik [`.gitignore`](.gitignore ) o nowe wpisy dla IntelliJ IDEA, Eclipse, NetBeans i VS Code.

## [0.0.2] - 2024-12-12
### Added
- Dodano klienta API w ApiClient.java.
- Dodano klasę główną CLI w Client.java.
- Dodano DTO dla tworzenia eksperymentów w CLI w CreateExperimentRequestDto.java i CreateExperimentResponseDto.java.
- Dodano DTO dla wyników eksperymentów w CLI w ExperimentResultsResponseDto.java.
- Dodano DTO dla statusu eksperymentów w CLI w ExperimentStatusResponseDto.java.
- Dodano model statusu eksperymentu w CLI w ExperimentStatus.java.
- Dodano model wyników iteracji w CLI w IterationResult.java.
- Dodano konfigurację Gradle dla modułu CLI w [`cli/build.gradle`](cli/build.gradle ) i [`cli/settings.gradle`](cli/settings.gradle ).

### Changed
- Zaktualizowano plik [`.gitignore`](.gitignore ) w module CLI o nowe wpisy dla IntelliJ IDEA, Eclipse, NetBeans i VS Code.

### Fixed
- Naprawiono błąd w metodzie [`formatResultsResponse`]() w ApiClient.java dotyczący formatowania wyników eksperymentów.

## [0.0.3] - 2024-12-12
### Added
- Dodano testy jednostkowe dla modelu eksperymentu w ExperimentTest.java.
- Dodano testy jednostkowe dla modelu wyników eksperymentu w ExperimentResultTest.java.
- Dodano testy integracyjne dla eksperymentów w ExperimentIntegrationTest.java.
- Dodano testy jednostkowe dla enumów w EnumsTest.java.

### Changed
- Zaktualizowano plik [`src/main/resources/application.properties`](src/main/resources/application.properties ) o konfigurację MongoDB.

### Fixed
- Naprawiono błąd w metodzie [`getResults`]() w ExperimentService.java dotyczący pobierania wyników eksperymentów.

## [0.0.4] - 2024-12-12
### Changed
- Zaktualizowano plik [`README.md`](README.md ) o skład zespołu i instrukcje uruchomienia projektu.

### Fixed
- Naprawiono błąd w metodzie [`createExperiment`]() w ExperimentController.java dotyczący walidacji danych wejściowych.

## [0.0.5] - 2024-12-12
### Added
- Dodano plik [`Changelog.md`](Changelog.md ) z historią zmian projektu.

### Changed
- Zaktualizowano plik [`build.gradle.kts`](build.gradle.kts ) o nowe zależności dla Spring Boot i MongoDB.

### Fixed
- Naprawiono błąd w metodzie [`runExperimentAsync`]() w ExperimentRunnerService.java dotyczący uruchamiania eksperymentów asynchronicznie.


## [0.0.6] - 2024-12-13
### Changed
- Zaktualizowano cli by dostosowane bylo do najnowszego API

### Fixed
- Naprawiono błąd mapowania id eksperymentu z bazy danych do API

## [0.1.0] - 2024-12-13
- Release pierwszej wersji projektu

## [0.1.1] - 2024-12-15
### Changed
- Przebudowano cli

## [0.1.2] - 2024-12-18
### Changed
- Refactoring backendu:
  - Przerobienie DTO na rekordy
  - Wrzucenie URI do bazy danych do pliku konfiguracyjnego
  - Usunięcie nieużywanego kodu
  - Rozbicie `ExperimentRunnerService` na `ExperimentRunnerService` oraz `ExperimentExecutor`
- Poprawa instrukcji odnośnie setupowania projektu
- Przebudowa struktury repozytorium

## [0.1.3] - 2025-01-02
### Changed
- Dodanie mozliwosci odpalenia eksperymentow wiecej niz raz
- W endpointach zwracamy teraz inny format, zawierający numer odpalenia
- Dostosowano cli do nowego api
- Dodano możliwość filtrowania zakończonych eksperymentów

## [0.1.4] - 2025-01-08
### Added
- Dodano możliwość agregacji wyników eksperymentów po wielu parametrach
- Dodano testy dla agregacji

## [0.2.0] - 2025-01-25
### Added
- Dodano endpoint służący do usuwania eksperymentów (DELETE)
- Dodano serializacje optionali do objectmappera
- Dodano mozliwosc generowania wykresow zagregowanych wynikow
- Dodano mozliwosc generowania plikow csv zagregowanych wynikow
- Dodano mozliwosc przypisania eksperymentow do grupy
- Dodano mozliwosc filtrowania i agregacji nazwanych grup eksperymentow