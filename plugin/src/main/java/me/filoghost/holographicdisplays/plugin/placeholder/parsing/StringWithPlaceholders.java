/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.placeholder.parsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class StringWithPlaceholders {

    private static final char PLACEHOLDER_END_CHAR = '}';
    private static final char PLACEHOLDER_START_CHAR = '{';

    private final @NotNull String string;
    private final @Nullable List<StringPart> stringParts;

    public static @Nullable StringWithPlaceholders ofOrNull(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return of(string);
    }

    public static @NotNull StringWithPlaceholders of(@NotNull String string) {
        return new StringWithPlaceholders(string, splitToParts(string));
    }

    private StringWithPlaceholders(@NotNull String string, @Nullable List<StringPart> stringParts) {
        this.string = string;
        this.stringParts = stringParts;
    }

    public @NotNull String getString() {
        return string;
    }

    public boolean containsPlaceholders() {
        if (stringParts == null) {
            return false;
        }

        for (StringPart stringPart : stringParts) {
            if (stringPart instanceof PlaceholderPart) {
                return true;
            }
        }

        return false;
    }

    public boolean anyPlaceholderMatch(Predicate<PlaceholderOccurrence> filter) {
        if (stringParts == null) {
            return false;
        }

        for (StringPart stringPart : stringParts) {
            if (stringPart instanceof PlaceholderPart) {
                PlaceholderPart placeholderPart = (PlaceholderPart) stringPart;
                if (filter.test(placeholderPart.getPlaceholderOccurrence())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean anyLiteralPartMatch(Predicate<String> filter) {
        if (stringParts == null) {
            return filter.test(string);
        }

        for (StringPart stringPart : stringParts) {
            if (stringPart instanceof LiteralPart) {
                if (filter.test(stringPart.getRawValue())) {
                    return true;
                }
            }
        }

        return false;
    }

    public @NotNull String replacePlaceholders(PlaceholderReplaceFunction replaceFunction) {
        return replaceParts(replaceFunction, StringReplaceFunction.NO_REPLACEMENTS);
    }

    public @NotNull String replaceLiteralParts(StringReplaceFunction replaceFunction) {
        return replaceParts(PlaceholderReplaceFunction.NO_REPLACEMENTS, replaceFunction);
    }

    public @NotNull String replaceParts(
            PlaceholderReplaceFunction placeholderReplaceFunction,
            StringReplaceFunction literalPartReplaceFunction) {
        if (stringParts == null) {
            return literalPartReplaceFunction.getReplacement(string);
        }

        StringBuilder output = new StringBuilder(string.length());

        for (StringPart part : stringParts) {
            if (part instanceof LiteralPart) {
                output.append(literalPartReplaceFunction.getReplacement(part.getRawValue()));
            } else {
                output.append(part.getValue(placeholderReplaceFunction));
            }
        }

        return output.toString();
    }

    private static @Nullable List<StringPart> splitToParts(@NotNull String string) {
        int placeholderStartIndex = -1;
        int lastAppendIndex = 0;
        List<StringPart> stringParts = null; // Lazy initialization

        for (int currentIndex = 0; currentIndex < string.length(); currentIndex++) {
            char currentChar = string.charAt(currentIndex);

            if (placeholderStartIndex >= 0) {
                // Inside placeholder
                if (currentChar == PLACEHOLDER_END_CHAR) {
                    int endIndex = currentIndex + 1;

                    // The unparsed string includes the opening and closing tags (e.g.: "{online: lobby}")
                    String unparsedString = string.substring(placeholderStartIndex, endIndex);

                    // The content string does NOT include the opening and closing tags (e.g.: "online: lobby")
                    String contentString = unparsedString.substring(1, unparsedString.length() - 1);
                    PlaceholderOccurrence content = PlaceholderOccurrence.parse(contentString);

                    if (stringParts == null) {
                        stringParts = new ArrayList<>();
                    }

                    // Append leading literal part (if any)
                    if (placeholderStartIndex != lastAppendIndex) {
                        stringParts.add(new LiteralPart(string.substring(lastAppendIndex, placeholderStartIndex)));
                    }

                    // Append placeholder part
                    stringParts.add(new PlaceholderPart(content, unparsedString));
                    lastAppendIndex = endIndex;
                    placeholderStartIndex = -1;

                } else if (currentChar == PLACEHOLDER_START_CHAR) {
                    // Nested placeholder, ignore outer placeholder and update start index
                    placeholderStartIndex = currentIndex;
                }
            } else {
                // Outside placeholders, just look for the start of a placeholder
                if (currentChar == PLACEHOLDER_START_CHAR) {
                    placeholderStartIndex = currentIndex;
                }
            }
        }

        // Append trailing literal part (if any)
        if (lastAppendIndex != string.length() && stringParts != null) {
            stringParts.add(new LiteralPart(string.substring(lastAppendIndex)));
        }

        return stringParts;
    }

    private static @NotNull String joinParts(@NotNull List<StringPart> stringParts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (StringPart stringPart : stringParts) {
            stringBuilder.append(stringPart.getRawValue());
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        StringWithPlaceholders other = (StringWithPlaceholders) obj;
        return Objects.equals(this.string, other.string);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(string);
    }


    private interface StringPart {

        String getValue(PlaceholderReplaceFunction placeholderReplaceFunction);

        String getRawValue();

    }


    private static class LiteralPart implements StringPart {

        private final String value;

        LiteralPart(@NotNull String value) {
            this.value = value;
        }

        @Override
        public String getValue(PlaceholderReplaceFunction placeholderReplaceFunction) {
            return value;
        }

        @Override
        public String getRawValue() {
            return value;
        }

    }


    private static class ReplacementPart implements StringPart {

        private final String replacement;

        ReplacementPart(@NotNull String replacement) {
            this.replacement = replacement;
        }

        @Override
        public String getValue(PlaceholderReplaceFunction placeholderReplaceFunction) {
            return replacement;
        }

        @Override
        public String getRawValue() {
            return replacement;
        }

    }


    private static class PlaceholderPart implements StringPart {

        private final PlaceholderOccurrence placeholderOccurrence;
        private final String unreplacedString;

        PlaceholderPart(@NotNull PlaceholderOccurrence placeholderOccurrence, @NotNull String unreplacedString) {
            this.placeholderOccurrence = placeholderOccurrence;
            this.unreplacedString = unreplacedString;
        }

        @Override
        public String getValue(PlaceholderReplaceFunction placeholderReplaceFunction) {
            String replacement = placeholderReplaceFunction.getReplacement(placeholderOccurrence);
            if (replacement != null) {
                return replacement;
            } else {
                // If no replacement is provided return the unreplaced placeholder string
                return unreplacedString;
            }
        }

        @Override
        public String getRawValue() {
            return unreplacedString;
        }

        public PlaceholderOccurrence getPlaceholderOccurrence() {
            return placeholderOccurrence;
        }

    }

}
