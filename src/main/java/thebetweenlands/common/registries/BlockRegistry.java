package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.container.BlockAlembic;
import thebetweenlands.common.block.container.BlockAnimator;
import thebetweenlands.common.block.container.BlockAspectVial;
import thebetweenlands.common.block.container.BlockBLDualFurnace;
import thebetweenlands.common.block.container.BlockBLFurnace;
import thebetweenlands.common.block.container.BlockChestBetweenlands;
import thebetweenlands.common.block.container.BlockCompostBin;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.container.BlockGeckoCage;
import thebetweenlands.common.block.container.BlockHopperBetweenlands;
import thebetweenlands.common.block.container.BlockInfuser;
import thebetweenlands.common.block.container.BlockItemShelf;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.container.BlockMortar;
import thebetweenlands.common.block.container.BlockMudBrickAlcove;
import thebetweenlands.common.block.container.BlockPresent;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.block.container.BlockRepeller;
import thebetweenlands.common.block.container.BlockTarLootPot;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;
import thebetweenlands.common.block.container.BlockWeedwoodWorkbench;
import thebetweenlands.common.block.farming.BlockAspectrusCrop;
import thebetweenlands.common.block.farming.BlockDugDirt;
import thebetweenlands.common.block.farming.BlockDugGrass;
import thebetweenlands.common.block.farming.BlockFungusCrop;
import thebetweenlands.common.block.farming.BlockMiddleFruitBush;
import thebetweenlands.common.block.misc.BlockAmatePaperPane;
import thebetweenlands.common.block.misc.BlockBouncyBetweenlands;
import thebetweenlands.common.block.misc.BlockButtonBetweenlands;
import thebetweenlands.common.block.misc.BlockCavingRopeLight;
import thebetweenlands.common.block.misc.BlockDampTorch;
import thebetweenlands.common.block.misc.BlockDentrothystPane;
import thebetweenlands.common.block.misc.BlockGlassBetweenlands;
import thebetweenlands.common.block.misc.BlockGroundItem;
import thebetweenlands.common.block.misc.BlockLadderBetweenlands;
import thebetweenlands.common.block.misc.BlockLeverBetweenlands;
import thebetweenlands.common.block.misc.BlockMossBed;
import thebetweenlands.common.block.misc.BlockMudFlowerPot;
import thebetweenlands.common.block.misc.BlockMudFlowerPotCandle;
import thebetweenlands.common.block.misc.BlockOctine;
import thebetweenlands.common.block.misc.BlockPaneBetweenlands;
import thebetweenlands.common.block.misc.BlockPolishedDentrothyst;
import thebetweenlands.common.block.misc.BlockPressurePlateBetweenlands;
import thebetweenlands.common.block.misc.BlockRope;
import thebetweenlands.common.block.misc.BlockRubberTap;
import thebetweenlands.common.block.misc.BlockSludge;
import thebetweenlands.common.block.misc.BlockSulfurTorch;
import thebetweenlands.common.block.misc.BlockTrapDoorBetweenlands;
import thebetweenlands.common.block.plant.BlockAlgae;
import thebetweenlands.common.block.plant.BlockBlackHatMushroom;
import thebetweenlands.common.block.plant.BlockBladderwortFlower;
import thebetweenlands.common.block.plant.BlockBladderwortStalk;
import thebetweenlands.common.block.plant.BlockBogBeanFlower;
import thebetweenlands.common.block.plant.BlockBogBeanStalk;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroom;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomCap;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomStalk;
import thebetweenlands.common.block.plant.BlockCaveGrass;
import thebetweenlands.common.block.plant.BlockCaveMoss;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;
import thebetweenlands.common.block.plant.BlockEdgeShroom;
import thebetweenlands.common.block.plant.BlockFlatheadMushroom;
import thebetweenlands.common.block.plant.BlockGoldenClubFlower;
import thebetweenlands.common.block.plant.BlockGoldenClubStalk;
import thebetweenlands.common.block.plant.BlockHollowLog;
import thebetweenlands.common.block.plant.BlockMarshMarigoldFlower;
import thebetweenlands.common.block.plant.BlockMarshMarigoldStalk;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.block.plant.BlockNettle;
import thebetweenlands.common.block.plant.BlockNettleFlowered;
import thebetweenlands.common.block.plant.BlockPhragmites;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.plant.BlockPlantUnderwater;
import thebetweenlands.common.block.plant.BlockPoisonIvy;
import thebetweenlands.common.block.plant.BlockSaplingBetweenlands;
import thebetweenlands.common.block.plant.BlockSaplingSpiritTree;
import thebetweenlands.common.block.plant.BlockSundew;
import thebetweenlands.common.block.plant.BlockSwampKelp;
import thebetweenlands.common.block.plant.BlockSwampReed;
import thebetweenlands.common.block.plant.BlockSwampReedUnderwater;
import thebetweenlands.common.block.plant.BlockThorns;
import thebetweenlands.common.block.plant.BlockVenusFlyTrap;
import thebetweenlands.common.block.plant.BlockWaterWeeds;
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.block.plant.BlockWeepingBlue;
import thebetweenlands.common.block.structure.BlockBeamLensSupport;
import thebetweenlands.common.block.structure.BlockBeamOrigin;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockBeamTube;
import thebetweenlands.common.block.structure.BlockBrazier;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick.EnumCarvedMudBrickType;
import thebetweenlands.common.block.structure.BlockChipPath;
import thebetweenlands.common.block.structure.BlockCompactedMud;
import thebetweenlands.common.block.structure.BlockCompactedMudSlope;
import thebetweenlands.common.block.structure.BlockDecayPitControl;
import thebetweenlands.common.block.structure.BlockDecayPitHangingChain;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;
import thebetweenlands.common.block.structure.BlockDoorBetweenlands;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.structure.BlockDungeonDoorCombination;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.block.structure.BlockDungeonWallCandle;
import thebetweenlands.common.block.structure.BlockEnergyBarrier;
import thebetweenlands.common.block.structure.BlockEnergyBarrierMud;
import thebetweenlands.common.block.structure.BlockFenceBetweenlands;
import thebetweenlands.common.block.structure.BlockFenceGateBetweenlands;
import thebetweenlands.common.block.structure.BlockItemCage;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.block.structure.BlockMudBrickRoof;
import thebetweenlands.common.block.structure.BlockMudBrickShingleSlab;
import thebetweenlands.common.block.structure.BlockMudBrickSpikeTrap;
import thebetweenlands.common.block.structure.BlockMudBricks;
import thebetweenlands.common.block.structure.BlockMudBricksClimbable;
import thebetweenlands.common.block.structure.BlockMudBricksSpawnerHole;
import thebetweenlands.common.block.structure.BlockMudTiles;
import thebetweenlands.common.block.structure.BlockMudTilesSpikeTrap;
import thebetweenlands.common.block.structure.BlockMudTilesWater;
import thebetweenlands.common.block.structure.BlockPortalFrame;
import thebetweenlands.common.block.structure.BlockPossessedBlock;
import thebetweenlands.common.block.structure.BlockPuffshroom;
import thebetweenlands.common.block.structure.BlockRottenBarkCarved;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSpikeTrap;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.structure.BlockStandingWeedwoodSign;
import thebetweenlands.common.block.structure.BlockTarBeastSpawner;
import thebetweenlands.common.block.structure.BlockTemplePillar;
import thebetweenlands.common.block.structure.BlockThatchRoof;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.block.structure.BlockWalkway;
import thebetweenlands.common.block.structure.BlockWallBetweenlands;
import thebetweenlands.common.block.structure.BlockWallWeedwoodSign;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.common.block.structure.BlockWoodenSupportBeam;
import thebetweenlands.common.block.structure.BlockWormDungeonPillar;
import thebetweenlands.common.block.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.common.block.terrain.BlockBlackIce;
import thebetweenlands.common.block.terrain.BlockCircleGem;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockDeadGrass;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.block.terrain.BlockFallenLeaves;
import thebetweenlands.common.block.terrain.BlockGenericCollapsing;
import thebetweenlands.common.block.terrain.BlockGenericMirage;
import thebetweenlands.common.block.terrain.BlockGenericOre;
import thebetweenlands.common.block.terrain.BlockGenericStone;
import thebetweenlands.common.block.terrain.BlockGiantRoot;
import thebetweenlands.common.block.terrain.BlockHanger;
import thebetweenlands.common.block.terrain.BlockHearthgroveLog;
import thebetweenlands.common.block.terrain.BlockLeavesSpiritTree;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.block.terrain.BlockLogBetweenlands;
import thebetweenlands.common.block.terrain.BlockLogSap;
import thebetweenlands.common.block.terrain.BlockMud;
import thebetweenlands.common.block.terrain.BlockPeat;
import thebetweenlands.common.block.terrain.BlockPuddle;
import thebetweenlands.common.block.terrain.BlockRoot;
import thebetweenlands.common.block.terrain.BlockRootUnderwater;
import thebetweenlands.common.block.terrain.BlockRottenLog;
import thebetweenlands.common.block.terrain.BlockRubber;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.block.terrain.BlockSilt;
import thebetweenlands.common.block.terrain.BlockSlimyGrass;
import thebetweenlands.common.block.terrain.BlockSludgyDirt;
import thebetweenlands.common.block.terrain.BlockSnowBetweenlands;
import thebetweenlands.common.block.terrain.BlockSpreadingRottenLog;
import thebetweenlands.common.block.terrain.BlockSpreadingSludgyDirt;
import thebetweenlands.common.block.terrain.BlockStagnantWater;
import thebetweenlands.common.block.terrain.BlockStalactite;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.block.terrain.BlockSwampGrass;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.block.terrain.BlockTar;
import thebetweenlands.common.block.terrain.BlockTintedLeaves;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.gen.feature.WorldGenRootPodRoots;
import thebetweenlands.common.world.gen.feature.tree.WorldGenHearthgroveTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenNibbletwigTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenRubberTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSapTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenWeedwoodTree;
import thebetweenlands.util.AdvancedStateMap;

