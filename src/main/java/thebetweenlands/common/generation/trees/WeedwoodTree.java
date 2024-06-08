package thebetweenlands.common.generation.trees;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.blocks.BetweenlandsVineBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class WeedwoodTree {

	// Pass this function a level and it'll generate a weedwood tree
	// Using WorldGenLevel
	@SuppressWarnings("deprecation")
	public static boolean generate(WorldGenLevel level, BlockPos blockpos, Random random, boolean watersafe) {
		
		// Trunk (use level rand to prevent client server de-sync)
		double trunkwidth = 9 + (random.nextInt(2) * 2);
		double trunkheight = 15 + random.nextInt(5);
		double trunkradius = trunkwidth * 0.5;
		
		// Bulb
		double bulbwidth = 16;
		double bulbradius = bulbwidth * 0.5;
		
		int translatex = blockpos.getX();
		int translatey = blockpos.getY();
		int translatez = blockpos.getZ();
		
		// Other
		boolean poisonivy = random.nextInt(6) == 0;
		int bulbstart1 = (int) (trunkheight / 2 + 5);
		int bulbstart2 = (int) (trunkheight / 2 + 4);
		
		if (!level.isAreaLoaded(blockpos, 16)) {
			return false;
		}
		
		// check for room to grow (only checks stem and leaf bulb)
		boolean clear = true;
		trunkaircheckloop:
		// Trunk
		for (int y = 0; y < trunkheight; y++)  {
			for (int x = (int) -trunkradius; x <= trunkradius; x++) {
				for (int z = (int) -trunkradius; z <= trunkradius; z++) {
					// Checks to ownly break soft blocks and water tagged fluids
					BlockPos scanpos = new BlockPos(x + translatex, y + translatey, z + translatez);
					BlockState scanstate = level.getBlockState(scanpos);
					
					if (scanstate.getDestroySpeed(level, scanpos) > 0.5 && !(scanstate.getFluidState().is(FluidTags.WATER) && scanstate.getDestroySpeed(level, scanpos) == 100)) {
						clear = false;
						break trunkaircheckloop;
					}
				}
			}
		}
		bulbaircheckloop:
		for (int y = bulbstart2; y < trunkheight; y++)  {
			for (int x = (int) -bulbradius; x <= bulbradius; x++) {
				for (int z = (int) -bulbradius; z <= bulbradius; z++) {
					// Checks to ownly break soft blocks and water tagged fluids
					BlockPos scanpos = new BlockPos(x + translatex, y + translatey, z + translatez);
					BlockState scanstate = level.getBlockState(scanpos);
					if (scanstate.getDestroySpeed(level, scanpos) > 0.5 && !(scanstate.getFluidState().is(FluidTags.WATER) && scanstate.getDestroySpeed(level, scanpos) == 100)) {
						clear = false;
						break bulbaircheckloop;
					}
				}
			}
		}
		// If failed
		if (!clear) {
			return clear;
		}
		// Place blocks
		
		// leaves
		// y and z flipped for hangers
		for (int x = (int) -bulbradius-2; x <= bulbradius+2; x++) {
			for (int z = (int) -bulbradius-2; z <= bulbradius+2; z++) {
				int rand = random.nextInt(3);
				for (int y = (int) (trunkheight / 2 + 5)-2; y <= trunkheight + 1; y++) {
					
					// Weedwood leaves cover
					double dist = Math.sqrt(Math.pow(x, 2) + Math.pow((y - bulbstart1)*1.95, 2) + Math.pow(z, 2));
					BlockPos pos = new BlockPos(x + translatex, y + translatey , z + translatez);
					BlockState blockstate = level.getBlockState(pos);
					
					if (blockstate.getDestroySpeed(level, pos) <= 0.5 || (blockstate.getFluidState().is(FluidTags.WATER) && blockstate.getDestroySpeed(level, pos) == 100)) {
						if (dist <= bulbradius + 2 && y > (int) bulbstart2 - rand && (y <= bulbstart1 || random.nextInt(4) < 3)) {
							level.setBlock(pos, BlockRegistry.LEAVES_WEEDWOOD_TREE.get().defaultBlockState(), 0);
						}
						// Poison ivy from the bulb
						else if (dist <= bulbradius + 2 && y == (int) bulbstart2 && rand == 0 && random.nextInt(4) < 3 && poisonivy) {
							level.setBlock(pos, BlockRegistry.POISON_IVY.get().defaultBlockState().setValue(BetweenlandsVineBlock.UP, true), 0);
						}
					}
				}
			}
		}
		
		// Bulb
		for (int y = bulbstart1; y <= trunkheight + 1; y++) {
			for (int x = (int) -bulbradius; x <= bulbradius; x++) {
				for (int z = (int) -bulbradius; z <= bulbradius; z++) {
					// Weedwood Bulb
					double dist = Math.sqrt(Math.pow(x, 2) + Math.pow((y - bulbstart1)*2, 2) + Math.pow(z, 2));
					if (dist <= bulbradius) {
						level.setBlock(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState(), 0);
					}
				}
			}
		}
		
		// Trunk
		double radius = trunkradius;
		for (int y = 0; y < trunkheight; y++) {
			
			// Iterate every 3 steps untill trunk radius equals 2 (also skip the first step down)
			if (y % 3 == 0 && radius > 2 && y != 3) {
				radius--;
			}
			
			for (int x = (int) -radius; x <= radius; x++) {
				for (int z = (int) -radius; z <= radius; z++) {
					// Weedwood Trunk
					double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
					if (dist <= radius) {
						level.setBlock(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD_BARK_LOG.get().defaultBlockState(), 0);
					}
					
					if (dist <= radius - 1) {
						// dont generate exposed weedwood on bulb
						if (y >= (int) (trunkheight / 2 + 4)) {
							level.setBlock(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState(), 0);
							continue;
						}
						
						level.setBlock(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD.get().defaultBlockState(), 0);
					}
				}
			}
		}
		
		// Roots
		radius = trunkradius;
		for (int angmod = 0; angmod < 8; angmod++) {
			int y = random.nextInt(2);
			int prex = 0;
			int prez = 0;
			for (int length = 0; length < 5; length++) {
				int x = (int) Math.round((Math.sin(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 0.5)));
				int z = (int) Math.round((Math.cos(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 0.5)));
				if (prex == x && prez == z) {
					continue;
				}
				prex = x;
				prez = z;
				
				if (length > random.nextInt(4)) {
					y--;
				}
				BlockPos pos = new BlockPos(x + translatex, y + translatey , z + translatez);
				BlockState topstate = level.getBlockState(pos);
				BlockState belowstate = level.getBlockState(pos.below());
				
				// Checks to ownly break soft blocks and water tagged fluids
				if (topstate.getDestroySpeed(level, pos) <= 0.5 || (topstate.getFluidState().is(FluidTags.WATER) && topstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlock(pos, BlockRegistry.WEEDWOOD_BARK_LOG.get().defaultBlockState(), 0);
				}
				if (belowstate.getDestroySpeed(level, pos.below()) <= 0.5 || (belowstate.getFluidState().is(FluidTags.WATER) && belowstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlock(pos.below(), BlockRegistry.WEEDWOOD_BARK_LOG.get().defaultBlockState(), 0);
				}
			}
		}
		
		// Branches
		radius = trunkradius;
		for (int angmod = 0; angmod < 8; angmod++) {
			int y = (int) (trunkheight * 0.7 + random.nextInt(2));
			int prex = 0;
			int prez = 0;
			for (int length = 0; length < 5; length++) {
				int x = (int) Math.round((Math.sin(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 2)));
				int z = (int) Math.round((Math.cos(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 2)));
				if (prex == x && prez == z) {
					continue;
				}
				prex = x;
				prez = z;
				
				if (length > random.nextInt(4)) {
					y++;
				}
				BlockPos pos = new BlockPos(x + translatex, y + translatey , z + translatez);
				BlockState topstate = level.getBlockState(pos);
				BlockState belowstate = level.getBlockState(pos.below());
				
				// Checks to ownly break soft blocks and water tagged fluids
				if (topstate.getDestroySpeed(level, pos) <= 0.5 || (topstate.getFluidState().is(FluidTags.WATER) && topstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlock(pos, BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState(), 0);
				}
				if (belowstate.getDestroySpeed(level, pos.below()) <= 0.5 || (belowstate.getFluidState().is(FluidTags.WATER) && belowstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlock(pos.below(), BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState(), 0);
				}
			}
		}
		
		return clear;
	}
	
	// Using LevelAccessor
	@SuppressWarnings("deprecation")
	public static boolean generate(Level level, BlockPos blockpos, Random random) {
		
		// Trunk (use level rand to prevent client server de-sync)
		double trunkwidth = 9 + (random.nextInt(2) * 2);
		double trunkheight = 15 + random.nextInt(5);
		double trunkradius = trunkwidth * 0.5;
		
		// Bulb
		double bulbwidth = 16;
		double bulbradius = bulbwidth * 0.5;
		
		int translatex = blockpos.getX();
		int translatey = blockpos.getY();
		int translatez = blockpos.getZ();
		
		// Other
		boolean poisonivy = random.nextInt(6) == 0;
		int bulbstart1 = (int) (trunkheight / 2 + 5);
		int bulbstart2 = (int) (trunkheight / 2 + 4);
		
		if (!level.isAreaLoaded(blockpos, 16)) {
			return false;
		}
		
		// check for room to grow (only checks stem and leaf bulb)
		boolean clear = true;
		trunkaircheckloop:
		// Trunk
		for (int y = 0; y < trunkheight; y++)  {
			for (int x = (int) -trunkradius; x <= trunkradius; x++) {
				for (int z = (int) -trunkradius; z <= trunkradius; z++) {
					// Checks to ownly break soft blocks and water tagged fluids
					BlockPos scanpos = new BlockPos(x + translatex, y + translatey, z + translatez);
					BlockState scanstate = level.getBlockState(scanpos);
					
					if (scanstate.getDestroySpeed(level, scanpos) > 0.5 && !(scanstate.getFluidState().is(FluidTags.WATER) && scanstate.getDestroySpeed(level, scanpos) == 100)) {
						clear = false;
						break trunkaircheckloop;
					}
				}
			}
		}
		bulbaircheckloop:
		for (int y = bulbstart2; y < trunkheight; y++)  {
			for (int x = (int) -bulbradius; x <= bulbradius; x++) {
				for (int z = (int) -bulbradius; z <= bulbradius; z++) {
					// Checks to ownly break soft blocks and water tagged fluids
					BlockPos scanpos = new BlockPos(x + translatex, y + translatey, z + translatez);
					BlockState scanstate = level.getBlockState(scanpos);
					if (scanstate.getDestroySpeed(level, scanpos) > 0.5 && !(scanstate.getFluidState().is(FluidTags.WATER) && scanstate.getDestroySpeed(level, scanpos) == 100)) {
						clear = false;
						break bulbaircheckloop;
					}
				}
			}
		}
		// If failed
		if (!clear) {
			return clear;
		}
		// Place blocks
		
		// leaves
		// x y and z flipped for hangers
		for (int x = (int) -bulbradius-2; x <= bulbradius+2; x++) {
			for (int z = (int) -bulbradius-2; z <= bulbradius+2; z++) {
				int rand = random.nextInt(3);
				for (int y = (int) (trunkheight / 2 + 5)-2; y <= trunkheight + 1; y++) {
					
					// Weedwood leaves cover
					double dist = Math.sqrt(Math.pow(x, 2) + Math.pow((y - bulbstart1)*1.95, 2) + Math.pow(z, 2));
					BlockPos pos = new BlockPos(x + translatex, y + translatey , z + translatez);
					BlockState blockstate = level.getBlockState(pos);
					
					if (blockstate.getDestroySpeed(level, pos) <= 0.5 || (blockstate.getFluidState().is(FluidTags.WATER) && blockstate.getDestroySpeed(level, pos) == 100)) {
						if (dist <= bulbradius + 2 && y > (int) bulbstart2 - rand && (y <= bulbstart1 || random.nextInt(4) < 3)) {
							level.setBlockAndUpdate(pos, BlockRegistry.LEAVES_WEEDWOOD_TREE.get().defaultBlockState());
						}
						// Poison ivy from the bulb
						else if (dist <= bulbradius + 2 && y == (int) bulbstart2 && rand == 0 && random.nextInt(4) < 3 && poisonivy) {
							level.setBlockAndUpdate(pos, BlockRegistry.POISON_IVY.get().defaultBlockState().setValue(BetweenlandsVineBlock.UP, true));
						}
					}
				}
			}
		}
		
		// Bulb
		for (int y = bulbstart1; y <= trunkheight + 1; y++) {
			for (int x = (int) -bulbradius; x <= bulbradius; x++) {
				for (int z = (int) -bulbradius; z <= bulbradius; z++) {
					// Weedwood Bulb
					double dist = Math.sqrt(Math.pow(x, 2) + Math.pow((y - bulbstart1)*2, 2) + Math.pow(z, 2));
					if (dist <= bulbradius) {
						level.setBlockAndUpdate(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState());
					}
				}
			}
		}
		
		// Trunk
		double radius = trunkradius;
		for (int y = 0; y < trunkheight; y++) {
			
			// Iterate every 3 steps untill trunk radius equals 2 (also skip the first step down)
			if (y % 3 == 0 && radius > 2 && y != 3) {
				radius--;
			}
			
			for (int x = (int) -radius; x <= radius; x++) {
				for (int z = (int) -radius; z <= radius; z++) {
					// Weedwood Trunk
					double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
					if (dist <= radius) {
						level.setBlockAndUpdate(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD_BARK_LOG.get().defaultBlockState());
					}
					
					if (dist <= radius - 1) {
						// dont generate exposed weedwood on bulb
						if (y >= (int) (trunkheight / 2 + 4)) {
							level.setBlockAndUpdate(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState());
							continue;
						}
						
						level.setBlockAndUpdate(new BlockPos(x + translatex, y + translatey, z + translatez), BlockRegistry.WEEDWOOD.get().defaultBlockState());
					}
				}
			}
		}
		
		// Roots
		radius = trunkradius;
		for (int angmod = 0; angmod < 8; angmod++) {
			int y = random.nextInt(2);
			int prex = 0;
			int prez = 0;
			for (int length = 0; length < 5; length++) {
				int x = (int) Math.round((Math.sin(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 0.5)));
				int z = (int) Math.round((Math.cos(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 0.5)));
				if (prex == x && prez == z) {
					continue;
				}
				prex = x;
				prez = z;
				
				if (length > random.nextInt(4)) {
					y--;
				}
				BlockPos pos = new BlockPos(x + translatex, y + translatey , z + translatez);
				BlockState topstate = level.getBlockState(pos);
				BlockState belowstate = level.getBlockState(pos.below());
				
				// Checks to ownly break soft blocks and water tagged fluids
				if (topstate.getDestroySpeed(level, pos) <= 0.5 || (topstate.getFluidState().is(FluidTags.WATER) && topstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlockAndUpdate(pos, BlockRegistry.WEEDWOOD_BARK_LOG.get().defaultBlockState());
				}
				if (belowstate.getDestroySpeed(level, pos.below()) <= 0.5 || (belowstate.getFluidState().is(FluidTags.WATER) && belowstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlockAndUpdate(pos.below(), BlockRegistry.WEEDWOOD_BARK_LOG.get().defaultBlockState());
				}
			}
		}
		
		// Branches
		radius = trunkradius;
		for (int angmod = 0; angmod < 8; angmod++) {
			int y = (int) (trunkheight * 0.7 + random.nextInt(2));
			int prex = 0;
			int prez = 0;
			for (int length = 0; length < 5; length++) {
				int x = (int) Math.round((Math.sin(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 2)));
				int z = (int) Math.round((Math.cos(Math.toRadians((float)angmod * 45)) * (trunkradius + length - 2)));
				if (prex == x && prez == z) {
					continue;
				}
				prex = x;
				prez = z;
				
				if (length > random.nextInt(4)) {
					y++;
				}
				BlockPos pos = new BlockPos(x + translatex, y + translatey , z + translatez);
				BlockState topstate = level.getBlockState(pos);
				BlockState belowstate = level.getBlockState(pos.below());
				
				// Checks to ownly break soft blocks and water tagged fluids
				if (topstate.getDestroySpeed(level, pos) <= 0.5 || (topstate.getFluidState().is(FluidTags.WATER) && topstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlockAndUpdate(pos, BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState());
				}
				if (belowstate.getDestroySpeed(level, pos.below()) <= 0.5 || (belowstate.getFluidState().is(FluidTags.WATER) && belowstate.getDestroySpeed(level, pos) == 100)) {
					level.setBlockAndUpdate(pos.below(), BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState());
				}
			}
		}
		
		return clear;
	}
}
