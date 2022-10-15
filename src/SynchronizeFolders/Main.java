package SynchronizeFolders;

import SynchronizeFolders.Service.Service;

public class Main {
	public static void main(String[] args) throws Exception {

		Service service = new Service();
		service.start();

	}
}
