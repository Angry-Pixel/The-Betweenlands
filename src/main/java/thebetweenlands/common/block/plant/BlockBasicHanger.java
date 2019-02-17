package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockBasicHanger extends BlockBush {

    public BlockBasicHanger() {
        super(Material.PLANTS);
        setHardness(0.0F);
        setCreativeTab(BLCreativeTabs.PLANTS);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);

    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isAirBlock(pos.down()) && canBlockStay(world, pos.down(), state) && rand.nextInt(8) == 0)
            world.setBlockState(pos.down(), this.getDefaultState());
    }

    @Override
    public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
        return 0;
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return isValidBlock(worldIn.getBlockState(pos.down())) && canBlockStay(worldIn, pos, worldIn.getBlockState(pos));
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return isValidBlock(worldIn.getBlockState(pos.up()));
    }

    protected boolean isValidBlock(IBlockState block) {
        return block.getMaterial().blocksMovement() || block.getBlock() == BlockRegistry.LEAVES_WEEDWOOD_TREE || block.getBlock() instanceof BlockBasicHanger;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
        return new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
    }
}
