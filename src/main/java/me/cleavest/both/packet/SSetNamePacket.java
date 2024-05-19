package me.cleavest.both.packet;

import io.netty.channel.Channel;
import me.cleavest.both.ConnectionState;
import me.cleavest.both.INetHandlerPlayClient;
import me.cleavest.both.State;

import java.io.IOException;

/**
 * @author Cleavest on 19/5/2024
 */
public class SSetNamePacket implements Packet<INetHandlerPlayClient>{

    private String name;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        name = buf.readString(100);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(ConnectionState.getPacketId(this, State.server));
        buf.writeString(name);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler, Channel channel) {
        handler.handleSetName(this);
    }

    public String getName() {
        return name;
    }
}
