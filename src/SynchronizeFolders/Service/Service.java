package SynchronizeFolders.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import SynchronizeFolders.Repo;
import SynchronizeFolders.Exceptions.FolderNotFoundException;

public class Service {
	public void start() throws Exception {
		if (!Repo.loadConfig()) {
			firstStart();
		}

		Repo.checkParams();

		ServiceLogging.log("Программа запущена");

		ServiceUpdate serviceUpdate = new ServiceUpdate();
		serviceUpdate.work();
	}
	private void firstStart() throws Exception {
		String sourceFolder;
		String syncFolder;
		int timeSleep;

		Scanner scan = new Scanner(System.in);

		System.out.print("Первый запуск\nВведите путь к источнику синхронизации:");
		sourceFolder = formatPath(scan.nextLine());
		Service.checkFolder(sourceFolder);

		System.out.print("Введите путь к конечной папке:");
		syncFolder = scan.nextLine().replace("\n", "");
		syncFolder = formatPath(syncFolder);

		String sourceFolderName = new File(sourceFolder).getName();
		// String syncFolderName = new File(syncFolder).getName();
		Service.checkFolder(syncFolder.replace(sourceFolderName, ""));
	
		Path of = Path.of(syncFolder);
		if (!Files.exists(of)) {
			Files.createDirectory(of);
		}

		if (sourceFolder.equalsIgnoreCase(syncFolder)) {
			scan.close();
			throw new Exception("Исходная папка не может быть папкой для синхронизации");
		}

		System.out.print("Введите время между синхронизацией(1 - 3600 секунд):");
		timeSleep = scan.nextInt();

		scan.close();

		Repo.setParam("sourceFolder", sourceFolder);
		Repo.setParam("syncFolder", syncFolder);
		Repo.setParam("timeSleep", String.valueOf(timeSleep));

		Repo.saveConfig();
	}
	public static void checkFolder(String str) throws FolderNotFoundException {
		if (!Files.exists(Path.of(str))) {
			throw new FolderNotFoundException("Папка \"" + str + "\" не существует");
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
