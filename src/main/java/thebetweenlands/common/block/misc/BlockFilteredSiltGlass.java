package thebetweenlands.common.block.misc;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.IConnectedTextureBlock;

public class BlockFilteredSiltGlass extends BlockPaneBetweenlands implements IConnectedTextureBlock {
	
	public BlockFilteredSiltGlass() {
		super(Material.GLASS, true);
		this.setSoundType(SoundType.GLASS);
		this.setHardness(0.3F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT_MIPPED;
	}
}
