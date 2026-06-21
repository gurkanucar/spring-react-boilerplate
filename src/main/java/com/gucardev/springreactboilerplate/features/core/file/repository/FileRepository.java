package com.gucardev.springreactboilerplate.features.core.file.repository;

import com.gucardev.springreactboilerplate.features.core.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends BaseJpaRepository<StoredFile, UUID> {
}
