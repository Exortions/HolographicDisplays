/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.plugin.bridge.placeholderapi.PlaceholderAPIHook;
import me.filoghost.holographicdisplays.plugin.placeholder.parsing.PlaceholderOccurrence;
import me.filoghost.holographicdisplays.plugin.placeholder.parsing.StringWithPlaceholders;
import me.filoghost.holographicdisplays.plugin.placeholder.tracking.PlaceholderTracker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class DisplayText {

    private final PlaceholderTracker placeholderTracker;

    private @Nullable StringWithPlaceholders textWithoutReplacements;
    private @Nullable String textWithGlobalReplacements;
    private @Nullable Map<Player, String> textWithIndividualReplacementsByPlayer;
    private boolean containsPlaceholderAPIPattern;

    DisplayText(PlaceholderTracker placeholderTracker) {
        this.placeholderTracker = placeholderTracker;
    }

    boolean containsIndividualPlaceholders() {
        if (textWithoutReplacements == null) {
            return false;
        }
        return containsPlaceholderAPIPattern || placeholderTracker.containsIndividualPlaceholders(textWithoutReplacements);
    }

    void setWithoutReplacements(@Nullable String textString) {
        textWithoutReplacements = StringWithPlaceholders.ofOrNull(textString);
        textWithGlobalReplacements = null;
        textWithIndividualReplacementsByPlayer = null;
        containsPlaceholderAPIPattern = textWithoutReplacements != null
                && textWithoutReplacements.anyLiteralPartMatch(PlaceholderAPIHook::containsPlaceholderPattern);
    }

    @Nullable String getWithoutReplacements() {
        if (textWithoutReplacements == null) {
            return null;
        }
        return textWithoutReplacements.getString();
    }

    @Nullable String getWithGlobalReplacements() {
        if (textWithGlobalReplacements == null) {
            return null;
        }
        return textWithGlobalReplacements;
    }

    @NotNull String getWithIndividualReplacements(Player player) {
        if (textWithIndividualReplacementsByPlayer == null) {
            throw new IllegalStateException();
        }
        String textWithIndividualReplacements = textWithIndividualReplacementsByPlayer.get(player);
        if (textWithIndividualReplacements == null) {
            textWithIndividualReplacements = computeTextWithIndividualReplacements(player);
            textWithIndividualReplacementsByPlayer.put(player, textWithIndividualReplacements);
        }
        return textWithIndividualReplacements;
    }

    public boolean updateReplacements(Set<Player> players) {
        if (textWithoutReplacements == null || !textWithoutReplacements.containsPlaceholders()) {
            return false;
        }

        boolean changed = false;

        if (containsIndividualPlaceholders()) {
            if (textWithIndividualReplacementsByPlayer == null) {
                textWithIndividualReplacementsByPlayer = new HashMap<>();
            }
            for (Player player : players) {
                String textWithIndividualReplacements = computeTextWithIndividualReplacements(player);
                String previousValue = textWithIndividualReplacementsByPlayer.put(player, textWithIndividualReplacements);
                if (!Objects.equals(textWithIndividualReplacements, previousValue)) {
                    changed = true;
                }
            }
            textWithIndividualReplacementsByPlayer.keySet().retainAll(players);
        } else {
            String previousValue = textWithGlobalReplacements;
            textWithGlobalReplacements = computeTextWithGlobalReplacements();
            if (!Objects.equals(textWithGlobalReplacements, previousValue)) {
                changed = true;
            }
        }

        return changed;
    }

    private @NotNull String computeTextWithGlobalReplacements() {
        return textWithoutReplacements.replacePlaceholders(placeholderTracker::updateAndGetGlobalReplacement);
    }

    private @NotNull String computeTextWithIndividualReplacements(Player player) {
        return textWithoutReplacements.replaceParts(
                (PlaceholderOccurrence placeholderOccurrence) -> {
                    return placeholderTracker.updateAndGetReplacement(placeholderOccurrence, player);
                },
                (String literalPart) -> {
                    if (containsPlaceholderAPIPattern
                            && PlaceholderAPIHook.isEnabled()
                            && PlaceholderAPIHook.containsPlaceholderPattern(literalPart)) {
                        return PlaceholderAPIHook.replacePlaceholders(player, literalPart);
                    } else {
                        return literalPart;
                    }
                });
    }

}
