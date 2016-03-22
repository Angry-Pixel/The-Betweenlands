package thebetweenlands.entities.properties.list;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.entities.properties.EntityProperties;

public class EntityPropertiesFlight extends EntityProperties<EntityPlayer> {
	private boolean flying = false;

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setBoolean("flying", this.flying);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.flying = nbt.getBoolean("flying");
	}

	@Override
	public String getID() {
		return "blPropertyFlight";
	}

	@Override
	public Class<EntityPlayer> getEntityClass() {
		return EntityPlayer.class;
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	@Override
	public boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		this.saveNBTData(nbt);
		return false;
	}

	@Override
	public void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.loadNBTData(nbt);
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	public boolean isFlying() {
		return this.flying;
	}
}
