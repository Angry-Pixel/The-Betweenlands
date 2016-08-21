package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.config.ConfigHandler;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenDruidCircle implements IWorldGenerator {
    private static final IBlockState[] DRUID_STONES = {
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
        MutableBlockPos pos = new MutableBlockPos();
        for (int xo = this.baseRadius + 1; xo <= this.checkRadius - this.baseRadius - 1; xo++) {
            for (int zo = this.baseRadius + 1; zo <= this.checkRadius - this.baseRadius - 1; zo++) {
                int x = startX + xo;
                int z = startZ + zo;
                pos.setPos(x, 0, z);
                Biome biome = world.getBiomeGenForCoords(pos);
                if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SWAMP)) {
                    int newY = world.getHeight(pos).getY();
                    pos.setY(newY);
                    IBlockState block = world.getBlockState(pos);
                    if (block.getBlock() == biome.topBlock.getBlock()) {
                        if (random.nextInt(ConfigHandler.druidCircleFrequency) == 0) {
                            if (generateStructure(world, random, pos.up())) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean generateStructure(World world, Random rand, BlockPos altar) {
        // air check
        for (BlockPos p : BlockPos.getAllInBox(altar.add(-this.baseRadius, 1, -this.baseRadius), altar.add(this.baseRadius, this.height, this.baseRadius))) {
            if (!world.isAirBlock(p)) {
                return false;
            }
        }
        // circle
        MutableBlockPos pos = new MutableBlockPos();
        IBlockState ground = world.getBiomeGenForCoords(altar).topBlock;
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
                    pos.setY(altarY - 1);
                    world.setBlockState(pos.toImmutable(), ground);
                }
            }
        }
        world.setBlockState(altar, BlockRegistry.DRUID_ALTAR.getDefaultState());
        world.setBlockState(altar.down(), BlockRegistry.DRUID_SPAWNER.getDefaultState());
        return true;
    }

    private void placeAir(World world, MutableBlockPos pos) {
        for (int k = 0, y = pos.getY(); k <= this.height; k++, pos.setY(y + k)) {
            world.setBlockToAir(pos);
        }
    }

    private void placePillar(World world, MutableBlockPos pos, Random rand) {
        int height = rand.nextInt(3) + 3;
        for (int k = 0, y = pos.getY(); k <= height; k++, pos.setY(y + k)) {
            EnumFacing facing = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
            if (rand.nextBoolean()) {
                world.setBlockState(pos, getRandomBlock(rand).withProperty(BlockDruidStone.FACING, facing), 3);
            } else {
                world.setBlockState(pos, Blocks.STONE.getDefaultState());
                for (int vineCount = 0; vineCount < 4; vineCount++) {
                    setRandomFoliage(world, pos, rand);
                }
            }
        }
    }

    private void setRandomFoliage(World world, BlockPos pos, Random rand) {
        EnumFacing facing = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
        BlockPos side = pos.offset(facing);
        if (world.isAirBlock(side)) {
            world.setBlockState(side, Blocks.VINE.getDefaultState().withProperty(BlockVine.getPropertyFor(facing.getOpposite()), true));
        }
    }

    private IBlockState getRandomBlock(Random rand) {
        return DRUID_STONES[rand.nextInt(DRUID_STONES.length)];
    }
}