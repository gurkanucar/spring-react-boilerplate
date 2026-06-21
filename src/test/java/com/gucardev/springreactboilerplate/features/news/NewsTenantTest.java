package com.gucardev.springreactboilerplate.features.news;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gucardev.springreactboilerplate.features.news.model.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.model.request.CreateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.model.request.NewsFilterRequest;
import com.gucardev.springreactboilerplate.features.news.service.usecase.CreateNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.service.usecase.GetAllNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.service.usecase.NewsFinder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContext;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Workspace-level tenant isolation and slug generation for news, driven through the
 * {@link TenantContextHolder} directly (so a workspace context is present without an HTTP header).
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsTenantTest {

    @Autowired
    private CreateNewsUseCase createNews;
    @Autowired
    private GetAllNewsUseCase getAllNews;
    @Autowired
    private NewsFinder newsFinder;

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    @Test
    void create_generatesSlugFromTitle_andScopesToWorkspace() {
        UUID workspace = UUID.randomUUID();
        inWorkspace(workspace);

        NewsResponseDto created = createNews.execute(
                new CreateNewsRequest("Yeni Menü Çıktı", "body", true, null, null, null, Set.of("menu")));

        assertThat(created.getWorkspaceId()).isEqualTo(workspace);
        assertThat(created.getSlug()).isEqualTo("yeni-menu-cikti");
        assertThat(created.getFeatured()).isTrue();
        assertThat(created.getTags()).containsExactly("menu");
        assertThat(newsFinder.findById(created.getId()).getId()).isEqualTo(created.getId());
    }

    @Test
    void duplicateTitleInSameWorkspace_getsSuffixedSlug() {
        inWorkspace(UUID.randomUUID());

        String first = createNews.execute(news("Same Title")).getSlug();
        String second = createNews.execute(news("Same Title")).getSlug();

        assertThat(first).isEqualTo("same-title");
        assertThat(second).isEqualTo("same-title-2");
    }

    @Test
    void crossWorkspaceFetch_isReportedAsNotFound() {
        UUID workspaceA = UUID.randomUUID();
        UUID workspaceB = UUID.randomUUID();

        inWorkspace(workspaceA);
        NewsResponseDto created = createNews.execute(news("Hello A"));

        inWorkspace(workspaceB);
        assertThatThrownBy(() -> newsFinder.findById(created.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getCode())
                .isEqualTo("NOT_FOUND");
    }

    @Test
    void featuredImageNotAmongImages_isRejected() {
        inWorkspace(UUID.randomUUID());

        UUID stray = UUID.randomUUID();
        assertThatThrownBy(() -> createNews.execute(
                new CreateNewsRequest("Bad", "b", false, List.of(UUID.randomUUID()), stray, null, null)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getCode())
                .isEqualTo("NEWS_FEATURED_IMAGE_INVALID");
    }

    @Test
    void list_isScopedToActiveWorkspace() {
        UUID workspaceA = UUID.randomUUID();
        UUID workspaceB = UUID.randomUUID();

        inWorkspace(workspaceA);
        createNews.execute(news("A One"));
        inWorkspace(workspaceB);
        createNews.execute(news("B One"));

        inWorkspace(workspaceA);
        var page = getAllNews.execute(new NewsFilterRequest());
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).allMatch(n -> n.getWorkspaceId().equals(workspaceA));
    }

    private void inWorkspace(UUID workspaceId) {
        TenantContextHolder.set(new TenantContext(UUID.randomUUID(), workspaceId, false));
    }

    private CreateNewsRequest news(String title) {
        return new CreateNewsRequest(title, "content", false, null, null, null, null);
    }
}
