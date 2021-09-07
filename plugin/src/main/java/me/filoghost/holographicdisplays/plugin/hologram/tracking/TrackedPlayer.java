/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import org.bukkit.entity.Player;

class TrackedPlayer {

    private final Player player;

    TrackedPlayer(Player player) {
        this.player = player;
    }

    Player getPlayer() {
        return player;
    }

}
