package com.github.ajharry69.testsoap.bpm.dto.response;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import java.util.List;

/*
 * @project kcb-sfa-bpm-st
 * @author Chris Watia - KENCONT211390
 */

@XmlRootElement(name = "WFUploadWorkItem_Output")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class WorkItemResponse {
    @XmlElement(name = "Option")
    private String option;

    @XmlElement(name = "Exception")
    private ExceptionData exception;

    @XmlElement(name = "ProcessInstanceId")
    private String processInstanceId;

    @XmlElement(name = "URN")
    private String urn;

    @XmlElement(name = "WorkStageId")
    private String workStageId;

    @XmlElement(name = "CreationDateTime")
    private String creationDateTime;

    @XmlElement(name = "FolderIndex")
    private String folderIndex;

    @XmlElement(name = "Documents")
    private Documents documents;

    @XmlElement(name = "InsertionOrderIdValues")
    private InsertionOrderIdValues insertionOrderIdValues;

    @XmlElement(name = "SessionId")
    private String sessionId;

    @XmlElement(name = "UserName")
    private String userName;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ExceptionData {
        @XmlElement(name = "MainCode")
        private Integer mainCode;
        @XmlElement(name = "SubErrorCode")
        private Integer subErrorCode;
        @XmlElement(name = "TypeOfError")
        private String typeOfError;
        @XmlElement(name = "Subject")
        private String subject;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Documents {
        @XmlElement(name = "Document")
        private List<Document> document;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Document {
            @XmlElement(name = "DocumentName")
            private String documentName;

            @XmlElement(name = "DocumentIndex")
            private String documentIndex;

            @XmlElement(name = "ISIndex")
            private String isIndex;
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InsertionOrderIdValues {
        @XmlElement(name = "InsertionOrderIdValue")
        private List<InsertionOrderIdValue> insertionOrderIdValue;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class InsertionOrderIdValue {
            @XmlElement(name = "HashId")
            private String hashId;

            @XmlElement(name = "InsertionOrderId")
            private String insertionOrderId;
        }
    }
}
