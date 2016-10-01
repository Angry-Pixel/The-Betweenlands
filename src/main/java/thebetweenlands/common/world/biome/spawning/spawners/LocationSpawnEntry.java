package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.common.world.storage.chunk.storage.ChunkStorage;
import thebetweenlands.common.world.storage.chunk.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.chunk.storage.location.LocationStorage;

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
	public void update(World world, BlockPos pos) {
		boolean inLocation = false;
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		if(chunk != null) {
			BetweenlandsChunkData chunkData = BetweenlandsChunkData.forChunk(world, chunk);
			for(ChunkStorage storage : chunkData.getStorage()) {
				if(storage instanceof LocationStorage && ((LocationStorage)storage).isInside(pos)) {
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
