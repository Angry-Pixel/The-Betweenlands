package thebetweenlands.common.world.gen.dungeon.layout;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase.TagSupplier;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.LayoutCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.pathfinder.Pathfinder;
import thebetweenlands.common.world.gen.dungeon.layout.postprocessor.Postprocessor;
import thebetweenlands.common.world.gen.dungeon.layout.topology.Topology;

public class LayoutGenerator {
	public static interface LayoutSequenceBase<T extends LayoutPhase<?>> {
		public LayoutSequenceBase<T> criterion(Supplier<? extends LayoutCriterion<? super T>> criterion);

		public LayoutGenerator finish();
	}

	public static interface LayoutSequence<T extends LayoutPhase<?>> extends LayoutSequenceBase<T> {
		@Override
		public TopologySequence<T> criterion(Supplier<? extends LayoutCriterion<? super T>> criterion);

		public <F extends Topology<?>> PostprocessSequence<F> topology(Supplier<? extends F> topology);

		public LayoutSequence<T> watchdog(Supplier<? extends LayoutCriterion<LayoutPhase<?>>> criterion);
	}

	public static interface TopologySequence<T extends LayoutPhase<?>> extends LayoutSequenceBase<T> {
		@Override
		public TopologySequence<T> criterion(Supplier<? extends LayoutCriterion<? super T>> criterion);

		public <F extends Topology<?>> PostprocessSequence<F> topology(Supplier<? extends F> topology);
	}

	public static interface PostprocessSequence<T extends LayoutPhase<?>> extends LayoutSequenceBase<T> {
		@Override
		public PostprocessSequence<T> criterion(Supplier<? extends LayoutCriterion<? super T>> criterion);

		public <F extends Postprocessor<?>> PostprocessSequence<F> postprocessor(Supplier<? extends F> postprocessor);

		public <F extends Pathfinder<?>> PathSequence<F> pathfinder(Supplier<? extends F> pathfinder);
	}

	public static interface PathSequence<T extends LayoutPhase<?>> extends LayoutSequenceBase<T> {
		@Override
		public PathSequence<T> criterion(Supplier<? extends LayoutCriterion<? super T>> criterion);

		public <F extends Pathfinder<?>> PathSequence<F> pathfinder(Supplier<? extends F> pathfinder);
	}

	private static class Builder<T extends LayoutPhase<?>> implements TopologySequence<T>, PostprocessSequence<T>, PathSequence<T> {
		private Supplier<? extends LayoutPhase<?>> lastPhaseFactory;

		private Supplier<? extends Topology<?>> topologyFactory = () -> new Topology<Object>() {
			@Override
			public boolean create() {
				return false;
			}
		};
		private List<Supplier<? extends Postprocessor<?>>> postprocessorFactories = new ArrayList<>();
		private List<Supplier<? extends Pathfinder<?>>> pathfinderFactories = new ArrayList<>();

		private List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> watchdogCriterionFactories = new ArrayList<>();
		private List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> globalCriterionFactories = new ArrayList<>();
		private Map<Pair<Supplier<? extends LayoutPhase<?>>, Integer>, List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>>> phaseCriterionFactories = new HashMap<>();
		private TObjectIntMap<Supplier<? extends LayoutPhase<?>>> factoryCounters = new TObjectIntHashMap<>();

		private Builder() { }

