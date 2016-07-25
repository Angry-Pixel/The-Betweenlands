package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.BlockLeavesBetweenlands;
import thebetweenlands.common.block.BlockLogBetweenlands;
import thebetweenlands.common.block.BlockRubberLog;
import thebetweenlands.common.block.BlockSaplingBetweenlands;
import thebetweenlands.common.block.container.BlockCompostBin;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;
import thebetweenlands.common.block.container.BlockWeedwoodWorkbench;
import thebetweenlands.common.block.plant.BlockBasicVine;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomCap;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomStalk;
import thebetweenlands.common.block.plant.BlockGenericDoublePlant;
import thebetweenlands.common.block.plant.BlockGenericPlant;
import thebetweenlands.common.block.plant.BlockGenericPlantUnderwater;
import thebetweenlands.common.block.plant.BlockGenericStackablePlantUnderwater;
import thebetweenlands.common.block.plant.BlockVenusFlyTrap;
import thebetweenlands.common.block.structure.*;
import thebetweenlands.common.block.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockDeadGrass;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.block.terrain.BlockGenericCollapsing;
import thebetweenlands.common.block.terrain.BlockGenericMirage;
import thebetweenlands.common.block.terrain.BlockGenericOre;
import thebetweenlands.common.block.terrain.BlockGenericStone;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.block.terrain.BlockMud;
import thebetweenlands.common.block.terrain.BlockPeat;
import thebetweenlands.common.block.terrain.BlockRoot;
import thebetweenlands.common.block.terrain.BlockRootUnderwater;
import thebetweenlands.common.block.terrain.BlockSilt;
import thebetweenlands.common.block.terrain.BlockSlimyGrass;
import thebetweenlands.common.block.terrain.BlockSludgyDirt;
import thebetweenlands.common.block.terrain.BlockStalactite;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.block.terrain.BlockSwampGrass;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;

public class BlockRegistry {
	public static final Block SWAMP_WATER = new BlockSwampWater(FluidRegistry.SWAMP_WATER, Material.WATER);

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
	public static final Block GENERIC_STONE = new BlockGenericStone();
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
	public static final Block WISP = new BlockWisp();
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
			BLParticles.SULFUR_ORE.spawn(world, x, y, z);
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
	public static final Block LIFE_CRYSTAL_STALACTITE = new BlockLifeCrystalStalactite(FluidRegistry.SWAMP_WATER, Material.WATER);
	public static final Block STALACTITE = new BlockStalactite();
	public static final Block SILT = new BlockSilt();
	public static final Block DEAD_GRASS = new BlockDeadGrass();

