package thebetweenlands.recipes;

import net.minecraft.init.Blocks;
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
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedWoodPickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.weedWoodBark));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedWoodShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.weedWoodBark));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedWoodAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.weedWoodBark));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedWoodSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.weedWoodBark));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstonePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', new ItemStack(BLBlockRegistry.betweenstone));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octinePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valonitePickaxe, 1), "XXX", " # ", " # ", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteShovel, 1), "X", "#", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteAxe, 1), "XX", "X#", " #", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteSword, 1), "X", "X", "#", '#', ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), 'X', ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD));

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
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.sulfurTorch, 4), ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedWoodPlanks, 4), new ItemStack(BLBlockRegistry.weedWoodLog));
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.weedWoodPlanks, 4), new ItemStack(BLBlockRegistry.weedWood));
		GameRegistry.addRecipe(ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK, 4), "p", "p", 'p', new ItemStack(BLBlockRegistry.weedWoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedWoodCraftingTable), "xx", "xx", 'x', BLBlockRegistry.weedWoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedWoodChest), "xxx", "x x", "xxx", 'x', BLBlockRegistry.weedWoodPlanks);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.furnaceBL), "xxx", "x x", "xxx", 'x', BLBlockRegistry.betweenstone);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.dualFurnaceBL), "xxx", "xfx", "xxx", 'x', BLBlockRegistry.betweenstone, 'f', BLBlockRegistry.furnaceBL);

		// Special Items
	}

	private static void registerSmelting() {
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.octineOre), ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 0.7F);
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.dampTorch), new ItemStack(Blocks.torch), 0F);
	}

	private static void registerOreDictionary() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(BLBlockRegistry.sulfurOre));
		OreDictionary.registerOre("oreOctine", new ItemStack(BLBlockRegistry.octineOre));
		OreDictionary.registerOre("oreValonite", new ItemStack(BLBlockRegistry.valoniteOre));
		OreDictionary.registerOre("oreAquaMiddleGem", new ItemStack(BLBlockRegistry.aquaMiddleGemOre));
		OreDictionary.registerOre("oreGreenMiddleGem", new ItemStack(BLBlockRegistry.greenMiddleGemOre));
		OreDictionary.registerOre("oreCrimsonMiddleGem", new ItemStack(BLBlockRegistry.crimsonMiddleGemOre));
		OreDictionary.registerOre("oreLifeCrystal", new ItemStack(BLBlockRegistry.lifeCrystalOre));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.weedWood));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.weedWoodLog));
		OreDictionary.registerOre("logWood", new ItemStack(BLBlockRegistry.sapTreeLog));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.weedWoodLeaves));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BLBlockRegistry.sapTreeLeaves));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingWeedWood));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingSapTree));
		OreDictionary.registerOre("treeSapling", new ItemStack(BLBlockRegistry.saplingSpiritTree));
		OreDictionary.registerOre("plankWood", new ItemStack(BLBlockRegistry.weedWoodPlanks));
		OreDictionary.registerOre("stickWood", ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK));

		// OreDictionary.registerOre("foodMushroom", new ItemStack(BLBlockRegistry.bulbCappedMushroom));

		OreDictionary.registerOre("ingotOctine", ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

	}
}
