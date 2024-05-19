package me.cleavest.both.packet;

/**
 * @author Cleavest on 19/5/2024
 */
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.cleavest.both.ConnectionState;
import me.cleavest.both.State;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    protected State state;

    public PacketDecoder(State state) {
        this.state = state;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (buf.readableBytes() != 0) {
            PacketBuffer pbuf = new PacketBuffer(buf);
            int id = pbuf.readVarInt();
            Packet<?> packet = ConnectionState.getPacket(id, state);
            packet.readPacketData(pbuf);
            out.add(packet);
        }
    }
}
