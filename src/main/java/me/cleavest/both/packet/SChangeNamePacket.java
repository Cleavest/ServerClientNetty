package me.cleavest.both.packet;

import io.netty.channel.Channel;
import me.cleavest.both.ConnectionState;
import me.cleavest.both.INetHandlerPlayClient;
import me.cleavest.both.State;

import java.io.IOException;

/**
 * @author Cleavest on 19/5/2024
 */
public class SChangeNamePacket implements Packet<INetHandlerPlayClient>{

    private String oldName;
    private String newName;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.oldName = buf.readString(100);
        this.newName = buf.readString(100);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(ConnectionState.getPacketId(this, State.server));
        buf.writeString(this.oldName);
        buf.writeString(this.newName);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler, Channel channel) {
        handler.handleChange(this);
    }


    public String getNewName() {
        return newName;
    }

    public String getOldName() {
        return oldName;
    }
}
