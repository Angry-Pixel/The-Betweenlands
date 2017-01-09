package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.location.EnumLocationType;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

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
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
		List<LocationStorage> locations = worldStorage.getSharedStorageAt(LocationStorage.class, location -> location.isInside(pos) && this.locationType.equals(location.getType()), pos.getX(), pos.getZ());
		if(locations.isEmpty()) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.getBaseWeight());
		}
	}
}
