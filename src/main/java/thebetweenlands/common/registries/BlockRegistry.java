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
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.client.render.models.block.registry.BlockModelRegistry;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.BlockLeavesBetweenlands;
import thebetweenlands.common.block.BlockLogBetweenlands;
import thebetweenlands.common.block.container.BlockCompostBin;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.block.container.BlockWeedwoodWorkbench;
import thebetweenlands.common.block.structure.BlockDruidSpawner;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.structure.BlockPortalFrame;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.block.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockDeadGrass;
import thebetweenlands.common.block.terrain.BlockGenericOre;
import thebetweenlands.common.block.terrain.BlockGenericStone;
import thebetweenlands.common.block.terrain.BlockLifeCrystalOre;
import thebetweenlands.common.block.terrain.BlockMud;
import thebetweenlands.common.block.terrain.BlockPeat;
import thebetweenlands.common.block.terrain.BlockSilt;
import thebetweenlands.common.block.terrain.BlockSlimyGrass;
import thebetweenlands.common.block.terrain.BlockSludgyDirt;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.block.terrain.BlockSwampGrass;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;

public class BlockRegistry {
	public static final Block SWAMP_WATER = new Block(Material.WATER);

	public static final Block DRUID_STONE_1 = new BlockDruidStone(Material.ROCK, "druid_stone_1");
	public static final Block DRUID_STONE_2 = new BlockDruidStone(Material.ROCK, "druid_stone_2");
	public static final Block DRUID_STONE_3 = new BlockDruidStone(Material.ROCK, "druid_stone_3");
	public static final Block DRUID_STONE_4 = new BlockDruidStone(Material.ROCK, "druid_stone_4");
	public static final Block DRUID_STONE_5 = new BlockDruidStone(Material.ROCK, "druid_stone_5");

