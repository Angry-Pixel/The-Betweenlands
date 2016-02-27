package thebetweenlands.entities.properties.list.equipment;

public enum EnumEquipmentCategory {
	NONE("none"), AMULET("amulet"), RING("ring");

	public static final EnumEquipmentCategory[] TYPES = EnumEquipmentCategory.values();

	public final String name;
	private EnumEquipmentCategory(String name) {
		this.name = name;
	}

	public static EnumEquipmentCategory fromName(String name) {
		for(EnumEquipmentCategory category : TYPES) {
			if(category.name.equals(name)) {
				return category;
			}
		}
		return NONE;
	}
}
