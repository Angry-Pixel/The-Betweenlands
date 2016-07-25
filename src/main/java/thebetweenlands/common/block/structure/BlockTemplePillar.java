package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockTemplePillar extends BlockRotatedPillar {

    public BlockTemplePillar() {
        super(Material.ROCK);
        setHardness(1.5F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(AXIS)) {
            case Y:
                return new AxisAlignedBB(0.0625f, 0f, 0.0625f, .9375f, 1f, .9375f);
            case X:
                return new AxisAlignedBB(0f, 0.0625f, 0.0625f, 1f, .9375f, .9375f);
            default:
                return new AxisAlignedBB(0.0625f, 0.0625f, 0f, .9375f, .9375f, 1f);
        }
    }

    @Override
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
