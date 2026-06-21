package com.gucardev.springreactboilerplate.features.shared.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

/**
 * Opt-in soft-delete base. Extend this <em>instead of</em> {@link BaseEntity} when an entity should
 * be soft-deleted rather than physically removed. Entities that extend {@link BaseEntity} keep
 * normal hard deletes — soft delete is never applied globally, only where you ask for it.
 *
 * <p>With Hibernate's {@link SoftDelete}, the framework transparently:
 * <ul>
 *   <li>turns {@code repository.delete(...)} into an {@code UPDATE ... SET deleted = true}, and</li>
 *   <li>appends {@code WHERE deleted = false} to every query/association load for the entity,</li>
 * </ul>
 * so existing finders, specifications and {@code findAll()} skip soft-deleted rows with no code
 * changes. {@link SoftDeleteType#DELETED} means the column stores {@code true} for deleted rows.
 *
 * <p><b>To enable on an entity:</b> change {@code extends BaseEntity} to
 * {@code extends SoftDeletableEntity} and add a {@code BOOLEAN NOT NULL DEFAULT false deleted}
 * column to its table in a Flyway migration (schema is {@code validate}-only, so the column must
 * exist). Nothing else is required.
 */
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@SoftDelete(strategy = SoftDeleteType.DELETED, columnName = "deleted")
public abstract class SoftDeletableEntity extends BaseEntity {
}
