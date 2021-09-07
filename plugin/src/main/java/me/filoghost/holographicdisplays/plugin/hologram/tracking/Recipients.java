/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.nms.common.NetworkSendable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class Recipients implements Iterable<Player> {

    private final Collection<Player> recipients;

    public Recipients(Collection<Player> recipients) {
        this.recipients = recipients;
    }

    public void send(NetworkSendable networkSendable) {
        for (Player recipient : recipients) {
            networkSendable.sendTo(recipient); // TODO optimize with forEach
        }
    }

    @NotNull
    @Override
    public Iterator<Player> iterator() {
        return recipients.iterator();
    }

}
