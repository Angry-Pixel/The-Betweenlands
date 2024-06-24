package thebetweenlands.common.world.event;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IRemotelyControllableEnvironmentEvent;
import thebetweenlands.api.network.IGenericDataAccessorAccess;
import thebetweenlands.common.networking.datamanager.GenericDataAccessor;

public abstract class BLEnvironmentEvent implements IEnvironmentEvent, IRemotelyControllableEnvironmentEvent, IGenericDataAccessorAccess.IDataManagedObject {
	private final BLEnvironmentEventRegistry registry;
	private final Level level;
	private CompoundTag nbt = new CompoundTag();
	private boolean loaded = false;

	protected static final EntityDataAccessor<Boolean> ACTIVE = GenericDataAccessor.defineId(BLEnvironmentEvent.class, EntityDataSerializers.BOOLEAN);

	protected boolean hasNoRemoteState = false;
	protected boolean isStateFromRemoteOverridden = false;
	protected boolean isStateFromRemote = false;
	protected int remoteResetTicks = -1;

	protected final GenericDataAccessor dataManager;

	public BLEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		this.registry = registry;
		this.level = registry.getLevel();
		this.dataManager = new GenericDataAccessor(this);
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
			for (Player player: this.getLevel().players()) {
				//AdvancementCriterionRegistry.EVENT.trigger(player, getEventName());
				}
	}

	@Override
	public void tick(Level level) {
		if(!level.isClientSide() && !this.isStateFromRemoteOverridden && (/*!EnvironmentEventOverridesHandler.isRemoteDataAvailable() ||*/ this.hasNoRemoteState)) {
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
		CompoundTag nbt = this.getData();
		nbt.putInt("remoteResetTicks", this.remoteResetTicks);
		nbt.putBoolean("isStateFromRemote", this.isStateFromRemote);
		nbt.putBoolean("isStateFromRemoteOverridden", this.isStateFromRemoteOverridden);
		nbt.putBoolean("hasNoRemoteState", this.hasNoRemoteState);
	}

	@Override
	public void loadEventData() {
		CompoundTag nbt = this.getData();
		this.remoteResetTicks = nbt.getInt("remoteResetTicks");
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
	public Level getLevel() {
		return this.level;
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
	public CompoundTag getData() {
		return this.nbt;
	}

	@Override
	public final void writeToNBT(CompoundTag compound) {
		this.nbt.putBoolean("active", this.dataManager.get(ACTIVE));
		this.saveEventData();
		compound.put("environmentEvent:" + this.getEventName(), this.nbt);
	}

	@Override
	public final void readFromNBT(CompoundTag compound) {
		this.nbt = compound.getCompound("environmentEvent:" + this.getEventName());
		this.dataManager.set(ACTIVE, this.nbt.getBoolean("active"));
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
		return "event." + getEventName().getNamespace() + "." + getEventName().getPath() + ".name";
	}

	@Override
	public GenericDataAccessor getDataManager() {
		return this.dataManager;
	}

	@Override
	public boolean onParameterChange(EntityDataAccessor<?> key, Object value, boolean fromPacket) {
		if(fromPacket && key == ACTIVE) {
			this.setActive((boolean) value);
			return true;
		}
		return false;
	}
}
