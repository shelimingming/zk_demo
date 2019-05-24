package com.sheliming.zk.client;

import com.sheliming.zk.constant.Constance;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 同步的方式创建节点
 */
public class ZKClientCreateSyncDemo implements Watcher {

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
            ZooKeeper zooKeeper = new ZooKeeper(Constance.zkAddress, Constance.sessionTimeout, new ZKClientCreateSyncDemo());
            System.out.println("state:" + zooKeeper.getState());
            //挂起线程，等待连接成功，当状态改变的时候会调用process方法，
            // process中会调用countDown(),使线程放行。
            connectedSemaphore.await();
            System.out.println("state:" + zooKeeper.getState());

            String path1 = zooKeeper.create("/ZKClientCreateSyncDemo", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("Success create znode: " + path1);

            String path2 = zooKeeper.create("/ZKClientCreateSyncDemo", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("Success create znode: " + path2);

            zooKeeper.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
