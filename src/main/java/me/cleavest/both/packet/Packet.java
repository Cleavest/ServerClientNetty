package me.cleavest.both.packet;

import java.io.IOException;

import io.netty.channel.Channel;
import me.cleavest.both.INetHandler;

/**
 * @author Cleavest on 19/5/2024
 */
public interface Packet<T extends INetHandler> {
    public void readPacketData(PacketBuffer buf) throws IOException;

    public void writePacketData(PacketBuffer buf) throws IOException;

    public void processPacket(T handler, Channel channel);
}
