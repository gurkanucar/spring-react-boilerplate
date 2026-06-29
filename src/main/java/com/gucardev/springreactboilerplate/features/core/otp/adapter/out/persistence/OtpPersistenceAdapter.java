package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.InvalidateActiveOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.LoadOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.PurgeExpiredOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.SaveOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the OTP output ports with Spring Data JPA. Maps domain ⇄ entity at the
 * boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class OtpPersistenceAdapter
        implements LoadOtpPort, SaveOtpPort, InvalidateActiveOtpPort, PurgeExpiredOtpPort {

    private final OtpJpaRepository repository;
    private final OtpPersistenceMapper mapper;

    @Override
    public Optional<Otp> findActiveByDestinationAndType(String destination, OtpType type) {
        return repository
                .findFirstByDestinationAndTypeAndUsedFalseOrderByCreatedAtDesc(destination, type)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Otp> findLatestByDestinationAndType(String destination, OtpType type) {
        return repository
                .findFirstByDestinationAndTypeOrderByCreatedAtDesc(destination, type)
                .map(mapper::toDomain);
    }

    @Override
    public Otp save(Otp otp) {
        return mapper.toDomain(repository.save(mapper.toEntity(otp)));
    }

    @Override
    public int invalidateActive(String destination, OtpType type) {
        return repository.invalidateActive(destination, type);
    }

    @Override
    public int deleteExpiredOrUsed(LocalDateTime now) {
        return repository.deleteExpiredOrUsed(now);
    }
}
