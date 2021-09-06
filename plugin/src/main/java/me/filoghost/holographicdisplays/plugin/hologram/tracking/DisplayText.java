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
    private @NotNull StringWithPlaceholders textWithoutReplacements;
    private @Nullable String textWithGlobalReplacements;
    private @Nullable Map<Player, String> textWithIndividualReplacementsByPlayer;
    private boolean containsPlaceholderAPIPattern;

    DisplayText(PlaceholderTracker placeholderTracker) {
        this.placeholderTracker = placeholderTracker;
        this.textWithoutReplacements = StringWithPlaceholders.of(null);
        this.textWithGlobalReplacements = null;
        this.textWithIndividualReplacementsByPlayer = null;
    }

    void setWithoutReplacements(@Nullable String textString) {
        textWithoutReplacements = StringWithPlaceholders.of(textString);
        textWithGlobalReplacements = null;
        textWithIndividualReplacementsByPlayer = null;
        containsPlaceholderAPIPattern = textWithoutReplacements.anyLiteralPartMatch(PlaceholderAPIHook::containsPlaceholderPattern);
    }

    String getWithoutReplacements() {
        return textWithoutReplacements.getString();
    }

    String getWithGlobalReplacements() {
        return textWithGlobalReplacements.getString();
    }

    String getWithIndividualReplacements(Player player) {
        return textWithGlobalReplacements.replaceParts(
                (PlaceholderOccurrence placeholderOccurrence) -> {
                    return placeholderTracker.updateAndGetIndividualReplacement(placeholderOccurrence, player);
                },
                (String literalPart) -> {
                    if (containsPlaceholderAPIPattern
                            && PlaceholderAPIHook.isEnabled()
                            && PlaceholderAPIHook.containsPlaceholderPattern(literalPart)) {
                        return PlaceholderAPIHook.replacePlaceholders(player, literalPart);
                    } else {
                        return literalPart;
                    }
                }
        );
    }

    public boolean updateReplacements(Set<Player> players) {
        if (!textWithoutReplacements.containsUnreplacedPlaceholders()) {
            return false;
        }

        boolean changed = false;

        if (containsIndividualPlaceholders()) {
            if (textWithIndividualReplacementsByPlayer == null) {
                textWithIndividualReplacementsByPlayer = new HashMap<>();
            }
            for (Player player : players) {
                String textWithIndividualReplacements = textWithoutReplacements.replaceParts(
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
                String previousValue = textWithIndividualReplacementsByPlayer.put(player, textWithIndividualReplacements);
                if (!Objects.equals(textWithIndividualReplacements, previousValue)) {
                    changed = true;
                }
            }
        } else {
            String previousValue = textWithGlobalReplacements;
            textWithGlobalReplacements = textWithoutReplacements.replacePlaceholders(
                    placeholderTracker::updateAndGetGlobalReplacement);
            if (!Objects.equals(textWithGlobalReplacements, previousValue)) {
                changed = true;
            }
        }

        return changed;
    }

    boolean containsIndividualPlaceholders() {
        return containsPlaceholderAPIPattern || placeholderTracker.containsIndividualPlaceholders(textWithoutReplacements);
    }

}
