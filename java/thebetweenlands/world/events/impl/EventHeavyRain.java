package thebetweenlands.world.events.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.crops.BlockBLGenericCrop;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.events.TimedEnvironmentEvent;

public class EventHeavyRain extends TimedEnvironmentEvent {
	public EventHeavyRain(EnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public String getEventName() {
		return "heavyRain";
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(130000) + 150000;
	}
	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(10000) + 8000;
	}

	@Override
	public void update(World world) {
		super.update(world);

		if(this.isActive() && world.provider instanceof WorldProviderBetweenlands && world.rand.nextInt(20) == 0) {
			if(!world.isRemote && world instanceof WorldServer) {
				WorldServer worldServer = (WorldServer)world;
				Set<ChunkCoordIntPair> activeChunks = new HashSet();
				activeChunks.addAll(world.getPersistentChunks().keySet());
				for (int i = 0; i < world.playerEntities.size(); ++i) {
					EntityPlayer entityplayer = (EntityPlayer)world.playerEntities.get(i);
					int cx = MathHelper.floor_double(entityplayer.posX / 16.0D);
					int cz = MathHelper.floor_double(entityplayer.posZ / 16.0D);
					int viewDist = worldServer.func_73046_m().getConfigurationManager().getViewDistance();
					for (int cxo = -viewDist; cxo <= viewDist; ++cxo) {
						for (int czo = -viewDist; czo <= viewDist; ++czo) {
							activeChunks.add(new ChunkCoordIntPair(cx + cxo, cz + czo));
						}
					}
				}
				Iterator<ChunkCoordIntPair> it = activeChunks.iterator();
				while(it.hasNext()) {
					ChunkCoordIntPair chunkPos = it.next();
					if(world.rand.nextInt(4) == 0) {
						Chunk chunk = worldServer.getChunkFromChunkCoords(chunkPos.chunkXPos, chunkPos.chunkZPos);
						int cbx = world.rand.nextInt(16);
						int cbz = world.rand.nextInt(16);
						int by = chunk.getPrecipitationHeight(cbx, cbz);
						int bx = chunkPos.chunkXPos * 16 + cbx;
						int bz = chunkPos.chunkZPos * 16 + cbz;
						if(world.getBlock(bx, by-1, bz) != BLBlockRegistry.puddle && (world.isAirBlock(bx, by, bz) || world.getBlock(bx, by, bz) instanceof BlockBLGenericCrop) && BLBlockRegistry.puddle.canPlaceBlockAt(world, bx, by, bz)) {
							world.setBlock(bx, by, bz, BLBlockRegistry.puddle);
						}
					}
				}
			}
		}
	}
}
