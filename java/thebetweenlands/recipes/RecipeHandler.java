package thebetweenlands.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeHandler {

	public static void init() {
		registerOreDictionary();
		registerRecipes();
		registerSmelting();
		registerPurifierRecipes();
	}

	private static void registerRecipes() {
		// Tools & Weapons
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodPickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstonePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octinePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valonitePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodBow, 1), " #X", "# X", " #X", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED_ROPE));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.anglerToothArrow, 4), "X", "#", "Y", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.ANGLER_TOOTH), 'Y', ItemMaterialsBL.createStack(EnumMaterialsBL.DRAGONFLY_WING));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineArrow, 4), "X", "#", "Y", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 'Y', ItemMaterialsBL.createStack(EnumMaterialsBL.DRAGONFLY_WING));
		
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

		// Miscellaneous
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.sulfurTorch, 4), ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlanks, 4), new ItemStack(BLBlockRegistry.weedwoodLog));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedwoodPlanks, 4), new ItemStack(BLBlockRegistry.weedwood));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.purpleRainPlanks, 4), new ItemStack(BLBlockRegistry.purpleRainLog));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 4, 10), new ItemStack(BLBlockRegistry.sundew));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 4, 9), new ItemStack(BLBlockRegistry.waterFlower));
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK, 4), "p", "p", 'p', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodCraftingTable), "xx", "xx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodChest), "xxx", "x x", "xxx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.furnaceBL), "xxx", "x x", "xxx", 'x', BLBlockRegistry.betweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.dualFurnaceBL), "xxx", "xfx", "xxx", 'x', BLBlockRegistry.betweenstone, 'f', BLBlockRegistry.furnaceBL);
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED_ROPE, 4), "p", "p" , "p", 'p', ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED));
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_BOWL, 4), "x x", " x ", 'x', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		
		//Machine Blocks
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purifier), "x x", "xxx", "ooo", 'x', BLBlockRegistry.weedwoodPlanks, 'o', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.animator), "xxx", "shs", "bbb", 'x', BLBlockRegistry.weedwoodPlanks, 's', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'h' ,new ItemStack(BLItemRegistry.wightsHeart), 'b' ,new ItemStack(BLBlockRegistry.betweenstone));
		
		//Deco Blocks
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrick, 4), "xx", "xx", 'x', ItemMaterialsBL.createStack(EnumMaterialsBL.MUD_BRICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.smoothBetweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragTiles, 4), "xx", "xx", 'x', new ItemStack(BLBlockRegistry.genericStone,1,1));
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BLItemRegistry.doorWeedwood, 3), "##", "##", "##", '#', "plankWeedwood"));
		
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
		
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankFence, 3), "xSx", "xSx", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankFence, 3), "xSx", "xSx", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.rubberTreePlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankFence, 3), "xSx", "xSx", 'x',  ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.purpleRainPlanks));
		
	/*	//Not sure how to do these
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.betweenstoneBrickSlab, 4), "###", 'x', BLBlockRegistry.betweenstoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.mudBrickSlab, 4), "###", 'x', BLBlockRegistry.mudBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.cragrockBrickSlab, 4), "###", 'x', BLBlockRegistry.cragrockBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.limestoneBrickSlab, 4), "###", 'x', BLBlockRegistry.limestoneBricks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.smoothBetweenstoneSlab, 4), "###", 'x', BLBlockRegistry.smoothBetweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.solidTarSlab, 4), "###", 'x', BLBlockRegistry.solidTar);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.templeBrickSlab, 4), "###", 'x', BLBlockRegistry.templeBrick);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankSlab, 4), "###", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankSlab, 4), "###", 'x', BLBlockRegistry.rubberTreePlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankSlab, 4), "###", 'x', BLBlockRegistry.purpleRainPlanks);
	*/	
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
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.genericStone,1,1), new ItemStack(BLBlockRegistry.cragrockBrick), 0F);
		GameRegistry.addSmelting(ItemMaterialsBL.createStack(EnumMaterialsBL.SLUDGE_BALL), new ItemStack(BLItemRegistry.sludgeJello), 0F);

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
	}
}
