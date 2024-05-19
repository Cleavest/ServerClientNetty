package me.cleavest.both.packet;

/**
 * @author Cleavest on 19/5/2024
 */
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf buf) throws Exception {
        PacketBuffer pbuf = new PacketBuffer(buf);
        packet.writePacketData(pbuf);
    }
}
