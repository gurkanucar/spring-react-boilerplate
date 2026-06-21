package com.gucardev.springreactboilerplate.features.news.entity;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "news",
        uniqueConstraints = @UniqueConstraint(name = "uk_news_workspace_slug", columnNames = {"workspace_id", "slug"}),
        indexes = {
                @Index(name = "idx_news_created_at", columnList = "created_at"),
                @Index(name = "idx_news_workspace", columnList = "workspace_id"),
                @Index(name = "idx_news_featured", columnList = "featured")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class News extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    /** Owning workspace (tenant). Every news entry belongs to exactly one workspace. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(nullable = false, length = 300)
    private String title;

    /** URL-safe slug, auto-generated from the title; unique within a workspace. */
    @Column(nullable = false, length = 320)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean featured;

    /** File id (in the file store) of the featured image; must be one of {@link #imageIds}. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "featured_image_id")
    private UUID featuredImageId;

    /** Ordered file ids of the attached images. Files are uploaded via the file API and referenced by id. */
    @ElementCollection
    @CollectionTable(name = "news_image_ids",
            joinColumns = @JoinColumn(name = "news_id"),
            indexes = @Index(name = "idx_news_image_ids_news", columnList = "news_id"))
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "image_id", nullable = false)
    @OrderColumn(name = "sort_order")
    @Builder.Default
    private List<UUID> imageIds = new ArrayList<>();

    /** Ordered file ids of attached documents (any file type). Uploaded via the file API and referenced by id. */
    @ElementCollection
    @CollectionTable(name = "news_attachment_ids",
            joinColumns = @JoinColumn(name = "news_id"),
            indexes = @Index(name = "idx_news_attachment_ids_news", columnList = "news_id"))
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "attachment_id", nullable = false)
    @OrderColumn(name = "sort_order")
    @Builder.Default
    private List<UUID> attachmentIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "news_tags",
            joinColumns = @JoinColumn(name = "news_id"),
            indexes = @Index(name = "idx_news_tags_news", columnList = "news_id"))
    @Column(name = "tag", nullable = false, length = 50)
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    /** True when there is no featured image, or it is present among {@link #imageIds}. */
    public boolean isFeaturedImageConsistent() {
        return featuredImageId == null || (imageIds != null && imageIds.contains(featuredImageId));
    }
}