public class BlockRegistry {
    public static final Block SWAMP_WATER = new BlockSwampWater(FluidRegistry.SWAMP_WATER, Material.WATER);
    public static final Block STAGNANT_WATER = new BlockStagnantWater();
    public static final Block TAR = new BlockTar();
    public static final Block RUBBER = new BlockRubber();
    public static final Block DRUID_STONE_1 = new BlockDruidStone(Material.ROCK);
    public static final Block DRUID_STONE_2 = new BlockDruidStone(Material.ROCK);
    public static final Block DRUID_STONE_3 = new BlockDruidStone(Material.ROCK);
    public static final Block DRUID_STONE_4 = new BlockDruidStone(Material.ROCK);
    public static final Block DRUID_STONE_5 = new BlockDruidStone(Material.ROCK);
    public static final Block DRUID_STONE_6 = new BlockDruidStone(Material.ROCK);
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
    public static final Block SPREADING_SLUDGY_DIRT = new BlockSpreadingSludgyDirt();
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
    public static final Block COARSE_SWAMP_DIRT = new BlockSwampDirt(Material.GROUND).setItemDropped(() -> Item.getItemFromBlock(BlockRegistry.SWAMP_DIRT));
    public static final Block SWAMP_GRASS = new BlockSwampGrass();
    public static final Block WISP = new BlockWisp();
    public static final Block OCTINE_ORE = new BlockGenericOre(Material.ROCK){
        @SideOnly(Side.CLIENT)
        @Override
        public void spawnParticle(World world, double x, double y, double z) {
            BLParticles.FLAME.spawn(world, x, y, z);
        }
    }.setHarvestLevel2("pickaxe", 1).setLightLevel(0.875F);
    public static final Block VALONITE_ORE = new BlockGenericOre(Material.ROCK) {
        @Override
        protected ItemStack getOreDrop(Random rand, int fortune) {
            return EnumItemMisc.VALONITE_SHARD.create(1 + rand.nextInt(fortune + 1));
        }
    }.setXP(5, 12).setHarvestLevel2("pickaxe", 2);
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
    }.setXP(0, 2).setHarvestLevel2("pickaxe", 0);
    public static final Block SLIMY_BONE_ORE = new BlockGenericOre(Material.ROCK) {
        @Override
        protected ItemStack getOreDrop(Random rand, int fortune) {
            return EnumItemMisc.SLIMY_BONE.create(1 + rand.nextInt(fortune + 1));
        }
    }.setXP(1, 4).setHarvestLevel2("pickaxe", 0);
    public static final Block SCABYST_ORE = new BlockGenericOre(Material.ROCK) {
        @Override
        protected ItemStack getOreDrop(Random rand, int fortune) {
            return EnumItemMisc.SCABYST.create(1 + rand.nextInt(fortune + 3));
        }
    }.setXP(4, 10).setHarvestLevel2("pickaxe", 2);
    public static final Block SYRMORITE_ORE = new BlockGenericOre(Material.ROCK).setHarvestLevel2("pickaxe", 1);
    public static final Block AQUA_MIDDLE_GEM_ORE = new BlockCircleGem(CircleGemType.AQUA);
    public static final Block CRIMSON_MIDDLE_GEM_ORE = new BlockCircleGem(CircleGemType.CRIMSON);
    public static final Block GREEN_MIDDLE_GEM_ORE = new BlockCircleGem(CircleGemType.GREEN);
    public static final Block LIFE_CRYSTAL_STALACTITE = new BlockLifeCrystalStalactite(FluidRegistry.SWAMP_WATER, Material.WATER);
    public static final Block STALACTITE = new BlockStalactite();
    public static final Block SILT = new BlockSilt();
    public static final Block DEAD_GRASS = new BlockDeadGrass();
    public static final Block TAR_SOLID = new BasicBlock(Material.ROCK)
            .setDefaultCreativeTab()
            .setSoundType2(SoundType.STONE)
            .setHardness(1.5F)
            .setResistance(10.0F);
    public static final Block PUDDLE = new BlockPuddle();
    //TREES
    public static final Block LOG_WEEDWOOD = new BlockLogBetweenlands();
    public static final Block LOG_ROTTEN_BARK = new BlockRottenLog();
    public static final Block LOG_SPREADING_ROTTEN_BARK = new BlockSpreadingRottenLog();
    public static final Block LOG_RUBBER = new BlockRubberLog();
    public static final Block LOG_HEARTHGROVE = new BlockHearthgroveLog();
    public static final Block LOG_NIBBLETWIG = new BlockLogBetweenlands();
    public static final Block LOG_SPIRIT_TREE = new BlockLogBetweenlands();
    public static final Block WEEDWOOD = new BasicBlock(Material.WOOD).setHarvestLevel2("axe", 0).setSoundType2(SoundType.WOOD).setHardness(2.0F);
    public static final Block LOG_SAP = new BlockLogSap();
    public static final Block SAPLING_WEEDWOOD = new BlockSaplingBetweenlands(new WorldGenWeedwoodTree());
    public static final Block SAPLING_SAP = new BlockSaplingBetweenlands(new WorldGenSapTree());
    public static final Block SAPLING_RUBBER = new BlockSaplingBetweenlands(new WorldGenRubberTree());
    public static final Block SAPLING_HEARTHGROVE = new BlockSaplingBetweenlands(new WorldGenHearthgroveTree());
    public static final Block SAPLING_NIBBLETWIG = new BlockSaplingBetweenlands(new WorldGenNibbletwigTree());
    public static final Block SAPLING_SPIRIT_TREE = new BlockSaplingSpiritTree();
    public static final Block ROOT_POD = new BlockSaplingBetweenlands(new WorldGenRootPodRoots());
    public static final Block LEAVES_WEEDWOOD_TREE = new BlockTintedLeaves() {
    	@Override
    	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    		return Item.getItemFromBlock(BlockRegistry.SAPLING_WEEDWOOD);
    	}
    };
    public static final Block LEAVES_SAP_TREE = new BlockTintedLeaves() {
        @Override
        public Item getItemDropped(IBlockState state, Random rand, int fortune) {
            return Item.getItemFromBlock(BlockRegistry.SAPLING_SAP);
        }
    };
    public static final Block LEAVES_RUBBER_TREE = new BlockTintedLeaves() {
        @Override
        public Item getItemDropped(IBlockState state, Random rand, int fortune) {
            return Item.getItemFromBlock(BlockRegistry.SAPLING_RUBBER);
        }
    };
    public static final Block LEAVES_HEARTHGROVE_TREE = new BlockTintedLeaves() {
    	@Override
    	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    		return Item.getItemFromBlock(BlockRegistry.SAPLING_HEARTHGROVE);
    	}
    };
    public static final Block LEAVES_NIBBLETWIG_TREE = new BlockTintedLeaves() {
    	@Override
    	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    		return Item.getItemFromBlock(BlockRegistry.SAPLING_NIBBLETWIG);
    	}
    };
    public static final Block LEAVES_SPIRIT_TREE_TOP = new BlockLeavesSpiritTree(BlockLeavesSpiritTree.Type.TOP);
    public static final Block LEAVES_SPIRIT_TREE_MIDDLE = new BlockLeavesSpiritTree(BlockLeavesSpiritTree.Type.MIDDLE);
    public static final Block LEAVES_SPIRIT_TREE_BOTTOM = new BlockLeavesSpiritTree(BlockLeavesSpiritTree.Type.BOTTOM);
    //STRUCTURE
    public static final Block WEEDWOOD_PLANKS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.WOOD).setHardness(2.0F).setResistance(5.0F);
    public static final Block RUBBER_TREE_PLANKS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block GIANT_ROOT_PLANKS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block HEARTHGROVE_PLANKS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block NIBBLETWIG_PLANKS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block ANGRY_BETWEENSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.8F);
    public static final Block BETWEENSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block BETWEENSTONE_BRICKS_MIRAGE = new BlockGenericMirage(Material.CIRCUITS).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block BETWEENSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block BETWEENSTONE_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block CRAGROCK_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block LIMESTONE_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block PITSTONE_CHISELED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_CHISELED_1 = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_CHISELED_2 = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_CHISELED_3 = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_PITSTONE_DOTTED = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_PITSTONE_HORIZONTAL = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block CRACKED_BETWEENSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block CRACKED_BETWEENSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block CRACKED_LIMESTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block CRAGROCK_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block CRAGROCK_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block GLOWING_BETWEENSTONE_TILE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.875F);
    public static final Block INACTIVE_GLOWING_SMOOTH_CRAGROCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block GLOWING_SMOOTH_CRAGROCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.875F);
    public static final Block LIMESTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block LIMESTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block MOSSY_BETWEENSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block MOSSY_BETWEENSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block MOSSY_LIMESTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block MOSSY_SMOOTH_BETWEENSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block MUD_BRICKS = new BlockMudBricks();
    public static final Block MUD_BRICK_SHINGLES = new BlockMudBricks();
    public static final Block RUBBER_BLOCK = new BlockBouncyBetweenlands(0.8f).setSoundType2(SoundType.SLIME).setHardness(1.0f);
    public static final Block PITSTONE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block PITSTONE_TILES = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block POLISHED_LIMESTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SMOOTH_BETWEENSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SMOOTH_CRAGROCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block OCTINE_BLOCK = new BlockOctine();
    public static final Block SYRMORITE_BLOCK = new BasicBlock(Material.IRON).setSoundType2(SoundType.METAL).setHardness(1.5F).setResistance(10.0F);
    public static final Block VALONITE_BLOCK = new BasicBlock(Material.IRON).setSoundType2(SoundType.METAL).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
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
    public static final Block TAR_BEAST_SPAWNER = new BlockTarBeastSpawner();
    public static final Block TAR_LOOT_POT = new BlockTarLootPot();
    public static final Block CRAGROCK_STAIRS = new BlockStairsBetweenlands(CRAGROCK.getDefaultState());
    public static final Block PITSTONE_STAIRS = new BlockStairsBetweenlands(PITSTONE.getDefaultState());
    public static final Block BETWEENSTONE_STAIRS = new BlockStairsBetweenlands(BETWEENSTONE.getDefaultState());
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
    public static final Block SCABYST_BRICK_STAIRS = new BlockStairsBetweenlands(SCABYST_BRICKS.getDefaultState());
    public static final Block SULFUR_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block TEMPLE_BRICKS = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block SMOOTH_PITSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block MIRE_CORAL_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(1F);
    public static final Block DEEP_WATER_CORAL_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(1F);
    public static final Block SLIMY_BONE_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
    public static final Block AQUA_MIDDLE_GEM_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.9f);
    public static final Block CRIMSON_MIDDLE_GEM_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.9f);
    public static final Block GREEN_MIDDLE_GEM_BLOCK = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F).setLightLevel(0.9f);
    public static final Block COMPOST_BLOCK = new BasicBlock(Material.PLANTS).setSoundType2(SoundType.PLANT).setHardness(0.5F).setResistance(10.0F);
    public static final Block POLISHED_DENTROTHYST = new BlockPolishedDentrothyst().setHardness(0.3F);
    public static final Block SILT_GLASS = new BlockGlassBetweenlands(Material.GLASS).setHardness(0.3F);
    public static final Block SILT_GLASS_PANE = new BlockPaneBetweenlands(Material.GLASS).setHardness(0.3F);
    public static final Block POLISHED_DENTROTHYST_PANE = new BlockDentrothystPane().setHardness(0.3F);
    public static final Block AMATE_PAPER_PANE_1 = new BlockAmatePaperPane();
    public static final Block AMATE_PAPER_PANE_2 = new BlockAmatePaperPane();
    public static final Block AMATE_PAPER_PANE_3 = new BlockAmatePaperPane();
    public static final Block SMOOTH_PITSTONE_STAIRS = new BlockStairsBetweenlands(SMOOTH_PITSTONE.getDefaultState());
    public static final Block TAR_SOLID_STAIRS = new BlockStairsBetweenlands(TAR_SOLID.getDefaultState());
    public static final Block TEMPLE_BRICK_STAIRS = new BlockStairsBetweenlands(TEMPLE_BRICKS.getDefaultState());
    public static final Block SPIKE_TRAP = new BlockSpikeTrap();
    public static final Block WEEDWOOD_PLANK_STAIRS = new BlockStairsBetweenlands(WEEDWOOD_PLANKS.getDefaultState());
    public static final Block RUBBER_TREE_PLANK_STAIRS = new BlockStairsBetweenlands(RUBBER_TREE_PLANKS.getDefaultState());
    public static final Block GIANT_ROOT_PLANK_STAIRS = new BlockStairsBetweenlands(GIANT_ROOT_PLANKS.getDefaultState());
    public static final Block HEARTHGROVE_PLANK_STAIRS = new BlockStairsBetweenlands(HEARTHGROVE_PLANKS.getDefaultState());
    public static final Block NIBBLETWIG_PLANK_STAIRS = new BlockStairsBetweenlands(NIBBLETWIG_PLANKS.getDefaultState());
    public static final Block POSSESSED_BLOCK = new BlockPossessedBlock();
    public static final Block ITEM_CAGE = new BlockItemCage();
    public static final Block ITEM_SHELF = new BlockItemShelf();
    public static final Block THATCH = new BasicBlock(Material.GRASS).setSoundType2(SoundType.PLANT).setDefaultCreativeTab().setHardness(0.5F);
    public static final Block CRAGROCK_SLAB = new BlockSlabBetweenlands(CRAGROCK);
    public static final Block PITSTONE_SLAB = new BlockSlabBetweenlands(PITSTONE);
    public static final Block BETWEENSTONE_SLAB = new BlockSlabBetweenlands(BETWEENSTONE);
    public static final Block SMOOTH_PITSTONE_SLAB = new BlockSlabBetweenlands(SMOOTH_PITSTONE);
    public static final Block TAR_SOLID_SLAB = new BlockSlabBetweenlands(TAR_SOLID);
    public static final Block TEMPLE_BRICK_SLAB = new BlockSlabBetweenlands(TEMPLE_BRICKS);
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
    public static final Block RUBBER_TREE_PLANK_SLAB = new BlockSlabBetweenlands(RUBBER_TREE_PLANKS);
    public static final Block GIANT_ROOT_PLANK_SLAB = new BlockSlabBetweenlands(GIANT_ROOT_PLANKS);
    public static final Block HEARTHGROVE_PLANK_SLAB = new BlockSlabBetweenlands(HEARTHGROVE_PLANKS);
    public static final Block NIBBLETWIG_PLANK_SLAB = new BlockSlabBetweenlands(NIBBLETWIG_PLANKS);
    public static final Block MUD_BRICK_SHINGLE_SLAB = new BlockMudBrickShingleSlab();
    public static final Block THATCH_SLAB = new BlockSlabBetweenlands(THATCH).setHardness(0.5F);
    public static final Block SCABYST_BRICK_SLAB = new BlockSlabBetweenlands(SCABYST_BRICKS);
    public static final Block PITSTONE_WALL = new BlockWallBetweenlands(PITSTONE.getDefaultState());
    public static final Block BETWEENSTONE_WALL = new BlockWallBetweenlands(BETWEENSTONE.getDefaultState());
    public static final Block TAR_SOLID_WALL = new BlockWallBetweenlands(TAR_SOLID.getDefaultState());
    public static final Block TEMPLE_BRICK_WALL = new BlockWallBetweenlands(TEMPLE_BRICKS.getDefaultState());
    public static final Block SMOOTH_PITSTONE_WALL = new BlockWallBetweenlands(SMOOTH_PITSTONE.getDefaultState());
    public static final Block BETWEENSTONE_BRICK_WALL = new BlockWallBetweenlands(BETWEENSTONE_BRICKS.getDefaultState());
    public static final Block MUD_BRICK_WALL = new BlockWallBetweenlands(MUD_BRICKS.getDefaultState());
    public static final Block CRAGROCK_WALL = new BlockWallBetweenlands(CRAGROCK.getDefaultState());
    public static final Block CRAGROCK_BRICK_WALL = new BlockWallBetweenlands(CRAGROCK_BRICKS.getDefaultState());
    public static final Block LIMESTONE_BRICK_WALL = new BlockWallBetweenlands(LIMESTONE_BRICKS.getDefaultState());
    public static final Block LIMESTONE_WALL = new BlockWallBetweenlands(LIMESTONE.getDefaultState());
    public static final Block POLISHED_LIMESTONE_WALL = new BlockWallBetweenlands(LIMESTONE.getDefaultState());
    public static final Block PITSTONE_BRICK_WALL = new BlockWallBetweenlands(PITSTONE_BRICKS.getDefaultState());
    public static final Block SMOOTH_BETWEENSTONE_WALL = new BlockWallBetweenlands(SMOOTH_BETWEENSTONE.getDefaultState());
    public static final Block SMOOTH_CRAGROCK_WALL = new BlockWallBetweenlands(SMOOTH_CRAGROCK.getDefaultState());
    public static final Block MOSSY_BETWEENSTONE_BRICK_WALL = new BlockWallBetweenlands(MOSSY_BETWEENSTONE_BRICKS.getDefaultState());
    public static final Block MOSSY_SMOOTH_BETWEENSTONE_WALL = new BlockWallBetweenlands(MOSSY_SMOOTH_BETWEENSTONE.getDefaultState());
    public static final Block CRACKED_BETWEENSTONE_BRICK_WALL = new BlockWallBetweenlands(CRACKED_BETWEENSTONE_BRICKS.getDefaultState());
    public static final Block SCABYST_BRICK_WALL = new BlockWallBetweenlands(SCABYST_BRICKS.getDefaultState());
    public static final Block WEEDWOOD_PLANK_FENCE = new BlockFenceBetweenlands(WEEDWOOD_PLANKS.getDefaultState());
    public static final Block WEEDWOOD_LOG_FENCE = new BlockFenceBetweenlands(WEEDWOOD.getDefaultState());
    public static final Block RUBBER_TREE_PLANK_FENCE = new BlockFenceBetweenlands(RUBBER_TREE_PLANKS.getDefaultState());
    public static final Block GIANT_ROOT_PLANK_FENCE = new BlockFenceBetweenlands(GIANT_ROOT_PLANKS.getDefaultState());
    public static final Block HEARTHGROVE_PLANK_FENCE = new BlockFenceBetweenlands(HEARTHGROVE_PLANKS.getDefaultState());
    public static final Block NIBBLETWIG_PLANK_FENCE = new BlockFenceBetweenlands(NIBBLETWIG_PLANKS.getDefaultState());
    public static final Block WEEDWOOD_PLANK_FENCE_GATE = new BlockFenceGateBetweenlands(WEEDWOOD_PLANKS.getDefaultState());
    public static final Block WEEDWOOD_LOG_FENCE_GATE = new BlockFenceGateBetweenlands(WEEDWOOD.getDefaultState());
    public static final Block RUBBER_TREE_PLANK_FENCE_GATE = new BlockFenceGateBetweenlands(RUBBER_TREE_PLANKS.getDefaultState());
    public static final Block GIANT_ROOT_PLANK_FENCE_GATE = new BlockFenceGateBetweenlands(GIANT_ROOT_PLANKS.getDefaultState());
    public static final Block HEARTHGROVE_PLANK_FENCE_GATE = new BlockFenceGateBetweenlands(HEARTHGROVE_PLANKS.getDefaultState());
    public static final Block NIBBLETWIG_PLANK_FENCE_GATE = new BlockFenceGateBetweenlands(NIBBLETWIG_PLANKS.getDefaultState());
    public static final Block WEEDWOOD_PLANK_PRESSURE_PLATE = new BlockPressurePlateBetweenlands(Material.WOOD, BlockPressurePlateBetweenlands.PressurePlateSensitivity.EVERYTHING)
            .setSoundType(SoundType.WOOD)
            .setHardness(2.0F)
            .setResistance(5.0F);
    public static final Block BETWEENSTONE_PRESSURE_PLATE = new BlockPressurePlateBetweenlands(Material.ROCK, BlockPressurePlateBetweenlands.PressurePlateSensitivity.MOBS)
            .setSoundType(SoundType.STONE)
            .setHardness(1.5F)
            .setResistance(10.0F);
    public static final Block SYRMORITE_PRESSURE_PLATE = new BlockPressurePlateBetweenlands(Material.IRON, BlockPressurePlateBetweenlands.PressurePlateSensitivity.PLAYERS)
            .setSoundType(SoundType.METAL)
            .setHardness(1.5F)
            .setResistance(10.0F);
    public static final Block WEEDWOOD_PLANK_BUTTON = new BlockButtonBetweenlands(true);
    public static final Block BETWEENSTONE_BUTTON = new BlockButtonBetweenlands(false);
    public static final Block WEEDWOOD_LADDER = new BlockLadderBetweenlands();
    public static final Block WEEDWOOD_LEVER = new BlockLeverBetweenlands();
    
    //Worm Dungeon
    public static final Block WORM_DUNGEON_PILLAR = new BlockWormDungeonPillar();
    public static final Block COMPACTED_MUD = new BlockCompactedMud();
    public static final Block MUD_TILES = new BlockMudTiles();
    public static final Block PUFFSHROOM = new BlockPuffshroom();
    public static final Block MUD_BRICKS_CARVED = new BlockCarvedMudBrick();
    public static final Block MUD_BRICK_STAIRS_DECAY_1 = new BlockStairsBetweenlands(MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_1));
    public static final Block MUD_BRICK_STAIRS_DECAY_2 = new BlockStairsBetweenlands(MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_2));
    public static final Block MUD_BRICK_STAIRS_DECAY_3 = new BlockStairsBetweenlands(MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_3));
    public static final Block MUD_BRICK_SLAB_DECAY_1 = new BlockSlabBetweenlands(MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_1).getBlock());
    public static final Block MUD_BRICK_SLAB_DECAY_2 = new BlockSlabBetweenlands(MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_2).getBlock());
    public static final Block MUD_BRICK_SLAB_DECAY_3 = new BlockSlabBetweenlands(MUD_BRICKS_CARVED.getDefaultState().withProperty(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_3).getBlock());
    public static final Block EDGE_SHROOM = new BlockEdgeShroom();
    public static final Block MUD_TOWER_BEAM_ORIGIN = new BlockBeamOrigin();
    public static final Block MUD_TOWER_BEAM_RELAY = new BlockBeamRelay();
    public static final Block MUD_TOWER_BEAM_TUBE = new BlockBeamTube();
    public static final Block MUD_TOWER_BEAM_LENS_SUPPORTS = new BlockBeamLensSupport();
    public static final Block DIAGONAL_ENERGY_BARRIER = new BlockDiagonalEnergyBarrier();
    public static final Block MUD_BRICK_SPAWNER_HOLE = new BlockMudBricksSpawnerHole();
    public static final Block MUD_BRICK_ALCOVE = new BlockMudBrickAlcove();
    public static final Block LOOT_URN = new BlockLootUrn();
	public static final Block DUNGEON_DOOR_RUNES = new BlockDungeonDoorRunes(false);
	public static final Block DUNGEON_DOOR_RUNES_MIMIC = new BlockDungeonDoorRunes(true);
	public static final Block DUNGEON_DOOR_COMBINATION = new BlockDungeonDoorCombination();
	public static final Block MUD_BRICKS_CLIMBABLE = new BlockMudBricksClimbable();
	public static final Block MUD_TILES_WATER = new BlockMudTilesWater();
	public static final Block DUNGEON_WALL_CANDLE = new BlockDungeonWallCandle();
	public static final Block WOODEN_SUPPORT_BEAM_ROTTEN_1 = new BlockWoodenSupportBeam();
	public static final Block WOODEN_SUPPORT_BEAM_ROTTEN_2 = new BlockWoodenSupportBeam();
	public static final Block WOODEN_SUPPORT_BEAM_ROTTEN_3 = new BlockWoodenSupportBeam();
	public static final Block LOG_ROTTEN_BARK_CARVED_1 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_2 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_3 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_4 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_5 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_6 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_7 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_8 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_9 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_10 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_11 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_12 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_13 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_14 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_15 = new BlockRottenBarkCarved();
	public static final Block LOG_ROTTEN_BARK_CARVED_16 = new BlockRottenBarkCarved();
	public static final Block ENERGY_BARRIER_MUD = new BlockEnergyBarrierMud();
	public static final Block MUD_BRICK_SPIKE_TRAP = new BlockMudBrickSpikeTrap();
	public static final Block MUD_TILES_SPIKE_TRAP = new BlockMudTilesSpikeTrap();
	public static final Block COMPACTED_MUD_SLOPE = new BlockCompactedMudSlope();
	public static final Block DECAY_PIT_CONTROL = new BlockDecayPitControl();

	//Place-Holders for stuffs and things
	public static final Block MUD_TOWER_BRAZIER = new BlockBrazier();
	public static final Block DECAY_PIT_HANGING_CHAIN = new BlockDecayPitHangingChain();
	public static final Block DECAY_PIT_CAPSTONE = new BasicBlock(Material.ROCK).setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);

	//Winter Event
    public static final Block PRESENT = new BlockPresent();
    
    //Plants
    public static final BlockDoublePlantBL PITCHER_PLANT = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.PITCHER_PLANT_TRAP.create(1));
    public static final BlockDoublePlantBL WEEPING_BLUE = new BlockWeepingBlue();
    public static final BlockDoublePlantBL SUNDEW = new BlockSundew();
    public static final Block BLACK_HAT_MUSHROOM = new BlockBlackHatMushroom();
    public static final Block BULB_CAPPED_MUSHROOM = new BlockBulbCappedMushroom();
    public static final Block FLAT_HEAD_MUSHROOM = new BlockFlatheadMushroom();
    public static final Block VENUS_FLY_TRAP = new BlockVenusFlyTrap().setSickleDrop(EnumItemPlantDrop.VENUS_FLY_TRAP_ITEM.create(1));
    public static final BlockDoublePlantBL VOLARPAD = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.VOLARPAD_ITEM.create(1));
    public static final Block SWAMP_PLANT = new BlockPlant() {
        @Override
        @SideOnly(Side.CLIENT)
        public Block.EnumOffsetType getOffsetType() {
            return Block.EnumOffsetType.XZ;
        }
    }.setSickleDrop(EnumItemPlantDrop.GENERIC_LEAF.create(1)).setReplaceable(true);
    public static final Block SWAMP_KELP = new BlockSwampKelp();
    public static final Block MIRE_CORAL = new BlockPlantUnderwater().setSickleDrop(EnumItemPlantDrop.MIRE_CORAL_ITEM.create(1)).setLightLevel(1F);
    public static final Block DEEP_WATER_CORAL = new BlockPlantUnderwater().setSickleDrop(EnumItemPlantDrop.DEEP_WATER_CORAL_ITEM.create(1)).setLightLevel(1F);
    public static final Block WATER_WEEDS = new BlockWaterWeeds();
    public static final Block BULB_CAPPED_MUSHROOM_CAP = new BlockBulbCappedMushroomCap();
    public static final Block BULB_CAPPED_MUSHROOM_STALK = new BlockBulbCappedMushroomStalk();
    public static final Block SHELF_FUNGUS = new BasicBlock(Material.WOOD).setSoundType2(SoundType.CLOTH).setHardness(0.2F);
    public static final Block ALGAE = new BlockAlgae().setSickleDrop(EnumItemPlantDrop.ALGAE_ITEM.create(1));
    public static final Block POISON_IVY = new BlockPoisonIvy();
    public static final Block ROOT = new BlockRoot();
    public static final Block ROOT_UNDERWATER = new BlockRootUnderwater();
    public static final Block GIANT_ROOT = new BlockGiantRoot();
    public static final Block ARROW_ARUM = new BlockPlant().setSickleDrop(EnumItemPlantDrop.ARROW_ARUM_LEAF.create(1));
    public static final Block BLUE_EYED_GRASS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BLUE_EYED_GRASS_FLOWERS.create(1));
    public static final Block BLUE_IRIS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BLUE_IRIS_PETAL.create(1));
    public static final Block BONESET = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BONESET_FLOWERS.create(1));
    public static final Block BOTTLE_BRUSH_GRASS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.BOTTLE_BRUSH_GRASS_BLADES.create(1));
    public static final BlockDoublePlantBL BROOMSEDGE = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.BROOM_SEDGE_LEAVES.create(1)).setReplaceable(true);
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
    public static final BlockDoublePlantBL SWAMP_DOUBLE_TALLGRASS = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES.create(1)).setReplaceable(true);
    public static final Block MILKWEED = new BlockPlant().setSickleDrop(EnumItemPlantDrop.MILKWEED_ITEM.create(1));
    public static final Block NETTLE = new BlockNettle().setSickleDrop(EnumItemPlantDrop.NETTLE_LEAF.create(1));
    public static final Block NETTLE_FLOWERED = new BlockNettleFlowered().setSickleDrop(EnumItemPlantDrop.NETTLE_LEAF.create(1));
    public static final Block PICKEREL_WEED = new BlockPlant().setSickleDrop(EnumItemPlantDrop.PICKEREL_WEED_FLOWER.create(1));
    public static final BlockDoublePlantBL PHRAGMITES = new BlockPhragmites().setReplaceable(true);
    public static final Block SHOOTS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.SHOOT_LEAVES.create(1)).setReplaceable(true);
    public static final Block SLUDGECREEP = new BlockPlant().setSickleDrop(EnumItemPlantDrop.SLUDGECREEP_LEAVES.create(1)).setReplaceable(true);
    public static final Block SOFT_RUSH = new BlockPlant().setSickleDrop(EnumItemPlantDrop.SOFT_RUSH_LEAVES.create(1)).setReplaceable(true);
    public static final Block SWAMP_REED = new BlockSwampReed();
    public static final Block SWAMP_REED_UNDERWATER = new BlockSwampReedUnderwater();
    public static final Block THORNS = new BlockThorns();
    public static final BlockDoublePlantBL TALL_CATTAIL = new BlockDoublePlantBL().setSickleDrop(EnumItemPlantDrop.CATTAIL_HEAD.create(1));
    public static final Block SWAMP_TALLGRASS = new BlockPlant().setSickleDrop(EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES.create(1)).setReplaceable(true);
    public static final Block DEAD_WEEDWOOD_BUSH = new BlockPlant().setSickleDrop(EnumItemMisc.WEEDWOOD_STICK.create(1));
    public static final Block WEEDWOOD_BUSH = new BlockWeedwoodBush();
    public static final Block HOLLOW_LOG = new BlockHollowLog();
    public static final Block CAVE_MOSS = new BlockCaveMoss();
    public static final Block MOSS = new BlockMoss().setSickleDrop(EnumItemPlantDrop.MOSS_ITEM.create(1)).setReplaceable(true);
    public static final Block LICHEN = new BlockMoss(){
    	@Override
    	public int getColorMultiplier(IBlockState state, net.minecraft.world.IBlockAccess worldIn, net.minecraft.util.math.BlockPos pos, int tintIndex) { return 0xFFFFFF; }
    }.setSickleDrop(EnumItemPlantDrop.LICHEN_ITEM.create(1)).setReplaceable(true);
    public static final Block HANGER = new BlockHanger();
    public static final Block MIDDLE_FRUIT_BUSH = new BlockMiddleFruitBush();
    public static final Block FUNGUS_CROP = new BlockFungusCrop();
    public static final BlockAspectrusCrop ASPECTRUS_CROP = new BlockAspectrusCrop();
    public static final Block PURIFIED_SWAMP_DIRT = new BlockSwampDirt(Material.GROUND);
    public static final Block DUG_SWAMP_DIRT = new BlockDugDirt(false).setItemDropped(() -> Item.getItemFromBlock(SWAMP_DIRT));
    public static final Block DUG_PURIFIED_SWAMP_DIRT = new BlockDugDirt(true).setItemDropped(() -> Item.getItemFromBlock(SWAMP_DIRT));
    public static final Block DUG_SWAMP_GRASS = new BlockDugGrass(false).setItemDropped(() -> Item.getItemFromBlock(SWAMP_DIRT));
    public static final Block DUG_PURIFIED_SWAMP_GRASS = new BlockDugGrass(true).setItemDropped(() -> Item.getItemFromBlock(SWAMP_DIRT));
    public static final Block BLACK_ICE = new BlockBlackIce();
    public static final Block SNOW = new BlockSnowBetweenlands();

    //Misc
    public static final Block LOG_PORTAL = new BlockLogBetweenlands() {
    	@Override
    	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    		return 0;
        }
    };
    public static final Block TREE_PORTAL = new BlockTreePortal();
    public static final Block PORTAL_FRAME = new BlockPortalFrame();
    public static final Block DRUID_ALTAR = new BlockDruidAltar();
    public static final Block PURIFIER = new BlockPurifier();
    public static final Block WEEDWOOD_WORKBENCH = new BlockWeedwoodWorkbench();
    public static final Block COMPOST_BIN = new BlockCompostBin();
    public static final Block WEEDWOOD_JUKEBOX = new BlockWeedwoodJukebox();
    public static final Block SULFUR_FURNACE = new BlockBLFurnace(false);
    public static final Block SULFUR_FURNACE_ACTIVE = new BlockBLFurnace(true).setLightLevel(0.875F);
    public static final Block SULFUR_FURNACE_DUAL = new BlockBLDualFurnace(false);
    public static final Block SULFUR_FURNACE_DUAL_ACTIVE = new BlockBLDualFurnace(true).setLightLevel(0.875F);
    public static final Block WEEDWOOD_CHEST = new BlockChestBetweenlands(BlockChestBetweenlands.WEEDWOOD_CHEST);
    public static final Block WEEDWOOD_RUBBER_TAP = new BlockRubberTap(BlockRegistry.WEEDWOOD_PLANKS.getDefaultState(), 540) {
        @Override
        protected ItemStack getBucket(boolean withRubber) {
            return new ItemStack(withRubber ? ItemRegistry.BL_BUCKET_RUBBER: ItemRegistry.BL_BUCKET, 1, 0);
        }
    };
    public static final Block SYRMORITE_RUBBER_TAP = new BlockRubberTap(BlockRegistry.SYRMORITE_BLOCK.getDefaultState(), 260) {
        @Override
        protected ItemStack getBucket(boolean withRubber) {
            return new ItemStack(withRubber ? ItemRegistry.BL_BUCKET_RUBBER: ItemRegistry.BL_BUCKET, 1, 1);
        }
    };
    public static final BlockSludge SLUDGE = new BlockSludge();
    public static final Block FALLEN_LEAVES = new BlockFallenLeaves("fallenLeaves");
    public static final Block ENERGY_BARRIER = new BlockEnergyBarrier();
    public static final Block WEEDWOOD_DOOR = new BlockDoorBetweenlands(Material.WOOD) {
        @Override
        public ItemStack getDoorItem() {
            return new ItemStack(ItemRegistry.WEEDWOOD_DOOR_ITEM);
        }
    }.setSoundType(SoundType.WOOD).setHardness(2.0F).setResistance(5.0F);
    public static final Block RUBBER_TREE_PLANK_DOOR = new BlockDoorBetweenlands(Material.WOOD) {
        @Override
        public ItemStack getDoorItem() {
            return new ItemStack(ItemRegistry.RUBBER_TREE_PLANK_DOOR_ITEM);
        }
    }.setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block GIANT_ROOT_PLANK_DOOR = new BlockDoorBetweenlands(Material.WOOD) {
        @Override
        public ItemStack getDoorItem() {
            return new ItemStack(ItemRegistry.GIANT_ROOT_PLANK_DOOR_ITEM);
        }
    }.setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block HEARTHGROVE_PLANK_DOOR = new BlockDoorBetweenlands(Material.WOOD) {
        @Override
        public ItemStack getDoorItem() {
            return new ItemStack(ItemRegistry.HEARTHGROVE_PLANK_DOOR_ITEM);
        }
    }.setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block NIBBLETWIG_PLANK_DOOR = new BlockDoorBetweenlands(Material.WOOD) {
        @Override
        public ItemStack getDoorItem() {
            return new ItemStack(ItemRegistry.NIBBLETWIG_PLANK_DOOR_ITEM);
        }
    }.setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block SYRMORITE_DOOR = new BlockDoorBetweenlands(Material.IRON) {
        @Override
        public ItemStack getDoorItem() {
            return new ItemStack(ItemRegistry.SYRMORITE_DOOR_ITEM);
        }
    }.setSoundType(SoundType.METAL).setHardness(1.5F).setResistance(10.0F);
    public static final Block SCABYST_DOOR = new BlockDoorBetweenlands(Material.IRON) {
        @Override
        public ItemStack getDoorItem() {
            return new ItemStack(ItemRegistry.SCABYST_DOOR_ITEM);
        }
    }.setSoundType(SoundType.METAL).setHardness(1.5F).setResistance(10.0F);
    public static final Block STANDING_WEEDWOOD_SIGN = new BlockStandingWeedwoodSign();
    public static final Block WALL_WEEDWOOD_SIGN = new BlockWallWeedwoodSign();
    public static final Block SULFUR_TORCH = new BlockSulfurTorch();
    public static final Block WEEDWOOD_TRAPDOOR = new BlockTrapDoorBetweenlands(Material.WOOD).setSoundType(SoundType.WOOD).setHardness(2.0F).setResistance(5.0F);
    public static final Block RUBBER_TREE_PLANK_TRAPDOOR = new BlockTrapDoorBetweenlands(Material.WOOD).setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block SYRMORITE_TRAPDOOR = new BlockTrapDoorBetweenlands(Material.IRON).setSoundType(SoundType.METAL).setHardness(1.5F).setResistance(10.0F);
    public static final Block GIANT_ROOT_PLANK_TRAPDOOR = new BlockTrapDoorBetweenlands(Material.WOOD).setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block HEARTHGROVE_PLANK_TRAPDOOR = new BlockTrapDoorBetweenlands(Material.WOOD).setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block NIBBLETWIG_PLANK_TRAPDOOR = new BlockTrapDoorBetweenlands(Material.WOOD).setSoundType(SoundType.WOOD).setHardness(1.75F).setResistance(5.0F);
    public static final Block SCABYST_TRAPDOOR = new BlockTrapDoorBetweenlands(Material.IRON).setSoundType(SoundType.STONE).setHardness(1.75F).setResistance(5.0F);
    public static final Block SYRMORITE_HOPPER = new BlockHopperBetweenlands();
    public static final Block MUD_FLOWER_POT = new BlockMudFlowerPot();
    public static final Block MUD_FLOWER_POT_CANDLE = new BlockMudFlowerPotCandle();
    public static final Block GECKO_CAGE = new BlockGeckoCage();
    public static final Block INFUSER = new BlockInfuser();
    public static final Block ASPECT_VIAL_BLOCK = new BlockAspectVial();
    public static final Block MORTAR = new BlockMortar();
    public static final Block ANIMATOR = new BlockAnimator();
    public static final Block ALEMBIC = new BlockAlembic();
    public static final Block MOSS_BED = new BlockMossBed();
    public static final Block ROPE = new BlockRope();
    public static final Block DAMP_TORCH = new BlockDampTorch();
    public static final Block WALKWAY = new BlockWalkway();
    public static final Block WOOD_CHIP_PATH = new BlockChipPath();
    public static final Block THATCH_ROOF = new BlockThatchRoof();
    public static final Block MUD_BRICK_ROOF = new BlockMudBrickRoof();
    public static final Block REPELLER = new BlockRepeller();
    public static final Block WAYSTONE = new BlockWaystone();
    public static final Block CAVING_ROPE_LIGHT = new BlockCavingRopeLight();
    public static final Block GROUND_ITEM = new BlockGroundItem();
    
    public static final Set<Block> BLOCKS = new LinkedHashSet<>();
    public static final List<ItemBlock> ITEM_BLOCKS = new ArrayList<ItemBlock>();
    
    private BlockRegistry() {
    }

    public static void preInit() {
        try {
            for (Field field : BlockRegistry.class.getDeclaredFields()) {
                Object obj = field.get(null);
                if (obj instanceof Block) {
                    Block block = (Block) obj;
                    String name = field.getName().toLowerCase(Locale.ENGLISH);
                    registerBlock(name, block);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerBlock(String name, Block block) {
        BLOCKS.add(block);
        block.setRegistryName(ModInfo.ID, name).setTranslationKey(ModInfo.ID + "." + name);

        ItemBlock item = null;
        if (block instanceof ICustomItemBlock)
            item = ((ICustomItemBlock) block).getItemBlock();
        else
            item = new ItemBlock(block);
        if(item != null) {
        	ITEM_BLOCKS.add(item);
        	item.setRegistryName(ModInfo.ID, name).setTranslationKey(ModInfo.ID + "." + name);
        	
        	if (BetweenlandsConfig.DEBUG.debug && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                if (block.getCreativeTab() == null)
                    TheBetweenlands.logger.warn(String.format("Block %s doesn't have a creative tab", block.getTranslationKey()));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerBlockRenderers(ModelRegistryEvent event) {
        for (Block block : BlockRegistry.BLOCKS) {
            if (block instanceof IStateMappedBlock) {
                AdvancedStateMap.Builder builder = new AdvancedStateMap.Builder();
                ((IStateMappedBlock) block).setStateMapper(builder);
                ModelLoader.setCustomStateMapper(block, builder.build());
            }
            
            Item item = Item.getItemFromBlock(block);
            
            if(item != Items.AIR) {
	            if (block instanceof ICustomItemBlock) {
	                ICustomItemBlock customItemBlock = (ICustomItemBlock) block;
	                ItemStack renderedItem = customItemBlock.getRenderedItem();
	                if (!renderedItem.isEmpty()) {
	                	Map<Integer, ResourceLocation> map = TheBetweenlands.proxy.getItemModelMap(renderedItem.getItem());
	                	ModelResourceLocation model = (ModelResourceLocation) map.get(renderedItem.getMetadata());
	                    ModelLoader.setCustomModelResourceLocation(item, 0, model);
	                    continue;
	                }
	            }
	            ResourceLocation name = block.getRegistryName();
	            if (block instanceof ISubtypeItemBlockModelDefinition) {
	                ISubtypeItemBlockModelDefinition subtypeBlock = (ISubtypeItemBlockModelDefinition) block;
	                for (int i = 0; i < subtypeBlock.getSubtypeNumber(); i++) {
	                    int meta = subtypeBlock.getSubtypeMeta(i);
	                    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name.getNamespace() + ":" + String.format(subtypeBlock.getSubtypeName(meta), name.getPath()), "inventory"));
	                }
	            } else {
	                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name, "inventory"));
	            }
        	}
        }
    }

    public interface ICustomItemBlock {
        /**
         * Returns a custom item for this block.
         *
         * @return
         */
        @Nullable
        default ItemBlock getItemBlock() {
            return getDefaultItemBlock((Block) this);
        }
        
        /**
         * Returns the default item for the specified block
         * @param block
         * @return
         */
        static ItemBlock getDefaultItemBlock(Block block) {
        	if (Item.getItemFromBlock(block) != Items.AIR)
                return (ItemBlock) Item.getItemFromBlock(block);
            else
                return new ItemBlock(block);
        }

        /**
         * Returns which item this block should be rendered as
         *
         * @return
         */
        @SideOnly(Side.CLIENT)
        default ItemStack getRenderedItem() {
            return ItemStack.EMPTY;
        }
    }

    public interface ISubtypeItemBlockModelDefinition {
        /**
         * Returns the amount of subtypes
         *
         * @return
         */
        int getSubtypeNumber();

        /**
         * Returns the name of this subtype.
         * String is formatted, use %s for the normal registry name.
         *
         * @param meta
         * @return
         */
        String getSubtypeName(int meta);

        /**
         * Returns the metadata for the specified subtype
         *
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
         *
         * @param builder
         */
        @SideOnly(Side.CLIENT)
        void setStateMapper(AdvancedStateMap.Builder builder);
    }

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        for (Block block : BLOCKS) {
            registry.register(block);
        }
    }
}



