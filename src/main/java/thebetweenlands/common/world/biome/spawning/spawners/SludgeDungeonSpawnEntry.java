package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationStorage;

/**
 * Spawns entities only in the sludge worm dungeon and on the specified floor.
 */
public class SludgeDungeonSpawnEntry extends BLSpawnEntry {
	protected final int floor;
	
	public SludgeDungeonSpawnEntry(int id, Class<? extends EntityLiving> entityType, int floor) {
		super(id, entityType);
		this.floor = floor;
	}

	public SludgeDungeonSpawnEntry(int id, Class<? extends EntityLiving> entityType, short weight, int floor) {
		super(id, entityType, weight);
		this.floor = floor;
	}

	@Override
	public void update(World world, BlockPos pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		
		List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), location -> location instanceof LocationSludgeWormDungeon);
		
		if(!locations.isEmpty() && ((LocationSludgeWormDungeon) locations.get(0)).getFloor(pos) == this.floor) {
			this.setWeight(this.getBaseWeight());
		} else {
			this.setWeight((short) 0);
		}
	}
}
