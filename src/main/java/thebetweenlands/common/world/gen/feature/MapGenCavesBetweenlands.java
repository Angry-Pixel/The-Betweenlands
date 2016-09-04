package thebetweenlands.common.world.gen.feature;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.OpenSimplexNoise;

public class MapGenCavesBetweenlands extends MapGenBase {
    private static final int CHUNK_SIZE = 16;

    private static final double XZ_CAVE_SCALE = 0.08;

    private static final double Y_CAVE_SCALE = 0.15;

    private static final double XZ_FORM_SCALE = 0.5;

    private static final double Y_FORM_SCALE = 0.3;

    private static final double FORM_SCALE = 0.4;

    private static final double XZ_BREAK_SCALE = 0.05;

    private static final double BREAK_SCALE = 0.85;

    private static final double BASE_LIMIT = -0.3;

    private static final int LOWER_BOUND = 10;

    private static final int UPPER_BOUND = 20;

    private static final double SHOULDNT_BREAK = 3.5;

    private static final double RIDGE_EXTENTS = 0.5;

    private OpenSimplexNoise cave;

    private OpenSimplexNoise seaLevelBreak;

    private FractalOpenSimplexNoise form;

    private List<BiomeBetweenlands> noBreakBiomes;

    private float terrainWeights[];

    public MapGenCavesBetweenlands(long seed) {
        cave = new OpenSimplexNoise(seed);
        seaLevelBreak = new OpenSimplexNoise(seed + 1);
        form = new FractalOpenSimplexNoise(seed + 2, 4, 0.1);
        noBreakBiomes = Lists.newArrayList(BiomeRegistry.DEEP_WATERS, BiomeRegistry.COARSE_ISLANDS, BiomeRegistry.MARSH_0, BiomeRegistry.MARSH_1, BiomeRegistry.PATCHY_ISLANDS, BiomeRegistry.SLUDGE_PLAINS);
    }

    public void setBiomeTerrainWeights(float[] terrainWeights) {
        this.terrainWeights = terrainWeights;
    }

    @Override
    public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
        int cx = chunkX * CHUNK_SIZE;
        int cz = chunkZ * CHUNK_SIZE;
        MutableBlockPos pos = new MutableBlockPos();
        for (int bx = 0; bx < CHUNK_SIZE; bx++) {
            for (int bz = 0; bz < CHUNK_SIZE; bz++) {
                int x = cx + bx, z = cz + bz;
                Biome biome = world.getBiomeGenForCoords(pos.setPos(cx + bx, 0, cz + bz));
                // Only break in correct biomes and don't generate in biome transitions
                double shouldntBreak = noBreakBiomes.contains(biome) ? SHOULDNT_BREAK : (1 - terrainWeights[bz + bx * 16]) * SHOULDNT_BREAK;
                int level = 0;
                while (level <= WorldProviderBetweenlands.LAYER_HEIGHT) {
                    IBlockState state = primer.getBlockState(bx, level, bz);
                    if (state.getMaterial() == Material.AIR || state.getMaterial().isLiquid()) {
                        break;
                    }
                    level++;
                }
                for (int y = 0; y <= level; y++) {
                    double noise = cave.eval(x * XZ_CAVE_SCALE, y * Y_CAVE_SCALE, z * XZ_CAVE_SCALE) + form.eval(x * XZ_FORM_SCALE, y * Y_FORM_SCALE, z * XZ_FORM_SCALE) * FORM_SCALE;
                    double limit = BASE_LIMIT;
                    if (y <= LOWER_BOUND) {
                        limit = (limit + 1) / LOWER_BOUND * y - 1;
                    }
                    int surfaceDist = level - y;
                    if (surfaceDist <= UPPER_BOUND) {
                        noise += (shouldntBreak + MathUtils.linearTransformd(seaLevelBreak.eval(x * XZ_BREAK_SCALE, z * XZ_BREAK_SCALE), -1, 1, 0, 1)) * BREAK_SCALE * (1 - surfaceDist / (float) UPPER_BOUND);
                    }
                    if (y == level) {   
                        primer.setBlockState(bx, 150, bz, Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.byMetadata((int) MathUtils.linearTransformd(noise, -0.5F, 1, 0, 15))));
                    }
                    if (noise < limit) {
                        primer.setBlockState(bx, y, bz, y > WorldProviderBetweenlands.CAVE_WATER_HEIGHT ? Blocks.AIR.getDefaultState() : BlockRegistry.SWAMP_WATER.getDefaultState());
                    }
                }
            }
        }
    }
}
