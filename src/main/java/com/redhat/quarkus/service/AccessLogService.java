package com.redhat.quarkus.service;

import com.redhat.quarkus.model.AccessLog;
import com.redhat.quarkus.model.MoveLog;
import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccessLogService {

    @Incoming("lobby")
    @Outgoing("entrance")
    @Blocking
    public MoveLog processLobbyEvent(AccessLog accessLog) throws InterruptedException {
        MoveLog moveLog = new MoveLog();
        moveLog.setDestination(accessLog.getDestination());
        moveLog.setPersonId(accessLog.getPersonId());
        return moveLog;
    }

}
