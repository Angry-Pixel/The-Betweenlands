package thebetweenlands.common.registries;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemSwampTalisman.EnumTalisman;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.recipe.misc.RecipeLurkerSkinPouchUpgrades;
import thebetweenlands.common.recipe.misc.RecipesCircleGems;
import thebetweenlands.common.recipe.misc.RecipesCoating;
import thebetweenlands.common.recipe.misc.RecipesLifeCrystal;
import thebetweenlands.common.recipe.misc.RecipesPlantTonic;
import thebetweenlands.common.recipe.misc.ShapedRecipesBetweenlands;
import thebetweenlands.common.recipe.misc.ShapelessRecipesBetweenlands;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.util.RecipeHelper;

@Mod.EventBusSubscriber
public class RecipeRegistry {
	private RecipeRegistry() { }

	public static void init() {
		registerOreDictionary();
//		registerRecipes();
		registerSmelting();
		registerPurifierRecipes();
		registerPestleAndMortarRecipes();
		registerCompostRecipes();
		registerDruidAltarRecipes();
		registerAnimatorRecipes();
		
		CustomRecipeRegistry.loadCustomRecipes();
	}

	private static void registerOreDictionary() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(BlockRegistry.SULFUR_ORE));
		OreDictionary.registerOre("oreSyrmorite", new ItemStack(BlockRegistry.SYRMORITE_ORE));
		OreDictionary.registerOre("oreBone", new ItemStack(BlockRegistry.SLIMY_BONE_ORE));
		OreDictionary.registerOre("oreOctine", new ItemStack(BlockRegistry.OCTINE_ORE));
		OreDictionary.registerOre("oreValonite", new ItemStack(BlockRegistry.VALONITE_ORE));
		OreDictionary.registerOre("oreAquaMiddleGem", new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreGreenMiddleGem", new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreCrimsonMiddleGem", new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreLifeCrystal", new ItemStack(BlockRegistry.LIFE_CRYSTAL_STALACTITE));
		
		OreDictionary.registerOre("blockSulfur", new ItemStack(BlockRegistry.SULFUR_BLOCK));
		OreDictionary.registerOre("blockSyrmorite", new ItemStack(BlockRegistry.SYRMORITE_BLOCK));
		OreDictionary.registerOre("blockBone", new ItemStack(BlockRegistry.SLIMY_BONE_BLOCK));
		OreDictionary.registerOre("blockOctine", new ItemStack(BlockRegistry.OCTINE_BLOCK));
		OreDictionary.registerOre("blockValonite", new ItemStack(BlockRegistry.VALONITE_BLOCK));
		OreDictionary.registerOre("blockAquaMiddleGem", new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK));
		OreDictionary.registerOre("blockGreenMiddleGem", new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK));
		OreDictionary.registerOre("blockCrimsonMiddleGem", new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK));
		
		OreDictionary.registerOre("blockGlass", new ItemStack(BlockRegistry.SILT_GLASS));
		OreDictionary.registerOre("blockGlassColorless", new ItemStack(BlockRegistry.SILT_GLASS));
		OreDictionary.registerOre("paneGlass", new ItemStack(BlockRegistry.SILT_GLASS_PANE));
		OreDictionary.registerOre("paneGlassColorless", new ItemStack(BlockRegistry.SILT_GLASS_PANE));
		
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.WEEDWOOD));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_WEEDWOOD));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_SAP));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_RUBBER));
		
		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.SWAMP_DIRT));
		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.COARSE_SWAMP_DIRT));
		
		OreDictionary.registerOre("grass", new ItemStack(BlockRegistry.SWAMP_GRASS));
		
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
		
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_WEEDWOOD_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SAP_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_RUBBER_TREE));
		
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_WEEDWOOD));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_SAP));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_RUBBER));
		
		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM));
		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.BLACK_HAT_MUSHROOM_ITEM));
		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM));
		
		OreDictionary.registerOre("ingotSyrmorite", EnumItemMisc.SYRMORITE_INGOT.create(1));
		OreDictionary.registerOre("ingotOctine", new ItemStack(ItemRegistry.OCTINE_INGOT));
		
		OreDictionary.registerOre("gemValonite", EnumItemMisc.VALONITE_SHARD.create(1));
		OreDictionary.registerOre("gemAquaMiddleGem", new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM));
		OreDictionary.registerOre("gemCrimsonMiddleGem", new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM));
		OreDictionary.registerOre("gemGreenMiddleGem", new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM));
		OreDictionary.registerOre("gemLifeCrystal", new ItemStack(ItemRegistry.LIFE_CRYSTAL));
		
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_SLAB));
		
		OreDictionary.registerOre("torch", new ItemStack(BlockRegistry.SULFUR_TORCH));
		
		OreDictionary.registerOre("bone", EnumItemMisc.SLIMY_BONE.create(1));
		
		OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.BETWEENSTONE));
		OreDictionary.registerOre("stone", new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));
		
		OreDictionary.registerOre("sand", new ItemStack(BlockRegistry.SILT));
		
		OreDictionary.registerOre("workbench", new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH));
		
		OreDictionary.registerOre("chest", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));
		OreDictionary.registerOre("chestWood", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));
		
		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.POISON_IVY));
		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.THORNS));
		
		OreDictionary.registerOre("sugarcane", new ItemStack(ItemRegistry.SWAMP_REED_ITEM));
		
		OreDictionary.registerOre("stickWood", EnumItemMisc.WEEDWOOD_STICK.create(1));
		
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ASTATOS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.BETWEEN_YOU_AND_ME));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.CHRISTMAS_ON_THE_MARSH));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.THE_EXPLORER));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.HAG_DANCE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.LONELY_FIRE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.MYSTERIOUS_RECORD));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ANCIENT));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.BENEATH_A_GREEN_SKY));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.DJ_WIGHTS_MIXTAPE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ONWARDS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.STUCK_IN_THE_MUD));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.WANDERING_WISPS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.WATERLOGGED));
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> register) {
		IForgeRegistry<IRecipe> registry = register.getRegistry();
//		RecipeSorter.register("thebetweenlands:shaped", ShapedRecipesBetweenlands.class, SHAPED, "before:minecraft:shaped");
//		RecipeSorter.register("thebetweenlands:shapeless", ShapelessRecipesBetweenlands.class, SHAPELESS, "after:thebetweenlands:shaped before:minecraft:shapeless");

		
//		// Tools & Weapons
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_PICKAXE, 1), "XXX", " # ", " # ", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_SHOVEL, 1), "X", "#", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_AXE, 1), "XX", "X#", " #", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_SWORD, 1), "X", "X", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_PICKAXE, 1), "XXX", " # ", " # ", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.SLIMY_BONE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_SHOVEL, 1), "X", "#", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.SLIMY_BONE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_AXE, 1), "XX", "X#", " #", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.SLIMY_BONE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_SWORD, 1), "X", "X", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.SLIMY_BONE.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.OCTINE_PICKAXE, 1), "XXX", " # ", " # ", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(ItemRegistry.OCTINE_INGOT));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.OCTINE_SHOVEL, 1), "X", "#", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(ItemRegistry.OCTINE_INGOT));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.OCTINE_AXE, 1), "XX", "X#", " #", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(ItemRegistry.OCTINE_INGOT));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.OCTINE_SWORD, 1), "X", "X", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(ItemRegistry.OCTINE_INGOT));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_PICKAXE, 1), "XXX", " # ", " # ", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.VALONITE_SHARD.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_SHOVEL, 1), "X", "#", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.VALONITE_SHARD.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_AXE, 1), "XX", "X#", " #", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.VALONITE_SHARD.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_SWORD, 1), "X", "X", "#", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.VALONITE_SHARD.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.OCTINE_SHIELD), "XXX", "XXX", " X ", 'X', ItemRegistry.OCTINE_INGOT);
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_SHIELD), "XIX", "XXX", " X ", 'X', EnumItemMisc.VALONITE_SHARD.create(1), 'I', BlockRegistry.PITSTONE);
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_SHIELD), "XIX", "XXX", " X ", 'X', new ItemStack(BlockRegistry.WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE), 'I', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_SHIELD), "XIX", "XXX", " X ", 'X', new ItemStack(BlockRegistry.LOG_WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE), 'I', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_SHIELD), "XXX", "XXX", " X ", 'X', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_SHIELD), "XIX", "XXX", " X ", 'X', EnumItemMisc.SLIMY_BONE.create(1), 'I', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.DENTROTHYST_SHIELD_GREEN), "XIX", "XXX", " X ", 'X', new ItemStack(BlockRegistry.DENTROTHYST, 1, EnumDentrothyst.GREEN.getMeta()), 'I', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.DENTROTHYST_SHIELD_ORANGE), "XIX", "XXX", " X ", 'X', new ItemStack(BlockRegistry.DENTROTHYST, 1, EnumDentrothyst.ORANGE.getMeta()), 'I', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.DENTROTHYST_SHIELD_GREEN_POLISHED), "XIX", "XXX", " X ", 'X', new ItemStack(BlockRegistry.POLISHED_DENTROTHYST, 1, EnumDentrothyst.GREEN.getMeta()), 'I', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.DENTROTHYST_SHIELD_ORANGE_POLISHED), "XIX", "XXX", " X ", 'X', new ItemStack(BlockRegistry.POLISHED_DENTROTHYST, 1, EnumDentrothyst.ORANGE.getMeta()), 'I', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.LURKER_SKIN_SHIELD), "XIX", "XXX", " X ", 'X', EnumItemMisc.LURKER_SKIN.create(1), 'I', EnumItemMisc.WEEDWOOD_STICK.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_BOW, 1), " #X", "# X", " #X", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW, 4), "X", "#", "Y", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', EnumItemMisc.ANGLER_TOOTH.create(1), 'Y', EnumItemMisc.DRAGONFLY_WING.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.OCTINE_ARROW, 4), "X", "#", "Y", '#', EnumItemMisc.WEEDWOOD_STICK.create(1), 'X', new ItemStack(ItemRegistry.OCTINE_INGOT), 'Y', EnumItemMisc.DRAGONFLY_WING.create(1));
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW, 1), EnumItemMisc.POISON_GLAND.create(1), new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_SHEARS, 1), " #", "# ", '#', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SICKLE, 1), " vv", "v s", "  r", 'v', EnumItemMisc.VALONITE_SHARD.create(1), 's', EnumItemMisc.WEEDWOOD_STICK.create(1), 'r', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.ROPE_ITEM, 1), "#", "#", "#", '#', new ItemStack(BlockRegistry.HANGER));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.ROPE_ITEM, 1), "#", "#", "#", '#', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.NET, 1), "SRR", "SRR", "S  ", 'S', EnumItemMisc.WEEDWOOD_STICK.create(1), 'R', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.LURKER_SKIN_POUCH), "RRR", "L L", "LLL", 'L', EnumItemMisc.LURKER_SKIN.create(1), 'R', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		GameRegistry.addRecipe(new RecipeLurkerSkinPouchUpgrades());
//
//		//Swamp talisman made from BL materials for a return portal (or in case portal doesn't generate in BL)
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.SWAMP_TALISMAN, 1), EnumItemPlantDrop.MOSS_ITEM.create(1), EnumItemMisc.SLIMY_BONE.create(1), new ItemStack(ItemRegistry.LIFE_CRYSTAL, 1));
//
//		// Armour
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.LURKER_SKIN_HELMET, 1), "###", "# #", '#', EnumItemMisc.LURKER_SKIN.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.LURKER_SKIN_CHESTPLATE, 1), "# #", "###", "###", '#', EnumItemMisc.LURKER_SKIN.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.LURKER_SKIN_LEGGINGS, 1), "###", "# #", "# #", '#', EnumItemMisc.LURKER_SKIN.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.LURKER_SKIN_BOOTS, 1), "# #", "# #", '#', EnumItemMisc.LURKER_SKIN.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_HELMET, 1), "###", "# #", '#', EnumItemMisc.SLIMY_BONE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_CHESTPLATE, 1), "# #", "###", "###", '#', EnumItemMisc.SLIMY_BONE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_LEGGINGS, 1), "###", "# #", "# #", '#', EnumItemMisc.SLIMY_BONE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.BONE_BOOTS, 1), "# #", "# #", '#', EnumItemMisc.SLIMY_BONE.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_HELMET, 1), "###", "# #", '#', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_CHESTPLATE, 1), "# #", "###", "###", '#', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_LEGGINGS, 1), "###", "# #", "# #", '#', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_BOOTS, 1), "# #", "# #", '#', EnumItemMisc.SYRMORITE_INGOT.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_HELMET, 1), "###", "# #", '#', EnumItemMisc.VALONITE_SHARD.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_CHESTPLATE, 1), "# #", "###", "###", '#', EnumItemMisc.VALONITE_SHARD.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_LEGGINGS, 1), "###", "# #", "# #", '#', EnumItemMisc.VALONITE_SHARD.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.VALONITE_BOOTS, 1), "# #", "# #", '#', EnumItemMisc.VALONITE_SHARD.create(1));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.RUBBER_BOOTS, 1), "# #", "# #", '#', EnumItemMisc.RUBBER_BALL.create(1));
//
//		// Miscellaneous
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SULFUR_TORCH, 4), "#", "I", 'I', EnumItemMisc.WEEDWOOD_STICK.create(1), '#', EnumItemMisc.SULFUR.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANKS, 4), "#", '#', new ItemStack(BlockRegistry.LOG_WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANKS, 4), "#", '#', new ItemStack(BlockRegistry.WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE));
//		RecipeHelper.addRecipe(EnumItemMisc.WEEDWOOD_STICK.create(4), "p", "p", 'p', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH), "xx", "xx", 'x', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_CHEST), "xxx", "x x", "xxx", 'x', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SULFUR_FURNACE), "xxx", "x x", "xxx", 'x', BlockRegistry.BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SULFUR_FURNACE_DUAL), "xxx", "xfx", "xxx", 'x', BlockRegistry.BETWEENSTONE, 'f', BlockRegistry.SULFUR_FURNACE);
//		RecipeHelper.addRecipe(EnumItemMisc.SWAMP_REED_ROPE.create(4), "p", "p" , "p", 'p', new ItemStack(ItemRegistry.SWAMP_REED_ITEM));
//		RecipeHelper.addRecipe(EnumItemMisc.WEEDWOOD_BOWL.create(4), "x x", " x ", 'x', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_BUCKET), " X ", "x x", " x ", 'x', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS),'X', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_BUCKET), " X ", "x x", " x ", 'x', EnumItemMisc.SYRMORITE_INGOT.create(1),'X', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_LADDER, 3), "X X", "xxx", "X X", 'x', EnumItemMisc.WEEDWOOD_STICK.create(1),'X', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANK_BUTTON), "#", '#', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_BUTTON), "#", '#', new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANK_PRESSURE_PLATE), "xx", 'x', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_PRESSURE_PLATE), "xx", 'x', BlockRegistry.SMOOTH_BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SYRMORITE_PRESSURE_PLATE), "xx", 'x', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MUD_FLOWER_POT), "x x", " x " , 'x', EnumItemMisc.MUD_BRICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_LEVER), "X", "x", 'x', new ItemStack(BlockRegistry.LOG_WEEDWOOD), 'X', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.PESTLE), "X", "x", "x", 'x', new ItemStack(BlockRegistry.CRAGROCK), 'X', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.ITEM_SHELF), "xxx", "   ", "xxx", 'x', BlockRegistry.WEEDWOOD_PLANK_SLAB);
//		//RecipeHelper.addRecipe(ItemRegistry.dentrothystVial.createStack(0, 3), " r ", "x x", " x ", 'x', new ItemStack(BlockRegistry.dentrothyst, 1, 0), 'r', ItemGeneric.createStack(EnumItemGeneric.RUBBER_BALL));
//		//RecipeHelper.addRecipe(ItemRegistry.dentrothystVial.createStack(2, 3), " r ", "x x", " x ", 'x', new ItemStack(BlockRegistry.dentrothyst, 1, 1), 'r', ItemGeneric.createStack(EnumItemGeneric.RUBBER_BALL));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_ROWBOAT), "x x", "xxx", 'x', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.CAVING_ROPE, 8), "rrr", "ror", "rrr", 'r', new ItemStack(ItemRegistry.ROPE_ITEM), 'o', new ItemStack(ItemRegistry.OCTINE_INGOT));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SYRMORITE_HOPPER), "s s", "scs", " s ", 's', EnumItemMisc.SYRMORITE_INGOT.create(1), 'c', new ItemStack(BlockRegistry.WEEDWOOD_CHEST));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_SIGN_ITEM, 3), "SSS", "SSS", " x ", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.MOSS_BED_ITEM), "xxx", "PPP", 'x',  EnumItemPlantDrop.MOSS_ITEM.create(1), 'P', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.MOSS_BED_ITEM), "xxx", "PPP", 'x',  EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1), 'P', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.MOSS_BED_ITEM), "xxx", "PPP", 'x',  new ItemStack(BlockRegistry.MOSS), 'P', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.MOSS_BED_ITEM), "xxx", "PPP", 'x',  new ItemStack(BlockRegistry.CAVE_MOSS), 'P', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(EnumItemMisc.PARCHMENT.create(3), "###", '#', EnumItemMisc.DRY_BARK.create(1));
//		//RecipeHelper.addRecipe(new ItemStack(ItemRegistry.manualHL), "LLL", "xxx", "LLL", 'x',  EnumItemMisc.PARCHMENT), 'L', ItemGeneric.createStack(EnumItemGeneric.LURKER_SKIN));
//		RecipeHelper.addRecipe(ItemAmulet.createStack(CircleGemType.NONE), "XXX", "X X", " # ", '#', EnumItemMisc.AMULET_SOCKET.create(1), 'X', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		//TODO: Volarkite
//		//RecipeHelper.addRecipe(new ItemStack(ItemRegistry.volarkite), "VVV", "RxR", " x ", 'x',  EnumItemMisc.WEEDWOOD_STICK), 'R', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE), 'V', ItemGenericPlantDrop.createStack(EnumItemPlantDrop.VOLARPAD));
//		//RecipeHelper.addRecipe(new ItemStack(ItemRegistry.volarkite), "VVV", "RxR", " x ", 'x',  EnumItemMisc.WEEDWOOD_STICK), 'R', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE), 'V', new ItemStack(BlockRegistry.volarpad));
//
//		//Machine Blocks
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PURIFIER), "x x", "xxx", "ooo", 'x', BlockRegistry.WEEDWOOD_PLANKS, 'o', new ItemStack(ItemRegistry.OCTINE_INGOT));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.COMPOST_BIN), "bxb", "x x", "x x", 'x', BlockRegistry.WEEDWOOD_PLANKS, 'b', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.ANIMATOR), "xxx", "shs", "bbb", 'x', BlockRegistry.WEEDWOOD_PLANKS, 's', EnumItemMisc.WEEDWOOD_STICK.create(1), 'h', new ItemStack(ItemRegistry.WIGHT_HEART), 'b' ,new ItemStack(BlockRegistry.BETWEENSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MORTAR), "x x", "xxx", "s s", 'x', BlockRegistry.CRAGROCK, 's', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		//RecipeHelper.addRecipe(new ItemStack(BlockRegistry.ALEMBIC), " o ", " dv", "coc", 'o', new ItemStack(ItemRegistry.OCTINE_INGOT), 'd', new ItemStack(Item.getItemFromBlock(BlockRegistry.DENTROTHYST)), 'v', new ItemStack(ItemRegistry.dentrothystVial), 'c', new ItemStack(BlockRegistry.genericStone, 1, 1)); //TODO: Dentrothyst vials
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.INFUSER), "o o", "opo", "sos", 'o', new ItemStack(ItemRegistry.OCTINE_INGOT), 'd', new ItemStack(Item.getItemFromBlock(BlockRegistry.DENTROTHYST)), 'p', new ItemStack(ItemRegistry.PESTLE), 's', EnumItemMisc.WEEDWOOD_STICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.GECKO_CAGE), "sps", "rrr", "sps", 's', EnumItemMisc.SYRMORITE_INGOT.create(1), 'p', new ItemStack(Item.getItemFromBlock(BlockRegistry.WEEDWOOD_PLANK_SLAB)), 'r', EnumItemMisc.SWAMP_REED_ROPE.create(1));
//		//RecipeHelper.addRecipe(new ItemStack(BlockRegistry.REPELLER), " wv", " w ", " c ", 'w', ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK), 'v', new ItemStack(ItemRegistry.dentrothystVial), 'c', new ItemStack(BlockRegistry.genericStone, 1, 1)); //TODO: Repeller
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_JUKEBOX), "xxx", "xVx", "xxx", 'x', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS), 'V', EnumItemMisc.VALONITE_SHARD.create(1));
//
//		//Deco Blocks
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MUD_BRICKS, 4), "xx", "xx", 'x', EnumItemMisc.MUD_BRICK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_TILES, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_TILES, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.SMOOTH_PITSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_BRICKS, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.BETWEENSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_BRICKS, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.PITSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_CHISELED, 4), "x", "x", 'x', new ItemStack(BlockRegistry.BETWEENSTONE_BRICK_SLAB));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_CHISELED, 4), "x", "x", 'x', new ItemStack(BlockRegistry.PITSTONE_BRICK_SLAB));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_TILES, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.SMOOTH_CRAGROCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_BRICKS, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.CRAGROCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_CHISELED, 4), "x", "x", 'x', new ItemStack(BlockRegistry.CRAGROCK_BRICK_SLAB));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_TILES, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.POLISHED_LIMESTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_BRICKS, 4), "xx", "xx", 'x', new ItemStack(BlockRegistry.LIMESTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_CHISELED, 4), "x", "x", 'x', new ItemStack(BlockRegistry.LIMESTONE_BRICK_SLAB));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SULFUR_BLOCK), "xxx", "xxx", "xxx", 'x', EnumItemMisc.SULFUR.create(1));
//		RecipeHelper.addRecipe(EnumItemMisc.SULFUR.create(9), "#", '#', new ItemStack(BlockRegistry.SULFUR_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.OCTINE_BLOCK), "xxx", "xxx", "xxx", 'x', new ItemStack(ItemRegistry.OCTINE_INGOT));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SYRMORITE_BLOCK), "xxx", "xxx", "xxx", 'x', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(EnumItemMisc.SYRMORITE_INGOT.create(9), "#", '#', new ItemStack(BlockRegistry.SYRMORITE_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.OCTINE_INGOT, 9), "#", '#', new ItemStack(BlockRegistry.OCTINE_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.VALONITE_BLOCK), "xxx", "xxx", "xxx", 'x', EnumItemMisc.VALONITE_SHARD.create(1));
//		RecipeHelper.addRecipe(EnumItemMisc.VALONITE_SHARD.create(9), "#", '#', new ItemStack(BlockRegistry.VALONITE_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.THATCH, 4), "xx", "xx", 'x', EnumItemMisc.DRIED_SWAMP_REED.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.RUBBER_TREE_PLANKS, 4), "#", '#', new ItemStack(BlockRegistry.LOG_RUBBER, 1, OreDictionary.WILDCARD_VALUE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MIRE_CORAL_BLOCK, 4), "xx", "xx", 'x', EnumItemPlantDrop.MIRE_CORAL_ITEM.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.DEEP_WATER_CORAL_BLOCK, 4), "xx", "xx", 'x', EnumItemPlantDrop.DEEP_WATER_CORAL_ITEM.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BULB_CAPPED_MUSHROOM_CAP, 4), "xx", "xx", 'x',  ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SLIMY_BONE_BLOCK), "xxx", "xxx", "xxx", 'x', EnumItemMisc.SLIMY_BONE.create(1));
//		RecipeHelper.addRecipe(EnumItemMisc.SLIMY_BONE.create(9), "#", '#', new ItemStack(BlockRegistry.SLIMY_BONE_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK), "xxx", "xxx", "xxx", 'x', new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM, 9), "#", '#', new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK), "xxx", "xxx", "xxx", 'x', new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM, 9), "#", '#', new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK), "xxx", "xxx", "xxx", 'x', new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM, 9), "#", '#', new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.COMPOST_BLOCK), "xxx", "xxx", "xxx", 'x', EnumItemMisc.COMPOST.create(1));
//		RecipeHelper.addRecipe(EnumItemMisc.COMPOST.create(9), "#", '#', new ItemStack(BlockRegistry.COMPOST_BLOCK));
//		RecipeHelper.addShapelessRecipe(new ItemStack(BlockRegistry.MOSSY_BETWEENSTONE_BRICKS, 1), EnumItemPlantDrop.MOSS_ITEM.create(1), new ItemStack(BlockRegistry.BETWEENSTONE_BRICKS));
//		RecipeHelper.addShapelessRecipe(new ItemStack(BlockRegistry.MOSSY_BETWEENSTONE_BRICKS, 1), EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1), new ItemStack(BlockRegistry.BETWEENSTONE_BRICKS));
//		RecipeHelper.addShapelessRecipe(new ItemStack(BlockRegistry.MOSSY_BETWEENSTONE_TILES, 1), EnumItemPlantDrop.MOSS_ITEM.create(1), new ItemStack(BlockRegistry.BETWEENSTONE_TILES));
//		RecipeHelper.addShapelessRecipe(new ItemStack(BlockRegistry.MOSSY_BETWEENSTONE_TILES, 1), EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1), new ItemStack(BlockRegistry.BETWEENSTONE_TILES));
//		RecipeHelper.addShapelessRecipe(new ItemStack(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE, 1), EnumItemPlantDrop.MOSS_ITEM.create(1), new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));
//		RecipeHelper.addShapelessRecipe(new ItemStack(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE, 1), EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1), new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.RUBBER_BLOCK), "xxx", "xxx", "xxx", 'x', EnumItemMisc.RUBBER_BALL.create(1));
//		RecipeHelper.addRecipe(EnumItemMisc.RUBBER_BALL.create(9), "#", '#', new ItemStack(BlockRegistry.RUBBER_BLOCK));
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WOOD_CHIP_PATH, 4), "###", '#', EnumItemCrushed.GROUND_WEEDWOOD_BARK.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WALKWAY, 2), "SSS", "x x", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.WEEDWOOD_DOOR_ITEM, 3), "##", "##", "##", '#', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.RUBBER_TREE_PLANK_DOOR_ITEM, 3), "##", "##", "##", '#', BlockRegistry.RUBBER_TREE_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.SYRMORITE_DOOR_ITEM, 3), "##", "##", "##", '#', EnumItemMisc.SYRMORITE_INGOT.create(1));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_TRAPDOOR, 2), "###", "###", '#', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_TRAPDOOR, 2), "###", "###", '#', BlockRegistry.RUBBER_TREE_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SYRMORITE_TRAPDOOR, 2), "###", "###", '#', EnumItemMisc.SYRMORITE_INGOT.create(1));
//
//		// Stairs, slabs, walls, fences, pillars
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.CRAGROCK);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.PITSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.PITSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MUD_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.MUD_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.CRAGROCK_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.LIMESTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.LIMESTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.SMOOTH_BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_CRAGROCK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.SMOOTH_CRAGROCK);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_PITSTONE_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.SMOOTH_PITSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.TAR_SOLID_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.TAR_SOLID);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.TEMPLE_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.TEMPLE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.RUBBER_TREE_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.MOSSY_BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.CRACKED_BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.POLISHED_LIMESTONE_STAIRS, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.POLISHED_LIMESTONE);
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.PITSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.PITSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MUD_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.MUD_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.CRAGROCK);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.CRAGROCK_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.LIMESTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.LIMESTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.POLISHED_LIMESTONE_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.POLISHED_LIMESTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.SMOOTH_BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.TAR_SOLID_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.TAR_SOLID);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.TEMPLE_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.TEMPLE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_PITSTONE_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.SMOOTH_PITSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_CRAGROCK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.SMOOTH_CRAGROCK);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.MOSSY_BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.CRACKED_BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE_WALL, 6), "xxx", "xxx", 'x', BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE);
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE, 3), "SxS", "SxS", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.LOG_WEEDWOOD));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE, 3), "SxS", "SxS", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE, 3), "SxS", "SxS", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.RUBBER_TREE_PLANKS));
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE_GATE, 1), "xSx", "xSx", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.LOG_WEEDWOOD));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE_GATE, 1), "xSx", "xSx", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE_GATE, 1), "xSx", "xSx", 'x',  EnumItemMisc.WEEDWOOD_STICK.create(1), 'S', new ItemStack(BlockRegistry.RUBBER_TREE_PLANKS));
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_SLAB, 6), "###", '#', BlockRegistry.CRAGROCK);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_SLAB, 6), "###", '#', BlockRegistry.PITSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_SLAB, 6), "###", '#', BlockRegistry.BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_BRICK_SLAB, 6), "###", '#', BlockRegistry.BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MUD_BRICK_SLAB, 6), "###", '#', BlockRegistry.MUD_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_BRICK_SLAB, 6), "###", '#', BlockRegistry.CRAGROCK_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_BRICK_SLAB, 6), "###", '#', BlockRegistry.LIMESTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.POLISHED_LIMESTONE_SLAB, 6), "###", '#', BlockRegistry.POLISHED_LIMESTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_SLAB, 6), "###", '#', BlockRegistry.LIMESTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE_SLAB, 6), "###", '#', BlockRegistry.SMOOTH_BETWEENSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_CRAGROCK_SLAB, 6), "###", '#', BlockRegistry.SMOOTH_CRAGROCK);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SMOOTH_PITSTONE_SLAB, 6), "###", '#', BlockRegistry.SMOOTH_PITSTONE);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_BRICK_SLAB, 6), "###", '#', BlockRegistry.PITSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.TAR_SOLID_SLAB, 6), "###", '#', BlockRegistry.TAR_SOLID);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.TEMPLE_BRICK_SLAB, 6), "###", '#', BlockRegistry.TEMPLE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.WEEDWOOD_PLANK_SLAB, 6), "###", '#', BlockRegistry.WEEDWOOD_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_SLAB, 6), "###", '#', BlockRegistry.RUBBER_TREE_PLANKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.THATCH_SLAB, 6), "###", '#', BlockRegistry.THATCH);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_SLAB, 6), "###", '#', BlockRegistry.MOSSY_BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_SLAB, 6), "###", '#', BlockRegistry.CRACKED_BETWEENSTONE_BRICKS);
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_SLAB, 6), "###", '#', BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE);
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.BETWEENSTONE_PILLAR, 2), "x", "x", 'x', new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.PITSTONE_PILLAR, 2), "x", "x", 'x', new ItemStack(BlockRegistry.SMOOTH_PITSTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.LIMESTONE_PILLAR, 2), "x", "x", 'x', new ItemStack(BlockRegistry.POLISHED_LIMESTONE));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.CRAGROCK_PILLAR, 2), "x", "x", 'x', new ItemStack(BlockRegistry.SMOOTH_CRAGROCK));
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.THATCH_ROOF, 4), "x  ", "xx ", "xxx", 'x', BlockRegistry.THATCH);
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.SILT_GLASS_PANE, 6), "xxx", "xxx", 'x', BlockRegistry.SILT_GLASS);
//
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.POLISHED_DENTROTHYST_PANE, 6, EnumDentrothyst.GREEN.getMeta()), "xxx", "xxx", 'x', new ItemStack(BlockRegistry.POLISHED_DENTROTHYST, 1, EnumDentrothyst.GREEN.getMeta()));
//		RecipeHelper.addRecipe(new ItemStack(BlockRegistry.POLISHED_DENTROTHYST_PANE, 6, EnumDentrothyst.ORANGE.getMeta()), "xxx", "xxx", 'x', new ItemStack(BlockRegistry.POLISHED_DENTROTHYST, 1, EnumDentrothyst.ORANGE.getMeta()));
//
//		//Food
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.REED_DONUT, 1), " # ", "# #", " # ", '#', EnumItemMisc.DRIED_SWAMP_REED.create(1));
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.JAM_DONUT, 1), new ItemStack(ItemRegistry.REED_DONUT), new ItemStack(ItemRegistry.MIDDLE_FRUIT));
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.GERTS_DONUT, 1), new ItemStack(ItemRegistry.REED_DONUT), new ItemStack(ItemRegistry.WIGHT_HEART), new ItemStack(Items.SLIME_BALL));
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.CRAB_STICK, 3), "  #", " # ", "#  ", '#', new ItemStack(ItemRegistry.SILT_CRAB_CLAW));
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.SAP_JELLO, 1), new ItemStack(ItemRegistry.SLUDGE_JELLO), new ItemStack(ItemRegistry.SAP_BALL));
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.MIDDLE_FRUIT_JELLO, 1), new ItemStack(ItemRegistry.SLUDGE_JELLO), new ItemStack(ItemRegistry.MIDDLE_FRUIT));
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.NETTLE_SOUP, 1), EnumItemMisc.WEEDWOOD_BOWL.create(1), ItemRegistry.BLACK_HAT_MUSHROOM_ITEM, ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM, BlockRegistry.NETTLE);
//		RecipeHelper.addShapelessRecipe(new ItemStack(ItemRegistry.NETTLE_SOUP, 1), EnumItemMisc.WEEDWOOD_BOWL.create(1), ItemRegistry.BLACK_HAT_MUSHROOM_ITEM, ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM, BlockRegistry.NETTLE_FLOWERED);
//
//		// Special Items
//		RecipeHelper.addRecipe(new ItemStack(ItemRegistry.TARMINION, 1), "ttt", "tht", "ttt", 't', EnumItemMisc.TAR_DRIP.create(1), 'h', EnumItemMisc.TAR_BEAST_HEART_ANIMATED.create(1));
//
		//TODO Recipes
		/*RecipeSorter.register("thebetweenlands:bookCloning", BookCloneRecipe.class, SHAPELESS, "after:minecraft:shapeless");
		RecipeHelper.addRecipe(new BookCloneRecipe());

		RecipeSorter.register("thebetweenlands:bookMerging", BookMergeRecipe.class, SHAPELESS, "after:minecraft:shapeless");
		RecipeHelper.addRecipe(new BookMergeRecipe());*/

