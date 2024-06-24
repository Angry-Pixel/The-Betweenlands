package thebetweenlands.common.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.AmateMapItem;
import thebetweenlands.common.items.EmptyAmateMapItem;

public class ItemRegistry {

	// Item list
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheBetweenlands.ID);

	// Item inits


	// Bucket inits
	public static final DeferredItem<Item> SWAMP_WATER_BUCKET = ITEMS.register("swamp_water_bucket", () -> new BucketItem(FluidRegistry.SWAMP_WATER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> STAGNANT_WATER_BUCKET = ITEMS.register("stagnant_water_bucket", () -> new BucketItem(FluidRegistry.STAGNANT_WATER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> TAR_BUCKET = ITEMS.register("tar_bucket", () -> new BucketItem(FluidRegistry.TAR_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> RUBBER_BUCKET = ITEMS.register("rubber_bucket", () -> new BucketItem(FluidRegistry.RUBBER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> DYE_BUCKET = ITEMS.register("dye_bucket", () -> new BucketItem(FluidRegistry.DYE_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> BREW_BUCKET = ITEMS.register("brew_bucket", () -> new BucketItem(FluidRegistry.BREW_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> CLEAN_WATER_BUCKET = ITEMS.register("clean_water_bucket", () -> new BucketItem(FluidRegistry.CLEAN_WATER_STILL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredItem<Item> FISH_OIL_BUCKET = ITEMS.register("fish_oil_bucket", () -> new BucketItem(FluidRegistry.FISH_OIL_STILL.get(), new Item.Properties().stacksTo(1)));

	// Blocks
	// - Temp
	public static final DeferredItem<Item> PORTAL = ITEMS.register("portal", () -> new BlockItem(BlockRegistry.PORTAL.get(), new Item.Properties()));

	//Spawn Eggs
	public static final DeferredItem<Item> SWAMP_HAG_SPAWN_EGG = ITEMS.register("swamp_hag_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.SWAMP_HAG, 0x5E4E2E, 0x18461A, new Item.Properties()));
	public static final DeferredItem<Item> GECKO_SPAWN_EGG = ITEMS.register("gecko_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.GECKO, 0xdc7202, 0x05e290, new Item.Properties()));
	public static final DeferredItem<Item> WIGHT_SPAWN_EGG = ITEMS.register("wight_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.WIGHT, 0x7d8378, 0x07190a, new Item.Properties()));

	// Betweenlands Special
	public static final DeferredItem<Item> AMATE_MAP = ITEMS.register("amate_map", () -> new EmptyAmateMapItem(new Item.Properties()));
	public static final DeferredItem<Item> FILLED_AMATE_MAP = ITEMS.register("filled_amate_map", () -> new AmateMapItem(new Item.Properties()));
	public static final DeferredItem<Item> RECORD_ASTATOS = ITEMS.register("astatos", () -> new RecordItem(0, () -> SoundRegistry.RECORD_ASTATOS.get(), new Item.Properties()));
}