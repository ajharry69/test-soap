package com.github.ajharry69.testsoap.bpm.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "WFGetWorkitemData_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkItemQueryResponse {

    @XmlElement(name = "Option")
    private String option;

    @XmlElement(name = "Status")
    private Integer status;
    @XmlElement(name = "Error")
    private Error error;
    @XmlElement(name = "Exception")
    private Exception exception;
    @XmlElement(name = "Instrument")
    private Instrument instrument;

    public Integer getMainCode() {
        if (error != null && error.getException() != null) {
            return error.getException().getMainCode();
        }
        if (exception != null) {
            return exception.getMainCode();
        }
        return 0;
    }

    public String getErrorDescription() {
        if (error != null && error.getException() != null) {
            return error.getException().getDescription();
        }
        if (exception != null) {
            return exception.getDescription();
        }
        return null;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Error {
        @XmlElement(name = "Exception")
        private Exception exception;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Exception {
        @XmlElement(name = "MainCode")
        private Integer mainCode;

        @XmlElement(name = "Description")
        private String description;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Instrument {
        @XmlElement(name = "ProcessInstanceId")
        private String processInstanceId;

        @XmlElement(name = "ProcessInstanceName")
        private String processInstanceName;

        @XmlElement(name = "ActivityName")
        private String activityName;

        @XmlElement(name = "LockStatus")
        private String lockStatus;

        @XmlElement(name = "LockedByPersonalName")
        private String lockedByPersonalName;

        @XmlElement(name = "Statename")
        private String statename;

        @XmlElement(name = "EntryDateTime")
        private String entryDateTime;
    }
}