/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common.entity;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import me.filoghost.holographicdisplays.nms.common.IndividualNetworkSendable;
import me.filoghost.holographicdisplays.nms.common.NetworkSendable;

public interface TextNMSPacketEntity extends NMSPacketEntity {

    double ARMOR_STAND_Y_OFFSET = -0.29;
    double ARMOR_STAND_TEXT_HEIGHT = 0.23;

    NetworkSendable newSpawnPackets(PositionCoordinates position, String text);

    IndividualNetworkSendable newSpawnPackets(PositionCoordinates position);

    NetworkSendable newChangePackets(String text);

    IndividualNetworkSendable newChangePackets();


}
