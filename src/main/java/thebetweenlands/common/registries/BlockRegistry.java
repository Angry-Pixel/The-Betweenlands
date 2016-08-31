package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.common.base.CaseFormat;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IItemColor;
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
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.container.BlockCompostBin;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;
import thebetweenlands.common.block.container.BlockWeedwoodWorkbench;
import thebetweenlands.common.block.plant.BlockAlgae;
import thebetweenlands.common.block.plant.BlockBladderwortFlower;
import thebetweenlands.common.block.plant.BlockBladderwortStalk;
import thebetweenlands.common.block.plant.BlockBogBeanFlower;
import thebetweenlands.common.block.plant.BlockBogBeanStalk;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomCap;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomStalk;
import thebetweenlands.common.block.plant.BlockCaveGrass;
import thebetweenlands.common.block.plant.BlockCaveMoss;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;
import thebetweenlands.common.block.plant.BlockGoldenClubFlower;
import thebetweenlands.common.block.plant.BlockGoldenClubStalk;
import thebetweenlands.common.block.plant.BlockHollowLog;
import thebetweenlands.common.block.plant.BlockMarshMarigoldFlower;
import thebetweenlands.common.block.plant.BlockMarshMarigoldStalk;
import thebetweenlands.common.block.plant.BlockNettle;
import thebetweenlands.common.block.plant.BlockNettleFlowered;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.plant.BlockPlantUnderwater;
import thebetweenlands.common.block.plant.BlockPoisonIvy;
import thebetweenlands.common.block.plant.BlockStackablePlantUnderwater;
import thebetweenlands.common.block.plant.BlockSwampReed;
import thebetweenlands.common.block.plant.BlockSwampReedUnderwater;
import thebetweenlands.common.block.plant.BlockThorns;
import thebetweenlands.common.block.plant.BlockVenusFlyTrap;
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.block.structure.BlockDruidSpawner;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.block.structure.BlockPortalFrame;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.structure.BlockTemplePillar;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.block.structure.BlockWallBetweenlands;
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
import thebetweenlands.common.block.terrain.BlockStagnantWater;
import thebetweenlands.common.block.terrain.BlockStalactite;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.block.terrain.BlockSwampGrass;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.block.terrain.BlockTar;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;

