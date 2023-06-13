package com.websocketdemo.game.model;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;

public class DataPackets {
    private DataPackets() {
    }

    public static void register(final ManualSerializer serializer) {
        // Note that the packets use simple, primitive data, but nothing stops you from using more complex types like
        // strings, arrays or even other transferables. Both Serializer and Deserializer APIs are well documented: make
        // sure to check them out.
        serializer.register(new EntityDataPacketList());
        serializer.register(new SyncClientTimeDataPacket());
        serializer.register(new EntityDataPacket());
        serializer.register(new WaveDataPacket());
        serializer.register(new UsernameUUIDPair());
        serializer.register(new UsernamesList());
        serializer.register(new VersionDataPacket());

    }
}