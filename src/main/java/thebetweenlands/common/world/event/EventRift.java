package thebetweenlands.common.world.event;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.common.lib.ModInfo;

public class EventRift extends BLEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "rift");

	protected int lastTicks;
	protected int ticks;

	protected float yaw, pitch, roll;

	public EventRift(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if(!this.getWorld().isRemote && active && !this.isActive() && this.ticks == 0) {
			this.yaw = this.getWorld().rand.nextFloat() * 360.0F;
			this.pitch = this.getWorld().rand.nextFloat() * 180.0F - 90.0F;
			this.roll = this.getWorld().rand.nextFloat() * 360.0F;
			this.markDirty();
		}

		super.setActive(active, markDirty);
	}

	@Override
	public void update(World world) {
		super.update(world);

		this.lastTicks = this.ticks;

		if(this.isActive()) {
			if(this.ticks < 200) {
				this.ticks++;
			} else if(this.ticks != 200) {
				this.ticks = 200;
			}
		} else {
			if(this.ticks > 0) {
				this.ticks--;
			} else if(this.ticks != 0) {
				this.ticks = 0;
			}
		}
	}

	@Override
	public void saveEventData() { 
		super.saveEventData();
		NBTTagCompound nbt = this.getData();
		nbt.setInteger("ticks", this.ticks);
		nbt.setFloat("yaw", this.yaw);
		nbt.setFloat("pitch", this.pitch);
		nbt.setFloat("roll", this.roll);
	}

	@Override
	public void loadEventData() { 
		super.loadEventData();
		NBTTagCompound nbt = this.getData();
		this.ticks = nbt.getInteger("ticks");
		this.yaw = nbt.getFloat("yaw");
		this.pitch = nbt.getFloat("pitch");
		this.roll = nbt.getFloat("pitch");
	}

	@Override
	public void sendEventPacket(NBTTagCompound nbt) {
		super.sendEventPacket(nbt);
		nbt.setInteger("ticks", this.ticks);
		nbt.setFloat("yaw", this.yaw);
		nbt.setFloat("pitch", this.pitch);
		nbt.setFloat("roll", this.roll);
	}

	@Override
	public void loadEventPacket(NBTTagCompound nbt) {
		super.loadEventPacket(nbt);
		this.ticks = nbt.getInteger("ticks");
		this.yaw = nbt.getFloat("yaw");
		this.pitch = nbt.getFloat("pitch");
		this.roll = nbt.getFloat("pitch");
	}

	/**
	 * Returns the current visibility of the rift [0, 1]
	 * @param partialTicks
	 * @return
	 */
	public float getVisibility(float partialTicks) {
		return (this.lastTicks + (this.ticks - this.lastTicks) * partialTicks) / 200.0F;
	}

	/**
	 * Returns the current active ticks
	 * @return
	 */
	public int getActiveTicks() {
		return this.ticks;
	}

	/**
	 * Returns the angles of the rift: [yaw, pitch, roll]
	 * @param partialTicks
	 * @param world
	 * @param mc
	 * @return
	 */
	public float[] getRiftAngles(float partialTicks) {
		return new float[]{this.yaw, this.pitch, this.roll};
	}
}
