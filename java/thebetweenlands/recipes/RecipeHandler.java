package thebetweenlands.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.blocks.BLBlockRegistry;
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
		// Tools

		// Armour

		// Miscellaneous
		GameRegistry.addShapelessRecipe(new ItemStack(BLBlockRegistry.sulfurTorch), ItemMaterialsBL.createStack(EnumMaterialsBL.WEEDWOOD_SICK), ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR));

		// Special Items
	}

	private static void registerSmelting() {
		GameRegistry.addSmelting(new ItemStack(BLBlockRegistry.octineOre), ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 0.7F);
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

		// OreDictionary.registerOre("foodMushroom", new ItemStack(BLBlockRegistry.bulbCappedMushroom));
		
		OreDictionary.registerOre("ingotOctine", ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT));

	}
}
