package thebetweenlands.api.storage;

import java.util.UUID;

public class StorageUUID extends StorageID {
	private final UUID uuid;

	public StorageUUID(UUID uuid) {
		super(uuid.toString());
		this.uuid = uuid;
	}

	public UUID getUUID() {
		return this.uuid;
	}
}
