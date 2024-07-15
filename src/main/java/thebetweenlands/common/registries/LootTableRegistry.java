package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import thebetweenlands.common.TheBetweenlands;

import java.util.HashSet;
import java.util.Set;

public class LootTableRegistry {

	private static final Set<ResourceKey<LootTable>> BUILTIN_TABLES = new HashSet<>();

	//MISC
	public static final ResourceKey<LootTable> MUSIC_DISC = register("gameplay/music_disc");
	public static final ResourceKey<LootTable> SCROLL = register("gameplay/animator/scroll");
	public static final ResourceKey<LootTable> FABRICATED_SCROLL = register("gameplay/animator/fabricated_scroll");
	public static final ResourceKey<LootTable> PRESENT = register("gameplay/present_loot");
	public static final ResourceKey<LootTable> PUFFSHROOM = register("gameplay/puffshroom");
	public static final ResourceKey<LootTable> LAKE_CAVERN_SIMULACRUM_OFFERINGS = register("gameplay/lake_cavern_simulacrum_offerings");
	public static final ResourceKey<LootTable> DEEPMAN_SIMULACRUM_OFFERINGS = register("gameplay/deepman_simulacrum_offerings");
	public static final ResourceKey<LootTable> ROOTMAN_SIMULACRUM_OFFERINGS = register("gameplay/rootman_simulacrum_offerings");

	//LOOT INVENTORIES
	//Misc
	public static final ResourceKey<LootTable> CAVE_POT = register("pots/cave_pot");
	public static final ResourceKey<LootTable> SLUDGE_PLAINS_RUINS_URN = register("urns/sludge_plains_ruins_urn");
	public static final ResourceKey<LootTable> MARSH_RUINS_POT = register("chests/marsh_ruins_pot");
	public static final ResourceKey<LootTable> SPAWNER_CHEST = register("chests/spawner_chest");
	public static final ResourceKey<LootTable> IDOL_HEADS_CHEST = register("chests/idol_heads_chest");
	public static final ResourceKey<LootTable> TAR_POOL_POT = register("pots/tar_pool_pot");
	public static final ResourceKey<LootTable> UNDERGROUND_RUINS_POT = register("pots/underground_ruins_pot");
	public static final ResourceKey<LootTable> UNDERWATER_RUINS_POT = register("pots/underwater_ruins_pot");
	//Sludge worm dungeon
	public static final ResourceKey<LootTable> SLUDGE_WORM_DUNGEON_CHEST = register("chests/sludge_worm_dungeon_chest");
	public static final ResourceKey<LootTable> SLUDGE_WORM_DUNGEON_URN = register("urns/sludge_worm_dungeon_urn");
	public static final ResourceKey<LootTable> SLUDGE_WORM_DUNGEON_BARRISHEE_CHEST = register("chests/sludge_worm_dungeon_barrishee_chest");
	public static final ResourceKey<LootTable> SLUDGE_WORM_DUNGEON_CRYPT_URN = register("urns/sludge_worm_dungeon_crypt_urn");
	public static final ResourceKey<LootTable> SLUDGE_WORM_DUNGEON_ITEM_SHELF = register("urns/sludge_worm_dungeon_item_shelf");
	//Cragrock tower
	public static final ResourceKey<LootTable> CRAGROCK_TOWER_CHEST = register("chests/cragrock_tower_chest");
	public static final ResourceKey<LootTable> CRAGROCK_TOWER_POT = register("pots/cragrock_tower_pot");
	//Wight fortress
	public static final ResourceKey<LootTable> WIGHT_FORTRESS_CHEST = register("chests/wight_fortress_chest");
	public static final ResourceKey<LootTable> WIGHT_FORTRESS_POT = register("pots/wight_fortress_pot");
	//Chiromaw Nest
	public static final ResourceKey<LootTable> CHIROMAW_NEST_SCATTERED_LOOT = register("gameplay/chiromaw_nest_scattered_loot");
	//Water Filter
	public static final ResourceKey<LootTable> FILTERED_STAGNANT_WATER = register("gameplay/filtered_stagnant_water");
	public static final ResourceKey<LootTable> FILTERED_SWAMP_WATER = register("gameplay/filtered_swamp_water");

	private static ResourceKey<LootTable> register(String id) {
		return register(ResourceKey.create(Registries.LOOT_TABLE, TheBetweenlands.prefix(id)));
	}

	private static ResourceKey<LootTable> register(ResourceKey<LootTable> id) {
		if (BUILTIN_TABLES.add(id)) {
			return id;
		} else {
			throw new IllegalArgumentException(id + " is already a registered built-in loot table");
		}
	}
}
