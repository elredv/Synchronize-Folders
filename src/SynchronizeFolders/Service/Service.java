package SynchronizeFolders.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import SynchronizeFolders.Repo.Repo;

public class Service {
	public void start() throws Exception {
		System.out.println("!!!!!ВНИМАНИЕ!!!!! В папке, в которую синхронизируются данные, все \"лишние\" данные будут удалены.");

		if (!Repo.loadConfigFile()) {
			firstStart();
		}

		ServiceUpdate serviceUpdate = new ServiceUpdate();
		serviceUpdate.setSourcePath(Repo.getParam("sourcePath"));
		serviceUpdate.setSyncToPath(Repo.getParam("syncToPath"));
		serviceUpdate.setTimeSleep(Float.parseFloat(Repo.getParam("timeSleepInt")));
		serviceUpdate.run();

		ServiceLogging.log("Программа запущена");
	}
	private void firstStart() throws Exception {
		Scanner scan = new Scanner(System.in);

		System.out.print("Первый запуск\nВведите путь к источнику синхронизации:");
		String sourcePath = formatPath(scan.nextLine());
		Service.checkFolderExists(sourcePath);

		System.out.print("Введите путь к конечной папке:");
		String syncToPath = scan.nextLine().replace("\n", "");
		syncToPath = formatPath(syncToPath);

		String sourceFolderName = new File(sourcePath).getName();
		if (syncToPath.endsWith(sourceFolderName)) {
			syncToPath.substring(0, syncToPath.length() - sourceFolderName.length());
		}
		Service.checkFolderExists(syncToPath);

		if (sourcePath.equalsIgnoreCase(syncToPath)) {
			scan.close();
			throw new Exception("Папка-источник должна отличаться от папки для синхронизации");
		}

		System.out.print("Введите время между синхронизацией(1 - 3600 секунд):");
		int timeSleep = scan.nextInt();

		scan.close();

		Repo.setParam("sourcePath", sourcePath);
		Repo.setParam("syncToPath", syncToPath);
		Repo.setParam("timeSleepInt", String.valueOf(timeSleep));

		Repo.saveConfigFile();
	}
	public static void checkFolderExists(String str) throws FileNotFoundException {
		if (!Files.exists(Path.of(str))) {
			throw new FileNotFoundException("Папка \"" + str + "\" не существует");
		}
	}
	public static String formatPath(String str) {
		str = str.replace("\"", "");
		str = str.replace("//", "/");
		if (!str.endsWith(File.separator)) {
			str += File.separator;
		}
		return str;
	}
}
