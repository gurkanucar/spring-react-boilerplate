package com.gucardev.springreactboilerplate.domain.file.repository;

import com.gucardev.springreactboilerplate.domain.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends BaseJpaRepository<StoredFile, UUID> {
}
