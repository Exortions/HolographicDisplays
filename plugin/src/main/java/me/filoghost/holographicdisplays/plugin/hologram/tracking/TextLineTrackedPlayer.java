/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import org.bukkit.entity.Player;

class TextLineTrackedPlayer extends TrackedPlayer {

    private String lastSeenText;

    TextLineTrackedPlayer(Player player) {
        super(player);
    }

    void setLastSeenText(String lastSeenText) {
        this.lastSeenText = lastSeenText;
    }

    public String getLastSeenText() {
        return lastSeenText;
    }

}
