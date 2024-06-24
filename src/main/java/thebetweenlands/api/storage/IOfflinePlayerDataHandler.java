package thebetweenlands.api.storage;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;

public interface IOfflinePlayerDataHandler {
	void updateCache();

	/**
	 * Returns offline data specific to the player with the specified UUID.
	 * This data is always available, regardless of whether the player is on- or offline.
	 * Use {@link #setOfflinePlayerData(UUID, CompoundTag)} after changing data to make sure it
	 * is saved.
	 * @param playerUuid
	 * @return
	 */
	@Nullable
	CompoundTag getOfflinePlayerData(UUID playerUuid);

	/**
	 * Sets the offline data of the player with the specified UUID.
	 * @param playerUuid
	 * @param tag
	 */
	void setOfflinePlayerData(UUID playerUuid, CompoundTag tag);

	/**
	 * Saves all offline player data to disk.
	 */
	void saveAllOfflinePlayerData();
}
