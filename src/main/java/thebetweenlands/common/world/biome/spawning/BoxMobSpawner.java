package thebetweenlands.common.world.biome.spawning;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;

public class BoxMobSpawner extends AreaMobSpawner {
	private List<AxisAlignedBB> areas = new ArrayList<>();
	private List<ICustomSpawnEntry> spawnEntries = new ArrayList<>();
	private int maxEntities;

	public void addArea(AxisAlignedBB area) {
		this.areas.add(area);
	}

	public void clearAreas() {
		this.areas.clear();
	}

	public void setMaxAreaEntities(int maxEntities) {
		this.maxEntities = maxEntities;
	}

	public void addSpawnEntry(ICustomSpawnEntry entry) {
		this.spawnEntries.add(entry);
	}

	public void clearSpawnEntries() {
		this.spawnEntries.clear();
	}

	@Override
	public int getHardEntityLimit() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public List<ICustomSpawnEntry> getSpawnEntries(World world, BlockPos pos, ICustomSpawnEntriesProvider provider) {
		return this.spawnEntries;
	}

	@Override
	public float getMaxEntitiesPerSpawnChunkFraction(int spawnerChunks) {
		if(spawnerChunks > 0) {
			return this.maxEntities / (float)spawnerChunks;
		}
		return 0.0f;
	}

	@Override
	protected void updateSpawnerChunks(WorldServer world, Set<ChunkPos> spawnerChunks) {
		spawnerChunks.clear();

		for(AxisAlignedBB area : this.areas) {
			int sx = MathHelper.floor(area.minX) >> 4;
			int sz = MathHelper.floor(area.minZ) >> 4;
			int ex = MathHelper.floor(area.maxX) >> 4;
			int ez = MathHelper.floor(area.maxZ) >> 4;

			for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					spawnerChunks.add(new ChunkPos(cx, cz));
				}
			}
		}
	}

	@Override
	public boolean isInsideSpawningArea(World world, BlockPos pos, boolean entityCount) {
		return this.isInsideAnyArea(pos) && (entityCount || world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 12.0D, false) == null);
	}

	private boolean isInsideAnyArea(BlockPos pos) {
		for(AxisAlignedBB area : this.areas) {
			if(area.intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)) {
				return true;
			}
		}
		return false;
	}
}
