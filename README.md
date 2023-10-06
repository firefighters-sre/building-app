# Building App

The Building App is a Quarkus-based application focused on registering and managing the number of people on each floor of the building. It reads messages from both the `stairs` and `elevator` topics and updates the count of individuals on each floor in a database.

## Key Features

- **Stairs and Elevator Event Processing**: Captures and processes events from the Kafka topics `stairs` and `elevator`.
- **Floor Management**: Registers and manages the count of people on each floor in a database.

## TODO List
- [ ] **Process Kafka `stairs` and `elevator` Topic Events**: Capture and process events from the Kafka topics named `stairs` and `elevator`.
- [ ] **Update Floor Count in Database**: Based on the messages from the topics, update the number of people on each floor in the database.

... [remaining parts of the README similar to the provided structure]
