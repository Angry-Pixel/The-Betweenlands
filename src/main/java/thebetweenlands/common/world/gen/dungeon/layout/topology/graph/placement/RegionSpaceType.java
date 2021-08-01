package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.PlacementStrategy.Placement;

public class RegionSpaceType extends SpaceType {

	@Override
	protected Instance instantiate() {
		return new Instance() {
			@Override
			public boolean generate(INodeGridHandle grid, Placement placement, ISpaceReservations reservations, Random rng) {
				if(this.getTag() == null) {
					throw new IllegalStateException();
				}

				if(placement != null) {
					Pos pos = placement.getPos();

					for(int i = 0; i < 7; i++) {
						int xo = 0;
						int yo = 0;
						int zo = 0;

						switch(i) {
						case 0:
							break;
						case 1:
							xo = 1;
							break;
						case 2:
							yo = 1;
							break;
						case 3:
							zo = 1;
							break;
						case 4:
							xo = -1;
							break;
						case 5:
							yo = -1;
							break;
						case 6:
							zo = -1;
							break;
						}

						Pos offsetPos = new Pos(pos.x + xo, pos.y + yo, pos.z + zo);
						reservations.reserve(offsetPos, false);
					}
				} else {
					for(IGridNodeHandle node : grid.get()) {
						if(node.isSpaceTagged(this.getTag())) {
							Pos pos = node.getPos();

							for(int i = 0; i < 6; i++) {
								int xo = 0;
								int yo = 0;
								int zo = 0;

								switch(i) {
								case 0:
									xo = 1;
									break;
								case 1:
									yo = 1;
									break;
								case 2:
									zo = 1;
									break;
								case 3:
									xo = -1;
									break;
								case 4:
									yo = -1;
									break;
								case 5:
									zo = -1;
									break;
								}

								Pos offsetPos = new Pos(pos.x + xo, pos.y + yo, pos.z + zo);
								reservations.reserve(offsetPos, false);
							}
						}
					}
				}

				return true;
			}

			@Override
			public boolean requiresRefresh(INodeGridHandle grid, IGridNodeHandle node) {
				return true;
			}
		};
	}

}
