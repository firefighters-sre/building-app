package com.redhat.quarkus.model;

public class MoveLog {

  private Long personId;
  private String destination;

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

  @Override
  public String toString() {
      return "MoveLog{" +
              ", personId=" + personId +
              ", destination='" + destination + '\'' +
              '}';
  }
  
}
