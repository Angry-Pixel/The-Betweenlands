package thebetweenlands.common.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.mobs.EntityRootSprite;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemSwampTalisman.EnumTalisman;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.recipe.ShapelessOverrideDummyRecipe;
import thebetweenlands.common.recipe.animator.ToolRepairAnimatorRecipe;
import thebetweenlands.common.recipe.censer.AbstractCenserRecipe;
import thebetweenlands.common.recipe.censer.CenserRecipeAspect;
import thebetweenlands.common.recipe.censer.CenserRecipeCremains;
import thebetweenlands.common.recipe.censer.CenserRecipeDungeonFog;
import thebetweenlands.common.recipe.censer.CenserRecipeElixir;
import thebetweenlands.common.recipe.censer.CenserRecipePlantTonic;
import thebetweenlands.common.recipe.censer.CenserRecipeSapBall;
import thebetweenlands.common.recipe.censer.CenserRecipeStagnantWater;
import thebetweenlands.common.recipe.censer.CenserRecipeSwampWater;
import thebetweenlands.common.recipe.censer.CenserRecipeWeepingBluePetal;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.recipe.misc.BookMergeRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.recipe.misc.HearthgroveTarringRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.recipe.misc.RecipeClearBoneWayfinder;
import thebetweenlands.common.recipe.misc.RecipeGrapplingHookUpgrades;
import thebetweenlands.common.recipe.misc.RecipeLurkerSkinPouchUpgrades;
import thebetweenlands.common.recipe.misc.RecipeMarshRunnerBoots;
import thebetweenlands.common.recipe.misc.RecipeMummyBait;
import thebetweenlands.common.recipe.misc.RecipeSapSpitCleanTool;
import thebetweenlands.common.recipe.misc.RecipesAspectVials;
import thebetweenlands.common.recipe.misc.RecipesCircleGems;
import thebetweenlands.common.recipe.misc.RecipesCoating;
import thebetweenlands.common.recipe.misc.RecipesLifeCrystal;
import thebetweenlands.common.recipe.misc.RecipesPlantTonic;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;

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
	public static final ResourceLocation CLEAR_BONE_WAYFINDER = new ResourceLocation(ModInfo.ID, "clear_bone_wayfinder");
	public static final ResourceLocation SAP_SPIT_CLEAN_TOOL = new ResourceLocation(ModInfo.ID, "sap_spit_clean_tool");
	public static final ResourceLocation GRAPPLING_HOOK_UPGRADE = new ResourceLocation(ModInfo.ID, "grappling_hook_upgrade");
	
	private RecipeRegistry() { }

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistry<IRecipe> registry = event.getRegistry();

		registerSmelting();
		registerDynamicRecipes(registry);
		registerPurifierRecipes();
		registerPestleAndMortarRecipes();
		registerCompostRecipes();
		registerDruidAltarRecipes();
		registerAnimatorRecipes();
		registerCenserRecipes();

		ElixirRecipes.init();

		CustomRecipeRegistry.loadCustomRecipes();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerRecipesLast(RegistryEvent.Register<IRecipe> event) {
		overrideConflictingRecipes(event.getRegistry());
	}
	
	private static void overrideConflictingRecipes(IForgeRegistry<IRecipe> registry) {
		boolean vanilla = BetweenlandsConfig.GENERAL.overrideConflictingVanillaRecipes;
		boolean any = BetweenlandsConfig.GENERAL.overrideAnyConflictingRecipes;
		if(vanilla || any) {
			if(BetweenlandsConfig.DEBUG.debugRecipeOverrides) TheBetweenlands.logger.info("Searching recipe conflicts:");

			List<IRecipe> blRecipes = new ArrayList<>();
			List<IRecipe> otherRecipes = new ArrayList<>();

			for(IRecipe recipe : registry) {
				if(ModInfo.ID.equals(recipe.getRegistryName().getNamespace())) {
					blRecipes.add(recipe);
				} else {
					otherRecipes.add(recipe);
				}
			}

			Multimap<IRecipe, ResourceLocation> conflictingRecipes = HashMultimap.create();

			for(IRecipe otherRecipe : otherRecipes) {
				if(!otherRecipe.isDynamic() && (any || (vanilla && "minecraft".equals(otherRecipe.getRegistryName().getNamespace())))) {
					NonNullList<Ingredient> otherIngredients = otherRecipe.getIngredients();
					for(IRecipe blRecipe : blRecipes) {
						if(!blRecipe.isDynamic()) {
							NonNullList<Ingredient> blIngredients = blRecipe.getIngredients();
							if(blIngredients.size() == otherIngredients.size()) {
								IShapedRecipe blRecipeShaped = blRecipe instanceof IShapedRecipe ? (IShapedRecipe) blRecipe : null;
								IShapedRecipe otherRecipeShaped = otherRecipe instanceof IShapedRecipe ? (IShapedRecipe) otherRecipe : null;
								boolean canBlRecipeFit = blRecipeShaped == null || otherRecipeShaped == null || /*shape does not matter, so only compare the ingredients*/
										(blRecipeShaped.getRecipeWidth() >= otherRecipeShaped.getRecipeWidth() && blIngredients.size() <= otherRecipeShaped.getRecipeWidth()) /*BL recipe fits in one row*/ ||
										(blRecipeShaped.getRecipeWidth() == otherRecipeShaped.getRecipeWidth() && blRecipeShaped.getRecipeHeight() <= otherRecipeShaped.getRecipeHeight()) /*BL recipe fits shape*/;
								if(canBlRecipeFit) {
									boolean hasConflict = true;
									for(int i = 0; i < blIngredients.size(); i++) {
										Ingredient blIngredient = blIngredients.get(i);
										Ingredient otherIngredient = otherIngredients.get(i);
										if(blIngredient.getMatchingStacks().length == 0 && otherIngredient.getMatchingStacks().length == 0) {
											continue;
										}
										boolean hasSlotMatch = false;
										for(ItemStack stack : blIngredient.getMatchingStacks()) {
											if(otherIngredient.apply(stack)) {
												hasSlotMatch = true;
												break;
											}
										}
										if(!hasSlotMatch) {
											hasConflict = false;
											break;
										}
									}
									if(hasConflict) {
										if(BetweenlandsConfig.DEBUG.debugRecipeOverrides) TheBetweenlands.logger.info(blRecipe.getRegistryName() + " " + otherRecipe.getRegistryName());
										conflictingRecipes.put(blRecipe, otherRecipe.getRegistryName());
									}
								}
							}
						}
					}
				}
			}

			if(BetweenlandsConfig.DEBUG.debugRecipeOverrides) TheBetweenlands.logger.info("Replacing conflicting recipes:");

			for(Entry<IRecipe, ResourceLocation> entry : conflictingRecipes.entries()) {
				IRecipe blRecipe = entry.getKey();
				IRecipe otherRecipe = registry.getValue(entry.getValue());

				IRecipe overrideDummy;
				if(otherRecipe instanceof IShapedRecipe) {
					overrideDummy = new ShapelessOverrideDummyRecipe.ShapedOverrideDummyRecipe(blRecipe, (IShapedRecipe) otherRecipe);
				} else {
					overrideDummy = new ShapelessOverrideDummyRecipe(blRecipe, otherRecipe);
				}
				overrideDummy.setRegistryName(otherRecipe.getRegistryName());

				registry.register(overrideDummy);

				if(BetweenlandsConfig.DEBUG.debugRecipeOverrides) TheBetweenlands.logger.info(blRecipe.getRegistryName() + " " + otherRecipe.getRegistryName() + (registry.getValue(entry.getValue()) != overrideDummy ? " FAILED" : ""));
			}
		}
	}

	private static void registerDynamicRecipes(IForgeRegistry<IRecipe> registry) {
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
		registry.register(new RecipeClearBoneWayfinder().setRegistryName(CLEAR_BONE_WAYFINDER));
		registry.register(new RecipeSapSpitCleanTool().setRegistryName(SAP_SPIT_CLEAN_TOOL));
		registry.register(new RecipeGrapplingHookUpgrades().setRegistryName(GRAPPLING_HOOK_UPGRADE));
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
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.BL_BUCKET_RUBBER, 1, 0), EnumItemMisc.RUBBER_BALL.create(3), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.BL_BUCKET_RUBBER, 1, 1), new ItemStack(ItemRegistry.SYRMORITE_BUCKET_SOLID_RUBBER), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ItemRegistry.MIRE_SNAIL_EGG), new ItemStack(ItemRegistry.MIRE_SNAIL_EGG_COOKED), 0.4F);
		GameRegistry.addSmelting(BlockRegistry.MOSS, new ItemStack(BlockRegistry.DEAD_MOSS), 0.1F);
		GameRegistry.addSmelting(BlockRegistry.LICHEN, new ItemStack(BlockRegistry.DEAD_LICHEN), 0.1F);
		GameRegistry.addSmelting(BlockRegistry.WEEDWOOD_BUSH, new ItemStack(BlockRegistry.DEAD_WEEDWOOD_BUSH), 0.1F);
		//smelt to nuggets
		GameRegistry.addSmelting(ItemRegistry.VALONITE_AXE, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.VALONITE_PICKAXE, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.VALONITE_SHOVEL, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.VALONITE_SWORD, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.VALONITE_HELMET, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.VALONITE_CHESTPLATE, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.VALONITE_LEGGINGS, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.VALONITE_BOOTS, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.OCTINE_AXE, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.OCTINE_PICKAXE, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.OCTINE_SHOVEL, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.OCTINE_SWORD, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.SYRMORITE_HELMET, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.SYRMORITE_CHESTPLATE, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.SYRMORITE_LEGGINGS, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()), 0.1F);
		GameRegistry.addSmelting(ItemRegistry.SYRMORITE_BOOTS, new ItemStack(ItemRegistry.ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()), 0.1F);
	}

	private static void registerDruidAltarRecipes() {
		DruidAltarRecipe.addRecipe(EnumTalisman.SWAMP_TALISMAN_1.create(1), EnumTalisman.SWAMP_TALISMAN_2.create(1), EnumTalisman.SWAMP_TALISMAN_3.create(1), EnumTalisman.SWAMP_TALISMAN_4.create(1), EnumTalisman.SWAMP_TALISMAN_0.create(1));
		
		// Add reactivation recipe
		DruidAltarRecipe.addRecipe(new IDruidAltarRecipe() {
			@Override
			public boolean containsInputItem(ItemStack input) {
				if(input.isEmpty()) {
					return false;
				}
				int[] ids = OreDictionary.getOreIDs(input);
				for (int id: ids) {
					if ("treeSapling".equals(OreDictionary.getOreName(id))) {
						return true;
					}
				}
				return false;
			}

			@Override
			public boolean matchesInput(ItemStack[] input) {
				for(ItemStack stack : input) {
					if(!this.containsInputItem(stack)) {
						return false;
					}
				}
				return true;
			}

			@Override
			public ItemStack getOutput(ItemStack[] input) {
				return ItemStack.EMPTY;
			}
			
			@Override
			public void onCrafted(World world, BlockPos pos, ItemStack[] input, ItemStack output) {
				BlockPos spawnerPos = pos.down();
				if (world.getBlockState(spawnerPos).getBlockHardness(world, spawnerPos) >= 0.0F) {
					world.setBlockState(spawnerPos, BlockRegistry.MOB_SPAWNER.getDefaultState());
					TileEntity te = world.getTileEntity(spawnerPos);
					if(te instanceof TileEntityMobSpawnerBetweenlands) {
						MobSpawnerLogicBetweenlands logic = ((TileEntityMobSpawnerBetweenlands)te).getSpawnerLogic();
						logic.setNextEntityName("thebetweenlands:dark_druid").setCheckRange(32.0D).setSpawnRange(6).setSpawnInAir(false).setMaxEntities(1 + world.rand.nextInt(3));
					}
					
					world.playSound(null, spawnerPos, SoundRegistry.DRUID_TELEPORT, SoundCategory.BLOCKS, 1, 1);
					
					// Block break effect, see RenderGlobal#playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
					world.playEvent(2001, spawnerPos.up(4), Block.getStateId(Blocks.SAPLING.getDefaultState()));
					world.playEvent(2003, spawnerPos.up(4), 0);
				}
			}
		});
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
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.EDGE_LEAF));
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.EDGE_MOSS));
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.EDGE_SHROOM));
		CompostRecipe.addRecipe(4, 6000, Item.getItemFromBlock(BlockRegistry.SWAMP_PLANT));
		CompostRecipe.addRecipe(12, 10000, Item.getItemFromBlock(BlockRegistry.VENUS_FLY_TRAP));
		CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.VOLARPAD));
		CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.WEEDWOOD_BUSH));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.THORNS));
		CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.POISON_IVY));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.MOSS));
		CompostRecipe.addRecipe(6, 3000, Item.getItemFromBlock(BlockRegistry.DEAD_MOSS));
		CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.LICHEN));
		CompostRecipe.addRecipe(6, 3000, Item.getItemFromBlock(BlockRegistry.DEAD_LICHEN));
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
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.TALL_SLUDGECREEP));
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.ROTBULB));
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.PALE_GRASS));
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.STRING_ROOTS));
		CompostRecipe.addRecipe(8, 6000, Item.getItemFromBlock(BlockRegistry.CRYPTWEED));
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
		CompostRecipe.addRecipe(25, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_SPIRIT_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_RUBBER_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_SAP_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_WEEDWOOD_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_HEARTHGROVE_TREE));
		CompostRecipe.addRecipe(4, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_NIBBLETWIG_TREE));
		CompostRecipe.addRecipe(6, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_SPIRIT_TREE_BOTTOM));
		CompostRecipe.addRecipe(6, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE));
		CompostRecipe.addRecipe(6, 11000, Item.getItemFromBlock(BlockRegistry.LEAVES_SPIRIT_TREE_TOP));
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
		CompostRecipe.addRecipe(10, 6000, ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS);
		CompostRecipe.addRecipe(10, 6000, ItemRegistry.SPORES);
		CompostRecipe.addRecipe(10, 6000, ItemRegistry.ASPECTRUS_SEEDS);

		for (ItemCrushed.EnumItemCrushed type : ItemCrushed.EnumItemCrushed.values()) {
			CompostRecipe.addRecipe(3, 4000, new ItemStack(ItemRegistry.ITEMS_CRUSHED, 1, type.getID()));
		}

		for (ItemPlantDrop.EnumItemPlantDrop type : ItemPlantDrop.EnumItemPlantDrop.values()) {
			CompostRecipe.addRecipe(3, 4000, new ItemStack(ItemRegistry.ITEMS_PLANT_DROP, 1, type.getID()));
		}
	}

	private static void registerPestleAndMortarRecipes() {
		PestleAndMortarRecipe.addRecipe((EnumItemMisc.LIMESTONE_FLUX.create(3)), new ItemStack(BlockRegistry.LIMESTONE));
		PestleAndMortarRecipe.addRecipe((EnumItemMisc.LIMESTONE_FLUX.create(3)), new ItemStack(BlockRegistry.POLISHED_LIMESTONE));
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
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ROOTS.create(1)), new ItemStack(ItemRegistry.TANGLED_ROOT));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ROOTS.create(1)), new ItemStack(BlockRegistry.GIANT_ROOT));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_WEEDWOOD_BARK.create(1)), new ItemStack(BlockRegistry.LOG_WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE));
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
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_EDGE_SHROOM.create(1)), (ItemPlantDrop.EnumItemPlantDrop.EDGE_SHROOM_GILLS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_EDGE_MOSS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.EDGE_MOSS_CLUMP.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_EDGE_LEAF.create(1)), (ItemPlantDrop.EnumItemPlantDrop.EDGE_LEAF_ITEM.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ROTBULB.create(1)), (ItemPlantDrop.EnumItemPlantDrop.ROTBULB_STALK.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_PALE_GRASS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.PALE_GRASS_BLADES.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_STRING_ROOTS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.STRING_ROOT_FIBERS.create(1)));
		PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CRYPTWEED.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CRYPTWEED_BLADES.create(1)));
		
		//Loot scraps
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.SKULL_MASK));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.WIGHTS_BANE));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.SLUDGE_SLICER));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.CRITTER_CRUNCHER));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.HAG_HACKER));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.VOODOO_DOLL));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.SWIFT_PICK));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.MAGIC_ITEM_MAGNET));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.RING_OF_DISPERSION));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.RING_OF_FLIGHT));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.RING_OF_POWER));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.RING_OF_RECRUITMENT));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.RING_OF_SUMMONING));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.RING_OF_GATHERING));
		PestleAndMortarRecipe.addRecipe(ItemMisc.EnumItemMisc.LOOT_SCRAPS.create(1), new ItemStack(ItemRegistry.GEM_SINGER));
	}

	private static void registerAnimatorRecipes() {
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.SCROLL.create(1), 16, 16, LootTableRegistry.SCROLL) {
			@Override
			public ItemStack onAnimated(World world, BlockPos pos, ItemStack stack) {
				LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(LootTableRegistry.SCROLL);
				LootContext.Builder lootBuilder = (new LootContext.Builder((WorldServer) world));
				List<ItemStack> loot = lootTable.generateLootForPools(world.rand, lootBuilder.build());
				if(!loot.isEmpty()) {
					return loot.get(world.rand.nextInt(loot.size()));
				}
				return ItemStack.EMPTY;
			}
		});
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.FABRICATED_SCROLL.create(1), 16, 16, LootTableRegistry.FABRICATED_SCROLL) {
			@Override
			public ItemStack onAnimated(World world, BlockPos pos, ItemStack stack) {
				LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(LootTableRegistry.FABRICATED_SCROLL);
				LootContext.Builder lootBuilder = (new LootContext.Builder((WorldServer) world));
				List<ItemStack> loot = lootTable.generateLootForPools(world.rand, lootBuilder.build());
				if(!loot.isEmpty()) {
					return loot.get(world.rand.nextInt(loot.size()));
				}
				return ItemStack.EMPTY;
			}
		});
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.TAR_BEAST_HEART.create(1), 32, 32, EnumItemMisc.TAR_BEAST_HEART_ANIMATED.create(1)));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.INANIMATE_TARMINION.create(1), 8, 8, new ItemStack(ItemRegistry.TARMINION)).setRenderEntity(new ResourceLocation("thebetweenlands:tarminion")));
		if (BetweenlandsConfig.DEBUG.debug) {
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
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(BlockRegistry.ROOT_POD), 10, 6, EntityRootSprite.class).setRenderEntity(new ResourceLocation("thebetweenlands:root_sprite")));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.SPIRIT_TREE_FACE_SMALL_MASK), 24, 24, new ItemStack(ItemRegistry.SPIRIT_TREE_FACE_SMALL_MASK_ANIMATED)));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.INANIMATE_ANGRY_PEBBLE.create(1), 1, 1, new ItemStack(ItemRegistry.ANGRY_PEBBLE)));
		AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.SLUDGE_WORM_EGG_SAC), 6, 3, new ItemStack(ItemRegistry.SLUDGE_WORM_ARROW)));
		
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
				if (attrs.isEmpty()) {
					compound.removeTag("attributes");
				}
				if (compound.isEmpty()) {
					output.setTagCompound(null);	
				}
				return output;
			}
		});
	}
	
	private static void registerCenserRecipes() {
		AbstractCenserRecipe.addRecipe(new CenserRecipeDungeonFog());
		AbstractCenserRecipe.addRecipe(new CenserRecipeSapBall());
		AbstractCenserRecipe.addRecipe(new CenserRecipeWeepingBluePetal());
		AbstractCenserRecipe.addRecipe(new CenserRecipeStagnantWater());
		AbstractCenserRecipe.addRecipe(new CenserRecipePlantTonic());
		AbstractCenserRecipe.addRecipe(new CenserRecipeElixir());
		AbstractCenserRecipe.addRecipe(new CenserRecipeAspect());
		AbstractCenserRecipe.addRecipe(new CenserRecipeCremains());
		AbstractCenserRecipe.addRecipe(new CenserRecipeSwampWater());
	}
}
