# Building App

The Building App is a Quarkus-based application focused on registering and managing the number of people on each floor of the building. It reads messages from both the `stairs` and `elevator` topics and updates the count of individuals on each floor in a database.

## TODO List
- [X] **Process Kafka `stairs` and `elevator` Topic Events**: Capture and process events from the Kafka topics named `stairs` and `elevator`.
- [X] **Update Floor Count in Database**: Based on the messages from the topics, update the number of people on each floor in the database.
 ### 1.1 
- [ ] **Monitoring**: Set up monitoring tools to keep track of the app's performance and health.
  - [X] **`camel_exchanges_succeeded_total`**: Number of successfully completed exchanges.
  - [X] **`camel_exchanges_external_redeliveries_total`**: Number of external initiated redeliveries (such as from JMS broker).
  - [X] **`elevatorCounter_total`**: Count of elevator usage.
  - [X] **`camel_exchanges_failed_total`**: Number of failed exchanges.
  - [X] **`camel_exchanges_failures_handled_total`**: Number of failures that were handled.
  - [X] **`camel_routes_running_routes`**: Indicates how many routes are currently active.
  - [X] **`stairsCounter_total`**: Count of stairs usage.
  - [X] **`updateFloorData_total`**: Count of times the floor data was updated.
  - [X] **`updateFloorDataTimer_seconds_max`**, **`updateFloorDataTimer_seconds_count`**, **`updateFloorDataTimer_seconds_sum`**: Metrics related to the time taken to update floor data.
  - [X] **`camel_route_policy_seconds_count`**, **`camel_route_policy_seconds_sum`**, **`camel_route_policy_seconds_max`**: Performance metrics related to route processing times.
  - [X] **`camel_exchange_event_notifier_seconds_max`**, **`camel_exchange_event_notifier_seconds_count`**, **`camel_exchange_event_notifier_seconds_sum`**: Time taken to send messages to endpoints.
  - [X] **`camel_exchanges_total`**: Total number of processed exchanges.
  - [X] **`camel_exchanges_inflight`**: Number of inflight message exchanges.
  - [X] **`camel_routes_added_routes`**: Indicates the number of routes added.
- [ ] **Logging**: Set up monitoring tools to keep track of the app's performance and health.
- [ ] **SLOs and SLAs**: Define and implement Service Level Objectives (SLOs) and Service Level Agreements (SLAs) for the mobility services.
- [ ] **Alerting**: Set up monitoring tools to keep track of the app's performance and health.
- [ ] **Helm Chart Creation**: Design and implement a Helm chart for streamlined deployments of the `mobility-app` on Kubernetes clusters.
- [ ] **Centralized Logging**: Integrate with a centralized logging system for better traceability.
- [ ] **API Documentation**: Document all exposed APIs and endpoints for better clarity.
### 1.2
- [ ] **Automatic Database Initialization**: Implement a mechanism to automatically set up the `buildingdb` with 5 floors in the `FloorData` table. For each floor:
  - Initialize `people_count` to 0.
  - Set a default value for `max_people`.
  - Set a default `structure_quality` rating (between 1 and 5).
  - Set default values for `o2_level` and `co2_level`.
- [ ] **Real-time Floor Count Display**: Implement a feature to provide a real-time display of people count on each floor.
- [ ] **Integration with Concierge and Mobility Apps**: Ensure seamless data flow with the other microservices.
