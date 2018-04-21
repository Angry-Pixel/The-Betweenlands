package thebetweenlands.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.event.EventRift;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.teleporter.TeleporterHandler;

public class PlayerJoinDimensionHandler {
	/**
	 * NBT key that stores whether the player has already been in the dimension
	 */
	public static final String NOT_FIRST_JOIN_NBT = "thebetweenlands.not_first_join";

	/**
	 * NBT key that stores whether the player has already spawned
	 */
	public static final String NOT_FIRST_SPAWN_NBT = "thebetweenlands.not_first_spawn";

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinWorldEvent event) {

		if(!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();

			NBTTagCompound dataNbt = player.getEntityData();
			NBTTagCompound persistentNbt = dataNbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

			if(BetweenlandsConfig.WORLD_AND_DIMENSION.activateRiftOnFirstJoin && event.getWorld().provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
				boolean isFirstTimeInDimension = !(persistentNbt.hasKey(NOT_FIRST_JOIN_NBT, Constants.NBT.TAG_BYTE) && persistentNbt.getBoolean(NOT_FIRST_JOIN_NBT));

				if(isFirstTimeInDimension) {
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
			
			if(BetweenlandsConfig.WORLD_AND_DIMENSION.startInBetweenlands && event.getWorld().provider.getDimension() != BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && event.getWorld() instanceof WorldServer) {
				boolean isFirstTimeSpawning = !(persistentNbt.hasKey(NOT_FIRST_SPAWN_NBT, Constants.NBT.TAG_BYTE) && persistentNbt.getBoolean(NOT_FIRST_SPAWN_NBT));

				if(isFirstTimeSpawning) {
					//Set before teleporting because recursion
					persistentNbt.setBoolean(NOT_FIRST_SPAWN_NBT, true);
					dataNbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNbt);
					
					WorldServer blWorld = ((WorldServer) event.getWorld()).getMinecraftServer().getWorld(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
					
					TeleporterHandler.transferToDim(event.getEntity(), blWorld, BetweenlandsConfig.WORLD_AND_DIMENSION.startInPortal);
				}
			}
		}
	}
}
