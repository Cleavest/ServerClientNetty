package me.cleavest.both;

import me.cleavest.both.packet.SChangeNamePacket;
import me.cleavest.both.packet.SPacket;
import me.cleavest.both.packet.SSetNamePacket;

/**
 * @author Cleavest on 19/5/2024
 */
public interface INetHandlerPlayClient extends INetHandler {
    void handleChat(SPacket packet);

    void handleChange(SChangeNamePacket sName);

    void handleSetName(SSetNamePacket sName);
}
