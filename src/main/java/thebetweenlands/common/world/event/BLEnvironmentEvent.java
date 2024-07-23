package thebetweenlands.common.world.event;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.api.environment.IRemotelyControllableEnvironmentEvent;
import thebetweenlands.api.network.IGenericDataAccessorAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.DimensionRegistries;

public abstract class BLEnvironmentEvent implements IRemotelyControllableEnvironmentEvent, IGenericDataAccessorAccess.IDataManagedObject {
	private boolean loaded = false;

	protected static final EntityDataAccessor<Boolean> ACTIVE = GenericDataAccessor.defineId(BLEnvironmentEvent.class, EntityDataSerializers.BOOLEAN);

	protected boolean hasNoRemoteState = false;
	protected boolean isStateFromRemoteOverridden = false;
	protected boolean isStateFromRemote = false;
	protected int remoteResetTicks = -1;

	protected final GenericDataAccessor dataManager;

	public BLEnvironmentEvent() {
		this.dataManager = new GenericDataAccessor(this);
		this.initDataParameters();
	}

	protected void initDataParameters() {
		this.dataManager.register(ACTIVE, false);
	}

	@Override
	public void setActive(Level level, boolean active) {
		if (this.isStateFromRemote && active != this.isActive()) {
			//State was overridden and is no longer from remote
			this.isStateFromRemote = false;
			this.isStateFromRemoteOverridden = true;
		}

		this.dataManager.set(ACTIVE, active);

		if (active)
			for (Player player : level.players()) {
				AdvancementCriteriaRegistry.EVENT.get().trigger(player, this);
			}
	}

	@Override
	public void tick(Level level) {
		if (!level.isClientSide() && !this.isStateFromRemoteOverridden && (/*!EnvironmentEventOverridesHandler.isRemoteDataAvailable() ||*/ this.hasNoRemoteState)) {
			if (this.remoteResetTicks > 0) {
				this.remoteResetTicks--;
			}
			if (this.remoteResetTicks == 0) {
				this.updateNoStateFromRemote();
				this.resetActiveState(level);
				this.remoteResetTicks = -1;
			}
		}
	}

	@Override
	public void saveEventData(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putInt("remoteResetTicks", this.remoteResetTicks);
		tag.putBoolean("isStateFromRemote", this.isStateFromRemote);
		tag.putBoolean("isStateFromRemoteOverridden", this.isStateFromRemoteOverridden);
		tag.putBoolean("hasNoRemoteState", this.hasNoRemoteState);
	}

	@Override
	public void loadEventData(CompoundTag tag, HolderLookup.Provider registries) {
		this.remoteResetTicks = tag.getInt("remoteResetTicks");
		this.isStateFromRemote = tag.getBoolean("isStateFromRemote");
		this.isStateFromRemoteOverridden = tag.getBoolean("isStateFromRemoteOverridden");
		this.hasNoRemoteState = tag.getBoolean("hasNoRemoteState");
	}

	@Override
	public void resetStateFromRemote(Level level) {
		this.resetActiveState(level);
	}

	@Override
	public boolean isCurrentStateFromRemote() {
		return this.isStateFromRemote;
	}

	@Override
	public void updateStateFromRemote(Level level, boolean value, int remoteResetTicks, ImmutableMap<String, String> data) {
		//Only update state if it wasn't overridden by a player or command
		if (!this.isStateFromRemoteOverridden) {
			if (this.isActive() != value && !this.isStateFromRemote) {
				this.setActive(level, value);
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
	public boolean isActive() {
		return this.dataManager.get(ACTIVE);
	}

	@Override
	public boolean isActiveAt(double x, double y, double z) {
		return this.isActive();
	}

	@Override
	public void resetActiveState(Level level) {
		if (this.isActive()) {
			this.setActive(level, false);
		}
	}

	@Override
	public void setLoaded(Level level) {
		this.loaded = true;
	}

	@Override
	public final void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putBoolean("active", this.dataManager.get(ACTIVE));
		this.saveEventData(tag, registries);
	}

	@Override
	public final void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
		this.dataManager.set(ACTIVE, tag.getBoolean("active"));
		this.loadEventData(tag, registries);
		this.loaded = true;
	}

	@Override
	public void setDefaults(Level level) {
	}

	@Override
	public boolean isLoaded() {
		return this.loaded;
	}

	@Override
	public GenericDataAccessor getDataManager() {
		return this.dataManager;
	}

	@Override
	public boolean onParameterChange(EntityDataAccessor<?> key, Object value, boolean fromPacket) {
		if (fromPacket && key == ACTIVE) {
			this.setActive(TheBetweenlands.getLevelWorkaround(DimensionRegistries.DIMENSION_KEY), (boolean) value);
			return true;
		}
		return false;
	}
}