	//TREES
	public static final Block LOG_WEEDWOOD = new BlockLogBetweenlands();
	public static final Block LOG_RUBBER = new BlockRubberLog();
	public static final Block WEEDWOOD = new BasicBlock(Material.WOOD).setHarvestLevel2("axe", 0).setSoundType2(SoundType.WOOD).setHardness(2.0F);
	public static final Block LOG_SAP = new BlockLogBetweenlands();
	public static final Block SAPLING_WEEDWOOD = new BlockSaplingBetweenlands("WEEDWOOD");
	public static final Block SAPLING_SAP = new BlockSaplingBetweenlands("SAP");
	public static final Block SAPLING_RUBBER = new BlockSaplingBetweenlands("RUBBER");
	public static final Block LEAVES_WEEDWOOD_TREE = new BlockLeavesBetweenlands() {
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return Item.getItemFromBlock(BlockRegistry.SAPLING_WEEDWOOD);
		}
	};
	public static final Block LEAVES_SAP_TREE = new BlockLeavesBetweenlands() {
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return Item.getItemFromBlock(BlockRegistry.SAPLING_SAP);
		}
	};
	public static final Block LEAVES_RUBBER_TREE = new BlockLeavesBetweenlands() {
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return Item.getItemFromBlock(BlockRegistry.SAPLING_RUBBER);
		}
	};

	//STRUCTURE
	public static final Block ANGRY_BETWEENSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block BETWEENSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block BETWEENSTONE_BRICKS_MIRAGE = new BlockGenericMirage(Material.CIRCUITS).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block BETWEENSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block BETWEENSTONE_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block CRAGROCK_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block LIMESTONE_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block PITSTONE_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block CRACKED_BETWEENSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block CRACKED_BETWEENSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block CRAGROCK_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block CRAGROCK_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block GLOWING_BETWEENSTONE_TILE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.875F);
	public static final Block GLOWING_SMOOTH_CRAGROCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.875F);
	public static final Block LIMESTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block LIMESTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block MOSSY_BETWEENSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block MOSSY_BETWEENSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block MOSSY_SMOOTH_BETWEENSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block MUD_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block OCTINE_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block PITSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block PITSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block POLISHED_LIMESTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block SMOOTH_BETWEENSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block SMOOTH_CRAGROCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block SYRMORITE_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block VALONITE_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block WEAK_BETWEENSTONE_TILES = new BlockGenericCollapsing(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block WEAK_POLISHED_LIMESTONE = new BlockGenericCollapsing(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block WEAK_MOSSY_BETWEENSTONE_TILES = new BlockGenericCollapsing(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block DENTROTHYST = new BlockDentrothyst(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	public static final Block LOOT_POT = new BlockLootPot();
	public static final Block MOB_SPAWNER = new BlockMobSpawnerBetweenlands();

	//Plants
	public static final Block PITCHER_PLANT = new BlockGenericDoublePlant();
	public static final Block WEEPING_BLUE = new BlockGenericDoublePlant();
	public static final Block SUNDEW = new BlockGenericDoublePlant();
	public static final Block BLACK_HAT_MUSHROOM = new BlockGenericPlant();
	public static final Block BULB_CAPPED_MUSHROOM = new BlockGenericPlant() {
		@Override
		@SideOnly(Side.CLIENT)
		public Block.EnumOffsetType getOffsetType() {
			return Block.EnumOffsetType.XZ;
		}
		@Override
		@SideOnly(Side.CLIENT)
		public BlockRenderLayer getBlockLayer() {
			return BlockRenderLayer.TRANSLUCENT;
		}
	};
	public static final Block FLAT_HEAD_MUSHROOM = new BlockGenericPlant();
	public static final Block VENUS_FLY_TRAP = new BlockVenusFlyTrap();
	public static final Block VOLARPAD = new BlockGenericDoublePlant();
	public static final Block SWAMP_PLANT = new BlockGenericPlant() {
		@Override
		@SideOnly(Side.CLIENT)
		public Block.EnumOffsetType getOffsetType() {
			return Block.EnumOffsetType.XZ;
		}
	};
	public static final Block SWAMP_KELP = new BlockGenericStackablePlantUnderwater(FluidRegistry.SWAMP_WATER, Material.WATER).setLightLevel(0.2F);
	public static final Block MIRE_CORAL = new BlockGenericPlantUnderwater(FluidRegistry.SWAMP_WATER, Material.WATER).setLightLevel(1F);
	public static final Block DEEP_WATER_CORAL = new BlockGenericPlantUnderwater(FluidRegistry.SWAMP_WATER, Material.WATER).setLightLevel(1F);
	public static final Block WATER_WEEDS = new BlockGenericPlantUnderwater(FluidRegistry.SWAMP_WATER, Material.WATER);
	public static final Block BULB_CAPPED_MUSHROOM_CAP = new BlockBulbCappedMushroomCap();
	public static final Block BULB_CAPPED_MUSHROOM_STALK = new BlockBulbCappedMushroomStalk();
	public static final Block SHELF_FUNGUS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.CLOTH).setHardness(0.2F);

	//Misc
	public static final Block LOG_PORTAL = new BlockLogBetweenlands();
	public static final Block TREE_PORTAL = new BlockTreePortal();
	public static final Block PORTAL_FRAME = new BlockPortalFrame();
	public static final Block DRUID_SPAWNER = new BlockDruidSpawner();
	public static final Block DRUID_ALTAR = new BlockDruidAltar();
	public static final Block PURIFIER = new BlockPurifier();
	public static final Block WEEDWOOD_WORKBENCH = new BlockWeedwoodWorkbench();
	public static final Block COMPOST_BIN = new BlockCompostBin();
	public static final Block ROOT = new BlockRoot();
	public static final Block ROOT_UNDERWATER = new BlockRootUnderwater(FluidRegistry.SWAMP_WATER, Material.WATER);
	public static final Block WEEDWOOD_JUKEBOX = new BlockWeedwoodJukebox();
	public static final Block POISON_IVY = new BlockBasicVine(true);


	private static final List<Block> BLOCKS = new ArrayList<Block>();

	public void preInit() {
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
			if(block instanceof IStateMappedBlock) {
				StateMap.Builder builder = new StateMap.Builder();
				((IStateMappedBlock)block).setStateMapper(builder);
				ModelLoader.setCustomStateMapper(block, builder.build());
			}
			ResourceLocation name = block.getRegistryName();
			if(block instanceof ISubtypeBlock) {
				ISubtypeBlock subtypeBlock = (ISubtypeBlock) block;
				for(int i = 0; i < subtypeBlock.getSubtypeNumber(); i++) {
					int meta = subtypeBlock.getSubtypeMeta(i);
					ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + String.format(subtypeBlock.getSubtypeName(meta), name.getResourcePath()), "inventory"));
				}
			} else {
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + name.getResourcePath(), "inventory"));
			}
		}
	}

	public static void registerBlock(String name, Block block) {
		BLOCKS.add(block);

		GameRegistry.register(block.setRegistryName(ModInfo.ID, name).setUnlocalizedName(ModInfo.NAME_PREFIX + name));

		ItemBlock item = getBlockItem(block);

		//Allows ICustomItemBlock to return null if no item block is required
		if(item != null)
			GameRegistry.register((ItemBlock) item.setRegistryName(ModInfo.ID, name).setUnlocalizedName(ModInfo.NAME_PREFIX + name));
	}

	public static ItemBlock getBlockItem(Block block) {
		ItemBlock item;
		if (block instanceof ICustomItemBlock)
			item = ((ICustomItemBlock) block).getItemBlock();
		else
			item = new ItemBlock(block);
		return item;
	}
	
	public interface ICustomItemBlock {
		/**
		 * Returns a custom item for this block
		 * @return
		 */
		ItemBlock getItemBlock();
	}

	public interface ISubtypeBlock {
		/**
		 * Returns the amount of subtypes
		 * @return
		 */
		int getSubtypeNumber();

		/**
		 * Returns the name of this subtype.
		 * String is formatted, use %s for the normal registry name.
		 * @param meta
		 * @return
		 */
		String getSubtypeName(int meta);
		
		/**
		 * Returns the metadata for the specified subtype
		 * @param subtype
		 * @return
		 */
		default int getSubtypeMeta(int subtype) {
			return subtype;
		}
	}

	public interface IStateMappedBlock {
		/**
		 * Sets the statemap
		 * @param builder
		 */
		@SideOnly(Side.CLIENT)
		void setStateMapper(StateMap.Builder builder);
	}
}