//		RecipeSorter.register("thebetweenlands:recipesCircleGems", RecipesCircleGems.class, SHAPELESS, "after:minecraft:shapeless");
//		GameRegistry.addRecipe(new RecipesCircleGems());
//
//		/*RecipeSorter.register("thebetweenlands:recipesAspectVials", RecipesAspectVials.class, SHAPELESS, "after:minecraft:shapeless");
//		RecipeHelper.addRecipe(new RecipesAspectVials());
//
//		RecipeSorter.register("thebetweenlands:recipesAspectrusSeeds", RecipesAspectrusSeeds.class, SHAPELESS, "after:minecraft:shapeless");
//		RecipeHelper.addRecipe(new RecipesAspectrusSeeds());
//
//		RecipeSorter.register("thebetweenlands:recipeImprovedRubberBoots", RecipeImprovedRubberBoots.class, SHAPELESS, "after:minecraft:shapeless");
//		RecipeHelper.addRecipe(new RecipeImprovedRubberBoots());
//
//		RecipeSorter.register("thebetweenlands:summonMummy", RecipeSummonMummy.class, SHAPELESS, "after:minecraft:shapeless");
//		RecipeHelper.addRecipe(new RecipeSummonMummy());*/
//
//		RecipeSorter.register("thebetweenlands:recipesCoating", RecipesCoating.class, SHAPELESS, "before:minecraft:shapeless");
//		GameRegistry.addRecipe(new RecipesCoating());
//
//		RecipeSorter.register("thebetweenlands:recipesLifeCrystal", RecipesLifeCrystal.class, SHAPELESS, "after:minecraft:shapeless");
//		GameRegistry.addRecipe(new RecipesLifeCrystal());
//
//		RecipeSorter.register("thebetweenlands:recipesPlantTonic", RecipesPlantTonic.class, SHAPELESS, "after:minecraft:shapeless");
//		GameRegistry.addRecipe(new RecipesPlantTonic());

