package SynchronizeFolders.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import SynchronizeFolders.Repo;


public class ServiceUpdate {
	private String sourceFolder = "";
	private String syncFolder = "";
	public void work() {
		sourceFolder = Repo.getParam("sourceFolder");
		syncFolder = Repo.getParam("syncFolder");
		int timeSleep = Integer.parseInt(Repo.getParam("timeSleep")) * 1000;

		while (true) {
			try {
				File[] filesSource = new File(sourceFolder).listFiles();
				File[] filesSynced = new File(syncFolder).listFiles();

				if (filesSource != null && filesSynced != null && filesSource.length > 0) {
					deleteOldFiles(filesSynced);
					baseHandler(filesSource);
				} else {
					System.err.println("Какая-то из папок недоступна или исходная папка пуста");
					Thread.sleep(7000);
				}

				Thread.sleep(timeSleep);
			} catch (InterruptedException | IOException  err) {
				err.printStackTrace();
				break;
			}
		}
	}

	private void baseHandler(File [] files) throws IOException {
		ArrayList<File> sync = new ArrayList<>();
		String newPath = "";

		for (File file : files) {
			if (! file.canRead()) {
				System.out.println("Невозможно прочитать файл " + file.getPath() + " | " + file.getName());
				continue;
			}

			if (newPath.equals("")) {
				newPath = file.getPath().replace(sourceFolder, syncFolder);
				newPath = chetoToPath(newPath, file.getName());
			}

			if (file.isDirectory()) {
				String newDir = addNameToPath(newPath, file.getName());
				File dirSync = new File(newDir);

				if (!dirSync.exists()) {
					try {
						Files.createDirectory(Path.of(newDir));
					} catch (IOException err) {
						err.printStackTrace();
						return;
					}
					ServiceLogging.log("Создан каталог: \"" + newDir + "\"");
				}

				File [] nextFiles = file.listFiles();

				if (nextFiles != null && nextFiles.length > 0) {
					baseHandler(nextFiles);
				}
				continue;
			}

			File fileSync = new File(newPath + File.separator + file.getName());
			if (!fileSync.exists()) {
				sync.add(file);
			}
		}

		if (sync.size() > 0) {
			try {
				copyFiles(sync, newPath);
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
	}
	private void deleteOldFiles(File [] filesSynced) throws IOException, InterruptedException {
		String sourcePath = "";

		for (File file : filesSynced) {
			if (sourcePath.equals("")) {
				sourcePath = file.getPath().replace(syncFolder, sourceFolder);
				sourcePath = chetoToPath(sourcePath, file.getName());
			}

			BasicFileAttributes basicFA = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			if (basicFA.isDirectory()) {
				String newDir = addNameToPath(sourcePath, file.getName());

				File [] nextFiles = file.listFiles();
				if (nextFiles != null && nextFiles.length > 0) {
					deleteOldFiles(nextFiles);
				}

				if (!Files.exists(Path.of(newDir))) {
					tryDelete(file);
				}
				continue;
			}

			File fileSource = new File(sourcePath + File.separator + file.getName());
			if (! (fileSource.exists() && basicFA.size() == fileSource.length())) {
				tryDelete(file);
			}
		}
	}
	private void copyFiles(ArrayList<File> files, String path) throws IOException {
		for (File file : files) {
			Files.copy(Path.of(file.getPath()), Path.of(path + File.separator + file.getName()));

			ServiceLogging.log("Синхронизировано: \"" + file.getPath() + "\"");
		}
	}
	private String addNameToPath(String path, String name) {
		if (! path.endsWith(File.separator)) {
			path += File.separator;
		}
		path += name;
		return path;
	}
	private String chetoToPath(String path, String name) {
		if (path.endsWith(name)) {
			path = path.substring(0, path.length() - name.length());
		}
		return path;
	}
	private void tryDelete(File file) {
		if (file.delete()) {
			ServiceLogging.log("Удалено устаревшее: \"" + file.getPath() + "\"");
		} else {
			ServiceLogging.log("Не удалось удалить устаревшее: \"" + file.getPath() + "\"");
		}
	}
}