public class BlockRegistry {
	public static final Block SWAMP_WATER = new BlockSwampWater(FluidRegistry.SWAMP_WATER, Material.WATER);
	public static final Block STAGNANT_WATER = new BlockStagnantWater();
	public static final Block TAR = new BlockTar();

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
	public static final Block COARSE_SWAMP_DIRT = new BlockSwampDirt(Material.GROUND).setItemDropped(Item.getItemFromBlock(SWAMP_DIRT));
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
	public static final Block TAR_SOLID = new BasicBlock(Material.ROCK)
			.setDefaultCreativeTab()
			.setSoundType2(SoundType.STONE)
			.setHardness(1.5F)
			.setResistance(10.0F);

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
	public static final Block WEEDWOOD_PLANKS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.WOOD).setHardness(2.0F).setResistance(5.0F);
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
	public static final Block CARVED_CRAGROCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
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
	public static final Block TEMPLE_PILLAR = new BlockTemplePillar();
	public static final Block BETWEENSTONE_PILLAR = new BlockTemplePillar();
	public static final Block PITSTONE_PILLAR = new BlockTemplePillar();
	public static final Block LIMESTONE_PILLAR = new BlockTemplePillar();
	public static final Block CRAGROCK_PILLAR = new BlockTemplePillar();
	public static final Block BETWEENSTONE_BRICK_STAIRS = new BlockStairsBetweenlands(BETWEENSTONE_BRICKS.getDefaultState());
	public static final Block MUD_BRICK_STAIRS = new BlockStairsBetweenlands(MUD_BRICKS.getDefaultState());
	public static final Block CRAGROCK_BRICK_STAIRS = new BlockStairsBetweenlands(CRAGROCK_BRICKS.getDefaultState());
	public static final Block LIMESTONE_BRICK_STAIRS = new BlockStairsBetweenlands(LIMESTONE_BRICKS.getDefaultState());
	public static final Block PITSTONE_BRICK_STAIRS = new BlockStairsBetweenlands(PITSTONE_BRICKS.getDefaultState());
	public static final Block LIMESTONE_STAIRS = new BlockStairsBetweenlands(LIMESTONE.getDefaultState());
	public static final Block SMOOTH_BETWEENSTONE_STAIRS = new BlockStairsBetweenlands(SMOOTH_BETWEENSTONE.getDefaultState());
	public static final Block SMOOTH_CRAGROCK_STAIRS = new BlockStairsBetweenlands(SMOOTH_CRAGROCK.getDefaultState());
	public static final Block POLISHED_LIMESTONE_STAIRS = new BlockStairsBetweenlands(POLISHED_LIMESTONE.getDefaultState());
	public static final Block MOSSY_BETWEENSTONE_BRICK_STAIRS = new BlockStairsBetweenlands(MOSSY_BETWEENSTONE_BRICKS.getDefaultState());
	public static final Block MOSSY_SMOOTH_BETWEENSTONE_STAIRS = new BlockStairsBetweenlands(MOSSY_SMOOTH_BETWEENSTONE.getDefaultState());
	public static final Block CRACKED_BETWEENSTONE_BRICK_STAIRS = new BlockStairsBetweenlands(CRACKED_BETWEENSTONE_BRICKS.getDefaultState());
	//public static final Block SOLID_TAR_STAIRS = new BlockStairsBetweenlands(SOLID_TAR.getDefaultState());
	//public static final Block TEMPLE_BRICK_STAIRS = new BlockStairsBetweenlands(TEMPLE_BRICK.getDefaultState());

	public static final Block BETWEENSTONE_BRICK_SLAB = new BlockSlabBetweenlands(BETWEENSTONE_BRICKS);
	public static final Block MUD_BRICK_SLAB = new BlockSlabBetweenlands(MUD_BRICKS);
	public static final Block CRAGROCK_BRICK_SLAB = new BlockSlabBetweenlands(CRAGROCK_BRICKS);
	public static final Block LIMESTONE_BRICK_SLAB = new BlockSlabBetweenlands(LIMESTONE_BRICKS);
	public static final Block LIMESTONE_SLAB = new BlockSlabBetweenlands(LIMESTONE);
	public static final Block SMOOTH_BETWEENSTONE_SLAB = new BlockSlabBetweenlands(SMOOTH_BETWEENSTONE);
	public static final Block SMOOTH_CRAGROCK_SLAB = new BlockSlabBetweenlands(SMOOTH_CRAGROCK);
	public static final Block POLISHED_LIMESTONE_SLAB = new BlockSlabBetweenlands(POLISHED_LIMESTONE);
	public static final Block PITSTONE_BRICK_SLAB = new BlockSlabBetweenlands(PITSTONE_BRICKS);
	public static final Block MOSSY_BETWEENSTONE_BRICK_SLAB = new BlockSlabBetweenlands(MOSSY_BETWEENSTONE_BRICKS);
	public static final Block MOSSY_SMOOTH_BETWEENSTONE_SLAB = new BlockSlabBetweenlands(MOSSY_SMOOTH_BETWEENSTONE);
	public static final Block CRACKED_BETWEENSTONE_BRICK_SLAB = new BlockSlabBetweenlands(CRACKED_BETWEENSTONE_BRICKS);
	public static final Block WEEDWOOD_PLANK_SLAB = new BlockSlabBetweenlands(WEEDWOOD_PLANKS);

	public static final Block BETWEENSTONE_BRICK_WALL = new BlockWallBetweenlands(BETWEENSTONE_BRICKS);
	public static final Block MUD_BRICK_WALL = new BlockWallBetweenlands(MUD_BRICKS);
	public static final Block CRAGROCK_WALL = new BlockWallBetweenlands(CRAGROCK);
	public static final Block CRAGROCK_BRICK_WALL = new BlockWallBetweenlands(CRAGROCK_BRICKS);
	public static final Block LIMESTONE_BRICK_WALL = new BlockWallBetweenlands(LIMESTONE_BRICKS);
	public static final Block LIMESTONE_WALL = new BlockWallBetweenlands(LIMESTONE);
	public static final Block PITSTONE_BRICK_WALL = new BlockWallBetweenlands(PITSTONE_BRICKS);
	public static final Block SMOOTH_BETWEENSTONE_WALL = new BlockWallBetweenlands(SMOOTH_BETWEENSTONE);
	public static final Block SMOOTH_CRAGROCK_WALL = new BlockWallBetweenlands(SMOOTH_CRAGROCK);
	public static final Block MOSSY_BETWEENSTONE_BRICK_WALL = new BlockWallBetweenlands(MOSSY_BETWEENSTONE_BRICKS);
	public static final Block MOSSY_SMOOTH_BETWEENSTONE_WALL = new BlockWallBetweenlands(MOSSY_SMOOTH_BETWEENSTONE);
	public static final Block CRACKED_BETWEENSTONE_BRICK_WALL = new BlockWallBetweenlands(CRACKED_BETWEENSTONE_BRICKS);
	//public static final Block SMOOTH_PITSTONE_WALL = new BlockWallBetweenlands(SMOOTH_PITSTONE);
	//public static final Block TEMPLE_BRICK_BRICK_WALL = new BlockWallBetweenlands(TEMPLE_BRICKS);
	//public static final Block SOLID_TAR_BRICK_WALL = new BlockWallBetweenlands(SOLID_TAR);


	//Plants
	public static final BlockDoublePlantBL PITCHER_PLANT = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.PITCHER_PLANT_TRAP.create(1));
	public static final BlockDoublePlantBL WEEPING_BLUE = new BlockDoublePlantBL();
	public static final BlockDoublePlantBL SUNDEW = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.SUNDEW_HEAD.create(1));
	public static final Block BLACK_HAT_MUSHROOM = new BlockPlant();
	public static final Block BULB_CAPPED_MUSHROOM = new BlockPlant() {
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
	public static final Block FLAT_HEAD_MUSHROOM = new BlockPlant();
	public static final Block VENUS_FLY_TRAP = new BlockVenusFlyTrap().setSickleDrop(EnumItemPlantDrop.VENUS_FLY_TRAP_ITEM.create(1));
	public static final BlockDoublePlantBL VOLARPAD = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.VOLARPAD_ITEM.create(1));
	public static final Block SWAMP_PLANT = new BlockPlant() {
		@Override
		@SideOnly(Side.CLIENT)
		public Block.EnumOffsetType getOffsetType() {
			return Block.EnumOffsetType.XZ;
		}
	}.setSickleDrop(EnumItemPlantDrop.GENERIC_LEAF.create(1)).setReplaceable(true);
	public static final Block SWAMP_KELP = new BlockStackablePlantUnderwater().setLightLevel(0.2F);
	public static final Block MIRE_CORAL = new BlockPlantUnderwater().setSickleDrop(EnumItemPlantDrop.MIRE_CORAL_ITEM.create(1)).setLightLevel(1F);
	public static final Block DEEP_WATER_CORAL = new BlockPlantUnderwater().setSickleDrop(EnumItemPlantDrop.DEEP_WATER_CORAL_ITEM.create(1)).setLightLevel(1F);
	public static final Block WATER_WEEDS = new BlockPlantUnderwater().setSickleDrop(EnumItemPlantDrop.WATER_WEEDS_ITEM.create(1)).setReplaceable(true);
	public static final Block BULB_CAPPED_MUSHROOM_CAP = new BlockBulbCappedMushroomCap();
	public static final Block BULB_CAPPED_MUSHROOM_STALK = new BlockBulbCappedMushroomStalk();
	public static final Block SHELF_FUNGUS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.CLOTH).setHardness(0.2F);
	public static final Block ALGAE = new BlockAlgae().setSickleDrop(EnumItemPlantDrop.ALGAE_ITEM.create(1));
	public static final Block POISON_IVY = new BlockPoisonIvy();
	public static final Block ROOT = new BlockRoot();
	public static final Block ROOT_UNDERWATER = new BlockRootUnderwater();
	public static final Block ARROW_ARUM = new BlockPlant().setSickleDrop(EnumItemPlantDrop.ARROW_ARUM_LEAF.create(1));
	public static final Block BLUE_EYED_GRASS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BLUE_EYED_GRASS_FLOWERS.create(1));
	public static final Block BLUE_IRIS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BLUE_IRIS_PETAL.create(1));
	public static final Block BONESET = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BONESET_FLOWERS.create(1));
	public static final Block BOTTLE_BRUSH_GRASS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BOTTLE_BRUSH_GRASS_BLADES.create(1));
	public static final BlockDoublePlantBL BROOMSEDGE = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.BROOM_SEDGE_LEAVES.create(1));
	public static final Block BUTTON_BUSH = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BUTTON_BUSH_FLOWERS.create(1));
	public static final BlockDoublePlantBL CARDINAL_FLOWER = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.CARDINAL_FLOWER_PETALS.create(1));
	public static final Block CATTAIL = new BlockPlant().setSickleDrop(EnumItemPlantDrop.CATTAIL_HEAD.create(1));
	public static final Block CAVE_GRASS = new BlockCaveGrass().setSickleDrop(EnumItemPlantDrop.CAVE_GRASS_BLADES.create(1)).setReplaceable(true);
	public static final Block COPPER_IRIS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.COPPER_IRIS_PETALS.create(1));
	public static final Block MARSH_HIBISCUS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.MARSH_HIBISCUS_FLOWER.create(1));
	public static final Block MARSH_MALLOW = new BlockPlant().setSickleDrop(EnumItemPlantDrop.MARSH_MALLOW_FLOWER.create(1));
	public static final Block BLADDERWORT_FLOWER = new BlockBladderwortFlower().setSickleDrop(EnumItemPlantDrop.BLADDERWORT_FLOWER_ITEM.create(1));
	public static final Block BLADDERWORT_STALK = new BlockBladderwortStalk().setSickleDrop(EnumItemPlantDrop.BLADDERWORT_STALK_ITEM.create(1));
	public static final Block BOG_BEAN_FLOWER = new BlockBogBeanFlower().setSickleDrop(EnumItemPlantDrop.BOG_BEAN_FLOWER_ITEM.create(1));
	public static final Block BOG_BEAN_STALK = new BlockBogBeanStalk().setSickleDrop(EnumItemPlantDrop.BOG_BEAN_FLOWER_ITEM.create(1));
	public static final Block GOLDEN_CLUB_FLOWER = new BlockGoldenClubFlower().setSickleDrop(EnumItemPlantDrop.GOLDEN_CLUB_FLOWER_ITEM.create(1));
	public static final Block GOLDEN_CLUB_STALK = new BlockGoldenClubStalk().setSickleDrop(EnumItemPlantDrop.GOLDEN_CLUB_FLOWER_ITEM.create(1));
	public static final Block MARSH_MARIGOLD_FLOWER = new BlockMarshMarigoldFlower().setSickleDrop(EnumItemPlantDrop.MARSH_MARIGOLD_FLOWER_ITEM.create(1));
	public static final Block MARSH_MARIGOLD_STALK = new BlockMarshMarigoldStalk().setSickleDrop(EnumItemPlantDrop.MARSH_MARIGOLD_FLOWER_ITEM.create(1));
	public static final BlockDoublePlantBL SWAMP_DOUBLE_TALLGRASS = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES.create(1));
	public static final Block MILKWEED = new BlockPlant().setSickleDrop(EnumItemPlantDrop.MILKWEED_ITEM.create(1));
	public static final Block NETTLE = new BlockNettle().setSickleDrop(EnumItemPlantDrop.NETTLE_LEAF.create(1));
	public static final Block NETTLE_FLOWERED = new BlockNettleFlowered().setSickleDrop(EnumItemPlantDrop.NETTLE_LEAF.create(1));
	public static final Block PICKEREL_WEED = new BlockPlant().setSickleDrop(EnumItemPlantDrop.PICKEREL_WEED_FLOWER.create(1));
	public static final BlockDoublePlantBL PHRAGMITES = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.PHRAGMITE_STEMS.create(1));
	public static final Block SHOOTS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.SHOOT_LEAVES.create(1)).setReplaceable(true);
	public static final Block SLUDGECREEP = new BlockPlant().setSickleDrop(EnumItemPlantDrop.SLUDGECREEP_LEAVES.create(1)).setReplaceable(true);
	public static final Block SOFT_RUSH = new BlockPlant().setSickleDrop(EnumItemPlantDrop.SOFT_RUSH_LEAVES.create(1));
	public static final Block SWAMP_REED = new BlockSwampReed();
	public static final Block SWAMP_REED_UNDERWATER = new BlockSwampReedUnderwater();
	public static final Block THORNS = new BlockThorns();
	public static final BlockDoublePlantBL TALL_CATTAIL = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.CATTAIL_HEAD.create(1));
	public static final Block SWAMP_TALLGRASS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.GENERIC_LEAF.create(1)).setReplaceable(true);
	public static final Block DEAD_WEEDWOOD_BUSH = new BlockPlant().setSickleDrop(EnumItemMisc.WEEDWOOD_STICK.create(1));
	public static final Block WEEDWOOD_BUSH = new BlockWeedwoodBush();
	public static final Block HOLLOW_LOG = new BlockHollowLog();
	public static final Block CAVE_MOSS = new BlockCaveMoss();

	//Misc
	public static final Block LOG_PORTAL = new BlockLogBetweenlands();
	public static final Block TREE_PORTAL = new BlockTreePortal();
	public static final Block PORTAL_FRAME = new BlockPortalFrame();
	public static final Block DRUID_SPAWNER = new BlockDruidSpawner();
	public static final Block DRUID_ALTAR = new BlockDruidAltar();
	public static final Block PURIFIER = new BlockPurifier();
	public static final Block WEEDWOOD_WORKBENCH = new BlockWeedwoodWorkbench();
	public static final Block COMPOST_BIN = new BlockCompostBin();
	public static final Block WEEDWOOD_JUKEBOX = new BlockWeedwoodJukebox();


	public static final List<Block> BLOCKS = new ArrayList<Block>();

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

	public static void registerBlock(String name, Block block) {
		BLOCKS.add(block);

		String unlocalized = ModInfo.NAME_PREFIX + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
		GameRegistry.register(block.setRegistryName(ModInfo.ID, name).setUnlocalizedName(unlocalized));

		ItemBlock item = getBlockItem(block);

		//Allows ICustomItemBlock to return null if no item block is required
		if(item != null)
			GameRegistry.register((ItemBlock) item.setRegistryName(ModInfo.ID, name).setUnlocalizedName(unlocalized));
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



