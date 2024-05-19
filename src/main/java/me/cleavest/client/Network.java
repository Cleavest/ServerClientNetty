package me.cleavest.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import me.cleavest.Config;
import me.cleavest.both.Initializer;
import me.cleavest.both.State;
import me.cleavest.both.packet.NetHandlerPlayClient;

/**
 * @author Cleavest on 18/5/2024
 */
public class Network {

    private Channel channel;

    public Network(){
        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new Initializer(new NetHandlerPlayClient(), State.client));
                //this.channel = bootstrap.connect(Config.ip, Config.port).sync().channel();
                ChannelFuture channelFuture = bootstrap.connect(Config.ip, Config.port).sync();
                channel = channelFuture.channel();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
    }


    public Channel getChannel() {
        return this.channel;
    }
}
