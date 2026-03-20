package com.be.rmc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "consumer_transaction_details")
public class ConsumerTransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pg_merchant_id")
    private String pgMerchantId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "in_ap_unq_chng")
    private BigDecimal transactionAmount;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "payment_id_pg")
    private String paymentIdPg;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "product_info")
    private String productInfo;

    @Column(name = "payment_link")
    private String paymentLink;  // for PG link flow

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "pg_reference_id")
    private String pgReferenceId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "rmc_api_response", columnDefinition = "TEXT")
    private String rmcApiResponse;

    @Column(name = "ap_unq_chng")
    private BigDecimal paidAmount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "pg_response", columnDefinition = "TEXT")
    private String pgResponse;

    @Column(name = "is_pp_success_msg_sent")
    private boolean isPpSuccessMsgSent;

    @Column(name = "is_pp_failure_msg_sent")
    private boolean isPpFailureMsgSent;

    @Column(name = "language_code")
    private String languageCode = "en";

    @Column(name = "payment_initiate_mode")
    private String paymentInitiateMode;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "bl_ap_unq_chng")
    private BigDecimal amountWithCharges;

}
