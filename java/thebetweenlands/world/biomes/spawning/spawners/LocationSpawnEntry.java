package thebetweenlands.world.biomes.spawning.spawners;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.world.storage.chunk.storage.ChunkStorage;
import thebetweenlands.world.storage.chunk.storage.location.EnumLocationType;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage;

/**
 * Spawns entities only in the specified world location type.
 */
public class LocationSpawnEntry extends BLSpawnEntry {
	protected final EnumLocationType locationType;

	public LocationSpawnEntry(Class<? extends EntityLiving> entityType, EnumLocationType locationType) {
		super(entityType);
		this.locationType = locationType;
	}

	public LocationSpawnEntry(Class<? extends EntityLiving> entityType, short weight, EnumLocationType locationType) {
		super(entityType, weight);
		this.locationType = locationType;
	}

	@Override
	protected void update(World world, int x, int y, int z) {
		boolean inLocation = false;
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		if(chunk != null) {
			BetweenlandsChunkData chunkData = BetweenlandsChunkData.forChunk(world, chunk);
			for(ChunkStorage storage : chunkData.getStorage()) {
				if(storage instanceof LocationStorage && ((LocationStorage)storage).isInside(x, y, z)) {
					LocationStorage location = (LocationStorage)storage;
					if(this.locationType.equals(location.getType())) {
						inLocation = true;
						break;
					}
				}
			}
		}
		if(!inLocation) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.getBaseWeight());
		}
	}
}
