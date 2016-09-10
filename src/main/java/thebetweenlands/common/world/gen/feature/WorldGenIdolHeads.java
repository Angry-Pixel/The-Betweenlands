package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockCragrock.EnumCragrockType;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.registries.LootTableRegistry;


public class WorldGenIdolHeads extends WorldGenerator {
	private int length = -1;
	private int width = -1;
	private int height = -1;
	private int direction = -1;
	private IBlockState solid = BlockRegistry.SMOOTH_CRAGROCK.getDefaultState();
	private IBlockState slab = BlockRegistry.SMOOTH_CRAGROCK_SLAB.getDefaultState();
	private IBlockState stairs = BlockRegistry.SMOOTH_CRAGROCK_STAIRS.getDefaultState();
	private IBlockState octine = BlockRegistry.OCTINE_BLOCK.getDefaultState();

	private final WorldGenerator crackGen = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK.getDefaultState(), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK));
	private final WorldGenerator crackGenSlabs = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK_BRICK_SLAB.getDefaultState(), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK_SLAB)).setInheritProperties(true);
	private final WorldGenerator crackGenStairs = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK_BRICK_STAIRS.getDefaultState(), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK_STAIRS)).setInheritProperties(true);
	private final WorldGenerator mossGen = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_2), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK));
	private final WorldGenerator lichenClusterGen = new WorldGenMossCluster(BlockRegistry.LICHEN.getDefaultState());
	private final WorldGenerator mossClusterGen = new WorldGenMossCluster(BlockRegistry.MOSS.getDefaultState());

	public WorldGenIdolHeads() {
		length = 8;
		width = 8;
		height = 8;
	}

	public boolean generateStructure(World world, Random rand, int x, int y, int z) {
		// air check
		MutableBlockPos checkPos = new MutableBlockPos();
		for (int xx = x - length / 2; xx <= x + length / 2; ++xx) {
			for(int zz = z - width / 2; zz <= z + width / 2; ++zz) {
				for(int yy = y + 1; yy < y + height; ++yy ) {
					if(!world.isAirBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz))) {
						return false;
					}
				}
				for (int yy = y; yy >= y - 5; yy--) {
					if(!world.isAirBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz)))
						break;
					if(yy <= y - 5)
						return false;
				}
			}
		}

		direction = rand.nextInt(4);
		int headType = rand.nextInt(3);
		int xx = x - length / 2;
		int zz = z - width / 2;

		switch (headType) {
		case 0:// Gold Head
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 0, 3, 4, solid, 1, 2, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 7, 3, 4, solid, 1, 2, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 5, 1, solid, 6, 2, 6, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 3, solid, 6, 1, 4, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 3, 0, solid, 2, 4, 3, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 3, 1, solid, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 3, 1, solid, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 1, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 3, solid, 2, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 6, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 6, 7, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 4, 7, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 4, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 4, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 1, 1, slab, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 2, 1, slab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 1, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH), 1, 1, 1, direction); //bottom right
			rotatedCubeVolume(world, rand, xx, y, zz, 6, 1, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH), 1, 1, 1, direction); //bottom left
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 2, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top right
			rotatedCubeVolume(world, rand, xx, y, zz, 6, 2, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top left
			break;

		case 1:// Silver Head
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 0, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 7, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 1, 7, solid, 2, 5, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 3, solid, 6, 3, 4, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 7, 1, solid, 2, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 6, 1, slab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 2, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 1, slab, 2, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 7, 1, solid, 2, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 6, 1, slab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 2, 1, 2, direction); 
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 3, 0, solid, 2, 3, 3, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 6, 1, solid, 2, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 3, 1, solid, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 1, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH), 1, 1, 1, direction); //bottom right
			rotatedCubeVolume(world, rand, xx, y, zz, 4, 1, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH), 1, 1, 1, direction); //bottom left
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 2, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top right
			rotatedCubeVolume(world, rand, xx, y, zz, 4, 2, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top left
			break;

		case 2://Bronze Head
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 0, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 7, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 2, 7, solid, 4, 4, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 6, 1, solid, 6, 1, 6, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 7, 1, solid, 4, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 7, 4, solid, 4, 1, 2, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 3, solid, 6, 2, 4, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 3, 0, solid, 2, 4, 3, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 3, 1, solid, 4, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 1, 1, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 3, 2, 1, slab.withProperty(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 1, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH), 1, 1, 1, direction); //bottom right
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 1, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH), 1, 1, 1, direction); //bottom left
			rotatedCubeVolume(world, rand, xx, y, zz, 2, 2, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top right
			rotatedCubeVolume(world, rand, xx, y, zz, 5, 2, 1, 
					direction == 0 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.EAST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.WEST).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.SOUTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.withProperty(BlockStairsBetweenlands.FACING, EnumFacing.NORTH).withProperty(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top left
			break;
		}

		for(int i = 0; i < 40 + rand.nextInt(160); i++) {
			int type = rand.nextInt(4);
			switch(type) {
			default:
			case 0:
				this.crackGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			case 1:
				this.crackGenSlabs.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			case 2:
				this.crackGenStairs.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			case 3:
				this.mossGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			}

			if(rand.nextInt(4) == 0) {
				if(rand.nextInt(8) == 0) {
					this.lichenClusterGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				} else {
					this.mossClusterGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				}
			}
		}

		TileEntityChestBetweenlands lootChest;
		world.setBlockState(new BlockPos(x, y - 1, z), BlockRegistry.WEEDWOOD_CHEST.getDefaultState());
		lootChest = (TileEntityChestBetweenlands) world.getTileEntity(new BlockPos(x, y - 1, z));
		if (lootChest != null)
			lootChest.setLootTable(LootTableRegistry.COMMON_CHEST_LOOT, rand.nextLong());
		return true;
	}

	public void rotatedCubeVolume(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, IBlockState blockType, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
		switch (direction) {
		case 0:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
					for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		case 1:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
					for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		case 2:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int xx = x + length - offsetA - 1; xx > x + length - offsetA - sizeWidth - 1; xx--)
					for (int zz = z + length - offsetC - 1; zz > z + length - offsetC - sizeDepth - 1; zz--)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		case 3:
			for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
				for (int zz = z + length - offsetA - 1; zz > z + length - offsetA - sizeWidth - 1; zz--)
					for (int xx = x + length - offsetC - 1; xx > x + length - offsetC - sizeDepth - 1; xx--)
						world.setBlockState(new BlockPos(xx, yy, zz), blockType, 2);
			break;
		}
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		return generateStructure(worldIn, rand, position.getX(), position.getY(), position.getZ());
	}
}
