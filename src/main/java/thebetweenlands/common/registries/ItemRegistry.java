package thebetweenlands.common.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.ItemAmateMap;
import thebetweenlands.common.items.ItemEmptyAmateMap;

public class ItemRegistry {

	// Item list
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheBetweenlands.ID);
	
	// Item inits
	
	
	// Bucket inits
	public static final DeferredItem<Item> SWAMP_WATER_BUCKET = ITEMS.register("swamp_water_bucket", () -> new BucketItem(() -> FluidRegistry.SWAMP_WATER_STILL.get(), new Item.Properties().stacksTo(1)));
	
	// Blocks
	// - Temp
	public static final DeferredItem<Item> PORTAL = ITEMS.register("portal", () -> new BlockItem(BlockRegistry.PORTAL.get(), new Item.Properties()));

	//Spawn Eggs
	public static final DeferredItem<Item> SWAMP_HAG_SPAWN_EGG = ITEMS.register("swamp_hag_spawn_egg", () -> new DeferredSpawnEggItem( () -> EntityRegistry.SWAMP_HAG.get(), 0x5E4E2E, 0x18461A, new Item.Properties()));
	public static final DeferredItem<Item> GECKO_SPAWN_EGG = ITEMS.register("gecko_spawn_egg", () -> new DeferredSpawnEggItem( () -> EntityRegistry.GECKO.get(), 0xdc7202, 0x05e290, new Item.Properties()));
	public static final DeferredItem<Item> WIGHT_SPAWN_EGG = ITEMS.register("wight_spawn_egg", () -> new DeferredSpawnEggItem( () -> EntityRegistry.WIGHT.get(), 0x7d8378, 0x07190a, new Item.Properties()));

	// Betweenlands Special
	public static final DeferredItem<Item> EMPTY_AMATE_MAP = ITEMS.register("empty_amate_map", () -> new ItemEmptyAmateMap(new Item.Properties()));
	public static final DeferredItem<Item> USED_AMATE_MAP = ITEMS.register("used_amate_map", () -> new ItemAmateMap(new Item.Properties()));
	public static final DeferredItem<Item> RECORD_ASTATOS = ITEMS.register("astatos", () -> new RecordItem(0, () -> SoundRegistry.RECORD_ASTATOS.get(), new Item.Properties()));
}