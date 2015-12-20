package thebetweenlands.utils.pool;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {
	private InstanceProvider<T> instanceProvider;

	private int size;

	private List<T> instances;

	public Pool(InstanceProvider<T> instanceProvider, int size) {
		this.instanceProvider = instanceProvider;
		this.size = size;
		instances = new ArrayList<T>();
	}

	public T getInstance() {
		if (instances.isEmpty()) {
			return instanceProvider.newInstance();
		}
		return instances.remove(0);
	}

	public void freeInstance(T instance) {
		if (instances.size() < size) {
			instances.add(instance);
		}
	}
}