		@SuppressWarnings("unchecked")
		@Override
		public <F extends Topology<?>> PostprocessSequence<F> topology(Supplier<? extends F> topology) {
			this.topologyFactory = topology;
			this.factoryCounters.adjustOrPutValue(topology, 1, 1);
			this.lastPhaseFactory = topology;
			return (PostprocessSequence<F>) this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <F extends Postprocessor<?>> PostprocessSequence<F> postprocessor(Supplier<? extends F> postprocessor) {
			this.postprocessorFactories.add(postprocessor);
			this.factoryCounters.adjustOrPutValue(postprocessor, 1, 1);
			this.lastPhaseFactory = postprocessor;
			return (PostprocessSequence<F>) this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <F extends Pathfinder<?>> PathSequence<F> pathfinder(Supplier<? extends F> pathfinder) {
			this.pathfinderFactories.add(pathfinder);
			this.factoryCounters.adjustOrPutValue(pathfinder, 1, 1);
			this.lastPhaseFactory = pathfinder;
			return (PathSequence<F>) this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Builder<T> criterion(Supplier<? extends LayoutCriterion<? super T>> criterion) {
			if(this.lastPhaseFactory == null) {
				this.globalCriterionFactories.add((Supplier<? extends LayoutCriterion<LayoutPhase<?>>>) criterion);
			} else {
				Pair<Supplier<? extends LayoutPhase<?>>, Integer> key = Pair.of(this.lastPhaseFactory, this.factoryCounters.get(this.lastPhaseFactory));
				List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> criteria = this.phaseCriterionFactories.get(key);
				if(criteria == null) {
					this.phaseCriterionFactories.put(key, criteria = new ArrayList<>());
				}
				criteria.add((Supplier<? extends LayoutCriterion<LayoutPhase<?>>>) criterion);
			}
			return this;
		}

		@Override
		public LayoutGenerator finish() {
			return new LayoutGenerator(this.topologyFactory, this.postprocessorFactories, this.pathfinderFactories, this.watchdogCriterionFactories, this.globalCriterionFactories, this.phaseCriterionFactories);
		}
	}

	public static LayoutSequence<LayoutPhase<?>> sequence() {
		Builder<LayoutPhase<?>> builder = new Builder<LayoutPhase<?>>();
		return new LayoutSequence<LayoutPhase<?>>() {
			@SuppressWarnings("unchecked")
			@Override
			public TopologySequence<LayoutPhase<?>> criterion(Supplier<? extends LayoutCriterion<? super LayoutPhase<?>>> criterion) {
				builder.criterion(criterion);
				return (TopologySequence<LayoutPhase<?>>)(TopologySequence<?>)builder;
			}

			@SuppressWarnings("unchecked")
			@Override
			public <F extends Topology<?>> PostprocessSequence<F> topology(Supplier<? extends F> topology) {
				builder.topology(topology);
				return (PostprocessSequence<F>)(PostprocessSequence<?>)builder;
			}

			@Override
			public LayoutSequence<LayoutPhase<?>> watchdog(Supplier<? extends LayoutCriterion<LayoutPhase<?>>> criterion) {
				builder.watchdogCriterionFactories.add((Supplier<? extends LayoutCriterion<LayoutPhase<?>>>) criterion);
				return this;
			}

			@Override
			public LayoutGenerator finish() {
				return builder.finish();
			}
		};
	}

	private final Supplier<? extends Topology<?>> topologyFactory;
	private final List<Supplier<? extends Postprocessor<?>>> postprocessorFactories;
	private final List<Supplier<? extends Pathfinder<?>>> pathfinderFactories;
	private final List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> watchdogCriterionFactories;
	private final List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> globalCriterionFactories;
	private final Map<Pair<Supplier<? extends LayoutPhase<?>>, Integer>, List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>>> phaseCriterionFactories;

	private class Context implements TagSupplier {
		private int reservedTags = 0;
		private Map<Class<?>, Integer> sharedMetaIds = new HashMap<>();
		private int reservedMetaIds = 0;
		private TObjectIntMap<LayoutPhase<?>> counters = new TObjectIntHashMap<>();
		private Set<LayoutPhase<?>> initialized = new HashSet<>();

		private final Topology<?> topology;
		private final List<Postprocessor<?>> postprocessors = new ArrayList<>();
		private final List<Pathfinder<?>> pathfinders = new ArrayList<>();
		private final List<LayoutCriterion<LayoutPhase<?>>> watchdogCriteria = new ArrayList<>();
		private final List<LayoutCriterion<LayoutPhase<?>>> globalCriteria = new ArrayList<>();
		private final Map<Pair<LayoutPhase<?>, Integer>, List<LayoutCriterion<LayoutPhase<?>>>> phaseCriteria = new HashMap<>();

		private Context() {
			TObjectIntMap<Supplier<? extends LayoutPhase<?>>> factoryCounters = new TObjectIntHashMap<>();
			TObjectIntMap<LayoutPhase<?>> instanceCounters = new TObjectIntHashMap<>();

			this.topology = this.createPhase(LayoutGenerator.this.topologyFactory, new ArrayList<>(), factoryCounters, instanceCounters).get(0);
			LayoutGenerator.this.postprocessorFactories.forEach(factory -> this.createPhase(factory, this.postprocessors, factoryCounters, instanceCounters));
			LayoutGenerator.this.pathfinderFactories.forEach(factory -> this.createPhase(factory, this.pathfinders, factoryCounters, instanceCounters));
			LayoutGenerator.this.watchdogCriterionFactories.forEach(factory -> this.watchdogCriteria.add(factory.get()));
			LayoutGenerator.this.globalCriterionFactories.forEach(factory -> this.globalCriteria.add(factory.get()));
		}

		private <T extends LayoutPhase<?>> List<T> createPhase(Supplier<? extends T> factory, List<T> list, TObjectIntMap<Supplier<? extends LayoutPhase<?>>> factoryCounters, TObjectIntMap<LayoutPhase<?>> instanceCounters) {
			T instance = factory.get();
			list.add(instance);
			int instanceCounter = instanceCounters.adjustOrPutValue(instance, 1, 1);
			int factoryCounter = factoryCounters.adjustOrPutValue(factory, 1, 1);
			List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> criterionFactories = LayoutGenerator.this.phaseCriterionFactories.get(Pair.of(factory, factoryCounter));
			if(criterionFactories != null) {
				for(Supplier<? extends LayoutCriterion<LayoutPhase<?>>> criterionFactory : criterionFactories) {
					Pair<LayoutPhase<?>, Integer> key = Pair.of(instance, instanceCounter);
					List<LayoutCriterion<LayoutPhase<?>>> criteria = this.phaseCriteria.get(key);
					if(criteria == null) {
						this.phaseCriteria.put(key, criteria = new ArrayList<>());
					}
					criteria.add(criterionFactory.get());
				}
			}
			return list;
		}

		@Override
		public int reserve(int num) {
			int startTag = this.reservedTags;
			this.reservedTags += num;
			return startTag;
		}
	}

	private LayoutGenerator(Supplier<? extends Topology<?>> topologyFactory, List<Supplier<? extends Postprocessor<?>>> postprocessorFactories, List<Supplier<? extends Pathfinder<?>>> pathfinderFactories,
			List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> watchdogCriterionFactories, List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>> globalCriterionFactories,
			Map<Pair<Supplier<? extends LayoutPhase<?>>, Integer>, List<Supplier<? extends LayoutCriterion<LayoutPhase<?>>>>> phaseCriterionFactories) {
		this.topologyFactory = topologyFactory;
		this.postprocessorFactories = postprocessorFactories;
		this.pathfinderFactories = pathfinderFactories;
		this.watchdogCriterionFactories = watchdogCriterionFactories;
		this.globalCriterionFactories = globalCriterionFactories;
		this.phaseCriterionFactories = phaseCriterionFactories;
	}

	public boolean generate(Grid grid, Random rng) {
		Context ctx = new Context();

		if(!runTopology(ctx, grid, rng)) {
			return false;
		}

		if(!runPostprocessors(ctx, grid, rng)) {
			return false;
		}

		if(!runPathfinders(ctx, grid, rng)) {
			return false;
		}

		return true;
	}

	private static void initPhase(Context ctx, Grid grid, Random rng, LayoutPhase<?> phase) {
		int prevReservedTags = ctx.reservedTags;

		phase.init(grid, rng, ctx);

		if(prevReservedTags != ctx.reservedTags) {
			BitSet tagsMask = new BitSet();
			tagsMask.set(prevReservedTags, ctx.reservedTags);
			phase.setTagsMask(tagsMask);
		}

		if(phase.getMeta() != null) {
			if(phase.isMetaShared()) {
				Class<?> metaCls = phase.getMetaClass();
				if(ctx.sharedMetaIds.containsKey(metaCls)) {
					phase.setMetaId(ctx.sharedMetaIds.get(metaCls));
				} else {
					int newId = ctx.reservedMetaIds++;
					ctx.sharedMetaIds.put(metaCls, newId);
					phase.setMetaId(newId);
				}
			} else {
				phase.setMetaId(ctx.reservedMetaIds++);
			}
		}
	}

	private static void clearData(Grid grid, List<? extends LayoutPhase<?>> phases) {
		for(LayoutPhase<?> phase : phases) {
			grid.clearTags(phase);
			grid.clearMeta(phase);
		}
	}

	private static boolean runTopology(Context ctx, Grid grid, Random rng) {
		grid.reset();

		ctx.counters.remove(ctx.topology);

		if(ctx.initialized.add(ctx.topology)) {
			initPhase(ctx, grid, rng, ctx.topology);
		}

		if(!runPhase(ctx, ctx.topology::create, ctx.topology, () -> {
			grid.reset();
			return true;
		}, () -> {
			grid.reset();
			return true;
		})) {
			return false;
		}

		grid.resolve();

		return true;
	}

	private static boolean runPostprocessors(Context ctx, Grid grid, Random rng) {
		for(Postprocessor<?> postprocessor : ctx.postprocessors) {
			ctx.counters.remove(postprocessor);
		}

		for(Postprocessor<?> postprocessor : ctx.postprocessors) {
			if(ctx.initialized.add(postprocessor)) {
				initPhase(ctx, grid, rng, postprocessor);
			}

			if(!runPhase(ctx, postprocessor::process, postprocessor, () -> {
				clearData(grid, ctx.postprocessors);
				grid.resolve();
				return true;
			}, () -> {
				return runTopology(ctx, grid, rng);
			})) {
				return false;
			}
		}

		return true;
	}

	private static boolean runPathfinders(Context ctx, Grid grid, Random rng) {
		for(Pathfinder<?> pathfinder : ctx.pathfinders) {
			ctx.counters.remove(pathfinder);
		}

		for(Pathfinder<?> pathfinder : ctx.pathfinders) {
			if(ctx.initialized.add(pathfinder)) {
				initPhase(ctx, grid, rng, pathfinder);
			}

			if(!runPhase(ctx, pathfinder::process, pathfinder, () -> {
				clearData(grid, ctx.postprocessors);
				clearData(grid, ctx.pathfinders);
				grid.resolve();
				return runPostprocessors(ctx, grid, rng);
			}, () -> {
				if(!runTopology(ctx, grid, rng)) {
					return false;
				}
				if(!runPostprocessors(ctx, grid, rng)) {
					return false;
				}
				return true;
			})) {
				return false;
			}
		}

		return true;
	}

	private static boolean runPhase(Context ctx, BooleanSupplier task, LayoutPhase<?> phase, BooleanSupplier retryPrevious, BooleanSupplier retryFully) {
		int counter = ctx.counters.adjustOrPutValue(phase, 1, 1);
		List<LayoutCriterion<LayoutPhase<?>>> phaseCriteria = ctx.phaseCriteria.get(Pair.of(phase, counter));

		int iteration = -1;
		boolean result;
		loop: while(true) {
			iteration++;

			for(LayoutCriterion<LayoutPhase<?>> criterion : ctx.watchdogCriteria) {
				criterion.reset();
				criterion.check(phase, false, iteration);

				if(criterion.isAbort()) {
					//Abort
					return false;
				}
			}

			result = task.getAsBoolean();

			if(phaseCriteria != null) {
				for(LayoutCriterion<LayoutPhase<?>> criterion : phaseCriteria) {
					criterion.reset();
					criterion.check(phase, result, iteration);

					if(criterion.isAbort()) {
						//Abort
						return false;
					} else if(criterion.isRetry()) {
						//Retry
						if(criterion.isRetryFully() && retryFully != null) {
							if(!retryFully.getAsBoolean()) {
								return false;
							}
						} else if(criterion.isRetryPrevious() && retryPrevious != null) {
							if(!retryPrevious.getAsBoolean()) {
								return false;
							}
						}
						continue loop;
					}
				}
			}

			for(LayoutCriterion<LayoutPhase<?>> criterion : ctx.globalCriteria) {
				criterion.reset();
				criterion.check(phase, result, iteration);

				if(criterion.isAbort()) {
					//Abort
					return false;
				} else if(criterion.isRetry()) {
					//Retry
					if(criterion.isRetryFully() && retryFully != null) {
						if(!retryFully.getAsBoolean()) {
							return false;
						}
					} else if(criterion.isRetryPrevious() && retryPrevious != null) {
						if(!retryPrevious.getAsBoolean()) {
							return false;
						}
					}
					continue loop;
				}
			}

			//Default is abort if false
			return result;
		}
	}
}
