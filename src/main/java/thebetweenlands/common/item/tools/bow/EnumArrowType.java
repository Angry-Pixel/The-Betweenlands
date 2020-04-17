package thebetweenlands.common.item.tools.bow;

import net.minecraft.util.IStringSerializable;

public enum EnumArrowType implements IStringSerializable {
	DEFAULT("default", 1), ANGLER_POISON("angler_poison", 2), OCTINE("octine", 3), BASILISK("basilisk", 4), WORM("worm", 5), CHIROMAW_BARB("chiromaw_barb", 6);

	private String name;
	private int id;

	private EnumArrowType(String name, int id){
		this.name = name;
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	public int getId() {
		return this.id;
	}

	public static EnumArrowType getEnumFromString(String name){
		for (EnumArrowType type : values()) {
			if (type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}
}
