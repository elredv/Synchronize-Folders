package SynchronizeFolders.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class ServiceSynchronize {
	private String sourcePath;
	private String syncToPath;
	private float timeSleep = 0;
	private HashMap<File, String> pathDifferentFiles = new HashMap<File, String>();

	public ServiceSynchronize() {}
	public ServiceSynchronize(String sourcePath, String syncToPath) {
		this.sourcePath = sourcePath;
		this.syncToPath = syncToPath;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String str) {
		sourcePath = str;
	}

	public String getSyncToPath() {
		return syncToPath;
	}
	public void setSyncToPath(String str) {
		syncToPath = str;
	}

	public float getTimeSleep() {
		return timeSleep;
	}
	public void setTimeSleep(float timeSleep) {
		this.timeSleep = timeSleep;
	}

	public boolean handle() {
		try {
			File [] filesSource = new File(getSourcePath()).listFiles();
			File [] filesSynced = new File(getSyncToPath()).listFiles();

			if (filesSource == null || filesSynced == null) {
				System.err.println("Папка-" + (filesSource == null ? "источник" : "наследник") + " недоступна/не существует");
			} else {
				ArrayList<File> differentFiles = getDifferentFiles(filesSynced, getSourcePath(), "");
				deleteOldFiles(differentFiles);

				differentFiles = getDifferentFiles(filesSource, getSyncToPath(), "");
				copyNewFiles(differentFiles);
			}
		} catch (InterruptedException | IOException  err) {
			err.printStackTrace();
			return false;
		}
		return true;
	}

	public void run() throws InterruptedException {
		while (true) {
			if (handle()) {
				Thread.sleep((int) getTimeSleep() * 1000);
			} else {
				Thread.sleep(7000);
				break;
			}
		}
	}

	private void deleteOldFiles(ArrayList<File> differentFiles) throws IOException, InterruptedException {
		// first folders, then files, to delete the files first
		Collections.reverse(differentFiles);

		for (File file : differentFiles) {
			boolean deleted = file.delete();
			ServiceLogging.log((deleted ? "Удалено" : "Не удалось удалить") + " устаревшее: \"" + file.getPath() + "\"");
		}
	}
	private void copyNewFiles(ArrayList<File> differentFiles) throws IOException, InterruptedException {
		for (File file : differentFiles) {
			String copyTo = getSyncToPath() + pathDifferentFiles.get(file) + file.getName();

			if (file.isDirectory()) {
				Files.createDirectory(Path.of(copyTo));
			} else {
				Files.copy(Path.of(file.getPath()), Path.of(copyTo));
			}

			ServiceLogging.log("Синхронизировано: \"" + copyTo + "\"");
		}
	}

	public ArrayList<File> getDifferentFiles(File [] listFiles, String comparePath, String currentPath) throws IOException, InterruptedException {
		ArrayList<File> differentFiles = new ArrayList<>();
		String checkPath = comparePath + currentPath;

		for (File file : listFiles) {
			if (file.isDirectory()) {
				String directoryName = file.getName();
				if (!Files.exists(Path.of(checkPath + directoryName))) {
					pathDifferentFiles.put(file, currentPath);
					differentFiles.add(file);
				}

				File [] filesInDir = file.listFiles();
				if (filesInDir.length > 0) {
					String newCurrentPath = currentPath + directoryName + File.separator;

					ArrayList<File> nextDifferentFiles = getDifferentFiles(filesInDir, comparePath, newCurrentPath);
					differentFiles.addAll(nextDifferentFiles);
				}
			} else {
				File compareFile = new File(checkPath + file.getName());
				if (! (compareFile.exists() && file.length() == compareFile.length())) {
					pathDifferentFiles.put(file, currentPath);
					differentFiles.add(file);
				}
			}
		}

		return differentFiles;
	}
}
