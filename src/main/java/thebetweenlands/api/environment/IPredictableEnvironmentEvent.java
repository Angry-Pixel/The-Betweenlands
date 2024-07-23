package thebetweenlands.api.environment;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

public interface IPredictableEnvironmentEvent extends IEnvironmentEvent {
	/**
	 * Describes a state that an event can be in. An event can only be in one state
	 * at a time.
	 */
	class State {
		public static final State ACTIVE = new State();
		public static final State INACTIVE = new State();

		private static final Collection<State> DEFAULT_STATES = ImmutableList.of(State.ACTIVE, State.INACTIVE);
	}

	/**
	 * Returns a collection of all states of this event that could potentially be estimated.
	 * @return
	 */
	default Collection<State> getStates() {
		return State.DEFAULT_STATES;
	}

	/**
	 * Estimates the time in ticks until the specified state. If the the event is already in the specified state returns 0.
	 * If the time cannot be estimated returns -1.
	 * @param state State to estimate time for
	 * @return
	 */
	int estimateTimeUntil(Level level, State state);

	/**
	 * Estimates the time in ticks remaining for the specified state. If the event is not in the specified state returns -1.
	 * @param state State to estimate time for
	 * @return
	 */
	default int estimateTimeRemaining(Level level, State state) {
		int remaining = -1;

		if(this.estimateTimeUntil(level, state) == 0) {
			//In most cases the remaining time can be estimated by taking
			//the minimum time until the next state. This will work unless
			//the time for the next state cannot be estimated, in which
			//case a custom implementation is needed for the time remaining

			for(State other : this.getStates()) {
				if(!Objects.equals(state, other)) {
					int estimation = this.estimateTimeUntil(level, other);

					if(estimation > 0) {
						if(remaining < 0) {
							remaining = estimation;
						} else {
							remaining = Math.min(remaining, estimation);
						}
					}
				}
			}
		}

		return remaining;
	}

	@Nullable
	ResourceLocation[] getVisionTextures();

	@Nullable
	SoundEvent getChimesSound();
}
