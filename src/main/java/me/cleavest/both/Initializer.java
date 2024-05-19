package me.cleavest.both;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import me.cleavest.both.packet.PacketDecoder;
import me.cleavest.both.packet.PacketEncoder;

/**
 * @author Cleavest on 19/5/2024
 */
public class Initializer extends ChannelInitializer<SocketChannel> {
    INetHandler packetListener;
    State state;

    public Initializer(INetHandler handler, State state) {
        this.packetListener = handler;
        this.state = state;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new PacketDecoder(state));
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", new NetworkManager(packetListener));
    }
}