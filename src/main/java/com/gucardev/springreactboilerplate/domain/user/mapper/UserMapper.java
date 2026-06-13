package com.gucardev.springreactboilerplate.domain.user.mapper;

import com.gucardev.springreactboilerplate.domain.file.model.dto.FileUrlDto;
import com.gucardev.springreactboilerplate.domain.file.repository.FileRepository;
import com.gucardev.springreactboilerplate.domain.file.service.FileUrlResolver;
import com.gucardev.springreactboilerplate.domain.role.entity.Role;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.model.request.CreateUserRequest;
import com.gucardev.springreactboilerplate.domain.user.model.request.UpdateUserRequest;
import com.gucardev.springreactboilerplate.domain.user.service.ProfileImageUrlLookup;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for {@link User}. Roles flatten to name strings on read. After mapping, the
 * profile image is resolved into {@code profileImageUrl}/{@code profileImageThumbnailUrl} via a
 * {@link ProfileImageUrlLookup} {@code @Context}: {@link #toDto(User)} looks the single id up
 * lazily, while list callers pass a pre-resolved batch map ({@link #toDto(User, ProfileImageUrlLookup)})
 * to avoid N+1. On write, {@code password}/{@code roles} are left for the use case.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected FileRepository fileRepository;

    @Autowired
    protected FileUrlResolver fileUrlResolver;

    /** Single-user mapping: resolves the profile image (one lookup) on the fly. */
    public UserResponseDto toDto(User user) {
        return toDto(user, this::resolveSingle);
    }

    /** Mapping with a caller-provided image-url lookup (used by list endpoints for batch resolution). */
    public abstract UserResponseDto toDto(User user, @Context ProfileImageUrlLookup imageUrlLookup);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract User toEntity(CreateUserRequest request);

    @Mapping(target = "roles", ignore = true)
    public abstract void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    protected String roleName(Role role) {
        return role.getName();
    }

    @AfterMapping
    protected void enrichProfileImageUrls(User user, @MappingTarget UserResponseDto dto,
                                          @Context ProfileImageUrlLookup imageUrlLookup) {
        UUID imageId = user.getProfileImageId();
        if (imageId == null) {
            return;
        }
        FileUrlDto urls = imageUrlLookup.resolve(imageId);
        if (urls != null) {
            dto.setProfileImageUrl(urls.url());
            dto.setProfileImageThumbnailUrl(urls.thumbnailUrl());
        }
    }

    private FileUrlDto resolveSingle(UUID imageId) {
        return fileRepository.findById(imageId).map(fileUrlResolver::resolve).orElse(null);
    }
}
