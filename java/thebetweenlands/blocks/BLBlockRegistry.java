package thebetweenlands.blocks;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import thebetweenlands.blocks.container.BlockAnimator;
import thebetweenlands.blocks.container.BlockBLDualFurnace;
import thebetweenlands.blocks.container.BlockBLFurnace;
import thebetweenlands.blocks.container.BlockBLWorkbench;
import thebetweenlands.blocks.container.BlockWeedWoodChest;
import thebetweenlands.blocks.ores.BlockGenericOre;
import thebetweenlands.blocks.ores.BlockMiddleGemOre;
import thebetweenlands.blocks.plants.BlockAlgae;
import thebetweenlands.blocks.plants.BlockBLSmallPlants;
import thebetweenlands.blocks.plants.BlockBlackHatMushroom;
import thebetweenlands.blocks.plants.BlockFlatHeadMushroom;
import thebetweenlands.blocks.plants.BlockHanger;
import thebetweenlands.blocks.plants.BlockMireCoral;
import thebetweenlands.blocks.plants.BlockPitcherPlant;
import thebetweenlands.blocks.plants.BlockSwampPlant;
import thebetweenlands.blocks.plants.BlockSwampReed;
import thebetweenlands.blocks.plants.BlockSwampReedUW;
import thebetweenlands.blocks.plants.BlockVenusFlyTrap;
import thebetweenlands.blocks.plants.BlockVolarpad;
import thebetweenlands.blocks.plants.BlockWaterFlower;
import thebetweenlands.blocks.plants.BlockWaterFlowerStalk;
import thebetweenlands.blocks.plants.BlockWeedWoodBush;
import thebetweenlands.blocks.plants.BlockWeepingBlue;
import thebetweenlands.blocks.plants.DoubleHeightPlant;
import thebetweenlands.blocks.plants.roots.BlockRoot;
import thebetweenlands.blocks.plants.roots.BlockRootUW;
import thebetweenlands.blocks.stalactite.BlockStalactite;
import thebetweenlands.blocks.structure.BlockDruidAltar;
import thebetweenlands.blocks.structure.BlockDruidSpawner;
import thebetweenlands.blocks.structure.BlockDruidStone;
import thebetweenlands.blocks.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.blocks.terrain.BlockBetweenstone;
import thebetweenlands.blocks.terrain.BlockDeadGrass;
import thebetweenlands.blocks.terrain.BlockFallenLeaves;
import thebetweenlands.blocks.terrain.BlockGenericStone;
import thebetweenlands.blocks.terrain.BlockMud;
import thebetweenlands.blocks.terrain.BlockPeat;
import thebetweenlands.blocks.terrain.BlockSilt;
import thebetweenlands.blocks.terrain.BlockSwampDirt;
import thebetweenlands.blocks.terrain.BlockSwampGrass;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.blocks.terrain.BlockWisp;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.blocks.tree.BlockBLSapling;
import thebetweenlands.blocks.tree.BlockRubberLog;
import thebetweenlands.blocks.tree.BlockTreeFungus;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import cpw.mods.fml.common.registry.GameRegistry;

public class BLBlockRegistry
{
	// LIST WITH ALL BLOCKS IN THIS CLASS
	public static final List<Block> BLOCKS = new LinkedList<Block>();

	// PORTAL
	public static BlockPortal portalBlock = new BlockPortal();

	// TERRAIN
	public static final Block betweenstone = new BlockBetweenstone();
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
	public static final Block fallenLeaves = new BlockFallenLeaves();

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
	
	public static final Block weedwoodLeaves = new BlockBLLeaves("weedwoodLeaves");
	public static final Block sapTreeLeaves = new BlockBLLeaves("sapTreeLeaves");
	public static final Block rubberTreeLeaves = new BlockBLLeaves("rubberTreeLeaves");
	//public static final Block spiritTreeLeaves = new BlockBLLeaves("spiritTreeLeaves"); - not sure about these
	
	public static final Block weedwoodLog = new BlockBLLog("weedwoodLog");
	public static final Block weedwood = new BlockBLLog("weedwood");
	public static final Block weedwoodBark = new BlockBLLog("weedwoodBark");
	public static final Block sapTreeLog = new BlockBLLog("sapTreeLog");
	public static final Block rubberTreeLog = new BlockRubberLog("rubberTreeLog");
	public static final Block weedwoodBush = new BlockWeedWoodBush().setBlockName("thebetweenlands.weedwoodBush").setCreativeTab(ModCreativeTabs.plants);

	// WOOD
	public static final Block weedwoodPlanks = new BlockWeedWoodPlanks();

	// DOUBLE PLANTS
	public static final Block sundew = new DoubleHeightPlant("Sundew", 0.8F);
	
	//PLANTS
	public static final BlockSwampReed swampReed = new BlockSwampReed();
	public static final BlockSwampReedUW swampReedUW = new BlockSwampReedUW();
	public static final BlockMireCoral mireCoral = new BlockMireCoral();
	public static final BlockWaterFlowerStalk waterFlowerStalk = new BlockWaterFlowerStalk();
	public static final BlockWaterFlower waterFlower = new BlockWaterFlower();
	public static final BlockRootUW rootUW = new BlockRootUW();
	public static final BlockRoot root = new BlockRoot();
	public static final BlockBlackHatMushroom blackHatMushroom = new BlockBlackHatMushroom();
	public static final BlockFlatHeadMushroom flatHeadMushroom = new BlockFlatHeadMushroom();
	public static final BlockPitcherPlant pitcherPlant = new BlockPitcherPlant();
	public static final BlockSwampPlant swampPlant = new BlockSwampPlant();
	public static final BlockVenusFlyTrap venusFlyTrap = new BlockVenusFlyTrap();
	public static final BlockVolarpad volarpad = new BlockVolarpad();
	public static final BlockWeepingBlue weepingBlue = new BlockWeepingBlue();
	
