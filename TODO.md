# Building App

The Building App is a Quarkus-based application focused on registering and managing the number of people on each floor of the building. It reads messages from both the `stairs` and `elevator` topics and updates the count of individuals on each floor in a database.

## TODO List
### 1.0
- [X] **Process Kafka `stairs` and `elevator` Topic Events**: Capture and process events from the Kafka topics named `stairs` and `elevator`.
- [X] **Update Floor Count in Database**: Based on the messages from the topics, update the number of people on each floor in the database.
### 1.0.1
- [X] **Monitoring**: Set up monitoring tools to keep track of the app's performance and health.
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
 ### 1.0.2
- [X] **Route Documentation**: Document all exposed APIs and endpoints for better clarity.
- [X] **Health Endpoint Integration**: Integrated `camel-quarkus-microprofile-health` to provide health check endpoints for application monitoring.
### 1.0.3
- [ ] **Env vars**: 
- [ ] **Helm Chart Creation**: Design and implement a Helm chart for streamlined deployments of the `building-app` on Kubernetes clusters.
- [ ] **Implement Basic SLOs, SLAs, and Alerting**
  - [ ] **Availability SLO**: Ensure 99.9% uptime over a 10-minute window and create Prometheus rules for alerting.
  - [ ] **Latency SLO**: Ensure API response times are under 200ms and event processing times are within 500ms.
  - [ ] **Error Rate SLO**: Ensure less than 0.1% of all API requests result in errors.
  - [ ] **Availability SLA**: Implement a service credit system for downtime that falls below the agreed availability of 99.8% in a 10-minute window.
  - [ ] **Latency SLA**: Implement a service credit for average response time exceeding 200ms for over an hour.
### 1.0.4
- [ ] **SLO/SLA prevention Automation**: Implement automation routines to monitor and alert on SLO/SLA disruption. 
  - [ ] **Automated Scaling**:
    - [ ] **Horizontal Pod Autoscaling (HPA)**: Dynamically scale the number of running pods based on observed CPU utilization or other select metrics.
  - [ ] **Self-Healing Systems**:
    - [ ] **Liveness and Readiness Probes**: Implement probes to check the health of the application and restart pods that are not responsive.
    - [ ] **PodDisruptionBudget (PDB)**: Ensure high availability during voluntary disruptions by defining the minimum available replicas.
### 1.0.5
- [ ] **Document the database**: 
- [ ] **Automatic Database Initialization**: Implement a mechanism to automatically set up the `buildingdb` with 5 floors in the `FloorData` table. For each floor:
  - Initialize `people_count` to 0.
  - Set a default value for `max_people`.
  - Set a default `structure_quality` rating (between 1 and 5).
  - Set default values for `o2_level` and `co2_level`.
### 1.0.6
- [ ] **SLO/SLA fire incident prevention Automation**:
  - [ ] Some task related to these SLOs: Escalate the application to to reduce the evacuation time
- [ ] **Custom Metrics Scaling**: Implement scaling based on custom application-specific metrics.
- [ ] **KEDA**:
### Backlog

- [ ] **Real-time Floor Count Display**: Implement a feature to provide a real-time display of people count on each floor.
- [ ] **Integration with Concierge and Mobility Apps**: Ensure seamless data flow with the other microservices.
- [ ] **Centralized Logging**: Integrate with a centralized logging system for better traceability.
