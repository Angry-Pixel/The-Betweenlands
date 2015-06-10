package thebetweenlands.world.events;

import java.util.Random;

import thebetweenlands.lib.ModInfo;

import net.minecraft.nbt.NBTTagCompound;

public abstract class EnvironmentEvent {
	private NBTTagCompound nbtt = new NBTTagCompound();
	private int time = 0;
	private boolean active = false;
	private boolean dirty = false;
	private boolean loaded = false;
	
	public boolean isActive() {
		return this.active;
	}
	
	public void markDirty() {
		this.dirty = true;
	}
	
	public boolean isDirty() {
		return this.dirty;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void update(Random rnd) {
		if(!this.loaded) {
			return;
		}
		this.time--;
		if(this.time <= 0) {
			if(this.active) {
				this.time = this.getOffTime(rnd);
			} else {
				this.time = this.getOnTime(rnd);
			}
			this.active = !this.active;
			this.markDirty();
		}
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		this.nbtt.setBoolean("active", this.active);
		this.nbtt.setInteger("time", this.time);
		compound.setTag(ModInfo.ID + ":environmentEvent:" + this.getEventName(), this.nbtt);
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		this.nbtt = compound.getCompoundTag(ModInfo.ID + ":environmentEvent:" + this.getEventName());
		this.active = this.nbtt.getBoolean("active");
		this.time = this.nbtt.getInteger("time");
		this.loaded = true;
	}
	
	public abstract String getEventName();
	
	public abstract int getOffTime(Random rnd);
	
	public abstract int getOnTime(Random rnd);
}
