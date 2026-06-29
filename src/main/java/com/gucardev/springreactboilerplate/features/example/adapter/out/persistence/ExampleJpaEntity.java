package com.gucardev.springreactboilerplate.features.example.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Persistence representation of the example aggregate — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.example.domain.model.Example domain model}
 * but carries all the JPA mapping so the domain stays free of infrastructure. The persistence
 * adapter maps between the two.
 */
@Entity
@Table(name = "examples")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean active;
}
