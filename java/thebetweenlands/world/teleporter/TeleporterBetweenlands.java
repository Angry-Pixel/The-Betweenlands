package thebetweenlands.world.teleporter;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodPortalTree;

import java.util.ArrayList;
import java.util.List;

public class TeleporterBetweenlands extends Teleporter {
	private final WorldServer worldServerInstance;
	private final LongHashMap destinationCoordinateCache = new LongHashMap();
	private final List<Long> destinationCoordinateKeys = new ArrayList<Long>();

	//TODO: No special functionality yet, just for testing purposes
	public TeleporterBetweenlands(WorldServer worldServer) {
		super(worldServer);
		worldServerInstance = worldServer;
	}


	//Just putting this here to stop nether portals appearing in the overworld
	@Override
	public void placeInPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw) {
		World world = entity.worldObj;
		if (placeInExistingPortal(entity, posX, posY, posZ, rotationYaw)) {
			for (int y = 200; y > 0; y--) {
				Block block = world.getBlock((int) posX, y, (int) posZ);
				if (block != Blocks.air) {
					if(canGenerate(world, (int)posX, y, (int)posZ)) {
						System.out.println("should have generated," + posX + "," + y + "," + posZ + "world id:" + DimensionManager.getWorld(entity.dimension));
						new WorldGenWeedWoodPortalTree().generate(world, world.rand, (int) posX, y, (int) posZ);
						entity.setLocationAndAngles((int) posX, y + 1, (int) posZ, rotationYaw, 0.0F);
						break;
					}else{
						for (int yy = y; yy < 200; yy++){
							if(canGenerate(world, (int)posX, yy, (int)posZ)){
								System.out.println("should have generated," + posX + "," + yy + "," + posZ + "world id:" + DimensionManager.getWorld(entity.dimension));
								new WorldGenWeedWoodPortalTree().generate(world, world.rand, (int) posX, yy, (int) posZ);
								entity.setLocationAndAngles((int) posX, yy + 2, (int) posZ, rotationYaw, 0.0F);
								break;
							}
						}
					}
					break;
				}
			}
		} /*else {
			teleport(entity, posX, posY, posZ, rotationYaw);
		}*/


	}
	public boolean canGenerate(World world, int posX, int posY, int posZ){
		int height = 16;
		int maxRadius = 9;
		for (int xx = posX - maxRadius; xx <= posX + maxRadius; xx++)
			for (int zz = posZ - maxRadius; zz <= posZ + maxRadius; zz++)
				for (int yy = posY + 2; yy < posY + height; yy++)
					if (!world.isAirBlock(xx, yy, zz) && world.getBlock(xx, yy, zz).isNormalCube())
						return false;
		return true;
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float rotationYaw) {
		int checkRadius = 32;
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
						Block block = worldServerInstance.getBlock(i, j, h);
						if (block instanceof BlockBLLog && ((BlockBLLog) block).getType().equals("weedwood") && block.getDamageValue(worldServerInstance, i, j, h) == 15) {
							double X = i + 0.5 - entity.posX;
							double Y = j + 0.5 - entity.posY;
							double Z = h  + 0.5 - entity.posZ;
							double dist = X * X + Z * Z + Y * Y;

							if (distToPortal < 0.0 || dist < distToPortal) {
								distToPortal = dist;
								posX = i;
								posY = h;
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

			float entityRotation = 0;

			entity.setLocationAndAngles(posX , posY + 2 , posZ , entityRotation, entity.rotationPitch);
			return true;
		}

		return false;
	}
}