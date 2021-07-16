package thebetweenlands.common.world.gen.dungeon;

import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutGenerator;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.LoggingCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.PathPercentageCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.PhaseLimitCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.RetryCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link;
import thebetweenlands.common.world.gen.dungeon.layout.pathfinder.SimplePathfinder;
import thebetweenlands.common.world.gen.dungeon.layout.postprocessor.CompactionPostprocessor;
import thebetweenlands.common.world.gen.dungeon.layout.topology.RandomWalkTopology;

public class Test {
	public static Test TEST = new Test();

	public Grid grid = new Grid(new Random(), 16);

	public RandomWalkTopology topology = new RandomWalkTopology();
	public CompactionPostprocessor postprocessor = new CompactionPostprocessor();
	public SimplePathfinder pathfinder = new SimplePathfinder();

	private boolean isShrinking = false;
	private int counter = 0;

	public static Link invalidLink = null;

	public void init() {
		System.out.println("---------------------------- Generate ----------------------------");

		this.isShrinking = false;
		this.counter = 0;

		this.grid = new Grid(new Random(), 8);

		this.isShrinking = false;
		LayoutGenerator.sequence()
		.watchdog(() -> new PhaseLimitCriterion(9))
		.watchdog(() -> new LoggingCriterion())
		.topology(() -> this.topology)
		.criterion(() -> new RetryCriterion(3, false, true))
		.postprocessor(() -> this.postprocessor)
		.criterion(() -> new RetryCriterion(3, false, true))
		.pathfinder(() -> this.pathfinder)
		.criterion(() -> new PathPercentageCriterion(0.5f, 3, true))
		.finish()
		.generate(this.grid, new Random());
	}

	public void step() {
		this.grid.clearMeta(this.pathfinder);

		/*this.isShrinking = false;
		this.grid.resolve(new Random());
		this.postprocessor.init(this.grid, new Random(), num -> 0);*/

		/*this.isShrinking = false;
		this.grid.resolve(new Random());
		this.postprocessor.init(this.grid, new Random(), num -> 0);
		this.postprocessor.process();*/

		/*if(!this.isShrinking) {
			if(!this.grid.resolveIteration(new Random())) {
				this.isShrinking = true;
				this.counter = 0;
			}
		} else {
			if(this.counter == 0) this.postprocessor.init(this.grid, new Random(), num -> 0);
			System.out.println((++this.counter) + " " + this.postprocessor.shrinkStep(new Random(), 1));
		}*/

		if(!this.isShrinking) {
			this.grid.resolve();
			this.isShrinking = true;
			this.counter = 0;
		} else {
			if(this.counter == 0) this.postprocessor.init(this.grid, new Random(), num -> 0);
			System.out.println((++this.counter) + " " + this.postprocessor.compactIteration(new Random(), 1));
			if(this.counter > 20) {
				this.isShrinking = false;
			}
		}

		/*if(!this.isShrinking) {
			this.grid.resolve(new Random());
			this.isShrinking = true;
		} else {
			this.postprocessor.init(this.grid, new Random(), num -> 0);
			this.postprocessor.process();
			this.isShrinking = false;
		}*/
	}
}
