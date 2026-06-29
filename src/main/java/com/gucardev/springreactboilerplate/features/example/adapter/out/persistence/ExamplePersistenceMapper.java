package com.gucardev.springreactboilerplate.features.example.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Example} domain model and the {@link ExampleJpaEntity}. Kept
 * hand-written (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the
 * super-builder and is trivial enough to read at a glance.
 */
@Component
public class ExamplePersistenceMapper {

    Example toDomain(ExampleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Example.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    ExampleJpaEntity toEntity(Example example) {
        if (example == null) {
            return null;
        }
        return ExampleJpaEntity.builder()
                .id(example.getId())
                .name(example.getName())
                .description(example.getDescription())
                .active(example.getActive())
                .createdAt(example.getCreatedAt())
                .updatedAt(example.getUpdatedAt())
                .createdBy(example.getCreatedBy())
                .updatedBy(example.getUpdatedBy())
                .build();
    }
}
