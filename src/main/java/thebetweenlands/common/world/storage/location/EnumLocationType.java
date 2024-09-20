package thebetweenlands.common.world.storage.location;

public enum EnumLocationType {
	NONE("none"),
	RUINS("ruins"),
	HUT("hut"),
	SHACK("abandoned_shack"),
	WIGHT_TOWER("wight_tower"),
	CRAGROCK_TOWER("cragrock_tower"),
	GIANT_TREE("giant_tree"),
	IDOL_HEAD("idol_head"),
	SPIRIT_TREE("spirit_tree"),
	WAYSTONE("waystone"),
	SLUDGE_WORM_DUNGEON("sludge_worm_dungeon"),
	FLOATING_ISLAND("floating_island"),
	CHIROMAW_MATRIARCH_NEST("chiromaw_matriarch_nest");

	public static final EnumLocationType[] TYPES = EnumLocationType.values();

	public final String name;

	EnumLocationType(String name) {
		this.name = name;
	}

	public static EnumLocationType fromName(String name) {
		for (EnumLocationType type : TYPES) {
			if (type.name.equals(name))
				return type;
		}
		return NONE;
	}
}