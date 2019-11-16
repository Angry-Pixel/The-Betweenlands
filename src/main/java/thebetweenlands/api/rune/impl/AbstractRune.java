package thebetweenlands.api.rune.impl;

import javax.annotation.Nullable;

import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.rune.INode;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public abstract class AbstractRune<T extends AbstractRune<T>> implements INode<T, RuneExecutionContext> {

	public static abstract class Blueprint<T extends AbstractRune<T>> implements INodeBlueprint<T, RuneExecutionContext> {
		private final RuneStats stats;

		public Blueprint(RuneStats stats) {
			this.stats = stats;
		}

		public RuneStats getStats() {
			return this.stats;
		}

		@Override
		public void run(T state, RuneExecutionContext context, INodeIO io) {
			RuneStats stats = this.getStats();
			Aspect cost = stats.getAspect();

			if(this.updateFuelConsumption(state, context, io, stats, cost.type, cost.amount)) {
				this.activate(state, context, io);

				// On last parallel rune activation on last branch drain any left over fuel to be consumed
				if(context.getParallelActivation() == context.getParallelActivationCount() - 1 && context.getBranch() == context.getBranchCount() - 1) {
					this.drainLeftOverFuel(state, context);
				}

				// Schedule sleep task to wait out duration if no other task was scheduled
				if(io.scheduled() == null && ((io.failed() && stats.getFailDuration() > 0) || (!io.failed() && stats.getSuccessDuration() > 0))) {
					io.schedule(scheduler -> {
						scheduler.sleep(io.failed() ? stats.getFailDuration() : stats.getSuccessDuration());
						scheduler.terminate();
					});
				}
			} else {
				io.fail();
				io.terminate();
			}
		}

		@Override
		public void terminate(T state, RuneExecutionContext context) {
			this.drainLeftOverFuel(state, context);
		}

		/**
		 * Drains any left over buffered fuel to be consumed and if bufferCost < costRatio drains at least 1 more aspect.
		 * @param state - node instance created by create(INodeComposition, INodeConfiguration)
		 * @param context - context that is executing the node
		 */
		protected void drainLeftOverFuel(T state, RuneExecutionContext context) {
			// If some buffered cost is left drain everything and if bufferCost < costRatio drain at least 1 more aspect
			if(state.bufferedCost > 0) {
				RuneStats stats = this.getStats();
				this.updateFuelConsumption(state, context, null, stats, stats.getAspect().type, state.bufferedCost < stats.getCostRatio() ? stats.getCostRatio() : 0);
			}
			state.bufferedCost = 0;
		}

		/**
		 * Updates the fuel consumption and drains aspect from the container if necessary.
		 * @param state - node instance created by create(INodeComposition, INodeConfiguration)
		 * @param context - context that is executing the node
		 * @param io - node input/output that allow reading input values and writing output values. May be null if no
		 * I/O is available while updating fuel consumption
		 * @param stats - rune stats of this blueprint
		 * @param type - aspect type to drain
		 * @param cost - amount of aspect to drain
		 * @return <i>true</i> if the cost was successfully drained, <i>false</i> otherwise
		 */
		protected boolean updateFuelConsumption(T state, RuneExecutionContext context, @Nullable INodeIO io, RuneStats stats, IAspectType type, int cost) {
			AspectContainer container = context.getAspectBuffer().get(type);

			state.bufferedCost += cost;

			if(state.bufferedCost >= stats.getCostRatio()) {
				int drain = Math.floorDiv(state.bufferedCost, stats.getCostRatio());

				state.bufferedCost %= stats.getCostRatio();

				if(container.drain(type, drain) >= drain) {
					return true;
				} else {
					return false;
				}
			}

			return true;
		}

		/**
		 * Called when the rune is activated.
		 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
		 * @param context - context that is executing the node
		 * @param io - node input/output that allow reading input values and writing output values
		 */
		protected abstract void activate(T state, RuneExecutionContext context, INodeIO io);
	}

	private final Blueprint<T> blueprint;
	private final INodeComposition<RuneExecutionContext> composition;
	private final INodeConfiguration configuration;

	protected int bufferedCost = 0;

	protected AbstractRune(Blueprint<T> blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		this.blueprint = blueprint;
		this.composition = composition;
		this.configuration = configuration;
	}

	@Override
	public final Blueprint<T> getBlueprint() {
		return this.blueprint;
	}

	@Override
	public final INodeConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public final INodeComposition<RuneExecutionContext> getComposition() {
		return this.composition;
	}
}
