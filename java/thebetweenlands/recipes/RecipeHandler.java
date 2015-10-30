package thebetweenlands.recipes;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.herblore.elixirs.ElixirRecipes;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.items.ItemGeneric.EnumItemGeneric;
import thebetweenlands.items.ItemGenericCrushed;
import thebetweenlands.items.ItemGenericCrushed.EnumItemGenericCrushed;
import thebetweenlands.items.ItemGenericPlantDrop;
import thebetweenlands.items.ItemGenericPlantDrop.EnumItemPlantDrop;
import thebetweenlands.utils.confighandler.ConfigHandler;

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
		ConfigHandler.userRecipes();
		registerPestleAndMortarRecipes();
		registerCompostItems();
		AspectRecipes.init();
		ElixirRecipes.init();
	}

	private static void registerRecipes() {
		// Tools & Weapons
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodPickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodPickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		weedwoodSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstonePickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		betweenstonePickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		betweenstoneShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		betweenstoneAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		betweenstoneSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octinePickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		octinePickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		octineShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		octineAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		octineSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valonitePickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		valonitePickAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		valoniteShovelRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		valoniteAxeRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		valoniteSwordRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodBow, 1), " #X", "# X", " #X", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));
		weedwoodBowRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.anglerToothArrow, 4), "X", "#", "Y", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.ANGLER_TOOTH), 'Y', ItemGeneric.createStack(EnumItemGeneric.DRAGONFLY_WING));
		anglerToothArrowRecipe = getLatestAddedRecipe();
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineArrow, 4), "X", "#", "Y", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 'Y', ItemGeneric.createStack(EnumItemGeneric.DRAGONFLY_WING));
		octineArrowRecipe = getLatestAddedRecipe();

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.syrmoriteShears, 1), " #", "# ", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));

		//Swamp talisman made from BL materials for a return portal (or in case portal doesn't generate in BL)
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.swampTalisman, 1), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MOSS), ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE, 1), new ItemStack(BLItemRegistry.lifeCrystal, 1));

		// Armour
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinHelmet, 1), "###", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinChestplate, 1), "# #", "###", "###", '#', ItemGeneric.createStack(EnumItemGeneric.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinLeggings, 1), "###", "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinBoots, 1), "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.LURKER_SKIN));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneHelmet, 1), "###", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneChestplate, 1), "# #", "###", "###", '#', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneLeggings, 1), "###", "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.boneBoots, 1), "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.syrmoriteHelmet, 1), "###", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.syrmoriteChestplate, 1), "# #", "###", "###", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.syrmoriteLeggings, 1), "###", "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.syrmoriteBoots, 1), "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteHelmet, 1), "###", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteChestplate, 1), "# #", "###", "###", '#', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteLeggings, 1), "###", "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteBoots, 1), "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.rubberBoots, 1), "# #", "# #", '#', ItemGeneric.createStack(EnumItemGeneric.RUBBER_BALL));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.rubberBootsImproved, 1), new ItemStack(BLItemRegistry.rubberBoots, 1), ItemGeneric.createStack(EnumItemGeneric.AQUA_MIDDLE_GEM));

		// Miscellaneous
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.sulfurTorch, 4), ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), ItemGeneric.createStack(EnumItemGeneric.SULFUR));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlanks, 4), new ItemStack(BLBlockRegistry.weedwoodLog));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlanks, 4), new ItemStack(BLBlockRegistry.weedwood));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.purpleRainPlanks, 4), new ItemStack(BLBlockRegistry.purpleRainLog));
		GameRegistry.addRecipe(ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK, 4), "p", "p", 'p', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodCraftingTable), "xx", "xx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodChest), "xxx", "x x", "xxx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.furnaceBL), "xxx", "x x", "xxx", 'x', BLBlockRegistry.betweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.dualFurnaceBL), "xxx", "xfx", "xxx", 'x', BLBlockRegistry.betweenstone, 'f', BLBlockRegistry.furnaceBL);
		GameRegistry.addRecipe(ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE, 4), "p", "p" , "p", 'p', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED));
		GameRegistry.addRecipe(ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_BOWL, 4), "x x", " x ", 'x', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodBucket), " X ", "x x", " x ", 'x', new ItemStack(BLBlockRegistry.weedwoodPlanks),'X', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodLadder, 3), "X X", "xxx", "X X", 'x', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK),'X', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));		
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankButton), new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.betweenstoneButton), new ItemStack(BLBlockRegistry.smoothBetweenstone));		
		GameRegistry.addShapelessRecipe(ItemGeneric.createStack(EnumItemGeneric.PLANT_TONIC), new ItemStack(BLItemRegistry.weedwoodBucketWater), new ItemStack(BLItemRegistry.sapBall));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankPressurePlate), "xx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstonePressurePlate), "xx", 'x', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.syrmoritePressurePlate), "xx", 'x', BLBlockRegistry.syrmoriteBlock);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudFlowerPot), "x x", " x " , 'x', ItemGeneric.createStack(EnumItemGeneric.MUD_BRICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodLever), "X", "x", 'x', new ItemStack(BLBlockRegistry.weedwoodBark), 'X', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.pestle), "X", "x", "x", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1), 'X', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.itemShelf, 3), "xxx", "   ", "xxx", 'x', BLBlockRegistry.weedwoodPlankSlab);
		GameRegistry.addRecipe(BLItemRegistry.dentrothystVial.createStack(0, 3), "x x", " x ", 'x', new ItemStack(Item.getItemFromBlock(BLBlockRegistry.dentrothyst), 1, 0));
		GameRegistry.addRecipe(BLItemRegistry.dentrothystVial.createStack(2, 3), "x x", " x ", 'x', new ItemStack(Item.getItemFromBlock(BLBlockRegistry.dentrothyst), 1, 1));


		//Machine Blocks
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purifier), "x x", "xxx", "ooo", 'x', BLBlockRegistry.weedwoodPlanks, 'o', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.compostBin), "bxb", "x x", "x x", 'x', BLBlockRegistry.weedwoodPlanks, 'b', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.animator), "xxx", "shs", "bbb", 'x', BLBlockRegistry.weedwoodPlanks, 's', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'h', new ItemStack(BLItemRegistry.wightsHeart), 'b' ,new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pestleAndMortar), "x x", "xxx", "s s", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1), 's', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));

		//Deco Blocks
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrick, 4), "xx", "xx", 'x', ItemGeneric.createStack(EnumItemGeneric.MUD_BRICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.smoothBetweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pitstoneTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.smoothPitstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBricks, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pitstoneBricks, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.pitstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.chiseledBetweenstone, 4), "x", "x", 'x', new ItemStack(BLBlockRegistry.betweenstoneBrickSlab));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.chiseledPitstone, 4), "x", "x", 'x', new ItemStack(BLBlockRegistry.pitstoneBrickSlab));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.smoothCragrock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockBrick, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.carvedCrag, 4), "x", "x", 'x', new ItemStack(BLBlockRegistry.cragrockBrickSlab));		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.polishedLimestone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBricks, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.limestone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.chiseledLimestone, 4), "x", "x", 'x', new ItemStack(BLBlockRegistry.limestoneBrickSlab));		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.sulphurBlock), "xxx", "xxx", "xxx", 'x', ItemGeneric.createStack(EnumItemGeneric.SULFUR));
		GameRegistry.addShapelessRecipe(ItemGeneric.createStack(EnumItemGeneric.SULFUR, 9), new ItemStack(BLBlockRegistry.sulphurBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.octineBlock), "xxx", "xxx", "xxx", 'x', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.syrmoriteBlock), "xxx", "xxx", "xxx", 'x', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addShapelessRecipe(ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT, 9), new ItemStack(BLBlockRegistry.syrmoriteBlock));
		GameRegistry.addShapelessRecipe(ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT, 9), new ItemStack(BLBlockRegistry.octineBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.valoniteBlock), "xxx", "xxx", "xxx", 'x', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		GameRegistry.addShapelessRecipe(ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD, 9), new ItemStack(BLBlockRegistry.valoniteBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.thatch, 4), "xx", "xx", 'x', ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.rubberTreePlanks, 4), new ItemStack(BLBlockRegistry.rubberTreeLog));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mireCoralBlock, 4), "xx", "xx", 'x', ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MIRE_CORAL));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.deepWaterCoralBlock, 4), "xx", "xx", 'x', ItemGenericPlantDrop.createStack(EnumItemPlantDrop.DEEP_WATER_CORAL));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.hugeMushroomTop, 4), "xx", "xx", 'x',  BLItemRegistry.bulbCappedMushroomItem);

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.blockWoodChipPath, 4), "###", '#', ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WEEDWOOD_BARK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.blockWalkWay, 3), "SSS", "x x", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BLItemRegistry.doorWeedwood, 3), "##", "##", "##", '#', "plankWeedwood"));
		weedwoodDoorRecipe = getLatestAddedRecipe();

		// Stairs, slabs, walls, fences
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.betweenstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pitstoneBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.pitstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.mudBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.cragrockBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.limestoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.limestone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothBetweenstoneStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothCragrockStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.smoothCragrock);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothPitstoneStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.smoothPitstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.solidTarStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.solidTar);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.templeBrickStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.templeBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.rubberTreePlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankStairs, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.purpleRainPlanks);

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.betweenstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pitstoneBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.pitstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.mudBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.cragrockBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.limestoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.limestone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothBetweenstoneWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.solidTarWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.solidTar);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.templeBrickWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.templeBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothPitstoneWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.smoothPitstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothCragrockWall, 6), "xxx", "xxx", 'x', BLBlockRegistry.smoothCragrock);

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankFence, 3), "SxS", "SxS", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankFence, 3), "SxS", "SxS", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.rubberTreePlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankFence, 3), "SxS", "SxS", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.purpleRainPlanks));

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankFenceGate, 3), "xSx", "xSx", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankFenceGate, 3), "xSx", "xSx", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.rubberTreePlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankFenceGate, 3), "xSx", "xSx", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.purpleRainPlanks));

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBrickSlab, 6), "###", '#', BLBlockRegistry.betweenstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrickSlab, 6), "###", '#', BLBlockRegistry.mudBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockBrickSlab, 6), "###", '#', BLBlockRegistry.cragrockBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBrickSlab, 6), "###", '#', BLBlockRegistry.limestoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneSlab, 6), "###", '#', BLBlockRegistry.limestone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothBetweenstoneSlab, 6), "###", '#', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothCragrockSlab, 6), "###", '#', BLBlockRegistry.smoothCragrock);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothPitstoneSlab, 6), "###", '#', BLBlockRegistry.smoothPitstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pitstoneBrickSlab, 6), "###", '#', BLBlockRegistry.pitstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.solidTarSlab, 6), "###", '#', BLBlockRegistry.solidTar);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.templeBrickSlab, 6), "###", '#', BLBlockRegistry.templeBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankSlab, 6), "###", '#', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankSlab, 6), "###", '#', BLBlockRegistry.rubberTreePlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankSlab, 6), "###", '#', BLBlockRegistry.purpleRainPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.thatchSlab, 6), "###", '#', BLBlockRegistry.thatch);

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.thatchSlope, 4), "x  ", "xx ", "xxx", 'x', BLBlockRegistry.thatch);

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.siltGlasPane, 6), "xxx", "xxx", 'x', BLBlockRegistry.siltGlas);

		//Food
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.reedDonut, 3), " # ", "# #", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.jamDonut, 1), new ItemStack(BLItemRegistry.reedDonut), new ItemStack(BLItemRegistry.middleFruit));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.gertsDonut, 1), new ItemStack(BLItemRegistry.reedDonut), new ItemStack(BLItemRegistry.wightsHeart), new ItemStack(Items.slime_ball));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.crabstick, 3), "  #", " # ", "#  ", '#', new ItemStack(BLItemRegistry.siltCrabClaw));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.sapJello, 1), new ItemStack(BLItemRegistry.sludgeJello), new ItemStack(BLItemRegistry.sapBall));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.middleFruitJello, 1), new ItemStack(BLItemRegistry.sludgeJello), new ItemStack(BLItemRegistry.middleFruit));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.nettleSoup, 1), ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_BOWL), BLItemRegistry.blackHatMushroomItem, BLItemRegistry.flatheadMushroomItem, BLBlockRegistry.nettleFlowered);
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.nettleSoup, 1), ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_BOWL), BLItemRegistry.blackHatMushroomItem, BLItemRegistry.flatheadMushroomItem, BLBlockRegistry.nettle);

		// Special Items
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 0), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 1), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 2));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 2), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 3));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lifeCrystal, 1, 3), "xxx", "xcx", "xxx", 'x', new ItemStack(BLItemRegistry.wightsHeart), 'c', new ItemStack(BLItemRegistry.lifeCrystal, 1, 4));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.tarminion, 3), "ttt", "tht", "ttt", 't', ItemGeneric.createStack(EnumItemGeneric.TAR_DRIP), 'h', ItemGeneric.createStack(EnumItemGeneric.TAR_BEAST_HEART_ANIMATED));
	}

	private static void registerSmelting() {
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.syrmoriteOre), ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.octineOre), ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.dampTorch), new ItemStack(Blocks.torch), 0F);
		GameRegistry.addSmelting(ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED), ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.mud), ItemGeneric.createStack(EnumItemGeneric.MUD_BRICK), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.krakenTentacle), new ItemStack(BLItemRegistry.krakenCalamari, 5), 0F);
		GameRegistry.addSmelting(ItemGeneric.createStack(EnumItemGeneric.SWAMP_KELP), new ItemStack(BLItemRegistry.friedSwampKelp), 5F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.anglerMeatRaw), new ItemStack(BLItemRegistry.anglerMeatCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.frogLegsRaw), new ItemStack(BLItemRegistry.frogLegsCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.snailFleshRaw), new ItemStack(BLItemRegistry.snailFleshCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.betweenstone), new ItemStack(BLBlockRegistry.smoothBetweenstone), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.pitstone), new ItemStack(BLBlockRegistry.smoothPitstone), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.genericStone, 1, 1), new ItemStack(BLBlockRegistry.smoothCragrock), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.limestone), new ItemStack(BLBlockRegistry.polishedLimestone), 0F);
		GameRegistry.addSmelting(ItemGeneric.createStack(EnumItemGeneric.SLUDGE_BALL), new ItemStack(BLItemRegistry.sludgeJello), 0F);
		GameRegistry.addSmelting(ItemGeneric.createStack(BLItemRegistry.weedwoodBucketRubber, 1, 0), ItemGeneric.createStack(EnumItemGeneric.RUBBER_BALL), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.silt), new ItemStack(BLBlockRegistry.siltGlas), 0F);
	}

	private static void registerOreDictionary() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(BLBlockRegistry.sulfurOre));
		OreDictionary.registerOre("oreSyrmorite", new ItemStack(BLBlockRegistry.syrmoriteOre));
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
		OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.bulbCappedMushroomItem));
		OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.blackHatMushroomItem));
		OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.flatheadMushroomItem));

		OreDictionary.registerOre("ingotSyrmorite", ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		OreDictionary.registerOre("ingotOctine", ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));

	}

	private static void registerPurifierRecipes() {
		PurifierRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.AQUA_MIDDLE_GEM), new ItemStack(BLBlockRegistry.aquaMiddleGemOre));
		PurifierRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.CRIMSON_MIDDLE_GEM), new ItemStack(BLBlockRegistry.crimsonMiddleGemOre));
		PurifierRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.GREEN_MIDDLE_GEM), new ItemStack(BLBlockRegistry.greenMiddleGemOre));
		PurifierRecipe.addRecipe(new ItemStack(BLBlockRegistry.farmedDirt, 1, 0), new ItemStack(BLBlockRegistry.swampDirt));
		PurifierRecipe.addRecipe(BLItemRegistry.dentrothystVial.createStack(0), BLItemRegistry.dentrothystVial.createStack(1));
	}

	private static void registerPestleAndMortarRecipes() {
		PestleAndMortarRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), new ItemStack(BLBlockRegistry.limestone));
		PestleAndMortarRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), new ItemStack(BLBlockRegistry.chiseledLimestone));
		PestleAndMortarRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), new ItemStack(BLBlockRegistry.polishedLimestone));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ALGAE), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.ALGAE));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ANGLER_TOOTH), ItemGeneric.createStack(EnumItemGeneric.ANGLER_TOOTH));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_AQUA_MIDDLE_GEM), ItemGeneric.createStack(EnumItemGeneric.AQUA_MIDDLE_GEM));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ARROW_ARUM), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.ARROW_ARUM_LEAF));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLACKHAT_MUSHROOM), new ItemStack(BLItemRegistry.blackHatMushroomItem));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLOOD_SNAIL_SHELL), ItemGeneric.createStack(EnumItemGeneric.BLOOD_SNAIL_SHELL));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_EYED_GRASS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BLUE_EYED_GRASS_FLOWERS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_IRIS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BLUE_IRIS_PETAL));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOG_BEAN), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BOG_BEAN_FLOWER));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BONESET), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BONESET_FLOWERS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOTTLE_BRUSH_GRASS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BOTTLE_BRUSH_GRASS_BLADES));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BROOM_SEDGE), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BROOM_SEDGE_LEAVES));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BULB_CAPPED_MUSHROOM), new ItemStack(BLItemRegistry.bulbCappedMushroomItem));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BUTTON_BUSH), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BUTTON_BUSH_FLOWERS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CARDINAL_FLOWER), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CARDINAL_FLOWER_PETALS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CATTAIL), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CATTAIL_HEAD));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_GRASS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CAVE_GRASS_BLADES));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_MOSS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CAVE_MOSS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_COPPER_IRIS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.COPPER_IRIS_PETALS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CRIMSON_MIDDLE_GEM), ItemGeneric.createStack(EnumItemGeneric.CRIMSON_MIDDLE_GEM));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DEEP_WATER_CORAL), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.DEEP_WATER_CORAL));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED), ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_FLATHEAD_MUSHROOM), new ItemStack(BLItemRegistry.flatheadMushroomItem));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GOLDEN_CLUB), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.GOLDEN_CLUB_FLOWERS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GREEN_MIDDLE_GEM), ItemGeneric.createStack(EnumItemGeneric.GREEN_MIDDLE_GEM));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_HANGER), new ItemStack(Item.getItemFromBlock(BLBlockRegistry.hanger)));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_LICHEN), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.LICHEN));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_HIBISCUS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MARSH_HIBISCUS_FLOWER));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MALLOW), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MARSH_MALLOW_FLOWER));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MARIGOLD), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MARSH_MARIGOLD_FLOWER));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MILKWEED), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MILK_WEED));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_CORAL), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MIRE_CORAL));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_SNAIL_SHELL), ItemGeneric.createStack(EnumItemGeneric.MIRE_SNAIL_SHELL));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MOSS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MOSS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_NETTLE), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.NETTLE_LEAF));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PHRAGMITES), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.PHRAGMITE_STEMS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PICKEREL_WEED), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.PICKEREL_WEED_FLOWER));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SHOOTS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SHOOT_LEAVES));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SLUDGECREEP), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SLUDGECREEP_LEAVES));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SOFT_RUSH), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SOFT_RUSH_LEAVES));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SUNDEW), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SUNDEW_HEAD));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_KELP), ItemGeneric.createStack(EnumItemGeneric.SWAMP_KELP));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_GRASS_TALL), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_TANGLED_ROOTS), ItemGeneric.createStack(EnumItemGeneric.TANGLED_ROOT));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WEEDWOOD_BARK), new ItemStack(BLBlockRegistry.weedwoodBark));
	}

	private static void registerCompostItems(){
		CompostRecipe.addRecipe(5, 12000, OreDictionary.getOres("treeSapling"));
		CompostRecipe.addRecipe(5, 12000, OreDictionary.getOres("treeLeaves"));
		CompostRecipe.addRecipe(3, 12000, OreDictionary.getOres("foodMushroom"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("cropWheat"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("cropPotato"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("cropCarrot"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("listAllseed"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("listAllveggie"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("listAllgrain"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("listAllberry"));
		CompostRecipe.addRecipe(2, 12000, OreDictionary.getOres("listAllfruit"));
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