	//TERRAIN BLOCKS
	public static final Block BETWEENLANDS_BEDROCK = new BlockBetweenlandsBedrock();
	public static final Block BETWEENSTONE = new BasicBlock(Material.ROCK)
			.setDefaultCreativeTab()
			.setSoundType2(SoundType.STONE)
			.setHardness(1.5F)
			.setResistance(10.0F);
	public static final Block GENERICSTONE = new BlockGenericStone();
	public static final Block MUD = new BlockMud();
	public static final Block PEAT = new BlockPeat();
	public static final Block SLUDGY_DIRT = new BlockSludgyDirt();
	public static final Block SLIMY_DIRT = new BasicBlock(Material.GROUND)
			.setDefaultCreativeTab()
			.setHarvestLevel2("shovel", 0)
			.setSoundType2(SoundType.GROUND)
			.setHardness(0.5F);
	public static final Block SLIMY_GRASS = new BlockSlimyGrass();
	public static final Block CRAGROCK = new BlockCragrock(Material.ROCK);
	public static final Block PITSTONE = new BasicBlock(Material.ROCK).setDefaultCreativeTab().setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block LIMESTONE = new BasicBlock(Material.ROCK)
			.setDefaultCreativeTab()
			.setSoundType2(SoundType.STONE)
			.setHardness(1.2F)
			.setResistance(8.0F);
	public static final Block SWAMP_DIRT = new BlockSwampDirt(Material.GROUND);
	public static final Block SWAMP_GRASS = new BlockSwampGrass();
	public static final Block OCTINE_ORE = new BlockGenericOre(Material.ROCK).setLightLevel(0.875F);
	public static final Block VALONITE_ORE = new BlockGenericOre(Material.ROCK) {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return EnumItemMisc.VALONITE_SHARD.create(1 + rand.nextInt(fortune + 1));
		}
	}.setXP(5, 12);
	public static final Block SULFUR_ORE = new BlockGenericOre(Material.ROCK) {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return EnumItemMisc.SULFUR.create(1 + rand.nextInt(fortune + 1));
		}
		@SideOnly(Side.CLIENT)
		@Override
		public void spawnParticle(World world, double x, double y, double z) {
			BLParticle.SULFUR_ORE.spawn(world, x, y, z, 0, 0, 0, 0);
		}
	}.setXP(2, 5);
	public static final Block SLIMY_BONE_ORE = new BlockGenericOre(Material.ROCK) {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return EnumItemMisc.SLIMY_BONE.create(1 + rand.nextInt(fortune + 1));
		}
	}.setXP(1, 4);
	public static final Block SCABYST_ORE = new BlockGenericOre(Material.ROCK) {
		@Override
		protected ItemStack getOreDrop(Random rand, int fortune) {
			return EnumItemMisc.SCABYST.create(1 + rand.nextInt(fortune + 3));
		}
	}.setXP(4, 10);
	public static final Block SYRMORITE_ORE = new BlockGenericOre(Material.ROCK);
	public static final Block AQUA_MIDDLE_GEM_ORE = new BlockGenericOre(Material.ROCK).setLightLevel(0.8F);
	public static final Block CRIMSON_MIDDLE_GEM_ORE = new BlockGenericOre(Material.ROCK).setLightLevel(0.8F);
	public static final Block GREEN_MIDDLE_GEM_ORE = new BlockGenericOre(Material.ROCK).setLightLevel(0.8F);
	public static final Block LIFE_CRYSTAL_ORE = new BlockLifeCrystalOre(Material.ROCK);
	public static final Block SILT = new BlockSilt();
	public static final Block DEAD_GRASS = new BlockDeadGrass();

	//TREES
	public static final Block LOG_WEEDWOOD = new BlockLogBetweenlands();
	public static final Block LOG_SAP = new BlockLogBetweenlands();
	public static final Block LEAVES_WEEDWOOD = new BlockLeavesBetweenlands() {

		/*	@Override
			public Item getItemDropped(int meta, Random rand, int fortune) {
				return Item.getItemFromBlock(BlockRegistry.SAPLING_WEEDWOOD);
			}*/
	};
	public static final Block LEAVES_SAP_TREE = new BlockLeavesBetweenlands() {
		/*	@Override
			public Item getItemDropped(int meta, Random rand, int fortune) {
				return Item.getItemFromBlock(BlockRegistry.SAPLING_SAP_TREE);
			}*/
	};
	public static final Block LEAVES_RUBBER_TREE = new BlockLeavesBetweenlands() {
		/*	@Override
			public Item getItemDropped(int meta, Random rand, int fortune) {
				return Item.getItemFromBlock(BlockRegistry.SAPLING_RUBBER_TREE);
			}*/
	};

	public static final Block LOG_PORTAL = new BlockLogBetweenlands();
	public static final Block TREE_PORTAL = new BlockTreePortal();
	public static final Block PORTAL_FRAME = new BlockPortalFrame();
	public static final Block DRUID_SPAWNER = new BlockDruidSpawner();
	public static final Block DRUID_ALTAR = new BlockDruidAltar();
	public static final Block PURIFIER = new BlockPurifier();
	public static final Block WEEDWOOD_WORKBENCH = new BlockWeedwoodWorkbench();
	public static final Block COMPOST_BIN = new BlockCompostBin();

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
		for (Block block : BLOCKS) {
			if(block instanceof IStateMapped) {
				((IStateMapped)block).setStateMapper();
			}
			if(block instanceof ISubBlocksBlock) {
				List<String> models = ((ISubBlocksBlock) block).getModels();
				for (int i = 0; i < models.size(); i++)
					ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(i), "inventory"));
			} else {
				ResourceLocation name = block.getRegistryName();
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + name.getResourcePath(), "inventory"));
			}
			if(block instanceof ICustomModelSupplier) {
				BlockModelRegistry.INSTANCE.registerModel(new ResourceLocation(ModInfo.ASSETS_PREFIX + block.getRegistryName().getResourcePath()), 
						(modelLocation) -> ((ICustomModelSupplier)block).getCustomModel(modelLocation));
			}
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

	public interface IStateMapped {
		@SideOnly(Side.CLIENT)
		void setStateMapper();
	}

	public interface ICustomModelSupplier {
		@SideOnly(Side.CLIENT)
		IModel getCustomModel(ResourceLocation modelLocation);
	}
}



