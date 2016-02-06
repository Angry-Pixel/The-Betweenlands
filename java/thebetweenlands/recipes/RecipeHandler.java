package thebetweenlands.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockGenericStone;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.elixirs.ElixirRecipes;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.herblore.ItemGenericCrushed;
import thebetweenlands.items.herblore.ItemGenericCrushed.EnumItemGenericCrushed;
import thebetweenlands.items.herblore.ItemGenericPlantDrop;
import thebetweenlands.items.herblore.ItemGenericPlantDrop.EnumItemPlantDrop;
import thebetweenlands.items.lanterns.LightVariant;
import thebetweenlands.items.lanterns.crafting.RecipeDyeColorNBT;
import thebetweenlands.items.lanterns.crafting.RecipeFairyLights;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.items.misc.ItemSwampTalisman;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.tileentities.TileEntityAnimator;
import thebetweenlands.utils.WeightedRandomItem;
import thebetweenlands.utils.confighandler.ConfigHandler;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

public class RecipeHandler {

	public static void init() {
		registerOreDictionary();
		registerRecipes();
		registerSmelting();
		registerPurifierRecipes();
		registerPestleAndMortarRecipes();
		registerCompostItems();
		registerDruidAltarRecipes();
		registerAnimatorRecipes();
		ConfigHandler.userRecipes();
		AspectRegistry.init();
		ElixirRecipes.init();
	}

