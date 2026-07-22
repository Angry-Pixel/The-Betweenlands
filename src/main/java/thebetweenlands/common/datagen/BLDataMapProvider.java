package thebetweenlands.common.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datamap.block.WaterPlant;
import thebetweenlands.common.datamap.entity.AmuletSpawn;
import thebetweenlands.common.datamap.item.AnimatorFuel;
import thebetweenlands.common.datamap.item.CompostableItem;
import thebetweenlands.common.datamap.item.DecayFood;
import thebetweenlands.common.datamap.item.FluxMultiplier;
import thebetweenlands.common.datamap.item.LightningConversion;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.spawning.BaseSpawnProperties;
import thebetweenlands.common.world.spawning.BiomeSpawnerList;
import thebetweenlands.common.world.spawning.BiomeSpawnerZone;
import thebetweenlands.common.world.spawning.CaveSpawnEntry;
import thebetweenlands.common.world.spawning.PitstoneCaveSpawnEntry;
import thebetweenlands.common.world.spawning.SkySpawnEntry;
import thebetweenlands.common.world.spawning.SurfaceSpawnEntry;
import thebetweenlands.common.world.spawning.TreeSpawnEntry;

public class BLDataMapProvider extends DataMapProvider {

	public BLDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void gather(HolderLookup.Provider provider) {
		var fuelMap = this.builder(NeoForgeDataMaps.FURNACE_FUELS);
		fuelMap.add(ItemRegistry.WEEDWOOD_STICK, new FurnaceFuel(100), false);
		fuelMap.add(ItemRegistry.SULFUR, new FurnaceFuel(1600), false);
		fuelMap.add(ItemRegistry.UNDYING_EMBERS, new FurnaceFuel(20000), false);
		fuelMap.add(BlockRegistry.HEARTHGROVE_LOG.getId(), new FurnaceFuel(800), false);
		fuelMap.add(BlockRegistry.HEARTHGROVE_BARK.getId(), new FurnaceFuel(800), false);
		fuelMap.add(BlockRegistry.TARRED_HEARTHGROVE_LOG.getId(), new FurnaceFuel(4800), false);
		fuelMap.add(BlockRegistry.TARRED_HEARTHGROVE_BARK.getId(), new FurnaceFuel(4800), false);
		fuelMap.add(BlockRegistry.MOTH_HOUSE.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.FISH_TRIMMING_TABLE.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.SMOKING_RACK.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.FISHING_TACKLE_BOX.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.ROOTMAN_SIMULACRUM_1.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.ROOTMAN_SIMULACRUM_2.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.ROOTMAN_SIMULACRUM_3.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.REPELLER.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CHIP_PATH.getId(), new FurnaceFuel(100), false);
		fuelMap.add(BlockRegistry.WALKWAY.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.WEEDWOOD_BARREL.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.GECKO_CAGE.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.WEEDWOOD_JUKEBOX.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.COMPOST_BIN.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_BOTTOM.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_BOTTOM_LEFT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_BOTTOM_RIGHT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_LEFT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_RIGHT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_TOP.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_TOP_LEFT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PORTAL_FRAME_TOP_RIGHT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.WEEDWOOD_CRAFTING_TABLE.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.PURIFIER.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.HOLLOW_LOG.getId(), new FurnaceFuel(200), false);
		fuelMap.add(BlockRegistry.GIANT_ROOT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.ROOT.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_1.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_2.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_3.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_4.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_5.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_6.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_7.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_8.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_9.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_10.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_11.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_12.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_13.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_14.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_15.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.CARVED_ROTTEN_BARK_16.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.WOODEN_SUPPORT_BEAM_1.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.WOODEN_SUPPORT_BEAM_2.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.WOODEN_SUPPORT_BEAM_3.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.ITEM_SHELF.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.FINE_LATTICE.getId(), new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.LATTICE.getId(), new FurnaceFuel(300), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_SWORD, new FurnaceFuel(200), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_PICKAXE, new FurnaceFuel(200), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_AXE, new FurnaceFuel(200), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_SHOVEL, new FurnaceFuel(200), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_FISHING_ROD, new FurnaceFuel(300), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_SHIELD, new FurnaceFuel(200), false);
		fuelMap.add(ItemRegistry.TANGLED_ROOT, new FurnaceFuel(400), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_BOWL, new FurnaceFuel(100), false);
		fuelMap.add(ItemRegistry.WEEDWOOD_BOW, new FurnaceFuel(300), false);
		fuelMap.add(BlockRegistry.WEEDWOOD_CHEST.getId(), new FurnaceFuel(300), false);

		var amuletMap = this.builder(DataMapRegistry.AMULET_SPAWNS);
		amuletMap.add(EntityRegistry.SWAMP_HAG, new AmuletSpawn(40), false);
		amuletMap.add(EntityRegistry.PEAT_MUMMY, new AmuletSpawn(40), false);
		amuletMap.add(EntityRegistry.TAR_BEAST, new AmuletSpawn(40), false);
		amuletMap.add(EntityRegistry.WIGHT, new AmuletSpawn(40), false);
		amuletMap.add(EntityRegistry.CRYPT_CRAWLER, new AmuletSpawn(40), false);
		amuletMap.add(EntityRegistry.BIPED_CRYPT_CRAWLER, new AmuletSpawn(40), false);

		var animatorFuel = this.builder(DataMapRegistry.ANIMATOR_FUEL);
		animatorFuel.add(ItemRegistry.SULFUR, new AnimatorFuel(42, 1), false);
		
		var decayMap = this.builder(DataMapRegistry.DECAY_FOOD);
		decayMap.add(ItemRegistry.SAP_BALL, new DecayFood(2, 0.0F), false);
		decayMap.add(ItemRegistry.FORBIDDEN_FIG, new DecayFood(20, 0.2F), false);
		decayMap.add(ItemRegistry.SAP_JELLO, new DecayFood(4, 0.2F), false);

		var fluxMap = this.builder(DataMapRegistry.FLUX_MULTIPLIER);
		fluxMap.add(Tags.Items.ORES, new FluxMultiplier(0.33F, 2), false);

		var lightningConversion = this.builder(DataMapRegistry.LIGHTNING_CONVERSION);
		lightningConversion.add(ItemTags.ARROWS, new LightningConversion(ItemRegistry.SHOCK_ARROW, 0.2F, 5, true), false);
		lightningConversion.add(ItemRegistry.CHIROBARB_ERUPTER, new LightningConversion(ItemRegistry.CHIROBARB_SHOCK_ERUPTER, 1.0F, 1, false), false);

		var compostMap = this.builder(DataMapRegistry.COMPOSTABLE);
		compostMap.add(ItemRegistry.DRY_BARK, new CompostableItem(30, 12000), false);
		compostMap.add(BlockRegistry.HOLLOW_LOG.asItem().builtInRegistryHolder(), new CompostableItem(25, 12000), false);
		compostMap.add(BlockRegistry.ROTTEN_BARK.asItem().builtInRegistryHolder(), new CompostableItem(25, 12000), false);
		compostMap.add(BlockRegistry.SUNDEW.asItem().builtInRegistryHolder(), new CompostableItem(10, 8000), false);
		compostMap.add(BlockRegistry.TALL_SWAMP_GRASS.asItem().builtInRegistryHolder(), new CompostableItem(6, 10000), false);
		compostMap.add(BlockRegistry.PHRAGMITES.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.TALL_CATTAIL.asItem().builtInRegistryHolder(), new CompostableItem(6, 10000), false);
		compostMap.add(BlockRegistry.CARDINAL_FLOWER.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BROOMSEDGE.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.WEEPING_BLUE.asItem().builtInRegistryHolder(), new CompostableItem(15, 11000), false);
		compostMap.add(BlockRegistry.PITCHER_PLANT.asItem().builtInRegistryHolder(), new CompostableItem(12, 11000), false);
		compostMap.add(BlockRegistry.BOG_BEAN_FLOWER.asItem().builtInRegistryHolder(), new CompostableItem(6, 8000), false);
		compostMap.add(BlockRegistry.GOLDEN_CLUB_FLOWER.asItem().builtInRegistryHolder(), new CompostableItem(6, 8000), false);
		compostMap.add(BlockRegistry.MARSH_MARIGOLD_FLOWER.asItem().builtInRegistryHolder(), new CompostableItem(6, 8000), false);
		compostMap.add(BlockRegistry.SWAMP_KELP.asItem().builtInRegistryHolder(), new CompostableItem(3, 5000), false);
		compostMap.add(BlockRegistry.WATER_WEEDS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BLADDERWORT_FLOWER.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BLADDERWORT_STALK.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.ROOT.asItem().builtInRegistryHolder(), new CompostableItem(20, 12000), false);
		compostMap.add(BlockRegistry.BLACK_HAT_MUSHROOM.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.FLATHEAD_MUSHROOM.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BULB_CAPPED_MUSHROOM.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.EDGE_LEAF.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.EDGE_MOSS.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.EDGE_SHROOM.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.SWAMP_PLANT.asItem().builtInRegistryHolder(), new CompostableItem(4, 6000), false);
		compostMap.add(BlockRegistry.VENUS_FLY_TRAP.asItem().builtInRegistryHolder(), new CompostableItem(12, 10000), false);
		compostMap.add(BlockRegistry.VOLARPAD.asItem().builtInRegistryHolder(), new CompostableItem(15, 11000), false);
		compostMap.add(BlockRegistry.WEEDWOOD_BUSH.asItem().builtInRegistryHolder(), new CompostableItem(20, 12000), false);
		compostMap.add(BlockRegistry.THORNS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.POISON_IVY.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.MOSS.asItem().builtInRegistryHolder(), new CompostableItem(6, 9000), false);
		compostMap.add(BlockRegistry.DEAD_MOSS.asItem().builtInRegistryHolder(), new CompostableItem(6, 3000), false);
		compostMap.add(BlockRegistry.LICHEN.asItem().builtInRegistryHolder(), new CompostableItem(6, 9000), false);
		compostMap.add(BlockRegistry.DEAD_LICHEN.asItem().builtInRegistryHolder(), new CompostableItem(6, 3000), false);
		compostMap.add(BlockRegistry.CAVE_MOSS.asItem().builtInRegistryHolder(), new CompostableItem(6, 9000), false);
		compostMap.add(BlockRegistry.CAVE_GRASS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.CATTAIL.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.SHORT_SWAMP_GRASS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.SHOOTS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.FLOWERED_NETTLE.asItem().builtInRegistryHolder(), new CompostableItem(6, 9000), false);
		compostMap.add(BlockRegistry.NETTLE.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.ARROW_ARUM.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BUTTON_BUSH.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.MARSH_HIBISCUS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.PICKERELWEED.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.SOFT_RUSH.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.MARSH_MALLOW.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BLUE_IRIS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.COPPER_IRIS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BLUE_EYED_GRASS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.MILKWEED.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BONESET.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.BOTTLE_BRUSH_GRASS.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.SLUDGECREEP.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.TALL_SLUDGECREEP.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.ROTBULB.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.PALE_GRASS.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.STRING_ROOTS.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.CRYPTWEED.asItem().builtInRegistryHolder(), new CompostableItem(8, 6000), false);
		compostMap.add(BlockRegistry.DEAD_WEEDWOOD_BUSH.asItem().builtInRegistryHolder(), new CompostableItem(5, 8000), false);
		compostMap.add(BlockRegistry.HANGER.asItem().builtInRegistryHolder(), new CompostableItem(3, 5000), false);
		compostMap.add(BlockRegistry.SEEDED_HANGER.asItem().builtInRegistryHolder(), new CompostableItem(4, 6000), false);
		compostMap.add(BlockRegistry.ALGAE.asItem().builtInRegistryHolder(), new CompostableItem(3, 5000), false);
		compostMap.add(BlockRegistry.ROPE.asItem().builtInRegistryHolder(), new CompostableItem(3, 5000), false);
		compostMap.add(BlockRegistry.MIRE_CORAL.asItem().builtInRegistryHolder(), new CompostableItem(6, 9000), false);
		compostMap.add(BlockRegistry.DEEP_WATER_CORAL.asItem().builtInRegistryHolder(), new CompostableItem(6, 9000), false);
		compostMap.add(BlockRegistry.RUBBER_SAPLING.asItem().builtInRegistryHolder(), new CompostableItem(15, 11000), false);
		compostMap.add(BlockRegistry.SAP_SAPLING.asItem().builtInRegistryHolder(), new CompostableItem(15, 11000), false);
		compostMap.add(BlockRegistry.WEEDWOOD_SAPLING.asItem().builtInRegistryHolder(), new CompostableItem(15, 11000), false);
		compostMap.add(BlockRegistry.HEARTHGROVE_SAPLING.asItem().builtInRegistryHolder(), new CompostableItem(15, 11000), false);
		compostMap.add(BlockRegistry.NIBBLETWIG_SAPLING.asItem().builtInRegistryHolder(), new CompostableItem(15, 11000), false);
		compostMap.add(BlockRegistry.SPIRIT_TREE_SAPLING.asItem().builtInRegistryHolder(), new CompostableItem(25, 11000), false);
		compostMap.add(BlockRegistry.RUBBER_TREE_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(4, 11000), false);
		compostMap.add(BlockRegistry.SAP_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(4, 11000), false);
		compostMap.add(BlockRegistry.WEEDWOOD_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(4, 11000), false);
		compostMap.add(BlockRegistry.HEARTHGROVE_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(4, 11000), false);
		compostMap.add(BlockRegistry.NIBBLETWIG_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(4, 11000), false);
		compostMap.add(BlockRegistry.TOP_SPIRIT_TREE_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(6, 11000), false);
		compostMap.add(BlockRegistry.MIDDLE_SPIRIT_TREE_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(6, 11000), false);
		compostMap.add(BlockRegistry.BOTTOM_SPIRIT_TREE_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(6, 11000), false);
		compostMap.add(BlockRegistry.FALLEN_LEAVES.asItem().builtInRegistryHolder(), new CompostableItem(4, 11000), false);
		compostMap.add(BlockRegistry.SWAMP_REED.asItem().builtInRegistryHolder(), new CompostableItem(3, 5000), false);
		compostMap.add(ItemRegistry.DRIED_SWAMP_REED, new CompostableItem(3, 5000), false);
		//compostMap.add(ItemRegistry.SWAMP_REED_ROPE, new CompostableItem(5, 8000), false);
		compostMap.add(ItemRegistry.TANGLED_ROOT, new CompostableItem(5, 8000), false);
		compostMap.add(ItemRegistry.FLATHEAD_MUSHROOM, new CompostableItem(5, 8000), false);
		compostMap.add(ItemRegistry.BLACK_HAT_MUSHROOM, new CompostableItem(5, 8000), false);
		compostMap.add(ItemRegistry.BULB_CAPPED_MUSHROOM, new CompostableItem(5, 8000), false);
		compostMap.add(ItemRegistry.YELLOW_DOTTED_FUNGUS, new CompostableItem(12, 10000), false);
		compostMap.add(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS, new CompostableItem(10, 6000), false);
		compostMap.add(ItemRegistry.SPORES, new CompostableItem(10, 6000), false);
		compostMap.add(ItemRegistry.ASPECTRUS_SEEDS, new CompostableItem(10, 6000), false);
		compostMap.add(ItemRegistry.GROUND_LEAF, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_CATTAIL, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_SWAMP_GRASS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_SHOOTS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_ARROW_ARUM, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BUTTON_BUSH, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_MARSH_HIBUSCUS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_PICKERELWEED, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_SOFT_RUSH, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_MARSH_MALLOW, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_MILKWEED, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BLUE_IRIS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_COPPER_IRIS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BLUE_EYED_GRASS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BONESET, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BOTTLE_BRUSH_GRASS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_WEEDWOOD_BARK, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_DRIED_SWAMP_REED, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_ALGAE, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_ANGLER_TOOTH, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BLACKHAT_MUSHROOM, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_CRIMSON_SNAIL_SHELL, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BOG_BEAN, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BROOMSEDGE, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_CARDINAL_FLOWER, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_CAVE_GRASS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_CAVE_MOSS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_DEEP_WATER_CORAL, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_FLATHEAD_MUSHROOM, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_GOLDEN_CLUB, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_HANGER, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_LICHEN, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_MARSH_MARIGOLD, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_MIRE_CORAL, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_OCHRE_SNAIL_SHELL, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_MOSS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_NETTLE, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_PHRAGMITES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_SLUDGECREEP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_SUNDEW, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_SWAMP_KELP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_ROOTS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_PITCHER_PLANT, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_WATER_WEEDS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_VENUS_FLY_TRAP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_VOLARPAD, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_THORNS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_POISON_IVY, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BLADDERWORT_FLOWER, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_BLADDERWORT_STALK, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_EDGE_SHROOM, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_EDGE_MOSS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_EDGE_LEAF, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_ROTBULB, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_PALE_GRASS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_STRING_ROOTS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GROUND_CRYPTWEED, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.LEAF, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.ALGAE_CLUMP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.ARROW_ARUM_LEAF, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BLUE_EYED_GRASS_FLOWERS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BLUE_IRIS_PETAL, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.MIRE_CORAL_STALK, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.DEEP_WATER_CORAL_STALK, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BOG_BEAN_FLOWER_DROP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BONESET_FLOWERS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BOTTLE_BRUSH_GRASS_BLADES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BROOMSEDGE_LEAVES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BUTTON_BUSH_FLOWERS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.CARDINAL_FLOWER_PETALS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.CATTAIL_HEAD, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.CAVE_GRASS_BLADES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.COPPER_IRIS_PETALS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.GOLDEN_CLUB_FLOWERS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.LICHEN_CLUMP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.MARSH_HIBISCUS_FLOWER, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.MARSH_MALLOW_FLOWER, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.MARSH_MARIGOLD_FLOWER_DROP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.NETTLE_LEAF, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.PHRAGMITE_STEMS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.PICKERELWEED_FLOWER, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.SHOOT_LEAVES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.SLUDGECREEP_LEAVES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.SOFT_RUSH_LEAVES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.SUNDEW_HEAD, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.SWAMP_GRASS_BLADES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.CAVE_MOSS_CLUMP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.MOSS_CLUMP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.MILKWEED_FLOWER, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.HANGER_DROP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.PITCHER_PLANT_TRAP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.WATER_WEEDS_DROP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.VENUS_FLY_TRAP_HEAD, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.VOLARPAD_LEAF, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.THORN_BRANCH, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.POISON_IVY_LEAF, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BLADDERWORT_STALK_DROP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.BLADDERWORT_FLOWER_DROP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.EDGE_SHROOM_GILLS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.EDGE_MOSS_CLUMP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.EDGE_LEAF_DROP, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.ROTBULB_STALK, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.PALE_GRASS_BLADES, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.STRING_ROOT_FIBERS, new CompostableItem(3, 4000), false);
		compostMap.add(ItemRegistry.CRYPTWEED_BLADES, new CompostableItem(3, 4000), false);
		
		var gunkMap = this.builder(DataMapRegistry.WATER_PLANT);
		gunkMap.add(BlockRegistry.ALGAE, new WaterPlant(10.0f, 0.05f), false);
		
		// Custom Mob Spawning
		var biome_spawns = builder(DataMapRegistry.BIOME_SPAWNS);
		// order of entries and meaning
		// EntityType<?> mobType,
		// int weight,
		// short baseWeight,
		boolean hostile_base = false;
		int minGroupSize_base = 1;
		int maxGroupSize_base = 1;
		// int minHeight,
		// int maxHeight,
		double spawnCheckRadius_base = 24.0D; // Shouldn't be lower than 24 (vanilla) or will explode atm
		double spawnCheckRangeY_base = 6.0D;
		double groupSpawnRadius_base = 6.0D;
		int spawningInterval_base = 0;
		boolean canSpawnOnWater_base = false;
		boolean canSpawnInWater_base = false;
		boolean constantWeight_base = false;

		var patchyIslandsSurface = new BiomeSpawnerZone(new SurfaceSpawnEntry(),
				List.of(
						new BaseSpawnProperties(EntityRegistry.DRAGONFLY.get(), (short) 35, (short) 100, false, 1, 3,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 64.0, spawnCheckRangeY_base, groupSpawnRadius_base, 400, true,
								false, false),

						new BaseSpawnProperties(EntityRegistry.FIREFLY.get(), (short) 60, (short) 100, false, minGroupSize_base, maxGroupSize_base,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32.0D, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, true,
								false, false),

						new BaseSpawnProperties(EntityRegistry.MIRE_SNAIL.get(), (short) 60, (short) 100, false, 1, 3,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32.0D, spawnCheckRangeY_base, groupSpawnRadius_base, 1000, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.FROG.get(), (short) 32, (short) 100, false, 1, 3,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32.0D, spawnCheckRangeY_base, groupSpawnRadius_base, 1000, true,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.FROG.get(), (short) 32, (short) 100, false, 1, 3,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32.0D, spawnCheckRangeY_base, groupSpawnRadius_base, 1000, true,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.GECKO.get(), (short) 40, (short) 100, false, 1, 3,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32.0D, spawnCheckRangeY_base, groupSpawnRadius_base, 600, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.LURKER.get(), (short) 35, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32.0D, spawnCheckRangeY_base, groupSpawnRadius_base, 600, canSpawnOnWater_base,
								true, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.ANGLER.get(), (short) 45, (short) 100, true, 1, 3,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32.0D, spawnCheckRangeY_base, groupSpawnRadius_base, 600, canSpawnOnWater_base,
								true, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.SWAMP_HAG.get(), (short) 90, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, spawnCheckRadius_base, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.WIGHT.get(), (short) 16, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 64, 16, groupSpawnRadius_base, 6000, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.BLOOD_SNAIL.get(), (short) 30, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 64, spawnCheckRangeY_base, groupSpawnRadius_base, 1000, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.LEECH.get(), (short) 35, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 64, spawnCheckRangeY_base, groupSpawnRadius_base, 1000, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.CHIROMAW.get(), (short) 40, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 30, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.SHAMBLER.get(), (short) 30, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, spawnCheckRadius_base, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.ANADIA.get(), (short) 60, (short) 100, false, 1, 5,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, spawnCheckRadius_base, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								true, constantWeight_base),

						// TODO these need surface rules for hearthgrove log blocks
						new BaseSpawnProperties(EntityRegistry.EMBERLING.get(), (short) 20, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						// TODO these need surface rules for silt blocks
						new BaseSpawnProperties(EntityRegistry.SILT_CRAB.get(), (short) 50, (short) 100, true, 2, 8,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 32, 16, groupSpawnRadius_base, 1000, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						// TODO these need spawn rules for direction facing on top of block
						new BaseSpawnProperties(EntityRegistry.GREEBLING.get(), (short) 20, (short) 100, true, 1, 2,
								(int) TheBetweenlands.CAVE_START, (int) TheBetweenlands.LAYER_HEIGHT + 3, 64, spawnCheckRangeY_base, 4, 2400, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base)
						));

		var patchyIslandsCave = new BiomeSpawnerZone(new CaveSpawnEntry(),
				List.of(
						new BaseSpawnProperties(EntityRegistry.OLM.get(), (short) 30, (short) 100, false, 3, 5,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.CAVE_START, 32, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base,
								canSpawnOnWater_base, true, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.ANGLER.get(), (short) 35, (short) 100, true, 1, 3,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.CAVE_START, spawnCheckRadius_base, spawnCheckRangeY_base, groupSpawnRadius_base, 600, canSpawnOnWater_base,
								true, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.WIGHT.get(), (short) 18, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.CAVE_START, 64, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.CHIROMAW.get(), (short) 60, (short) 100, true, 1, 3,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.CAVE_START, spawnCheckRadius_base, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base),

						new BaseSpawnProperties(EntityRegistry.INFESTATION.get(), (short) 50, (short) 100, true, 1, 3,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.CAVE_START, 32, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, true),

						new BaseSpawnProperties(EntityRegistry.CAVE_FISH.get(), (short) 30, (short) 100, false, 1, 3,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.CAVE_START, 32, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base,
								canSpawnOnWater_base, true, constantWeight_base),

						//TODO This needs the multipliers for amplified attributes based on depth on spawning
						new BaseSpawnProperties(EntityRegistry.SWAMP_HAG.get(), (short) 80, (short) 100, true, 1, 3,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.CAVE_START, spawnCheckRadius_base, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base, canSpawnOnWater_base,
								canSpawnInWater_base, constantWeight_base)
						));
		
		var patchyIslandsPitstone = new BiomeSpawnerZone(new PitstoneCaveSpawnEntry(),
				List.of(
						new BaseSpawnProperties(EntityRegistry.STALKER.get(), (short) 13, (short) 100, true, 1, 1,
								(int) TheBetweenlands.CAVE_WATER_HEIGHT, (int) TheBetweenlands.PITSTONE_HEIGHT, 64, 16, groupSpawnRadius_base, 6000,
								canSpawnOnWater_base, canSpawnInWater_base, true)
						));
		
		var patchyIslandsSky = new BiomeSpawnerZone(new SkySpawnEntry(),
				List.of(
						new BaseSpawnProperties(EntityRegistry.CHIROMAW_GREEBLING_RIDER.get(), (short) 20, (short) 100, true, 1, 3,
								(int) TheBetweenlands.LAYER_HEIGHT + 30, (int) TheBetweenlands.LAYER_HEIGHT + 130, 64, 32, groupSpawnRadius_base, 600,
								canSpawnOnWater_base, canSpawnInWater_base, constantWeight_base)
						));
		
		var patchyIslandsTree = new BiomeSpawnerZone(new TreeSpawnEntry(),
				List.of(
						// TODO This needs spawn rules to sometimes spawn riding a gecko (1:10)
						new BaseSpawnProperties(EntityRegistry.SPORELING.get(), (short) 80, (short) 100, true, 2, 5,
								(int) TheBetweenlands.LAYER_HEIGHT, (int) TheBetweenlands.LAYER_HEIGHT + 8, 32, spawnCheckRangeY_base, groupSpawnRadius_base, spawningInterval_base,
								canSpawnOnWater_base, canSpawnInWater_base, constantWeight_base)
						));

	/*	TODO OLD Entries to add for this biome
		entries.add(new BetweenstoneCaveSpawnEntry(21, EntityBoulderSprite.class, EntityBoulderSprite::new, (short) 60).setHostile(true).setSpawnCheckRadius(16.0D).setSpawnCheckRangeY(8));
	 */

		biome_spawns.add(BiomeRegistry.PATCHY_ISLANDS, new BiomeSpawnerList(List.of(patchyIslandsSurface, patchyIslandsCave, patchyIslandsPitstone, patchyIslandsSky, patchyIslandsTree)), false);

	}
}
