package SynchronizeFolders.Repo.Tests;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import SynchronizeFolders.Repo.Repo;


public class RepoTests {
	private String configName = "TestWorkRepo.bin";

	// vars for tests
	String varString1 = "heLLow";
	String varString2 = "hellow2";
	Integer varInteger1 = 4;
	Integer varInteger2 = 60;
	Float varFloat1 = 0.35f;
	Float varFloat2 = 1.55f;

	private void deleteTestConfigFile(Repo repo) {
		File config = new File(repo.getConfigPath());
		if (config.exists()) {
			config.delete();
		}
	}

	@Test
	public void testGetSetRepo() {
		Repo repo = new Repo(configName);

		// insert in repo
		repo.setString("varString1", varString1);
		repo.setString("varString2", varString2);

		repo.setInteger("varInteger1", varInteger1);
		repo.setInteger("varInteger2", varInteger2);

		repo.setFloat("varFloat1", varFloat1);
		repo.setFloat("varFloat2", varFloat2);

		// get vars and check equals
		String expectedString1 = repo.getString("varString1");
		String expectedString2 = repo.getString("varString2");
		Assert.assertEquals(expectedString1, varString1);
		Assert.assertEquals(expectedString2, varString2);

		Integer expectedInteger1 = repo.getInteger("varInteger1");
		Integer expectedInteger2 = repo.getInteger("varInteger2");
		Assert.assertEquals(expectedInteger1, varInteger1);
		Assert.assertEquals(expectedInteger2, varInteger2);

		Float expectedFloat1 = repo.getFloat("varFloat1");
		Float expectedFloat2 = repo.getFloat("varFloat2");
		Assert.assertEquals(expectedFloat1, varFloat1, 5);
		Assert.assertEquals(expectedFloat2, varFloat2, 5);
	}

	@Test
	public void testLoadSaveConfigFile() {
		Repo repo = new Repo(configName);

		// delete previous config test file if exists
		deleteTestConfigFile(repo);

		// insert in repo
		repo.setString("Var1", varString1);
		repo.setInteger("Var2", varInteger1);
		repo.setFloat("Var3", varFloat1);

		// try save and load config file
		Assert.assertTrue(repo.saveConfigFile());
		Assert.assertTrue(repo.loadConfigFile());

		// get vars and check equals
		String expectedString = repo.getString("Var1");
		Integer expectedInteger = repo.getInteger("Var2");
		Float expectedFloat = repo.getFloat("Var3");

		Assert.assertEquals(expectedString, varString1);
		Assert.assertEquals(expectedInteger, varInteger1);
		Assert.assertEquals(expectedFloat, varFloat1, 5);


		// try load with new repo and check equals
		repo = new Repo(configName);
		Assert.assertTrue(repo.loadConfigFile());

		// get vars and check equals
		expectedString = repo.getString("Var1");
		expectedInteger = repo.getInteger("Var2");
		expectedFloat = repo.getFloat("Var3");

		Assert.assertEquals(expectedString, varString1);
		Assert.assertEquals(expectedInteger, varInteger1);
		Assert.assertEquals(expectedFloat, varFloat1, 5);

		// delete config test file
		deleteTestConfigFile(repo);
	}
}
