package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.PlacementStrategy.Placement;

public class PathSpaceType extends SpaceType {

	@Override
	protected Instance instantiate() {
		return new Instance() {
			@Override
			public boolean generate(INodeGridHandle grid, Placement placement, ISpaceReservations reservations, Random rng) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

}
