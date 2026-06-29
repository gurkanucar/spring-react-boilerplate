package com.gucardev.springreactboilerplate.features.core.file.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link FileJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the load/save/delete file ports.
 */
@Repository
public interface FileJpaRepository extends BaseJpaRepository<FileJpaEntity, UUID> {
}
