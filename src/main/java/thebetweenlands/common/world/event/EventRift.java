package thebetweenlands.common.world.event;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
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
import thebetweenlands.common.network.datamanager.GenericDataManager;

public class EventRift extends TimedEnvironmentEvent {
	public static class RiftConfiguration {
		public final int riftSeed;
		public final float yawComponent, pitchComponent, rollComponent, scaleComponent;
		public final boolean mirrorU, mirrorV;

		public RiftConfiguration(int riftSeed, float yawComponent, float pitchComponent, float rollComponent, float scaleComponent, boolean mirrorU, boolean mirrorV) {
			this.riftSeed = riftSeed;
			this.yawComponent = yawComponent;
			this.pitchComponent = pitchComponent;
			this.rollComponent = rollComponent;
			this.scaleComponent = scaleComponent;
			this.mirrorU = mirrorU;
			this.mirrorV = mirrorV;
		}

		public RiftConfiguration(PacketBuffer buf) {
			this.riftSeed = buf.readInt();
			this.yawComponent = buf.readFloat();
			this.pitchComponent = buf.readFloat();
			this.rollComponent = buf.readFloat();
			this.scaleComponent = buf.readFloat();
			this.mirrorU = buf.readBoolean();
			this.mirrorV = buf.readBoolean();
		}

		public RiftConfiguration(NBTTagCompound nbt) {
			this.yawComponent = nbt.getFloat("yaw");
			this.pitchComponent = nbt.getFloat("pitch");
			this.rollComponent = nbt.getFloat("roll");
			this.riftSeed = Math.max(nbt.getInteger("riftSeed"), 0);
			this.mirrorU = nbt.getBoolean("mirrorU");
			this.mirrorV = nbt.getBoolean("mirrorV");
			this.scaleComponent = nbt.getFloat("scale");
		}

		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setFloat("yaw", this.yawComponent);
			nbt.setFloat("pitch", this.pitchComponent);
			nbt.setFloat("roll", this.rollComponent);
			nbt.setInteger("riftSeed", this.riftSeed);
			nbt.setBoolean("mirrorU", this.mirrorU);
			nbt.setBoolean("mirrorV", this.mirrorV);
			nbt.setFloat("scale", this.scaleComponent);
			return nbt;
		}

