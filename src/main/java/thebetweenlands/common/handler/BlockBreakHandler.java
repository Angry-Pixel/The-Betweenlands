package thebetweenlands.common.handler;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class BlockBreakHandler {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(player != null && !event.getWorld().isRemote) {
			BlockPos pos = event.getPos();
			IBlockState blockState = event.getState();

			if (!player.isCreative()) {
				//Wake up nearby Pyrads
				if (blockState.getBlock() == BlockRegistry.WEEDWOOD || blockState.getBlock() == BlockRegistry.LOG_WEEDWOOD) {
					BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(event.getWorld());
					List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), location -> location.isInside(pos));

					for (LocationStorage location : locations) {
						if (location.getType() == EnumLocationType.GIANT_TREE) {
							List<EntityPyrad> pyrads = event.getWorld().getEntitiesWithinAABB(EntityPyrad.class, location.getEnclosingBounds(), pyrad -> location.isInside(pyrad));

							for (EntityPyrad pyrad : pyrads) {
								if (!pyrad.isActive() && event.getWorld().rand.nextInt(10) == 0) {
									pyrad.playSound(SoundRegistry.PYRAD_HURT, 0.4F, 1.0F);
									pyrad.playSound(SoundRegistry.PYRAD_HURT, 0.4F, 0.5F);
									pyrad.playSound(SoundRegistry.PYRAD_HURT, 0.4F, 0.25F);
									pyrad.setActive(true);
									pyrad.setAttackTarget(player);
									if (player instanceof EntityPlayerMP)
										AdvancementCriterionRegistry.PYRAD_AGGRO.trigger((EntityPlayerMP) player);
								}
							}
						}
					}
				}
			}
		}
	}
}
