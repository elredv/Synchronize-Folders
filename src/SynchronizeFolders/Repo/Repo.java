package SynchronizeFolders.Repo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import SynchronizeFolders.Service.ServiceLogging;

public class Repo {
	private final String absolutePath = new File("").getAbsolutePath();
	private String configName = "ConfigMySynchronize.bin";
	private String configPath = absolutePath + File.separator + configName;

	private HashMap<String, RepoObject<?>> hashMap = new HashMap<String, RepoObject<?>>();

	public void setConfigName(String name) {
		configName = name;
		configPath = absolutePath + File.separator + configName;
	}

	public String getConfigPath() {
		return configPath;
	}

	public RepoObject<?> getParam(String name) {
		return hashMap.get(name);
	}

	public void setParam(String name, RepoObject<?> param) {
		hashMap.put(name, param);
	}

	public boolean loadConfigFile() {
		File config = new File(configPath);
		if (!config.exists()) {
			return false;
		}

		try {
			FileInputStream fis = new FileInputStream(config);
			ObjectInput ois = new ObjectInputStream(fis);
	 
			@SuppressWarnings("unchecked")
			HashMap<String, RepoObject<?>> loadedMap = (HashMap<String, RepoObject<?>>) ois.readObject();
			hashMap = loadedMap;
	
			fis.close();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		ServiceLogging.log("Конфиг загружен, путь: " + configPath);

		return true;
	}

	public boolean saveConfigFile() {
		try {
			FileOutputStream fis = new FileOutputStream(configPath);
			ObjectOutputStream ois = new ObjectOutputStream(fis);
			ois.writeObject(hashMap);
			fis.close();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		ServiceLogging.log("Конфиг сохранен, путь: " + configPath);
		return true;
	}
}
