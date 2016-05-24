package thebetweenlands.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BasicBlock extends Block {
	public BasicBlock(Material materialIn, String name) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public BasicBlock setStepSound2(SoundType sound) {
		super.setStepSound(sound);
		return this;
	}

	public BasicBlock setHarvestLevel2(String toolClass, int level) {
		super.setHarvestLevel(toolClass, level);
		return this;
	}
}
