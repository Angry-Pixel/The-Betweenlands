package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.client.tabs.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.blocks.BasicBlock;
import thebetweenlands.common.blocks.terrain.BlockGenericOre;
import thebetweenlands.common.blocks.terrain.BlockSwampDirt;
import thebetweenlands.common.lib.ModInfo;

public class BlockRegistry {
	public final List<Block> blocks = new ArrayList<Block>();

	public final Block betweenstone = new BasicBlock(Material.rock)
			.setStepSound2(SoundType.STONE)
			.setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone")
			.setHardness(1.5F)
			.setResistance(10.0F);

	public final Block swampDirt = new BlockSwampDirt(Material.ground);

	public final Block octineOre = new BlockGenericOre(Material.rock) {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return new ItemStack(Item.getItemFromBlock(this));
		}
	}.setUnlocalizedName(ModInfo.NAME_PREFIX + "octine_ore");

	public void preInit() {
		try {
			for(Field field : this.getClass().getDeclaredFields()) {
				if(field.getType().isAssignableFrom(Block.class)) {
					Block block = (Block) field.get(this);
					registerBlock(block);

					if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
						if(block.getCreativeTabToDisplayOn() == null)
							block.setCreativeTab(BLCreativeTabs.blocks);
					}
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
