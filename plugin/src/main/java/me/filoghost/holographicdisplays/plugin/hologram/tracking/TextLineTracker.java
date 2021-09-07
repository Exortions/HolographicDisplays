/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.nms.common.IndividualNetworkSendable;
import me.filoghost.holographicdisplays.nms.common.NMSManager;
import me.filoghost.holographicdisplays.nms.common.NetworkSendable;
import me.filoghost.holographicdisplays.nms.common.entity.TextNMSPacketEntity;
import me.filoghost.holographicdisplays.plugin.hologram.base.BaseTextHologramLine;
import me.filoghost.holographicdisplays.plugin.listener.LineClickListener;
import me.filoghost.holographicdisplays.plugin.placeholder.tracking.PlaceholderTracker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Objects;

public class TextLineTracker extends ClickableLineTracker<BaseTextHologramLine, TextLineTrackedPlayer> {

    private final TextNMSPacketEntity textEntity;

    private final DisplayText displayText;
    private boolean displayTextChanged;
    private boolean allowPlaceholders;

    public TextLineTracker(
            BaseTextHologramLine line,
            NMSManager nmsManager,
            LineClickListener lineClickListener,
            PlaceholderTracker placeholderTracker) {
        super(line, nmsManager, lineClickListener);
        this.textEntity = nmsManager.newTextPacketEntity();
        this.displayText = new DisplayText(placeholderTracker);
    }

    @Override
    protected boolean updatePlaceholders() {
        if (!allowPlaceholders) {
            return false;
        }

        boolean placeholdersChanged = displayText.updateReplacements(getTrackedPlayers());
        if (placeholdersChanged) {
            displayTextChanged = true; // Mark as changed to trigger a packet send with updated placeholders
        }
        return placeholdersChanged;
    }

    @Override
    protected TextLineTrackedPlayer createTrackedPlayer(Player player) {
        return new TextLineTrackedPlayer(player);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void detectChanges() {
        super.detectChanges();

        String displayText = line.getText();
        if (!Objects.equals(this.displayText.getWithoutReplacements(), displayText)) {
            this.displayText.setWithoutReplacements(displayText);
            this.displayTextChanged = true;
        }

        boolean allowPlaceholders = line.isAllowPlaceholders();
        if (this.allowPlaceholders != allowPlaceholders) {
            this.allowPlaceholders = allowPlaceholders;
            this.displayTextChanged = true;
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void clearDetectedChanges() {
        super.clearDetectedChanges();
        this.displayTextChanged = false;
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendSpawnPackets(Recipients recipients) {
        super.sendSpawnPackets(recipients);

        if (!allowPlaceholders) {
            String text = displayText.getWithoutReplacements();
            NetworkSendable spawnPackets = textEntity.newSpawnPackets(position, text);
            for (Player recipient : recipients) {
                spawnPackets.sendTo(recipient);
                getTrackedPlayer(recipient).setLastSeenText(text);
            }
        } else if (displayText.containsIndividualPlaceholders()) {
            IndividualNetworkSendable spawnPackets = textEntity.newSpawnPackets(position);
            for (Player recipient : recipients) {
                String text = displayText.getWithIndividualReplacements(recipient);
                spawnPackets.sendTo(recipient, text);
                getTrackedPlayer(recipient).setLastSeenText(text);
            }
        } else {
            String text = displayText.getWithGlobalReplacements();
            NetworkSendable spawnPackets = textEntity.newSpawnPackets(position, text);
            for (Player recipient : recipients) {
                spawnPackets.sendTo(recipient);
                getTrackedPlayer(recipient).setLastSeenText(text);
            }
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendDestroyPackets(Recipients recipients) {
        super.sendDestroyPackets(recipients);
        recipients.send(textEntity.newDestroyPackets());
    }

    @Override
    protected void sendChangesPackets(Recipients recipients) {
        super.sendChangesPackets(recipients);

        if (displayTextChanged) {
            if (!allowPlaceholders) {
                String text = displayText.getWithoutReplacements();
                NetworkSendable spawnPackets = textEntity.newChangePackets(text);
                for (Player recipient : recipients) {
                    spawnPackets.sendTo(recipient);
                    getTrackedPlayer(recipient).setLastSeenText(text);
                }
            } else if (displayText.containsIndividualPlaceholders()) {
                IndividualNetworkSendable spawnPackets = textEntity.newChangePackets();
                for (Player recipient : recipients) {
                    String text = displayText.getWithIndividualReplacements(recipient);
                    spawnPackets.sendTo(recipient, text);
                    getTrackedPlayer(recipient).setLastSeenText(text);
                }
            } else {
                String text = displayText.getWithGlobalReplacements();
                NetworkSendable spawnPackets = textEntity.newChangePackets(text);
                for (Player recipient : recipients) {
                    spawnPackets.sendTo(recipient);
                    getTrackedPlayer(recipient).setLastSeenText(text);
                }
            }
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendPositionChangePackets(Recipients recipients) {
        super.sendPositionChangePackets(recipients);
        recipients.send(textEntity.newTeleportPackets(position));
    }

}
