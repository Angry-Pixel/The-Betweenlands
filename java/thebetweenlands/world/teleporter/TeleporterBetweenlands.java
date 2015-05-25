package thebetweenlands.world.teleporter;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodPortalTree;

public class TeleporterBetweenlands extends Teleporter {
	//TODO: No special functionality yet, just for testing purposes
	public TeleporterBetweenlands(WorldServer worldServer) {
		super(worldServer);
	}

	//Just putting this here to stop nether portals appearing in the overworld
	@Override
	public void placeInPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw) {
		World world = entity.worldObj;
		int radius = 40;
		int lowest = (int) posY - radius;
		int highest = (int) posY + radius;
		int xMin = (int) posX - radius;
		int xMax = (int) posX + radius;
		int zMin = (int) posZ - radius;
		int zMax = (int) posZ + radius;
		boolean check = true;

		for (int y = lowest; y <= highest; y++) {
			for (int x = xMin; x <= xMax; x++) {
				for (int z = zMin; z <= zMax; z++) {
					Block block = world.getBlock(x, y, z);
					int meta = block.getDamageValue(world, x, y, z);
					if (block instanceof BlockBLLog && ((BlockBLLog) block).getType().equals("weedwood") && meta == 15) {
						System.out.println("should have teleported:" + x + "," + y + "," + z);
						entity.setLocationAndAngles(x, y + 1, z, entity.rotationYaw, 0.0F);
						check = false;
						break;
					}
				}
			}
		}
		if (check) {
			for (int y = 200; y > 0; y--) {
				Block block = world.getBlock((int) posX, y, (int) posX);
				if (block != Blocks.air) {
					if(canGenerate(world, (int)posX, y, (int)posZ)) {
						System.out.println("should have generated");
						new WorldGenWeedWoodPortalTree().generate(world, world.rand, (int) posX, y, (int) posZ);
						entity.setLocationAndAngles((int) posX, y + 1, (int) posZ, entity.rotationYaw, 0.0F);
					}else{
						for (int yy = y; yy < 200; yy++){
							if(canGenerate(world, (int)posX, yy, (int)posZ)){
								System.out.println("should have generated");
								new WorldGenWeedWoodPortalTree().generate(world, world.rand, (int) posX, yy, (int) posZ);
								entity.setLocationAndAngles((int) posX, yy + 1, (int) posZ, entity.rotationYaw, 0.0F);
								break;
							}
						}
					}
					break;
				}
			}
		}
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
}