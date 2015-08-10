package thebetweenlands.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.ItemMaterialsCrushed;
import thebetweenlands.items.ItemMaterialsCrushed.EnumMaterialsCrushed;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.List;

public class RecipeHandler {


	public static IRecipe weedwoodPickAxeRecipe;
	public static IRecipe weedwoodShovelRecipe;
	public static IRecipe weedwoodAxeRecipe;
	public static IRecipe weedwoodSwordRecipe;

	public static IRecipe betweenstonePickAxeRecipe;
	public static IRecipe betweenstoneShovelRecipe;
	public static IRecipe betweenstoneAxeRecipe;
	public static IRecipe betweenstoneSwordRecipe;

	public static IRecipe octinePickAxeRecipe;
	public static IRecipe octineShovelRecipe;
	public static IRecipe octineAxeRecipe;
	public static IRecipe octineSwordRecipe;

	public static IRecipe valonitePickAxeRecipe;
	public static IRecipe valoniteShovelRecipe;
	public static IRecipe valoniteAxeRecipe;
	public static IRecipe valoniteSwordRecipe;

	public static IRecipe weedwoodBowRecipe;
	public static IRecipe anglerToothArrowRecipe;
	public static IRecipe octineArrowRecipe;

	public static IRecipe weedwoodDoorRecipe;

	public static void init() {
		registerOreDictionary();
		registerRecipes();
		registerSmelting();
		registerPurifierRecipes();
		registerPestleAndMortarRecipes();
		registerCompostItems();
	}

