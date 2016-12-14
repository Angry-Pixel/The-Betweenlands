package thebetweenlands.common.world.storage.world.global;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.event.EnvironmentEvent;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.chunk.BetweenlandsChunkData;

public class BetweenlandsWorldData extends WorldDataBase<BetweenlandsChunkData> {
	private EnvironmentEventRegistry environmentEventRegistry;
	private AspectManager aspectManager = new AspectManager();

	public BetweenlandsWorldData() {
		super(ModInfo.ID + ":worldData");
	}

	public BetweenlandsWorldData(String name) {
		super(name);
	}

	public EnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.environmentEventRegistry;
	}

	public AspectManager getAspectManager() {
		return this.aspectManager;
	}

	/**
	 * Called before loading data and setting defaults
	 */
	@Override
	protected void init() {
		this.environmentEventRegistry = new EnvironmentEventRegistry(this.getWorld());
		this.environmentEventRegistry.init();
	}

	/**
	 * Called when this data is created for the first time for this world
	 */
	@Override
	protected void setDefaults() {
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.setDefaults();
				event.setLoaded();
			}
			this.aspectManager.loadAndPopulateStaticAspects(null, AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
		}
	}

	/**
	 * Load data here
	 */
	@Override
	protected void load() {
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.readFromNBT(this.getData());
			}
			this.environmentEventRegistry.setDisabled(this.getData().getBoolean("eventsDisabled"));
			this.aspectManager.loadAndPopulateStaticAspects(this.getData().getCompoundTag("itemAspects"), AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
		}
	}

	/**
	 * Save data here
	 */
	@Override
	protected void save() {
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.writeToNBT(this.getData());
			}
			this.getData().setBoolean("eventsDisabled", this.environmentEventRegistry.isDisabled());
			NBTTagCompound aspectData = new NBTTagCompound();
			this.aspectManager.saveStaticAspects(aspectData);
			this.getData().setTag("itemAspects", aspectData);
		}
	}

	public static BetweenlandsWorldData forWorld(World world) {
		return WorldDataBase.forWorld(world, BetweenlandsWorldData.class);
	}

	@Override
	public Class<BetweenlandsChunkData> getChunkStorage() {
		return BetweenlandsChunkData.class;
	}
}
