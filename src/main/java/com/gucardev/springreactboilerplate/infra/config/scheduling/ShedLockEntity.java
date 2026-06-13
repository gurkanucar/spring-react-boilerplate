package com.gucardev.springreactboilerplate.infra.config.scheduling;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Backing table for ShedLock's {@code JdbcTemplateLockProvider}. Mapped only so Hibernate creates
 * the table (the column names/shape match ShedLock's contract); ShedLock itself reads and writes
 * the rows directly via JDBC, so this entity is never used through a repository.
 */
@Entity
@Table(name = "shedlock")
@Getter
@Setter
@NoArgsConstructor
public class ShedLockEntity {

    @Id
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "lock_until", nullable = false)
    private Instant lockUntil;

    @Column(name = "locked_at", nullable = false)
    private Instant lockedAt;

    @Column(name = "locked_by", length = 255, nullable = false)
    private String lockedBy;
}
