package thebetweenlands.common.registries;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemSwampTalisman.EnumTalisman;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.recipe.animator.ToolRepairAnimatorRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.recipe.misc.BookMergeRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.recipe.misc.HearthgroveTarringRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.recipe.misc.RecipeLurkerSkinPouchUpgrades;
import thebetweenlands.common.recipe.misc.RecipeMarshRunnerBoots;
import thebetweenlands.common.recipe.misc.RecipeMummyBait;
import thebetweenlands.common.recipe.misc.RecipesAspectVials;
import thebetweenlands.common.recipe.misc.RecipesCircleGems;
import thebetweenlands.common.recipe.misc.RecipesCoating;
import thebetweenlands.common.recipe.misc.RecipesLifeCrystal;
import thebetweenlands.common.recipe.misc.RecipesPlantTonic;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.util.config.ConfigHandler;

public class RecipeRegistry {

	public static final ResourceLocation CIRCLE_GEMS = new ResourceLocation(ModInfo.ID, "recipe_circle_gems");
	public static final ResourceLocation COATING = new ResourceLocation(ModInfo.ID, "recipe_coating");
	public static final ResourceLocation LIFE_CRYSTAL = new ResourceLocation(ModInfo.ID, "recipe_life_crystal");
	public static final ResourceLocation PLANT_TONIC = new ResourceLocation(ModInfo.ID, "recipe_plant_tonic");
	public static final ResourceLocation LURKER_POUCH = new ResourceLocation(ModInfo.ID, "recipe_lurker_skin_pouch_upgrade");
	public static final ResourceLocation MUMMY_BAIT = new ResourceLocation(ModInfo.ID, "recipe_mummy_bait");
	public static final ResourceLocation ASPECT_VIAL = new ResourceLocation(ModInfo.ID, "recipe_aspect_vial");
	public static final ResourceLocation RUNNER_BOOTS = new ResourceLocation(ModInfo.ID, "recipe_marsh_runner_boots");
	public static final ResourceLocation BOOK_MERGE = new ResourceLocation(ModInfo.ID, "recipe_book_merge");
	public static final ResourceLocation HEARTHGROVE_LOG_TARRING = new ResourceLocation(ModInfo.ID, "hearthgrove_log_tarring");
	
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
		ElixirRecipes.init();
		
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

		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.SWAMP_DIRT));
		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.COARSE_SWAMP_DIRT));

		OreDictionary.registerOre("grass", new ItemStack(BlockRegistry.SWAMP_GRASS));

		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_WEEDWOOD_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SAP_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_RUBBER_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_HEARTHGROVE_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_NIBBLETWIG_TREE));

		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_WEEDWOOD));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_SAP));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_RUBBER));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_HEARTHGROVE));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_NIBBLETWIG));

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

		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.WEEDWOOD));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_WEEDWOOD));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_SAP));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_RUBBER));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.GIANT_ROOT));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_HEARTHGROVE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_NIBBLETWIG));

		//Recipes that use and creates these conflicts with vanilla recipes because of ore dict
//		OreDictionary.registerOre("stickWood", EnumItemMisc.WEEDWOOD_STICK.create(1));
//		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));

		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANKS));

		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_SLAB));

		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_FENCE));

		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_FENCE_GATE));

		OreDictionary.registerOre("torch", new ItemStack(BlockRegistry.SULFUR_TORCH));

		OreDictionary.registerOre("bone", EnumItemMisc.SLIMY_BONE.create(1));

		//OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.BETWEENSTONE));
		OreDictionary.registerOre("stone", new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));

		OreDictionary.registerOre("sand", new ItemStack(BlockRegistry.SILT));

		OreDictionary.registerOre("workbench", new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH));

		OreDictionary.registerOre("chest", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));
		OreDictionary.registerOre("chestWood", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));

		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.POISON_IVY));
		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.THORNS));

		OreDictionary.registerOre("sugarcane", new ItemStack(ItemRegistry.SWAMP_REED_ITEM));
		
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

		//TODO Recipe