//
//		registry.register(new RecipesCircleGems().setRegistryName("thebetweenlands:recipesCircleGems"));
//		registry.register(new RecipesCoating().setRegistryName("thebetweenlands:recipesCoating"));
//		registry.register(new RecipesLifeCrystal().setRegistryName("thebetweenlands:recipesLifeCrystal"));
//		registry.register(new RecipesPlantTonic().setRegistryName("thebetweenlands:recipesPlantTonic"));
	}

	private static void registerSmelting() {
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.SYRMORITE_ORE), EnumItemMisc.SYRMORITE_INGOT.create(1), 0.7F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.OCTINE_ORE), new ItemStack(ItemRegistry.OCTINE_INGOT), 0.7F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.DAMP_TORCH), new ItemStack(Blocks.TORCH), 0F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.SWAMP_REED_ITEM), EnumItemMisc.DRIED_SWAMP_REED.create(1), 0.1F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.MUD), EnumItemMisc.MUD_BRICK.create(1), 0.2F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.KRAKEN_TENTACLE), new ItemStack(ItemRegistry.KRAKEN_CALAMARI, 5), 0.3F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.SWAMP_KELP_ITEM), new ItemStack(ItemRegistry.FRIED_SWAMP_KELP), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.ANGLER_MEAT_RAW), new ItemStack(ItemRegistry.ANGLER_MEAT_COOKED), 0.3F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.FROG_LEGS_RAW), new ItemStack(ItemRegistry.FROG_LEGS_COOKED), 0.3F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.SNAIL_FLESH_RAW), new ItemStack(ItemRegistry.SNAIL_FLESH_COOKED), 0.3F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.BETWEENSTONE), new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE), 0.1F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.PITSTONE), new ItemStack(BlockRegistry.SMOOTH_PITSTONE), 0.1F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.CRAGROCK), new ItemStack(BlockRegistry.SMOOTH_CRAGROCK), 0.1F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.LIMESTONE), new ItemStack(BlockRegistry.POLISHED_LIMESTONE), 0.1F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.DENTROTHYST, 1, EnumDentrothyst.GREEN.getMeta()), new ItemStack(BlockRegistry.POLISHED_DENTROTHYST, 1, EnumDentrothyst.GREEN.getMeta()), 0.3F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.DENTROTHYST, 1, EnumDentrothyst.ORANGE.getMeta()), new ItemStack(BlockRegistry.POLISHED_DENTROTHYST, 1, EnumDentrothyst.ORANGE.getMeta()), 0.3F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.SLUDGE_BALL), new ItemStack(ItemRegistry.SLUDGE_JELLO), 0.3F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.SILT), new ItemStack(BlockRegistry.SILT_GLASS), 0.2F);	
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.BETWEENSTONE_TILES), new ItemStack(BlockRegistry.CRACKED_BETWEENSTONE_TILES), 0.1F);
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.BETWEENSTONE_BRICKS), new ItemStack(BlockRegistry.CRACKED_BETWEENSTONE_BRICKS), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.WEEDWOOD_BUCKET_RUBBER), EnumItemMisc.RUBBER_BALL.create(1), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.SYRMORITE_BUCKET_RUBBER), new ItemStack(ItemRegistry.SYRMORITE_BUCKET_SOLID_RUBBER), 0.5F);
	}

	private static void registerDruidAltarRecipes() {
		DruidAltarRecipe.addRecipe(EnumTalisman.SWAMP_TALISMAN_1.create(1), EnumTalisman.SWAMP_TALISMAN_2.create(1), EnumTalisman.SWAMP_TALISMAN_3.create(1), EnumTalisman.SWAMP_TALISMAN_4.create(1), EnumTalisman.SWAMP_TALISMAN_0.create(1));
	}

	private static void registerCompostRecipes() {
		CompostRecipe.addRecipe(30, 12000, EnumItemMisc.DRY_BARK.create(1));
		CompostRecipe.addRecipe(25, 12000, Item.getItemFromBlock(BlockRegistry.HOLLOW_LOG));
		CompostRecipe.addRecipe(25, 12000, Item.getItemFromBlock(BlockRegistry.LOG_ROTTEN_BARK));
		CompostRecipe.addRecipe(10, 8000, Item.getItemFromBlock(BlockRegistry.SUNDEW));
		CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BlockRegistry.SWAMP_DOUBLE_TALLGRASS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.PHRAGMITES));
		CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BlockRegistry.TALL_CATTAIL));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.CARDINAL_FLOWER));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BROOMSEDGE));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.WEEPING_BLUE));
		CompostRecipe.addRecipe(12, 11000, Item.getItemFromBlock(BlockRegistry.PITCHER_PLANT));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.BOG_BEAN_FLOWER));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.BOG_BEAN_STALK));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.GOLDEN_CLUB_FLOWER));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.GOLDEN_CLUB_STALK));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.MARSH_MARIGOLD_FLOWER));
		CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.MARSH_MARIGOLD_STALK));
		CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BlockRegistry.SWAMP_KELP));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.WATER_WEEDS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLADDERWORT_FLOWER));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLADDERWORT_STALK));
		CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.ROOT));
		CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.ROOT_UNDERWATER));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLACK_HAT_MUSHROOM));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.FLAT_HEAD_MUSHROOM));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BULB_CAPPED_MUSHROOM));
		CompostRecipe.addRecipe(4, 6000, Item.getItemFromBlock(BlockRegistry.SWAMP_PLANT));
		CompostRecipe.addRecipe(12, 10000, Item.getItemFromBlock(BlockRegistry.VENUS_FLY_TRAP));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.VOLARPAD));
		CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.WEEDWOOD_BUSH));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.THORNS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.POISON_IVY));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.MOSS));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.LICHEN));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.CAVE_MOSS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.CAVE_GRASS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.CATTAIL));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SWAMP_TALLGRASS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SHOOTS));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.NETTLE_FLOWERED));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.NETTLE));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.ARROW_ARUM));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BUTTON_BUSH));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.MARSH_HIBISCUS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.PICKEREL_WEED));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SOFT_RUSH));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.MARSH_MALLOW));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLUE_IRIS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.COPPER_IRIS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLUE_EYED_GRASS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.MILKWEED));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BONESET));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BOTTLE_BRUSH_GRASS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SLUDGECREEP));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.DEAD_WEEDWOOD_BUSH));
		CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BlockRegistry.HANGER));
		CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BlockRegistry.ALGAE));
		CompostRecipe.addRecipe(3, 5000, ItemRegistry.ROPE_ITEM);
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.MIRE_CORAL));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.DEEP_WATER_CORAL));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_RUBBER));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_SAP));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_WEEDWOOD));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_RUBBER_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_SAP_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_WEEDWOOD_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.FALLEN_LEAVES));
		CompostRecipe.addRecipe(3, 5000, ItemRegistry.SWAMP_REED_ITEM);
		CompostRecipe.addRecipe(3, 5000, EnumItemMisc.DRIED_SWAMP_REED.create(1));
		CompostRecipe.addRecipe(5, 8000, EnumItemMisc.SWAMP_REED_ROPE.create(1));
		CompostRecipe.addRecipe(5, 8000, ItemRegistry.TANGLED_ROOT);
		CompostRecipe.addRecipe(3, 5000, ItemRegistry.SWAMP_KELP_ITEM);
		CompostRecipe.addRecipe(5, 8000, ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM);
		CompostRecipe.addRecipe(5, 8000, ItemRegistry.BLACK_HAT_MUSHROOM_ITEM);
		CompostRecipe.addRecipe(5, 8000, ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM);
		CompostRecipe.addRecipe(12, 10000, ItemRegistry.YELLOW_DOTTED_FUNGUS);

		for (ItemCrushed.EnumItemCrushed type : ItemCrushed.EnumItemCrushed.values()) {
			CompostRecipe.addRecipe(3, 4000, new ItemStack(ItemRegistry.ITEMS_CRUSHED, 1, type.getID()));
		}

		for (ItemPlantDrop.EnumItemPlantDrop type : ItemPlantDrop.EnumItemPlantDrop.values()) {
			CompostRecipe.addRecipe(3, 4000, new ItemStack(ItemRegistry.ITEMS_PLANT_DROP, 1, type.getID()));
		}
	}

	private static void registerPestleAndMortarRecipes() {
		PestleAndMortarRecipe.addRecipe((EnumItemMisc.LIMESTONE_FLUX.create(1)), new ItemStack(BlockRegistry.LIMESTONE));
		PestleAndMortarRecipe.addRecipe((EnumItemMisc.LIMESTONE_FLUX.create(1)), new ItemStack(BlockRegistry.POLISHED_LIMESTONE));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ALGAE.create(1)), (ItemPlantDrop.EnumItemPlantDrop.ALGAE_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ANGLER_TOOTH.create(1)), (EnumItemMisc.ANGLER_TOOTH.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_AQUA_MIDDLE_GEM.create(1)), new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM, 1));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ARROW_ARUM.create(1)), (ItemPlantDrop.EnumItemPlantDrop.ARROW_ARUM_LEAF.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLACKHAT_MUSHROOM.create(1)), new ItemStack(ItemRegistry.BLACK_HAT_MUSHROOM_ITEM));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLOOD_SNAIL_SHELL.create(1)), (EnumItemMisc.BLOOD_SNAIL_SHELL.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLUE_EYED_GRASS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BLUE_EYED_GRASS_FLOWERS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLUE_IRIS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BLUE_IRIS_PETAL.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BOG_BEAN.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BOG_BEAN_FLOWER_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BONESET.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BONESET_FLOWERS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BOTTLE_BRUSH_GRASS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BOTTLE_BRUSH_GRASS_BLADES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BROOM_SEDGE.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BROOM_SEDGE_LEAVES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BULB_CAPPED_MUSHROOM.create(1)), new ItemStack(ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BUTTON_BUSH.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BUTTON_BUSH_FLOWERS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CARDINAL_FLOWER.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CARDINAL_FLOWER_PETALS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CATTAIL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CATTAIL_HEAD.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CAVE_GRASS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CAVE_GRASS_BLADES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CAVE_MOSS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_COPPER_IRIS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.COPPER_IRIS_PETALS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CRIMSON_MIDDLE_GEM.create(1)), new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM, 1));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_DEEP_WATER_CORAL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.DEEP_WATER_CORAL_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_DRIED_SWAMP_REED.create(1)), (EnumItemMisc.DRIED_SWAMP_REED.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_FLATHEAD_MUSHROOM.create(1)), new ItemStack(ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_GOLDEN_CLUB.create(1)), (ItemPlantDrop.EnumItemPlantDrop.GOLDEN_CLUB_FLOWER_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_GREEN_MIDDLE_GEM.create(1)), new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM, 1));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_LICHEN.create(1)), (ItemPlantDrop.EnumItemPlantDrop.LICHEN_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MARSH_HIBISCUS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MARSH_HIBISCUS_FLOWER.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MARSH_MALLOW.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MARSH_MALLOW_FLOWER.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MARSH_MARIGOLD.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MARSH_MARIGOLD_FLOWER_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MILKWEED.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MILKWEED_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MIRE_CORAL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MIRE_CORAL_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MIRE_SNAIL_SHELL.create(1)), (EnumItemMisc.MIRE_SNAIL_SHELL.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MOSS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MOSS_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_NETTLE.create(1)), (ItemPlantDrop.EnumItemPlantDrop.NETTLE_LEAF.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_PHRAGMITES.create(1)), (ItemPlantDrop.EnumItemPlantDrop.PHRAGMITE_STEMS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_PICKEREL_WEED.create(1)), (ItemPlantDrop.EnumItemPlantDrop.PICKEREL_WEED_FLOWER.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SHOOTS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SHOOT_LEAVES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SLUDGECREEP.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SLUDGECREEP_LEAVES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SOFT_RUSH.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SOFT_RUSH_LEAVES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SUNDEW.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SUNDEW_HEAD.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SWAMP_KELP.create(1)), new ItemStack(ItemRegistry.SWAMP_KELP_ITEM));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SWAMP_GRASS_TALL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_TANGLED_ROOTS.create(1)), new ItemStack(ItemRegistry.TANGLED_ROOT));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_WEEDWOOD_BARK.create(1)), new ItemStack(BlockRegistry.LOG_WEEDWOOD));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_WEEDWOOD_BARK.create(1)), EnumItemMisc.DRY_BARK.create(1));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_HANGER.create(1)), (ItemPlantDrop.EnumItemPlantDrop.HANGER_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_WATER_WEEDS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.WATER_WEEDS_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_VENUS_FLY_TRAP.create(1)), (ItemPlantDrop.EnumItemPlantDrop.VENUS_FLY_TRAP_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_VOLARPAD.create(1)), (ItemPlantDrop.EnumItemPlantDrop.VOLARPAD_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_THORNS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.THORNS_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_POISON_IVY.create(1)), (ItemPlantDrop.EnumItemPlantDrop.POISON_IVY_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_PITCHER_PLANT.create(1)), (ItemPlantDrop.EnumItemPlantDrop.PITCHER_PLANT_TRAP.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_GENERIC_LEAF.create(1)), (ItemPlantDrop.EnumItemPlantDrop.GENERIC_LEAF.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLADDERWORT_FLOWER.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BLADDERWORT_FLOWER_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLADDERWORT_STALK.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BLADDERWORT_STALK_ITEM.create(1)));
	}

	private static void registerAnimatorRecipes() {
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.SCROLL.create(1), 16, 16, LootTableRegistry.ANIMATOR_SCROLL) {
			@Override
			public ItemStack onAnimated(World world, BlockPos pos, ItemStack stack) {
				LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(LootTableRegistry.ANIMATOR_SCROLL);
				LootContext.Builder lootBuilder = (new LootContext.Builder((WorldServer) world));
				List<ItemStack> loot = lootTable.generateLootForPools(world.rand, lootBuilder.build());
				if(!loot.isEmpty()) {
					return loot.get(world.rand.nextInt(loot.size()));
				}
				return null;
			}
		});
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.TAR_BEAST_HEART.create(1), 32, 32, EnumItemMisc.TAR_BEAST_HEART_ANIMATED.create(1)).setRenderEntity(new ResourceLocation("thebetweenlands:tarminion")));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.INANIMATE_TARMINION.create(1), 8, 8, new ItemStack(ItemRegistry.TARMINION)).setRenderEntity(new ResourceLocation("thebetweenlands:tarminion")));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.TEST_ITEM), 2, 1) {
			@Override
			public boolean onRetrieved(World world, BlockPos pos, ItemStack stack) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof TileEntityAnimator) {
					TileEntityAnimator animator = (TileEntityAnimator) te;
					EntityItem entityitem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, new ItemStack(ItemRegistry.TEST_ITEM));
					entityitem.motionX = 0;
					entityitem.motionZ = 0;
					entityitem.motionY = 0.11000000298023224D;
					world.spawnEntity(entityitem);
					animator.setInventorySlotContents(0, null);
					return false;
				}
				return true;
			}
		});
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.SPORES), 8, 4, EntitySporeling.class).setRenderEntity(new ResourceLocation("thebetweenlands:sporeling")));
	}

	private static void registerPurifierRecipes() {
		PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_1.getMetadata()));
		PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_2.getMetadata()));
		PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_ORE));
		PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE));
		PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_ORE));
		PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.PURIFIED_SWAMP_DIRT), new ItemStack(BlockRegistry.SWAMP_DIRT));
		//TODO add vials
		//PurifierRecipe.addRecipe(ItemRegistry.dentrothystVial.createStack(0), ItemRegistry.dentrothystVial.createStack(1));
		PurifierRecipe.addRecipe(new IPurifierRecipe() {
			@Override
			public boolean matchesInput(ItemStack stack) {
				return stack != null && stack.getItem() == ItemRegistry.WEEDWOOD_ROWBOAT && EntityWeedwoodRowboat.isTarred(stack);
			}

			@Override
			public ItemStack getOutput(ItemStack input) {
				ItemStack output = input.copy();
				NBTTagCompound compound = output.getTagCompound();
				NBTTagCompound attrs = compound.getCompoundTag("attributes");
				attrs.removeTag("isTarred");
				if (attrs.hasNoTags()) {
					compound.removeTag("attributes");
				}
				if (compound.hasNoTags()) {
					output.setTagCompound(null);	
				}
				return output;
			}
		});
	}
}
