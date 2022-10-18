package SynchronizeFolders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import SynchronizeFolders.Exceptions.FolderNotFoundException;
import SynchronizeFolders.Service.Service;
import SynchronizeFolders.Service.ServiceLogging;

public class Repo {
	private Repo(){}

	static File file = new File("");
	private static final String configPath = file.getAbsolutePath() + File.separator + "ConfigMySynchronize.cfg";
	private static final HashMap<String, String> map = new HashMap<String, String>();

	public static String getParam(String name) {
		return map.get(name);
	}
	public static void setParam(String name,  String param) {
		map.put(name, param);
	}
	private static int limitTimeSleep(int i) {
		return Math.max(Math.min(i, 3600), 1);
	}
	public static void checkParams() throws FolderNotFoundException {
		for (Map.Entry<String, String> name : map.entrySet()) {
			 if (name.getKey().endsWith("Folder")) {
				 Service.checkFolderExists(name.getValue());
			 } else if (name.getKey().endsWith("Enable")) {
				 if (!(name.getValue().equals("true") || name.getValue().equals("false"))) {
					 Repo.setParam(name.getKey(), "false");
				 }
			 } else if (name.getKey().equals("timeSleep")) {
				int val = Integer.parseInt(name.getValue());
				val = limitTimeSleep(val);

				Repo.setParam(name.getKey(), String.valueOf(val));
			}
		}
	}
	public static boolean loadConfig() throws FileNotFoundException {
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

		ServiceLogging.log("Конфиг загружен, путь: " + configPath);

		return true;
	}
	public static void saveConfig() throws IOException {
		FileWriter fileWriter = new FileWriter(configPath);
		for (Map.Entry<String, String> name : map.entrySet()) {
			fileWriter.append(name.getKey() + "=" + name.getValue() + "\n");
		}
		fileWriter.close();

		ServiceLogging.log("Конфиг сохранен, путь: " + configPath);
	}
}
