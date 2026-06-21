package com.gucardev.springreactboilerplate.features.shared.util;

import java.text.Normalizer;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Turns arbitrary text into a URL-safe slug matching {@code ^[a-z0-9]+(?:-[a-z0-9]+)*$}.
 * Turkish letters are transliterated to ASCII first ({@code ş->s}, {@code ı->i}, ...), then any
 * remaining diacritics are stripped, the result is lowercased and non-alphanumeric runs collapse
 * to single hyphens. Returns an empty string when the input has no usable characters.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlugUtil {

    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }
        String s = input.trim()
                .replace('ı', 'i').replace('İ', 'i')
                .replace('ş', 's').replace('Ş', 's')
                .replace('ğ', 'g').replace('Ğ', 'g')
                .replace('ç', 'c').replace('Ç', 'c')
                .replace('ö', 'o').replace('Ö', 'o')
                .replace('ü', 'u').replace('Ü', 'u');
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        s = s.toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9]+", "-");
        s = s.replaceAll("(^-+)|(-+$)", "");
        return s;
    }
}
