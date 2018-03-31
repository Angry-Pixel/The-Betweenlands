package thebetweenlands.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.teleporter.TeleporterBetweenlands;

public class PlayerRespawnHandler {
	@SubscribeEvent
	public static void onRespawn(PlayerRespawnEvent event) {
		if(!event.player.world.isRemote && event.player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			BlockPos spawnPos = event.player.getBedLocation(event.player.dimension);
			boolean forced = event.player.isSpawnForced(event.player.dimension);

			BlockPos adjustedSpawnPos = EntityPlayer.getBedSpawnLocation(event.player.world, spawnPos, forced);

			if(adjustedSpawnPos == null) {
				NBTTagCompound dataNbt = event.player.getEntityData();
				NBTTagCompound persistentNbt = dataNbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

				if(persistentNbt.hasKey(TeleporterBetweenlands.LAST_PORTAL_POS_NBT, Constants.NBT.TAG_LONG)) {
					BlockPos lastPortal = BlockPos.fromLong(persistentNbt.getLong(TeleporterBetweenlands.LAST_PORTAL_POS_NBT));

					int spawnFuzz = 64;
					int spawnFuzzHalf = spawnFuzz / 2;
					BlockPos newSpawn = event.player.world.getTopSolidOrLiquidBlock(lastPortal.add(event.player.world.rand.nextInt(spawnFuzz) - spawnFuzzHalf, 0, event.player.world.rand.nextInt(spawnFuzz) - spawnFuzzHalf));

					event.player.setLocationAndAngles(newSpawn.getX() + 0.5D, newSpawn.getY(), newSpawn.getZ() + 0.5D, event.player.rotationYaw, event.player.rotationPitch);

					while (!event.player.world.getCollisionBoxes(event.player, event.player.getEntityBoundingBox()).isEmpty() && event.player.posY < 256.0D) {
						event.player.setPosition(event.player.posX, event.player.posY + 1.0D, event.player.posZ);
					}

					newSpawn = new BlockPos(event.player);

					event.player.setSpawnPoint(newSpawn, true);

					if(event.player instanceof EntityPlayerMP) {
						EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

						playerMP.connection.setPlayerLocation(playerMP.posX, playerMP.posY, playerMP.posZ, playerMP.rotationYaw, playerMP.rotationPitch);
						playerMP.connection.sendPacket(new SPacketSpawnPosition(newSpawn));
					}
				}
			}
		}
	}
}
