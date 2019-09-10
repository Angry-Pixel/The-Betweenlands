package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class ConditionalSpawnEntry extends BLSpawnEntry {
	private final BiPredicate<World, BlockPos> condition;
	private final BLSpawnEntry parent;

	public ConditionalSpawnEntry(int id, BLSpawnEntry parent, BiPredicate<World, BlockPos> condition) {
		super(id, parent.getEntityType(), parent.getBaseWeight());
		this.parent = parent;
		this.condition = condition;
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState) {
		return this.parent.canSpawn(world, chunk, pos, blockState, surfaceBlockState);
	}

	@Override
	public void update(World world, BlockPos pos) {
		if(this.condition.test(world, pos)) {
			this.setWeight(this.parent.getWeight());
		} else {
			this.setWeight((short) 0);
		}
	}

	@Override
	public EntityLiving createEntity(World world) {
		return this.parent.createEntity(world);
	}

	@Override
	public boolean isSaved() {
		return this.parent.isSaved();
	}

	@Override
	public void onSpawned(EntityLivingBase entity) {
		this.parent.onSpawned(entity);
	}

	@Override
	public boolean shouldCheckExistingGroups() {
		return this.parent.shouldCheckExistingGroups();
	}

	public static BiPredicate<World, BlockPos> createEventPredicate(ResourceLocation eventName) {
		return (world, pos) -> {
			if(world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
				IEnvironmentEvent event = provider.getEnvironmentEventRegistry().forName(eventName);
				return event != null && event.isActive();
			}
			return false;
		};
	}

	public static BiPredicate<World, BlockPos> createSludgeDungeonPredicate(int floor) {
		return (world, pos) -> {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);

			List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), location -> location instanceof LocationSludgeWormDungeon);

			return !locations.isEmpty() && ((LocationSludgeWormDungeon) locations.get(0)).getFloor(pos) == floor;
		};
	}
}
