package thebetweenlands.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BasicBlock extends Block {
	public BasicBlock(Material materialIn) {
		super(materialIn);
	}

	public BasicBlock setSoundType2(SoundType sound) {
		super.setSoundType(sound);
		return this;
	}

	public BasicBlock setHarvestLevel2(String toolClass, int level) {
		super.setHarvestLevel(toolClass, level);
		return this;
	}

	public BasicBlock setDefaultCreativeTab(){
		super.setCreativeTab(BLCreativeTabs.BLOCKS);
		return this;
	}
}
