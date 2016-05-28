package thebetweenlands.common.world.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.TimedEnvironmentEvent;

import java.util.Iterator;
import java.util.Random;

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
				for (Iterator<Chunk> iterator = worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); ) {
					Chunk chunk = iterator.next();
					if(world.rand.nextInt(4) == 0) {
						int cbx = world.rand.nextInt(16);
						int cbz = world.rand.nextInt(16);
						BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(cbx, -999, cbz));
						//TODO: Re-implement rain
						/*if(world.getBlockState(pos.add(0, -1, 0)).getBlock() != BLBlockRegistry.puddle && (world.isAirBlock(pos) || world.getBlockState(pos).getBlock() instanceof BlockBLGenericCrop) && BLBlockRegistry.puddle.canPlaceBlockAt(world, bx, by, bz)) {
							world.setBlock(bx, by, bz, BLBlockRegistry.puddle);
						}*/
					}
				}
			}
		}
	}
}
