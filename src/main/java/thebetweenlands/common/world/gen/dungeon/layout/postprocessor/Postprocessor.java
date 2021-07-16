package thebetweenlands.common.world.gen.dungeon.layout.postprocessor;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;

public abstract class Postprocessor<TMeta> extends LayoutPhase<TMeta> {
	public abstract boolean process();
}
