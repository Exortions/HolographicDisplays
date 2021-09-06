/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.common.entity;

import org.bukkit.entity.Player;

public interface PlayerSendFilter {

    boolean shouldReceivePacket(Player player, String text);

}
