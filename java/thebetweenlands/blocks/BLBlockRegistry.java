package thebetweenlands.blocks;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import thebetweenlands.blocks.terrain.BlockAlgae;
import thebetweenlands.blocks.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.blocks.terrain.BlockBetweenstone;
import thebetweenlands.blocks.terrain.BlockGenericOre;
import thebetweenlands.blocks.terrain.BlockGenericStone;
import thebetweenlands.blocks.terrain.BlockMud;
import thebetweenlands.blocks.terrain.BlockSilt;
import thebetweenlands.blocks.terrain.BlockSwampDirt;
import thebetweenlands.blocks.terrain.BlockSwampGrass;
import thebetweenlands.blocks.terrain.BlockSwampWater;
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
	public static final Block silt = new BlockSilt();
	public static final Block mud = new BlockMud();
	public static final Block swampWater = new BlockSwampWater();
	public static final Block algae = new BlockAlgae();
	public static final Block sulfurTorch = new BlockSulfurTorch();

	// ORES @Params (name/texture, drops this item)
	// hardness & resistance could be set on an individual basis but aren't atm.
	public static final Block aquaMiddleGemOre = new BlockGenericOre("aquaMiddleGemOre", null);
	public static final Block crimsonMiddleGemOre = new BlockGenericOre("crimsonMiddleGemOre", null);
	public static final Block greenMiddleGemOre = new BlockGenericOre("greenMiddleGemOre", null);
	public static final Block octineOre = new BlockGenericOre("octineOre", null); //setting null drops item block
	public static final Block sulfurOre = new BlockGenericOre("sulfurOre", EnumMaterialsBL.SULFUR);
	public static final Block valoniteOre = new BlockGenericOre("valoniteOre", EnumMaterialsBL.VALONITE_SHARD);
	public static final Block lifeCrystalOre = new BlockGenericOre("lifeCrystalOre", EnumMaterialsBL.LIFE_CRYSTAL);

	// TREES
	public static final Block saplingWeedWood = new BlockBLSapling("saplingWeedWood");
	public static final Block saplingSapTree = new BlockBLSapling("saplingSapTree");
	public static final Block saplingSpiritTree = new BlockBLSapling("saplingSpiritTree");
	
	public static final Block weedWoodLeaves = new BlockBLLeaves("weedWoodLeaves");
	public static final Block sapTreeLeaves = new BlockBLLeaves("sapTreeLeaves");
	//public static final Block spiritTreeLeaves = new BlockBLLeaves("spiritTreeLeaves"); - not sure about these
	
	public static final Block weedWoodLog = new BlockBLLog("weedWoodLog");
	public static final Block weedWood = new BlockBLLog("weedWood");
	public static final Block weedWoodBark = new BlockBLLog("weedWoodBark");
	public static final Block sapTreeLog = new BlockBLLog("sapTreeLog");

	// WOOD
	public static final Block weedWoodPlanks = new BlockWeedWoodPlanks();

	// DOUBLE PLANTS

	// SMALL PLANTS

	// UNDERGROWTH

	// DECORATIONS AND UTILITIES

	// ALTARS
	public static final Block druidAltar = new BlockDruidAltar();

	// STAIRS, SLABS, WALLS

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
