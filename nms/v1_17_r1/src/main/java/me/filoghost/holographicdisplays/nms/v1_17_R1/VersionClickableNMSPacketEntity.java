/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.v1_17_R1;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import me.filoghost.holographicdisplays.nms.common.EntityID;
import me.filoghost.holographicdisplays.nms.common.NetworkSendable;
import me.filoghost.holographicdisplays.nms.common.entity.ClickableNMSPacketEntity;

public class VersionClickableNMSPacketEntity implements ClickableNMSPacketEntity {

    private final EntityID slimeID;

    public VersionClickableNMSPacketEntity(EntityID slimeID) {
        this.slimeID = slimeID;
    }

    @Override
    public EntityID getID() {
        return slimeID;
    }

    @Override
    public NetworkSendable newSpawnPackets(PositionCoordinates position) {
        return NetworkSendable.group(
                new EntityLivingSpawnNMSPacket(slimeID, EntityTypeID.SLIME, position, SLIME_Y_OFFSET),
                EntityMetadataNMSPacket.builder(slimeID)
                        .setInvisible()
                        .setSlimeSmall() // Required for a correct client-side collision box
                        .build());
    }

    @Override
    public NetworkSendable newTeleportPackets(PositionCoordinates position) {
        return new EntityTeleportNMSPacket(slimeID, position, SLIME_Y_OFFSET);
    }

    @Override
    public NetworkSendable newDestroyPackets() {
        return PacketHelper.newDestroyPackets(slimeID);
    }

}
