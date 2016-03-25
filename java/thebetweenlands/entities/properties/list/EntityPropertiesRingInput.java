package thebetweenlands.entities.properties.list;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.entities.properties.EntityProperties;

public class EntityPropertiesRingInput extends EntityProperties<EntityPlayer> {
	private boolean isUsingRing = false;

	@Override
	public void saveNBTData(NBTTagCompound nbt) { }

	@Override
	public void loadNBTData(NBTTagCompound nbt) { }

	@Override
	public int getTrackingTime() {
		return 0;
	}

	@Override
	public int getTrackingUpdateTime() {
		return 0;
	}

	@Override
	protected boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		nbt.setBoolean("usingRing", this.isUsingRing);
		return false;
	}

	@Override
	protected void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.isUsingRing = nbt.getBoolean("usingRing");
	}

	@Override
	public String getID() {
		return "blPropertyRingInput";
	}

	@Override
	public Class<EntityPlayer> getEntityClass() {
		return EntityPlayer.class;
	}

	public void setInUse(boolean use) {
		this.isUsingRing = use;
	}

	public boolean isInUse() {
		return this.isUsingRing;
	}
}
