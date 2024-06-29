package thebetweenlands.common.world.event;

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

public class AuroraEvent extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = TheBetweenlands.prefix("auroras");

	protected static final EntityDataAccessor<Integer> AURORA_TYPE = GenericDataAccessor.defineId(AuroraEvent.class, EntityDataSerializers.INT);

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { TheBetweenlands.prefix("textures/events/auroras.png") };

	public AuroraEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(AURORA_TYPE, 0);
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
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
	public void setActive(boolean active) {
		if((active && this.canBeActive()) || !active) {
			super.setActive(active);
			if(active && !this.getLevel().isClientSide()) {
				this.dataManager.set(AURORA_TYPE, this.getLevel().getRandom().nextInt(3));
			}
		}
	}

	@Override
	public void tick(Level level) {
		super.tick(level);
		if(!level.isClientSide() && !this.canBeActive() && this.getTicks() > 500) {
			this.dataManager.set(TICKS, 500).syncImmediately(); //Start fading out
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().putShort("auroraType", this.getAuroraType());
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.dataManager.set(AURORA_TYPE, (int)this.getData().getShort("auroraType"));
	}

	@Override
	protected boolean canActivate() {
		return this.canBeActive();
	}

	public short getAuroraType() {
		return (short)this.dataManager.get(AURORA_TYPE).intValue();
	}

	protected boolean canBeActive() {
		for(IEnvironmentEvent event : this.getRegistry().getEventsOfState(true)) {
			if(event != this && event.getClass() != WinterEvent.class && event.getClass() != SnowfallEvent.class &&
					event.getClass() != RiftEvent.class) {
				return false;
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
