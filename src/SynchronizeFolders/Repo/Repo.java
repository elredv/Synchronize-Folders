package SynchronizeFolders.Repo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import SynchronizeFolders.Service.ServiceLogging;

public class Repo {
	private Repo() {}

	private static String absolutePath = new File("").getAbsolutePath();
	private static final String configPath = absolutePath + File.separator + "ConfigMySynchronize.cfg";
	private static HashMap<String, RepoObject<?>> hashMap = new HashMap<String, RepoObject<?>>();

	public static RepoObject<?> getParam(String name) {
		return hashMap.get(name);
	}

	public static void setParam(String name, RepoObject<?> param) {
		hashMap.put(name, param);
	}

	public static boolean loadConfigFile() throws IOException, ClassNotFoundException {
		File config = new File(configPath);
		if (!config.exists()) {
			return false;
		}

		FileInputStream fis = new FileInputStream(config);
		ObjectInput ois = new ObjectInputStream(fis);
 
		@SuppressWarnings("unchecked")
		HashMap<String, RepoObject<?>> loadedMap = (HashMap<String, RepoObject<?>>) ois.readObject();
		hashMap = loadedMap;

		fis.close();
		ois.close();

		ServiceLogging.log("Конфиг загружен, путь: " + configPath);

		return true;
	}

	public static void saveConfigFile() throws IOException {
		ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(configPath));
		ois.writeObject(hashMap);
		ois.close();

		ServiceLogging.log("Конфиг сохранен, путь: " + configPath);
	}
}
