package SynchronizeFolders.Repo;

import java.io.Serializable;

public class RepoObject <T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public RepoObject(T value) {
		this.value = value;
	}

	private T value;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
