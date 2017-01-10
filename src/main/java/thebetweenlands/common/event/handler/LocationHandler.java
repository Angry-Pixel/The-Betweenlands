package thebetweenlands.common.event.handler;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.location.LocationCragrockTower;

public class LocationHandler {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			EntityPlayer player = event.player;

			if(player != null && !player.isCreative() && !player.worldObj.isRemote) {
				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(player.worldObj);
				List<LocationCragrockTower> locations = worldStorage.getSharedStorageAt(LocationCragrockTower.class, location -> location.isInside(player), player.posX, player.posZ);

				for(LocationCragrockTower location : locations) {
					BlockPos structurePos = location.getStructurePos();

					if(!location.wasEntered()) {
						location.setEntered(true);
					}

					if(location.getInnerBoundingBox().isVecInside(player.getPositionVector()) && player.posY - structurePos.getY() >= 45) {
						if(!location.isTopReached()) {
							location.setTopReached(true);
						}
					} else if(!location.isTopReached() && !location.getInnerBoundingBox().expand(0.5D, 0.5D, 0.5D).isVecInside(player.getPositionVector()) && player.posY - structurePos.getY() > 12) {
						//Player trying to bypass tower, teleport to entrance

						player.dismountRidingEntity();
						if(player instanceof EntityPlayerMP) {
							EntityPlayerMP playerMP = (EntityPlayerMP) player;
							playerMP.connection.setPlayerLocation(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
						} else {
							player.setLocationAndAngles(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
						}
						player.fallDistance = 0.0F;
						player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 2));
						player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.FORTRESS_BOSS_TELEPORT, SoundCategory.AMBIENT, 1, 1);
					} else if(location.isTopReached() && player.posY - structurePos.getY() <= 42 && !location.isCrumbling() && location.getCrumblingTicks() == 0) {
						location.setCrumbling(true);
						location.restoreBlockade(4);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(player != null && !event.getWorld().isRemote) {
			BlockPos pos = event.getPos();
			IBlockState blockState = event.getState();

			if(blockState.getBlock() == BlockRegistry.MOB_SPAWNER) {
				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(event.getWorld());
				List<LocationCragrockTower> towers = worldStorage.getSharedStorageAt(LocationCragrockTower.class, location -> location.isInside(pos), pos.getX(), pos.getZ());

				for(LocationCragrockTower tower : towers) {
					int level = tower.getLevel(pos.getY());

					if(level != -1) {
						tower.setSpawnerState(level, false);
					}
				}
			}
		}
	}
}
