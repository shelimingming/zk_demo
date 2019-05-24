package com.sheliming.zk.client;

import com.sheliming.zk.constant.Constance;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 异步的方式删除节点
 */
public class ZKClientDeleteASyncDemo implements Watcher {

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
            ZooKeeper zooKeeper = new ZooKeeper(Constance.zkAddress, Constance.sessionTimeout, new ZKClientDeleteASyncDemo());
            System.out.println("state:" + zooKeeper.getState());
            //挂起线程，等待连接成功，当状态改变的时候会调用process方法，
            // process中会调用countDown(),使线程放行。
            connectedSemaphore.await();
            System.out.println("state:" + zooKeeper.getState());

            String path = "/ZKClientDeleteASyncDemo";

            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path);
            zooKeeper.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path + "/c1");

            zooKeeper.delete(path, -1, new IVoidCallback(), null);
            zooKeeper.delete(path + "/c1", -1, new IVoidCallback(), null);
            zooKeeper.delete(path, -1, new IVoidCallback(), null);

            Thread.sleep(Integer.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}

class IVoidCallback implements AsyncCallback.VoidCallback {
    @Override
    public void processResult(int rc, String path, Object ctx) {
        System.out.println(rc + ", " + path + ", " + ctx);
    }
}
