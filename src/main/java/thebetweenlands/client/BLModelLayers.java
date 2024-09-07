package thebetweenlands.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import thebetweenlands.common.TheBetweenlands;

public class BLModelLayers {

	public static final ModelLayerLocation ANADIA = register("anadia");
	public static final ModelLayerLocation BUBBLER_CRAB = register("bubbler_crab");
	public static final ModelLayerLocation FISH_HOOK = register("fish_hook");
	public static final ModelLayerLocation SILT_CRAB = register("silt_crab");

	public static final ModelLayerLocation ALCOVE = register("alcove");
	public static final ModelLayerLocation ALEMBIC = register("alembic");
	public static final ModelLayerLocation ANIMATOR = register("animator");
	public static final ModelLayerLocation BARREL = register("barrel");
	public static final ModelLayerLocation CENSER = register("censer");
	public static final ModelLayerLocation COMPOST_BIN = register("compost_bin");
	public static final ModelLayerLocation CRAB_POT = register("crab_pot");
	public static final ModelLayerLocation CRAB_POT_FILTER = register("crab_pot_filter");
	public static final ModelLayerLocation DEEPMAN_SIMULACRUM_1 = register("deepman_simulacrum_1");
	public static final ModelLayerLocation DEEPMAN_SIMULACRUM_2 = register("deepman_simulacrum_2");
	public static final ModelLayerLocation DEEPMAN_SIMULACRUM_3 = register("deepman_simulacrum_3");
	public static final ModelLayerLocation DRUID_ALTAR = register("druid_altar");
	public static final ModelLayerLocation DRUID_STONES = register("druid_stones");
	public static final ModelLayerLocation FISH_TRIMMING_TABLE = register("fish_trimming_table");
	public static final ModelLayerLocation FISHING_TACKLE_BOX = register("fishing_tackle_box");
	public static final ModelLayerLocation GECKO_CAGE = register("gecko_cage");
	public static final ModelLayerLocation GLASS_JAR = register("glass_jar");
	public static final ModelLayerLocation HANGING_STEEPING_POT = register("hanging_steeping_pot");
	public static final ModelLayerLocation ITEM_CAGE = register("item_cage");
	public static final ModelLayerLocation LAKE_CAVERN_SIMULACRUM_1 = register("lake_cavern_simulacrum_1");
	public static final ModelLayerLocation LAKE_CAVERN_SIMULACRUM_2 = register("lake_cavern_simulacrum_2");
	public static final ModelLayerLocation LAKE_CAVERN_SIMULACRUM_3 = register("lake_cavern_simulacrum_3");
	public static final ModelLayerLocation LOOT_POT_1 = register("loot_pot_1");
	public static final ModelLayerLocation LOOT_POT_2 = register("loot_pot_2");
	public static final ModelLayerLocation LOOT_POT_3 = register("loot_pot_3");
	public static final ModelLayerLocation LOOT_URN_1 = register("loot_urn_1");
	public static final ModelLayerLocation LOOT_URN_2 = register("loot_urn_2");
	public static final ModelLayerLocation LOOT_URN_3 = register("loot_urn_3");
	public static final ModelLayerLocation MOB_SPAWNER_CRYSTAL = register("mob_spawner_crystal");
	public static final ModelLayerLocation MORTAR = register("mortar");
	public static final ModelLayerLocation MOSS_BED = register("moss_bed");
	public static final ModelLayerLocation MOTH_HOUSE = register("mouth_house");
	public static final ModelLayerLocation OFFERING_TABLE = register("offering_table");
	public static final ModelLayerLocation PUFFSHROOM = register("puffshroom");
	public static final ModelLayerLocation PURIFIER = register("purifier");
	public static final ModelLayerLocation ROOTMAN_SIMULACRUM_1 = register("rootman_simulacrum_1");
	public static final ModelLayerLocation ROOTMAN_SIMULACRUM_2 = register("rootman_simulacrum_2");
	public static final ModelLayerLocation ROOTMAN_SIMULACRUM_3 = register("rootman_simulacrum_3");
	public static final ModelLayerLocation SMOKING_RACK = register("smoking_rack");
	public static final ModelLayerLocation SPIKE_BLOCK = register("spike_block");
	public static final ModelLayerLocation SPOOP = register("spoop");
	public static final ModelLayerLocation STEEPING_POT = register("steeping_pot");
	public static final ModelLayerLocation WAYSTONE = register("waystone");
	public static final ModelLayerLocation WIND_CHIME = register("wind_chime");

	private static ModelLayerLocation register(String name) {
		return register(name, "main");
	}

	private static ModelLayerLocation register(String name, String layer) {
		return new ModelLayerLocation(TheBetweenlands.prefix(name), layer);
	}
}
