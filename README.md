# Building App

The Building App is a Quarkus-based application focused on registering and managing the number of people on each floor of the building. It reads messages from both the `stairs` and `elevator` topics and updates the count of individuals on each floor in a database.

## Key Features

- **Stairs and Elevator Event Processing**: Captures and processes events from the Kafka topics `stairs` and `elevator`.
- **Floor Management**: Registers and manages the count of people on each floor in a database.

## TODO List
- [X] **Process Kafka `stairs` and `elevator` Topic Events**: Capture and process events from the Kafka topics named `stairs` and `elevator`.
- [X] **Update Floor Count in Database**: Based on the messages from the topics, update the number of people on each floor in the database.
### 1.1
- [ ] **Automatic Database Initialization**: Implement a mechanism to automatically set up the `buildingdb` with 5 floors in the `FloorData` table. For each floor:
  - Initialize `people_count` to 0.
  - Set a default value for `max_people`.
  - Set a default `structure_quality` rating (between 1 and 5).
  - Set default values for `o2_level` and `co2_level`.
- [ ] **Monitoring & Alerting**: Set up monitoring tools to keep track of the app's performance and health.
- [ ] **Centralized Logging**: Integrate with a centralized logging system for better traceability.
- [ ] **API Documentation**: Document all exposed APIs and endpoints for better clarity.
- [ ] **Helm Chart Creation**: Design and implement a Helm chart for streamlined deployments of the `building-app` on Kubernetes clusters.
- [ ] **Real-time Floor Count Display**: Implement a feature to provide a real-time display of people count on each floor.
- [ ] **Integration with Concierge and Mobility Apps**: Ensure seamless data flow with the other microservices.

## Payload Example

Here's an example of a typical payload that the Building App expects from the `stairs` and `elevator` topics:

```json
{
  "personId": 12345,
  "destination": "5",
  "preferredRoute": "stairs"
}
```
## Database Structure - `FloorData`

The `buildingdb` contains the `FloorData` table, designed to manage various data attributes related to each floor of the building. Here's a breakdown of its columns:

- `floor_number`: An integer indicating the floor's number and acts as the primary key.
- `people_count`: An integer indicating the number of people currently on that floor, initialized to 0.
- `structure_quality`: An integer rating between 1 and 5, reflecting the structural quality of the floor.
- `max_people`: An integer indicating the maximum number of people allowed on that floor.
- `o2_level`: A decimal indicating the current oxygen level on the floor.
- `co2_level`: A decimal indicating the current carbon dioxide level on the floor.

The database should be initialized with 5 floors, with each attribute appropriately set.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/building-app-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Camel Jackson ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/jackson.html)): Marshal POJOs to JSON and back using Jackson
- Camel Rest ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/rest.html)): Expose REST services and their OpenAPI Specification or call external REST services
