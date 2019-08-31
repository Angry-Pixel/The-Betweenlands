package thebetweenlands.common.world.storage.location;

public enum EnumLocationType {
	NONE("none"), RUINS("ruins"), HUT("hut"), SHACK("shack"), WIGHT_TOWER("wightTower"), DUNGEON("dungeon"), GIANT_TREE("giantTree"), IDOL_HEAD("idolHead"),
	SPIRIT_TREE("spiritTree"), WAYSTONE("waystone"), CC_WINDING_WALKWAYS("cc_winding_walkways");

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