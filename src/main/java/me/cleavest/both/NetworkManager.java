package me.cleavest.both;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.cleavest.both.packet.*;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Cleavest on 19/5/2024
 */
public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    private Channel channel;
    public static ChannelGroup allConnected = new DefaultChannelGroup("all-connected", GlobalEventExecutor.INSTANCE);
    public static Map<Channel, String> names = new HashMap<Channel, String>();
    private INetHandler packetListener;

    public NetworkManager(INetHandler handler) {
        packetListener = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channel, Packet<?> packet) throws IOException {
        if (this.channel.isOpen()) {
            try {
                ((Packet<INetHandler>) packet).processPacket(this.packetListener, channel.channel());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext channel) throws Exception {
        super.channelActive(channel);
        this.channel = channel.channel();
        allConnected.add(channel.channel());
        String randomUUID = UUID.randomUUID().toString();
        names.put(channel.channel(), randomUUID);

        if (packetListener instanceof NetHandlerPlayServer) {
            Packet<INetHandlerPlayClient> packet = new SSetNamePacket();
            try {
                NetworkManager.sendMessage(channel.channel(), packet, randomUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext channel) {
        allConnected.remove(channel.channel());
        closeChannel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channel, Throwable cause) throws Exception {
        closeChannel();
    }

    public void closeChannel() {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
        }
    }

    public static void sendMessage(Channel channel, Packet<?> packet, String message) throws IOException {
        ByteBuf buf = Unpooled.buffer();
        PacketBuffer pbuf = new PacketBuffer(buf);
        pbuf.writeString(message);
        packet.readPacketData(pbuf);
        channel.writeAndFlush(packet);
    }

    public static void sendAll(Packet<?> packet, String message, String author) throws IOException {
        ByteBuf buf = Unpooled.buffer();
        PacketBuffer pbuf = new PacketBuffer(buf);
        pbuf.writeString(message);
        pbuf.writeString(author);
        packet.readPacketData(pbuf);
        allConnected.writeAndFlush(packet);
    }

    public static void sendAllChange(Packet<?> packet, String oldName, String newName) throws IOException {
        ByteBuf buf = Unpooled.buffer();
        PacketBuffer pbuf = new PacketBuffer(buf);
        pbuf.writeString(oldName);
        pbuf.writeString(newName);
        packet.readPacketData(pbuf);
        allConnected.writeAndFlush(packet);
    }


}
