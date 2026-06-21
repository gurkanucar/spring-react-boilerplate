package com.gucardev.springreactboilerplate.features.shared.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SlugUtilTest {

    @Test
    void transliteratesTurkishAndLowercases() {
        assertThat(SlugUtil.toSlug("Yeni Menü Çıktı")).isEqualTo("yeni-menu-cikti");
        assertThat(SlugUtil.toSlug("İstanbul Şubesi")).isEqualTo("istanbul-subesi");
    }

    @Test
    void collapsesSeparatorsAndTrims() {
        assertThat(SlugUtil.toSlug("  Hello,   World!!  ")).isEqualTo("hello-world");
        assertThat(SlugUtil.toSlug("a--b__c")).isEqualTo("a-b-c");
    }

    @Test
    void returnsEmptyForBlankOrSymbolOnly() {
        assertThat(SlugUtil.toSlug(null)).isEmpty();
        assertThat(SlugUtil.toSlug("   ")).isEmpty();
        assertThat(SlugUtil.toSlug("!!!")).isEmpty();
    }
}
