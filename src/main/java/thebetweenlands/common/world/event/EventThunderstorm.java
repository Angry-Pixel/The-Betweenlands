package thebetweenlands.common.world.event;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.entity.EntityBLLightningBolt;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntitySimulacrum;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventThunderstorm extends TimedEnvironmentEvent {
	protected int updateLCG = (new Random()).nextInt();

	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "thunderstorm");

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { new ResourceLocation("thebetweenlands:textures/events/thunderstorm.png") };
	
	public EventThunderstorm(BLEnvironmentEventRegistry registry) {
		super(registry);
		this.getActiveStateEstimator().dependsOnEvent(() -> registry.heavyRain);
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
					if(world.provider.canDoLightning(chunk) && world.rand.nextInt(2500) == 0) {
						this.updateLCG = this.updateLCG * 3 + 1013904223;
						int l = this.updateLCG >> 2;
						
						BlockPos seedPos = new BlockPos(chunk.x * 16 + (l & 15), 0, chunk.z * 16 + (l >> 8 & 15));
						
						TileEntitySimulacrum simulacrum = TileEntitySimulacrum.getClosestActiveTile(TileEntitySimulacrum.class, null, worldServer, seedPos.getX() + 0.5D, world.getHeight(seedPos).getY(), seedPos.getZ() + 0.5D, 64.0D, TileEntitySimulacrum.Effect.ATTRACTION, null);
						
						BlockPos pos;
						boolean isFlyingPlayerTarget = false;						
						
						if(simulacrum != null) {
							pos = simulacrum.getPos().up();
						} else {
							pos = this.getNearbyFlyingPlayer(worldServer, seedPos);
							if(pos == null) {
								pos = this.adjustPosToNearbyEntity(worldServer, seedPos);
							} else {
								isFlyingPlayerTarget = true;
							}
						}
						
						if((pos.getY() > 150 || this.getWorld().rand.nextInt(8) == 0) && world.isRainingAt(pos)) {
							world.spawnEntity(new EntityBLLightningBolt(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, isFlyingPlayerTarget ? 50 : 400, isFlyingPlayerTarget, false));
						}
					}
				}
			}
		}
	}

	@Nullable
	protected BlockPos getNearbyFlyingPlayer(WorldServer world, BlockPos blockpos) {
		EntityPlayer closestPlayer = null;
		double closestDistSq = Double.MAX_VALUE;
		for(EntityPlayer player : world.playerEntities) {
			if(player.posY > 130 && (!player.onGround || player.isRiding()) && (player.posY - world.getHeight(new BlockPos(player)).getY()) > 8) {
				double dstSq = (blockpos.getX() - player.posX) * (blockpos.getX() - player.posX) + (blockpos.getZ() - player.posZ) * (blockpos.getZ() - player.posZ);
				if(dstSq < closestDistSq) {
					closestPlayer = player;
					closestDistSq = dstSq;
				}
			}
		}
		
		if(closestPlayer != null && closestDistSq < 50 * 50) {
			double motionX;
			double motionY;
			double motionZ;
			if(closestPlayer.getRidingEntity() != null) {
				motionX = closestPlayer.getRidingEntity().motionX;
				motionY = closestPlayer.getRidingEntity().motionY;
				motionZ = closestPlayer.getRidingEntity().motionZ;
			} else {
				motionX = closestPlayer.motionX;
				motionY = closestPlayer.motionY;
				motionZ = closestPlayer.motionZ;
			}
			
			return new BlockPos(closestPlayer).add(motionX * 60 + world.rand.nextInt(5) - 2, motionY * 60 + world.rand.nextInt(5) - 2, motionZ * 60 + world.rand.nextInt(5) - 2);
		}
		
		return null;
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

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}
	
	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_THUNDERSTORM;
	}
}
