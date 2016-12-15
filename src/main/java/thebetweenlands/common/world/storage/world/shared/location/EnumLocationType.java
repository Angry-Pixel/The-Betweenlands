package thebetweenlands.common.world.storage.world.shared.location;

public enum EnumLocationType {
	NONE("none"), RUINS("ruins"), HUT("hut"), SHACK("shack"), WIGHT_TOWER("wightTower"), DUNGEON("dungeon"), GIANT_TREE("giantTree");

	public static EnumLocationType[] TYPES = EnumLocationType.values();

	public final String name;
	private EnumLocationType(String name) {
		this.name = name;
	}
	public static EnumLocationType fromName(String name) {
		for(EnumLocationType type : TYPES){
			if(type.name.equals(name))
				return type;
		}
		return NONE;
	}
}