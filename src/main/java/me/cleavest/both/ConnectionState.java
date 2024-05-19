package me.cleavest.both;

/**
 * @author Cleavest on 19/5/2024
 */
import me.cleavest.both.packet.*;

import java.util.HashMap;
import java.util.Map;

public class ConnectionState {
    private static Map<Integer, Class<? extends Packet<?>>> client = new HashMap<Integer, Class<? extends Packet<?>>>();
    private static Map<Integer, Class<? extends Packet<?>>> server = new HashMap<Integer, Class<? extends Packet<?>>>();

    public static int getPacketId(Packet<?> packet, State state) {
        if (state == State.client) {
            return getClientPacketId(packet);
        } else {
            return getServerPacketId(packet);
        }
    }

    private static int getClientPacketId(Packet<?> packet) {
        Map<Class<? extends Packet<?>>, Integer> inverse = new HashMap<Class<? extends Packet<?>>, Integer>();
        for (Integer id : client.keySet()) {
            inverse.put(client.get(id), id);
        }
        return inverse.get(packet.getClass());
    }

    private static int getServerPacketId(Packet<?> packet) {
        Map<Class<? extends Packet<?>>, Integer> inverse = new HashMap<Class<? extends Packet<?>>, Integer>();
        for (Integer id : server.keySet()) {
            inverse.put(server.get(id), id);
        }
        return inverse.get(packet.getClass());
    }

    public static Packet<?> getPacket(int i, State state) throws InstantiationException, IllegalAccessException {
        if (state == State.client) {
            return getServerPacket(i);
        }
        if (state == State.server) {
            return getClientPacket(i);
        }
        throw new IllegalAccessException("state cannot be null");
    }

    public static Packet<?> getServerPacket(int i) throws InstantiationException, IllegalAccessException {
        Class<? extends Packet<?>> oclass = server.get(i);
        return oclass == null ? null : (Packet<?>) oclass.newInstance();
    }

    public static Packet<?> getClientPacket(int i) throws InstantiationException, IllegalAccessException {
        Class<? extends Packet<?>> oclass = client.get(i);
        return oclass == null ? null : (Packet<?>) oclass.newInstance();
    }

    static {
        client.put(client.size(), CPacket.class);
        server.put(server.size(), SPacket.class);
        client.put(client.size(), CChangeNamePacket.class);
        server.put(server.size(), SChangeNamePacket.class);
        server.put(server.size(), SSetNamePacket.class);
    }
}
