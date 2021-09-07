/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.v1_17_R1;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import me.filoghost.holographicdisplays.nms.common.EntityID;
import me.filoghost.holographicdisplays.nms.common.IndividualNetworkSendable;
import me.filoghost.holographicdisplays.nms.common.NetworkSendable;
import me.filoghost.holographicdisplays.nms.common.entity.TextNMSPacketEntity;

class VersionTextNMSPacketEntity implements TextNMSPacketEntity {

    private final EntityID armorStandID;

    VersionTextNMSPacketEntity(EntityID armorStandID) {
        this.armorStandID = armorStandID;
    }

    @Override
    public NetworkSendable newSpawnPackets(PositionCoordinates position, String text) {
        return NetworkSendable.group(
                new EntityLivingSpawnNMSPacket(armorStandID, EntityTypeID.ARMOR_STAND, position, ARMOR_STAND_Y_OFFSET),
                EntityMetadataNMSPacket.builder(armorStandID)
                        .setArmorStandMarker()
                        .setCustomName(text)
                        .build());
    }

    @Override
    public IndividualNetworkSendable newSpawnPackets(PositionCoordinates position) {
        return IndividualNetworkSendable.group(
                new EntityLivingSpawnNMSPacket(armorStandID, EntityTypeID.ARMOR_STAND, position, ARMOR_STAND_Y_OFFSET),
                (String text) -> EntityMetadataNMSPacket.builder(armorStandID)
                        .setArmorStandMarker()
                        .setCustomName(text)
                        .build());
    }

    @Override
    public NetworkSendable newChangePackets(String text) {
        return EntityMetadataNMSPacket.builder(armorStandID)
                .setCustomName(text)
                .build();
    }

    @Override
    public IndividualNetworkSendable newChangePackets() {
        return IndividualNetworkSendable.single(
                (String text) -> EntityMetadataNMSPacket.builder(armorStandID)
                        .setCustomName(text)
                        .build());
    }

    @Override
    public NetworkSendable newTeleportPackets(PositionCoordinates position) {
        return new EntityTeleportNMSPacket(armorStandID, position, ARMOR_STAND_Y_OFFSET);
    }

    @Override
    public NetworkSendable newDestroyPackets() {
        return PacketHelper.newDestroyPackets(armorStandID);
    }

}
