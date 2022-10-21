package SynchronizeFolders.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import SynchronizeFolders.Repo.Repo;
import SynchronizeFolders.Repo.RepoObject;

public class Service {
	Repo repo;

	public void start() throws Exception {
		repo = new Repo();

		System.out.println("!!!!!ВНИМАНИЕ!!!!! В папке, в которую синхронизируются данные, все \"лишние\" данные будут удалены.");

		if (!repo.loadConfigFile()) {
			firstStart();
		}

		ServiceSynchronize serviceUpdate = new ServiceSynchronize();
		serviceUpdate.setSourcePath((String) repo.getParam("sourcePath").getValue());
		serviceUpdate.setSyncToPath((String) repo.getParam("syncToPath").getValue());
		serviceUpdate.setTimeSleep((Float) repo.getParam("timeSleepInt").getValue());
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
		int timeSleep = scan.nextInt();

		scan.close();

		RepoObject<String> sourcePathObject = new RepoObject<>(sourcePath);
		RepoObject<String> syncToPathObject = new RepoObject<>(syncToPath);
		RepoObject<Float> timeSleepObject = new RepoObject<>(Float.valueOf(timeSleep));
		repo.setParam("sourcePath", sourcePathObject);
		repo.setParam("syncToPath", syncToPathObject);
		repo.setParam("timeSleepInt", timeSleepObject);

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
