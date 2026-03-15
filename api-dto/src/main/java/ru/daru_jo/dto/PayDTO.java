package ru.daru_jo.dto;

public class PayDTO {
    @SuppressWarnings("unused")
    public PayDTO() {
    }

    private Long id;
    private String merchantLogin;
    private Long invId;
    private Double outSum;
    private String description;
    private String signatureValue;
    private String email;
    private Integer isTest;
    private Boolean isCompleted;

    public PayDTO(Long id, String MerchantLogin, Long orderId, Double OutSum, String description, String SignatureValue, String email, Integer IsTest,Boolean isCompleted) {
        this.id = id;
        this.merchantLogin = MerchantLogin;
        this.invId = orderId;
        this.outSum = OutSum;
        this.description = description;
        this.signatureValue = SignatureValue;
        this.email = email;
        this.isTest = IsTest;
        this.isCompleted = isCompleted;
    }

    @SuppressWarnings("unused")
    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getMerchantLogin() {
        return merchantLogin;
    }

    @SuppressWarnings("unused")
    public Long getInvId() {
        return invId;
    }

    @SuppressWarnings("unused")
    public Double getOutSum() {
        return outSum;
    }

    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unused")
    public String getSignatureValue() {
        return signatureValue;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public Integer getIsTest() {
        return isTest;
    }

    @SuppressWarnings("unused")
    public Boolean getIsCompleted() {
        return isCompleted;
    }
//MerchantLogin=ndflbroker&OutSum=990.00&InvId=12&Description=%EE%EF%E8%F1%E0%ED%E8%E5&SignatureValue=cbe076e81be598fdf4008f305c6639fb&IsTest=1"
}
