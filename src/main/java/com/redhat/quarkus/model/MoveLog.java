package com.redhat.quarkus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MoveLog {

    @JsonProperty("personId")
    private Long personId;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("preferredRoute")
    private String preferredRoute;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
    
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPreferredRoute() {
        return preferredRoute;
    }

    public void setPreferredRoute(String preferredRoute) {
        this.preferredRoute = preferredRoute;
    }

    @Override
    public String toString() {
        return "MoveLog{" +
                " personId=" + personId +
                ", destination='" + destination + '\'' +
                ", preferredRoute='" + preferredRoute + '\'' +
                '}';
    }

}
