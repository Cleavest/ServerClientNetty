package me.cleavest.client;

import me.cleavest.both.INetHandlerPlayClient;
import me.cleavest.both.INetHandlerPlayServer;
import me.cleavest.both.NetworkManager;
import me.cleavest.both.packet.CChangeNamePacket;
import me.cleavest.both.packet.CPacket;
import me.cleavest.both.packet.Packet;
import me.cleavest.both.packet.SChangeNamePacket;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Cleavest on 18/5/2024
 */
public class ClientPanel extends JPanel {

    private JTextField messageField;
    private JTextField channelField;
    private JTextArea messageArea;
    private JButton changeNameButton;

    public ClientPanel(Network network) {
        setLayout(new BorderLayout());

        messageArea = new JTextArea(10, 20);
        messageField = new JTextField(10);

        messageArea.setEditable(false);

        messageField.addActionListener(event -> {
            String text = messageField.getText();

            Packet<INetHandlerPlayServer> packet = new CPacket();
            try {
                NetworkManager.sendMessage(network.getChannel(), packet, text);
            } catch (Exception e) {
               e.printStackTrace();
            }
            messageField.setText(null);
        });



        add(messageField, BorderLayout.PAGE_START);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel();


        channelField = new JTextField(10);
        changeNameButton = new JButton("Change Name");
        actionsPanel.add(channelField);
        actionsPanel.add(changeNameButton);

        changeNameButton.addActionListener(event -> {
            if (channelField.getText().length() > 0) {
                String name = channelField.getText();
                Packet<INetHandlerPlayServer> packet = new CChangeNamePacket();
                try {
                    NetworkManager.sendMessage(network.getChannel(), packet, channelField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ClientApp.setNickName(name);
                channelField.setText(null);
            }
        });

        add(actionsPanel, BorderLayout.PAGE_END);


    }

    public JTextArea getMessageArea() {
        return messageArea;
    }
}
