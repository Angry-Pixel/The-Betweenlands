package thebetweenlands.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.function.Function;

public class BLItemModelProvider extends ItemModelProvider {
	public BLItemModelProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, TheBetweenlands.ID, helper);
	}

	@Override
	protected void registerModels() {

		for (DeferredHolder<Item, ?> item : EntityRegistry.SPAWN_EGGS.getEntries()) {
			if (item.get() instanceof SpawnEggItem) {
				this.getBuilder(item.getId().getPath()).parent(this.getExistingFile(ResourceLocation.withDefaultNamespace("item/template_spawn_egg")));
			}
		}
		this.vanillaBucket(ItemRegistry.SWAMP_WATER_BUCKET);
		this.vanillaBucket(ItemRegistry.STAGNANT_WATER_BUCKET);
		this.vanillaBucket(ItemRegistry.TAR_BUCKET);
		this.vanillaBucket(ItemRegistry.RUBBER_BUCKET);
		this.vanillaBucket(ItemRegistry.CLEAN_WATER_BUCKET);
		this.vanillaBucket(ItemRegistry.FISH_OIL_BUCKET);

		this.vanillaBucket(ItemRegistry.DULL_LAVENDER_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.MAROON_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.SHADOW_GREEN_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.CAMELOT_MAGENTA_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.SAFFRON_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.CARIBBEAN_GREEN_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.VIVID_TANGERINE_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.CHAMPAGNE_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.RAISIN_BLACK_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.SUSHI_GREEN_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.ELM_CYAN_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.CADMIUM_GREEN_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.LAVENDER_BLUE_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.BROWN_RUST_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.MIDNIGHT_PURPLE_DYE_BUCKET);
		this.vanillaBucket(ItemRegistry.PEWTER_GREY_DYE_BUCKET);

		this.vanillaBucket(ItemRegistry.NETTLE_SOUP_BUCKET);
		this.vanillaBucket(ItemRegistry.NETTLE_TEA_BUCKET);
		this.vanillaBucket(ItemRegistry.PHEROMONE_EXTRACT_BUCKET);
		this.vanillaBucket(ItemRegistry.SWAMP_BROTH_BUCKET);
		this.vanillaBucket(ItemRegistry.STURDY_STOCK_BUCKET);
		this.vanillaBucket(ItemRegistry.PEAR_CORDIAL_BUCKET);
		this.vanillaBucket(ItemRegistry.SHAMANS_BREW_BUCKET);
		this.vanillaBucket(ItemRegistry.LAKE_BROTH_BUCKET);
		this.vanillaBucket(ItemRegistry.SHELL_STOCK_BUCKET);
		this.vanillaBucket(ItemRegistry.FROG_LEG_EXTRACT_BUCKET);
		this.vanillaBucket(ItemRegistry.WITCH_TEA_BUCKET);

		this.basicItem(ItemRegistry.ROTTEN_FOOD);
		this.basicItem(ItemRegistry.TAINTED_POTION);

		this.basicItem(ItemRegistry.CRIMSON_SNAIL_SHELL);
		this.basicItem(ItemRegistry.OCHRE_SNAIL_SHELL);
		this.basicItem(ItemRegistry.COMPOST);
		this.basicItem(ItemRegistry.DRAGONFLY_WING);
		this.basicItem(ItemRegistry.LURKER_SKIN);
		this.basicItem(ItemRegistry.DRIED_SWAMP_REED);
		this.basicItem(ItemRegistry.REED_ROPE);
		this.basicItem(ItemRegistry.MUD_BRICK);
		this.basicItem(ItemRegistry.SYRMORITE_INGOT);
		this.basicItem(ItemRegistry.DRY_BARK);
		this.basicItem(ItemRegistry.SLIMY_BONE);
		this.basicItem(ItemRegistry.SNAPPER_ROOT);
		this.basicItem(ItemRegistry.STALKER_EYE);
		this.basicItem(ItemRegistry.SULFUR);
		this.basicItem(ItemRegistry.VALONITE_SHARD);
		this.basicItem(ItemRegistry.WEEDWOOD_STICK);
		this.basicItem(ItemRegistry.ANGLER_TOOTH);
		this.basicItem(ItemRegistry.WEEDWOOD_BOWL);
		this.basicItem(ItemRegistry.RUBBER_BALL);
		this.basicItem(ItemRegistry.TAR_BEAST_HEART);
		this.basicItem(ItemRegistry.ANIMATED_TAR_BEAST_HEART);
		this.basicItem(ItemRegistry.TAR_DRIP);
		this.basicItem(ItemRegistry.LIMESTONE_FLUX);
		this.basicItem(ItemRegistry.INANIMATE_TARMINION);
		this.basicItem(ItemRegistry.POISON_GLAND);
		this.basicItem(ItemRegistry.AMATE_PAPER);
		this.basicItem(ItemRegistry.SHOCKWAVE_SWORD_PIECE_1);
		this.basicItem(ItemRegistry.SHOCKWAVE_SWORD_PIECE_2);
		this.basicItem(ItemRegistry.SHOCKWAVE_SWORD_PIECE_3);
		this.basicItem(ItemRegistry.SHOCKWAVE_SWORD_PIECE_4);
		this.basicItem(ItemRegistry.AMULET_SOCKET);
		this.basicItem(ItemRegistry.SCABYST);
		this.basicItem(ItemRegistry.ITEM_SCROLL);
		this.basicItem(ItemRegistry.SYRMORITE_NUGGET);
		this.basicItem(ItemRegistry.OCTINE_NUGGET);
		this.basicItem(ItemRegistry.VALONITE_SPLINTER);
		this.basicItem(ItemRegistry.CREMAINS);
		this.basicItem(ItemRegistry.UNDYING_EMBERS);
		this.basicItem(ItemRegistry.INANIMATE_ANGRY_PEBBLE);
		this.basicItem(ItemRegistry.ANCIENT_REMNANT);
		this.basicItem(ItemRegistry.LOOT_SCRAPS);
		this.basicItem(ItemRegistry.FABRICATED_SCROLL);
		this.basicItem(ItemRegistry.BETWEENSTONE_PEBBLE);
		this.basicItem(ItemRegistry.ANADIA_SWIM_BLADDER);
		this.basicItem(ItemRegistry.ANADIA_EYE);
		this.basicItem(ItemRegistry.ANADIA_GILLS);
		this.basicItem(ItemRegistry.ANADIA_SCALES);
		this.basicItem(ItemRegistry.ANADIA_BONES);
		this.basicItem(ItemRegistry.ANADIA_REMAINS);
		this.basicItem(ItemRegistry.ANADIA_FINS);
		this.basicItem(ItemRegistry.SNOT);
		this.basicItem(ItemRegistry.URCHIN_SPIKE);
		this.basicItem(ItemRegistry.FISHING_FLOAT_AND_HOOK);
		this.basicItem(ItemRegistry.OLMLETTE_MIXTURE);
		this.basicItem(ItemRegistry.SILK_COCOON);
		this.basicItem(ItemRegistry.SILK_THREAD);
		this.basicItem(ItemRegistry.DIRTY_SILK_BUNDLE);
		this.basicItem(ItemRegistry.PHEROMONE_THORAXES);
		this.basicItem(ItemRegistry.SWAMP_TALISMAN);
		this.basicItem(ItemRegistry.SWAMP_TALISMAN_PIECE_1);
		this.basicItem(ItemRegistry.SWAMP_TALISMAN_PIECE_2);
		this.basicItem(ItemRegistry.SWAMP_TALISMAN_PIECE_3);
		this.basicItem(ItemRegistry.SWAMP_TALISMAN_PIECE_4);
		this.basicItem(ItemRegistry.ORANGE_DENTROTHYST_SHARD);
		this.basicItem(ItemRegistry.GREEN_DENTROTHYST_SHARD);
		this.basicItem(ItemRegistry.FISH_BAIT);
		this.basicItem(ItemRegistry.FUMIGANT);
		this.basicItem(ItemRegistry.SAP_BALL);
		this.basicItem(ItemRegistry.COOKED_MIRE_SNAIL_EGG);
		this.basicItem(ItemRegistry.RAW_FROG_LEGS);
		this.basicItem(ItemRegistry.COOKED_FROG_LEGS);
		this.basicItem(ItemRegistry.RAW_SNAIL_FLESH);
		this.basicItem(ItemRegistry.COOKED_SNAIL_FLESH);
		this.basicItem(ItemRegistry.REED_DONUT);
		this.basicItem(ItemRegistry.JAM_DONUT);
		this.basicItem(ItemRegistry.GERTS_DONUT);
		this.basicItem(ItemRegistry.PUFFSHROOM_TENDRIL);
		this.basicItem(ItemRegistry.KRAKEN_TENTACLE);
		this.basicItem(ItemRegistry.KRAKEN_CALAMARI);
		this.basicItem(ItemRegistry.MIDDLE_FRUIT);
		this.basicItem(ItemRegistry.MINCE_PIE);
		this.basicItem(ItemRegistry.CHRISTMAS_PUDDING);
		this.basicItem(ItemRegistry.CANDY_CANE);
		this.basicItem(ItemRegistry.WEEPING_BLUE_PETAL);
		this.basicItem(ItemRegistry.WIGHT_HEART);
		this.basicItem(ItemRegistry.YELLOW_DOTTED_FUNGUS);
		this.basicItem(ItemRegistry.SILT_CRAB_CLAW);
		this.basicItem(ItemRegistry.CRAB_STICK);
		this.basicItem(ItemRegistry.SLUDGE_JELLO);
		this.basicItem(ItemRegistry.MIDDLE_FRUIT_JELLO);
		this.basicItem(ItemRegistry.SAP_JELLO);
		this.basicItem(ItemRegistry.GREEN_MARSHMALLOW);
		this.basicItem(ItemRegistry.PINK_MARSHMALLOW);
		this.basicItem(ItemRegistry.FLATHEAD_MUSHROOM);
		this.basicItem(ItemRegistry.BLACK_HAT_MUSHROOM);
		this.basicItem(ItemRegistry.BULB_CAPPED_MUSHROOM);
		this.basicItem(ItemRegistry.FRIED_SWAMP_KELP);
		this.basicItem(ItemRegistry.FORBIDDEN_FIG);
		this.basicItem(ItemRegistry.BLUE_CANDY);
		this.basicItem(ItemRegistry.RED_CANDY);
		this.basicItem(ItemRegistry.YELLOW_CANDY);
		this.basicItem(ItemRegistry.CHIROMAW_WING);
		this.basicItem(ItemRegistry.TANGLED_ROOT);
		this.basicItem(ItemRegistry.MIRE_SCRAMBLE);
		this.basicItem(ItemRegistry.WEEPING_BLUE_PETAL_SALAD);
		this.basicItem(ItemRegistry.NIBBLESTICK);
		this.basicItem(ItemRegistry.SPIRIT_FRUIT);
		this.basicItem(ItemRegistry.SUSHI);
		this.basicItem(ItemRegistry.ROCK_SNOT_PEARL);
		this.basicItem(ItemRegistry.PEARLED_PEAR);
		this.basicItem(ItemRegistry.RAW_ANADIA_MEAT);
		this.basicItem(ItemRegistry.COOKED_ANADIA_MEAT);
		this.basicItem(ItemRegistry.SMOKED_ANADIA_MEAT);
		this.basicItem(ItemRegistry.BARNACLE);
		this.basicItem(ItemRegistry.COOKED_BARNACLE);
		this.basicItem(ItemRegistry.SMOKED_BARNACLE);
		this.basicItem(ItemRegistry.SMOKED_CRAB_STICK);
		this.basicItem(ItemRegistry.SMOKED_FROG_LEGS);
		this.basicItem(ItemRegistry.SMOKED_PUFFSHROOM_TENDRIL);
		this.basicItem(ItemRegistry.SMOKED_SILT_CRAB_CLAW);
		this.basicItem(ItemRegistry.SMOKED_SNAIL_FLESH);
		this.basicItem(ItemRegistry.RAW_OLM_EGG);
		this.basicItem(ItemRegistry.COOKED_OLM_EGG);
		this.basicItem(ItemRegistry.OLMLETTE);
		this.basicItem(ItemRegistry.SILK_GRUB);
		this.basicItem(ItemRegistry.NETTLE_SOUP);
		this.basicItem(ItemRegistry.NETTLE_TEA);
		this.basicItem(ItemRegistry.PHEROMONE_EXTRACT);
		this.basicItem(ItemRegistry.SWAMP_BROTH);
		this.basicItem(ItemRegistry.STURDY_STOCK);
		this.basicItem(ItemRegistry.PEAR_CORDIAL);
		this.basicItem(ItemRegistry.SHAMANS_BREW);
		this.basicItem(ItemRegistry.LAKE_BROTH);
		this.basicItem(ItemRegistry.SHELL_STOCK);
		this.basicItem(ItemRegistry.FROG_LEG_EXTRACT);
		this.basicItem(ItemRegistry.WITCH_TEA);
		this.basicItem(ItemRegistry.HERBLORE_BOOK);
		this.basicItem(ItemRegistry.CRIMSON_MIDDLE_GEM);
		this.basicItem(ItemRegistry.AQUA_MIDDLE_GEM);
		this.basicItem(ItemRegistry.GREEN_MIDDLE_GEM);
		this.basicItem(ItemRegistry.LIFE_CRYSTAL)
			.override().predicate(TheBetweenlands.prefix("remaining"), 1).model(this.basicItem(this.modLoc("life_crystal_1"))).end()
			.override().predicate(TheBetweenlands.prefix("remaining"), 2).model(this.basicItem(this.modLoc("life_crystal_2"))).end()
			.override().predicate(TheBetweenlands.prefix("remaining"), 3).model(this.basicItem(this.modLoc("life_crystal_3"))).end()
			.override().predicate(TheBetweenlands.prefix("remaining"), 4).model(this.basicItem(this.modLoc("life_crystal_4")));

		this.basicItem(ItemRegistry.LIFE_CRYSTAL_FRAGMENT)
			.override().predicate(TheBetweenlands.prefix("remaining"), 1).model(this.basicItem(this.modLoc("life_crystal_fragment_1"))).end()
			.override().predicate(TheBetweenlands.prefix("remaining"), 2).model(this.basicItem(this.modLoc("life_crystal_fragment_2"))).end()
			.override().predicate(TheBetweenlands.prefix("remaining"), 3).model(this.basicItem(this.modLoc("life_crystal_fragment_3"))).end()
			.override().predicate(TheBetweenlands.prefix("remaining"), 4).model(this.basicItem(this.modLoc("life_crystal_fragment_4")));
		this.basicItem(ItemRegistry.PYRAD_FLAME);
		this.basicItem(ItemRegistry.FIREFLY);
		this.basicItem(ItemRegistry.MIRE_SNAIL);
		this.basicItem(ItemRegistry.GECKO);
		this.basicItem(ItemRegistry.DRAGONFLY);
		this.basicItem(ItemRegistry.OLM);
		this.basicItem(ItemRegistry.TINY_SLUDGE_WORM);
		this.otherTextureItem(ItemRegistry.TINY_SLUDGE_WORM_HELPER, this.modLoc("tiny_sludge_worm"));
		this.basicItem(ItemRegistry.BUBBLER_CRAB);
		this.basicItem(ItemRegistry.SILT_CRAB);
		//critters
		this.basicItem(ItemRegistry.SHIMMER_STONE);
		//tarminion
		this.basicItem(ItemRegistry.SLUDGE_BALL);
		//rope
		Function<DeferredItem<?>, ModelFile> pebbleOverlay = item -> this.multiLayerItem(item.getId().getPath() + "_active", this.itemTexture(item), TheBetweenlands.prefix("item/angry_pebble_overlay"));
		this.basicItem(ItemRegistry.ANGRY_PEBBLE).override().predicate(TheBetweenlands.prefix("charging"), 1.0F).model(pebbleOverlay.apply(ItemRegistry.ANGRY_PEBBLE));
		this.basicItem(ItemRegistry.OCTINE_INGOT);
		this.basicItem(ItemRegistry.SAP_SPIT);
		this.basicItem(ItemRegistry.SHAMBLER_TONGUE);
		this.basicItem(ItemRegistry.RUNE_DOOR_KEY);
		this.basicItem(ItemRegistry.LURKER_SKIN_PATCH);
		this.basicItem(ItemRegistry.DRAETON_BALLOON);
		this.basicItem(ItemRegistry.DRAETON_BURNER);
		//draeton
		//draeton upgrades
		//rowboat lantern
		this.basicItem(ItemRegistry.AMATE_NAME_TAG);
		this.basicItem(ItemRegistry.DULL_LAVENDER_DYE);
		this.basicItem(ItemRegistry.MAROON_DYE);
		this.basicItem(ItemRegistry.SHADOW_GREEN_DYE);
		this.basicItem(ItemRegistry.CAMELOT_MAGENTA_DYE);
		this.basicItem(ItemRegistry.SAFFRON_DYE);
		this.basicItem(ItemRegistry.CARIBBEAN_GREEN_DYE);
		this.basicItem(ItemRegistry.VIVID_TANGERINE_DYE);
		this.basicItem(ItemRegistry.CHAMPAGNE_DYE);
		this.basicItem(ItemRegistry.RAISIN_BLACK_DYE);
		this.basicItem(ItemRegistry.SUSHI_GREEN_DYE);
		this.basicItem(ItemRegistry.ELM_CYAN_DYE);
		this.basicItem(ItemRegistry.CADMIUM_GREEN_DYE);
		this.basicItem(ItemRegistry.LAVENDER_BLUE_DYE);
		this.basicItem(ItemRegistry.BROWN_RUST_DYE);
		this.basicItem(ItemRegistry.MIDNIGHT_PURPLE_DYE);
		this.basicItem(ItemRegistry.PEWTER_GREY_DYE);
		this.itemFrame(ItemRegistry.DULL_LAVENDER_ITEM_FRAME);
		this.itemFrame(ItemRegistry.MAROON_ITEM_FRAME);
		this.itemFrame(ItemRegistry.SHADOW_GREEN_ITEM_FRAME);
		this.itemFrame(ItemRegistry.CAMELOT_MAGENTA_ITEM_FRAME);
		this.itemFrame(ItemRegistry.SAFFRON_ITEM_FRAME);
		this.itemFrame(ItemRegistry.CARIBBEAN_GREEN_ITEM_FRAME);
		this.itemFrame(ItemRegistry.VIVID_TANGERINE_ITEM_FRAME);
		this.itemFrame(ItemRegistry.CHAMPAGNE_ITEM_FRAME);
		this.itemFrame(ItemRegistry.RAISIN_BLACK_ITEM_FRAME);
		this.itemFrame(ItemRegistry.SUSHI_GREEN_ITEM_FRAME);
		this.itemFrame(ItemRegistry.ELM_CYAN_ITEM_FRAME);
		this.itemFrame(ItemRegistry.CADMIUM_GREEN_ITEM_FRAME);
		this.itemFrame(ItemRegistry.LAVENDER_BLUE_ITEM_FRAME);
		this.itemFrame(ItemRegistry.BROWN_RUST_ITEM_FRAME);
		this.itemFrame(ItemRegistry.MIDNIGHT_PURPLE_ITEM_FRAME);
		this.itemFrame(ItemRegistry.PEWTER_GREY_ITEM_FRAME);
		this.basicItem(ItemRegistry.PHEROMONE_THORAX);
		this.basicItem(ItemRegistry.MOSS_FILTER);
		this.basicItem(ItemRegistry.SILK_FILTER);
		this.basicItem(ItemRegistry.SILKY_PEBBLE).override().predicate(TheBetweenlands.prefix("charging"), 1.0F).model(pebbleOverlay.apply(ItemRegistry.SILKY_PEBBLE));

		this.basicItem(ItemRegistry.BONE_HELMET);
		this.basicItem(ItemRegistry.BONE_CHESTPLATE);
		this.basicItem(ItemRegistry.BONE_LEGGINGS);
		this.basicItem(ItemRegistry.BONE_BOOTS);
		this.basicItem(ItemRegistry.LURKER_SKIN_HELMET);
		this.basicItem(ItemRegistry.LURKER_SKIN_CHESTPLATE);
		this.basicItem(ItemRegistry.LURKER_SKIN_LEGGINGS);
		this.basicItem(ItemRegistry.LURKER_SKIN_BOOTS);
		this.basicItem(ItemRegistry.SYRMORITE_HELMET);
		this.basicItem(ItemRegistry.SYRMORITE_CHESTPLATE);
		this.basicItem(ItemRegistry.SYRMORITE_LEGGINGS);
		this.basicItem(ItemRegistry.SYRMORITE_BOOTS);
		this.basicItem(ItemRegistry.VALONITE_HELMET);
		this.basicItem(ItemRegistry.VALONITE_CHESTPLATE);
		this.basicItem(ItemRegistry.VALONITE_LEGGINGS);
		this.basicItem(ItemRegistry.VALONITE_BOOTS);
		this.basicItem(ItemRegistry.ANCIENT_HELMET);
		this.basicItem(ItemRegistry.ANCIENT_CHESTPLATE);
		this.basicItem(ItemRegistry.ANCIENT_LEGGINGS);
		this.basicItem(ItemRegistry.ANCIENT_BOOTS);
		this.basicItem(ItemRegistry.AMPHIBIOUS_HELMET);
		this.basicItem(ItemRegistry.AMPHIBIOUS_CHESTPLATE);
		this.basicItem(ItemRegistry.AMPHIBIOUS_LEGGINGS);
		this.basicItem(ItemRegistry.AMPHIBIOUS_BOOTS);
		this.basicItem(ItemRegistry.RUBBER_BOOTS);
		this.otherTextureItem(ItemRegistry.MARSH_RUNNER_BOOTS, this.modLoc("rubber_boots"));
		this.toolItem(ItemRegistry.WEEDWOOD_SWORD);
		this.toolItem(ItemRegistry.WEEDWOOD_SHOVEL);
		this.toolItem(ItemRegistry.WEEDWOOD_PICKAXE);
		this.toolItem(ItemRegistry.WEEDWOOD_AXE);
		this.toolItem(ItemRegistry.BONE_SWORD);
		this.toolItem(ItemRegistry.BONE_SHOVEL);
		this.toolItem(ItemRegistry.BONE_PICKAXE);
		this.toolItem(ItemRegistry.BONE_AXE);
		this.toolItem(ItemRegistry.OCTINE_SWORD);
		this.toolItem(ItemRegistry.OCTINE_SHOVEL);
		this.toolItem(ItemRegistry.OCTINE_PICKAXE);
		this.toolItem(ItemRegistry.OCTINE_AXE);
		this.toolItem(ItemRegistry.VALONITE_SWORD);
		this.toolItem(ItemRegistry.VALONITE_SHOVEL);
		this.toolItem(ItemRegistry.VALONITE_PICKAXE);
		this.toolItem(ItemRegistry.VALONITE_GREATAXE);
		this.toolItem(ItemRegistry.VALONITE_AXE);
		this.basicItem(ItemRegistry.SYRMORITE_SHEARS);
		this.basicItem(ItemRegistry.SICKLE);
		//shockwave sword
		this.basicItem(ItemRegistry.ANGLER_TOOTH_ARROW);
		this.basicItem(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW);
		this.basicItem(ItemRegistry.OCTINE_ARROW);
		this.basicItem(ItemRegistry.BASILISK_ARROW);
		this.basicItem(ItemRegistry.SLUDGE_WORM_ARROW);
		this.basicItem(ItemRegistry.SHOCK_ARROW);
		this.basicItem(ItemRegistry.CHIROMAW_BARB);
		this.bow(ItemRegistry.WEEDWOOD_BOW);
		this.bow(ItemRegistry.PREDATOR_BOW);
		//ancient weps
		this.basicItem(ItemRegistry.PESTLE).override().predicate(TheBetweenlands.prefix("active"), 1).model(this.basicItem(this.modLoc("pestle_animated")));
		this.toolItem(ItemRegistry.NET);
		this.pouch(ItemRegistry.SMALL_LURKER_SKIN_POUCH);
		this.pouch(ItemRegistry.MEDIUM_LURKER_SKIN_POUCH);
		this.pouch(ItemRegistry.LARGE_LURKER_SKIN_POUCH);
		this.pouch(ItemRegistry.XL_LURKER_SKIN_POUCH);
		//caving rope light
		//grappling hooks
		this.basicItem(ItemRegistry.VOLARKITE);
		this.basicItem(ItemRegistry.SLINGSHOT).transforms()
			.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(5.0F, 180.0F, 35.0F).translation(1.13F, 1.5F, 1.13F).scale(0.55F, 0.55F, 0.55F).end()
			.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(5.0F, 0.0F, -35.0F).translation(1.13F, 1.5F, 1.13F).scale(0.55F, 0.55F, 0.55F).end()
			.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0.0F, 180.0F, 45.0F).translation(0.0F, 2.0F, 1.0F).scale(0.55F, 0.55F, 0.55F).end()
			.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(0.0F, 0.0F, -45.0F).translation(0.0F, 2.0F, 1.0F).scale(0.55F, 0.55F, 0.55F).end().end()
			.override().predicate(TheBetweenlands.prefix("pulling"), 1.0F).model(this.basicItem(this.modLoc("simple_slingshot_equipped")).transforms()
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(5.0F, 180.0F, 35.0F).translation(1.13F, 1.5F, 1.13F).scale(0.55F, 0.55F, 0.55F).end()
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(5.0F, 0.0F, -35.0F).translation(1.13F, 1.5F, 1.13F).scale(0.55F, 0.55F, 0.55F).end()
				.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0.0F, 180.0F, 45.0F).translation(0.0F, 2.0F, 1.0F).scale(0.55F, 0.55F, 0.55F).end()
				.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(0.0F, 0.0F, -45.0F).translation(0.0F, 2.0F, 1.0F).scale(0.55F, 0.55F, 0.55F).end().end());
		//spears
		this.bucket(ItemRegistry.WEEDWOOD_BUCKET);
		this.bucket(ItemRegistry.SYRMORITE_BUCKET);
		this.multiLayerItem(ItemRegistry.WEEDWOOD_INFUSION_BUCKET.getId().getPath(), TheBetweenlands.prefix("item/weedwood_bucket"), TheBetweenlands.prefix("item/infusion_liquid"));
		this.multiLayerItem(ItemRegistry.SYRMORITE_INFUSION_BUCKET.getId().getPath(), TheBetweenlands.prefix("item/syrmorite_bucket"), TheBetweenlands.prefix("item/infusion_liquid"));
		this.basicItem(ItemRegistry.SOLID_RUBBER_SYRMORITE_BUCKET);
		this.basicItem(ItemRegistry.ASCENT_UPGRADE);
		this.basicItem(ItemRegistry.ELECTRIC_UPGRADE);
		this.basicItem(ItemRegistry.FISH_VORTEX_UPGRADE);
		this.basicItem(ItemRegistry.GLIDE_UPGRADE);
		this.basicItem(ItemRegistry.URCHIN_SPIKE_UPGRADE);
		this.basicItem(ItemRegistry.BIOPATHIC_TRIGGERSTONE)
			.override().predicate(TheBetweenlands.prefix("effect"), 1.0F).model(this.basicItem(TheBetweenlands.prefix("biopathic_triggerstone_electric"))).end()
			.override().predicate(TheBetweenlands.prefix("effect"), 2.0F).model(this.basicItem(TheBetweenlands.prefix("biopathic_triggerstone_urchin"))).end()
			.override().predicate(TheBetweenlands.prefix("effect"), 3.0F).model(this.basicItem(TheBetweenlands.prefix("biopathic_triggerstone_vortex"))).end();
		this.basicItem(ItemRegistry.BIOPATHIC_LINKSTONE);
		this.basicItem(ItemRegistry.SILK_BUNDLE);

		//skull mask
		//explorer hat
		//tree masks
		//gallery frames
		//silk mask
		this.toolItem(ItemRegistry.WIGHTS_BANE);
		this.toolItem(ItemRegistry.SLUDGE_SLICER);
		this.toolItem(ItemRegistry.CRITTER_CRUNCHER);
		this.toolItem(ItemRegistry.HAG_HACKER);
		this.toolItem(ItemRegistry.VOODOO_DOLL);
		this.toolItem(ItemRegistry.SWIFT_PICK);
		//erupters
		this.toolItem(ItemRegistry.MIST_STAFF);
		this.toolItem(ItemRegistry.SHADOW_STAFF);
		this.basicItem(ItemRegistry.RECORD_ANCIENT);
		this.basicItem(ItemRegistry.RECORD_ASTATOS);
		this.basicItem(ItemRegistry.RECORD_BENEATH_A_GREEN_SKY);
		this.basicItem(ItemRegistry.RECORD_BETWEEN_YOU_AND_ME);
		this.basicItem(ItemRegistry.RECORD_CHRISTMAS_ON_THE_MARSH);
		this.otherTextureItem(ItemRegistry.RECORD_DEEP_WATER_THEME, this.modLoc("freshwater_urchin"));
		this.basicItem(ItemRegistry.RECORD_DJ_WIGHTS_MIXTAPE);
		this.basicItem(ItemRegistry.RECORD_HAG_DANCE);
		this.basicItem(ItemRegistry.RECORD_LONELY_FIRE);
		this.basicItem(ItemRegistry.RECORD_ONWARDS);
		this.basicItem(ItemRegistry.RECORD_STUCK_IN_THE_MUD);
		this.basicItem(ItemRegistry.RECORD_THE_EXPLORER);
		this.basicItem(ItemRegistry.RECORD_WANDERING_WISPS);
		this.basicItem(ItemRegistry.RECORD_WATERLOGGED);
		this.basicItem(ItemRegistry.MYSTERIOUS_RECORD);
		//amulets
		//amulet slot
		this.basicItem(ItemRegistry.RING_OF_POWER);
		this.basicItem(ItemRegistry.RING_OF_ASCENT);
		this.basicItem(ItemRegistry.RING_OF_RECRUITMENT);
		this.basicItem(ItemRegistry.RING_OF_SUMMONING);
		this.basicItem(ItemRegistry.RING_OF_DISPERSION);
		this.basicItem(ItemRegistry.RING_OF_GATHERING);
		this.otherTextureItem(ItemRegistry.THEM_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.MUTANTS_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.SHADOWS_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.RUINS_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.HEADS_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.TAR_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.DUNGEON_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.PITSTONE_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.TOWER_SCRAP, this.modLoc("lore_scrap"));
		this.otherTextureItem(ItemRegistry.FORT_SCRAP, this.modLoc("lore_scrap"));
		this.basicItem(ItemRegistry.MUMMY_BAIT);
		this.basicItem(ItemRegistry.BARK_AMULET);
		this.basicItem(ItemRegistry.AMATE_MAP);
		this.basicItem(ItemRegistry.FILLED_AMATE_MAP);
		this.basicItem(ItemRegistry.BONE_WAYFINDER);
		this.basicItem(ItemRegistry.MAGIC_ITEM_MAGNET);
		this.basicItem(ItemRegistry.GEM_SINGER);
		this.basicItem(ItemRegistry.SNOT_POD);
		this.basicItem(ItemRegistry.GLUE);
		this.basicItem(ItemRegistry.TEST_CHIMP);
		this.toolItem(ItemRegistry.TEST_FLAG).transforms()
			.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(0.5F).translation(0, 4, 0).rotation(0, 90, 0).end()
			.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).scale(0.6F).translation(0, 4.75F, 2).rotation(0, -90, 0).end()
			.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).scale(0.6F).translation(0, 4.75F, 2).rotation(0, 90, 0).end();

		this.basicItem(ItemRegistry.GROUND_LEAF);
		this.basicItem(ItemRegistry.GROUND_CATTAIL);
		this.basicItem(ItemRegistry.GROUND_SWAMP_GRASS);
		this.basicItem(ItemRegistry.GROUND_SHOOTS);
		this.basicItem(ItemRegistry.GROUND_ARROW_ARUM);
		this.basicItem(ItemRegistry.GROUND_BUTTON_BUSH);
		this.basicItem(ItemRegistry.GROUND_MARSH_HIBUSCUS);
		this.basicItem(ItemRegistry.GROUND_PICKERELWEED);
		this.basicItem(ItemRegistry.GROUND_SOFT_RUSH);
		this.basicItem(ItemRegistry.GROUND_MARSH_MALLOW);
		this.basicItem(ItemRegistry.GROUND_MILKWEED);
		this.basicItem(ItemRegistry.GROUND_BLUE_IRIS);
		this.basicItem(ItemRegistry.GROUND_COPPER_IRIS);
		this.basicItem(ItemRegistry.GROUND_BLUE_EYED_GRASS);
		this.basicItem(ItemRegistry.GROUND_BONESET);
		this.basicItem(ItemRegistry.GROUND_BOTTLE_BRUSH_GRASS);
		this.basicItem(ItemRegistry.GROUND_WEEDWOOD_BARK);
		this.basicItem(ItemRegistry.GROUND_DRIED_SWAMP_REED);
		this.basicItem(ItemRegistry.GROUND_ALGAE);
		this.basicItem(ItemRegistry.GROUND_ANGLER_TOOTH);
		this.basicItem(ItemRegistry.GROUND_BLACKHAT_MUSHROOM);
		this.basicItem(ItemRegistry.GROUND_CRIMSON_SNAIL_SHELL);
		this.basicItem(ItemRegistry.GROUND_BOG_BEAN);
		this.basicItem(ItemRegistry.GROUND_BROOMSEDGE);
		this.basicItem(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM);
		this.basicItem(ItemRegistry.GROUND_CARDINAL_FLOWER);
		this.basicItem(ItemRegistry.GROUND_CAVE_GRASS);
		this.basicItem(ItemRegistry.GROUND_CAVE_MOSS);
		this.basicItem(ItemRegistry.GROUND_CRIMSON_MIDDLE_GEM);
		this.basicItem(ItemRegistry.GROUND_DEEP_WATER_CORAL);
		this.basicItem(ItemRegistry.GROUND_FLATHEAD_MUSHROOM);
		this.basicItem(ItemRegistry.GROUND_GOLDEN_CLUB);
		this.basicItem(ItemRegistry.GROUND_GREEN_MIDDLE_GEM);
		this.basicItem(ItemRegistry.GROUND_HANGER);
		this.basicItem(ItemRegistry.GROUND_LICHEN);
		this.basicItem(ItemRegistry.GROUND_MARSH_MARIGOLD);
		this.basicItem(ItemRegistry.GROUND_MIRE_CORAL);
		this.basicItem(ItemRegistry.GROUND_OCHRE_SNAIL_SHELL);
		this.basicItem(ItemRegistry.GROUND_MOSS);
		this.basicItem(ItemRegistry.GROUND_NETTLE);
		this.basicItem(ItemRegistry.GROUND_PHRAGMITES);
		this.basicItem(ItemRegistry.GROUND_SLUDGECREEP);
		this.basicItem(ItemRegistry.GROUND_SUNDEW);
		this.basicItem(ItemRegistry.GROUND_SWAMP_KELP);
		this.basicItem(ItemRegistry.GROUND_ROOTS);
		this.basicItem(ItemRegistry.GROUND_AQUA_MIDDLE_GEM);
		this.basicItem(ItemRegistry.GROUND_PITCHER_PLANT);
		this.basicItem(ItemRegistry.GROUND_WATER_WEEDS);
		this.basicItem(ItemRegistry.GROUND_VENUS_FLY_TRAP);
		this.basicItem(ItemRegistry.GROUND_VOLARPAD);
		this.basicItem(ItemRegistry.GROUND_THORNS);
		this.basicItem(ItemRegistry.GROUND_POISON_IVY);
		this.basicItem(ItemRegistry.GROUND_BLADDERWORT_FLOWER);
		this.basicItem(ItemRegistry.GROUND_BLADDERWORT_STALK);
		this.basicItem(ItemRegistry.GROUND_EDGE_SHROOM);
		this.basicItem(ItemRegistry.GROUND_EDGE_MOSS);
		this.basicItem(ItemRegistry.GROUND_EDGE_LEAF);
		this.basicItem(ItemRegistry.GROUND_ROTBULB);
		this.basicItem(ItemRegistry.GROUND_PALE_GRASS);
		this.basicItem(ItemRegistry.GROUND_STRING_ROOTS);
		this.basicItem(ItemRegistry.GROUND_CRYPTWEED);
		this.basicItem(ItemRegistry.GROUND_BETWEENSTONE_PEBBLE);
		this.basicItem(ItemRegistry.LEAF);
		this.basicItem(ItemRegistry.ALGAE_CLUMP);
		this.basicItem(ItemRegistry.ARROW_ARUM_LEAF);
		this.basicItem(ItemRegistry.BLUE_EYED_GRASS_FLOWERS);
		this.basicItem(ItemRegistry.BLUE_IRIS_PETAL);
		this.basicItem(ItemRegistry.MIRE_CORAL_STALK);
		this.basicItem(ItemRegistry.DEEP_WATER_CORAL_STALK);
		this.basicItem(ItemRegistry.BOG_BEAN_FLOWER_DROP);
		this.basicItem(ItemRegistry.BONESET_FLOWERS);
		this.basicItem(ItemRegistry.BOTTLE_BRUSH_GRASS_BLADES);
		this.basicItem(ItemRegistry.BROOMSEDGE_LEAVES);
		this.basicItem(ItemRegistry.BUTTON_BUSH_FLOWERS);
		this.basicItem(ItemRegistry.CARDINAL_FLOWER_PETALS);
		this.basicItem(ItemRegistry.CATTAIL_HEAD);
		this.basicItem(ItemRegistry.CAVE_GRASS_BLADES);
		this.basicItem(ItemRegistry.COPPER_IRIS_PETALS);
		this.basicItem(ItemRegistry.GOLDEN_CLUB_FLOWERS);
		this.basicItem(ItemRegistry.LICHEN_CLUMP);
		this.basicItem(ItemRegistry.MARSH_HIBISCUS_FLOWER);
		this.basicItem(ItemRegistry.MARSH_MALLOW_FLOWER);
		this.basicItem(ItemRegistry.MARSH_MARIGOLD_FLOWER_DROP);
		this.basicItem(ItemRegistry.NETTLE_LEAF);
		this.basicItem(ItemRegistry.PHRAGMITE_STEMS);
		this.basicItem(ItemRegistry.PICKERELWEED_FLOWER);
		this.basicItem(ItemRegistry.SHOOT_LEAVES);
		this.basicItem(ItemRegistry.SLUDGECREEP_LEAVES);
		this.basicItem(ItemRegistry.SOFT_RUSH_LEAVES);
		this.basicItem(ItemRegistry.SUNDEW_HEAD);
		this.basicItem(ItemRegistry.SWAMP_GRASS_BLADES);
		this.basicItem(ItemRegistry.CAVE_MOSS_CLUMP);
		this.basicItem(ItemRegistry.MOSS_CLUMP);
		this.basicItem(ItemRegistry.MILKWEED_FLOWER);
		this.basicItem(ItemRegistry.HANGER_DROP);
		this.basicItem(ItemRegistry.PITCHER_PLANT_TRAP);
		this.basicItem(ItemRegistry.WATER_WEEDS_DROP);
		this.basicItem(ItemRegistry.VENUS_FLY_TRAP_HEAD);
		this.basicItem(ItemRegistry.VOLARPAD_LEAF);
		this.basicItem(ItemRegistry.THORN_BRANCH);
		this.basicItem(ItemRegistry.POISON_IVY_LEAF);
		this.basicItem(ItemRegistry.BLADDERWORT_STALK_DROP);
		this.basicItem(ItemRegistry.BLADDERWORT_FLOWER_DROP);
		this.basicItem(ItemRegistry.EDGE_SHROOM_GILLS);
		this.basicItem(ItemRegistry.EDGE_MOSS_CLUMP);
		this.basicItem(ItemRegistry.EDGE_LEAF_DROP);
		this.basicItem(ItemRegistry.ROTBULB_STALK);
		this.basicItem(ItemRegistry.PALE_GRASS_BLADES);
		this.basicItem(ItemRegistry.STRING_ROOT_FIBERS);
		this.basicItem(ItemRegistry.CRYPTWEED_BLADES);

		var anadia = this.multiLayerItem(ItemRegistry.ANADIA.getId().getPath(), TheBetweenlands.prefix("item/anadia_head_1"), TheBetweenlands.prefix("item/anadia_body_1"), TheBetweenlands.prefix("item/anadia_tail_1"));

		for (int head = 0; head < 3; head++) {
			for (int body = 0; body < 3; body++) {
				for (int tail = 0; tail < 3; tail++) {
					var anadiaModel = this.multiLayerItem(ItemRegistry.ANADIA.getId().toString() + head + body + tail, TheBetweenlands.prefix("item/anadia_head_" + (head + 1)), TheBetweenlands.prefix("item/anadia_body_" + (body + 1)), TheBetweenlands.prefix("item/anadia_tail_" + (tail + 1)));
					anadia.override()
						.predicate(TheBetweenlands.prefix("head"), head)
						.predicate(TheBetweenlands.prefix("body"), body)
						.predicate(TheBetweenlands.prefix("tail"), tail)
						.model(anadiaModel).end();
				}
			}
		}

		this.multiLayerItem(ItemRegistry.ASPECTRUS_FRUIT.getId().getPath(), TheBetweenlands.prefix("item/aspectrus_fruit"), TheBetweenlands.prefix("item/aspectrus_fruit_overlay"));

		this.basicItem(ItemRegistry.GREEN_DENTROTHYST_VIAL);
		this.basicItem(ItemRegistry.DIRTY_DENTROTHYST_VIAL);
		this.basicItem(ItemRegistry.ORANGE_DENTROTHYST_VIAL);

		this.multiLayerItem(ItemRegistry.GREEN_ELIXIR.getId().getPath(), TheBetweenlands.prefix("item/vial_liquid"), TheBetweenlands.prefix("item/green_dentrothyst_vial"), TheBetweenlands.prefix("item/vial_liquid_glint"));
		this.multiLayerItem(ItemRegistry.ORANGE_ELIXIR.getId().getPath(), TheBetweenlands.prefix("item/vial_liquid"), TheBetweenlands.prefix("item/orange_dentrothyst_vial"), TheBetweenlands.prefix("item/vial_liquid_glint"));
		this.multiLayerItem(ItemRegistry.GREEN_ASPECT_VIAL.getId().getPath(), TheBetweenlands.prefix("item/aspect_liquid"), TheBetweenlands.prefix("item/green_dentrothyst_vial"));
		this.multiLayerItem(ItemRegistry.ORANGE_ASPECT_VIAL.getId().getPath(), TheBetweenlands.prefix("item/aspect_liquid"), TheBetweenlands.prefix("item/orange_dentrothyst_vial"));
	}

	public ItemModelBuilder basicItem(DeferredItem<? extends Item> item) {
		return this.basicItem(item.get());
	}

	public ItemModelBuilder toolItem(DeferredItem<? extends Item> item) {
		return this.getBuilder(item.getId().toString())
			.parent(new ModelFile.UncheckedModelFile("item/handheld"))
			.texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getId().getNamespace(), "item/" + item.getId().getPath()));
	}

	public ItemModelBuilder itemFrame(DeferredItem<? extends Item> item) {
		return this.getBuilder(item.getId().toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", TheBetweenlands.prefix("item/item_frame_bg"))
			.texture("layer1", TheBetweenlands.prefix("item/item_frame"));
	}

	public ItemModelBuilder otherTextureItem(DeferredItem<? extends Item> item, ResourceLocation texture) {
		return this.getBuilder(item.getId().toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "item/" + texture.getPath()));
	}

	public ItemModelBuilder multiLayerItem(String name, ResourceLocation... layers) {
		ItemModelBuilder builder = this.getBuilder(name).parent(new ModelFile.UncheckedModelFile("item/generated"));
		for (int i = 0; i < layers.length; i++) {
			builder = builder.texture("layer" + i, layers[i]);
		}
		return builder;
	}

	public ResourceLocation itemTexture(DeferredItem<?> item) {
		ResourceLocation name = item.getId();
		return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), ModelProvider.ITEM_FOLDER + "/" + name.getPath());
	}

	public ItemModelBuilder pouch(DeferredItem<Item> pouch) {
		return this.multiLayerItem(pouch.getId().getPath(), this.itemTexture(pouch), this.itemTexture(pouch).withSuffix("_cord"));
	}

	private static final String[] TYPES = {"", "_poison", "_octine", "_basilisk", "_shock", "_barb", "_worm"};
	private static final float[] PULLS = {0.0F, 0.65F, 0.9F};

	public ItemModelBuilder bow(DeferredItem<Item> bow) {
		var builder = this.getBuilder(bow.getId().toString())
			.parent(new ModelFile.UncheckedModelFile("item/bow"))
			.texture("layer0", this.itemTexture(bow));

		for (int type = 0; type < TYPES.length; type++) {
			for (int i = 0; i <= 2; i++) {
				var model = this.getBuilder(bow.getId() + TYPES[type] + "_" + i)
					.parent(new ModelFile.UncheckedModelFile("item/bow"))
					.texture("layer0", this.itemTexture(bow).withSuffix(TYPES[type] + "_" + i));
				builder.override()
					.predicate(TheBetweenlands.prefix("pull"), PULLS[i])
					.predicate(TheBetweenlands.prefix("pulling"), 1)
					.predicate(TheBetweenlands.prefix("type"), type)
					.model(model).end();
			}
		}
		return builder;
	}

	public ItemModelBuilder bucket(DeferredItem<? extends Item> item) {
		return this.getBuilder(item.getId().toString())
			.parent(new ModelFile.UncheckedModelFile("neoforge:item/default"))
			.customLoader(DynamicFluidContainerModelBuilder::begin).fluid(Fluids.EMPTY).end()
			.texture("base", this.itemTexture(item))
			.texture("fluid", TheBetweenlands.prefix("item/bl_bucket_fluid"));
	}

	public ItemModelBuilder vanillaBucket(DeferredItem<? extends BucketItem> item) {
		return this.getBuilder(item.getId().toString())
			.parent(new ModelFile.UncheckedModelFile("neoforge:item/default"))
			.customLoader(DynamicFluidContainerModelBuilder::begin).fluid(item.get().content).end()
			.texture("base", this.mcLoc("item/bucket"))
			.texture("fluid", ResourceLocation.fromNamespaceAndPath(NeoForgeVersion.MOD_ID, "item/mask/bucket_fluid"));
	}
}
