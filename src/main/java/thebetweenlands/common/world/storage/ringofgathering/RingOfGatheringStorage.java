package thebetweenlands.common.world.storage.ringofgathering;

import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class RingOfGatheringStorage extends SavedData {

	// In the past, there was a time that offline playerdata was stored in a separate directory to normal playerdata
	// However, now everything is all stored in the same directory (in the hope there won't be any collisions)
	// As such, we will also store this data in a single directory
	public static final String ID_PREFIX = "thebetweenlands/ringofgathering/";
	
	/**
	 * Creates a factory for {@link RingOfGatheringStorage} with a specific {@link UUID}
	 * @param uuid the UUID of the player this storage refers to
	 * @return a factory for ring of gathering data with the target uuid
	 */
	public static SavedData.Factory<RingOfGatheringStorage> factory(UUID uuid) {
		return new SavedData.Factory<RingOfGatheringStorage>(
				() -> new RingOfGatheringStorage(uuid),
				(tag, provider) -> RingOfGatheringStorage.load(uuid, tag, provider)
			);
	}

	private ObjectList<UUID> minionUuids;
	private Object2ObjectMap<UUID, MinionData> minionData;
	
	public RingOfGatheringStorage(UUID uuid) {
		
	}
	
	public static RingOfGatheringStorage load(UUID uuid, CompoundTag tag, Provider registries) {
		
		return new RingOfGatheringStorage(uuid);
	}
	
	@Override
	public CompoundTag save(CompoundTag tag, Provider registries) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