		public static void write(PacketBuffer buf, RiftConfiguration configuration) {
			buf.writeInt(configuration.riftSeed);
			buf.writeFloat(configuration.yawComponent);
			buf.writeFloat(configuration.pitchComponent);
			buf.writeFloat(configuration.rollComponent);
			buf.writeFloat(configuration.scaleComponent);
			buf.writeBoolean(configuration.mirrorU);
			buf.writeBoolean(configuration.mirrorV);
		}
	}

	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "rift");

	private static final int MAX_ACTIVATION_TICKS = 350;

	protected static final DataParameter<RiftConfiguration> RIFT_CONFIGURATION = GenericDataManager.createKey(EventRift.class, RiftConfiguration::write, RiftConfiguration::new);

	protected static final DataParameter<Integer> ACTIVATION_TICKS = GenericDataManager.createKey(EventRift.class, DataSerializers.VARINT);
	protected int lastActivationTicks;

	protected int soundTicks;

	protected boolean playRiftOpenSound = true;

	public EventRift(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(ACTIVATION_TICKS, 10, 0);
		this.dataManager.register(RIFT_CONFIGURATION, new RiftConfiguration(0, 0, 0, 0, 1, false, false));
	}

	@Override
	public void setDefaults() {
		super.setDefaults();
		if(this.getRegistry().isDisabled()) {
			this.dataManager.set(ACTIVATION_TICKS, this.lastActivationTicks = 0).syncImmediately();
		} else {
			this.playRiftOpenSound = false;
			this.setRandomConfiguration();
			this.setActive(true);
			this.playRiftOpenSound = true;
			this.dataManager.set(ACTIVATION_TICKS, this.lastActivationTicks = MAX_ACTIVATION_TICKS).syncImmediately();
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
	public void setActive(boolean active) {
		if(!this.getWorld().isRemote && active && !this.isActive() && this.getActivationTicks() == 0) {
			this.setRandomConfiguration();

			if(this.playRiftOpenSound) {
				TheBetweenlands.networkWrapper.sendToDimension(new MessageRiftSound(RiftSoundType.OPEN), BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
			}
		}

		super.setActive(active);
	}

	protected void setRandomConfiguration() {
		this.dataManager.set(RIFT_CONFIGURATION, new RiftConfiguration(
				this.getWorld().rand.nextInt(Integer.MAX_VALUE),
				this.getWorld().rand.nextFloat(), this.getWorld().rand.nextFloat(),
				this.getWorld().rand.nextFloat(), this.getWorld().rand.nextFloat(),
				this.getWorld().rand.nextBoolean(), this.getWorld().rand.nextBoolean()));
	}

	@Override
	public void update(World world) {
		super.update(world);

		this.lastActivationTicks = this.getActivationTicks();

		if(this.isActive()) {
			if(this.getActivationTicks() < MAX_ACTIVATION_TICKS) {
				if(this.getActivationTicks() == 108) {
					this.dataManager.set(ACTIVATION_TICKS, this.getActivationTicks() + 180).syncImmediately();
				}
				this.dataManager.set(ACTIVATION_TICKS, this.getActivationTicks() + 1);
			} else if(this.getActivationTicks() != MAX_ACTIVATION_TICKS) {
				this.dataManager.set(ACTIVATION_TICKS, MAX_ACTIVATION_TICKS).syncImmediately();
			}
		} else {
			if(this.getActivationTicks() > 0) {
				this.dataManager.set(ACTIVATION_TICKS, this.getActivationTicks() - 4);
			}
			if(this.getActivationTicks() < 0) {
				this.dataManager.set(ACTIVATION_TICKS, 0).syncImmediately();
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
		nbt.setInteger("riftTicks", this.getActivationTicks());
		this.getRiftConfiguration().writeToNBT(nbt);
	}

	@Override
	public void loadEventData() { 
		super.loadEventData();
		NBTTagCompound nbt = this.getData();
		this.dataManager.set(ACTIVATION_TICKS, nbt.getInteger("riftTicks")).syncImmediately();
		this.dataManager.set(RIFT_CONFIGURATION, new RiftConfiguration(nbt));
	}

	/**
	 * Returns the current visibility of the rift [0, 1]
	 * @param partialTicks
	 * @return
	 */
	public float getVisibility(float partialTicks) {
		return (this.lastActivationTicks + (this.getActivationTicks() - this.lastActivationTicks) * partialTicks) / (float)MAX_ACTIVATION_TICKS;
	}

	/**
	 * Returns the current active ticks
	 * @return
	 */
	public int getActivationTicks() {
		return this.dataManager.get(ACTIVATION_TICKS);
	}

	/**
	 * Returns the current rift configuration
	 * @return
	 */
	public RiftConfiguration getRiftConfiguration() {
		return this.dataManager.get(RIFT_CONFIGURATION);
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
			return availableVariants.get(this.getRiftConfiguration().riftSeed % availableVariants.size());
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
		return variant.getMinScale() + this.getRiftConfiguration().scaleComponent * (variant.getMaxScale() - variant.getMinScale());
	}

	/**
	 * Returns whether the U coordinates of the rift are mirrored
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public boolean getRiftMirrorU() {
		return this.getRiftConfiguration().mirrorU && this.getVariant().getMirrorU();
	}

	/**
	 * Returns whether the V coordinates of the rift are mirrored
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public boolean getRiftMirrorV() {
		return this.getRiftConfiguration().mirrorV && this.getVariant().getMirrorV();
	}

	/**
	 * Returns the angles of the rift: [yaw, pitch, roll]
	 * @param partialTicks
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public float[] getRiftAngles(float partialTicks) {
		RiftVariant variant = this.getVariant();
		RiftConfiguration configuration = this.getRiftConfiguration();
		return new float[] {
				variant.getMinYaw() + configuration.yawComponent * (variant.getMaxYaw() - variant.getMinYaw()), 
				variant.getMinPitch() + configuration.pitchComponent * (variant.getMaxPitch() - variant.getMinPitch()), 
				variant.getMinRoll() + configuration.rollComponent * (variant.getMaxRoll() - variant.getMinRoll())
		};
	}

	@Override
	public ResourceLocation getVisionTexture() {
		return new ResourceLocation("thebetweenlands:textures/events/rift.png");
	}
}
