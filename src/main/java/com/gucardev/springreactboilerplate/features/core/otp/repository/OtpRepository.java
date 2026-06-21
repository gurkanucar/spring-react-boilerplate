package com.gucardev.springreactboilerplate.features.core.otp.repository;

import com.gucardev.springreactboilerplate.features.core.otp.entity.Otp;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OtpRepository extends BaseJpaRepository<Otp, Long> {

    /** The most recent still-active OTP for a destination/type (there is at most one). */
    Optional<Otp> findFirstByDestinationAndTypeAndUsedFalseOrderByCreatedAtDesc(
            String destination, OtpType type);

    /** The most recent OTP for a destination/type regardless of status (used for the resend cooldown). */
    Optional<Otp> findFirstByDestinationAndTypeOrderByCreatedAtDesc(String destination, OtpType type);

    /**
     * Marks every active OTP for a destination/type as used (called before issuing a new one).
     * Flushes/clears the persistence context so the bulk update isn't shadowed by stale managed
     * entities in the same transaction.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("UPDATE Otp o SET o.used = true WHERE o.destination = :destination "
            + "AND o.type = :type AND o.used = false")
    int invalidateActive(@Param("destination") String destination, @Param("type") OtpType type);

    /** Purges expired or already-used OTPs. Returns the number of rows removed. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Otp o WHERE o.expiryTime < :now OR o.used = true")
    int deleteExpiredOrUsed(@Param("now") LocalDateTime now);
}
