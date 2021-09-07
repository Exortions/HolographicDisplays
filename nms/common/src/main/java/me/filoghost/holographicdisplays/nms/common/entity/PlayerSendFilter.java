/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common.entity;

import me.filoghost.holographicdisplays.nms.common.PacketRecipient;

public interface PlayerSendFilter {

    boolean shouldReceivePacket(PacketRecipient packetRecipient, String text);

}
