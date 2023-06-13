package com.websocketdemo.game.server;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.websocketdemo.game.model.EntityDataPacket;
import com.websocketdemo.game.model.EntityDataPacketList;
import com.websocketdemo.game.model.EntityFactory;
import com.websocketdemo.game.model.EntityFactoryID;
import com.websocketdemo.game.model.EntityPacketQueue;
import com.websocketdemo.game.model.GameWrapperSettings;
import com.websocketdemo.game.model.IEntity;
import com.websocketdemo.game.model.PacketQueueReceiveHandler;
import com.websocketdemo.game.model.ThreadSafeQueue;
import com.websocketdemo.game.model.VersionDataPacket;
import com.websocketdemo.game.model.WaveDataPacket;
import com.websocketdemo.game.model.WaveStates;
import com.websocketdemo.game.model.WorldWrapper;

public class ServerWrapper implements Runnable {

    public WorldWrapper m_WorldWrapper;
    float physicsTimeLeft;
    long aika;
    int times;
    int m_StepCount;
    SocketQueueWrapper m_ReplySockets;
    PacketQueueReceiveHandler m_PacketHandler;
    ManualSerializer m_Serializer;
    EntityFactory m_Factory;
    Random rnum;
    WaveDataPacket m_waveStates;
    int m_waveZombieCount;


    public ServerWrapper(EntityPacketQueue pQueue, SocketQueueWrapper replySockets) {
        m_StepCount = 0;
        m_WorldWrapper = new WorldWrapper(true);
        m_PacketHandler = new PacketQueueReceiveHandler(pQueue, m_WorldWrapper.getEntities());
        m_ReplySockets = replySockets;
        m_Factory = new EntityFactory(m_WorldWrapper.getWorld(), m_WorldWrapper.getEntities());
        m_waveStates = new WaveDataPacket(0, 0);
        rnum = new Random();
        m_waveZombieCount = 0;

    }

    public void updateWaveState(){
        for(WebSocketWrapper i : m_ReplySockets.m_SocketQueue){
            i.AddToQueue(m_waveStates);
            i.AddToQueue(new VersionDataPacket());
        }
    }

    private void ProcessWaveStep() {
        if (m_ReplySockets.size() > 0) {
            if (m_waveStates.m_waveState == WaveStates.Beginning && m_waveZombieCount == 0) {

                if (m_ReplySockets.size() > 0) {
                    m_waveStates.m_waveNumber++;
                    m_waveStates.m_waveState=WaveStates.Active;
                    updateWaveState();
                    m_waveZombieCount = 3 * m_waveStates.m_waveNumber;
                }
            } else if (m_waveStates.m_waveState == WaveStates.Active) {
                int switcheroo =1;
                while (m_waveZombieCount > 0) {
                    try {
                        m_WorldWrapper.addEntity(m_Factory.CreateEntity(EntityFactoryID.Zombie, (float) (21*switcheroo), (float) ((rnum.nextFloat()) * 21), (int) (rnum.nextFloat() * 1000000), true));
                        switcheroo*=-1;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    m_waveZombieCount--;
                }
                if (m_waveZombieCount == 0 && m_WorldWrapper.getEntities().getLocalEntities(true).size() == 0) {
                    m_waveStates.m_waveState = WaveStates.Break;

                }

            } else if (m_waveStates.m_waveState == WaveStates.Break) {
                for (IEntity entity : m_WorldWrapper.getEntities().GetEntityList()) {
                    if (entity.GetID() == EntityFactoryID.Soldier || entity.GetID() == EntityFactoryID.UserSoldier || entity.GetID() == EntityFactoryID.LocalSoldier) {
                        entity.setHealth(100);
                    }
                }
                m_waveStates.m_waveState = WaveStates.Active;
                m_waveStates.m_waveNumber++;
                m_waveZombieCount = 3 * m_waveStates.m_waveNumber;
                updateWaveState();
            }

        }
        else
            {
                for (IEntity entity : m_WorldWrapper.getEntities().GetEntityList()) {
                    entity.setHealth(-1);
                    m_waveStates.m_waveState=WaveStates.Beginning;
                    m_waveStates.m_waveNumber=0;
                    m_waveZombieCount=0;
                }
        }
    }


    private boolean fixedStep(float delta) {


        physicsTimeLeft += delta;
        if (physicsTimeLeft > GameWrapperSettings.MAX_TIME_PER_FRAME)
            physicsTimeLeft = GameWrapperSettings.MAX_TIME_PER_FRAME;

        boolean stepped = false;
        while (physicsTimeLeft >= GameWrapperSettings.TIME_STEP) {

            EntityDataPacketList list = new EntityDataPacketList();
            EntityDataPacketList listWorldWrapper;
            boolean newBullets=m_PacketHandler.handleQueue(true);
            if( newBullets){
                for (EntityDataPacket i : m_WorldWrapper.getSerializedWorld().getPacketQueue()) {
                    if(i.m_TypeID==EntityFactoryID.Bullet) {
                        list.getPacketQueue().addLast(i);
                    }
                }
            }
            listWorldWrapper= m_WorldWrapper.WorldStep(delta, physicsTimeLeft);
            for(EntityDataPacket i : listWorldWrapper.getPacketQueue()){
                list.getPacketQueue().addLast(i);
            }

            if (list.getPacketQueue().size > 0 ) {
                m_StepCount = 0;
               for (EntityDataPacket i : m_WorldWrapper.getSerializedWorld().getPacketQueue()) {
                    list.getPacketQueue().addLast(i);
                }
                for (int i = 0; i < m_ReplySockets.size(); i++) {
                    try {
                        m_ReplySockets.get(i).AddToQueue(list);
                    } catch (Exception e) {

                    }

                }
            }
            physicsTimeLeft -= GameWrapperSettings.TIME_STEP;
            stepped = true;

        }
        if (stepped) {
            ProcessWaveStep();
            /**THIS IS A DIRTY HACK**/
            if (m_StepCount > 10) {
                for (int i = 0; i < m_ReplySockets.size(); i++) {
                    if (m_ReplySockets.get(i).m_isRunning) {
                        if (m_ReplySockets.get(i).m_Queue.size < 15) {
                            try {
                                m_ReplySockets.get(i).AddToQueue(m_WorldWrapper.getSerializedWorld());
                            } catch (Exception e) {

                            }
                        }
                    }

                }
                m_StepCount = 0;
            } else {
                m_StepCount++;
            }
        }
        return stepped;
    }

    @Override
    public void run() {
        System.out.println("Started Main Server Game Thread");

        long begin = System.currentTimeMillis();

        float delta = (float) ((float) (System.currentTimeMillis() - begin) / 1000);
        while (true) {
            begin = System.currentTimeMillis();
            fixedStep(delta);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            delta = (float) ((float) (System.currentTimeMillis() - begin)) / 1000;

        }
    }

}
