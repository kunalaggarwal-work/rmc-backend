package com.be.rmc.repository;

import com.be.rmc.entity.ConsumerTransactionDetails;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerTransactionDetailsRepository extends JpaRepository<ConsumerTransactionDetails, Long> {

    Optional<ConsumerTransactionDetails> findByAccountId(String accountId);

    Optional<ConsumerTransactionDetails> findFirstByPhoneNumberAndAccountIdIsNull(String phoneNumber);
}
