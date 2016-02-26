package thebetweenlands.gemcircle;

import net.minecraft.nbt.NBTTagCompound;

public class EntityAmulet {
	private final CircleGem gem;
	private final boolean removable;

	public EntityAmulet(CircleGem gem, boolean removable) {
		this.gem = gem;
		this.removable = removable;
	}

	public CircleGem getAmuletGem() {
		return this.gem;
	}

	public boolean isRemovable() {
		return this.removable;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("gem", this.gem.name);
		nbt.setBoolean("removable", this.removable);
		return nbt;
	}

	public static EntityAmulet readFromNBT(NBTTagCompound nbt) {
		CircleGem gem = CircleGem.fromName(nbt.getString("gem"));
		boolean removable = nbt.getBoolean("removable");
		return new EntityAmulet(gem, removable);
	}
}