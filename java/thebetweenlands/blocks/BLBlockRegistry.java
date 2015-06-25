package thebetweenlands.blocks;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.container.BlockAnimator;
import thebetweenlands.blocks.container.BlockBLDualFurnace;
import thebetweenlands.blocks.container.BlockBLFurnace;
import thebetweenlands.blocks.container.BlockBLWorkbench;
import thebetweenlands.blocks.container.BlockPurifier;
import thebetweenlands.blocks.container.BlockWeedWoodChest;
import thebetweenlands.blocks.ores.BlockGenericOre;
import thebetweenlands.blocks.ores.BlockMiddleGemOre;
import thebetweenlands.blocks.plants.BlockAlgae;
import thebetweenlands.blocks.plants.BlockBLHanger;
import thebetweenlands.blocks.plants.BlockBLSmallPlants;
import thebetweenlands.blocks.plants.BlockBlackHatMushroom;
import thebetweenlands.blocks.plants.BlockBlubCappedMushroomHead;
import thebetweenlands.blocks.plants.BlockBlubCappedMushroomStalk;
import thebetweenlands.blocks.plants.BlockBogBean;
import thebetweenlands.blocks.plants.BlockBulbCappedMushroom;
import thebetweenlands.blocks.plants.BlockDeepWaterCoral;
import thebetweenlands.blocks.plants.BlockFlatHeadMushroom;
import thebetweenlands.blocks.plants.BlockGoldenClub;
import thebetweenlands.blocks.plants.BlockMarshMarigold;
import thebetweenlands.blocks.plants.BlockMireCoral;
import thebetweenlands.blocks.plants.BlockPitcherPlant;
import thebetweenlands.blocks.plants.BlockPoisonIvy;
import thebetweenlands.blocks.plants.BlockRottenLog;
import thebetweenlands.blocks.plants.BlockSwampKelp;
import thebetweenlands.blocks.plants.BlockSwampPlant;
import thebetweenlands.blocks.plants.BlockSwampReed;
import thebetweenlands.blocks.plants.BlockSwampReedUW;
import thebetweenlands.blocks.plants.BlockThorns;
import thebetweenlands.blocks.plants.BlockVenusFlyTrap;
import thebetweenlands.blocks.plants.BlockVolarpad;
import thebetweenlands.blocks.plants.BlockWallPlants;
import thebetweenlands.blocks.plants.BlockWaterFlower;
import thebetweenlands.blocks.plants.BlockWaterFlowerStalk;
import thebetweenlands.blocks.plants.BlockWaterWeeds;
import thebetweenlands.blocks.plants.BlockWeedWoodBush;
import thebetweenlands.blocks.plants.BlockWeepingBlue;
import thebetweenlands.blocks.plants.DoubleHeightPlant;
import thebetweenlands.blocks.plants.crops.BlockBLGenericCrop;
import thebetweenlands.blocks.plants.roots.BlockRoot;
import thebetweenlands.blocks.plants.roots.BlockRootUW;
import thebetweenlands.blocks.stalactite.BlockStalactite;
import thebetweenlands.blocks.structure.BlockDruidAltar;
import thebetweenlands.blocks.structure.BlockDruidSpawner;
import thebetweenlands.blocks.structure.BlockDruidStone;
import thebetweenlands.blocks.terrain.BlockBLFallenLeaves;
import thebetweenlands.blocks.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.blocks.terrain.BlockBetweenstone;
import thebetweenlands.blocks.terrain.BlockDeadGrass;
import thebetweenlands.blocks.terrain.BlockDentrothyst;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.blocks.terrain.BlockGenericStone;
import thebetweenlands.blocks.terrain.BlockMud;
import thebetweenlands.blocks.terrain.BlockPeat;
import thebetweenlands.blocks.terrain.BlockPuddle;
import thebetweenlands.blocks.terrain.BlockSilt;
import thebetweenlands.blocks.terrain.BlockSlimyDirt;
import thebetweenlands.blocks.terrain.BlockSlimyGrass;
import thebetweenlands.blocks.terrain.BlockSwampDirt;
import thebetweenlands.blocks.terrain.BlockSwampGrass;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.blocks.terrain.BlockTarFluid;
import thebetweenlands.blocks.terrain.BlockWisp;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.blocks.tree.BlockBLPortalFrame;
import thebetweenlands.blocks.tree.BlockBLSapling;
import thebetweenlands.blocks.tree.BlockRubberLog;
import thebetweenlands.blocks.tree.BlockTreeFungus;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.block.ItemBlockSlab;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BLBlockRegistry
{
	// LIST WITH ALL BLOCKS IN THIS CLASS
	public static final List<Block> BLOCKS = new LinkedList<Block>();

	// PORTAL
	public static BlockTreePortal treePortalBlock = new BlockTreePortal();

	// TERRAIN
	public static final Block betweenstone = new BlockBetweenstone();
	public static final Block limestone = new BlockBLGenericDeco("limestone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block genericStone = new BlockGenericStone();
	public static final Block druidStone1 = new BlockDruidStone("druidStone1");
	public static final Block druidStone2 = new BlockDruidStone("druidStone2");
	public static final Block druidStone3 = new BlockDruidStone("druidStone3");
	public static final Block druidStone4 = new BlockDruidStone("druidStone4");
	public static final Block druidStone5 = new BlockDruidStone("druidStone5");
	public static final Block betweenlandsBedrock = new BlockBetweenlandsBedrock();
	public static final Block swampDirt = new BlockSwampDirt();
	public static final Block swampGrass = new BlockSwampGrass();
	public static final Block deadGrass = new BlockDeadGrass();
	public static final Block silt = new BlockSilt();
	public static final Block mud = new BlockMud();
	public static final Block swampWater = new BlockSwampWater();
	public static final Block algae = new BlockAlgae();
	public static final Block sulfurTorch = new BlockSulfurTorch();
	public static final Block dampTorch = new BlockDampTorch();
	public static final Block peat = new BlockPeat();
	public static final BlockWisp wisp = new BlockWisp();
	public static final BlockSludge sludge = new BlockSludge();
	public static final Block fallenLeaves = new BlockBLFallenLeaves("fallenLeaves");
	public static final Block purpleFallenLeaves = new BlockBLFallenLeaves("purpleFallenLeaves");
	public static final Block slimyGrass = new BlockSlimyGrass();
	public static final Block slimyDirt = new BlockSlimyDirt();
	public static final Block tarFluid = new BlockTarFluid();
	public static final Block puddle = new BlockPuddle();
	public static final Block farmedDirt = new BlockFarmedDirt();
	public static final Block dentrothyst = new BlockDentrothyst();

	// ORES @Params (name/texture, drops this item)
	// hardness & resistance could be set on an individual basis but aren't atm.
	public static final Block aquaMiddleGemOre = new BlockMiddleGemOre("aquaMiddleGemOre", null);
	public static final Block crimsonMiddleGemOre = new BlockMiddleGemOre("crimsonMiddleGemOre", null);
	public static final Block greenMiddleGemOre = new BlockMiddleGemOre("greenMiddleGemOre", null);
	public static final Block octineOre = new BlockGenericOre("octineOre", null).setLightLevel(0.875F); //setting null drops item block
	public static final Block sulfurOre = new BlockGenericOre("sulfurOre", EnumMaterialsBL.SULFUR);
	public static final Block valoniteOre = new BlockGenericOre("valoniteOre", EnumMaterialsBL.VALONITE_SHARD);
	public static final Block lifeCrystalOre = new BlockGenericOre("lifeCrystalOre", EnumMaterialsBL.LIFE_CRYSTAL);

	// TREES
	public static final Block saplingWeedwood = new BlockBLSapling("saplingWeedwood");
	public static final Block saplingSapTree = new BlockBLSapling("saplingSapTree");
	public static final Block saplingSpiritTree = new BlockBLSapling("saplingSpiritTree");
	public static final Block saplingRubberTree = new BlockBLSapling("saplingRubberTree");
	public static final Block saplingPurpleRain = new BlockBLSapling("saplingPurpleRain");

	public static final Block weedwoodLeaves = new BlockBLLeaves("weedwoodLeaves");
	public static final Block sapTreeLeaves = new BlockBLLeaves("sapTreeLeaves");
	public static final Block rubberTreeLeaves = new BlockBLLeaves("rubberTreeLeaves");
	//public static final Block spiritTreeLeaves = new BlockBLLeaves("spiritTreeLeaves"); - not sure about these
	public static final Block purpleRainLeavesLight = new BlockBLLeaves("purpleRainLeavesLight");
	public static final Block purpleRainLeavesDark = new BlockBLLeaves("purpleRainLeavesDark");

	public static final Block weedwoodLog = new BlockBLLog("weedwoodLog");
	public static final Block weedwood = new BlockBLLog("weedwood");
	public static final Block weedwoodBark = new BlockBLLog("weedwoodBark");
	public static final Block rottenWeedwoodBark = new BlockBLLog("rottenWeedwoodBark");
	public static final Block sapTreeLog = new BlockBLLog("sapTreeLog");
	public static final Block rubberTreeLog = new BlockRubberLog("rubberTreeLog");
	public static final Block weedwoodBush = new BlockWeedWoodBush().setBlockName("thebetweenlands.weedwoodBush").setCreativeTab(ModCreativeTabs.plants);
	public static final Block portalBark = new BlockBLLog("portalBark");
	public static final Block portalBarkFrame = new BlockBLPortalFrame();
	public static final Block rottenLog = new BlockRottenLog();
	public static final Block purpleRainLog = new BlockBLLog("purpleRainLog");

	// WOOD
	public static final Block weedwoodPlanks = new BlockWeedWoodPlanks();

	// DOUBLE PLANTS
	public static final DoubleHeightPlant sundew = new DoubleHeightPlant("Sundew", 0.8F){
		@Override
		@SideOnly(Side.CLIENT)
		public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
			if(world.rand.nextInt(35) == 0) {
				TheBetweenlands.proxy.spawnCustomParticle("fly", world, x, y + 1, z, 0.0D, 0.0D, 0.0D, 0);
			}
		}
	}.setRenderType(BlockRenderIDs.MODEL_PLANT.id());
	public static final Block doubleSwampTallgrass = new DoubleHeightPlant("DoubleSwampTallgrass", 0.8F); 
	public static final Block phragmites = new DoubleHeightPlant("Phragmites", 0.8F){
		@Override
		@SideOnly(Side.CLIENT)
		public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
			if(world.rand.nextInt(15) == 0) {
				if(world.rand.nextInt(6) != 0) {
					TheBetweenlands.proxy.spawnCustomParticle("fly", world, x, y, z, 0.0D, 0.0D, 0.0D, 0);
				} else {
					TheBetweenlands.proxy.spawnCustomParticle("moth", world, x, y, z, 0.0D, 0.0D, 0.0D, 0);
				}
			}
		}
	}; 
	public static final Block tallCattail = new DoubleHeightPlant("TallCattail", 0.8F); 
	public static final Block cardinalFlower = new DoubleHeightPlant("CardinalFlower", 0.8F); 
	public static final Block broomsedge = new DoubleHeightPlant("BroomSedge", 0.8F); 
	public static final BlockWeepingBlue weepingBlue = new BlockWeepingBlue();
	public static final BlockPitcherPlant pitcherPlant = new BlockPitcherPlant();

	//PLANTS
	public static final BlockSwampReed swampReed = new BlockSwampReed();
	public static final BlockSwampReedUW swampReedUW = new BlockSwampReedUW();
	public static final BlockBogBean bogBean = new BlockBogBean();
	public static final BlockGoldenClub goldenClub = new BlockGoldenClub();
	public static final BlockMarshMarigold marshMarigold = new BlockMarshMarigold();
	public static final BlockSwampKelp waterWeeds = new BlockSwampKelp();
	public static final BlockWaterWeeds waterWeedsSmall = new BlockWaterWeeds();
	public static final BlockMireCoral mireCoral = new BlockMireCoral();
	public static final BlockDeepWaterCoral deepWaterCoral = new BlockDeepWaterCoral();
	public static final BlockWaterFlowerStalk waterFlowerStalk = new BlockWaterFlowerStalk();
	public static final BlockWaterFlower waterFlower = new BlockWaterFlower();
	public static final BlockRootUW rootUW = new BlockRootUW();
	public static final BlockRoot root = new BlockRoot();
	public static final BlockBlackHatMushroom blackHatMushroom = new BlockBlackHatMushroom();
	public static final BlockFlatHeadMushroom flatHeadMushroom = new BlockFlatHeadMushroom();
	public static final BlockBulbCappedMushroom bulbCappedMushroom = new BlockBulbCappedMushroom();
	public static final BlockSwampPlant swampPlant = new BlockSwampPlant();
	public static final BlockVenusFlyTrap venusFlyTrap = new BlockVenusFlyTrap();
	public static final BlockVolarpad volarpad = new BlockVolarpad();
	public static final BlockThorns thorns = new BlockThorns();
	public static final BlockPoisonIvy poisonIvy = new BlockPoisonIvy();
	public static final Block wallPlants = new BlockWallPlants();

	public static final Block hugeMushroomStalk = new BlockBlubCappedMushroomStalk();
	public static final Block hugeMushroomTop = new BlockBlubCappedMushroomHead();
	public static final Block middleFruitBush = new BlockBLGenericCrop("middleFruitBush");

	// SMALL PLANTS
	public static final Block catTail = new BlockBLSmallPlants("cattail");
	public static final Block swampTallGrass = new BlockBLSmallPlants("swampTallGrass");
	public static final Block shoots = new BlockBLSmallPlants("shoots");
	public static final Block nettleFlowered = new BlockBLSmallPlants("nettleFlowered");
	public static final Block nettle = new BlockBLSmallPlants("nettle");
	public static final Block arrowArum = new BlockBLSmallPlants("arrowArum");
	public static final Block buttonBush = new BlockBLSmallPlants("buttonBush");
	public static final Block marshHibiscus = new BlockBLSmallPlants("marshHibiscus");
	public static final Block pickerelWeed = new BlockBLSmallPlants("pickerelWeed");
	public static final Block softRush = new BlockBLSmallPlants("softRush");
	public static final Block marshMallow = new BlockBLSmallPlants("marshMallow");
	public static final Block milkweed = new BlockBLSmallPlants("milkweed");
	public static final Block blueIris = new BlockBLSmallPlants("blueIris");
	public static final Block copperIris = new BlockBLSmallPlants("copperIris");
	public static final Block blueEyedGrass = new BlockBLSmallPlants("blueEyedGrass");
	public static final Block boneset = new BlockBLSmallPlants("boneset");
	public static final Block bottleBrushGrass = new BlockBLSmallPlants("bottleBrushGrass");

	// UNDERGROWTH
	public static final Block hanger = new BlockBLHanger("hanger");
	public static final Block purpleHanger = new BlockBLHanger("purpleHanger");
	public static final Block purpleHangerFlowered = new BlockBLHanger("purpleHangerFlowered");
	public static final Block treeFungus = new BlockTreeFungus();

	// DECORATIONS AND UTILITIES
	public static final Block weedwoodCraftingTable = new BlockBLWorkbench();
	public static final Block weedwoodChest = new BlockWeedWoodChest();
	public static final Block furnaceBL = new BlockBLFurnace(false).setBlockName("thebetweenlands.furnaceBL");
	public static final Block furnaceBLLit = new BlockBLFurnace(true).setBlockName("thebetweenlands.furnaceBLLit").setLightLevel(0.875F);  
	public static final Block dualFurnaceBL = new BlockBLDualFurnace(false).setBlockName("thebetweenlands.dualFurnaceBL");
	public static final Block dualFurnaceBLLit = new BlockBLDualFurnace(true).setBlockName("thebetweenlands.dualFurnaceBLLit").setLightLevel(0.875F);
	public static final Block stalactite = new BlockStalactite(Material.rock).setHardness(0F).setStepSound(Block.soundTypeStone).setBlockName("thebetweenlands.stalactite").setCreativeTab(ModCreativeTabs.blocks);
	public static final Block animator = new BlockAnimator();
	public static final Block purifier = new BlockPurifier();
	public static final Block mudBrick = new BlockBLGenericDeco("mudBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block angryBetweenstone = new BlockBLGenericDeco("angryBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block betweenstoneBricks = new BlockBLGenericDeco("betweenstoneBricks", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block betweenstoneTiles = new BlockBLGenericDeco("betweenstoneTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block chiseledBetweenstone = new BlockBLGenericDeco("chiseledBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block carvedCrag = new BlockBLGenericDeco("carvedCrag", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block cragrockBrick = new BlockBLGenericDeco("cragrockBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block polishedLimestone = new BlockBLGenericDeco("polishedLimestone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block limestoneBricks = new BlockBLGenericDeco("limestoneBricks", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block limestoneTiles = new BlockBLGenericDeco("limestoneTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block chiseledLimestone = new BlockBLGenericDeco("chiseledLimestone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block cragTiles = new BlockBLGenericDeco("cragTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block goldCircleBrick = new BlockBLGenericDeco("goldCircleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothBetweenstone = new BlockBLGenericDeco("smoothBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothCragrock = new BlockBLGenericDeco("smoothCragrock", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block solidTar = new BlockBLGenericDeco("solidTar", Material.rock).setHardness(15F).setResistance(20.0F).setStepSound(Block.soundTypeStone);
	public static final Block sulphurBlock = new BlockBLGenericDeco("sulphurBlock", Material.rock).setHardness(2F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block octineBlock = new BlockBLGenericDeco("octineBlock", Material.rock).setHardness(5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block valoniteBlock = new BlockBLGenericDeco("valoniteBlock", Material.rock).setHardness(10F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block templeBrick = new BlockBLGenericDeco("templeBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block mossyTempleBrick = new BlockBLGenericDeco("mossyTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block crackedTempleBrick = new BlockBLGenericDeco("crackedTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block bloodyTempleBrick = new BlockBLGenericDeco("bloodyTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothTempleBrick = new BlockBLGenericDeco("smoothTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block carvedTempleBrick = new BlockBLGenericDeco("carvedTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block dungeonTile = new BlockBLGenericDeco("dungeonTile", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block dungeonWall = new BlockBLGenericDeco("dungeonWall", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final BlockTemplePillar templePillar = new BlockTemplePillar();
	public static final Block thatch = new BlockBLGenericDeco("thatch", Material.cloth).setHardness(0.5F).setResistance(1.0F).setStepSound(Block.soundTypeGrass);
	public static final Block rubberTreePlanks = new BlockBLGenericDeco("rubberTreePlanks", Material.wood).setHardness(2F).setResistance(5.0F).setStepSound(Block.soundTypeWood);
	public static final Block purpleRainPlanks = new BlockBLGenericDeco("purpleRainPlanks", Material.wood).setHardness(2F).setResistance(5.0F).setStepSound(Block.soundTypeWood);
	public static final Block blockOfCompost = new BlockBLGenericDeco("blockOfCompost", Material.ground).setHardness(2F).setResistance(5.0F).setStepSound(Block.soundTypeGrass);
	public static final Block mireCoralBlock = new BlockBLGenericDeco("mireCoralBlock", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block deepWaterCoralBlock = new BlockBLGenericDeco("deepWaterCoralBlock", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final BlockRubberTap rubberTap = new BlockRubberTap();

	public static final Block choca = new BlockBLGenericDeco("choca", Material.rock).setHardness(15F).setResistance(20.0F).setStepSound(Block.soundTypeStone).setBlockTextureName("thebetweenlands:dave");;
	public static final Block blockWoodChipPath = new BlockWoodChipPath();
	public static final BlockWalkway blockWalkWay = new BlockWalkway();
	public static final Block blockTotem = new BlockTotem();
	public static final Block siltGlas = new BlockBLGlass();
	public static final Block siltGlasPane = new BlockBLPane("siltGlass", Material.glass, false).setHardness(0.3F).setStepSound(Block.soundTypeGlass);

	// ALTARS
	public static final Block druidAltar = new BlockDruidAltar();

	// STAIRS, SLABS, WALLS, FENCES, SIGNS, ETC.
	public static final Block betweenstoneBrickStairs = new BlockBLStairs(betweenstoneBricks, 0).setBlockName("thebetweenlands.betweenstoneBrickStairs");
	public static final Block mudBrickStairs = new BlockBLStairs(mudBrick, 0).setBlockName("thebetweenlands.mudBrickStairs");
	public static final Block cragrockBrickStairs = new BlockBLStairs(cragrockBrick, 0).setBlockName("thebetweenlands.cragrockBrickStairs");
	public static final Block limestoneBrickStairs = new BlockBLStairs(limestoneBricks, 0).setBlockName("thebetweenlands.limestoneBrickStairs");
	public static final Block smoothBetweenstoneStairs = new BlockBLStairs(smoothBetweenstone, 0).setBlockName("thebetweenlands.smoothBetweenstoneStairs");
	public static final Block solidTarStairs = new BlockBLStairs(solidTar, 0).setBlockName("thebetweenlands.solidTarStairs");	
	public static final Block templeBrickStairs = new BlockBLStairs(templeBrick, 0).setBlockName("thebetweenlands.templeBrickStairs");
	public static final Block weedwoodPlankStairs = new BlockBLStairs(weedwoodPlanks, 0).setBlockName("thebetweenlands.weedwoodPlankStairs");
	public static final Block rubberTreePlankStairs = new BlockBLStairs(rubberTreePlanks, 0).setBlockName("thebetweenlands.rubberTreePlankStairs");
	public static final Block purpleRainPlankStairs = new BlockBLStairs(purpleRainPlanks, 0).setBlockName("thebetweenlands.purpleRainPlankStairs");
	public static final Block betweenstoneBrickWall = new BlockBLWall(betweenstoneBricks, 0).setBlockName("thebetweenlands.betweenstoneBrickWall");
	public static final Block mudBrickWall = new BlockBLWall(mudBrick, 0).setBlockName("thebetweenlands.mudBrickWall");
	public static final Block cragrockWall = new BlockBLWall(cragrockBrick, 0).setBlockName("thebetweenlands.cragrockWall");
	public static final Block limestoneBrickWall = new BlockBLWall(limestoneBricks, 0).setBlockName("thebetweenlands.limestoneBrickWall");
	public static final Block smoothBetweenstoneWall = new BlockBLWall(smoothBetweenstone, 0).setBlockName("thebetweenlands.smoothBetweenstoneWall");
	public static final Block solidTarWall = new BlockBLWall(solidTar, 0).setBlockName("thebetweenlands.solidTarWall");
	public static final Block templeBrickWall = new BlockBLWall(templeBrick, 0).setBlockName("thebetweenlands.templeBrickWall");
	public static final Block weedwoodPlankFence = new BlockBLFence("weedwoodPlanks", Material.wood).setBlockName("thebetweenlands.weedwoodPlankFence");
	public static final Block rubberTreePlankFence = new BlockBLFence("rubberTreePlanks", Material.wood).setBlockName("thebetweenlands.rubberTreePlankFence");
	public static final Block purpleRainPlankFence = new BlockBLFence("purpleRainPlanks", Material.wood).setBlockName("thebetweenlands.purpleRainPlankFence");
	public static final Block weedwoodPlankFenceGate = new BlockBLFenceGate("weedwoodPlanks");
	public static final Block rubberTreePlankFenceGate = new BlockBLFenceGate("rubberTreePlanks");
	public static final Block purpleRainPlankFenceGate = new BlockBLFenceGate("purpleRainPlanks");
	public static final Block weedwoodJukebox = new BlockBLJukebox("weedwood");
	public static final Block weedwoodLadder = new BlockBLLadder("weedwood");
	public static final Block weedwoodPlankButton = new BlockBLButton("weedwoodPlanks", true);
	public static final Block betweenstoneButton = new BlockBLButton("smoothBetweenstone", false);
	public static final Block weedwoodPlankPressurePlate = new BlockBLPressurePlate("weedwoodPlanks", Material.wood, BlockPressurePlate.Sensitivity.everything);
	public static final Block betweenstonePressurePlate = new BlockBLPressurePlate("betweenstone", Material.rock, BlockPressurePlate.Sensitivity.mobs);
	public static final Block octinePressurePlate = new BlockBLPressurePlate("octineBlock", Material.rock, BlockPressurePlate.Sensitivity.players);
	public static final Block weedwoodLever = new BlockBLLever();
	public static final BlockBLFlowerPot mudFlowerPot = new BlockBLFlowerPot();

	//DOORS
	public static final Block doorWeedwood = new BlockBLDoor("weedwood", Material.wood);
	public static final Block doorOctine = new BlockBLDoor("octine", Material.rock);
	public static final Block trapDoorWeedwood = new BlockBLTrapDoor("weedwood", Material.wood);
	public static final Block trapDoorOctine = new BlockBLTrapDoor("octine", Material.rock);

	// OTHER THINGS
	public static final Block druidSpawner = new BlockDruidSpawner("darkDruid");

	// SLABS
	public static final Block weedwoodPlankSlab = new BlockBLSlabPlanks(false, Material.wood, "weedwoodPlanks", null);
	public static final Block weedwoodPlankSlabDouble = new BlockBLSlabPlanks(true, Material.wood, "weedwoodPlanks", weedwoodPlankSlab);
	public static final Block rubberTreePlankSlab = new BlockBLSlabPlanks(false, Material.wood, "rubberTreePlanks", null);
	public static final Block rubberTreePlankSlabDouble = new BlockBLSlabPlanks(true, Material.wood, "rubberTreePlanks", rubberTreePlankSlab);
	public static final Block purpleRainPlankSlab = new BlockBLSlabPlanks(false, Material.wood, "purpleRainPlanks", null);
	public static final Block purpleRainPlankSlabDouble = new BlockBLSlabPlanks(true, Material.wood, "purpleRainPlanks", rubberTreePlankSlab);
	public static final Block betweenstoneBrickSlab = new BlockBLSlabStone(false, Material.rock, "betweenstoneBricks", null);
	public static final Block betweenstoneBrickSlabDouble = new BlockBLSlabStone(true, Material.rock, "betweenstoneBricks", betweenstoneBrickSlab);
	public static final Block mudBrickSlab = new BlockBLSlabStone(false, Material.rock, "mudBrick", null);
	public static final Block mudBrickSlabDouble = new BlockBLSlabStone(true, Material.rock, "mudBrick", mudBrickSlab);
	public static final Block cragrockBrickSlab = new BlockBLSlabStone(false, Material.rock, "cragrockBrick", null);
	public static final Block cragrockBrickSlabDouble = new BlockBLSlabStone(true, Material.rock, "cragrockBrick", cragrockBrickSlab);
	public static final Block limestoneBrickSlab = new BlockBLSlabStone(false, Material.rock, "limestoneBricks", null);
	public static final Block limestoneBrickSlabDouble = new BlockBLSlabStone(true, Material.rock, "limestoneBricks", limestoneBrickSlab);
	public static final Block smoothBetweenstoneSlab = new BlockBLSlabStone(false, Material.rock, "smoothBetweenstone", null);
	public static final Block smoothBetweenstoneSlabDouble = new BlockBLSlabStone(true, Material.rock, "smoothBetweenstone", smoothBetweenstoneSlab);
	public static final Block solidTarSlab = new BlockBLSlabStone(false, Material.rock, "solidTar", null);
	public static final Block solidTarSlabDouble = new BlockBLSlabStone(true, Material.rock, "solidTar", solidTarSlab);
	public static final Block templeBrickSlab = new BlockBLSlabStone(false, Material.rock, "templeBrick", null);
	public static final Block templeBrickSlabDouble = new BlockBLSlabStone(true, Material.rock, "templeBrick", templeBrickSlab);

	public static void registerSlabs() {
		GameRegistry.registerBlock(weedwoodPlankSlab, ItemBlockSlab.class, weedwoodPlankSlab.getUnlocalizedName(), weedwoodPlankSlab, weedwoodPlankSlabDouble, false);
		GameRegistry.registerBlock(weedwoodPlankSlabDouble, ItemBlockSlab.class, weedwoodPlankSlabDouble.getUnlocalizedName() + "Double", weedwoodPlankSlab, weedwoodPlankSlabDouble, true);

		GameRegistry.registerBlock(rubberTreePlankSlab, ItemBlockSlab.class, rubberTreePlankSlab.getUnlocalizedName(), rubberTreePlankSlab, rubberTreePlankSlabDouble, false);
		GameRegistry.registerBlock(rubberTreePlankSlabDouble, ItemBlockSlab.class, rubberTreePlankSlabDouble.getUnlocalizedName() + "Double", rubberTreePlankSlab, rubberTreePlankSlabDouble, true);

		GameRegistry.registerBlock(purpleRainPlankSlab, ItemBlockSlab.class, purpleRainPlankSlab.getUnlocalizedName(), purpleRainPlankSlab, purpleRainPlankSlabDouble, false);
		GameRegistry.registerBlock(purpleRainPlankSlabDouble, ItemBlockSlab.class, purpleRainPlankSlabDouble.getUnlocalizedName() + "Double", purpleRainPlankSlab, purpleRainPlankSlabDouble, true);

		GameRegistry.registerBlock(betweenstoneBrickSlab, ItemBlockSlab.class, betweenstoneBrickSlab.getUnlocalizedName(), betweenstoneBrickSlab, betweenstoneBrickSlabDouble, false);
		GameRegistry.registerBlock(betweenstoneBrickSlabDouble, ItemBlockSlab.class, betweenstoneBrickSlabDouble.getUnlocalizedName() + "Double", betweenstoneBrickSlab, betweenstoneBrickSlabDouble, true);

		GameRegistry.registerBlock(mudBrickSlab, ItemBlockSlab.class, mudBrickSlab.getUnlocalizedName(), mudBrickSlab, mudBrickSlabDouble, false);
		GameRegistry.registerBlock(mudBrickSlabDouble, ItemBlockSlab.class, mudBrickSlabDouble.getUnlocalizedName() + "Double", mudBrickSlab, mudBrickSlabDouble, true);

		GameRegistry.registerBlock(cragrockBrickSlab, ItemBlockSlab.class, cragrockBrickSlab.getUnlocalizedName(), cragrockBrickSlab, cragrockBrickSlabDouble, false);
		GameRegistry.registerBlock(cragrockBrickSlabDouble, ItemBlockSlab.class, cragrockBrickSlabDouble.getUnlocalizedName() + "Double", cragrockBrickSlab, cragrockBrickSlabDouble, true);

		GameRegistry.registerBlock(limestoneBrickSlab, ItemBlockSlab.class, limestoneBrickSlab.getUnlocalizedName(), limestoneBrickSlab, limestoneBrickSlabDouble, false);
		GameRegistry.registerBlock(limestoneBrickSlabDouble, ItemBlockSlab.class, limestoneBrickSlabDouble.getUnlocalizedName() + "Double", limestoneBrickSlab, limestoneBrickSlabDouble, true);

		GameRegistry.registerBlock(smoothBetweenstoneSlab, ItemBlockSlab.class, smoothBetweenstoneSlab.getUnlocalizedName(), smoothBetweenstoneSlab, smoothBetweenstoneSlabDouble, false);
		GameRegistry.registerBlock(smoothBetweenstoneSlabDouble, ItemBlockSlab.class, smoothBetweenstoneSlabDouble.getUnlocalizedName() + "Double", smoothBetweenstoneSlab, smoothBetweenstoneSlabDouble, true);

		GameRegistry.registerBlock(solidTarSlab, ItemBlockSlab.class, solidTarSlab.getUnlocalizedName(), solidTarSlab, solidTarSlabDouble, false);
		GameRegistry.registerBlock(solidTarSlabDouble, ItemBlockSlab.class, solidTarSlabDouble.getUnlocalizedName() + "Double", solidTarSlab, solidTarSlabDouble, true);

		GameRegistry.registerBlock(templeBrickSlab, ItemBlockSlab.class, templeBrickSlab.getUnlocalizedName(), templeBrickSlab, templeBrickSlabDouble, false);
		GameRegistry.registerBlock(templeBrickSlabDouble, ItemBlockSlab.class, templeBrickSlabDouble.getUnlocalizedName() + "Double", templeBrickSlab, templeBrickSlabDouble, true);
	}

	public static void init() {
		initBlocks();
		registerBlocks();
		registerSlabs();
		registerProperties();
	}

	private static void initBlocks() {
	}

	private static void registerBlocks() {
		try {
			for (Field f : BLBlockRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Block && !(obj instanceof BlockBLSlabPlanks) && !(obj instanceof BlockBLSlabStone))
					registerBlock((Block) obj);
				else if (obj instanceof Block[])
					for (Block block : (Block[]) obj)
						registerBlock(block);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void registerBlock(Block block) {
		BLOCKS.add(block);
		String name = block.getUnlocalizedName();
		String[] strings = name.split("\\.");

		if (block instanceof ISubBlocksBlock)
			GameRegistry.registerBlock(block, ((ISubBlocksBlock) block).getItemBlockClass(), strings[strings.length - 1]);
		else
			GameRegistry.registerBlock(block, strings[strings.length - 1]);
	}

	private static void registerProperties() {
		//for fire etc
		aquaMiddleGemOre.setHarvestLevel("shovel", 0);
		crimsonMiddleGemOre.setHarvestLevel("shovel", 0);
		greenMiddleGemOre.setHarvestLevel("shovel", 0);
		octineOre.setHarvestLevel("pickaxe", 1);
		sulfurOre.setHarvestLevel("pickaxe", 0);
		valoniteOre.setHarvestLevel("pickaxe", 2);
		lifeCrystalOre.setHarvestLevel("pickaxe", 0);
	}

	public static interface ISubBlocksBlock
	{
		Class<? extends ItemBlock> getItemBlockClass();
	}
}
