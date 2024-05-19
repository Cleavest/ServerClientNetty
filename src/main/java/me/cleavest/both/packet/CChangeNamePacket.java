package me.cleavest.both.packet;

import io.netty.channel.Channel;
import me.cleavest.both.ConnectionState;
import me.cleavest.both.INetHandlerPlayServer;
import me.cleavest.both.State;

import java.io.IOException;

/**
 * @author Cleavest on 19/5/2024
 */
public class CChangeNamePacket implements Packet<INetHandlerPlayServer>{

    private String newName;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.newName = buf.readString(100);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(ConnectionState.getPacketId(this, State.client));
        buf.writeString(this.newName);
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler, Channel channel) {
        handler.processChangeName(this , channel);
    }

    public String getNewName() {
        return newName;
    }
}
