package thebetweenlands.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
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
	}

	private static void registerRecipes() {
		// Tools & Weapons
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodPickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstonePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octinePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valonitePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));

		// Armour
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinHelmet, 1), "###", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinChestplate, 1), "# #", "###", "###", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinLeggings, 1), "###", "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.lurkerSkinBoots, 1), "# #", "# #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN));

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
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 4, 10), new ItemStack(BLBlockRegistry.sundew));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 4, 9), new ItemStack(BLBlockRegistry.waterFlower));
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK, 4), "p", "p", 'p', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodCraftingTable), "xx", "xx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodChest), "xxx", "x x", "xxx", 'x', BLBlockRegistry.weedwoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.furnaceBL), "xxx", "x x", "xxx", 'x', BLBlockRegistry.betweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.dualFurnaceBL), "xxx", "xfx", "xxx", 'x', BLBlockRegistry.betweenstone, 'f', BLBlockRegistry.furnaceBL);
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED_ROPE, 4), "p", "p" , "p", 'p', ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED));

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
		
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.doorWeedwood, 3), "##", "##", "##", '#', new ItemStack(BLBlockRegistry.weedwoodPlanks, 1, 2));
		
		//Food
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.reedDonut, 3), " # ", "# #", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.DRIED_SWAMP_REED));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.jamDonut, 1), new ItemStack(BLItemRegistry.reedDonut), new ItemStack(BLItemRegistry.middleFruit));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.gertsDonut, 1), new ItemStack(BLItemRegistry.reedDonut), new ItemStack(BLItemRegistry.wightsHeart), new ItemStack(Items.slime_ball));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.crabstick, 1), "  #", " # ", "#  ", '#', new ItemStack(BLItemRegistry.siltCrabClaw));
		
		// Special Items
	}

	private static void registerSmelting() {
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.octineOre), ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 0.7F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.dampTorch), new ItemStack(Blocks.torch), 0F);
		GameRegistry.addSmelting(ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED), ItemMaterialsBL.createStack(EnumMaterialsBL.DRIED_SWAMP_REED), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.mud), ItemMaterialsBL.createStack(EnumMaterialsBL.MUD_BRICK), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.krakenTentacle), new ItemStack(BLItemRegistry.krakenCalamari, 5), 5F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.anglerMeatRaw), new ItemStack(BLItemRegistry.anglerMeatCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.frogLegsRaw), new ItemStack(BLItemRegistry.frogLegsCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLItemRegistry.snailFleshRaw), new ItemStack(BLItemRegistry.snailFleshCooked), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.betweenstone), new ItemStack(BLBlockRegistry.smoothBetweenstone), 0F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.genericStone,1,1), new ItemStack(BLBlockRegistry.cragrockBrick), 0F);

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
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.weedwoodLeaves));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.sapTreeLeaves));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingWeedwood));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingSapTree));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingSpiritTree));
		OreDictionary.registerOre("plankWood", new ItemStack(BLBlockRegistry.weedwoodPlanks));
		OreDictionary.registerOre("stickWood", ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_STICK));

		// OreDictionary.registerOre("foodMushroom", new ItemStack(BLBlockRegistry.bulbCappedMushroom));

		OreDictionary.registerOre("ingotOctine", ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

	}
}
