package thebetweenlands.common.world.teleporter;

import java.util.UUID;

import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TObjectByteHashMap;
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
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
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
		INSTANCE.transferEntity(entity, ConfigHandler.INSTANCE.dimensionId);
	}

	private final TObjectByteMap<UUID> waitingPlayers = new TObjectByteHashMap<UUID>();
	private boolean checkWaitingPlayers = false;

	private TeleporterBetweenlands teleportToOverworld;
	private TeleporterBetweenlands teleportToBetweenlands;

	private TeleporterHandler() {
	}

	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		if (!(e.getWorld() instanceof WorldServer))
			return;

		WorldServer world = (WorldServer) e.getWorld();

		if (world.provider.getDimensionType() == DimensionType.OVERWORLD)
			world.customTeleporters.add(teleportToOverworld = new TeleporterBetweenlands(world));
		else if (world.provider.getDimensionType() == TheBetweenlands.dimensionType)
			world.customTeleporters.add(teleportToBetweenlands = new TeleporterBetweenlands(world));

		System.out.println("added to " + e.getWorld());
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		if (e.phase != Phase.END || !checkWaitingPlayers)
			return;

		UUID[] ids = waitingPlayers.keys(new UUID[waitingPlayers.size()]);

		for (UUID uuid : ids)
			if (waitingPlayers.adjustOrPutValue(uuid, (byte) -1, (byte) 0) <= 0) {
				waitingPlayers.remove(uuid);
				if (waitingPlayers.isEmpty())
					checkWaitingPlayers = false;
			}
	}

	private void transferEntity(Entity entity, int dimensionId) {
		if (dimensionId != 0 && dimensionId != ConfigHandler.INSTANCE.dimensionId)
			throw new IllegalArgumentException("Supplied invalid dimension ID into Betweenlands teleporter: " + dimensionId);

		World world = entity.worldObj;

		if (!world.isRemote && !entity.isDead)
			if (entity instanceof EntityPlayerMP) {
				if (entity instanceof FakePlayer)
					return;

				EntityPlayerMP player = (EntityPlayerMP) entity;
				if (waitingPlayers.containsKey(player.getUniqueID())) {
					waitingPlayers.put(player.getUniqueID(), (byte) 20);
					return;
				}

				waitingPlayers.put(player.getUniqueID(), (byte) 40); // if there are any issues, we can either increase the number or rewrite the "is player in portal?" checking part
				checkWaitingPlayers = true;
				player.mcServer.getPlayerList().transferPlayerToDimension(player, dimensionId, dimensionId == 0 ? teleportToOverworld : teleportToBetweenlands);

				player.timeUntilPortal = 0;
				/*
				 * player.lastExperience = -1; player.lastHealth = -1.0F; player.lastFoodLevel = -1;
				 */
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