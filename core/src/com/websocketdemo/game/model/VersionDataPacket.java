	package com.websocketdemo.game.model;

import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

    public class VersionDataPacket implements Transferable<VersionDataPacket>
    {
        public String m_Version;
        /*Should be pulled from the Entity itself*/

        public VersionDataPacket()
        {
            m_Version="";
        }
        public VersionDataPacket(String version)
        {
            m_Version = version;
        }
        @Override
        public void serialize(Serializer serializer) throws SerializationException
        {
            serializer.serializeString(Version.VersionNumber);
        }
        @Override
        public VersionDataPacket deserialize(Deserializer deserializer) throws SerializationException
        {
            return new VersionDataPacket(deserializer.deserializeString());
        }
    }
