package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.client.tabs.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.blocks.BasicBlock;
import thebetweenlands.common.blocks.terrain.BlockSwampDirt;
import thebetweenlands.common.lib.ModInfo;

public class BlockRegistry {
	public final List<Block> blocks = new ArrayList<Block>();

	public final Block betweenstone = new BasicBlock(Material.rock)
			.setStepSound2(SoundType.STONE)
			.setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone")
			.setHardness(1.5F)
			.setResistance(10.0F)
			.setCreativeTab(BLCreativeTabs.blocks);

	public final Block swampDirt = new BlockSwampDirt(Material.ground);

	public void preInit() {
		try {
			for(Field field : this.getClass().getDeclaredFields()) {
				if(field.getType().isAssignableFrom(Block.class)) {
					Block block = (Block) field.get(this);
					registerBlock(block);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void init() {
		for(Block block : this.blocks) {
			TheBetweenlands.proxy.registerDefaultBlockItemRenderer(block);
		}
	}

	private void registerBlock(Block block) {
		String name = block.getUnlocalizedName();
		String blockName = name.substring(name.lastIndexOf(".") + 1, name.length());
		GameRegistry.registerBlock(block, blockName);
		this.blocks.add(block);
	}
}
