package thebetweenlands.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.event.EventRift;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class PlayerJoinDimensionHandler {
	public static final String NOT_FIRST_JOIN_NBT = "thebetweenlands.not_first_join";
	
	@SubscribeEvent
	public static void onEntityJoin(EntityJoinWorldEvent event) {
		if(BetweenlandsConfig.WORLD_AND_DIMENSION.activateRiftOnFirstJoin) {
			if(!event.getWorld().isRemote && event.getWorld().provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && event.getEntity() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getEntity();

				NBTTagCompound dataNbt = player.getEntityData();
				NBTTagCompound persistentNbt = dataNbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

				boolean isFirstTime = !(persistentNbt.hasKey(NOT_FIRST_JOIN_NBT, Constants.NBT.TAG_BYTE) && persistentNbt.getBoolean(NOT_FIRST_JOIN_NBT));

				if(isFirstTime) {
					int minActiveTicks = BetweenlandsConfig.WORLD_AND_DIMENSION.minRiftOnFirstJoinDuration * 20;

					EventRift rift = BetweenlandsWorldStorage.forWorld(event.getWorld()).getEnvironmentEventRegistry().rift;
					if(!rift.isActive()) {
						rift.setActive(true, true);
					}
					if(rift.getTicks() < minActiveTicks) {
						rift.setTicks(minActiveTicks);
					}

					persistentNbt.setBoolean(NOT_FIRST_JOIN_NBT, true);
					dataNbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNbt);
				}
			}
		}
	}
}
