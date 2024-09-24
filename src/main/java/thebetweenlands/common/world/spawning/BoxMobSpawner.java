package thebetweenlands.common.world.spawning;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.entity.spawning.BiomeSpawnEntriesData;
import thebetweenlands.api.entity.spawning.CustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.CustomSpawnEntry;

public class BoxMobSpawner extends AreaMobSpawner {
	private static class SimpleSpawnEntriesData implements BiomeSpawnEntriesData {
		private final Object2LongMap<ResourceLocation> lastSpawnMap = new Object2LongOpenHashMap<>();

		@Override
		public long getLastSpawn(CustomSpawnEntry spawnEntry) {
			return this.lastSpawnMap.containsKey(spawnEntry.getID()) ? this.lastSpawnMap.getLong(spawnEntry.getID()) : -1;
		}

		@Override
		public void setLastSpawn(CustomSpawnEntry spawnEntry, long lastSpawn) {
			this.lastSpawnMap.put(spawnEntry.getID(), lastSpawn);
		}

		@Override
		public long removeLastSpawn(CustomSpawnEntry spawnEntry) {
			return this.lastSpawnMap.removeLong(spawnEntry.getID());
		}
	}

	private final BiomeSpawnEntriesData spawnEntriesData = new SimpleSpawnEntriesData();
	private final List<AABB> areas = new ArrayList<>();
	private final List<CustomSpawnEntry> spawnEntries = new ArrayList<>();
	private int maxEntities;

	public void addArea(AABB area) {
		this.areas.add(area);
	}

	public void clearAreas() {
		this.areas.clear();
	}

	public void setMaxAreaEntities(int maxEntities) {
		this.maxEntities = maxEntities;
	}

	public void addSpawnEntry(CustomSpawnEntry entry) {
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
	public List<CustomSpawnEntry> getSpawnEntries(Level level, BlockPos pos, CustomSpawnEntriesProvider provider) {
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
	public BiomeSpawnEntriesData getSpawnEntriesData(Level level, BlockPos pos, CustomSpawnEntriesProvider provider) {
		return this.spawnEntriesData;
	}

	@Override
	protected void updateSpawnerChunks(ServerLevel level, Set<ChunkPos> spawnerChunks) {
		spawnerChunks.clear();

		for(AABB area : this.areas) {
			int sx = Mth.floor(area.minX) >> 4;
			int sz = Mth.floor(area.minZ) >> 4;
			int ex = Mth.floor(area.maxX) >> 4;
			int ez = Mth.floor(area.maxZ) >> 4;

			for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					spawnerChunks.add(new ChunkPos(cx, cz));
				}
			}
		}
	}

	@Override
	public boolean isInsideSpawningArea(Level level, BlockPos pos, boolean entityCount) {
		return this.isInsideAnyArea(pos) && (entityCount || level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10.0D, false) == null);
	}

	private boolean isInsideAnyArea(BlockPos pos) {
		for(AABB area : this.areas) {
			if(area.intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)) {
				return true;
			}
		}
		return false;
	}
}
