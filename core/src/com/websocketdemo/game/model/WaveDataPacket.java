	package com.websocketdemo.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

    public class WaveDataPacket implements Transferable<WaveDataPacket>{
        public int m_waveState;
        public int m_waveNumber;
        /*Should be pulled from the Entity itself*/

        public WaveDataPacket() {
            m_waveState=0;
            m_waveNumber=0;
        }
        public WaveDataPacket(int waveState, int waveNumber){
            m_waveState=waveState;
            m_waveNumber=waveNumber;
        }
        @Override
        public void serialize(Serializer serializer) throws SerializationException {
            serializer.serializeInt(m_waveState).serializeInt(m_waveNumber);


        }
        @Override
        public WaveDataPacket deserialize(Deserializer deserializer) throws SerializationException {

            return new WaveDataPacket(deserializer.deserializeInt(),
                                    deserializer.deserializeInt());
        }
    }
