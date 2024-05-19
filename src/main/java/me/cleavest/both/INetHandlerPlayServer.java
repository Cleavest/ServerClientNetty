package me.cleavest.both;

/**
 * @author Cleavest on 19/5/2024
 */
import io.netty.channel.Channel;
import me.cleavest.both.packet.CChangeNamePacket;
import me.cleavest.both.packet.CPacket;

public interface INetHandlerPlayServer extends INetHandler {
    void processChatMessage(CPacket packet, Channel channel);

    void processChangeName(CChangeNamePacket packet, Channel channel);
}
