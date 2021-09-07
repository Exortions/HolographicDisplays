/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.v1_17_R1;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import me.filoghost.holographicdisplays.nms.common.EntityID;
import me.filoghost.holographicdisplays.nms.common.NetworkSendable;
import me.filoghost.holographicdisplays.nms.common.entity.ItemNMSPacketEntity;
import org.bukkit.inventory.ItemStack;

public class VersionItemNMSPacketEntity implements ItemNMSPacketEntity {

    private final EntityID itemID;
    private final EntityID vehicleID;

    public VersionItemNMSPacketEntity(EntityID itemID, EntityID vehicleID) {
        this.itemID = itemID;
        this.vehicleID = vehicleID;
    }

    @Override
    public NetworkSendable newSpawnPackets(PositionCoordinates position, ItemStack itemStack) {
        return NetworkSendable.group(
                new EntityLivingSpawnNMSPacket(vehicleID, EntityTypeID.ARMOR_STAND, position, ITEM_Y_OFFSET),
                EntityMetadataNMSPacket.builder(vehicleID)
                        .setArmorStandMarker()
                        .build(),
                new EntitySpawnNMSPacket(itemID, EntityTypeID.ITEM, position, ITEM_Y_OFFSET),
                EntityMetadataNMSPacket.builder(itemID)
                        .setItemStack(itemStack)
                        .build(),
                new EntityMountNMSPacket(vehicleID, itemID));
    }

    @Override
    public NetworkSendable newChangePackets(ItemStack itemStack) {
        return EntityMetadataNMSPacket.builder(itemID)
                .setItemStack(itemStack)
                .build();
    }

    @Override
    public NetworkSendable newTeleportPackets(PositionCoordinates position) {
        return new EntityTeleportNMSPacket(vehicleID, position, ITEM_Y_OFFSET);
    }

    @Override
    public NetworkSendable newDestroyPackets() {
        return PacketHelper.newDestroyPackets(itemID, vehicleID);
    }

}
