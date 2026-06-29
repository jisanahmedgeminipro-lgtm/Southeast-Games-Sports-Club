package bd.edu.seu.gamesclub.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Generates URL-friendly slugs from arbitrary text.
 */
public final class SlugUtil {

    private SlugUtil() {
    }

    /**
     * Converts text such as {@code "Table Tennis"} into {@code "table-tennis"}.
     *
     * @param input the source text
     * @return a lower-case, hyphenated slug (empty string if input is null/blank)
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("[\\s-]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    /**
     * Produces a unique slug from the given text by appending {@code -2}, {@code -3}, ...
     * until the supplied predicate reports the candidate as free.
     *
     * @param input  source text
     * @param exists predicate returning {@code true} when a slug is already taken
     * @return a unique slug
     */
    public static String uniqueSlug(String input, java.util.function.Predicate<String> exists) {
        String base = toSlug(input);
        if (base.isEmpty()) {
            base = "item";
        }
        String candidate = base;
        int counter = 2;
        while (exists.test(candidate)) {
            candidate = base + "-" + counter++;
        }
        return candidate;
    }
}
