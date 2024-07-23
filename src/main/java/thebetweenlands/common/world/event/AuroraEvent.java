package thebetweenlands.common.world.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class AuroraEvent extends TimedEnvironmentEvent {
	protected static final EntityDataAccessor<Integer> AURORA_TYPE = GenericDataAccessor.defineId(AuroraEvent.class, EntityDataSerializers.INT);
	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { TheBetweenlands.prefix("textures/events/auroras.png") };

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(AURORA_TYPE, 0);
	}

	@Override
	public int getOffTime(RandomSource rnd) {
		return rnd.nextInt(8000) + 28000;
	}

	@Override
	public int getOnTime(RandomSource rnd) {
		return rnd.nextInt(5000) + 8000;
	}

	@Override
	public void setActive(Level level, boolean active) {
		if(!active || this.canBeActive(level)) {
			super.setActive(level, active);
			if(active && !level.isClientSide()) {
				this.dataManager.set(AURORA_TYPE, level.getRandom().nextInt(3));
			}
		}
	}

	@Override
	public void tick(Level level) {
		super.tick(level);
		if(!level.isClientSide() && !this.canBeActive(level) && this.getTicks() > 500) {
			this.dataManager.set(TICKS, 500).syncImmediately(); //Start fading out
		}
	}

	@Override
	public void saveEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveEventData(tag, registries);
		tag.putShort("auroraType", this.getAuroraType());
	}

	@Override
	public void loadEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadEventData(tag, registries);
		this.dataManager.set(AURORA_TYPE, (int)tag.getShort("auroraType"));
	}

	@Override
	protected boolean canActivate(Level level) {
		return this.canBeActive(level);
	}

	public short getAuroraType() {
		return (short)this.dataManager.get(AURORA_TYPE).intValue();
	}

	protected boolean canBeActive(Level level) {
		if (BetweenlandsWorldStorage.get(level) != null) {
			for (IEnvironmentEvent event : BetweenlandsWorldStorage.getOrThrow(level).getEnvironmentEventRegistry().getEventsOfState(true)) {
				if (event != this && event.getClass() != WinterEvent.class && event.getClass() != SnowfallEvent.class &&
					event.getClass() != RiftEvent.class) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_AURORAS.get();
	}
}
