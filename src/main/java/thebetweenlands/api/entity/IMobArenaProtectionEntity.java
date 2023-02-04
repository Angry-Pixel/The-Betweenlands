package thebetweenlands.api.entity;

import thebetweenlands.api.storage.ILocalStorage;

public interface IMobArenaProtectionEntity {
	boolean isProtectingLocation(ILocalStorage location);
}
