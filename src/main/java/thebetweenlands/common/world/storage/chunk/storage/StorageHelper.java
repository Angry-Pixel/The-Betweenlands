package thebetweenlands.common.world.storage.chunk.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.common.world.storage.chunk.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.chunk.storage.location.GuardedLocationStorage;
import thebetweenlands.common.world.storage.chunk.storage.location.LocationStorage;

public class StorageHelper {
	public static List<LocationStorage> addArea(World world, String name, AxisAlignedBB area, EnumLocationType type, int layer) {
		return addArea(world, name, area, type, layer, false);
	}

	public static List<LocationStorage> addArea(World world, String name, AxisAlignedBB area, EnumLocationType type, int layer, boolean guarded) {
		List<LocationStorage> addedLocations = new ArrayList<LocationStorage>();
		int sx = MathHelper.floor_double(area.minX / 16.0D);
		int sz = MathHelper.floor_double(area.minZ / 16.0D);
		int ex = MathHelper.floor_double(area.maxX / 16.0D);
		int ez = MathHelper.floor_double(area.maxZ / 16.0D);
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
				double csx = Math.max(area.minX, cx * 16);
				double csz = Math.max(area.minZ, cz * 16);
				double cex = Math.min(area.maxX, (cx+1) * 16);
				double cez = Math.min(area.maxZ, (cz+1) * 16);
				AxisAlignedBB clampedArea = new AxisAlignedBB(csx, area.minY, csz, cex, area.maxY, cez);
				BetweenlandsChunkData chunkData = BetweenlandsChunkData.forChunk(world, chunk);
				LocationStorage location;
				if(guarded) {
					location = new GuardedLocationStorage(chunk, chunkData, name, clampedArea, type).setLayer(layer);
				} else {
					location = new LocationStorage(chunk, chunkData, name, clampedArea, type).setLayer(layer);
				}
				addedLocations.add(location);
				chunkData.getStorage().add(location);
				chunkData.markDirty();
			}
		}
		return addedLocations;
	}

	public static void removeArea(World world, String name, AxisAlignedBB area) {
		int sx = MathHelper.floor_double(area.minX / 16.0D);
		int sz = MathHelper.floor_double(area.minZ / 16.0D);
		int ex = MathHelper.floor_double(area.maxX / 16.0D);
		int ez = MathHelper.floor_double(area.maxZ / 16.0D);
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
				BetweenlandsChunkData chunkData = BetweenlandsChunkData.forChunk(world, chunk);
				boolean changed = false;
				Iterator<ChunkStorage> storageIT = chunkData.getStorage().iterator();
				while(storageIT.hasNext()) {
					ChunkStorage storage = storageIT.next();
					if(storage instanceof LocationStorage) {
						LocationStorage areaStorage = (LocationStorage)storage;
						if(name.equals(areaStorage.getName())) {
							storageIT.remove();
							changed = true;
						}
					}
				}
				if(changed) {
					chunkData.markDirty();
				}
			}
		}
	}

	public static List<LocationStorage> getAreas(World world, AxisAlignedBB area) {
		List<LocationStorage> foundLocations = new ArrayList<LocationStorage>();
		int sx = MathHelper.floor_double(area.minX / 16.0D);
		int sz = MathHelper.floor_double(area.minZ / 16.0D);
		int ex = MathHelper.floor_double(area.maxX / 16.0D);
		int ez = MathHelper.floor_double(area.maxZ / 16.0D);
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
				BetweenlandsChunkData chunkData = BetweenlandsChunkData.forChunk(world, chunk);
				for(ChunkStorage storage : chunkData.getStorage()) {
					if(storage instanceof LocationStorage) {
						LocationStorage location = (LocationStorage) storage;
						if(location.getArea().intersectsWith(area))
							foundLocations.add(location);
					}
				}
			}
		}
		return foundLocations;
	}
}
