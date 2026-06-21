package com.gucardev.springreactboilerplate.features.core.file.service.usecase;

import com.gucardev.springreactboilerplate.features.core.file.mapper.FileMapper;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetFileUseCase {

    private final FileFinder finder;
    private final FileMapper fileMapper;

    @Transactional(readOnly = true)
    public FileResponseDto execute(UUID id) {
        return fileMapper.toDto(finder.findById(id));
    }
}
