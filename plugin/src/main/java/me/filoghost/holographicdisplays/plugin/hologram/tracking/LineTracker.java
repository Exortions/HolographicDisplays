/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.plugin.hologram.base.BaseHologramLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class LineTracker<T extends BaseHologramLine, U> {

    protected final T line;
    private final Map<Player, U> trackedPlayers;

    /**
     * Flag to indicate that the line has changed in some way and there could be the need to send update packets.
     */
    private boolean lineChanged;

    LineTracker(T line) {
        this.line = line;
        this.trackedPlayers = new HashMap<>();
    }

    final boolean shouldBeRemoved() {
        return line.isDeleted();
    }

    @MustBeInvokedByOverriders
    public void onRemoval() {
        clearTrackedPlayersAndSendPackets();
    }

    public final void setLineChanged() {
        lineChanged = true;
    }

    @MustBeInvokedByOverriders
    protected void update(Collection<? extends Player> onlinePlayers) {
        boolean sendChangesPackets = false;

        // First, detect the changes if the flag is on and set it off
        if (lineChanged) {
            lineChanged = false;
            detectChanges();
            sendChangesPackets = true;
        }

        if (updatePlaceholders()) {
            sendChangesPackets = true;
        }

        // Then, send the changes (if any) to already tracked players
        if (sendChangesPackets) {
            if (hasTrackedPlayers()) {
                sendChangesPackets(new Recipients(getTrackedPlayers()));
            }
            clearDetectedChanges();
        }

        // Finally, add/remove tracked players sending them the full spawn/destroy packets
        updateTrackedPlayersAndSendPackets(onlinePlayers);
    }

    protected abstract void detectChanges();

    protected abstract void clearDetectedChanges();

    protected abstract boolean updatePlaceholders();

    private void updateTrackedPlayersAndSendPackets(Collection<? extends Player> onlinePlayers) {
        if (!line.isInLoadedChunk()) {
            clearTrackedPlayersAndSendPackets();
            return;
        }

        // Lazy initialization
        List<Player> addedPlayers = null;
        List<Player> removedPlayers = null;

        for (Player player : onlinePlayers) {
            if (shouldTrackPlayer(player)) {
                if (!trackedPlayers.containsKey(player)) {
                    trackedPlayers.put(player, createTrackedPlayerData(player));
                    if (addedPlayers == null) {
                        addedPlayers = new ArrayList<>();
                    }
                    addedPlayers.add(player);
                }
            } else {
                if (trackedPlayers.containsKey(player)) {
                    trackedPlayers.remove(player);
                    if (removedPlayers == null) {
                        removedPlayers = new ArrayList<>();
                    }
                    removedPlayers.add(player);
                }
            }
        }

        if (addedPlayers != null) {
            sendSpawnPackets(new Recipients(addedPlayers));
        }
        if (removedPlayers != null) {
            sendDestroyPackets(new Recipients(removedPlayers));
        }
    }

    protected abstract U createTrackedPlayerData(Player player);

    protected abstract boolean shouldTrackPlayer(Player player);

    protected final boolean hasTrackedPlayers() {
        return !trackedPlayers.isEmpty();
    }

    protected final Set<Player> getTrackedPlayers() {
        return trackedPlayers.keySet();
    }

    protected final U getTrackedPlayerData(Player player) {
        return trackedPlayers.get(player);
    }

    public final boolean isTrackedPlayer(Player player) {
        return trackedPlayers.containsKey(player);
    }

    protected final void removeTrackedPlayer(Player player) {
        trackedPlayers.remove(player);
    }

    protected final void clearTrackedPlayersAndSendPackets() {
        if (!hasTrackedPlayers()) {
            return;
        }

        sendDestroyPackets(new Recipients(getTrackedPlayers()));
        trackedPlayers.clear();
    }

    protected abstract void sendSpawnPackets(Recipients recipients);

    protected abstract void sendDestroyPackets(Recipients recipients);

    protected abstract void sendChangesPackets(Recipients recipients);

}
