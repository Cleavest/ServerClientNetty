package me.cleavest.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.cleavest.Config;
import me.cleavest.both.Initializer;
import me.cleavest.both.NetworkManager;
import me.cleavest.both.State;
import me.cleavest.both.packet.NetHandlerPlayServer;
import me.cleavest.both.packet.PacketDecoder;
import me.cleavest.both.packet.PacketEncoder;

/**
 * @author Cleavest on 18/5/2024
 */
public class ServerApp {

    private static Channel channel;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new Initializer(new NetHandlerPlayServer(), State.server));

            channel = bootstrap.bind(Config.port).sync().channel().closeFuture().sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public Channel getChannel() {
        return this.channel;
    }
}
