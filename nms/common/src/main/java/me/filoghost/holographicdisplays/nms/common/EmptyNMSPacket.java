/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common;

import org.bukkit.entity.Player;

public enum EmptyNMSPacket implements NMSPacket {

    INSTANCE;

    @Override
    public void sendTo(Player player) {}

}