	// SMALL PLANTS
	public static final Block catTail = new BlockBLSmallPlants("cattail");
	public static final Block swampTallGrass = new BlockBLSmallPlants("swampTallGrass");
	public static final Block nettleFlowered = new BlockBLSmallPlants("nettleFlowered");
	public static final Block nettle = new BlockBLSmallPlants("nettle");

	// UNDERGROWTH
	public static final Block hanger = new BlockHanger();
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
	public static final Block mudBrick = new BlockBLGenericDeco("mudBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block angryBetweenstone = new BlockBLGenericDeco("angryBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block betweenstoneTiles = new BlockBLGenericDeco("betweenstoneTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block carvedCrag = new BlockBLGenericDeco("carvedCrag", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block cragrockBrick = new BlockBLGenericDeco("cragrockBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block cragTiles = new BlockBLGenericDeco("cragTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block goldCircleBrick = new BlockBLGenericDeco("goldCircleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothBetweenstone = new BlockBLGenericDeco("smoothBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block solidTar = new BlockBLGenericDeco("solidTar", Material.rock).setHardness(15F).setResistance(20.0F).setStepSound(Block.soundTypeStone);
	public static final Block sulphurBlock = new BlockBLGenericDeco("sulphurBlock", Material.rock).setHardness(2F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block octineBlock = new BlockBLGenericDeco("octineBlock", Material.rock).setHardness(5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block valoniteBlock = new BlockBLGenericDeco("valoniteBlock", Material.rock).setHardness(10F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block templeBrick = new BlockBLGenericDeco("templeBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothTempleBrick = new BlockBLGenericDeco("smoothTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block carvedTempleBrick = new BlockBLGenericDeco("carvedTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block dungeonTile = new BlockBLGenericDeco("dungeonTile", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block dungeonWall = new BlockBLGenericDeco("dungeonWall", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block thatch = new BlockBLGenericDeco("thatch", Material.cloth).setHardness(0.5F).setResistance(1.0F).setStepSound(Block.soundTypeGrass);
	public static final Block rubberTreePlanks = new BlockBLGenericDeco("rubberTreePlanks", Material.wood).setHardness(2F).setResistance(5.0F).setStepSound(Block.soundTypeWood);
	public static final Block blockOfCompost = new BlockBLGenericDeco("blockOfCompost", Material.ground).setHardness(2F).setResistance(5.0F).setStepSound(Block.soundTypeGrass);
	
	// ALTARS
	public static final Block druidAltar = new BlockDruidAltar();

	// STAIRS, SLABS, WALLS, FENCES
	public static final Block mudBrickStairs = new BlockBLStairs(mudBrick, 0).setBlockName("thebetweenlands.mudBrickStairs");
	public static final Block cragrockBrickStairs = new BlockBLStairs(cragrockBrick, 0).setBlockName("thebetweenlands.cragrockBrickStairs");
	public static final Block smoothBetweenstoneStairs = new BlockBLStairs(smoothBetweenstone, 0).setBlockName("thebetweenlands.smoothBetweenstoneStairs");
	public static final Block solidTarStairs = new BlockBLStairs(solidTar, 0).setBlockName("thebetweenlands.solidTarStairs");	
	public static final Block templeBrickStairs = new BlockBLStairs(templeBrick, 0).setBlockName("thebetweenlands.templeBrickStairs");
	public static final Block weedwoodPlankStairs = new BlockBLStairs(weedwoodPlanks, 0).setBlockName("thebetweenlands.weedwoodPlankStairs");
	public static final Block rubberTreePlankStairs = new BlockBLStairs(rubberTreePlanks, 0).setBlockName("thebetweenlands.rubberTreePlankStairs");
	public static final Block mudBrickWall = new BlockBLWall(mudBrick, 0).setBlockName("thebetweenlands.mudBrickWall");
	public static final Block cragrockWall = new BlockBLWall(cragrockBrick, 0).setBlockName("thebetweenlands.cragrockWall");
	public static final Block smoothBetweenstoneWall = new BlockBLWall(smoothBetweenstone, 0).setBlockName("thebetweenlands.smoothBetweenstoneWall");
	public static final Block solidTarWall = new BlockBLWall(solidTar, 0).setBlockName("thebetweenlands.solidTarWall");
	public static final Block templeBrickWall = new BlockBLWall(templeBrick, 0).setBlockName("thebetweenlands.templeBrickWall");
	public static final Block weedwoodPlankFence = new BlockBLFence("weedwoodPlanks", Material.wood).setBlockName("thebetweenlands.weedwoodPlankFence");
	public static final Block rubberTreePlankFence = new BlockBLFence("rubberTreePlanks", Material.wood).setBlockName("thebetweenlands.rubberTreePlankFence");

	// OTHER THINGS
	public static final Block druidSpawner = new BlockDruidSpawner("darkDruid");

	public static void init() {
		initBlocks();
		registerBlocks();
		registerProperties();
	}

	private static void initBlocks() {

	}

	private static void registerBlocks() {
		try {
			for (Field f : BLBlockRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Block)
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
