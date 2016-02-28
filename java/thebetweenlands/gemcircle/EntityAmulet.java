package thebetweenlands.gemcircle;

import net.minecraft.nbt.NBTTagCompound;

public class EntityAmulet {
	private final CircleGem gem;
	private final boolean removable;
	private final boolean canDrop;

	public EntityAmulet(CircleGem gem, boolean removable, boolean canDrop) {
		this.gem = gem;
		this.removable = removable;
		this.canDrop = canDrop;
	}

	public CircleGem getAmuletGem() {
		return this.gem;
	}

	public boolean isRemovable() {
		return this.removable;
	}

	public boolean canDrop() {
		return this.canDrop;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("gem", this.gem.name);
		nbt.setBoolean("removable", this.removable);
		nbt.setBoolean("canDrop", this.canDrop);
		return nbt;
	}

	public static EntityAmulet readFromNBT(NBTTagCompound nbt) {
		CircleGem gem = CircleGem.fromName(nbt.getString("gem"));
		return new EntityAmulet(gem, nbt.getBoolean("removable"), nbt.getBoolean("canDrop"));
	}
}