//		RecipeSorter.register("thebetweenlands:recipesAspectrusSeeds", RecipesAspectrusSeeds.class, SHAPELESS, "after:minecraft:shapeless");
//		RecipeHelper.addRecipe(new RecipesAspectrusSeeds());

		//TODO: Volarkite
		//RecipeHelper.addRecipe(new ItemStack(ItemRegistry.volarkite), "VVV", "RxR", " x ", 'x',  EnumItemMisc.WEEDWOOD_STICK), 'R', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE), 'V', ItemGenericPlantDrop.createStack(EnumItemPlantDrop.VOLARPAD));
		//RecipeHelper.addRecipe(new ItemStack(ItemRegistry.volarkite), "VVV", "RxR", " x ", 'x',  EnumItemMisc.WEEDWOOD_STICK), 'R', ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE), 'V', new ItemStack(BlockRegistry.volarpad));

		registry.register(new RecipesCircleGems().setRegistryName(CIRCLE_GEMS));
		registry.register(new RecipesCoating().setRegistryName(COATING));
		registry.register(new RecipesLifeCrystal().setRegistryName(LIFE_CRYSTAL));
		registry.register(new RecipesPlantTonic().setRegistryName(PLANT_TONIC));
		registry.register(new RecipeLurkerSkinPouchUpgrades().setRegistryName(LURKER_POUCH));
		registry.register(new RecipeMummyBait().setRegistryName(MUMMY_BAIT));
		registry.register(new RecipesAspectVials().setRegistryName(ASPECT_VIAL));
		registry.register(new RecipeMarshRunnerBoots().setRegistryName(RUNNER_BOOTS));
		registry.register(new BookMergeRecipe().setRegistryName(BOOK_MERGE));
		registry.register(new HearthgroveTarringRecipe().setRegistryName(HEARTHGROVE_LOG_TARRING));
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
		GameRegistry.addSmelting(new ItemStack(BlockRegistry.LIMESTONE_BRICKS), new ItemStack(BlockRegistry.CRACKED_LIMESTONE_BRICKS), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.BL_BUCKET_RUBBER, 1, 0), EnumItemMisc.RUBBER_BALL.create(1), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.BL_BUCKET_RUBBER, 1, 1), new ItemStack(ItemRegistry.SYRMORITE_BUCKET_SOLID_RUBBER), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.MIRE_SNAIL_EGG), new ItemStack(ItemRegistry.MIRE_SNAIL_EGG_COOKED), 0.4F);
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
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_HEARTHGROVE));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_NIBBLETWIG));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_RUBBER_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_SAP_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_WEEDWOOD_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_HEARTHGROVE_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_NIBBLETWIG_TREE));
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
		if (ConfigHandler.debug) {
			AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.TEST_ITEM), 2, 1) {
				@Override
				public boolean onRetrieved(World world, BlockPos pos, ItemStack stack) {
					TileEntity te = world.getTileEntity(pos);
					if (te instanceof TileEntityAnimator) {
						TileEntityAnimator animator = (TileEntityAnimator) te;
						EntityItem entityitem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, new ItemStack(ItemRegistry.TEST_ITEM));
						entityitem.motionX = 0;
						entityitem.motionZ = 0;
						entityitem.motionY = 0.11000000298023224D;
						world.spawnEntity(entityitem);
						animator.setInventorySlotContents(0, ItemStack.EMPTY);
						return false;
					}
					return true;
				}
			});
		}
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.SPORES), 8, 4, EntitySporeling.class).setRenderEntity(new ResourceLocation("thebetweenlands:sporeling")));
	
		for(Item item : ItemRegistry.ITEMS) {
			if(item instanceof IAnimatorRepairable) {
				AnimatorRecipe.addRecipe(new ToolRepairAnimatorRecipe((IAnimatorRepairable)item));
			}
		}
	}

	private static void registerPurifierRecipes() {
		PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_1.getMetadata()));
		PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_2.getMetadata()));
		PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_ORE));
		PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE));
		PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_ORE));
		PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.PURIFIED_SWAMP_DIRT), new ItemStack(BlockRegistry.SWAMP_DIRT));
		PurifierRecipe.addRecipe(ItemRegistry.DENTROTHYST_VIAL.createStack(0), ItemRegistry.DENTROTHYST_VIAL.createStack(1));
		PurifierRecipe.addRecipe(new IPurifierRecipe() {
			@Override
			public boolean matchesInput(ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() == ItemRegistry.WEEDWOOD_ROWBOAT && EntityWeedwoodRowboat.isTarred(stack);
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
