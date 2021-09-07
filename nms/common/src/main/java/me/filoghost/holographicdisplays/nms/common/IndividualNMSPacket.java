/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common;

import java.util.function.Function;

public class IndividualNMSPacket implements NMSPacket {

    private final Function<PacketRecipient, NMSPacket> individualPacketFactory;

    public IndividualNMSPacket(Function<PacketRecipient, NMSPacket> individualPacketFactory) {
        this.individualPacketFactory = individualPacketFactory;
    }

    @Override
    public void sendTo(PacketRecipient packetRecipient) {
        individualPacketFactory.apply(packetRecipient).sendTo(packetRecipient);
    }

}
