package thebetweenlands.common.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.world.event.EnvironmentEvent;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;

public class BetweenlandsWorldStorage extends WorldStorageImpl {
	private EnvironmentEventRegistry environmentEventRegistry;
	private AspectManager aspectManager = new AspectManager();

	public EnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.environmentEventRegistry;
	}

	public AspectManager getAspectManager() {
		return this.aspectManager;
	}
	
	@Override
	protected void init() {
		this.environmentEventRegistry = new EnvironmentEventRegistry(this.getWorld());
		this.environmentEventRegistry.init();
		
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.setDefaults();
				event.setLoaded();
			}
			this.aspectManager.loadAndPopulateStaticAspects(null, AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.readFromNBT(nbt);
			}
			this.environmentEventRegistry.setDisabled(nbt.getBoolean("eventsDisabled"));
			this.aspectManager.loadAndPopulateStaticAspects(nbt.getCompoundTag("itemAspects"), AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.writeToNBT(nbt);
			}
			nbt.setBoolean("eventsDisabled", this.environmentEventRegistry.isDisabled());
			NBTTagCompound aspectData = new NBTTagCompound();
			this.aspectManager.saveStaticAspects(aspectData);
			nbt.setTag("itemAspects", aspectData);
		}
	}

	public static BetweenlandsWorldStorage forWorld(World world) {
		if(world.hasCapability(CAPABILITY_INSTANCE, null)) {
			IWorldStorage storage = world.getCapability(CAPABILITY_INSTANCE, null);
			if(storage instanceof BetweenlandsWorldStorage) {
				return (BetweenlandsWorldStorage) storage;
			}
		}
		return null;
	}
}
