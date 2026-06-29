package com.gucardev.springreactboilerplate.features.example.domain.model;

import com.gucardev.springreactboilerplate.features.example.application.exception.ExampleExceptionType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The example aggregate — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code ExampleJpaEntity} on the way out, {@code ExampleResponseDto} on the way in to the client).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Example {

    private Long id;
    private String name;
    private String description;
    private Boolean active;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /** Domain transition: activate the example, refusing if it is already active. */
    public void activate() {
        if (Boolean.TRUE.equals(this.active)) {
            throw ExampleExceptionType.ALREADY_ACTIVE.toException();
        }
        this.active = true;
    }
}
