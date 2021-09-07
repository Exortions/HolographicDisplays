/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import me.filoghost.holographicdisplays.nms.common.NMSManager;
import me.filoghost.holographicdisplays.nms.common.entity.ClickableNMSPacketEntity;
import me.filoghost.holographicdisplays.plugin.hologram.base.BaseClickableHologramLine;
import me.filoghost.holographicdisplays.plugin.listener.LineClickListener;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class ClickableLineTracker<T extends BaseClickableHologramLine, U> extends PositionBasedLineTracker<T, U> {

    private final ClickableNMSPacketEntity clickableEntity;
    private final double positionOffsetY;
    private final LineClickListener lineClickListener;

    private boolean spawnClickableEntity;
    private boolean spawnClickableEntityChanged;

    public ClickableLineTracker(T line, NMSManager nmsManager, LineClickListener lineClickListener) {
        super(line);
        this.clickableEntity = nmsManager.newClickablePacketEntity();
        this.positionOffsetY = (line.getHeight() - ClickableNMSPacketEntity.SLIME_HEIGHT) / 2;
        this.lineClickListener = lineClickListener;
    }

    @MustBeInvokedByOverriders
    @Override
    public void onRemoval() {
        super.onRemoval();
        lineClickListener.unregisterLine(clickableEntity.getID());
    }

    @MustBeInvokedByOverriders
    @Override
    protected void detectChanges() {
        super.detectChanges();

        boolean spawnClickableEntity = line.hasClickCallback();
        if (this.spawnClickableEntity != spawnClickableEntity) {
            this.spawnClickableEntity = spawnClickableEntity;
            this.spawnClickableEntityChanged = true;
            if (spawnClickableEntity) {
                lineClickListener.registerLine(clickableEntity.getID(), line);
            } else {
                lineClickListener.unregisterLine(clickableEntity.getID());
            }
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void clearDetectedChanges() {
        super.clearDetectedChanges();
        this.spawnClickableEntityChanged = false;
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendSpawnPackets(Recipients recipients) {
        if (spawnClickableEntity) {
            recipients.send(clickableEntity.newSpawnPackets(getClickableEntityPosition()));
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendDestroyPackets(Recipients recipients) {
        if (spawnClickableEntity) {
            recipients.send(clickableEntity.newDestroyPackets());
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendChangesPackets(Recipients recipients) {
        super.sendChangesPackets(recipients);

        if (spawnClickableEntityChanged) {
            if (spawnClickableEntity) {
                recipients.send(clickableEntity.newSpawnPackets(getClickableEntityPosition()));
            } else {
                recipients.send(clickableEntity.newDestroyPackets());
            }
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendPositionChangePackets(Recipients recipients) {
        if (spawnClickableEntity) {
            recipients.send(clickableEntity.newTeleportPackets(getClickableEntityPosition()));
        }
    }

    private PositionCoordinates getClickableEntityPosition() {
        return position.addY(positionOffsetY);
    }

}
