/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common;

import org.bukkit.entity.Player;

public interface NetworkSendable {

    static NetworkSendable group(NMSPacket packet1, NMSPacket packet2) {
        return new PacketGroup2(packet1, packet2);
    }

    static NetworkSendable group(NMSPacket packet1, NMSPacket packet2, NMSPacket packet3, NMSPacket packet4, NMSPacket packet5) {
        return new PacketGroup5(packet1, packet2, packet3, packet4, packet5);
    }

    void sendTo(Player player);


    class PacketGroup2 implements NetworkSendable {

        private final NMSPacket packet1;
        private final NMSPacket packet2;

        public PacketGroup2(NMSPacket packet1, NMSPacket packet2) {
            this.packet1 = packet1;
            this.packet2 = packet2;
        }

        @Override
        public void sendTo(Player player) {
            packet1.sendTo(player);
            packet2.sendTo(player);
        }

    }

    class PacketGroup5 implements NetworkSendable {

        private final NMSPacket packet1;
        private final NMSPacket packet2;
        private final NMSPacket packet3;
        private final NMSPacket packet4;
        private final NMSPacket packet5;

        public PacketGroup5(NMSPacket packet1, NMSPacket packet2, NMSPacket packet3, NMSPacket packet4, NMSPacket packet5) {
            this.packet1 = packet1;
            this.packet2 = packet2;
            this.packet3 = packet3;
            this.packet4 = packet4;
            this.packet5 = packet5;
        }

        @Override
        public void sendTo(Player player) {
            packet1.sendTo(player);
            packet2.sendTo(player);
            packet3.sendTo(player);
            packet4.sendTo(player);
            packet5.sendTo(player);
        }

    }

}
