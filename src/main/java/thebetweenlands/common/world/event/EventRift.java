package thebetweenlands.common.world.event;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.sky.RiftVariant;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.clientbound.MessageRiftSound;
import thebetweenlands.common.network.clientbound.MessageRiftSound.RiftSoundType;

public class EventRift extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "rift");

	private static final int MAX_ACTIVATION_TICKS = 350;

	protected int lastActivationTicks;
	protected int activationTicks;

	protected int soundTicks;
	
	protected int riftSeed;
	protected float yawComponent, pitchComponent, rollComponent, scaleComponent;
	protected boolean mirrorU, mirrorV;

	protected boolean playRiftOpenSound = true;
	
	public EventRift(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public void setDefaults() {
		super.setDefaults();
		if(this.getRegistry().isDisabled()) {
			this.activationTicks = this.lastActivationTicks = 0;
		} else {
			this.playRiftOpenSound = false;
			this.activationTicks = this.lastActivationTicks = 0;
			this.setActive(true, true);
			this.playRiftOpenSound = true;
			this.activationTicks = this.lastActivationTicks = MAX_ACTIVATION_TICKS;
		}
	}
	
	@Override
	public int getOffTime(Random rnd) {
		return 12000 + rnd.nextInt(6000);
	}

	@Override
	public int getOnTime(Random rnd) {
		return 28800 + rnd.nextInt(7200);
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if(!this.getWorld().isRemote && active && !this.isActive() && this.activationTicks == 0) {
			this.yawComponent = this.getWorld().rand.nextFloat();
			this.pitchComponent = this.getWorld().rand.nextFloat();
			this.rollComponent = this.getWorld().rand.nextFloat();
			this.scaleComponent = this.getWorld().rand.nextFloat();
			this.riftSeed = this.getWorld().rand.nextInt(Integer.MAX_VALUE);
			this.mirrorU = this.getWorld().rand.nextBoolean();
			this.mirrorV = this.getWorld().rand.nextBoolean();

			if(this.playRiftOpenSound) {
				TheBetweenlands.networkWrapper.sendToDimension(new MessageRiftSound(RiftSoundType.OPEN), BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
			}
		}

		super.setActive(active, markDirty);
	}

	@Override
	public void update(World world) {
		super.update(world);

		this.lastActivationTicks = this.activationTicks;

		if(this.isActive()) {
			if(this.activationTicks < MAX_ACTIVATION_TICKS) {
				if(this.activationTicks == 108) {
					this.activationTicks += 180;
				}
				this.activationTicks++;
			} else if(this.activationTicks != MAX_ACTIVATION_TICKS) {
				this.activationTicks = MAX_ACTIVATION_TICKS;
			}
		} else {
			if(this.activationTicks > 0) {
				this.activationTicks -= 4;
			}
			if(this.activationTicks < 0) {
				this.activationTicks = 0;
			}
		}
		
		if(!this.getWorld().isRemote) {
			int remainingTicks = this.getTicks();
			if((!this.isActive() && remainingTicks < 1800 && remainingTicks > 80) || (this.isActive() && remainingTicks < 1800 && remainingTicks > 80)) {
				if(this.soundTicks-- <= 0) {
					TheBetweenlands.networkWrapper.sendToDimension(new MessageRiftSound(RiftSoundType.CREAK), BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
					this.soundTicks = this.getWorld().rand.nextInt(150) + 100;
				}
			}
		}
	}

	@Override
	public void saveEventData() { 
		super.saveEventData();
		NBTTagCompound nbt = this.getData();
		nbt.setInteger("riftTicks", this.activationTicks);
		nbt.setFloat("yaw", this.yawComponent);
		nbt.setFloat("pitch", this.pitchComponent);
		nbt.setFloat("roll", this.rollComponent);
		nbt.setInteger("riftSeed", this.riftSeed);
		nbt.setBoolean("mirrorU", this.mirrorU);
		nbt.setBoolean("mirrorV", this.mirrorV);
		nbt.setFloat("scale", this.scaleComponent);
	}

	@Override
	public void loadEventData() { 
		super.loadEventData();
		NBTTagCompound nbt = this.getData();
		this.activationTicks = nbt.getInteger("riftTicks");
		this.yawComponent = nbt.getFloat("yaw");
		this.pitchComponent = nbt.getFloat("pitch");
		this.rollComponent = nbt.getFloat("roll");
		this.riftSeed = Math.max(nbt.getInteger("riftSeed"), 0);
		this.mirrorU = nbt.getBoolean("mirrorU");
		this.mirrorV = nbt.getBoolean("mirrorV");
		this.scaleComponent = nbt.getFloat("scale");
	}

	@Override
	public void sendEventPacket(NBTTagCompound nbt) {
		super.sendEventPacket(nbt);
		nbt.setInteger("riftTicks", this.activationTicks);
		nbt.setFloat("yaw", this.yawComponent);
		nbt.setFloat("pitch", this.pitchComponent);
		nbt.setFloat("roll", this.rollComponent);
		nbt.setInteger("riftSeed", this.riftSeed);
		nbt.setBoolean("mirrorU", this.mirrorU);
		nbt.setBoolean("mirrorV", this.mirrorV);
		nbt.setFloat("scale", this.scaleComponent);
	}

	@Override
	public void loadEventPacket(NBTTagCompound nbt) {
		super.loadEventPacket(nbt);
		this.activationTicks = nbt.getInteger("riftTicks");
		this.yawComponent = nbt.getFloat("yaw");
		this.pitchComponent = nbt.getFloat("pitch");
		this.rollComponent = nbt.getFloat("roll");
		this.riftSeed = Math.max(nbt.getInteger("riftSeed"), 0);
		this.mirrorU = nbt.getBoolean("mirrorU");
		this.mirrorV = nbt.getBoolean("mirrorV");
		this.scaleComponent = nbt.getFloat("scale");
	}

	/**
	 * Returns the current visibility of the rift [0, 1]
	 * @param partialTicks
	 * @return
	 */
	public float getVisibility(float partialTicks) {
		return (this.lastActivationTicks + (this.activationTicks - this.lastActivationTicks) * partialTicks) / (float)MAX_ACTIVATION_TICKS;
	}

	/**
	 * Returns the current active ticks
	 * @return
	 */
	public int getActivationTicks() {
		return this.activationTicks;
	}

	/**
	 * Returns the rift variant
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public RiftVariant getVariant() {
		List<RiftVariant> availableVariants = TheBetweenlands.proxy.getRiftVariants();
		if(availableVariants.isEmpty()) {
			return RiftVariant.DEFAULT;
		} else {
			return availableVariants.get(this.riftSeed % availableVariants.size());
		}
	}

	/**
	 * Returns the scale of the rift
	 * @param partialTicks
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public float getRiftScale(float partialTicks) {
		RiftVariant variant = this.getVariant();
		return variant.getMinScale() + this.scaleComponent * (variant.getMaxScale() - variant.getMinScale());
	}

	/**
	 * Returns whether the U coordinates of the rift are mirrored
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public boolean getRiftMirrorU() {
		return this.mirrorU && this.getVariant().getMirrorU();
	}

	/**
	 * Returns whether the V coordinates of the rift are mirrored
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public boolean getRiftMirrorV() {
		return this.mirrorV && this.getVariant().getMirrorV();
	}

	/**
	 * Returns the angles of the rift: [yaw, pitch, roll]
	 * @param partialTicks
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public float[] getRiftAngles(float partialTicks) {
		RiftVariant variant = this.getVariant();
		return new float[] {
				variant.getMinYaw() + this.yawComponent * (variant.getMaxYaw() - variant.getMinYaw()), 
				variant.getMinPitch() + this.pitchComponent * (variant.getMaxPitch() - variant.getMinPitch()), 
				variant.getMinRoll() + this.rollComponent * (variant.getMaxRoll() - variant.getMinRoll())
		};
	}

	/**
	 * Returns the angle components of the rift: [yaw, pitch, roll]
	 * @return
	 */
	public float[] getRiftAngleComponents() {
		return new float[]{this.yawComponent, this.pitchComponent, this.rollComponent};
	}
}
