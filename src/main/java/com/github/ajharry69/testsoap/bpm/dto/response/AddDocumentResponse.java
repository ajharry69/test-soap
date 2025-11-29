package com.github.ajharry69.testsoap.bpm.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "NGOAddDocumentResponseBDO")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AddDocumentResponse{
    @XmlElement(name = "message")
    private String message;

    @XmlElement(name = "statusCode")
    private Integer statusCode;

    @XmlElement(name = "NGOGetDocListDocDataBDO")
    private NGOGetDocListDocDataBDO documentData;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class NGOGetDocListDocDataBDO {
        @XmlElement(name = "accessedDateTime")
        private String accessedDateTime;

        @XmlElement(name = "author")
        private String author;

        @XmlElement(name = "checkOutBy")
        private String checkOutBy;

        @XmlElement(name = "checkOutStatus")
        private String checkOutStatus;

        @XmlElement(name = "comment")
        private String comment;

        @XmlElement(name = "createdByApp")
        private String createdByApp;

        @XmlElement(name = "createdByAppName")
        private String createdByAppName;

        @XmlElement(name = "createdDateTime")
        private String createdDateTime;

        @XmlElement(name = "docOrderNo")
        private Integer docOrderNo;

        @XmlElement(name = "documentIndex")
        private String documentIndex;

        @XmlElement(name = "documentLock")
        private String documentLock;

        @XmlElement(name = "documentName")
        private String documentName;

        @XmlElement(name = "documentSize")
        private String documentSize;

        @XmlElement(name = "documentType")
        private String documentType;

        @XmlElement(name = "documentVersionNo")
        private String documentVersionNo;

        @XmlElement(name = "enableLog")
        private String enableLog;

        @XmlElement(name = "expiryDateTime")
        private String expiryDateTime;

        @XmlElement(name = "filedByUser")
        private String filedByUser;

        @XmlElement(name = "filedDateTime")
        private String filedDateTime;

        @XmlElement(name = "finalizedBy")
        private String finalizedBy;

        @XmlElement(name = "ftsDocumentIndex")
        private Integer ftsDocumentIndex;

        @XmlElement(name = "isIndex")
        private String isIndex;

        @XmlElement(name = "location")
        private String location;

        @XmlElement(name = "lockByUser")
        private String lockByUser;

        @XmlElement(name = "loginUserRights")
        private String loginUserRights;

        @XmlElement(name = "noOfPages")
        private Integer noOfPages;

        @XmlElement(name = "owner")
        private String owner;

        @XmlElement(name = "ownerIndex")
        private String ownerIndex;

        @XmlElement(name = "parentFolderIndex")
        private String parentFolderIndex;

        @XmlElement(name = "referenceFlag")
        private String referenceFlag;

        @XmlElement(name = "revisedDateTime")
        private String revisedDateTime;

        @XmlElement(name = "textISIndex")
        private Integer textISIndex;

        @XmlElement(name = "useFulInfo")
        private String useFulInfo;

        @XmlElement(name = "versionFlag")
        private String versionFlag;
    }
}