package me.cleavest.both.packet;

import io.netty.channel.Channel;
import me.cleavest.both.ConnectionState;
import me.cleavest.both.INetHandlerPlayClient;
import me.cleavest.both.State;

import java.io.IOException;

/**
 * @author Cleavest on 19/5/2024
 */
public class SPacket implements Packet<INetHandlerPlayClient>{

    private String message;
    private String author;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.message = buf.readString(100);
        this.author = buf.readString(100);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(ConnectionState.getPacketId(this, State.server));
        buf.writeString(message);
        buf.writeString(author);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler, Channel channel) {
        handler.handleChat(this);
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
