package thebetweenlands.common.registries;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.*;

//FIXME basically none of the items are accurate, I only added entries so I could use them in blocks
public class ItemRegistry {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheBetweenlands.ID);

	//TODO proper armor item
	public static final DeferredItem<Item> LURKER_SKIN_BOOTS = ITEMS.register("lurker_skin_boots", () -> new Item(new Item.Properties().stacksTo(1)));

	public static final DeferredItem<Item> GERTS_DONUT = ITEMS.register("gerts_donut", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> LIMESTONE_FLUX = ITEMS.register("limestone_flux", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> COMPOST = ITEMS.register("compost", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> OCTINE_INGOT = ITEMS.register("octine_ingot", () -> new OctineIngotItem(new Item.Properties()));
	public static final DeferredItem<Item> RUNE_DOOR_KEY = ITEMS.register("rune_door_key", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> MIDDLE_FRUIT = ITEMS.register("middle_fruit", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.6F).build())));
	public static final DeferredItem<Item> SAP_SPIT = ITEMS.register("sap_spit", () -> new SapSpitItem(new Item.Properties()));
	public static final DeferredItem<Item> PESTLE = ITEMS.register("pestle", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> SULFUR = ITEMS.register("sulfur", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> CREMAINS = ITEMS.register("cremains", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> ELIXIR = ITEMS.register("elixir", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> FUMIGANT = ITEMS.register("fumigant", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> SAP_BALL = ITEMS.register("sap_ball", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> BARK_AMULET = ITEMS.register("bark_amulet", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> WEEPING_BLUE_PETAL = ITEMS.register("weeping_blue_petal", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> ASPECTRUS_FRUIT = ITEMS.register("apsectrus_fruit", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> SHIMMER_STONE = ITEMS.register("shimmer_stone", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> ANADIA = ITEMS.register("anadia", () -> new AnadiaMobItem(new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> ANADIA_REMAINS = ITEMS.register("anadia_remains", () -> new Item(new Item.Properties()));

	public static final DeferredItem<Item> DIRTY_DENTROTHYST_VIAL = ITEMS.register("dirty_dentothyst_vial", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> ORANGE_DENTROTHYST_VIAL = ITEMS.register("orange_dentothyst_vial", () -> new DentrothystVialItem(ItemRegistry.ORANGE_ASPECT_VIAL, new Item.Properties()));
	public static final DeferredItem<Item> GREEN_DENTROTHYST_VIAL = ITEMS.register("green_dentothyst_vial", () -> new DentrothystVialItem(ItemRegistry.GREEN_ASPECT_VIAL, new Item.Properties()));
	public static final DeferredItem<Item> ORANGE_ASPECT_VIAL = ITEMS.register("orange_aspect_vial", () -> new AspectVialItem(new Item.Properties().craftRemainder(ORANGE_DENTROTHYST_VIAL.get())));
	public static final DeferredItem<Item> GREEN_ASPECT_VIAL = ITEMS.register("green_aspect_vial", () -> new AspectVialItem(new Item.Properties().craftRemainder(GREEN_DENTROTHYST_VIAL.get())));


	public static final DeferredItem<Item> SWAMP_WATER_BUCKET = ITEMS.register("swamp_water_bucket", () -> new BucketItem(FluidRegistry.SWAMP_WATER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> STAGNANT_WATER_BUCKET = ITEMS.register("stagnant_water_bucket", () -> new BucketItem(FluidRegistry.STAGNANT_WATER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> TAR_BUCKET = ITEMS.register("tar_bucket", () -> new BucketItem(FluidRegistry.TAR_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> RUBBER_BUCKET = ITEMS.register("rubber_bucket", () -> new BucketItem(FluidRegistry.RUBBER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> DYE_BUCKET = ITEMS.register("dye_bucket", () -> new BucketItem(FluidRegistry.DYE_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> BREW_BUCKET = ITEMS.register("brew_bucket", () -> new BucketItem(FluidRegistry.BREW_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> CLEAN_WATER_BUCKET = ITEMS.register("clean_water_bucket", () -> new BucketItem(FluidRegistry.CLEAN_WATER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> FISH_OIL_BUCKET = ITEMS.register("fish_oil_bucket", () -> new BucketItem(FluidRegistry.FISH_OIL_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> PLANT_TONIC_BUCKET = ITEMS.register("plant_tonic_bucket", () -> new Item(new Item.Properties().stacksTo(1)));

	public static final DeferredItem<Item> SWAMP_HAG_SPAWN_EGG = ITEMS.register("swamp_hag_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.SWAMP_HAG, 0x5E4E2E, 0x18461A, new Item.Properties()));
	public static final DeferredItem<Item> GECKO_SPAWN_EGG = ITEMS.register("gecko_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.GECKO, 0xdc7202, 0x05e290, new Item.Properties()));
	public static final DeferredItem<Item> WIGHT_SPAWN_EGG = ITEMS.register("wight_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.WIGHT, 0x7d8378, 0x07190a, new Item.Properties()));

	public static final DeferredItem<Item> AMATE_MAP = ITEMS.register("amate_map", () -> new EmptyAmateMapItem(new Item.Properties()));
	public static final DeferredItem<Item> FILLED_AMATE_MAP = ITEMS.register("filled_amate_map", () -> new AmateMapItem(new Item.Properties()));
	public static final DeferredItem<Item> RECORD_ASTATOS = ITEMS.register("astatos", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.ASTATOS)));
	public static final DeferredItem<Item> RECORD_BETWEEN_YOU_AND_ME = ITEMS.register("between_you_and_me", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.BETWEEN_YOU_AND_ME)));
	public static final DeferredItem<Item> RECORD_CHRISTMAS_ON_THE_MARSH = ITEMS.register("christmas_on_the_marsh", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.CHRISTMAS_ON_THE_MARSH)));
	public static final DeferredItem<Item> RECORD_THE_EXPLORER = ITEMS.register("the_explorer", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.THE_EXPLORER)));
	public static final DeferredItem<Item> RECORD_HAG_DANCE = ITEMS.register("hag_dance", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.HAG_DANCE)));
	public static final DeferredItem<Item> RECORD_LONELY_FIRE = ITEMS.register("lonely_fire", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.LONELY_FIRE)));
	public static final DeferredItem<Item> MYSTERIOUS_RECORD = ITEMS.register("mysterious_record", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.MYSTERIOUS_RECORD)));
	public static final DeferredItem<Item> RECORD_ANCIENT = ITEMS.register("ancient", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.ANCIENT)));
	public static final DeferredItem<Item> RECORD_BENEATH_A_GREEN_SKY = ITEMS.register("beneath_a_green_sky", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.BENEATH_A_GREEN_SKY)));
	public static final DeferredItem<Item> RECORD_DJ_WIGHTS_MIXTAPE = ITEMS.register("dj_wights_mixtape", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.DJ_WIGHTS_MIXTAPE)));
	public static final DeferredItem<Item> RECORD_ONWARDS = ITEMS.register("onwards", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.ONWARDS)));
	public static final DeferredItem<Item> RECORD_STUCK_IN_THE_MUD = ITEMS.register("stuck_in_the_mud", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.STUCK_IN_THE_MUD)));
	public static final DeferredItem<Item> RECORD_WANDERING_WISPS = ITEMS.register("wandering_wisps", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.WANDERING_WISPS)));
	public static final DeferredItem<Item> RECORD_WATERLOGGED = ITEMS.register("waterlogged", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.WATERLOGGED)));
	public static final DeferredItem<Item> RECORD_DEEP_WATER_THEME = ITEMS.register("deep_water_theme", () -> new Item(new Item.Properties().jukeboxPlayable(MusicRegistry.DEEP_WATER_THEME)));

}