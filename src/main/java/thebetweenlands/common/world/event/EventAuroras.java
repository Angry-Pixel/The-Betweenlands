package thebetweenlands.common.world.event;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EventAuroras extends TimedEnvironmentEvent {
	public EventAuroras(EnvironmentEventRegistry registry) {
		super(registry);
	}

	private short auroraType = 0;

	@Override
	public String getEventName() {
		return "auroras";
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(42000) + 28000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(20000) + 8000;
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if((active && this.getRegistry().getActiveEvents().size() <= 1) || !active) {
			super.setActive(active, markDirty);
			if(active && !this.getWorld().isRemote) {
				this.auroraType = (short)this.getWorld().rand.nextInt(3);
			}
		}
	}

	@Override
	public void update(World world) {
		super.update(world);
		if(!world.isRemote && this.getRegistry().getActiveEvents().size() > 1 && this.ticks > 500) {
			this.ticks = 500; //Start fading out
			this.setDirty(true);
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().setShort("auroraType", this.auroraType);
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.auroraType = this.getData().getShort("auroraType");
	}

	@Override
	public void loadEventPacket(NBTTagCompound nbt) {
		super.loadEventPacket(nbt);
		this.auroraType = nbt.getShort("auroraType");
	}

	@Override
	public void sendEventPacket(NBTTagCompound nbt) { 
		super.sendEventPacket(nbt);
		nbt.setShort("auroraType", this.auroraType);
	}

	public short getAuroraType() {
		return this.auroraType;
	}
}
