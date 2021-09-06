/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.nms.common.IndividualNMSPacket;
import me.filoghost.holographicdisplays.nms.common.NMSPacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class Recipients implements Iterable<Player> {

    private List<NMSPacket> packets;

    public void addGlobal(NMSPacket packet) {
        packets.add(packet);
    }

    public void addIndividual(Function<Player, NMSPacket> packetFactory) {
        packets.add(new IndividualNMSPacket(packetFactory));
    }

    @NotNull
    @Override
    public Iterator<Player> iterator() {
        return null;
    }

}
