# Building App

The Building App is a Quarkus-based application focused on registering and managing the number of people on each floor of the building. It reads messages from both the `stairs` and `elevator` topics and updates the count of individuals on each floor in a database.

## Key Features

- **Stairs and Elevator Event Processing**: Captures and processes events from the Kafka topics `stairs` and `elevator`.
- **Floor Management**: Registers and manages the count of people on each floor in a database.
## Camel Route Logic

The `building-app` uses Apache Camel to process and route building events, particularly those related to elevator and stairs traffic:

### BuildingElevatorRoute
- **Source**: Kafka topic `{{kafka.topic.elevator.name}}`.
- **Description**: Processes events received from the elevator and updates relevant counters.
- **Actions**:
  - Logs the received event.
  - Redirects the event to `direct:updateFloorData`.
- **Example Event**:
  ```json
  {
      "personId": 12345,
      "preferredRoute": "elevator",
      "destination": "5"
  }
  ```
### BuildingStairsRoute
- **Source**: Kafka topic `{{kafka.topic.stairs.name}}`.
- **Description**: Processes events received from the stairs and updates relevant counters.
- **Actions**:
  - Logs the received event.
  - Redirects the event to `direct:updateFloorData`.
- **Example Event**:
  ```json
  {
      "personId": 12345,
      "preferredRoute": "elevator",
      "destination": "5"
  }
  ```
### InFloorRoute
- **Source**: Direct endpoint `updateFloorData`.
- **Description**: Processes events and updates the database with the current count of people on each floor.
- **Actions**:
  - Starts the `updateFloorDataTimer`.
  - Constructs an SQL update statement to increment the people count for the specified floor.
  - Executes the SQL statement against the `building-ds` database.

## Event Processing

In the `building-app`, event processing is essential, particularly when dealing with real-time data streams that determine the flow of people within the building. Here's a detailed breakdown of the Kafka topics the app interacts with and how it processes incoming messages:

### Input Topics:
- **`elevator`**: 
  - **Description**: This topic receives events indicating individuals who have chosen the elevator as their preferred route from the Mobility App. The `building-app` processes these events and updates the relevant floor data in the database.
  
- **`stairs`**: 
  - **Description**: This topic receives events indicating individuals who have chosen the stairs as their preferred route from the Mobility App. The `building-app` processes these events and updates the relevant floor data in the database.

### Database Update:
After processing the input from the Kafka topics, the `building-app` updates the `FloorData` in the database to reflect the current number of people on each floor and their chosen routes (either elevator or stairs).

## Monitoring Section for Building App 📊

### Accessing the Metrics:

To view the real-time metrics, navigate to the endpoint `/q/metrics`.

### Key Metrics:

- **`camel_exchanges_succeeded_total`**: Reflects the number of successfully completed exchanges.
  
- **`camel_exchanges_external_redeliveries_total`**: Indicates the number of external initiated redeliveries, such as from a JMS broker.

- **`elevatorCounter_total`**: Provides a count of how frequently the elevator is utilized.

- **`camel_exchanges_failed_total`**: Represents the number of exchanges that failed.

- **`camel_exchanges_failures_handled_total`**: Shows the number of failures that the system managed to handle.

- **`camel_routes_running_routes`**: Signifies the number of routes that are currently active.

- **`stairsCounter_total`**: Offers a count of stairs usage instances.

- **`updateFloorData_total`**: Shows how many times the floor data was updated.

- **`updateFloorDataTimer_seconds`** (max, count, sum): Metrics that relate to the time taken for updating floor data.

- **`camel_route_policy_seconds`** (count, sum, max): These metrics provide insights into the performance and efficiency of the routes.

- **`camel_exchange_event_notifier_seconds`** (max, count, sum): Metrics detailing the time taken to send messages to various endpoints.

- **`camel_exchanges_total`**: Displays the overall number of processed exchanges.

- **`camel_exchanges_inflight`**: Reflects the number of inflight message exchanges.

- **`camel_routes_added_routes`**: Indicates the number of routes added to the system.

## Database Structure - `FloorData`

The `buildingdb` houses the `FloorData` table, which manages various data attributes related to each floor of the building. Below is a detailed breakdown of its columns:

- **`floor_number`**:
  - **Type**: Integer
  - **Description**: Represents the floor's number.
  - **Special Note**: Acts as the primary key. Initialized with values ranging from 1 to 5, representing five floors.

- **`people_count`**:
  - **Type**: Integer
  - **Description**: Denotes the number of people currently on that floor.
  - **Default Value**: 0

- **`structure_quality`**:
  - **Type**: Integer
  - **Description**: Rates the structural quality of the floor.
  - **Range**: 1 to 5 (1 being the lowest quality and 5 being the highest quality)
  - **Default Value**: 5 (indicating top-notch structural quality for all floors)

- **`max_people`**:
  - **Type**: Integer
  - **Description**: Specifies the maximum number of people permitted on that floor.
  - **Default Value**: 30 (indicating that up to 30 people can be present on a floor at any given time)

- **`o2_level`**:
  - **Type**: Decimal
  - **Description**: Reflects the current oxygen level (%) on the floor.
  - **Default Value**: 20.9% (representing the typical atmospheric oxygen level)

- **`co2_level`**:
  - **Type**: Decimal
  - **Description**: Signifies the current carbon dioxide level (%) on the floor.
  - **Default Value**: 0.04% (indicative of the standard atmospheric carbon dioxide level)

Upon initializing the database, five floors are set up with these default attributes, ensuring consistent and reliable data representation for the building's floors.

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
