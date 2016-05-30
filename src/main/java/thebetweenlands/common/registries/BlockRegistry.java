package thebetweenlands.common.registries;

import com.google.common.base.CaseFormat;
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
import thebetweenlands.common.block.structure.BlockPortalFrame;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.block.terrain.BlockGenericOre;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.lib.ModInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRegistry {
	public static final Block swampWater = new Block(Material.WATER);
	public final List<Block> blocks = new ArrayList<Block>();
	public final Block betweenstone = new BasicBlock(Material.ROCK)
			.setSoundType2(SoundType.STONE)
			.setHardness(1.5F)
			.setResistance(10.0F);
	public final Block druidStone1 = new BlockDruidStone(Material.ROCK, "druid_stone_1");
	public final Block druidStone2 = new BlockDruidStone(Material.ROCK, "druid_stone_2");
	public final Block druidStone3 = new BlockDruidStone(Material.ROCK, "druid_stone_3");
	public final Block druidStone4 = new BlockDruidStone(Material.ROCK, "druid_stone_4");
	public final Block druidStone5 = new BlockDruidStone(Material.ROCK, "druid_stone_5");
	public final Block swampDirt = new BlockSwampDirt(Material.GROUND);
	public final Block octineOre = new BlockGenericOre(Material.ROCK) {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return new ItemStack(Item.getItemFromBlock(this));
		}
	};

	public final Block treePortal = new BlockTreePortal();
	public final Block portalFrame = new BlockPortalFrame();

	public void preInit() {
		try {
			for(Field field : this.getClass().getDeclaredFields()) {
				if(field.getType().isAssignableFrom(Block.class)) {
					Block block = (Block) field.get(this);
					registerBlock(block, field.getName());

					if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
						if(block.getCreativeTabToDisplayOn() == null)
							block.setCreativeTab(BLCreativeTabs.BLOCKS);
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

	private void registerBlock(Block block, String fieldName) {
		String itemName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
		GameRegistry.register(block.setRegistryName(ModInfo.ID, itemName).setUnlocalizedName(ModInfo.NAME_PREFIX + itemName));
		ItemBlock itemBlock;
		if (block instanceof IHasCustomItem)
			itemBlock = ((IHasCustomItem) block).getItemBlock();
		else
			itemBlock = new ItemBlock(block);
		GameRegistry.register((ItemBlock) itemBlock.setRegistryName(ModInfo.ID, itemName).setUnlocalizedName(ModInfo.NAME_PREFIX + itemName));
		this.blocks.add(block);
	}

	public interface IHasCustomItem {
		ItemBlock getItemBlock();
	}

	public interface ISubBlocksBlock {
		List<String> getModels();
	}
}
