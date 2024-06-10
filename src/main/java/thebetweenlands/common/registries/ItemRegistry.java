package thebetweenlands.common.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.ItemAmateMap;
import thebetweenlands.common.items.ItemEmptyAmateMap;

public class ItemRegistry {

	// Item list
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheBetweenlands.ID);
	
	// Item inits
	
	
	// Bucket inits
	public static final RegistryObject<Item> SWAMP_WATER_BUCKET = ITEMS.register("swamp_water_bucket", () -> new BucketItem(() -> FluidRegistry.SWAMP_WATER_STILL.get(), new Item.Properties().stacksTo(1).tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	
	// Blocks
	// - Landscape
	public static final RegistryObject<Item> MUD = ITEMS.register("mud", () -> new BlockItem(BlockRegistry.MUD.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SILT = ITEMS.register("silt", () -> new BlockItem(BlockRegistry.SILT.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> PEAT = ITEMS.register("peat", () -> new BlockItem(BlockRegistry.PEAT.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SWAMP_DIRT = ITEMS.register("swamp_dirt", () -> new BlockItem(BlockRegistry.SWAMP_DIRT.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> DEAD_SWAMP_GRASS =ITEMS.register("dead_grass", () -> new BlockItem(BlockRegistry.DEAD_SWAMP_GRASS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SWAMP_GRASS = ITEMS.register("swamp_grass", () -> new BlockItem(BlockRegistry.SWAMP_GRASS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> BETWEENSTONE = ITEMS.register("betweenstone", () -> new BlockItem(BlockRegistry.BETWEENSTONE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> PITSTONE = ITEMS.register("pitstone", () -> new BlockItem(BlockRegistry.PITSTONE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> LIMESTONE = ITEMS.register("limestone", () -> new BlockItem(BlockRegistry.LIMESTONE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> CRAGROCK = ITEMS.register("cragrock", () -> new BlockItem(BlockRegistry.CRAGROCK.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> BETWEENLANDS_BEDROCK = ITEMS.register("betweenlands_bedrock", () -> new BlockItem(BlockRegistry.BETWEENLANDS_BEDROCK.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	// - Leaves
	public static final RegistryObject<Item> WEEDWOOD_LEAVES = ITEMS.register("weedwood_leaves", () -> new BlockItem(BlockRegistry.LEAVES_WEEDWOOD_TREE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SAP_LEAVES = ITEMS.register("sap_leaves", () -> new BlockItem(BlockRegistry.LEAVES_SAP_TREE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> RUBBER_LEAVES = ITEMS.register("rubber_leaves", () -> new BlockItem(BlockRegistry.LEAVES_RUBBER_TREE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> NIBBLETWIG_LEAVES = ITEMS.register("nibbletwig_leaves", () -> new BlockItem(BlockRegistry.LEAVES_NIBBLETWIG_TREE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> LEAVES_HEARTHGROVE = ITEMS.register("leaves_hearthgrove", () -> new BlockItem(BlockRegistry.LEAVES_HEARTHGROVE_TREE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	// - Woods
	public static final RegistryObject<Item> WEEDWOOD = ITEMS.register("weedwood", () -> new BlockItem(BlockRegistry.WEEDWOOD.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> WEEDWOOD_LOG = ITEMS.register("weedwood_log", () -> new BlockItem(BlockRegistry.WEEDWOOD_LOG.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> WEEDWOOD_BARK_LOG = ITEMS.register("weedwood_bark_log", () -> new BlockItem(BlockRegistry.WEEDWOOD_BARK_LOG.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SAP_LOG = ITEMS.register("sap_log", () -> new BlockItem(BlockRegistry.SAP_LOG.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SAP_BARK_LOG = ITEMS.register("sap_bark_log", () -> new BlockItem(BlockRegistry.SAP_BARK_LOG.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> RUBBER_LOG = ITEMS.register("rubber_log", () -> new BlockItem(BlockRegistry.RUBBER_LOG.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> LOG_HEARTHGROVE = ITEMS.register("log_hearthgrove", () -> new BlockItem(BlockRegistry.LOG_HEARTHGROVE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> NIBBLETWIG_LOG = ITEMS.register("log_nibbletwig", () -> new BlockItem(BlockRegistry.LOG_NIBBLETWIG.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> GIANT_ROOT = ITEMS.register("giant_root", () -> new BlockItem(BlockRegistry.GIANT_ROOT.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	// - Plants
	public static final RegistryObject<Item> WEEDWOOD_SAPLING = ITEMS.register("weedwood_sapling", () -> new BlockItem(BlockRegistry.WEEDWOOD_SAPLING.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> SAP_SAPLING = ITEMS.register("sap_sapling", () -> new BlockItem(BlockRegistry.SAP_SAPLING.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> RUBBER_SAPLING = ITEMS.register("rubber_sapling", () -> new BlockItem(BlockRegistry.RUBBER_SAPLING.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> NIBBLETWIG_SAPLING = ITEMS.register("nibbletwig_sapling", () -> new BlockItem(BlockRegistry.NIBBLETWIG_SAPLING.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> MOSS = ITEMS.register("moss", () -> new BlockItem(BlockRegistry.MOSS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> BULB_CAPPED_MUSHROOM_CAP = ITEMS.register("bulb_capped_mushroom_cap", () -> new BlockItem(BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> BULB_CAPPED_MUSHROOM_STALK = ITEMS.register("bulb_capped_mushroom_stalk", () -> new BlockItem(BlockRegistry.BULB_CAPPED_MUSHROOM_STALK.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SHELF_FUNGUS = ITEMS.register("shelf_fungus", () -> new BlockItem(BlockRegistry.SHELF_FUNGUS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> THORNS = ITEMS.register("thorns", () -> new BlockItem(BlockRegistry.THORNS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> POISON_IVY = ITEMS.register("poison_ivy", () -> new BlockItem(BlockRegistry.POISON_IVY.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> CAVE_MOSS = ITEMS.register("cave_moss", () -> new BlockItem(BlockRegistry.CAVE_MOSS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> SWAMP_REED = ITEMS.register("swamp_reed", () -> new BlockItem(BlockRegistry.SWAMP_REED.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	public static final RegistryObject<Item> SWAMP_TALLGRASS = ITEMS.register("swamp_tallgrass", () -> new BlockItem(BlockRegistry.SWAMP_TALLGRASS.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_PLANTS)));
	// - Ores
	public static final RegistryObject<Item> SULFUR_ORE = ITEMS.register("sulfur_ore", () -> new BlockItem(BlockRegistry.SULFUR_ORE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SYRMOITE_ORE = ITEMS.register("syrmorite_ore", () -> new BlockItem(BlockRegistry.SYRMORITE_ORE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SLIMY_BONE_ORE = ITEMS.register("slimy_bone_ore", () -> new BlockItem(BlockRegistry.SLIMY_BONE_ORE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> OCTINE_ORE = ITEMS.register("octine_ore", () -> new BlockItem(BlockRegistry.OCTINE_ORE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> SCABYST_ORE = ITEMS.register("scabyst_ore", () -> new BlockItem(BlockRegistry.SCABYST_ORE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
	public static final RegistryObject<Item> VALONITE_ORE = ITEMS.register("valonite_ore", () -> new BlockItem(BlockRegistry.VALONITE_ORE.get(), new Item.Properties().tab(CreativeGroupRegistry.BETWEELANDS_BLOCKS)));
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