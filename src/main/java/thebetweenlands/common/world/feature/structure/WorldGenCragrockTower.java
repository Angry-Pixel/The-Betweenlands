package thebetweenlands.common.world.feature.structure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.feature.WorldGenHelper;

import java.util.Random;

public class WorldGenCragrockTower extends WorldGenHelper {
    private static IBlockState CRAGROCK;
    private static IBlockState MOSSY_CRAGROCK_TOP;
    private static IBlockState MOSSY_CRAGROCK_BOTTOM;
    private static IBlockState CRAGROCK_BRICKS;
    private static IBlockState SMOOTH_CRAGROCK_STAIRS;
    private static IBlockState CRAGROCK_BRICK_SLAB;
    private static IBlockState SMOOTH_CRAGROCK_SLAB;
    private static IBlockState CRAGROCK_BRICK_STAIRS;
    private static IBlockState CRAGROCK_PILLAR;
    private static IBlockState SMOOTH_CRAGROCK;
    private static IBlockState CARVED_CRAGROCK;
    private static IBlockState ROOT;
    private static IBlockState SMOOTH_BETWEENSTONE_WALL;
    private static IBlockState CRAGROCK_BRICK_WALL;
    private static IBlockState SMOOTH_CRAGROCK_WALL;
    private static IBlockState GLOWING_SMOOTH_CRAGROCK;
    private static IBlockState WISP;

    public WorldGenCragrockTower() {
        super(17, 64, 19);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (worldIn.getBlockState(position).getBlock() == Blocks.AIR && position.getY() > 80)
            position.add(0, -1, 0);
        CRAGROCK = BlockRegistry.CRAGROCK.getDefaultState();
        MOSSY_CRAGROCK_TOP = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, BlockCragrock.EnumCragrockType.MOSSY_1);
        MOSSY_CRAGROCK_BOTTOM = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, BlockCragrock.EnumCragrockType.MOSSY_2);
        CRAGROCK_BRICKS = BlockRegistry.CRAGROCK_BRICKS.getDefaultState();




        return tower(worldIn, rand, position.getX(), position.getY(), position.getZ());
    }

    private boolean tower(World world, Random random, int x, int y, int z) {
        return true;
    }


}
