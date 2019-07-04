package thebetweenlands.common.world.event;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventHeavyRain extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "heavy_rain");

	public EventHeavyRain(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(50000) + 60000;
	}
	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(5000) + 8000;
	}

	@Override
	public void setActive(boolean active) {
		if((active && !this.getRegistry().winter.isActive()) || !active) {
			super.setActive(active);
		}
	}

	@Override
	public void update(World world) {
		super.update(world);

		if(!world.isRemote && this.getRegistry().winter.isActive()) {
			this.setActive(false);
		}

		if(this.isActive() && world.provider instanceof WorldProviderBetweenlands && world.rand.nextInt(20) == 0) {
			if(!world.isRemote && world instanceof WorldServer) {
				WorldServer worldServer = (WorldServer)world;
				for (Iterator<Chunk> iterator = worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); ) {
					Chunk chunk = iterator.next();
					if(world.rand.nextInt(4) == 0) {
						int cbx = world.rand.nextInt(16);
						int cbz = world.rand.nextInt(16);
						BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(chunk.getPos().getXStart() + cbx, -999, chunk.getPos().getZStart() + cbz));
						if(world.getBlockState(pos.add(0, -1, 0)).getBlock() != BlockRegistry.PUDDLE && BlockRegistry.PUDDLE.canPlaceBlockAt(world, pos)) {
							world.setBlockState(pos, BlockRegistry.PUDDLE.getDefaultState());
						}
					}
				}
			}
		}
	}
}
