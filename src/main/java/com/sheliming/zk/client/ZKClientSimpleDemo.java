package com.sheliming.zk.client;

import com.sheliming.zk.constant.Constance;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKClientSimpleDemo implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent event) {
        System.out.println("event:" + event);
        if (event.getState() == Event.KeeperState.SyncConnected) {
            connectedSemaphore.countDown();
        }
    }

    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(Constance.zkAddress, Constance.sessionTimeout, new ZKClientSimpleDemo());
            System.out.println("state:" + zooKeeper.getState());

            //挂起线程，等待连接成功，当状态改变的时候会调用process方法，
            // process中会调用countDown(),使线程放行。
            connectedSemaphore.await();

            System.out.println("state:" + zooKeeper.getState());

            zooKeeper.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
