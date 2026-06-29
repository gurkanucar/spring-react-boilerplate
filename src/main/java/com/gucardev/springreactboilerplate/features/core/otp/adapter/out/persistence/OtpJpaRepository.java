package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data repository for {@link OtpJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the OTP output ports.
 */
@Repository
public interface OtpJpaRepository extends BaseJpaRepository<OtpJpaEntity, Long> {

    /** The most recent still-active OTP for a destination/type (there is at most one). */
    Optional<OtpJpaEntity> findFirstByDestinationAndTypeAndUsedFalseOrderByCreatedAtDesc(
            String destination, OtpType type);

    /** The most recent OTP for a destination/type regardless of status (used for the resend cooldown). */
    Optional<OtpJpaEntity> findFirstByDestinationAndTypeOrderByCreatedAtDesc(String destination, OtpType type);

    /**
     * Marks every active OTP for a destination/type as used (called before issuing a new one).
     * Flushes/clears the persistence context so the bulk update isn't shadowed by stale managed
     * entities in the same transaction.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("UPDATE OtpJpaEntity o SET o.used = true WHERE o.destination = :destination "
            + "AND o.type = :type AND o.used = false")
    int invalidateActive(@Param("destination") String destination, @Param("type") OtpType type);

    /** Purges expired or already-used OTPs. Returns the number of rows removed. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM OtpJpaEntity o WHERE o.expiryTime < :now OR o.used = true")
    int deleteExpiredOrUsed(@Param("now") LocalDateTime now);
}