	private static void registerRecipes() {
		// Tools & Weapons
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodPickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', new ItemStack(BLBlockRegistry.weedwoodPlanks));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstonePickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.betweenstoneSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octinePickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valonitePickaxe, 1), "XXX", " # ", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteShovel, 1), "X", "#", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteAxe, 1), "XX", "X#", " #", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.valoniteSword, 1), "X", "X", "#", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodBow, 1), " #X", "# X", " #X", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.anglerToothArrow, 4), "X", "#", "Y", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.ANGLER_TOOTH), 'Y', ItemGeneric.createStack(EnumItemGeneric.DRAGONFLY_WING));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.octineArrow, 4), "X", "#", "Y", '#', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'X', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 'Y', ItemGeneric.createStack(EnumItemGeneric.DRAGONFLY_WING));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.poisonedAnglerToothArrow, 1), ItemGeneric.createStack(EnumItemGeneric.POISON_GLAND), new ItemStack(BLItemRegistry.anglerToothArrow));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.syrmoriteShears, 1), " #", "# ", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.sickle, 1), " vv", "v s", "  r", 'v', ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD), 's', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'r', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.rope, 1), "#", "#", "#", '#', new ItemStack(BLBlockRegistry.hanger));

		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.net, 1), "SRR", "SRR", "S  ", 'S', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'R', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));

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
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.rubberBootsImproved, 1), new ItemStack(BLItemRegistry.rubberBoots, 1), new ItemStack(BLItemRegistry.aquaMiddleGem, 1));

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
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.mudFlowerPot), "x x", " x " , 'x', ItemGeneric.createStack(EnumItemGeneric.MUD_BRICK));;
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodLever), "X", "x", 'x', new ItemStack(BLBlockRegistry.weedwoodBark), 'X', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.pestle), "X", "x", "x", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1), 'X', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.itemShelf, 3), "xxx", "   ", "xxx", 'x', BLBlockRegistry.weedwoodPlankSlab);
		GameRegistry.addRecipe(BLItemRegistry.dentrothystVial.createStack(0, 3), " r ", "x x", " x ", 'x', new ItemStack(BLBlockRegistry.dentrothyst, 1, 0), 'r', ItemGeneric.createStack(EnumItemGeneric.RUBBER_BALL));
		GameRegistry.addRecipe(BLItemRegistry.dentrothystVial.createStack(2, 3), " r ", "x x", " x ", 'x', new ItemStack(BLBlockRegistry.dentrothyst, 1, 1), 'r', ItemGeneric.createStack(EnumItemGeneric.RUBBER_BALL));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodRowboat), "x x", "xxx", "ttt", 'x', BLBlockRegistry.weedwoodPlanks, 't', ItemGeneric.createStack(EnumItemGeneric.TAR_DRIP));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.cavingRope, 4), "rrr", "ror", "rrr", 'r', new ItemStack(BLItemRegistry.rope), 'o', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.syrmoriteHopper), "s s", "scs", " s ", 's', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT), 'c', new ItemStack(BLBlockRegistry.weedwoodChest));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.weedwoodSign, 3), "SSS", "SSS", " x ", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.mossBed), "xxx", "PPP", 'x',  ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MOSS), 'P', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.mossBed), "xxx", "PPP", 'x',  ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CAVE_MOSS), 'P', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(ItemGeneric.createStack(EnumItemGeneric.PARCHMENT, 3), "###", '#', ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.manualHL), "LLL", "xxx", "LLL", 'x',  ItemGeneric.createStack(EnumItemGeneric.PARCHMENT), 'L', ItemGeneric.createStack(EnumItemGeneric.LURKER_SKIN));
		
		//Machine Blocks
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purifier), "x x", "xxx", "ooo", 'x', BLBlockRegistry.weedwoodPlanks, 'o', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.compostBin), "bxb", "x x", "x x", 'x', BLBlockRegistry.weedwoodPlanks, 'b', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.animator), "xxx", "shs", "bbb", 'x', BLBlockRegistry.weedwoodPlanks, 's', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'h', new ItemStack(BLItemRegistry.wightsHeart), 'b' ,new ItemStack(BLBlockRegistry.betweenstone));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.pestleAndMortar), "x x", "xxx", "s s", 'x', new ItemStack(BLBlockRegistry.genericStone, 1, 1), 's', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.alembic), " o ", " dv", "coc", 'o', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 'd', new ItemStack(Item.getItemFromBlock(BLBlockRegistry.dentrothyst)), 'v', new ItemStack(BLItemRegistry.dentrothystVial), 'c', new ItemStack(BLBlockRegistry.genericStone, 1, 1));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.infuser), "o o", "opo", "sos", 'o', ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 'd', new ItemStack(Item.getItemFromBlock(BLBlockRegistry.dentrothyst)), 'p', new ItemStack(BLItemRegistry.pestle), 's', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.geckoCage), "sps", "rrr", "sps", 's', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT), 'p', new ItemStack(Item.getItemFromBlock(BLBlockRegistry.weedwoodPlankSlab)), 'r', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.repeller), " wv", " w ", " c ", 'w', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'v', new ItemStack(BLItemRegistry.dentrothystVial), 'c', new ItemStack(BLBlockRegistry.genericStone, 1, 1));
		
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
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.bulbCappedMushroomTop, 4), "xx", "xx", 'x',  BLItemRegistry.bulbCappedMushroomItem);
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.slimyBoneBlock), "xxx", "xxx", "xxx", 'x', ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE));
		GameRegistry.addShapelessRecipe(ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE, 9), new ItemStack(BLBlockRegistry.slimyBoneBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.aquaMiddleGemBlock), "xxx", "xxx", "xxx", 'x', new ItemStack(BLItemRegistry.aquaMiddleGem));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.aquaMiddleGem, 9), new ItemStack(BLBlockRegistry.aquaMiddleGemBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.crimsonMiddleGemBlock), "xxx", "xxx", "xxx", 'x', new ItemStack(BLItemRegistry.crimsonMiddleGem));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.crimsonMiddleGem, 9), new ItemStack(BLBlockRegistry.crimsonMiddleGemBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.greenMiddleGemBlock), "xxx", "xxx", "xxx", 'x', new ItemStack(BLItemRegistry.greenMiddleGem));
		GameRegistry.addShapelessRecipe(new ItemStack(BLItemRegistry.greenMiddleGem, 9), new ItemStack(BLBlockRegistry.greenMiddleGemBlock));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.blockOfCompost), "xxx", "xxx", "xxx", 'x', ItemGeneric.createStack(EnumItemGeneric.COMPOST));
		GameRegistry.addShapelessRecipe(ItemGeneric.createStack(EnumItemGeneric.COMPOST, 9), new ItemStack(BLBlockRegistry.blockOfCompost));

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.blockWoodChipPath, 4), "###", '#', ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WEEDWOOD_BARK));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.blockWalkWay, 3), "SSS", "x x", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BLItemRegistry.doorWeedwood, 1), "##", "##", "##", '#', BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BLItemRegistry.doorRubber, 1), "##", "##", "##", '#', BLBlockRegistry.rubberTreePlanks));
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.doorSyrmorite, 1), "##", "##", "##", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BLBlockRegistry.trapDoorWeedwood, 2), "###", "###", '#', "plankWeedwood"));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.trapDoorSyrmorite, 2), "###", "###", '#', ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));

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

		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.weedwoodPlankFenceGate, 1), "xSx", "xSx", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.weedwoodPlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.rubberTreePlankFenceGate, 1), "xSx", "xSx", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.rubberTreePlanks));
		GameRegistry.addRecipe(new ItemStack(BLBlockRegistry.purpleRainPlankFenceGate, 1), "xSx", "xSx", 'x',  ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'S', new ItemStack(BLBlockRegistry.purpleRainPlanks));

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
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.reedDonut, 1), " # ", "# #", " # ", '#', ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED));
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
		GameRegistry.addRecipe(new ItemStack(BLItemRegistry.tarminion, 1), "ttt", "tht", "ttt", 't', ItemGeneric.createStack(EnumItemGeneric.TAR_DRIP), 'h', ItemGeneric.createStack(EnumItemGeneric.TAR_BEAST_HEART_ANIMATED));
		RecipeSorter.register("thebetweenlands:bookCloning", BookCloneRecipe.class, SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new BookCloneRecipe());
		RecipeSorter.register("thebetweenlands:bookMerging", BookMergeRecipe.class, SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new BookMergeRecipe());
		RecipeSorter.register("thebetweenlands:recipesCircleGems", RecipesCircleGems.class, SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new RecipesCircleGems());
		RecipeSorter.register("thebetweenlands:recipesAspectVials", RecipesAspectVials.class, SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new RecipesAspectVials());
		RecipeSorter.register("thebetweenlands:recipesAspectrusSeeds", RecipesAspectrusSeeds.class, SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new RecipesAspectrusSeeds());

		GameRegistry.addRecipe(new RecipeFairyLights());
		for (LightVariant variant : LightVariant.values()) {
			GameRegistry.addRecipe(new RecipeDyeColorNBT(variant.getCraftingResult(), variant.getCraftingRecipe()));
		}
		RecipeSorter.register(ModInfo.ID + ":fairy_lights", RecipeFairyLights.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		RecipeSorter.register(ModInfo.ID + ":dye_color_nbt", RecipeDyeColorNBT.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
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
		OreDictionary.registerOre("oreBone", new ItemStack(BLBlockRegistry.boneOre));
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
		OreDictionary.registerOre("plankWeedwood", new ItemStack(BLBlockRegistry.weedwoodPlanks));
		OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.bulbCappedMushroomItem));
		OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.blackHatMushroomItem));
		OreDictionary.registerOre("foodMushroom", new ItemStack(BLItemRegistry.flatheadMushroomItem));

		OreDictionary.registerOre("ingotSyrmorite", ItemGeneric.createStack(EnumItemGeneric.SYRMORITE_INGOT));
		OreDictionary.registerOre("ingotOctine", ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT));

	}

	private static void registerPurifierRecipes() {
		PurifierRecipe.addRecipe(new ItemStack(BLBlockRegistry.genericStone, 1, BlockGenericStone.META_CRAGROCK), new ItemStack(BLBlockRegistry.genericStone, 1, BlockGenericStone.META_MOSSYCRAGROCK1));
		PurifierRecipe.addRecipe(new ItemStack(BLBlockRegistry.genericStone, 1, BlockGenericStone.META_CRAGROCK), new ItemStack(BLBlockRegistry.genericStone, 1, BlockGenericStone.META_MOSSYCRAGROCK2));
		PurifierRecipe.addRecipe(new ItemStack(BLItemRegistry.aquaMiddleGem, 1), new ItemStack(BLBlockRegistry.aquaMiddleGemOre));
		PurifierRecipe.addRecipe(new ItemStack(BLItemRegistry.crimsonMiddleGem, 1), new ItemStack(BLBlockRegistry.crimsonMiddleGemOre));
		PurifierRecipe.addRecipe(new ItemStack(BLItemRegistry.greenMiddleGem, 1), new ItemStack(BLBlockRegistry.greenMiddleGemOre));
		PurifierRecipe.addRecipe(new ItemStack(BLBlockRegistry.farmedDirt, 1, 0), new ItemStack(BLBlockRegistry.swampDirt));
		PurifierRecipe.addRecipe(BLItemRegistry.dentrothystVial.createStack(0), BLItemRegistry.dentrothystVial.createStack(1));
	}

	private static void registerPestleAndMortarRecipes() {
		PestleAndMortarRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), new ItemStack(BLBlockRegistry.limestone));
		PestleAndMortarRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), new ItemStack(BLBlockRegistry.chiseledLimestone));
		PestleAndMortarRecipe.addRecipe(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), new ItemStack(BLBlockRegistry.polishedLimestone));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ALGAE), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.ALGAE));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ANGLER_TOOTH), ItemGeneric.createStack(EnumItemGeneric.ANGLER_TOOTH));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_AQUA_MIDDLE_GEM), new ItemStack(BLItemRegistry.aquaMiddleGem, 1));
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
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CRIMSON_MIDDLE_GEM), new ItemStack(BLItemRegistry.crimsonMiddleGem, 1));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DEEP_WATER_CORAL), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.DEEP_WATER_CORAL));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED), ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_FLATHEAD_MUSHROOM), new ItemStack(BLItemRegistry.flatheadMushroomItem));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GOLDEN_CLUB), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.GOLDEN_CLUB_FLOWERS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GREEN_MIDDLE_GEM), new ItemStack(BLItemRegistry.greenMiddleGem, 1));
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
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_HANGER), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.HANGER));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WATER_WEEDS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.WATER_WEEDS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_VENUS_FLY_TRAP), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.VENUS_FLY_TRAP));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_VOLARPAD), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.VOLARPAD));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_THORNS), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.THORNS));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_POISON_IVY), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.POISON_IVY));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PITCHER_PLANT), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.PITCHER_PLANT_TRAP));
		PestleAndMortarRecipe.addRecipe(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GENERIC_LEAF), ItemGenericPlantDrop.createStack(EnumItemPlantDrop.GENERIC_LEAF));
	}

	private static void registerDruidAltarRecipes() {
		DruidAltarRecipe.addRecipe(new ItemStack(BLItemRegistry.swampTalisman, 1, ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_1.ordinal()), new ItemStack(BLItemRegistry.swampTalisman, 1, ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_2.ordinal()), new ItemStack(BLItemRegistry.swampTalisman, 1, ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_3.ordinal()), new ItemStack(BLItemRegistry.swampTalisman, 1, ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_4.ordinal()), new ItemStack(BLItemRegistry.swampTalisman, 1, ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN.ordinal()));
	}

	private static void registerAnimatorRecipes() {
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(BLItemRegistry.scroll), 16, 16){
			private final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(new ItemStack(BLItemRegistry.lifeCrystal), 10), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD), 20), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 30), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.SULFUR), 40) };

			@Override
			public ItemStack onAnimated(World world, int x, int y, int z) {
				WeightedRandomItem randItem = (WeightedRandomItem) WeightedRandom.getRandomItem(world.rand, this.items);
				ItemStack result = randItem.getItem(world.rand);
				result.stackSize = Math.min(1 + world.rand.nextInt(randItem.itemWeight + 4), result.getMaxStackSize());
				return result;
			}
		});
		AnimatorRecipe.addRecipe(new AnimatorRecipe(ItemGeneric.createStack(EnumItemGeneric.TAR_BEAST_HEART), 32, 32, ItemGeneric.createStack(EnumItemGeneric.TAR_BEAST_HEART_ANIMATED)));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(ItemGeneric.createStack(EnumItemGeneric.INANIMATE_TARMINION), 8, 8, new ItemStack(BLItemRegistry.tarminion)));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(BLItemRegistry.testItem), 2, 1) {
			@Override
			public boolean onRetrieved(TileEntityAnimator tile, World world, int x, int y, int z) {
				EntityItem entityitem = new EntityItem(world, x, y + 1D, z, new ItemStack(BLItemRegistry.testItem));
				entityitem.motionX = 0;
				entityitem.motionZ = 0;
				entityitem.motionY = 0.11000000298023224D;
				world.spawnEntityInWorld(entityitem);
				tile.setInventorySlotContents(0, null);
				return false;
			}
		});
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(BLItemRegistry.spores), 8, 4, EntitySporeling.class));
	}

	private static void registerCompostItems(){
		CompostRecipe.addRecipe(100, 1, BLItemRegistry.testItem);
		CompostRecipe.addRecipe(30, 12000, BLItemRegistry.itemsGeneric, ItemGeneric.createStack(EnumItemGeneric.ROTTEN_BARK).getItemDamage());
		CompostRecipe.addRecipe(25, 12000, Item.getItemFromBlock(BLBlockRegistry.rottenWeedwoodBark));
		CompostRecipe.addRecipe(10, 8000, Item.getItemFromBlock(BLBlockRegistry.sundew));
		CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BLBlockRegistry.doubleSwampTallgrass));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.phragmites));
		CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BLBlockRegistry.tallCattail));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.cardinalFlower));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.broomsedge));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BLBlockRegistry.weepingBlue));
		CompostRecipe.addRecipe(12, 11000, Item.getItemFromBlock(BLBlockRegistry.pitcherPlant));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BLBlockRegistry.bogBean));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BLBlockRegistry.goldenClub));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BLBlockRegistry.marshMarigold));
		CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BLBlockRegistry.swampKelp));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.waterWeeds));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.waterFlower));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.waterFlowerStalk));
		CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BLBlockRegistry.root));
		CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BLBlockRegistry.rootUW));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.blackHatMushroom));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.flatHeadMushroom));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.bulbCappedMushroom));
		CompostRecipe.addRecipe(4, 6000, Item.getItemFromBlock(BLBlockRegistry.swampPlant));
		CompostRecipe.addRecipe(12, 10000, Item.getItemFromBlock(BLBlockRegistry.venusFlyTrap));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BLBlockRegistry.volarpad));
		CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BLBlockRegistry.weedwoodBush));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.thorns));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.poisonIvy));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BLBlockRegistry.wallPlants));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BLBlockRegistry.wallPlants), 1);
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BLBlockRegistry.caveMoss));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.caveGrass));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.catTail));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.swampTallGrass));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.shoots));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BLBlockRegistry.nettleFlowered));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.nettle));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.arrowArum));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.buttonBush));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.marshHibiscus));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.pickerelWeed));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.softRush));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.marshMallow));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.blueIris));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.copperIris));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.blueEyedGrass));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.milkweed));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.boneset));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.bottleBrushGrass));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.sludgecreep));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.deadWeedwoodBush));
		CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BLBlockRegistry.hanger));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BLBlockRegistry.waterFlowerStalk));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BLBlockRegistry.mireCoral));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BLBlockRegistry.deepWaterCoral));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BLBlockRegistry.saplingRubberTree));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BLBlockRegistry.saplingSapTree));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BLBlockRegistry.saplingWeedwood));
		CompostRecipe.addRecipe(3, 5000, BLItemRegistry.itemsGeneric, ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED).getItemDamage());
		CompostRecipe.addRecipe(3, 5000, BLItemRegistry.itemsGeneric, ItemGeneric.createStack(EnumItemGeneric.DRIED_SWAMP_REED).getItemDamage());
		CompostRecipe.addRecipe(5, 8000, BLItemRegistry.itemsGeneric, ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE).getItemDamage());
		CompostRecipe.addRecipe(5, 8000, BLItemRegistry.itemsGeneric, ItemGeneric.createStack(EnumItemGeneric.TANGLED_ROOT).getItemDamage());
		CompostRecipe.addRecipe(3, 5000, BLItemRegistry.itemsGeneric, ItemGeneric.createStack(EnumItemGeneric.SWAMP_KELP).getItemDamage());
		CompostRecipe.addRecipe(5, 8000, BLItemRegistry.flatheadMushroomItem);
		CompostRecipe.addRecipe(5, 8000, BLItemRegistry.blackHatMushroomItem);
		CompostRecipe.addRecipe(5, 8000, BLItemRegistry.bulbCappedMushroomItem);
		CompostRecipe.addRecipe(12, 10000, BLItemRegistry.yellowDottedFungus);

		for(EnumItemGenericCrushed type : EnumItemGenericCrushed.values()) {
			CompostRecipe.addRecipe(3, 4000, BLItemRegistry.itemsGenericCrushed, ItemGenericCrushed.createStack(type).getItemDamage());
		}

		for(EnumItemPlantDrop type : EnumItemPlantDrop.values()) {
			CompostRecipe.addRecipe(3, 4000, BLItemRegistry.itemsGenericPlantDrop, ItemGenericPlantDrop.createStack(type).getItemDamage());
		}
	}
}
