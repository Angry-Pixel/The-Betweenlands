package thebetweenlands.common.world.event;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.entity.EntityBLLightningBolt;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventThunderstorm extends TimedEnvironmentEvent {
	protected int updateLCG = (new Random()).nextInt();

	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "thunderstorm");

	public EventThunderstorm(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	protected boolean canActivate() {
		return this.getRegistry().heavyRain.isActive();
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public void update(World world) {
		super.update(world);

		if (!world.isRemote) {
			if(this.isActive() && !this.getRegistry().heavyRain.isActive()) {
				this.setActive(false);
			}

			if(this.isActive() && world.provider instanceof WorldProviderBetweenlands && world instanceof WorldServer) {
				WorldServer worldServer = (WorldServer)world;
				for (Iterator<Chunk> iterator = worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); ) {
					Chunk chunk = iterator.next();
					if(world.provider.canDoLightning(chunk) && world.rand.nextInt(30000) == 0) {
						this.updateLCG = this.updateLCG * 3 + 1013904223;
						int l = this.updateLCG >> 2;
						BlockPos pos = this.adjustPosToNearbyEntity(worldServer, new BlockPos(chunk.x * 16 + (l & 15), 0, chunk.z * 16 + (l >> 8 & 15)));
						if(world.isRainingAt(pos)) {
							world.spawnEntity(new EntityBLLightningBolt(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 400));
						}
					}
				}
			}
		}
	}

	protected BlockPos adjustPosToNearbyEntity(WorldServer world, BlockPos pos) {
		BlockPos blockpos = world.getPrecipitationHeight(pos);
		AxisAlignedBB aabb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), world.getHeight(), blockpos.getZ()))).grow(3.0D);
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, new com.google.common.base.Predicate<EntityLivingBase>() {
			@Override
			public boolean apply(@Nullable EntityLivingBase entity) {
				return entity != null && entity.isEntityAlive() && world.canSeeSky(entity.getPosition());
			}
		});

		if (!list.isEmpty()) {
			return ((EntityLivingBase)list.get(world.rand.nextInt(list.size()))).getPosition();
		} else {
			if (blockpos.getY() == -1) {
				blockpos = blockpos.up(2);
			}

			return blockpos;
		}
	}

	@Override
	public int getOffTime(Random rnd) {
		return 5000 + rnd.nextInt(4000);
	}

	@Override
	public int getOnTime(Random rnd) {
		return 4000 + rnd.nextInt(4000);
	}
}
