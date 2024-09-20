package thebetweenlands.common.world.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import thebetweenlands.api.environment.PredictableEnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.registries.AttachmentRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class TimedEnvironmentEvent extends BLEnvironmentEvent implements PredictableEnvironmentEvent {
	public static class ActiveStateEstimator {
		private final TimedEnvironmentEvent event;
		private final List<Supplier<ActiveStateEstimator>> dependencies = new ArrayList<>();
		private int minTimeForActivity = 600;
		private int activationOffsetTime = -2400;
		private int maxActivationOffsetTimeFromDependency = 1;

		public ActiveStateEstimator(TimedEnvironmentEvent event) {
			this.event = event;
		}

		public ActiveStateEstimator dependsOnEvent(TimedEnvironmentEvent event) {
			return this.dependsOn(event.getActiveStateEstimator());
		}

		public ActiveStateEstimator dependsOnEvent(Supplier<TimedEnvironmentEvent> event) {
			return this.dependsOn(() -> event.get().getActiveStateEstimator());
		}

		public ActiveStateEstimator dependsOn(ActiveStateEstimator dependency) {
			return this.dependsOn(() -> dependency);
		}

		public ActiveStateEstimator dependsOn(Supplier<ActiveStateEstimator> dependency) {
			this.dependencies.add(dependency);
			return this;
		}

		public ActiveStateEstimator activationOffsetTime(int offset, int maxOffsetFromDependency) {
			this.activationOffsetTime = offset;
			this.maxActivationOffsetTimeFromDependency = maxOffsetFromDependency;
			return this;
		}

		public ActiveStateEstimator minTimeForActivity(int minTime) {
			this.minTimeForActivity = minTime;
			return this;
		}

		public int estimateTimeUntil(Level level, State state) {
			if(state == State.ACTIVE) {
				if(this.event.isActive()) {
					return 0;
				} else if(!this.dependencies.isEmpty()) {
					int startDepTime = 0;
					int endDepTime = Integer.MAX_VALUE;

					for(Supplier<ActiveStateEstimator> dep : this.dependencies) {
						ActiveStateEstimator estimator = dep.get();
						int startEstimate = estimator.estimateTimeUntil(level, State.ACTIVE);
						startDepTime = Math.max(startDepTime, startEstimate);

						int endEstimate;
						if(estimator.event.isActive()) {
							endEstimate = estimator.event.estimateTimeRemaining(level, State.ACTIVE);
						} else {
							endEstimate = startEstimate + estimator.estimateNextStateDuration();
						}
						endDepTime = Math.min(endDepTime, endEstimate);
					}

					int depDuration = endDepTime - startDepTime;

					int timeUntilActivation = this.event.getTicks();

					if(timeUntilActivation < depDuration && depDuration - timeUntilActivation >= this.minTimeForActivity) {
						return Math.max(1, Math.max(startDepTime - this.maxActivationOffsetTimeFromDependency, Math.min(endDepTime + this.maxActivationOffsetTimeFromDependency, startDepTime + timeUntilActivation + (((this.activationOffsetTime < 0 && startDepTime > 0) || (this.activationOffsetTime > 0)) ? this.activationOffsetTime : 0))));
					} else {
						return -1;
					}
				} else if(this.event.canActivate(level)) {
					return this.event.getTicks();
				}
			} else if(state == State.INACTIVE) {
				if(!this.event.isActive()) {
					return 0;
				} else if(!this.dependencies.isEmpty()) {
					int endDepTime = 0;

					for(Supplier<ActiveStateEstimator> dep : this.dependencies) {
						ActiveStateEstimator estimator = dep.get();
						endDepTime = Math.min(endDepTime, estimator.estimateTimeUntil(level, State.ACTIVE) + estimator.estimateNextStateDuration());
					}

					return Math.min(endDepTime, this.event.getTicks());
				} else {
					return this.event.getTicks();
				}
			}
			return -1;
		}

		public int estimateNextStateDuration() {
			return this.event.dataManager.get(NEXT_DURATION);
		}
	}

	protected static final EntityDataAccessor<Integer> TICKS = GenericDataAccessor.defineId(TimedEnvironmentEvent.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> START_TICKS = GenericDataAccessor.defineId(TimedEnvironmentEvent.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> NEXT_DURATION = GenericDataAccessor.defineId(TimedEnvironmentEvent.class, EntityDataSerializers.INT);

	protected final ActiveStateEstimator activeStateEstimator = new ActiveStateEstimator(this);

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(TICKS, 20, 0);
		this.dataManager.register(START_TICKS, 0);
		this.dataManager.register(NEXT_DURATION, 0);
	}

	@Override
	public void tick(Level level) {
		super.tick(level);
		var storage = level.getExistingData(AttachmentRegistry.WORLD_STORAGE);
		if(storage.isPresent() && !storage.get().getEnvironmentEventRegistry().isDisabled() && !this.isCurrentStateFromRemote() && level.getGameRules().getBoolean(TheBetweenlands.TIMED_EVENT_GAMERULE)) {
			if(this.isActive() || this.canActivate(level)) {
				this.dataManager.set(TICKS, this.getTicks() - 1);
			}

			if(!level.isClientSide() && this.getTicks() <= 0) {
				int nextDuration = this.dataManager.get(NEXT_DURATION);

				if(this.isActive() || this.canActivate(level)) {
					this.setActive(level, !this.isActive());
				}

				this.dataManager.set(TICKS, nextDuration).syncImmediately();
				this.dataManager.set(START_TICKS, nextDuration);

				if(!this.isActive()) {
					this.dataManager.set(NEXT_DURATION, this.getOnTime(level.getRandom()));
				} else {
					this.dataManager.set(NEXT_DURATION, this.getOffTime(level.getRandom()));
				}
			}
		}
	}

	/**
	 * Returns whether the event can activate right now
	 * @return
	 */
	protected boolean canActivate(Level level) {
		return true;
	}

	/**
	 * Sets how many ticks this event stays on/off
	 * @param ticks
	 * @return
	 */
	public void setTicks(int ticks) {
		int currTicks = this.getTicks();
		int diff = ticks - currTicks;
		this.dataManager.set(TICKS, currTicks + diff);
		this.dataManager.set(START_TICKS, Math.max(this.getStartTicks() + diff, 0));
	}

	/**
	 * Returns the time in ticks this event stays on/off
	 * @return
	 */
	public int getTicks() {
		return this.dataManager.get(TICKS);
	}

	/**
	 * Returns the maximum ticks
	 * @return
	 */
	public int getStartTicks() {
		return this.dataManager.get(START_TICKS);
	}

	/**
	 * Returns how many ticks have elapsed since changing state
	 * @return
	 */
	public int getTicksElapsed() {
		return this.getStartTicks() - this.getTicks();
	}

	@Override
	public void setActive(Level level, boolean active) {
		if(!active || this.canActivate(level)) {
			super.setActive(level, active);
			if(!level.isClientSide()) {
				if(!this.isActive()) {
					int offTime = this.getOffTime(level.getRandom());
					this.dataManager.set(TICKS, offTime).syncImmediately();
					this.dataManager.set(START_TICKS, offTime);
					this.dataManager.set(NEXT_DURATION, this.getOnTime(level.getRandom()));
				} else {
					int onTime = this.getOnTime(level.getRandom());
					this.dataManager.set(TICKS, onTime).syncImmediately();
					this.dataManager.set(START_TICKS, onTime);
					this.dataManager.set(NEXT_DURATION, this.getOffTime(level.getRandom()));
				}
			}
		}
	}

	@Override
	public void saveEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveEventData(tag, registries);
		tag.putInt("ticks", this.getTicks());
		tag.putInt("startTicks", this.getStartTicks());
		tag.putInt("nextDuration", this.dataManager.get(NEXT_DURATION));
	}

	@Override
	public void loadEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadEventData(tag, registries);
		this.dataManager.set(TICKS, tag.getInt("ticks")).syncImmediately();
		this.dataManager.set(START_TICKS, tag.getInt("startTicks"));

		//Backwards compatibility with <= 3.7.1 before NEXT_DURATION existed
		if(!tag.contains("nextDuration", Tag.TAG_INT)) {
			this.dataManager.set(NEXT_DURATION, this.isActive() ? this.getOffTime(RandomSource.create()) : this.getOnTime(RandomSource.create()));
		} else {
			this.dataManager.set(NEXT_DURATION, tag.getInt("nextDuration"));
		}
	}

	@Override
	public void setDefaults(Level level) {
		this.dataManager.set(TICKS, this.getOffTime(level.getRandom()));
		this.dataManager.set(NEXT_DURATION, this.getOnTime(level.getRandom()));
	}

	/**
	 * Returns how many ticks the event is not active.
	 * @param rnd
	 * @return
	 */
	public abstract int getOffTime(RandomSource rnd);

	/**
	 * Returns how many ticks the event is active.
	 * @param rnd
	 * @return
	 */
	public abstract int getOnTime(RandomSource rnd);

	@Override
	public int estimateTimeUntil(Level level, State state) {
		return this.activeStateEstimator.estimateTimeUntil(level, state);
	}

	public ActiveStateEstimator getActiveStateEstimator() {
		return this.activeStateEstimator;
	}
}
