/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common.entity;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import me.filoghost.holographicdisplays.nms.common.EntityID;
import me.filoghost.holographicdisplays.nms.common.NetworkSendable;

public interface ClickableNMSPacketEntity extends NMSPacketEntity {

    double SLIME_Y_OFFSET = 0;
    double SLIME_HEIGHT = 0.5;

    EntityID getID();

    NetworkSendable newSpawnPackets(PositionCoordinates position);

}
