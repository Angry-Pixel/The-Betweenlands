package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;

public class WorldGenDruidCircle implements IWorldGenerator {
	private static final IBlockState[] RUNE_STONES = {
			BlockRegistry.DRUID_STONE_1.getDefaultState(),
			BlockRegistry.DRUID_STONE_2.getDefaultState(),
			BlockRegistry.DRUID_STONE_3.getDefaultState(),
			BlockRegistry.DRUID_STONE_4.getDefaultState(),
			BlockRegistry.DRUID_STONE_5.getDefaultState()
	};
	private final int height = 4;
	private final int baseRadius = 6;
	private final int checkRadius = 32;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			this.generate(world, random, chunkX * 16, chunkZ * 16);
		}
	}

	private void generate(World world, Random random, int startX, int startZ) {
		BlockPos genPos = null;
		MutableBlockPos pos = new MutableBlockPos();

		//Try to find a suitable location
		check:
		for (int xo = this.baseRadius + 1; xo <= this.checkRadius - (this.baseRadius + 1); xo++) {
			for (int zo = this.baseRadius + 1; zo <= this.checkRadius - (this.baseRadius + 1); zo++) {
				int x = startX + xo;
				int z = startZ + zo;
				if(world.isAreaLoaded(new BlockPos(x - baseRadius - 2, 64, z - baseRadius - 2), new BlockPos(x + baseRadius + 3, 64, z + baseRadius + 3))) {
					pos.setPos(x, 0, z);
					Biome biome = world.getBiome(pos);
					if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) {
						int newY = world.getHeight(pos).getY() - 1;
						pos.setY(newY);
						IBlockState block = world.getBlockState(pos);
						if (block == biome.topBlock) {
							if(this.canGenerateAt(world, pos.up())) {
								genPos = pos.up();
								break check;
							}
						}
					}
				}
			}
		}

		if(genPos != null && random.nextInt(BetweenlandsConfig.WORLD_AND_DIMENSION.druidCircleFrequency) == 0) {
			generateStructure(world, random, genPos);
		}
	}

	public boolean canGenerateAt(World world, BlockPos altar) {
		for (BlockPos p : BlockPos.getAllInBox(altar.add(-this.baseRadius, 1, -this.baseRadius), altar.add(this.baseRadius, this.height, this.baseRadius))) {
			if (!world.isAirBlock(p) && !world.getBlockState(p).getBlock().isReplaceable(world, p)) {
				return false;
			}
		}
		return true;
	}

	public void generateStructure(World world, Random rand, BlockPos altar) {
		// circle
		MutableBlockPos pos = new MutableBlockPos();
		IBlockState ground = world.getBiome(altar).topBlock;
		IBlockState filler = world.getBiome(altar).fillerBlock;
		int altarX = altar.getX(), altarY = altar.getY(), altarZ = altar.getZ();
		for (int x = -this.baseRadius; x <= this.baseRadius; x++) {
			for (int z = -this.baseRadius; z <= this.baseRadius; z++) {
				pos.setPos(altarX + x, altarY, altarZ + z);
				int dSq = (int) Math.round(Math.sqrt(x * x + z * z));
				if (dSq == this.baseRadius) {
					if (x % 2 == 0 && z % 2 == 0) {
						placePillar(world, pos, rand);
					} else {
						placeAir(world, pos);
					}
				}
				if (dSq <= this.baseRadius) {
					for(int yo = 0; yo < 16; yo++) {
						Biome biome = world.getBiomeForCoordsBody(pos);
						IBlockState blockState = world.getBlockState(pos);
						if(blockState == biome.fillerBlock || blockState == biome.topBlock || blockState.getMaterial() == Material.ROCK || blockState.getMaterial() == Material.GROUND) {
							world.setBlockToAir(pos.toImmutable());
						}
						pos.setY(pos.getY() + 1);
					}

					pos.setY(altarY - 1);
					world.setBlockState(pos.toImmutable(), ground);

					int offset = world.rand.nextInt(2);
					if(world.isAirBlock(pos.down(2)) || world.getBlockState(pos.down(2)).getMaterial().isLiquid()) {
						offset -= 1;
					}
					for(int yo = 0; yo < 10; yo++) {
						if (dSq <= this.baseRadius / 10.0F * (10 - yo) + offset) {
							pos.setY(altarY - 2 - yo);
							world.setBlockState(pos.toImmutable(), filler);
						}
					}
				}
			}
		}
		world.setBlockState(altar, BlockRegistry.DRUID_ALTAR.getDefaultState());
		world.setBlockState(altar.down(), BlockRegistry.MOB_SPAWNER.getDefaultState());
		TileEntity te = world.getTileEntity(altar.down());
		if(te instanceof TileEntityMobSpawnerBetweenlands) {
			MobSpawnerLogicBetweenlands logic = ((TileEntityMobSpawnerBetweenlands)te).getSpawnerLogic();
			logic.setNextEntityName("thebetweenlands:dark_druid").setCheckRange(32.0D).setSpawnRange(6).setSpawnInAir(false).setMaxEntities(1 + world.rand.nextInt(3));
		}
	}

	private void placeAir(World world, MutableBlockPos pos) {
		Biome biome = world.getBiome(pos);
		for (int k = 0, y = pos.getY(); k <= this.height; k++, pos.setY(y + k)) {
			IBlockState blockState = world.getBlockState(pos);
			if(blockState == biome.fillerBlock || blockState == biome.topBlock || blockState.getMaterial() == Material.ROCK || blockState.getMaterial() == Material.GROUND) {
				world.setBlockToAir(pos.toImmutable());
			}
		}
	}

	private void placePillar(World world, MutableBlockPos pos, Random rand) {
		int height = rand.nextInt(3) + 3;
		for (int k = 0, y = pos.getY(); k <= height; k++, pos.setY(y + k)) {
			EnumFacing facing = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
			if (rand.nextBoolean()) {
				world.setBlockState(pos.toImmutable(), getRandomRuneBlock(rand).withProperty(BlockDruidStone.FACING, facing), 3);
			} else {
				world.setBlockState(pos.toImmutable(), BlockRegistry.DRUID_STONE_6.getDefaultState().withProperty(BlockDruidStone.FACING, facing));
				for (int vineCount = 0; vineCount < 4; vineCount++) {
					setRandomFoliage(world, pos, rand);
				}
			}
		}
	}

	private void setRandomFoliage(World world, BlockPos pos, Random rand) {
		EnumFacing facing = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
		BlockPos side = pos.toImmutable().offset(facing);
		if (world.isAirBlock(side)) {
			world.setBlockState(side, Blocks.VINE.getDefaultState().withProperty(BlockVine.getPropertyFor(facing.getOpposite()), true));
		}
	}

	private IBlockState getRandomRuneBlock(Random rand) {
		return RUNE_STONES[rand.nextInt(RUNE_STONES.length)];
	}
}