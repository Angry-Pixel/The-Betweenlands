package thebetweenlands.common.world.event;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IRemotelyControllableEnvironmentEvent;
import thebetweenlands.api.network.IGenericDataManagerAccess.IDataManagedObject;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.handler.EnvironmentEventOverridesHandler;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public abstract class BLEnvironmentEvent implements IEnvironmentEvent, IRemotelyControllableEnvironmentEvent, IDataManagedObject {
	private final BLEnvironmentEventRegistry registry;
	private final World world;
	private NBTTagCompound nbtt = new NBTTagCompound();
	private boolean loaded = false;

	protected static final DataParameter<Boolean> ACTIVE = GenericDataManager.createKey(BLEnvironmentEvent.class, DataSerializers.BOOLEAN);

	protected boolean hasNoRemoteState = false;
	protected boolean isStateFromRemoteOverridden = false;
	protected boolean isStateFromRemote = false;
	protected int remoteResetTicks = -1;

	protected final GenericDataManager dataManager;

	public BLEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		this.registry = registry;
		this.world = registry.getWorld();
		this.dataManager = new GenericDataManager(this);
		this.initDataParameters();
	}

	protected void initDataParameters() {
		this.dataManager.register(ACTIVE, false);
	}

	@Override
	public BLEnvironmentEventRegistry getRegistry() {
		return this.registry;
	}

	@Override
	public void setActive(boolean active) {
		if(this.isStateFromRemote && active != this.isActive()) {
			//State was overridden and is no longer from remote
			this.isStateFromRemote = false;
			this.isStateFromRemoteOverridden = true;
		}

		this.dataManager.set(ACTIVE, active);

		if (active)
			for (EntityPlayerMP player: getWorld().getPlayers(EntityPlayerMP.class, player -> player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId))
				AdvancementCriterionRegistry.EVENT.trigger(player, getEventName());
	}

	@Override
	public void update(World world) {
		if(!world.isRemote && !this.isStateFromRemoteOverridden && (!EnvironmentEventOverridesHandler.isRemoteDataAvailable() || this.hasNoRemoteState)) {
			if(this.remoteResetTicks > 0) {
				this.remoteResetTicks--;
			}
			if(this.remoteResetTicks == 0) {
				this.updateNoStateFromRemote();
				this.resetActiveState();
				this.remoteResetTicks = -1;
			}
		}
	}

	@Override
	public void saveEventData() {
		NBTTagCompound nbt = this.getData();
		nbt.setInteger("remoteResetTicks", this.remoteResetTicks);
		nbt.setBoolean("isStateFromRemote", this.isStateFromRemote);
		nbt.setBoolean("isStateFromRemoteOverridden", this.isStateFromRemoteOverridden);
		nbt.setBoolean("hasNoRemoteState", this.hasNoRemoteState);
	}

	@Override
	public void loadEventData() {
		NBTTagCompound nbt = this.getData();
		this.remoteResetTicks = nbt.getInteger("remoteResetTicks");
		this.isStateFromRemote = nbt.getBoolean("isStateFromRemote");
		this.isStateFromRemoteOverridden = nbt.getBoolean("isStateFromRemoteOverridden");
		this.hasNoRemoteState = nbt.getBoolean("hasNoRemoteState");
	}

	@Override
	public void resetStateFromRemote() {
		this.resetActiveState();
	}

	@Override
	public boolean isCurrentStateFromRemote() {
		return this.isStateFromRemote;
	}

	@Override
	public void updateStateFromRemote(boolean value, int remoteResetTicks, ImmutableMap<String, String> data) {
		//Only update state if it wasn't overridden by a player or command
		if(!this.isStateFromRemoteOverridden) {
			if(this.isActive() != value && !this.isStateFromRemote) {
				this.setActive(value);
			}
			this.remoteResetTicks = remoteResetTicks;
			this.isStateFromRemote = true;
		}
		this.hasNoRemoteState = false;
	}

	@Override
	public void updateNoStateFromRemote() {
		this.hasNoRemoteState = true;
		this.isStateFromRemoteOverridden = false;
		this.isStateFromRemote = false;
		this.remoteResetTicks = -1;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public boolean isActive() {
		return this.dataManager.get(ACTIVE);
	}

	@Override
	public boolean isActiveAt(double x, double y, double z) {
		return this.isActive();
	}

	@Override
	public void resetActiveState() {
		if(this.isActive()) {
			this.setActive(false);
		}
	}

	@Override
	public void setLoaded() {
		this.loaded = true;
	}

	@Override
	public NBTTagCompound getData() {
		return this.nbtt;
	}

	@Override
	public final void writeToNBT(NBTTagCompound compound) {
		this.nbtt.setBoolean("active", this.dataManager.get(ACTIVE));
		this.saveEventData();
		compound.setTag("environmentEvent:" + this.getEventName(), this.nbtt);
	}

	@Override
	public final void readFromNBT(NBTTagCompound compound) {
		this.nbtt = compound.getCompoundTag("environmentEvent:" + this.getEventName());
		this.dataManager.set(ACTIVE, this.nbtt.getBoolean("active"));
		this.loadEventData();
		this.loaded = true;
	}

	@Override
	public void setDefaults() { }

	@Override
	public boolean isLoaded() {
		return this.loaded;
	}

	@Override
	public String getLocalizationEventName() {
		return "event." + getEventName().getResourceDomain() + "." + getEventName().getResourcePath() + ".name";
	}

	@Override
	public GenericDataManager getDataManager() {
		return this.dataManager;
	}

	@Override
	public boolean onParameterChange(DataParameter<?> key, Object value, boolean fromPacket) {
		if(fromPacket && key == ACTIVE) {
			this.setActive((boolean) value);
			return true;
		}
		return false;
	}
}
