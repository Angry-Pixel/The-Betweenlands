package thebetweenlands.common.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.ItemAmateMap;
import thebetweenlands.common.items.ItemEmptyAmateMap;

public class ItemRegistry {

	// Item list
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheBetweenlands.ID);
	
	// Item inits
	
	
	// Bucket inits
	public static final RegistryObject<Item> SWAMP_WATER_BUCKET = ITEMS.register("swamp_water_bucket", () -> new BucketItem(() -> FluidRegistry.SWAMP_WATER_STILL.get(), new Item.Properties().stacksTo(1).tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	
	// Blocks
	// - Temp
	public static final RegistryObject<Item> PORTAL = ITEMS.register("portal", () -> new BlockItem(BlockRegistry.PORTAL.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SWAMP_HAG_SPAWN_EGG = ITEMS.register("swamp_hag_spawn_egg", () -> new ForgeSpawnEggItem( () -> EntityRegistry.SWAMP_HAG.get(), 0x5E4E2E, 0x18461A, new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> GECKO_SPAWN_EGG = ITEMS.register("gecko_spawn_egg", () -> new ForgeSpawnEggItem( () -> EntityRegistry.GECKO.get(), 0xdc7202, 0x05e290, new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> WIGHT_SPAWN_EGG = ITEMS.register("wight_spawn_egg", () -> new ForgeSpawnEggItem( () -> EntityRegistry.WIGHT.get(), 0x7d8378, 0x07190a, new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));

	// Betweenlands Special
	public static final RegistryObject<Item> EMPTY_AMATE_MAP = ITEMS.register("empty_amate_map", () -> new ItemEmptyAmateMap(new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_SPECIAL)));
	public static final RegistryObject<Item> USED_AMATE_MAP = ITEMS.register("used_amate_map", () -> new ItemAmateMap(new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_SPECIAL)));
	public static final RegistryObject<Item> RECORD_ASTATOS = ITEMS.register("astatos", () -> new RecordItem(0, () -> SoundRegistry.RECORD_ASTATOS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_SPECIAL)));

	// Register item list
	public static void register(IEventBus eventBus) {
		
		// Register to betweenlands (if anyone would realy need it its here)
		// Block Items
		
		
		
		
		
		// debug
		//ITEMS.register("debug", () -> new BlockItem(ModBlocks.DEBUG.get(), new Item.Properties().tab(ModCreativeGroup.BETWEELANDS_BLOCKS)));
		
		// Item list register
		ITEMS.register(eventBus);
	}

	public interface IHandRenderCallback {

		/**
		 * A callback to get a custom mesh definition
		 * @return
		 */
		@OnlyIn(Dist.CLIENT)
		ItemInHandRenderer getHandRenderer();

	}
}