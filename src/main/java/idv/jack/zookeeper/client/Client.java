package idv.jack.zookeeper.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Client implements Watcher, Closeable {
	private ZooKeeper zk;
	
	public void startZk() throws Exception{
		zk = new ZooKeeper("server-a1", 2181, this);
	}
	
	public void submitTask(String task){
		try{
			zk.create("/test" + task, 
					  "data".getBytes(),
					  Ids.OPEN_ACL_UNSAFE, 
					  CreateMode.PERSISTENT_SEQUENTIAL);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}


	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("recevice event:" + event.getState() + "\n");
		if(KeeperState.SyncConnected == event.getState()){
			System.out.println("Synconnected");
		}else if(KeeperState.Disconnected == event.getState()){
			System.out.println("Disconnected");
		}else if(KeeperState.Expired == event.getState()){
			System.out.println("Expired");
		}
	
	}

	@Override
	public void close() throws IOException {
		try{
			zk.close();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String args[]) throws Exception{
		Client client = new Client();
		client.startZk();
		client.submitTask("task");
		
		client.close();
	}

}
