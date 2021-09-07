/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common;

import org.bukkit.entity.Player;

import java.util.function.Function;

public interface IndividualNetworkSendable {

    static IndividualNetworkSendable single(Function<String, NMSPacket> packet) {
        return new PacketGroup1(packet);
    }

    static IndividualNetworkSendable group(NMSPacket packet1, Function<String, NMSPacket> packet2) {
        return new PacketGroup2(packet1, packet2);
    }

    void sendTo(Player player, String text);


    class PacketGroup1 implements IndividualNetworkSendable {

        private final Function<String, NMSPacket> packet;

        public PacketGroup1(Function<String, NMSPacket> packet) {
            this.packet = packet;
        }

        @Override
        public void sendTo(Player player, String text) {
            packet.apply(text).sendTo(player);
        }

    }

    class PacketGroup2 implements IndividualNetworkSendable {

        private final NMSPacket packet1;
        private final Function<String, NMSPacket> packet2;

        public PacketGroup2(NMSPacket packet1, Function<String, NMSPacket> packet2) {
            this.packet1 = packet1;
            this.packet2 = packet2;
        }

        @Override
        public void sendTo(Player player, String text) {
            packet1.sendTo(player);
            packet2.apply(text).sendTo(player);
        }

    }

}
