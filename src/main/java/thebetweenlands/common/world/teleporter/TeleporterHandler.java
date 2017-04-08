package thebetweenlands.common.world.teleporter;

import org.omg.CORBA.TypeCodePackage.BadKind;

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
	private static final TeleporterHandler INSTANCE = new TeleporterHandler();

	private TeleporterHandler() {}

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

	/*
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
	}*/

	private void transferEntity(Entity entity, int dimensionId) {
		World world = entity.worldObj;
		if (!world.isRemote && !entity.isDead && !(entity instanceof FakePlayer)) {
			MinecraftServer server = world.getMinecraftServer();
			WorldServer toWorld = server.worldServerForDimension(dimensionId);
			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				player.mcServer.getPlayerList().transferPlayerToDimension(player, dimensionId, new TeleporterBetweenlands(toWorld));
				player.timeUntilPortal = 0;
			} else { // if (!(entity instanceof EntityMinecartContainer))
				world.removeEntityDangerously(entity);
				entity.dimension = dimensionId;
				entity.isDead = false;
				WorldServer oldWorld = server.worldServerForDimension(entity.dimension);
				server.getPlayerList().transferEntityToWorld(entity, dimensionId, oldWorld, toWorld, new TeleporterBetweenlands(toWorld));
			}
		}
	}
}