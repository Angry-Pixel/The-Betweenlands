package thebetweenlands.common.world.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import thebetweenlands.util.config.ConfigHandler;

public final class TeleporterHandler {
	private static final TeleporterHandler INSTANCE = new TeleporterHandler();

	private TeleporterHandler() {}

	public static void transferToOverworld(Entity entity) {
		INSTANCE.transferEntity(entity, DimensionType.OVERWORLD.getId());
	}

	public static void transferToBL(Entity entity) {
		INSTANCE.transferEntity(entity, ConfigHandler.dimensionId);
	}

	private void transferEntity(Entity entity, int dimensionId) {
		World world = entity.world;
		if (!world.isRemote && !entity.isDead && !(entity instanceof FakePlayer)) {
			MinecraftServer server = world.getMinecraftServer();
			WorldServer toWorld = server.getWorld(dimensionId);
			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				player.mcServer.getPlayerList().transferPlayerToDimension(player, dimensionId, new TeleporterBetweenlands(toWorld));
				player.timeUntilPortal = 0;
			} else {
				entity.setDropItemsWhenDead(false);
				world.removeEntityDangerously(entity);
				entity.dimension = dimensionId;
				entity.isDead = false;
				WorldServer oldWorld = server.getWorld(entity.dimension);
				server.getPlayerList().transferEntityToWorld(entity, dimensionId, oldWorld, toWorld, new TeleporterBetweenlands(toWorld));
			}
		}
	}
}