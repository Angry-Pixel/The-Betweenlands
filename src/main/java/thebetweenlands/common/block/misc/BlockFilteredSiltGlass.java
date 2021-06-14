package thebetweenlands.common.block.misc;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.IConnectedTextureBlock;

public class BlockFilteredSiltGlass extends BlockGlassBetweenlands implements IConnectedTextureBlock {

	public BlockFilteredSiltGlass() {
		super(Material.GLASS);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT_MIPPED;
	}

    @Override
    public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
        IExtendedBlockState state = (IExtendedBlockState) oldState;
        return this.getExtendedConnectedTextureState(state, worldIn, pos, p -> worldIn.getBlockState(p).getBlock() instanceof BlockFilteredSiltGlass, false);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[0]));
    }
}
