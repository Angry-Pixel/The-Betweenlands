package thebetweenlands.common.datagen.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.api.item.CustomCorrodible;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BLItemTagProvider extends ItemTagsProvider {

	public static final TagKey<Item> OCTINE_IGNITES = tag("octine_ignites");
	public static final TagKey<Item> GIVES_FOOD_SICKNESS = tag("gives_food_sickness");
	public static final TagKey<Item> REPAIRS_WEEDWOOD_TOOLS = tag("repairs_weedwood_tools");
	public static final TagKey<Item> REPAIRS_BONE_TOOLS = tag("repairs_bone_tools");
	public static final TagKey<Item> REPAIRS_OCTINE_TOOLS = tag("repairs_octine_tools");
	public static final TagKey<Item> REPAIRS_VALONITE_TOOLS = tag("repairs_valonite_tools");

	public static final TagKey<Item> REPAIRS_GREEN_DENTROTHYST_SHIELD = tag("repairs_green_dentrothyst_shield");
	public static final TagKey<Item> REPAIRS_ORANGE_DENTROTHYST_SHIELD = tag("repairs_orange_dentrothyst_shield");
	public static final TagKey<Item> REPAIRS_SYRMORITE_SHIELD = tag("repairs_syrmorite_shield");
	public static final TagKey<Item> REPAIRS_LURKER_SKIN_SHIELD = tag("repairs_lurker_skin_shield");

	public static final TagKey<Item> BL_LOGS = tag("betweenlands_logs");
	public static final TagKey<Item> FILTERED_SILT_GLASS = tag("filtered_silt_glass");
	public static final TagKey<Item> MUD_BRICK_SHINGLES = tag("mud_brick_shingles");
	public static final TagKey<Item> REED_MATS = tag("reed_mats");
	public static final TagKey<Item> SAMITE = tag("samite");
	public static final TagKey<Item> SAMITE_CANVAS_PANELS = tag("samite_canvas_panels");
	public static final TagKey<Item> ITEM_FRAMES = tag("item_frames");
	public static final TagKey<Item> SLINGSHOT_AMMO = tag("slingshot_ammo");

	/**
	 * Whether an item ignores the weakness on non-betweenlands tools
	 */
	public static final TagKey<Item> IGNORES_TOOL_WEAKNESS = tag("ignores_tool_weakness");

	/**
	 * Whether an item should be looked at by the corrosion engine
	 */
	public static final TagKey<Item> CORRODIBLE = tag("corrodible");

	/**
	 * Use default corrosion on an item. Apply to items from other mods you want to be corrodible.
	 */
	public static final TagKey<Item> DEFAULT_CORRODIBLE = tag("corrodible/default");

	/**
	 * Whether an item has custom corrosion information. The item should implement {@link CustomCorrodible}; this tag will be ignored if it doesn't.
	 */
	public static final TagKey<Item> CUSTOM_CORRODIBLE = tag("corrodible/custom");

	public static final TagKey<Item> DOES_NOT_ROT = tag("does_not_rot");

	public BLItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper helper) {
		super(output, lookupProvider, blockTags, TheBetweenlands.ID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.copy(BLBlockTagProvider.OCTINE_IGNITES, OCTINE_IGNITES);
		this.tag(SLINGSHOT_AMMO).add(ItemRegistry.FISH_BAIT.get(), BlockRegistry.BETWEENSTONE_PEBBLE.asItem());
		this.tag(REPAIRS_BONE_TOOLS).add(ItemRegistry.SLIMY_BONE.get());
		this.tag(REPAIRS_OCTINE_TOOLS).add(ItemRegistry.OCTINE_INGOT.get());
		this.tag(REPAIRS_WEEDWOOD_TOOLS).add(BlockRegistry.WEEDWOOD_PLANKS.asItem());
		this.tag(REPAIRS_VALONITE_TOOLS).add(ItemRegistry.VALONITE_SHARD.get());

		this.tag(REPAIRS_GREEN_DENTROTHYST_SHIELD).add(ItemRegistry.GREEN_DENTROTHYST_SHARD.get());
		this.tag(REPAIRS_ORANGE_DENTROTHYST_SHIELD).add(ItemRegistry.ORANGE_DENTROTHYST_SHARD.get());
		this.tag(REPAIRS_SYRMORITE_SHIELD).add(ItemRegistry.SYRMORITE_INGOT.get());
		this.tag(REPAIRS_LURKER_SKIN_SHIELD).add(ItemRegistry.LURKER_SKIN.get());

		this.tag(FILTERED_SILT_GLASS).add(BlockRegistry.DULL_LAVENDER_FILTERED_SILT_GLASS.asItem(), BlockRegistry.MAROON_FILTERED_SILT_GLASS.asItem(),
			BlockRegistry.SHADOW_GREEN_FILTERED_SILT_GLASS.asItem(), BlockRegistry.CAMELOT_MAGENTA_FILTERED_SILT_GLASS.asItem(),
			BlockRegistry.SAFFRON_FILTERED_SILT_GLASS.asItem(), BlockRegistry.CARIBBEAN_GREEN_FILTERED_SILT_GLASS.asItem(),
			BlockRegistry.VIVID_TANGERINE_FILTERED_SILT_GLASS.asItem(), BlockRegistry.CHAMPAGNE_FILTERED_SILT_GLASS.asItem(),
			BlockRegistry.RAISIN_BLACK_FILTERED_SILT_GLASS.asItem(), BlockRegistry.SUSHI_GREEN_FILTERED_SILT_GLASS.asItem(),
			BlockRegistry.ELM_CYAN_FILTERED_SILT_GLASS.asItem(), BlockRegistry.CADMIUM_GREEN_FILTERED_SILT_GLASS.asItem(),
			BlockRegistry.LAVENDER_BLUE_FILTERED_SILT_GLASS.asItem(), BlockRegistry.BROWN_RUST_FILTERED_SILT_GLASS.asItem(),
			BlockRegistry.MIDNIGHT_PURPLE_FILTERED_SILT_GLASS.asItem(), BlockRegistry.PEWTER_GREY_FILTERED_SILT_GLASS.asItem());

		this.tag(MUD_BRICK_SHINGLES).add(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.MAROON_MUD_BRICK_SHINGLES.asItem(),
			BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLES.asItem(),
			BlockRegistry.SAFFRON_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLES.asItem(),
			BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLES.asItem(),
			BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLES.asItem(),
			BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLES.asItem(),
			BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLES.asItem(),
			BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLES.asItem(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLES.asItem());

		this.tag(SAMITE).add(BlockRegistry.DULL_LAVENDER_SAMITE.asItem(), BlockRegistry.MAROON_SAMITE.asItem(),
			BlockRegistry.SHADOW_GREEN_SAMITE.asItem(), BlockRegistry.CAMELOT_MAGENTA_SAMITE.asItem(),
			BlockRegistry.SAFFRON_SAMITE.asItem(), BlockRegistry.CARIBBEAN_GREEN_SAMITE.asItem(),
			BlockRegistry.VIVID_TANGERINE_SAMITE.asItem(), BlockRegistry.CHAMPAGNE_SAMITE.asItem(),
			BlockRegistry.RAISIN_BLACK_SAMITE.asItem(), BlockRegistry.SUSHI_GREEN_SAMITE.asItem(),
			BlockRegistry.ELM_CYAN_SAMITE.asItem(), BlockRegistry.CADMIUM_GREEN_SAMITE.asItem(),
			BlockRegistry.LAVENDER_BLUE_SAMITE.asItem(), BlockRegistry.BROWN_RUST_SAMITE.asItem(),
			BlockRegistry.MIDNIGHT_PURPLE_SAMITE.asItem(), BlockRegistry.PEWTER_GREY_SAMITE.asItem());

		this.tag(SAMITE_CANVAS_PANELS).add(BlockRegistry.DULL_LAVENDER_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.MAROON_SAMITE_CANVAS_PANEL.asItem(),
			BlockRegistry.SHADOW_GREEN_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.CAMELOT_MAGENTA_SAMITE_CANVAS_PANEL.asItem(),
			BlockRegistry.SAFFRON_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.CARIBBEAN_GREEN_SAMITE_CANVAS_PANEL.asItem(),
			BlockRegistry.VIVID_TANGERINE_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.CHAMPAGNE_SAMITE_CANVAS_PANEL.asItem(),
			BlockRegistry.RAISIN_BLACK_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.SUSHI_GREEN_SAMITE_CANVAS_PANEL.asItem(),
			BlockRegistry.ELM_CYAN_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.CADMIUM_GREEN_SAMITE_CANVAS_PANEL.asItem(),
			BlockRegistry.LAVENDER_BLUE_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.BROWN_RUST_SAMITE_CANVAS_PANEL.asItem(),
			BlockRegistry.MIDNIGHT_PURPLE_SAMITE_CANVAS_PANEL.asItem(), BlockRegistry.PEWTER_GREY_SAMITE_CANVAS_PANEL.asItem());

		this.tag(REED_MATS).add(BlockRegistry.DULL_LAVENDER_REED_MAT.asItem(), BlockRegistry.MAROON_REED_MAT.asItem(),
			BlockRegistry.SHADOW_GREEN_REED_MAT.asItem(), BlockRegistry.CAMELOT_MAGENTA_REED_MAT.asItem(),
			BlockRegistry.SAFFRON_REED_MAT.asItem(), BlockRegistry.CARIBBEAN_GREEN_REED_MAT.asItem(),
			BlockRegistry.VIVID_TANGERINE_REED_MAT.asItem(), BlockRegistry.CHAMPAGNE_REED_MAT.asItem(),
			BlockRegistry.RAISIN_BLACK_REED_MAT.asItem(), BlockRegistry.SUSHI_GREEN_REED_MAT.asItem(),
			BlockRegistry.ELM_CYAN_REED_MAT.asItem(), BlockRegistry.CADMIUM_GREEN_REED_MAT.asItem(),
			BlockRegistry.LAVENDER_BLUE_REED_MAT.asItem(), BlockRegistry.BROWN_RUST_REED_MAT.asItem(),
			BlockRegistry.MIDNIGHT_PURPLE_REED_MAT.asItem(), BlockRegistry.PEWTER_GREY_REED_MAT.asItem());

		this.tag(ITEM_FRAMES).add(ItemRegistry.DULL_LAVENDER_ITEM_FRAME.asItem(), ItemRegistry.MAROON_ITEM_FRAME.asItem(),
			ItemRegistry.SHADOW_GREEN_ITEM_FRAME.asItem(), ItemRegistry.CAMELOT_MAGENTA_ITEM_FRAME.asItem(),
			ItemRegistry.SAFFRON_ITEM_FRAME.asItem(), ItemRegistry.CARIBBEAN_GREEN_ITEM_FRAME.asItem(),
			ItemRegistry.VIVID_TANGERINE_ITEM_FRAME.asItem(), ItemRegistry.CHAMPAGNE_ITEM_FRAME.asItem(),
			ItemRegistry.RAISIN_BLACK_ITEM_FRAME.asItem(), ItemRegistry.SUSHI_GREEN_ITEM_FRAME.asItem(),
			ItemRegistry.ELM_CYAN_ITEM_FRAME.asItem(), ItemRegistry.CADMIUM_GREEN_ITEM_FRAME.asItem(),
			ItemRegistry.LAVENDER_BLUE_ITEM_FRAME.asItem(), ItemRegistry.BROWN_RUST_ITEM_FRAME.asItem(),
			ItemRegistry.MIDNIGHT_PURPLE_ITEM_FRAME.asItem(), ItemRegistry.PEWTER_GREY_ITEM_FRAME.asItem());

		// Those two "inherit" from this one
		this.tag(CORRODIBLE).addTag(DEFAULT_CORRODIBLE).addTag(CUSTOM_CORRODIBLE);
		this.tag(DEFAULT_CORRODIBLE);
		this.tag(CUSTOM_CORRODIBLE);

		this.tag(GIVES_FOOD_SICKNESS).add(ItemRegistry.MIRE_SNAIL_EGG.get(), ItemRegistry.COOKED_MIRE_SNAIL_EGG.get(),
			ItemRegistry.RAW_FROG_LEGS.get(), ItemRegistry.COOKED_FROG_LEGS.get(),
			ItemRegistry.RAW_SNAIL_FLESH.get(), ItemRegistry.COOKED_SNAIL_FLESH.get(),
			ItemRegistry.REED_DONUT.get(), ItemRegistry.JAM_DONUT.get(),
			ItemRegistry.GERTS_DONUT.get(), ItemRegistry.PUFFSHROOM_TENDRIL.get(),
			ItemRegistry.KRAKEN_TENTACLE.get(), ItemRegistry.KRAKEN_CALAMARI.get(),
			ItemRegistry.MIDDLE_FRUIT.get(), ItemRegistry.MINCE_PIE.get(),
			ItemRegistry.CHRISTMAS_PUDDING.get(), ItemRegistry.CANDY_CANE.get(),
			ItemRegistry.WEEPING_BLUE_PETAL.get(), ItemRegistry.YELLOW_DOTTED_FUNGUS.get(),
			ItemRegistry.SILT_CRAB_CLAW.get(), ItemRegistry.CRAB_STICK.get(),
			ItemRegistry.SLUDGE_JELLO.get(), ItemRegistry.MIDDLE_FRUIT_JELLO.get(),
			ItemRegistry.SAP_JELLO.get(), ItemRegistry.GREEN_MARSHMALLOW.get(),
			ItemRegistry.PINK_MARSHMALLOW.get(), ItemRegistry.BULB_CAPPED_MUSHROOM.get(),
			ItemRegistry.FRIED_SWAMP_KELP.get(), ItemRegistry.FORBIDDEN_FIG.get(),
			ItemRegistry.BLUE_CANDY.get(), ItemRegistry.RED_CANDY.get(),
			ItemRegistry.YELLOW_CANDY.get(), ItemRegistry.MIRE_SCRAMBLE.get(),
			ItemRegistry.WEEPING_BLUE_PETAL_SALAD.get(), ItemRegistry.NIBBLESTICK.get(),
			ItemRegistry.SPIRIT_FRUIT.get(), ItemRegistry.SUSHI.get(),
			ItemRegistry.RAW_ANADIA_MEAT.get(), ItemRegistry.COOKED_ANADIA_MEAT.get(),
			ItemRegistry.SMOKED_ANADIA_MEAT.get(), ItemRegistry.BARNACLE.get(),
			ItemRegistry.COOKED_BARNACLE.get(), ItemRegistry.SMOKED_BARNACLE.get(),
			ItemRegistry.SMOKED_CRAB_STICK.get(), ItemRegistry.SMOKED_FROG_LEGS.get(),
			ItemRegistry.SMOKED_PUFFSHROOM_TENDRIL.get(), ItemRegistry.SMOKED_SILT_CRAB_CLAW.get(),
			ItemRegistry.SMOKED_SNAIL_FLESH.get(), ItemRegistry.RAW_OLM_EGG.get(),
			ItemRegistry.COOKED_OLM_EGG.get(), ItemRegistry.OLMLETTE.get(),
			ItemRegistry.SILK_GRUB.get());

		this.tag(ItemTags.SWORDS).add(
			ItemRegistry.WEEDWOOD_SWORD.get(),
			ItemRegistry.BONE_SWORD.get(),
			ItemRegistry.OCTINE_SWORD.get(),
			ItemRegistry.VALONITE_SWORD.get()
			// TODO Shockwave, Ancient
		);

		this.tag(ItemTags.PICKAXES).add(
			ItemRegistry.WEEDWOOD_PICKAXE.get(),
			ItemRegistry.BONE_PICKAXE.get(),
			ItemRegistry.OCTINE_PICKAXE.get(),
			ItemRegistry.VALONITE_PICKAXE.get(),
			ItemRegistry.SWIFT_PICK.get()
		);

		this.tag(ItemTags.AXES).add(
			ItemRegistry.WEEDWOOD_AXE.get(),
			ItemRegistry.BONE_AXE.get(),
			ItemRegistry.OCTINE_AXE.get(),
			ItemRegistry.VALONITE_AXE.get(),
			ItemRegistry.VALONITE_GREATAXE.get()
			// TODO Ancient Greataxe
		);

		this.tag(ItemTags.SHOVELS).add(
			ItemRegistry.WEEDWOOD_SHOVEL.get(),
			ItemRegistry.BONE_SHOVEL.get(),
			ItemRegistry.OCTINE_SHOVEL.get(),
			ItemRegistry.VALONITE_SHOVEL.get()
		);

		this.tag(DEFAULT_CORRODIBLE).add(
			//Swords
			ItemRegistry.WEEDWOOD_SWORD.get(),
			ItemRegistry.BONE_SWORD.get(),
			ItemRegistry.OCTINE_SWORD.get(),
			ItemRegistry.VALONITE_SWORD.get(),
			ItemRegistry.CRITTER_CRUNCHER.get(),
			ItemRegistry.WIGHTS_BANE.get(),
			ItemRegistry.SLUDGE_SLICER.get(),
			// TODO Shockwave, Ancient

			// Pickaxes
			ItemRegistry.WEEDWOOD_PICKAXE.get(),
			ItemRegistry.BONE_PICKAXE.get(),
			ItemRegistry.OCTINE_PICKAXE.get(),
			ItemRegistry.VALONITE_PICKAXE.get(),
			ItemRegistry.SWIFT_PICK.get(),

			// Axes
			ItemRegistry.WEEDWOOD_AXE.get(),
			ItemRegistry.BONE_AXE.get(),
			ItemRegistry.OCTINE_AXE.get(),
			ItemRegistry.VALONITE_AXE.get(),
			ItemRegistry.VALONITE_GREATAXE.get(),
			ItemRegistry.HAG_HACKER.get(),
			// TODO Ancient Greataxe

			// Shovels
			ItemRegistry.WEEDWOOD_SHOVEL.get(),
			ItemRegistry.BONE_SHOVEL.get(),
			ItemRegistry.OCTINE_SHOVEL.get(),
			ItemRegistry.VALONITE_SHOVEL.get(),

			ItemRegistry.SICKLE.get(),
			ItemRegistry.SLINGSHOT.get(),
			ItemRegistry.WEEDWOOD_BOW.get(),
			ItemRegistry.PREDATOR_BOW.get()
		);

		this.tag(ItemTags.ARROWS).add(ItemRegistry.ANGLER_TOOTH_ARROW.get(), ItemRegistry.POISONED_ANGLER_TOOTH_ARROW.get(),
			ItemRegistry.OCTINE_ARROW.get(), ItemRegistry.BASILISK_ARROW.get(), ItemRegistry.SLUDGE_WORM_ARROW.get(),
			ItemRegistry.SHOCK_ARROW.get(), ItemRegistry.CHIROMAW_BARB.get());

		this.tag(DOES_NOT_ROT).add(Items.ROTTEN_FLESH); // TODO: add BL food items here

		this.tag(IGNORES_TOOL_WEAKNESS).addTag(CORRODIBLE);
	}

	public static TagKey<Item> tag(String tagName) {
		return ItemTags.create(TheBetweenlands.prefix(tagName));
	}

	public static TagKey<Item> commonTag(String tagName) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", tagName));
	}

}
