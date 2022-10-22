package SynchronizeFolders.Service.Tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import SynchronizeFolders.Service.ServiceSynchronize;

public class ServiceSynchronizeTests {
	private String pathForTests = "T:" + File.separator + "yyyyyyyServiceSynchronizeTest" + File.separator;
	private String sourcePath = pathForTests + "CopyFrom" + File.separator;
	private String syncToPath = pathForTests + "CopyTo" + File.separator;
	
	private String nameSubDirectory = "CopyDirectory";
	private String nameFileInSubDirectory = "NeedCopy.txt";

	private String pathSubDirectoryForCopy = sourcePath + nameSubDirectory + File.separator;
	private String pathSubDirectoryAfterCopy = syncToPath + nameSubDirectory + File.separator;

	private String pathFileForCopy = pathSubDirectoryForCopy + nameFileInSubDirectory;
	private String pathFileAfterCopy = pathSubDirectoryAfterCopy + nameFileInSubDirectory;

	private void createTestEnvironment() throws IOException {
		deleteTestEnvironment();

		try {
			Files.createDirectory(Path.of(pathForTests));
			Files.createDirectory(Path.of(sourcePath));
			Files.createDirectory(Path.of(syncToPath));
			Files.createDirectory(Path.of(pathSubDirectoryForCopy));
		} catch (NoSuchFileException e) {
			throw new NoSuchFileException("Need change pathForTests in ServiceSynchronizeTest");
		} catch (FileAlreadyExistsException e) {}
	}

	private void deleteTestEnvironment() {
		// delete files in directories
		new File(pathFileForCopy).delete();
		new File(pathFileAfterCopy).delete();

		// delete directories
		new File(pathSubDirectoryForCopy).delete();
		new File(pathSubDirectoryAfterCopy).delete();

		// delete base directories
		new File(sourcePath).delete();
		new File(syncToPath).delete();
		new File(pathForTests).delete();
	}

	private void createFileInTestEnvironment(String path) throws IOException {
		FileWriter fileWriter = new FileWriter(path);
		fileWriter.write("txt for testing");
		fileWriter.close();
	}

	@Test
	public void testGetterSetter() {
		float timeSleep = 0.5f;

		ServiceSynchronize serviceSynch = new ServiceSynchronize();
		
		serviceSynch.setSourcePath(sourcePath);
		serviceSynch.setSyncToPath(syncToPath);
		serviceSynch.setTimeSleep(timeSleep);

		Assert.assertEquals(sourcePath, serviceSynch.getSourcePath());
		Assert.assertEquals(syncToPath, serviceSynch.getSyncToPath());
		Assert.assertEquals(timeSleep, serviceSynch.getTimeSleep(), 5);
	}


	@Test
	public void testGetDifferentFiles() throws IOException, InterruptedException {
		// create folders for test, create txt file for copy
		createTestEnvironment();
		createFileInTestEnvironment(pathFileForCopy);

		// start service
		ServiceSynchronize serviceSynch = new ServiceSynchronize(sourcePath, syncToPath);

		File [] filesSource = new File(sourcePath).listFiles();
		ArrayList<File> list = serviceSynch.getDifferentFiles(filesSource, syncToPath, "");

		// in list should be directory and file
		Assert.assertEquals(2, list.size());

		// first dir, then file
		Assert.assertTrue(list.get(0).isDirectory());
		Assert.assertTrue(list.get(1).isFile());

		// check dir and file names
		Assert.assertEquals(nameSubDirectory, list.get(0).getName());
		Assert.assertEquals(nameFileInSubDirectory, list.get(1).getName());

		// delete test environment
		deleteTestEnvironment();
	}


	@Test
	public void testSynchronizeWork() throws IOException {
		// create folders for test, create txt file for copy
		createTestEnvironment();
		createFileInTestEnvironment(pathFileForCopy);

		// start service
		ServiceSynchronize serviceSynch = new ServiceSynchronize(sourcePath, syncToPath);

		// all files and directories should have been copied
		Assert.assertTrue(serviceSynch.handle());

		// check if dir is copied
		File copiedDir = new File(pathSubDirectoryAfterCopy);
		Assert.assertTrue(copiedDir.exists());

		// check if file is copied
		File copiedFile = new File(pathFileAfterCopy);
		Assert.assertTrue(copiedFile.exists());

		// delete source file and check if copied file is deleted
		File sourceFile = new File(pathFileForCopy);
		Assert.assertTrue(sourceFile.delete());
		Assert.assertTrue(serviceSynch.handle());
		Assert.assertFalse(copiedFile.exists());

		// delete source dir and check if copied dir is deleted
		File sourceDir = new File(pathSubDirectoryForCopy);
		Assert.assertTrue(sourceDir.delete());
		Assert.assertTrue(serviceSynch.handle());
		Assert.assertFalse(copiedDir.exists());

		// recreate folders for test, recreate txt file for copy
		createTestEnvironment();
		createFileInTestEnvironment(pathFileForCopy);

		// delete source file and dir, check if copy folder with file is deleted
		Assert.assertTrue(sourceFile.delete());
		Assert.assertTrue(sourceDir.delete());
		Assert.assertTrue(serviceSynch.handle());
		Assert.assertFalse(copiedFile.exists());
		Assert.assertFalse(copiedDir.exists());

		// delete test environment
		deleteTestEnvironment();
	}
}
