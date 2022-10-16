package SynchronizeFolders.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import SynchronizeFolders.Repo;


public class ServiceUpdate {
	private String sourcePath = "";
	private String syncToPath = "";

	public String getSourcePath() {
		return sourcePath;
	}
	public String getSyncToPath() {
		return syncToPath;
	}
	public void setSourcePath(String str) {
		sourcePath = str;
	}
	public void setSyncToPath(String str) {
		syncToPath = str;
	}

	public void work() {
		setSourcePath(Repo.getParam("sourcePath"));
		setSyncToPath(Repo.getParam("syncToPath"));

		int timeSleep = Integer.parseInt(Repo.getParam("timeSleep")) * 1000;

		while (true) {
			try {
				File[] filesSource = new File(getSourcePath()).listFiles();
				File[] filesSynced = new File(getSyncToPath()).listFiles();

				if (filesSource == null) {
					System.err.println("Папка-источник недоступна/не существует");
					Thread.sleep(7000);
				} else if (filesSynced == null) {
					System.err.println("Папка-наследник недоступна/не существует");
					Thread.sleep(7000);
				} else {
					deleteOldFiles(filesSynced, "");
					baseHandler(filesSource, "");
				}

				Thread.sleep(timeSleep);
			} catch (InterruptedException | IOException  err) {
				err.printStackTrace();
				break;
			}
		}
	}

	private void baseHandler(File [] filesList, String currentPath) throws IOException {
		ArrayList<File> toSync = new ArrayList<>();
		String syncToPath = getSyncToPath();

		syncToPath += currentPath;

		for (File file : filesList) {
			if (! file.canRead()) {
				System.out.println("Невозможно прочитать файл " + file.getPath() + " | " + file.getName());
				continue;
			}

			if (file.isDirectory()) {
				String directoryName = file.getName();
				String newDir = syncToPath + directoryName + File.separator;
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

				File [] filesInDir = file.listFiles();
				if (filesInDir != null && filesInDir.length > 0) {
					baseHandler(filesInDir, currentPath + directoryName + File.separator);
				}
			} else {
				File fileSync = new File(syncToPath + file.getName());
				if (!fileSync.exists()) {
					toSync.add(file);
				}
			}
		}

		if (toSync.size() > 0) {
			try {
				copyFiles(toSync, syncToPath);
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
	}
	private void deleteOldFiles(File [] filesSynced, String currentPath) throws IOException, InterruptedException {
		String sourcePath = getSourcePath();
		sourcePath += currentPath;

		for (File file : filesSynced) {
			BasicFileAttributes basicFA = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			if (basicFA.isDirectory()) {
				String directoryName = file.getName();
				String pathSourceDir = sourcePath + directoryName;

				File [] filesInDir = file.listFiles();
				if (filesInDir != null && filesInDir.length > 0) {
					deleteOldFiles(filesInDir, currentPath + directoryName + File.separator);
				}

				if (!Files.exists(Path.of(pathSourceDir))) {
					tryDelete(file);
				}
			} else {
				File fileSource = new File(sourcePath + file.getName());
				if (! (fileSource.exists() && basicFA.size() == fileSource.length())) {
					tryDelete(file);
				}
			}
		}
	}
	private void copyFiles(ArrayList<File> filesArray, String path) throws IOException {
		for (File file : filesArray) {
			String pathSourceFile = file.getPath();
			String pathSyncFile = path + file.getName();
			Files.copy(Path.of(pathSourceFile), Path.of(pathSyncFile));

			ServiceLogging.log("Синхронизировано: \"" + pathSyncFile + "\"");
		}
	}
	private void tryDelete(File file) {
		if (file.delete()) {
			ServiceLogging.log("Удалено устаревшее: \"" + file.getPath() + "\"");
		} else {
			ServiceLogging.log("Не удалось удалить устаревшее: \"" + file.getPath() + "\"");
		}
	}
}
