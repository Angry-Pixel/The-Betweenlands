package thebetweenlands.common.item.tools.bow;

import net.minecraft.util.IStringSerializable;

public enum EnumArrowType implements IStringSerializable {
	DEFAULT("default"), ANGLER_POISON("angler_poison"), OCTINE("octine"), BASILISK("basilisk"), WORM("worm");

	private String name;

	private EnumArrowType(String name){
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
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
