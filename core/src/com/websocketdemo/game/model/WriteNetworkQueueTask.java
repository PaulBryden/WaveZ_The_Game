package com.websocketdemo.game.model;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class WriteNetworkQueueTask extends Timer.Task {

    ThreadSafeQueue m_Queue;
    ThreadSafeSocket m_socket;

    public WriteNetworkQueueTask(ThreadSafeQueue queue, ThreadSafeSocket socket){
        m_Queue=queue;
        m_socket=socket;
    }

    @Override
    public void run() {
        WriteNetworkQueue();
    }

    private void WriteNetworkQueue(){
        while (m_Queue.size > 0) {
            m_socket.send(m_Queue.removeFirst());

        }
    }
}
