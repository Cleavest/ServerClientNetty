package me.cleavest.client;

import me.cleavest.both.packet.Packet;

import javax.swing.*;

/**
 * @author Cleavest on 18/5/2024
 */
public class ClientApp {

    public static ClientPanel clientPanel;
    private static String name = "NULL";

    public static void main(String[] args) {
        Network network = new Network();
        clientPanel = new ClientPanel(network);
        JFrame frame = new JFrame();
        frame.add(clientPanel);
        frame.pack();
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static ClientPanel getClientPanel() {
        return clientPanel;
    }

    public static JTextArea getMessageArea() {
        return getClientPanel().getMessageArea();
    }

    public static void addMessage(String message){
        getMessageArea().append(message + "\n");
    }

    public static void addMessageFrom(String message, String author){
        if (name.equals(author)) {
            addMessage(author + "(ME): " + message);
            return;
        }

        addMessage(author + ": " + message);

        System.out.println(author + ": " + name);
    }

    public static void setNickName(String nickName) {
        name = nickName;
    }
}
