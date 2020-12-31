package thebetweenlands.common.world.event;

import java.util.Random;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.SoundRegistry;

public class EventAuroras extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "auroras");

	protected static final DataParameter<Integer> AURORA_TYPE = GenericDataManager.createKey(EventAuroras.class, DataSerializers.VARINT);

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { new ResourceLocation("thebetweenlands:textures/events/auroras.png") };
	
	public EventAuroras(BLEnvironmentEventRegistry registry) {
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
	public int getOffTime(Random rnd) {
		return rnd.nextInt(8000) + 28000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(5000) + 8000;
	}

	@Override
	public void setActive(boolean active) {
		if((active && this.canBeActive()) || !active) {
			super.setActive(active);
			if(active && !this.getWorld().isRemote) {
				this.dataManager.set(AURORA_TYPE, this.getWorld().rand.nextInt(3));
			}
		}
	}

	@Override
	public void update(World world) {
		super.update(world);
		if(!world.isRemote && !this.canBeActive() && this.getTicks() > 500) {
			this.dataManager.set(TICKS, 500).syncImmediately(); //Start fading out
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().setShort("auroraType", this.getAuroraType());
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
			if(event != this && event.getClass() != EventWinter.class && event.getClass() != EventSnowfall.class &&
					event.getClass() != EventRift.class) {
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
		return SoundRegistry.CHIMES_AURORAS;
	}
}
