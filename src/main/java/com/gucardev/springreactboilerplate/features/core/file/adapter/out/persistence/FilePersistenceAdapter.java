package com.gucardev.springreactboilerplate.features.core.file.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.file.application.port.out.DeleteFilePort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.LoadFilePort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.SaveFilePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the file load/save/delete output ports with Spring Data JPA. Maps domain ⇄
 * entity at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class FilePersistenceAdapter implements LoadFilePort, SaveFilePort, DeleteFilePort {

    private final FileJpaRepository repository;
    private final FilePersistenceMapper mapper;

    @Override
    public Optional<StoredFile> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StoredFile> findAllById(Collection<UUID> ids) {
        return repository.findAllById(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public StoredFile save(StoredFile file) {
        return mapper.toDomain(repository.save(mapper.toEntity(file)));
    }

    @Override
    public void delete(StoredFile file) {
        repository.delete(mapper.toEntity(file));
    }
}
