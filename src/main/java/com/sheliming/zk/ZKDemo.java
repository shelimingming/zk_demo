package com.sheliming.zk;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZKDemo {
    public static void main(String[] args) {
        try {
            ZooKeeper zk = new ZooKeeper("127.0.0.1:2181",30000,null,false);
            Thread.sleep(3000);
            ZooKeeper.States state = zk.getState();
            System.out.println(state);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
