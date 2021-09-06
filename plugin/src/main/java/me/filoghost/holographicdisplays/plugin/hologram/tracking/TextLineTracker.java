/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.nms.common.NMSManager;
import me.filoghost.holographicdisplays.nms.common.NMSPacket;
import me.filoghost.holographicdisplays.nms.common.NMSPacketList;
import me.filoghost.holographicdisplays.nms.common.entity.TextNMSPacketEntity;
import me.filoghost.holographicdisplays.plugin.hologram.base.BaseTextHologramLine;
import me.filoghost.holographicdisplays.plugin.listener.LineClickListener;
import me.filoghost.holographicdisplays.plugin.placeholder.tracking.PlaceholderTracker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Objects;

public class TextLineTracker extends ClickableLineTracker<BaseTextHologramLine, MutableString> {

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
    protected MutableString createTrackedPlayerData(Player player) {
        return new MutableString();
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
    protected void addSpawnPackets(NMSPacketList packetList) {
        super.addSpawnPackets(packetList);

        if (!allowPlaceholders) {
            String text = displayText.getWithoutReplacements();
            textEntity.addSpawnPackets(packetList, position, text, (player, sentText) -> {
                getTrackedPlayerData(player).set(sentText);
                return true;
            });
        } else if (displayText.containsIndividualPlaceholders()) {
            textEntity.addSpawnPackets(packetList, position, player -> {
                MutableString lastSeenText = getTrackedPlayerData(player);
                String text = displayText.getWithIndividualReplacements(player);
                lastSeenText.set(text);
                return text;
            });
        } else {
            String text = displayText.getWithGlobalReplacements();
            textEntity.addSpawnPackets(packetList, position, text);
            for (MutableString lastSeenText : getTrackedPlayersData()) {
                lastSeenText.set(text);
            }
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void addDestroyPackets(Recipients recipients) {
        super.addDestroyPackets(recipients);
        textEntity.addDestroyPackets(packetList);
    }

    @Override
    protected void addChangesPackets(Recipients recipients) {
        super.addChangesPackets(recipients);

        if (displayTextChanged) {
            if (!allowPlaceholders) {
                String text = displayText.getWithoutReplacements();
                NMSPacket packet = textEntity.getChangePacket(text);
                for (Player player : recipients) {
                    packet.sendTo(player);
                    getTrackedPlayerData(player).set(text);
                }
            } else if (displayText.containsIndividualPlaceholders()) {
                recipients.addIndividual(player -> {
                    textEntity.getChangePacket(displayText.getWithIndividualReplacements(player));
                });
            } else {
                recipients.addGlobal(textEntity.getChangePacket(displayText.getWithGlobalReplacements());
            }
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void addPositionChangePackets(Recipients recipients) {
        super.addPositionChangePackets(packetList);
        textEntity.addTeleportPackets(packetList, position);
    }

}
