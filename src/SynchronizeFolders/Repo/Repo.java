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

	private HashMap<String, String> hashMapString = new HashMap<>();
	private HashMap<String, Integer> hashMapInteger = new HashMap<>();
	private HashMap<String, Float> hashMapFloat = new HashMap<>();

	public void setConfigName(String name) {
		configName = name;
		configPath = absolutePath + File.separator + configName;
	}
	
	public Repo() {}

	public Repo(String configName) {
		setConfigName(configName);
	}


	public String getConfigPath() {
		return configPath;
	}

	public String getString(String key) {
		return hashMapString.get(key);
	}
	public Integer getInteger(String key) {
		return hashMapInteger.get(key);
	}
	public Float getFloat(String key) {
		return hashMapFloat.get(key);
	}

	public void setString(String key, String value) {
		hashMapString.put(key, value);
	}
	public void setInteger(String key, Integer value) {
		hashMapInteger.put(key, value);
	}
	public void setFloat(String key, Float value) {
		hashMapFloat.put(key, value);
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
			HashMap<String, String> loadedHashMapString = (HashMap<String, String>) ois.readObject();
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> loadedHashMapInteger = (HashMap<String, Integer>) ois.readObject();
			@SuppressWarnings("unchecked")
			HashMap<String, Float> loadedHashMapFloat = (HashMap<String, Float>) ois.readObject();

			hashMapString = loadedHashMapString;
			hashMapInteger = loadedHashMapInteger;
			hashMapFloat = loadedHashMapFloat;
	
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
			ois.writeObject(hashMapString);
			ois.writeObject(hashMapInteger);
			ois.writeObject(hashMapFloat);
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
