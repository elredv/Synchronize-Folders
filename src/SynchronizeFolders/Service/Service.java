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
		String sourcePath;
		String syncToPath;
		int timeSleep;

		Scanner scan = new Scanner(System.in);

		System.out.print("Первый запуск\nВведите путь к источнику синхронизации:");
		sourcePath = formatPath(scan.nextLine());
		Service.checkFolder(sourcePath);

		System.out.print("Введите путь к конечной папке:");
		syncToPath = scan.nextLine().replace("\n", "");
		syncToPath = formatPath(syncToPath);

		String sourceFolderName = new File(sourcePath).getName();
		// String syncFolderName = new File(syncToPath).getName();
		Service.checkFolder(syncToPath.replace(sourceFolderName, ""));
	
		Path of = Path.of(syncToPath);
		if (!Files.exists(of)) {
			Files.createDirectory(of);
		}

		if (sourcePath.equalsIgnoreCase(syncToPath)) {
			scan.close();
			throw new Exception("Папка-источник должна отличаться от папки для синхронизации");
		}

		System.out.print("Введите время между синхронизацией(1 - 3600 секунд):");
		timeSleep = scan.nextInt();

		scan.close();

		Repo.setParam("sourcePath", sourcePath);
		Repo.setParam("syncToPath", syncToPath);
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
