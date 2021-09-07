/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NMSPacketList {

    // Lazily instantiate a list only when adding more than one element
    private @Nullable NMSPacket singlePacket;
    private @Nullable List<NMSPacket> multiplePackets;

    public void add(NMSPacket packet) {
        if (multiplePackets != null) {
            multiplePackets.add(packet);
        } else if (singlePacket != null) {
            multiplePackets = new ArrayList<>();
            multiplePackets.add(singlePacket);
            multiplePackets.add(packet);
            singlePacket = null;
        } else {
            singlePacket = packet;
        }
    }

    public void sendTo(Player player) {
        if (multiplePackets != null) {
            for (NMSPacket packet : multiplePackets) {
                packet.sendTo(player);
            }
        } else if (singlePacket != null) {
            singlePacket.sendTo(player);
        }
    }

}
