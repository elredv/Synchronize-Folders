package SynchronizeFolders.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import SynchronizeFolders.Repo.Repo;

public class Service {
	Repo repo;

	public void start() throws Exception {
		repo = new Repo();

		System.out.println("!!!!!ВНИМАНИЕ!!!!! В папке, в которую синхронизируются данные, все \"лишние\" данные будут удалены.");

		if (!repo.loadConfigFile()) {
			firstStart();
		}

		ServiceSynchronize serviceUpdate = new ServiceSynchronize();
		serviceUpdate.setSourcePath(repo.getString("sourcePath"));
		serviceUpdate.setSyncToPath(repo.getString("syncToPath"));
		serviceUpdate.setTimeSleep(repo.getFloat("timeSleep"));
		serviceUpdate.run();

		ServiceLogging.log("Программа запущена");
	}
	private void firstStart() throws Exception {
		Scanner scan = new Scanner(System.in);

		System.out.print("Первый запуск\nВведите путь к источнику синхронизации:");
		String sourcePath = formatPath(scan.nextLine());
		checkFolderExists(sourcePath);

		System.out.print("Введите путь к конечной папке:");
		String syncToPath = scan.nextLine().replace("\n", "");
		syncToPath = formatPath(syncToPath);

		String sourceFolderName = new File(sourcePath).getName();
		if (syncToPath.endsWith(sourceFolderName)) {
			syncToPath.substring(0, syncToPath.length() - sourceFolderName.length());
		}
		checkFolderExists(syncToPath);

		if (sourcePath.equalsIgnoreCase(syncToPath)) {
			scan.close();
			throw new Exception("Папка-источник должна отличаться от папки для синхронизации");
		}

		System.out.print("Введите время между синхронизацией(1 - 3600 секунд):");
		float timeSleep = scan.nextFloat();

		scan.close();

		repo.setString("sourcePath", sourcePath);
		repo.setString("syncToPath", syncToPath);
		repo.setFloat("timeSleep", timeSleep);

		repo.saveConfigFile();
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
