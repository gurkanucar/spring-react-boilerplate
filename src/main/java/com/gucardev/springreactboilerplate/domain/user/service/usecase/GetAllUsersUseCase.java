package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.domain.file.model.dto.FileUrlDto;
import com.gucardev.springreactboilerplate.domain.file.repository.FileRepository;
import com.gucardev.springreactboilerplate.domain.file.service.FileUrlResolver;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.model.request.UserFilterRequest;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import com.gucardev.springreactboilerplate.domain.user.repository.specification.UserSpecification;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final FileRepository fileRepository;
    private final FileUrlResolver fileUrlResolver;

    @Transactional(readOnly = true)
    public Page<UserResponseDto> execute(UserFilterRequest filter) {
        Page<User> users = repository.findAll(UserSpecification.build(filter), filter.toPageable());

        // Resolve every profile image on the page in one query, then map against that batch.
        Map<UUID, FileUrlDto> urlsByImageId = batchResolveProfileImages(users.getContent());
        return users.map(user -> mapper.toDto(user, urlsByImageId::get));
    }

    private Map<UUID, FileUrlDto> batchResolveProfileImages(List<User> users) {
        Set<UUID> imageIds = users.stream()
                .map(User::getProfileImageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (imageIds.isEmpty()) {
            return Map.of();
        }
        return fileRepository.findAllById(imageIds).stream()
                .collect(Collectors.toMap(StoredFile::getId, fileUrlResolver::resolve));
    }
}
