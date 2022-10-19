package SynchronizeFolders.Repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import SynchronizeFolders.Service.Service;
import SynchronizeFolders.Service.ServiceLogging;

public class Repo {
	private Repo() {}

	static File file = new File("");
	private static final String configPath = file.getAbsolutePath() + File.separator + "ConfigMySynchronize.cfg";
	private static final HashMap<String, String> map = new HashMap<String, String>();

	public static String getParam(String name) {
		return map.get(name);
	}

	public static void setParam(String name, String param) {
		map.put(name, param);
	}

	private static int clampParam(String key, int i) {
		if (key.equals("timeSleepInt")) {
			return Math.max(Math.min(i, 3600), 1);
		}
		return i;
	}

	public static void checkParams() throws FileNotFoundException {
		for (Map.Entry<String, String> name : map.entrySet()) {
			String key = name.getKey();
			String value = name.getValue();

			if (key.endsWith("Path")) {
				Service.checkFolderExists(value);
			} else if (key.endsWith("Int")) {
				int val = Integer.parseInt(value);
				val = clampParam(key, val);

				Repo.setParam(key, String.valueOf(val));
			}
		}
	}

	public static boolean loadConfigFile() throws FileNotFoundException {
		File file = new File(configPath);
		if (!file.exists() || file.length() < 5) {
			return false;
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String [] lines = line.split("=");
			if (lines.length < 2) {
				map.clear();
				scan.close();
				return false;
			}
			Repo.setParam(lines[0], lines[1]);
		}

		scan.close();

		checkParams();

		ServiceLogging.log("Конфиг загружен, путь: " + configPath);

		return true;
	}

	public static void saveConfigFile() throws IOException {
		FileWriter fileWriter = new FileWriter(configPath);
		for (Map.Entry<String, String> name : map.entrySet()) {
			fileWriter.append(name.getKey() + "=" + name.getValue() + "\n");
		}
		fileWriter.close();

		ServiceLogging.log("Конфиг сохранен, путь: " + configPath);
	}
}
