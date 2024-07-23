package thebetweenlands.common.world.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.ClientEvents;
import thebetweenlands.client.sky.RiftVariant;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.RiftSoundPacket;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import java.util.List;

public class RiftEvent extends TimedEnvironmentEvent {
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

		public RiftConfiguration(FriendlyByteBuf buf) {
			this.riftSeed = buf.readInt();
			this.yawComponent = buf.readFloat();
			this.pitchComponent = buf.readFloat();
			this.rollComponent = buf.readFloat();
			this.scaleComponent = buf.readFloat();
			this.mirrorU = buf.readBoolean();
			this.mirrorV = buf.readBoolean();
		}

		public RiftConfiguration(CompoundTag nbt) {
			this.yawComponent = nbt.getFloat("yaw");
			this.pitchComponent = nbt.getFloat("pitch");
			this.rollComponent = nbt.getFloat("roll");
			this.riftSeed = Math.max(nbt.getInt("riftSeed"), 0);
			this.mirrorU = nbt.getBoolean("mirrorU");
			this.mirrorV = nbt.getBoolean("mirrorV");
			this.scaleComponent = nbt.getFloat("scale");
		}

		public CompoundTag writeToNBT(CompoundTag nbt) {
			nbt.putFloat("yaw", this.yawComponent);
			nbt.putFloat("pitch", this.pitchComponent);
			nbt.putFloat("roll", this.rollComponent);
			nbt.putInt("riftSeed", this.riftSeed);
			nbt.putBoolean("mirrorU", this.mirrorU);
			nbt.putBoolean("mirrorV", this.mirrorV);
			nbt.putFloat("scale", this.scaleComponent);
			return nbt;
		}

		public static void write(FriendlyByteBuf buf, RiftConfiguration configuration) {
			buf.writeInt(configuration.riftSeed);
			buf.writeFloat(configuration.yawComponent);
			buf.writeFloat(configuration.pitchComponent);
			buf.writeFloat(configuration.rollComponent);
			buf.writeFloat(configuration.scaleComponent);
			buf.writeBoolean(configuration.mirrorU);
			buf.writeBoolean(configuration.mirrorV);
		}
	}

	private static final int MAX_ACTIVATION_TICKS = 350;

	protected static final EntityDataAccessor<RiftConfiguration> RIFT_CONFIGURATION = GenericDataAccessor.defineId(RiftEvent.class, RiftConfiguration::write, RiftConfiguration::new);

	protected static final EntityDataAccessor<Integer> ACTIVATION_TICKS = GenericDataAccessor.defineId(RiftEvent.class, EntityDataSerializers.INT);

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { TheBetweenlands.prefix("textures/events/rift.png") };

	protected int lastActivationTicks;

	protected int soundTicks;

	protected boolean playRiftOpenSound = true;

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(ACTIVATION_TICKS, 10, 0);
		this.dataManager.register(RIFT_CONFIGURATION, new RiftConfiguration(0, 0, 0, 0, 1, false, false));
	}

	@Override
	public void setDefaults(Level level) {
		super.setDefaults(level);
		if(BetweenlandsWorldStorage.get(level) != null && BetweenlandsWorldStorage.getOrThrow(level).getEnvironmentEventRegistry().isDisabled()) {
			this.dataManager.set(ACTIVATION_TICKS, this.lastActivationTicks = 0).syncImmediately();
		} else {
			this.playRiftOpenSound = false;
			this.setRandomConfiguration(level);
			this.setActive(level, true);
			this.playRiftOpenSound = true;
			this.dataManager.set(ACTIVATION_TICKS, this.lastActivationTicks = MAX_ACTIVATION_TICKS).syncImmediately();
		}
	}

	@Override
	public int getOffTime(RandomSource rnd) {
		return 12000 + rnd.nextInt(6000);
	}

	@Override
	public int getOnTime(RandomSource rnd) {
		return 28800 + rnd.nextInt(7200);
	}

	@Override
	public void setActive(Level level, boolean active) {
		if(!level.isClientSide() && active && !this.isActive() && this.getActivationTicks() == 0) {
			this.setRandomConfiguration(level);

			if(this.playRiftOpenSound) {
				PacketDistributor.sendToPlayersInDimension((ServerLevel) level, new RiftSoundPacket(RiftSoundPacket.RiftSoundType.OPEN));
			}
		}

		super.setActive(level, active);
	}

	protected void setRandomConfiguration(Level level) {
		this.dataManager.set(RIFT_CONFIGURATION, new RiftConfiguration(
				level.getRandom().nextInt(Integer.MAX_VALUE),
				level.getRandom().nextFloat(), level.getRandom().nextFloat(),
				level.getRandom().nextFloat(), level.getRandom().nextFloat(),
				level.getRandom().nextBoolean(), level.getRandom().nextBoolean()));
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

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

		if(!level.isClientSide()) {
			int remainingTicks = this.getTicks();
			if((!this.isActive() && remainingTicks < 1800 && remainingTicks > 80) || (this.isActive() && remainingTicks < 1800 && remainingTicks > 80)) {
				if(this.soundTicks-- <= 0) {
					PacketDistributor.sendToPlayersInDimension((ServerLevel) level, new RiftSoundPacket(RiftSoundPacket.RiftSoundType.CREAK));
					this.soundTicks = level.getRandom().nextInt(150) + 100;
				}
			}
		}
	}

	@Override
	public void saveEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveEventData(tag, registries);
		tag.putInt("riftTicks", this.getActivationTicks());
		this.getRiftConfiguration().writeToNBT(tag);
	}

	@Override
	public void loadEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadEventData(tag, registries);
		this.dataManager.set(ACTIVATION_TICKS, tag.getInt("riftTicks")).syncImmediately();
		this.dataManager.set(RIFT_CONFIGURATION, new RiftConfiguration(tag));
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
	public RiftVariant getVariant() {
		List<RiftVariant> availableVariants = ClientEvents.getRiftVariantLoader().getRiftVariants();
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
	public float getRiftScale(float partialTicks) {
		RiftVariant variant = this.getVariant();
		return variant.minScale() + this.getRiftConfiguration().scaleComponent * (variant.maxScale() - variant.minScale());
	}

	/**
	 * Returns whether the U coordinates of the rift are mirrored
	 * @return
	 */
	public boolean getRiftMirrorU() {
		return this.getRiftConfiguration().mirrorU && this.getVariant().mirrorU();
	}

	/**
	 * Returns whether the V coordinates of the rift are mirrored
	 * @return
	 */
	public boolean getRiftMirrorV() {
		return this.getRiftConfiguration().mirrorV && this.getVariant().mirrorV();
	}

	/**
	 * Returns the angles of the rift: [yaw, pitch, roll]
	 * @param partialTicks
	 * @return
	 */
	public float[] getRiftAngles(float partialTicks) {
		RiftVariant variant = this.getVariant();
		RiftConfiguration configuration = this.getRiftConfiguration();
		return new float[] {
				variant.minYaw() + configuration.yawComponent * (variant.maxYaw() - variant.minYaw()),
				variant.minPitch() + configuration.pitchComponent * (variant.maxPitch() - variant.minPitch()),
				variant.minRoll() + configuration.rollComponent * (variant.maxRoll() - variant.minRoll())
		};
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_RIFT.get();
	}
}
