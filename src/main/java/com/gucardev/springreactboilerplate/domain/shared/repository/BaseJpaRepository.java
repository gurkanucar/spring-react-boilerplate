package com.gucardev.springreactboilerplate.domain.shared.repository;

import com.gucardev.springreactboilerplate.domain.shared.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

//    @Transactional
//    @Modifying
//    @Query("UPDATE #{#entityName} e SET e.deletedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
//    void softDelete(@Param("id") ID id, @Param("reason") String reason);

}
