package com.github.ajharry69.testsoap.bpm.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class WorkItemRequest {
    @Valid
    @NotNull(message = "Basic information is required")
    private BasicInfo basic;

    @Valid
    @NotNull(message = "Contact information is required")
    private ContactInfo contact;

    @NotBlank(message = "Customer exists flag is required")
    private String customerExists;

    @NotBlank(message = "Work item ext info is required")
    private String workItemExtInfo;

    @NotBlank(message = "Variant ID is required")
    private String variantId;

    @NotBlank(message = "User def var flag is required")
    private String userDefVarFlag;

    @Valid
    @NotNull(message = "Legal information is required")
    private LegalInfo legal;

    @NotBlank(message = "Documents are required")
    private String documents;

    private Map<String, String> additionalData;

    @Data
    public static class BasicInfo {
        private String customerNo;

        @NotBlank(message = "Customer full name is required")
        private String customerFullName;

        @NotBlank(message = "KRA PIN is required")
        private String kraPin;

        @NotBlank(message = "Nationality is required")
        private String nationality;

        @NotBlank(message = "Mobile number is required")
        private String mobileNo;

        @NotBlank(message = "Minimum income is required")
        private String minIncome;

        @NotBlank(message = "Salary range is required")
        private String salaryRange;

        @NotBlank(message = "Branch code is required")
        private String branchCode;

        @NotBlank(message = "Branch name is required")
        private String branchName;

        @NotBlank(message = "Customer segment is required")
        private String customerSegment;

        @NotBlank(message = "Mnemonic is required")
        private String mnemonic;

        @NotBlank(message = "Country of residence is required")
        private String countryOfResidence;

        @NotNull(message = "Date of birth is required")
        private LocalDate dob;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "ID or passport number is required")
        private String idOrPassportNo;

        @NotBlank(message = "ID type is required")
        private String idType;

        @NotBlank(message = "Marital status is required")
        private String maritalStatus;

        @NotBlank(message = "Gender is required")
        private String gender;
    }

    @Data
    public static class ContactInfo {
        @NotBlank(message = "Physical address is required")
        private String physicalAddress;

        @NotBlank(message = "Postal address is required")
        private String postalAddress;

        @NotBlank(message = "Postal code is required")
        private String postalCode;

        @NotBlank(message = "Country is required")
        private String country;
    }

    @Data
    public static class LegalInfo {
        @NotBlank(message = "Document type is required")
        private String docType;

        @NotBlank(message = "ID number is required")
        private String idNo;

        @NotBlank(message = "Issuing authority is required")
        private String issuingAuthority;

        @NotNull(message = "Issue date is required")
        private LocalDate issueDate;

        private LocalDate expiryDate;
    }
}