	private static void registerRecipes() {
		// Tools & Weapons
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodPickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodPickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstonePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		betweenstonePickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		betweenstoneShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		betweenstoneAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		betweenstoneSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octinePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		octinePickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		octineShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		octineAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		octineSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valonitePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		valonitePickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		valoniteShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		valoniteAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		valoniteSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodBow, 1), " #X", "# X", " #X", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED_ROPE));
		weedwoodBowRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.anglerToothArrow, 4), "X", "#", "Y", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.ANGLER_TOOTH), 'Y', ItemMaterialsBL.createStack(EnumMaterialsBL.DRAGONFLY_WING));
		anglerToothArrowRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineArrow, 4), "X", "#", "Y", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 'Y', ItemMaterialsBL.createStack(EnumMaterialsBL.DRAGONFLY_WING));
		octineArrowRecipe = getLatestAddedRecipe();
		
		//Swamp talisman made from BL materials for a return portal (or in case portal doesn't generate in BL)
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.swampTalisman, 1), ItemMaterialsBL.createStack(EnumMaterialsBL.MOSS, 1), ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE, 1), new ItemStack(BLItemRegistry.lifeCrystal, 1));
		
		// Armour
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinHelmet, 1), "###", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinChestplate, 1), "# #", "###", "###", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinLeggings, 1), "###", "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinBoots, 1), "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneHelmet, 1), "###", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneChestplate, 1), "# #", "###", "###", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneLeggings, 1), "###", "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneBoots, 1), "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineHelmet, 1), "###", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineChestplate, 1), "# #", "###", "###", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineLeggings, 1), "###", "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineBoots, 1), "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteHelmet, 1), "###", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteChestplate, 1), "# #", "###", "###", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteLeggings, 1), "###", "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteBoots, 1), "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.rubberBoots, 1), "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.RUBBER_BALL));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.rubberBootsImproved, 1), new ItemStack(BLItemRegistry.rubberBoots, 1), ItemMaterialsBL.createStack(EnumMaterialsBL.AQUA_MIDDLE_GEM));
		
		// Miscellaneous
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.sulfurTorch, 4), ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlanks, 4), new ItemStack(BLBlockRegistry.weedwoodLog));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlanks, 4), new ItemStack(BLBlockRegistry.weedwood));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.purpleRainPlanks, 4), new ItemStack(BLBlockRegistry.purpleRainLog));
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK, 4), "p", "p", 'p', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodCraftingTable), "xx", "xx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodChest), "xxx", "x x", "xxx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.furnaceBL), "xxx", "x x", "xxx", 'x', BLBlockRegistry.betweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.dualFurnaceBL), "xxx", "xfx", "xxx", 'x', BLBlockRegistry.betweenstone, 'f', BLBlockRegistry.furnaceBL);
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED_ROPE, 4), "p", "p" , "p", 'p', ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED));
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_BOWL, 4), "x x", " x ", 'x', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodBucket), " X ", "x x", " x ", 'x', new ItemStack(BLBlockRegistry.weedwoodPlanks),'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED_ROPE));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodLadder, 3), "X X", "xxx", "X X", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK),'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED_ROPE));		
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankButton), new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.betweenstoneButton), new ItemStack(BLBlockRegistry.smoothBetweenstone));		
		GameRegistry.addShapelessRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.PLANT_TONIC), new ItemStack(BLItemRegistry.weedwoodBucketWater), new ItemStack(BLItemRegistry.sapBall));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankPressurePlate), "xx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstonePressurePlate), "xx", 'x', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.octinePressurePlate), "xx", 'x', BLBlockRegistry.octineBlock);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudFlowerPot), "x x", " x " , 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.MUD_BRICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodLever), "X", "x", 'x', new ItemStack(BLBlockRegistry.weedwoodBark), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.pestle), "X", "x", "x", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK));
		
		//Machine Blocks
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purifier), "x x", "xxx", "ooo", 'x', BLBlockRegistry.weedwoodPlanks, 'o', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.animator), "xxx", "shs", "bbb", 'x', BLBlockRegistry.weedwoodPlanks, 's', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'h', new ItemStack(BLItemRegistry.wightsHeart), 'b' ,new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pestleAndMortar), "x x", "xxx", "s s", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1), 's', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK));
		
		//Deco Blocks
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrick, 4), "xx", "xx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.MUD_BRICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.smoothBetweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBricks, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.chiseledBetweenstone, 4), "x", "x", 'x', new ItemStack(BLBlockRegistry.betweenstoneBrickSlab));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.smoothCragrock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockBrick, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.carvedCrag, 4), "x", "x", 'x', new ItemStack(BLBlockRegistry.cragrockBrickSlab));		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.polishedLimestone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBricks, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.limestone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.chiseledLimestone, 4), "x", "x", 'x', new ItemStack(BLBlockRegistry.limestoneBrickSlab));		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.sulphurBlock), "xxx", "xxx", "xxx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR));
		GameRegistry.addShapelessRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR, 9), new ItemStack(BLBlockRegistry.sulphurBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.octineBlock), "xxx", "xxx", "xxx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addShapelessRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT, 9), new ItemStack(BLBlockRegistry.octineBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.valoniteBlock), "xxx", "xxx", "xxx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addShapelessRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD, 9), new ItemStack(BLBlockRegistry.valoniteBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.thatch, 4), "xx", "xx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.DRIED_SWAMP_REED));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.rubberTreePlanks, 4), new ItemStack(BLBlockRegistry.rubberTreeLog));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mireCoralBlock, 4), "xx", "xx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.MIRE_CORAL));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.deepWaterCoralBlock, 4), "xx", "xx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.DEEP_WATER_CORAL));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.hugeMushroomTop, 4), "xx", "xx", 'x',  BLItemRegistry.bulbCappedMushroomItem);
		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.blockWoodChipPath, 4), "###", '#', ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_WEEDWOOD_BARK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.blockWalkWay, 3), "SSS", "x x", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BLItemRegistry.doorWeedwood, 3), "##", "##", "##", '#', "plankWeedwood"));
		weedwoodDoorRecipe = getLatestAddedRecipe();

		// Stairs, slabs, walls, fences
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.betweenstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.mudBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.cragrockBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.limestoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothBetweenstoneStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.solidTarStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.solidTar);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.templeBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.templeBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.rubberTreePlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.purpleRainPlanks);
		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.betweenstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.mudBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.cragrockBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.limestoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothBetweenstoneWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.solidTarWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.solidTar);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.templeBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.templeBrick);
		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankFence, 3), "SxS", "SxS", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankFence, 3), "SxS", "SxS", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.rubberTreePlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankFence, 3), "SxS", "SxS", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.purpleRainPlanks));

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankFenceGate, 3), "xSx", "xSx", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankFenceGate, 3), "xSx", "xSx", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.rubberTreePlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankFenceGate, 3), "xSx", "xSx", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.purpleRainPlanks));
		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBrickSlab, 6), "###", '#', BLBlockRegistry.betweenstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrickSlab, 6), "###", '#', BLBlockRegistry.mudBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockBrickSlab, 6), "###", '#', BLBlockRegistry.cragrockBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBrickSlab, 6), "###", '#', BLBlockRegistry.limestoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothBetweenstoneSlab, 6), "###", '#', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.solidTarSlab, 6), "###", '#', BLBlockRegistry.solidTar);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.templeBrickSlab, 6), "###", '#', BLBlockRegistry.templeBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankSlab, 6), "###", '#', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankSlab, 6), "###", '#', BLBlockRegistry.rubberTreePlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankSlab, 6), "###", '#', BLBlockRegistry.purpleRainPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.thatchSlab, 6), "###", '#', BLBlockRegistry.thatch);
		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.thatchSlope, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.thatch);

		//Food
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.reedDonut, 3), " # ", "# #", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.DRIED_SWAMP_REED));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.jamDonut, 1), new ItemStack(BLItemRegistry.reedDonut), new ItemStack(BLItemRegistry.middleFruit));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.gertsDonut, 1), new ItemStack(BLItemRegistry.reedDonut), new ItemStack(BLItemRegistry.wightsHeart), new ItemStack(Items.slime_ball));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.crabstick, 3), "  #", " # ", "#  ", '#', new ItemStack(BLItemRegistry.siltCrabClaw));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.sapJello, 1), new ItemStack(BLItemRegistry.sludgeJello), new ItemStack(BLItemRegistry.sapBall));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.middleFruitJello, 1), new ItemStack(BLItemRegistry.sludgeJello), new ItemStack(BLItemRegistry.middleFruit));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.nettleSoup, 1), ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_BOWL), BLItemRegistry.blackHatMushroomItem, BLItemRegistry.flatheadMushroomItem, BLBlockRegistry.nettleFlowered);
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.nettleSoup, 1), ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_BOWL), BLItemRegistry.blackHatMushroomItem, BLItemRegistry.flatheadMushroomItem, BLBlockRegistry.nettle);
		
		// Special Items
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 0), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 1), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 2));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 2), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 3));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 3), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 4));
	}

	private static void registerSmelting() {
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.octineOre), ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.dampTorch), new ItemStack(Blocks.torch), 0F);
		GameRegistry.addSmelting(ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED), ItemMaterialsBL.createStack(EnumMaterialsBL.DRIED_SWAMP_REED), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.mud), ItemMaterialsBL.createStack(EnumMaterialsBL.MUD_BRICK), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.krakenTentacle), new ItemStack(BLItemRegistry.krakenCalamari, 5), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.swampKelp), new ItemStack(BLItemRegistry.friedSwampKelp), 5F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.anglerMeatRaw), new ItemStack(BLItemRegistry.anglerMeatCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.frogLegsRaw), new ItemStack(BLItemRegistry.frogLegsCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.snailFleshRaw), new ItemStack(BLItemRegistry.snailFleshCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.betweenstone), new ItemStack(BLBlockRegistry.smoothBetweenstone), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.genericStone, 1, 1), new ItemStack(BLBlockRegistry.smoothCragrock), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.limestone), new ItemStack(BLBlockRegistry.polishedLimestone), 0F);
		GameRegistry.addSmelting(ItemMaterialsBL.createStack(EnumMaterialsBL.SLUDGE_BALL), new ItemStack(BLItemRegistry.sludgeJello), 0F);
		GameRegistry.addSmelting(ItemMaterialsBL.createStack(BLItemRegistry.weedwoodBucketRubber, 1, 0), ItemMaterialsBL.createStack(EnumMaterialsBL.RUBBER_BALL), 0F);
	}

	private static void registerOreDictionary() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(BLBlockRegistry.sulfurOre));
		OreDictionary.registerOre("oreOctine", new ItemStack(BLBlockRegistry.octineOre));
		OreDictionary.registerOre("oreValonite", new ItemStack(BLBlockRegistry.valoniteOre));
		OreDictionary.registerOre("oreAquaMiddleGem", new ItemStack(BLBlockRegistry.aquaMiddleGemOre));
		OreDictionary.registerOre("oreGreenMiddleGem", new ItemStack(BLBlockRegistry.greenMiddleGemOre));
		OreDictionary.registerOre("oreCrimsonMiddleGem", new ItemStack(BLBlockRegistry.crimsonMiddleGemOre));
		OreDictionary.registerOre("oreLifeCrystal", new ItemStack(BLBlockRegistry.lifeCrystalOre));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.weedwood));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.weedwoodLog));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.sapTreeLog));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.rubberTreeLog));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.purpleRainLog));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.weedwoodLeaves));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.sapTreeLeaves));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.rubberTreeLeaves));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.purpleRainLeavesLight));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.purpleRainLeavesDark));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingWeedwood));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingSapTree));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingRubberTree));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingPurpleRain));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingSpiritTree));
		OreDictionary.registerOre("plankWood", new ItemStack(BLBlockRegistry.weedwoodPlanks, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("plankWood", new ItemStack(BLBlockRegistry.rubberTreePlanks, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("plankWood", new ItemStack(BLBlockRegistry.purpleRainPlanks, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("plankWeedwood", new ItemStack(BLBlockRegistry.weedwoodPlanks));
		OreDictionary.registerOre("stickWood", ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK));
        OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.bulbCappedMushroomItem));
        OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.blackHatMushroomItem));
        OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.flatheadMushroomItem));

		OreDictionary.registerOre("ingotOctine", ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

	}

	private static void registerPurifierRecipes() {
		PurifierRecipe.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.AQUA_MIDDLE_GEM), new ItemStack(BLBlockRegistry.aquaMiddleGemOre));
		PurifierRecipe.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.CRIMSON_MIDDLE_GEM), new ItemStack(BLBlockRegistry.crimsonMiddleGemOre));
		PurifierRecipe.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.GREEN_MIDDLE_GEM), new ItemStack(BLBlockRegistry.greenMiddleGemOre));
		PurifierRecipe.addRecipe(new ItemStack(BLBlockRegistry.farmedDirt, 1, 0), new ItemStack(BLBlockRegistry.swampDirt));
	}

	private static void registerPestleAndMortarRecipes() {
		PestleAndMortarRecipe.addRecipe(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_DRIED_SWAMP_REED), ItemMaterialsBL.createStack(EnumMaterialsBL.DRIED_SWAMP_REED));
		PestleAndMortarRecipe.addRecipe(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_WEEDWOOD_BARK), new ItemStack(BLBlockRegistry.weedwoodBark));	
	}

	private static void registerCompostItems(){
		for (ItemStack stack:OreDictionary.getOres("treeSapling"))
			CompostRecipe.addRecipe(5, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("treeLeaves"))
			CompostRecipe.addRecipe(5, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("foodMushroom"))
			CompostRecipe.addRecipe(3, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("cropWheat"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("cropPotato"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("cropCarrot"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("listAllseed"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("listAllveggie"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("listAllgrain"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("listAllberry"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		for (ItemStack stack:OreDictionary.getOres("listAllfruit"))
			CompostRecipe.addRecipe(2, 12000, stack.getItem(), stack.getItemDamage());
		CompostRecipe.addRecipe(100, 1, BLItemRegistry.testItem);
		CompostRecipe.addRecipe(10, 12000, Item.getItemFromBlock(BLBlockRegistry.wallPlants));
		CompostRecipe.addRecipe(10, 12000, Item.getItemFromBlock(BLBlockRegistry.wallPlants), 1);
		CompostRecipe.addRecipe(25, 12000, Item.getItemFromBlock(BLBlockRegistry.rottenWeedwoodBark));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.boneset));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.marshMallow));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.nettle));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.nettleFlowered));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.buttonBush));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.milkweed));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.copperIris));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.blueIris));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.waterFlower));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.marshHibiscus));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(BLBlockRegistry.pickerelWeed));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.yellow_flower));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.red_flower));
		CompostRecipe.addRecipe(6, 12000, Item.getItemFromBlock(Blocks.cactus));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.brown_mushroom));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.red_mushroom));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.sapling));
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.sapling), 1);
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.sapling), 2);
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.sapling), 3);
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.sapling), 4);
		CompostRecipe.addRecipe(3, 12000, Item.getItemFromBlock(Blocks.sapling), 5);

	}

	public static IRecipe getLatestAddedRecipe() {
		List<IRecipe> list = CraftingManager.getInstance().getRecipeList();
		return list.get(list.size() - 1);
	}
}
