package thebetweenlands.gemcircle;

import net.minecraft.nbt.NBTTagCompound;

public class EntityGem {
	public static enum Type {
		OFFENSIVE, DEFENSIVE, BOTH;
	}

	private final CircleGem gem;
	private final Type type;

	public EntityGem(CircleGem gem, Type type) {
		this.gem = gem;
		this.type = type;
	}

	public CircleGem getGem() {
		return this.gem;
	}

	public Type getType() {
		return this.type;
	}

	public boolean matches(Type type) {
		return this.type == Type.BOTH || type == this.type;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("gem", this.gem.name);
		nbt.setInteger("type", this.type.ordinal());
		return nbt;
	}

	public static EntityGem readFromNBT(NBTTagCompound nbt) {
		CircleGem gem = CircleGem.fromName(nbt.getString("gem"));
		int typeOrdinal = nbt.getInteger("type");
		if(Type.values().length > typeOrdinal) {
			return new EntityGem(gem, Type.values()[typeOrdinal]);
		}
		return null;
	}
}