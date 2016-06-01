package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.BlockLogBetweenlands;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.block.structure.BlockDruidSpawner;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.structure.BlockPortalFrame;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.block.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.common.block.terrain.BlockGenericOre;
import thebetweenlands.common.block.terrain.BlockGenericStone;
import thebetweenlands.common.block.terrain.BlockMud;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.lib.ModInfo;

public class BlockRegistry {
	public static final Block SWAMP_WATER = new Block(Material.WATER);

	public static final Block DRUID_STONE_1 = new BlockDruidStone(Material.ROCK, "druid_stone_1");
	public static final Block DRUID_STONE_2 = new BlockDruidStone(Material.ROCK, "druid_stone_2");
	public static final Block DRUID_STONE_3 = new BlockDruidStone(Material.ROCK, "druid_stone_3");
	public static final Block DRUID_STONE_4 = new BlockDruidStone(Material.ROCK, "druid_stone_4");
	public static final Block DRUID_STONE_5 = new BlockDruidStone(Material.ROCK, "druid_stone_5");
	public static final Block SWAMP_DIRT = new BlockSwampDirt(Material.GROUND);
	public static final Block OCTINE_ORE = new BlockGenericOre(Material.ROCK) {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return new ItemStack(Item.getItemFromBlock(this));
		}
	};

	//TERRAIN BLOCKS
	public static final Block BETWEENLANDS_BEDROCK = new BlockBetweenlandsBedrock();
	public static final Block BETWEENSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block GENERICSTONE = new BlockGenericStone();
	public static final Block MUD = new BlockMud();

	public static final Block LOG_PORTAL = new BlockLogBetweenlands();
	public static final Block TREE_PORTAL = new BlockTreePortal();
	public static final Block PORTAL_FRAME = new BlockPortalFrame();
	public static final Block DRUID_SPAWNER = new BlockDruidSpawner();
	public static final Block DRUID_ALTAR = new BlockDruidAltar();
	public static final Block PURIFIER = new BlockPurifier();
	private static final List<Block> BLOCKS = new ArrayList<Block>();
	
	public static void preInit() {
		try {
			for (Field field : BlockRegistry.class.getDeclaredFields()) {
				Object obj = field.get(null);
				if (obj instanceof Block) {
					Block block = (Block) obj;
					String name = field.getName().toLowerCase(Locale.ENGLISH);
					registerBlock(name, block);
					
					if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
						if(block.getCreativeTabToDisplayOn() == null)
							block.setCreativeTab(BLCreativeTabs.BLOCKS);
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void registerRenderers() {
		for (Block block : BLOCKS)
			if (block instanceof ISubBlocksBlock) {
				List<String> models = ((ISubBlocksBlock) block).getModels();
				for (int i = 0; i < models.size(); i++)
					ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(i), "inventory"));
			} else {
				ResourceLocation name = block.getRegistryName();
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + name.getResourcePath(), "inventory"));
			}
    }
	
	public static void registerBlock(String name, Block block) {
		BLOCKS.add(block);

		GameRegistry.register(block.setRegistryName(ModInfo.ID, name).setUnlocalizedName(ModInfo.NAME_PREFIX + name));

		ItemBlock item;
		if (block instanceof IHasCustomItem)
			item = ((IHasCustomItem) block).getItemBlock();
		else
			item = new ItemBlock(block);

		GameRegistry.register((ItemBlock) item.setRegistryName(ModInfo.ID, name).setUnlocalizedName(ModInfo.NAME_PREFIX + name));
	}

	public interface IHasCustomItem {
		ItemBlock getItemBlock();
	}

	public interface ISubBlocksBlock {
		List<String> getModels();
	}
}



