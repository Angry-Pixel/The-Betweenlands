package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenWaystone extends WorldGenerator implements IWorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		if(SurfaceType.MIXED_GROUND.matches(world.getBlockState(pos.down()))) {
			IBlockState state1 = world.getBlockState(pos);
			IBlockState state2 = world.getBlockState(pos.up());
			IBlockState state3 = world.getBlockState(pos.up(2));

			if(!state1.getMaterial().isLiquid() && state1.getBlock().isReplaceable(world, pos)
					&& !state2.getMaterial().isLiquid() && state2.getBlock().isReplaceable(world, pos.up())
					&& !state3.getMaterial().isLiquid() && state3.getBlock().isReplaceable(world, pos.up(2))) {

				IBlockState state = BlockRegistry.WAYSTONE.getDefaultState();

				world.setBlockState(pos, state.withProperty(BlockWaystone.PART, BlockWaystone.Part.BOTTOM));
				world.setBlockState(pos.up(2), state.withProperty(BlockWaystone.PART, BlockWaystone.Part.TOP));
				world.setBlockState(pos.up(1), state.withProperty(BlockWaystone.PART, BlockWaystone.Part.MIDDLE));

				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
				AxisAlignedBB locationAABB = new AxisAlignedBB(pos.up()).grow(4, 3, 4);
				LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos.getX(), pos.getZ()), "waystone", EnumLocationType.WAYSTONE);
				locationStorage.setSeed(rand.nextLong());
				locationStorage.addBounds(locationAABB);
				locationStorage.setVisible(false);
				locationStorage.setDirty(true);
				worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);
			}
		}
		return false;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			int gridSizePower2 = 3;

			int gridBlockSize = 1 << (gridSizePower2 + 4);

			int regionX = chunkX >> gridSizePower2;
			int regionZ = chunkZ >> gridSizePower2;

			long regionSeed = (long)(regionX * 3129871) ^ (long)regionZ * 116129781L ^ world.getSeed();
			regionSeed = regionSeed * regionSeed * 42317861L + regionSeed * 11L;

			Random seededRandom = new Random(regionSeed);

			int blockX = (regionX << (gridSizePower2 + 4)) + gridBlockSize / 4 + seededRandom.nextInt(gridBlockSize / 2 + 1);
			int blockZ = (regionZ << (gridSizePower2 + 4)) + gridBlockSize / 4 + seededRandom.nextInt(gridBlockSize / 2 + 1);

			if(blockX > chunkX * 16 + 5 && blockZ > chunkZ * 16 + 5 && blockX < (chunkX + 2) * 16 - 5 && blockZ < (chunkZ + 2) * 16 - 5) {
				int height = world.getHeight(blockX, blockZ);
				this.generate(world, random, new BlockPos(blockX, height, blockZ));
			}
		}
	}
}
