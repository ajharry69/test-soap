package com.github.ajharry69.testsoap.bpm.dto.response;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/*
 * @project kcb-sfa-bpm-st
 * @author Chris Watia - KENCONT211390
 */
@XmlRootElement(name = "WMConnect_Output")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class WMConnect {
    @XmlElement(name = "Option")
    private String option;

    @XmlElement(name = "Exception")
    private Exception exception;

    @XmlElement(name = "Participant")
    private Participant participant;

    @XmlElement(name = "LastLoginTime")
    private String lastLoginTime;

    @XmlElement(name = "LastLoginFailureTime")
    private String lastLoginFailureTime;

    @XmlElement(name = "FailureAttemptCount")
    private Integer failureAttemptCount;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Exception {
        @XmlElement(name = "MainCode")
        private Integer mainCode;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Participant {
        @XmlElement(name = "SessionId")
        private String sessionId;

        @XmlElement(name = "ID")
        private String id;

        @XmlElement(name = "LastLoginTime")
        private String lastLoginTime;

        @XmlElement(name = "IsAdmin")
        private String isAdmin;

        @XmlElement(name = "Privileges")
        private String privileges;
    }
}

