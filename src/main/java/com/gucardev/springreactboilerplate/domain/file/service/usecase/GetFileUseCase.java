package com.gucardev.springreactboilerplate.domain.file.service.usecase;

import com.gucardev.springreactboilerplate.domain.file.mapper.FileMapper;
import com.gucardev.springreactboilerplate.domain.file.model.dto.FileResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetFileUseCase {

    private final FileFinder finder;
    private final FileMapper mapper;

    @Transactional(readOnly = true)
    public FileResponseDto execute(UUID id) {
        return mapper.toDto(finder.findById(id));
    }
}
