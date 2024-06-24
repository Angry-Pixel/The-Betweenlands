package thebetweenlands.common.world.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import thebetweenlands.api.environment.IPredictableEnvironmentEvent;
import thebetweenlands.common.networking.datamanager.GenericDataAccessor;

public abstract class TimedEnvironmentEvent extends BLEnvironmentEvent implements IPredictableEnvironmentEvent {
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

		public int estimateTimeUntil(State state) {
			if(state == State.ACTIVE) {
				if(this.event.isActive()) {
					return 0;
				} else if(!this.dependencies.isEmpty()) {
					int startDepTime = 0;
					int endDepTime = Integer.MAX_VALUE;

					for(Supplier<ActiveStateEstimator> dep : this.dependencies) {
						ActiveStateEstimator estimator = dep.get();
						int startEstimate = estimator.estimateTimeUntil(State.ACTIVE);
						startDepTime = Math.max(startDepTime, startEstimate);

						int endEstimate;
						if(estimator.event.isActive()) {
							endEstimate = estimator.event.estimateTimeRemaining(State.ACTIVE);
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
				} else if(this.event.canActivate()) {
					return this.event.getTicks();
				}
			} else if(state == State.INACTIVE) {
				if(!this.event.isActive()) {
					return 0;
				} else if(!this.dependencies.isEmpty()) {
					int endDepTime = 0;

					for(Supplier<ActiveStateEstimator> dep : this.dependencies) {
						ActiveStateEstimator estimator = dep.get();
						endDepTime = Math.min(endDepTime, estimator.estimateTimeUntil(State.ACTIVE) + estimator.estimateNextStateDuration());
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

	protected ActiveStateEstimator activeStateEstimator = new ActiveStateEstimator(this);

	public TimedEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

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

		if(!this.getRegistry().isDisabled() && !this.isCurrentStateFromRemote() /*&& this.getLevel().getGameRules().getBoolean(GameruleRegistry.BL_TIMED_EVENTS)*/) {
			if(this.isActive() || this.canActivate()) {
				this.dataManager.set(TICKS, this.getTicks() - 1);
			}

			if(!level.isClientSide() && this.getTicks() <= 0) {
				int nextDuration = this.dataManager.get(NEXT_DURATION);

				if(this.isActive() || this.canActivate()) {
					this.setActive(!this.isActive());
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
	protected boolean canActivate() {
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
	public void setActive(boolean active) {
		if(!active || this.canActivate()) {
			super.setActive(active);
			if(!this.getLevel().isClientSide()) {
				if(!this.isActive()) {
					int offTime = this.getOffTime(this.getLevel().getRandom());
					this.dataManager.set(TICKS, offTime).syncImmediately();
					this.dataManager.set(START_TICKS, offTime);
					this.dataManager.set(NEXT_DURATION, this.getOnTime(this.getLevel().getRandom()));
				} else {
					int onTime = this.getOnTime(this.getLevel().getRandom());
					this.dataManager.set(TICKS, onTime).syncImmediately();
					this.dataManager.set(START_TICKS, onTime);
					this.dataManager.set(NEXT_DURATION, this.getOffTime(this.getLevel().getRandom()));
				}
			}
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().putInt("ticks", this.getTicks());
		this.getData().putInt("startTicks", this.getStartTicks());
		this.getData().putInt("nextDuration", this.dataManager.get(NEXT_DURATION));
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.dataManager.set(TICKS, this.getData().getInt("ticks")).syncImmediately();
		this.dataManager.set(START_TICKS, this.getData().getInt("startTicks"));

		//Backwards compatibility with <= 3.7.1 before NEXT_DURATION existed
		if(!this.getData().contains("nextDuration", Tag.TAG_INT)) {
			this.dataManager.set(NEXT_DURATION, this.isActive() ? this.getOffTime(RandomSource.create()) : this.getOnTime(RandomSource.create()));
		} else {
			this.dataManager.set(NEXT_DURATION, this.getData().getInt("nextDuration"));
		}
	}

	@Override
	public void setDefaults() {
		RandomSource rnd = RandomSource.create();
		this.dataManager.set(TICKS, this.getOffTime(rnd));
		this.dataManager.set(NEXT_DURATION, this.getOnTime(rnd));
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
	public int estimateTimeUntil(State state) {
		return this.activeStateEstimator.estimateTimeUntil(state);
	}

	public ActiveStateEstimator getActiveStateEstimator() {
		return this.activeStateEstimator;
	}
}
