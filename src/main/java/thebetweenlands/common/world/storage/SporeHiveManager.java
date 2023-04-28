package thebetweenlands.common.world.storage;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.common.world.storage.location.LocationSporeHive;

public class SporeHiveManager {
	protected final World world;

	protected final int range;
	protected final int chunkRange;

	protected final Multimap<ChunkPos, LocationSporeHive> hives = MultimapBuilder.hashKeys().arrayListValues().build();

	public SporeHiveManager(World world, int range) {
		this.world = world;
		this.range = range;
		this.chunkRange = (range >> 4) + 1;
	}

	public boolean checkAt(BlockPos pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(this.world);
		ILocalStorageHandler handler = worldStorage.getLocalStorageHandler();

		int sx = MathHelper.floor(pos.getX() - this.range) >> 4;
		int sz = MathHelper.floor(pos.getZ() - this.range) >> 4;
		int ex = MathHelper.floor(pos.getX() + this.range) >> 4;
		int ez = MathHelper.floor(pos.getZ() + this.range) >> 4;

		boolean has = false;
		
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				// TODO Check if in chunk range
				
				if(!Iterables.isEmpty(handler.getMetadata(LocationSporeHive.class, new ChunkPos(cx, cz), false))) {
					has = true;
				}
			}
		}
		
		return has;
	}
}
