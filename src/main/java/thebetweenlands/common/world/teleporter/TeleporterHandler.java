package thebetweenlands.common.world.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

public final class TeleporterHandler {
	private static final TeleporterHandler INSTANCE = new TeleporterHandler();

	private TeleporterHandler() {}
	
	public static void transferToDim(Entity entity, World world) {
		INSTANCE.transferEntity(entity, world.provider.getDimension(), true, true);
	}
	
	public static void transferToDim(Entity entity, World world, boolean makePortal, boolean setSpawn) {
		INSTANCE.transferEntity(entity, world.provider.getDimension(), makePortal, setSpawn);
	}

	private void transferEntity(Entity entity, int dimensionId, boolean makePortal, boolean setSpawn) {
		World world = entity.world;
		if (!world.isRemote && !entity.isDead && !(entity instanceof FakePlayer) && world instanceof WorldServer) {
			if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, dimensionId)) {
				return;
			}
			
			MinecraftServer server = world.getMinecraftServer();
			WorldServer toWorld = server.getWorld(dimensionId);
			AxisAlignedBB aabb = entity.getEntityBoundingBox();
			aabb = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				player.invulnerableDimensionChange = true;
				player.server.getPlayerList().transferPlayerToDimension(player, dimensionId, new TeleporterBetweenlands(world.provider.getDimension(), aabb, toWorld, makePortal, setSpawn));
				player.timeUntilPortal = 0;
			} else {
				entity.setDropItemsWhenDead(false);
				world.removeEntityDangerously(entity);
				entity.dimension = dimensionId;
				entity.isDead = false;
				WorldServer oldWorld = server.getWorld(entity.dimension);
				server.getPlayerList().transferEntityToWorld(entity, dimensionId, oldWorld, toWorld, new TeleporterBetweenlands(world.provider.getDimension(), aabb, toWorld, makePortal, setSpawn));
			}
		}
	}
}