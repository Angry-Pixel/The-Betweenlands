package thebetweenlands.common.world.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.config.ConfigHandler;

public final class TeleporterHandler {
	private static TeleporterHandler INSTANCE = new TeleporterHandler();

	public static void init() {
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	public static void transferToOverworld(Entity entity) {
		INSTANCE.transferEntity(entity, 0);
	}

	public static void transferToBL(Entity entity) {
		INSTANCE.transferEntity(entity, ConfigHandler.dimensionId);
	}

	private TeleporterBetweenlands teleportToOverworld;
	private TeleporterBetweenlands teleportToBetweenlands;

	private TeleporterHandler() { }


	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		if (!(e.getWorld() instanceof WorldServer))
			return;

		WorldServer world = (WorldServer) e.getWorld();

		//TODO This is broken, teleporter is set AFTER the entity is transferred and if the dimension was unloaded the reference points to an invalid world and the portal doesn't gen

		if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			world.customTeleporters.add(this.teleportToOverworld = new TeleporterBetweenlands(world));
		} else if (world.provider.getDimensionType() == TheBetweenlands.dimensionType) {
			world.customTeleporters.add(this.teleportToBetweenlands = new TeleporterBetweenlands(world));
		}
	}

	private void transferEntity(Entity entity, int dimensionId) {
		World world = entity.worldObj;

		if (!world.isRemote && !entity.isDead) {
			if (entity instanceof EntityPlayerMP) {
				if (entity instanceof FakePlayer) {
					return;
				}

				EntityPlayerMP player = (EntityPlayerMP) entity;
				player.mcServer.getPlayerList().transferPlayerToDimension(player, dimensionId, dimensionId == 0 ? teleportToOverworld : teleportToBetweenlands);
				player.timeUntilPortal = 0;
			} else if (!(entity instanceof EntityMinecartContainer)) { // TODO we cannot handle this, would result in container breaking in both worlds and duplicate items;
				// find some sneaky solution around this issue fixme copy paste
				world.theProfiler.startSection("changeDimension");

				MinecraftServer mcServer = world.getMinecraftServer();
				WorldServer worldCurrent = mcServer.worldServerForDimension(entity.dimension);
				WorldServer worldTarget = mcServer.worldServerForDimension(dimensionId);
				entity.dimension = dimensionId;

				world.removeEntity(entity);
				entity.isDead = false;
				world.theProfiler.startSection("reposition");
				mcServer.getPlayerList().transferEntityToWorld(entity, dimensionId, worldCurrent, worldTarget, dimensionId == 0 ? teleportToOverworld : teleportToBetweenlands);
				world.theProfiler.endStartSection("reloading");
				Entity newEntity = EntityList.createEntityByName(EntityList.getEntityString(entity), worldTarget);

				if (newEntity != null) {
					//newEntity.copyDataFromOld(entity);
					worldTarget.spawnEntityInWorld(newEntity);
				}

				entity.isDead = true;
				world.theProfiler.endSection();
				worldCurrent.resetUpdateEntityTick();
				worldTarget.resetUpdateEntityTick();
				world.theProfiler.endSection();

				newEntity.timeUntilPortal = entity.getPortalCooldown();
			}
		}
	}
}