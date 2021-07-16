package thebetweenlands.common.world.gen.dungeon.layout.topology;

import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;

public abstract class Topology<TMeta> extends LayoutPhase<TMeta> {
	public abstract boolean create();
}
