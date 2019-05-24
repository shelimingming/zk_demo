package com.sheliming.zk.client;

import com.sheliming.zk.constant.Constance;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 异步的方式创建节点
 */
public class ZKClientCreateASyncDemo implements Watcher {

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
            ZooKeeper zooKeeper = new ZooKeeper(Constance.zkAddress, Constance.sessionTimeout, new ZKClientCreateASyncDemo());
            System.out.println("state:" + zooKeeper.getState());
            //挂起线程，等待连接成功，当状态改变的时候会调用process方法，
            // process中会调用countDown(),使线程放行。
            connectedSemaphore.await();
            System.out.println("state:" + zooKeeper.getState());

            zooKeeper.create("/ZKClientCreateASyncDemo", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                    new IStringCallback(), "I am context. ");

            zooKeeper.create("/ZKClientCreateASyncDemo", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                    new IStringCallback(), "I am context. ");

            zooKeeper.create("/ZKClientCreateASyncDemo", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                    new IStringCallback(), "I am context. ");
            Thread.sleep(Integer.MAX_VALUE);

            zooKeeper.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class IStringCallback implements AsyncCallback.StringCallback {
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("Create path result: [" + rc + ", " + path + ", " + ctx + ", real path name: " + name);
    }
}
