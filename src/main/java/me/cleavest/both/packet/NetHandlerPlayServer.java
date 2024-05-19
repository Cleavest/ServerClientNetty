package me.cleavest.both.packet;

import io.netty.channel.Channel;
import me.cleavest.both.INetHandlerPlayClient;
import me.cleavest.both.INetHandlerPlayServer;
import me.cleavest.both.NetworkManager;

import java.io.IOException;

/**
 * @author Cleavest on 19/5/2024
 */
public class NetHandlerPlayServer implements INetHandlerPlayServer {
    @Override
    public void processChatMessage(CPacket packet, Channel channel) {
        String name = "ERROR";
        if (NetworkManager.names.containsKey(channel)) {
            String temp = NetworkManager.names.get(channel);
//            if (temp.split("-").length > 0) {
//                name = temp.split("-")[0];
//            } else {
//                name = temp.split("-")[1];
//            }

            name = temp.split("-").length > 0 ? temp.split("-")[0] : temp;
        }
        Packet<INetHandlerPlayClient> packets = new SPacket();
        try {
            NetworkManager.sendAll(packets, packet.getMessage(), name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processChangeName(CChangeNamePacket packet, Channel channel) {
        String oldName = NetworkManager.names.get(channel);
        String newName = packet.getNewName();

        NetworkManager.names.put(channel,newName);

        Packet<INetHandlerPlayClient> packets = new SChangeNamePacket();
        try {
            NetworkManager.sendAllChange(packets, oldName, newName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
