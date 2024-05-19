package me.cleavest.both.packet;

import me.cleavest.both.INetHandlerPlayClient;
import me.cleavest.client.ClientApp;
import me.cleavest.client.ClientPanel;

/**
 * @author Cleavest on 19/5/2024
 */
public class NetHandlerPlayClient implements INetHandlerPlayClient {
    @Override
    public void handleChat(SPacket packet) {
        ClientApp.addMessageFrom(packet.getMessage(), packet.getAuthor());
    }

    @Override
    public void handleChange(SChangeNamePacket packet) {
        ClientApp.addMessage(packet.getOldName() + " -> " + packet.getNewName());
    }

    @Override
    public void handleSetName(SSetNamePacket sName) {
        ClientApp.setNickName(sName.getName().split("-")[0]);
    }


}
