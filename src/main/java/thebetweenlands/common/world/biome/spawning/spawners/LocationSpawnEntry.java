package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

/**
 * Spawns entities only in the specified world location type.
 */
public class LocationSpawnEntry extends BLSpawnEntry {
	protected final EnumLocationType locationType;

	public LocationSpawnEntry(int id, Class<? extends EntityLiving> entityType, EnumLocationType locationType) {
		super(id, entityType);
		this.locationType = locationType;
	}

	public LocationSpawnEntry(int id, Class<? extends EntityLiving> entityType, short weight, EnumLocationType locationType) {
		super(id, entityType, weight);
		this.locationType = locationType;
	}

	@Override
	public void update(World world, BlockPos pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), location -> location.isInside(pos) && this.locationType.equals(location.getType()));
		if(locations.isEmpty()) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.getBaseWeight());
		}
	}
}
