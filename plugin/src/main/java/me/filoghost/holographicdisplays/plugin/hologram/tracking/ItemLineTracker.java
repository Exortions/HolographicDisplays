/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.nms.common.NMSManager;
import me.filoghost.holographicdisplays.nms.common.entity.ItemNMSPacketEntity;
import me.filoghost.holographicdisplays.plugin.hologram.base.BaseItemHologramLine;
import me.filoghost.holographicdisplays.plugin.listener.LineClickListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Collection;
import java.util.Objects;

public class ItemLineTracker extends ClickableLineTracker<BaseItemHologramLine, TrackedPlayer> {

    private final ItemNMSPacketEntity itemEntity;

    private ItemStack itemStack;
    private boolean itemStackChanged;

    private boolean spawnItemEntity;
    private boolean spawnItemEntityChanged;

    public ItemLineTracker(BaseItemHologramLine line, NMSManager nmsManager, LineClickListener lineClickListener) {
        super(line, nmsManager, lineClickListener);
        this.itemEntity = nmsManager.newItemPacketEntity();
    }

    @MustBeInvokedByOverriders
    @Override
    protected void update(Collection<? extends Player> onlinePlayers) {
        super.update(onlinePlayers);

        if (spawnItemEntity && hasTrackedPlayers() && line.hasPickupCallback()) {
            for (Player trackedPlayer : getTrackedPlayers()) {
                if (CollisionHelper.isInPickupRange(trackedPlayer, position)) {
                    line.onPickup(trackedPlayer);
                }
            }
        }
    }

    @Override
    protected boolean updatePlaceholders() {
        return false;
    }

    @Override
    protected TrackedPlayer createTrackedPlayer(Player player) {
        return new TrackedPlayer(player);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void detectChanges() {
        super.detectChanges();

        ItemStack itemStack = line.getItemStack();
        if (!Objects.equals(this.itemStack, itemStack)) {
            this.itemStack = itemStack;
            this.itemStackChanged = true;
        }

        boolean spawnItemEntity = itemStack != null;
        if (this.spawnItemEntity != spawnItemEntity) {
            this.spawnItemEntity = spawnItemEntity;
            this.spawnItemEntityChanged = true;
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void clearDetectedChanges() {
        super.clearDetectedChanges();
        this.itemStackChanged = false;
        this.spawnItemEntityChanged = false;
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendSpawnPackets(Recipients recipients) {
        super.sendSpawnPackets(recipients);

        if (spawnItemEntity) {
            recipients.send(itemEntity.newSpawnPackets(position, itemStack));
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendDestroyPackets(Recipients recipients) {
        super.sendDestroyPackets(recipients);

        if (spawnItemEntity) {
            recipients.send(itemEntity.newDestroyPackets());
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendChangesPackets(Recipients recipients) {
        super.sendChangesPackets(recipients);

        if (spawnItemEntityChanged) {
            if (spawnItemEntity) {
                recipients.send(itemEntity.newSpawnPackets(position, itemStack));
            } else {
                recipients.send(itemEntity.newDestroyPackets());
            }
        } else if (itemStackChanged) {
            // Only send item changes if full spawn/destroy packets were not sent
            recipients.send(itemEntity.newChangePackets(itemStack));
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendPositionChangePackets(Recipients recipients) {
        super.sendPositionChangePackets(recipients);

        if (spawnItemEntity) {
            recipients.send(itemEntity.newTeleportPackets(position));
        }
    }

}
