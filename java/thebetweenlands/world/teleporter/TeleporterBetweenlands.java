package thebetweenlands.world.teleporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodPortalTree;

public final class TeleporterBetweenlands extends Teleporter {
	private final WorldServer worldServerInstance;
	private final LongHashMap destinationCoordinateCache = new LongHashMap();
	private final List<Long> destinationCoordinateKeys = new ArrayList<Long>();

	public TeleporterBetweenlands(WorldServer worldServer) {
		super(worldServer);
		worldServerInstance = worldServer;
	}

	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float rotationYaw) {
		if (!placeInExistingPortal(entity, x, y, z, rotationYaw)) {
			makePortal(entity);
			placeInExistingPortal(entity, x, y, z, rotationYaw);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float rotationYaw) {
		int checkRadius = 50;
		double distToPortal = -1.0;
		int posX = 0;
		int posY = 0;
		int posZ = 0;
		int roundX = MathHelper.floor_double(entity.posX);
		int roundZ = MathHelper.floor_double(entity.posZ);
		long coordPair = ChunkCoordIntPair.chunkXZ2Int(roundX, roundZ);
		boolean portalNotSaved = true;

		
		if (destinationCoordinateCache.containsItem(coordPair)) {
			PortalPosition pos = (PortalPosition) destinationCoordinateCache.getValueByKey(coordPair);
			distToPortal = 0.0;
			posX = pos.posX;
			posY = pos.posY;
			posZ = pos.posZ;
			pos.lastUpdateTime = worldServerInstance.getTotalWorldTime();
			portalNotSaved = false;
		} else
			for (int i = roundX - checkRadius; i <= roundX + checkRadius; i++)
				for (int j = roundZ - checkRadius; j <= roundZ + checkRadius; j++)
					for (int h = worldServerInstance.getActualHeight() - 1; h >= 0; h--) {
						Block block = worldServerInstance.getBlock(i, h, j);
						if (block == BLBlockRegistry.druidStone1) {
							double X = i + 0.5 - entity.posX;
							double Z = j + 0.5 - entity.posZ;
							double Y = h + 2  + 0.5 - entity.posY;
							double dist = X * X + Y * Y + Z * Z;

							if (distToPortal < 0.0 || dist < distToPortal) {
								distToPortal = dist;
								posX = i;
								posY = h + 2;
								posZ = j;
							}
						}
					}
		if (distToPortal >= 0.0) {
			if (portalNotSaved) {
				destinationCoordinateCache.add(coordPair, new PortalPosition(posX, posY, posZ, worldServerInstance.getTotalWorldTime()));
				destinationCoordinateKeys.add(Long.valueOf(coordPair));
			}

			entity.motionX = entity.motionY = entity.motionZ = 0.0;

			int entityFacing = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			float entityRotation = 0;
			double offsetX = 0;
			double offsetZ = 0;

			switch (entityFacing) {
				case 0:
					entityRotation = 180;
					offsetX = 0.5D;
					offsetZ = -4.5D;
					break;
				case 1:
					entityRotation = 270;
					offsetX = 4.5D;
					offsetZ = 0.5D;
					break;
				case 2:
					entityRotation = 0;
					offsetX = 0.5D;
					offsetZ = 4.5D;
					break;
				case 3:
					entityRotation = 90;
					offsetX = -0.5D;
					offsetZ = 4.5D;
					break;
			}

			entity.setLocationAndAngles(posX + offsetX, posY, posZ + offsetZ, entityRotation, entity.rotationPitch);
			return true;
		}

		return false;
	}

	@Override
	public boolean makePortal(Entity entity) {
		int posX = MathHelper.floor_double(entity.posX);
		int posZ = MathHelper.floor_double(entity.posZ);
		int maxPortalSpawnHeight;
		if (entity.dimension == ConfigHandler.DIMENSION_ID){
			maxPortalSpawnHeight = 85;
		}else {
			maxPortalSpawnHeight = 100;
		}
		for (int z = posZ; z < posZ + 40; z++) {
			for (int y = maxPortalSpawnHeight; y > 80; y--) {
				Block block = worldServerInstance.getBlock(posX, y, z);
				if (block != Blocks.air) {
					if (canGenerate(worldServerInstance, posX, y, z)) {
						new WorldGenWeedWoodPortalTree().generate(worldServerInstance, worldServerInstance.rand, posX, y, z);
						entity.setLocationAndAngles(posX, y + 2, z, entity.rotationYaw, entity.rotationPitch);
						return true;
					} else {
						for (int yy = y; yy < maxPortalSpawnHeight; yy++) {
							if (canGenerate(worldServerInstance, posX, yy, z)) {
								new WorldGenWeedWoodPortalTree().generate(worldServerInstance, worldServerInstance.rand, posX, yy, z);
								entity.setLocationAndAngles(posX, yy + 2, z, entity.rotationYaw, entity.rotationPitch);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean canGenerate(World world, int posX, int posY, int posZ){
		int height = 16;
		int maxRadius = 11;
		for (int xx = posX - maxRadius; xx <= posX + maxRadius; xx++)
			for (int zz = posZ - maxRadius; zz <= posZ + maxRadius; zz++)
				for (int yy = posY + 2; yy < posY + height; yy++) {
					Block block = world.getBlock(xx, yy, zz);
					if (!world.isAirBlock(xx, yy, zz) && block.isNormalCube())
						return false;
					else if(block instanceof BlockBLLeaves)
						return false;
				}
		return true;
	}

	@Override
	public void removeStalePortalLocations(long timer) {
		if (timer % 100L == 0L) {
			Iterator<Long> iterator = destinationCoordinateKeys.iterator();
			while (iterator.hasNext()) {
				Long hashedPortalPos = iterator.next();
				PortalPosition position = (PortalPosition) destinationCoordinateCache.getValueByKey(hashedPortalPos.longValue());

				if (position == null || position.lastUpdateTime < timer - 600L) {
					iterator.remove();
					destinationCoordinateCache.remove(hashedPortalPos.longValue());
				}
			}
		}
	}
}