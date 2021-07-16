package thebetweenlands.common.world.gen.dungeon.layout.pathfinder;

import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;

public abstract class Pathfinder<TMeta> extends LayoutPhase<TMeta> {
	public abstract boolean process();
}
