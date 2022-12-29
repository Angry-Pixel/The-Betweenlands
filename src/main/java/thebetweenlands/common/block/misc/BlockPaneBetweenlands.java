package thebetweenlands.common.block.misc;

import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockPaneBetweenlands extends BlockPane {
	public BlockPaneBetweenlands(Material materialIn) {
		super(materialIn, false);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.GLASS);
	}
	
	public BlockPaneBetweenlands(Material materialIn, boolean canDrop) {
		super(materialIn, canDrop);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.GLASS);
	}

	public BlockPaneBetweenlands(Material materialIn, SoundType soundtypeIn) {
		super(materialIn, true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(soundtypeIn);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
