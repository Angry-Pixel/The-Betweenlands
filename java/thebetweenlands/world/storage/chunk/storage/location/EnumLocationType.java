package thebetweenlands.world.storage.chunk.storage.location;

public enum EnumLocationType {
	NONE("none"), RUINS("ruins"), HUT("hut"), SHACK("shack"), WIGHT_TOWER("wightTower"), DUNGEON("dungeon");

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