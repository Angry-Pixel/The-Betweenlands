package thebetweenlands.api.rune.impl;

import com.google.common.base.Preconditions;

import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;

public class RuneStats {
	public static final class Builder {
		private Aspect aspect;
		private int costRatio = 1;
		private float successDuration = 0.1F;
		private float failDuration = 0.1F;
		
		private Builder() { }

		public Builder aspect(IAspectType type, int cost) {
			this.aspect = new Aspect(type, cost);
			return this;
		}

		public Builder costRatio(int internalPerNormalAspect) {
			this.costRatio = internalPerNormalAspect;
			return this;
		}

		/**
		 * {@link RuneStats#getSuccessDuration()}, {@link RuneStats#getFailDuration()}
		 * @param successDuration - duration when rune was successful
		 * @param failDuration - duration when rune failed
		 * @return
		 */
		public Builder duration(float successDuration, float failDuration) {
			this.successDuration = successDuration;
			this.failDuration = failDuration;
			return this;
		}
		
		/**
		 * {@link RuneStats#getSuccessDuration()}, {@link RuneStats#getFailDuration()}
		 * @param duration - duration
		 */
		public Builder duration(float duration) {
			this.successDuration = this.failDuration = duration;
			return this;
		}

		public RuneStats build() {
			Preconditions.checkNotNull(this.aspect, "Must specify an aspect type and cost");
			return new RuneStats(this);
		}
	}

	private final Aspect aspect;
	private final int costRatio;
	private final float successDuration;
	private final float failDuration;

	private RuneStats(Builder builder) {
		this.aspect = builder.aspect;
		this.costRatio = builder.costRatio;
		this.successDuration = builder.successDuration;
		this.failDuration = builder.failDuration;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Aspect getAspect() {
		return this.aspect;
	}

	public int getCostRatio() {
		return this.costRatio;
	}

	/**
	 * Returns the static duration that specifies how long a rune's
	 * activation takes until it has finished if the rune was successful. 
	 * @return static duration that specifies how long a rune's
	 * activation takes until it has finished if the rune was successful. If the duration <= 0 then
	 * the activation is either instant or the duration is dynamic
	 */
	public float getSuccessDuration() {
		return this.successDuration;
	}
	
	/**
	 * Returns the static duration that specifies how long a rune's
	 * activation takes until it has finished if the rune failed.
	 * @return static duration that specifies how long a rune's
	 * activation takes until it has finished if the rune failed. If the duration <= 0 then
	 * the activation is either instant or the duration is dynamic
	 */
	public float getFailDuration() {
		return this.failDuration;
	}
}
