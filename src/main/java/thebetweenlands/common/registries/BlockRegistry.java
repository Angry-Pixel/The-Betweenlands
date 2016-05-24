package thebetweenlands.common.registries;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.terrain.BlockGenericOre;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.lib.ModInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRegistry {
	public static final Block swampWater = new Block(Material.water).setUnlocalizedName(ModInfo.NAME_PREFIX + "swampWater");
	public final List<Block> blocks = new ArrayList<Block>();
	public final Block betweenstone = new BasicBlock(Material.rock, "betweenstone")
			.setStepSound2(SoundType.STONE)
			.setHardness(1.5F)
			.setResistance(10.0F);
	public final Block druidStone1 = new BlockDruidStone(Material.rock, "druidStone1");
	public final Block druidStone2 = new BlockDruidStone(Material.rock, "druidStone2");
	public final Block druidStone3 = new BlockDruidStone(Material.rock, "druidStone3");
	public final Block druidStone4 = new BlockDruidStone(Material.rock, "druidStone4");
	public final Block druidStone5 = new BlockDruidStone(Material.rock, "druidStone5");
	public final Block swampDirt = new BlockSwampDirt(Material.ground);
	public final Block octineOre = new BlockGenericOre(Material.rock, "octine_ore") {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return new ItemStack(Item.getItemFromBlock(this));
		}
	};

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
		GameRegistry.register(block);
		ItemBlock itemBlock = new ItemBlock(block);
		GameRegistry.register(itemBlock.setRegistryName(block.getRegistryName()));
		this.blocks.add(block);
	}
}
