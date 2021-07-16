package thebetweenlands.common.world.gen.dungeon.layout.topology;

import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Cell;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Connector;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;

public class RandomWalkTopology extends Topology<TopologyMeta> {
	@Override
	public void init(Grid grid, Random rng, TagSupplier tagSupplier) {
		super.init(grid, rng, tagSupplier);

		this.setMeta(TopologyMeta.class, TopologyMeta::new, false);
	}

	@Override
	public boolean create() {
		int cx = 0;
		int cy = 0;
		int cz = 0;
		int px = 0;
		int py = 0;
		int pz = 0;
		int prevdir = 0;

		int minrooms = 50;
		int maxrooms = 80;

		int minXZPerY = 1;
		int maxXZPerY = 3;

		for(int i = 0; i < minrooms + rng.nextInt(maxrooms - minrooms + 1); i++) {
			Cell curr = this.grid.getCell(cx, cy, cz);
			if(curr == null) {
				curr = this.grid.setCell(cx, cy, cz, rng.nextInt(2) + 2, 1, rng.nextInt(2) + 2);
				/*if(rng.nextInt(20) == 0) {
					curr = this.grid.setCell(cx, cy, cz, rng.nextInt(15) + 15, rng.nextInt(2) + 2, rng.nextInt(15) + 15);
				} else {
					curr = this.grid.setCell(cx, cy, cz, rng.nextInt(8) + 3, rng.nextInt(2) + 2, rng.nextInt(8) + 3);
				}*/
				//curr = this.grid.setCell(cx, cy, cz, 6, 6, 6);
				//curr = this.grid.setCell(cx, cy, cz, rng.nextInt(50) + 3, rng.nextInt(4) + 3, rng.nextInt(50) + 3);
			}

			int xzPerY = minXZPerY + rng.nextInt(maxXZPerY - minXZPerY + 1);

			if(rng.nextInt(10) == 0) {
				this.genPath(rng, 10, 20, cx, cy, cz, xzPerY);
			}

			if(i != 0 && (cx != px || cy != py || cz != pz)) {
				Cell prev = this.grid.getCell(px, py, pz);

				Connector c1;
				Connector c2;

				switch(prevdir) {
				default:
					/*case 0:
					c1 = prev.addConnector(prev.getTileSizeX() - 1, rng.nextInt(prev.getTileSizeY()), rng.nextInt(prev.getTileSizeZ()), Direction.POS_X);
					c2 = curr.addConnector(0, rng.nextInt(curr.getTileSizeY()), rng.nextInt(curr.getTileSizeZ()), Direction.NEG_X);
					break;
				case 1:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), prev.getTileSizeY() - 1, rng.nextInt(prev.getTileSizeZ()), Direction.POS_Y);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), 0, rng.nextInt(curr.getTileSizeZ()), Direction.NEG_Y);
					break;
				case 2:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), rng.nextInt(prev.getTileSizeY()), prev.getTileSizeZ() - 1, Direction.POS_Z);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), rng.nextInt(curr.getTileSizeY()), 0, Direction.NEG_Z);
					break;
				case 3:
					c1 = prev.addConnector(0, rng.nextInt(prev.getTileSizeY()), rng.nextInt(prev.getTileSizeZ()), Direction.NEG_X);
					c2 = curr.addConnector(curr.getTileSizeX() - 1, rng.nextInt(curr.getTileSizeY()), rng.nextInt(curr.getTileSizeZ()), Direction.POS_X);
					break;
				case 4:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), 0, rng.nextInt(prev.getTileSizeZ()), Direction.NEG_Y);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), curr.getTileSizeY() - 1, rng.nextInt(curr.getTileSizeZ()), Direction.POS_Y);
					break;
				case 5:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), rng.nextInt(prev.getTileSizeY()), 0, Direction.NEG_Z);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), rng.nextInt(curr.getTileSizeY()), curr.getTileSizeZ() - 1, Direction.POS_Z);
					break;*/

					/*case 0:
					c1 = prev.addConnector(prev.getTileSizeX() - 1, 1, rng.nextInt(prev.getTileSizeZ()), Direction.POS_X);
					c2 = curr.addConnector(0, 1, rng.nextInt(curr.getTileSizeZ()), Direction.NEG_X);
					break;
				case 1:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), prev.getTileSizeY() - 1, rng.nextInt(prev.getTileSizeZ()), Direction.POS_Y);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), 0, rng.nextInt(curr.getTileSizeZ()), Direction.NEG_Y);
					break;
				case 2:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), 1, prev.getTileSizeZ() - 1, Direction.POS_Z);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), 1, 0, Direction.NEG_Z);
					break;
				case 3:
					c1 = prev.addConnector(0, 1, rng.nextInt(prev.getTileSizeZ()), Direction.NEG_X);
					c2 = curr.addConnector(curr.getTileSizeX() - 1, 1, rng.nextInt(curr.getTileSizeZ()), Direction.POS_X);
					break;
				case 4:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), 0, rng.nextInt(prev.getTileSizeZ()), Direction.NEG_Y);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), curr.getTileSizeY() - 1, rng.nextInt(curr.getTileSizeZ()), Direction.POS_Y);
					break;
				case 5:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), 1, 0, Direction.NEG_Z);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), 1, curr.getTileSizeZ() - 1, Direction.POS_Z);
					break;*/

				case 0:
					c1 = prev.addTileConnector(prev.getTileSizeX() - 1, prev.getTileSizeY() / 2, prev.getTileSizeZ() / 2, Direction.POS_X);
					c2 = curr.addTileConnector(0, curr.getTileSizeY() / 2, curr.getTileSizeZ() / 2, Direction.NEG_X);
					break;
				case 1:
					if(false && rng.nextBoolean()) {
						c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() - 1, prev.getTileSizeZ() / 2, Direction.POS_Y);
						c2 = curr.addTileConnector(curr.getTileSizeX() / 2, 0, curr.getTileSizeZ() / 2, Direction.NEG_Y);
					} else {
						if(rng.nextBoolean()) {
							c1 = prev.addTileConnector(prev.getTileSizeX() - 1, prev.getTileSizeY() / 2, prev.getTileSizeZ() / 2, Direction.POS_X);
							c2 = curr.addTileConnector(0, curr.getTileSizeY() / 2, curr.getTileSizeZ() / 2, Direction.NEG_X);
						} else {
							c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() / 2, prev.getTileSizeZ() - 1, Direction.POS_Z);
							c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() / 2, 0, Direction.NEG_Z);
						}
					}
					break;
				case 2:
					c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() / 2, prev.getTileSizeZ() - 1, Direction.POS_Z);
					c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() / 2, 0, Direction.NEG_Z);
					break;
				case 3:
					c1 = prev.addTileConnector(0, prev.getTileSizeY() / 2, prev.getTileSizeZ() / 2, Direction.NEG_X);
					c2 = curr.addTileConnector(curr.getTileSizeX() - 1, curr.getTileSizeY() / 2, curr.getTileSizeZ() / 2, Direction.POS_X);
					break;
				case 4:
					if(false && rng.nextBoolean()) {
						c1 = prev.addTileConnector(prev.getTileSizeX() / 2, 0, prev.getTileSizeZ() / 2, Direction.NEG_Y);
						c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() - 1, curr.getTileSizeZ() / 2, Direction.POS_Y);
					} else {
						if(rng.nextBoolean()) {
							c1 = prev.addTileConnector(0, prev.getTileSizeY() / 2, prev.getTileSizeZ() / 2, Direction.NEG_X);
							c2 = curr.addTileConnector(curr.getTileSizeX() - 1, curr.getTileSizeY() / 2, curr.getTileSizeZ() / 2, Direction.POS_X);
						} else {
							c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() / 2, 0, Direction.NEG_Z);
							c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() / 2, curr.getTileSizeZ() - 1, Direction.POS_Z);
						}
					}
					break;
				case 5:
					c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() / 2, 0, Direction.NEG_Z);
					c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() / 2, curr.getTileSizeZ() - 1, Direction.POS_Z);
					break;
				}

				this.grid.connect(c1, c2, xzPerY);
			}

			px = cx;
			py = cy;
			pz = cz;

			int maxretries = 30;
			for(int j = 0; j < maxretries; j++) {
				int newdir = prevdir;
				if(rng.nextInt(2) == 0) {
					newdir = rng.nextInt(6);
				}
				if((newdir == 1 || newdir == 4) && rng.nextInt(10) != 0) continue; //TODO
				if(i % 10 == 0) newdir = 4;
				//if(newdir == 1 || newdir == 4) continue;
				if(newdir != (prevdir + 3) % 6) {
					switch(newdir) {
					default:
					case 0:
						cx++;
						break;
					case 1:
						cy++;
						break;
					case 2:
						cz++;
						break;
					case 3:
						cx--;
						break;
					case 4:
						cy--;
						break;
					case 5:
						cz--;
						break;
					}
				}

				if(this.grid.getCell(cx, cy, cz) != null) {
					cx = px;
					cy = py;
					cz = pz;
					continue;
				} else if(j == maxretries - 1) {
					return false;
				} else {
					prevdir = newdir;
					break;
				}
			}
		}

		return true;
	}

	private void genPath(Random rng, int min, int max, int cx, int cy, int cz, int xzPerY) {
		int px = 0;
		int py = 0;
		int pz = 0;
		int prevdir = 0;

		for(int i = 0; i < min + rng.nextInt(max - min); i++) {
			Cell curr = this.grid.getCell(cx, cy, cz);

			if(curr == null) {
				curr = this.grid.setCell(cx, cy, cz, 1, 1, 1/*rng.nextInt(8) + 1, rng.nextInt(4) + 1, rng.nextInt(8) + 1*/);
				//curr = this.grid.setCell(cx, cy, cz, 2, 2, 2/*rng.nextInt(8) + 1, rng.nextInt(4) + 1, rng.nextInt(8) + 1*/);
			}

			if(i != 0 && (cx != px || cy != py || cz != pz)) {
				Cell prev = this.grid.getCell(px, py, pz);

				Connector c1;
				Connector c2;

				switch(prevdir) {
				default:
					/*case 0:
					c1 = prev.addConnector(prev.getTileSizeX() - 1, rng.nextInt(prev.getTileSizeY()), rng.nextInt(prev.getTileSizeZ()), Direction.POS_X);
					c2 = curr.addConnector(0, rng.nextInt(curr.getTileSizeY()), rng.nextInt(curr.getTileSizeZ()), Direction.NEG_X);
					break;
				case 1:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), prev.getTileSizeY() - 1, rng.nextInt(prev.getTileSizeZ()), Direction.POS_Y);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), 0, rng.nextInt(curr.getTileSizeZ()), Direction.NEG_Y);
					break;
				case 2:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), rng.nextInt(prev.getTileSizeY()), prev.getTileSizeZ() - 1, Direction.POS_Z);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), rng.nextInt(curr.getTileSizeY()), 0, Direction.NEG_Z);
					break;
				case 3:
					c1 = prev.addConnector(0, rng.nextInt(prev.getTileSizeY()), rng.nextInt(prev.getTileSizeZ()), Direction.NEG_X);
					c2 = curr.addConnector(prev.getTileSizeX() - 1, rng.nextInt(curr.getTileSizeY()), rng.nextInt(curr.getTileSizeZ()), Direction.POS_X);
					break;
				case 4:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), 0, rng.nextInt(prev.getTileSizeZ()), Direction.NEG_Y);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), prev.getTileSizeY() - 1, rng.nextInt(curr.getTileSizeZ()), Direction.POS_Y);
					break;
				case 5:
					c1 = prev.addConnector(rng.nextInt(prev.getTileSizeX()), rng.nextInt(prev.getTileSizeY()), 0, Direction.NEG_Z);
					c2 = curr.addConnector(rng.nextInt(curr.getTileSizeX()), rng.nextInt(curr.getTileSizeY()), prev.getTileSizeZ() - 1, Direction.POS_Z);
					break;*/

				case 0:
					c1 = prev.addTileConnector(prev.getTileSizeX() - 1, prev.getTileSizeY() / 2, prev.getTileSizeZ() / 2, Direction.POS_X);
					c2 = curr.addTileConnector(0, curr.getTileSizeY() / 2, curr.getTileSizeZ() / 2, Direction.NEG_X);
					break;
				case 1:
					c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() - 1, prev.getTileSizeZ() / 2, Direction.POS_Y);
					c2 = curr.addTileConnector(curr.getTileSizeX() / 2, 0, curr.getTileSizeZ() / 2, Direction.NEG_Y);
					break;
				case 2:
					c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() / 2, prev.getTileSizeZ() - 1, Direction.POS_Z);
					c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() / 2, 0, Direction.NEG_Z);
					break;
				case 3:
					c1 = prev.addTileConnector(0, prev.getTileSizeY() / 2, prev.getTileSizeZ() / 2, Direction.NEG_X);
					c2 = curr.addTileConnector(curr.getTileSizeX() - 1, curr.getTileSizeY() / 2, curr.getTileSizeZ() / 2, Direction.POS_X);
					break;
				case 4:
					c1 = prev.addTileConnector(prev.getTileSizeX() / 2, 0, prev.getTileSizeZ() / 2, Direction.NEG_Y);
					c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() - 1, curr.getTileSizeZ() / 2, Direction.POS_Y);
					break;
				case 5:
					c1 = prev.addTileConnector(prev.getTileSizeX() / 2, prev.getTileSizeY() / 2, 0, Direction.NEG_Z);
					c2 = curr.addTileConnector(curr.getTileSizeX() / 2, curr.getTileSizeY() / 2, curr.getTileSizeZ() - 1, Direction.POS_Z);
					break;
				}

				this.grid.connect(c1, c2, xzPerY);
			}

			px = cx;
			py = cy;
			pz = cz;

			int maxretries = 30;
			for(int j = 0; j < maxretries; j++) {
				int newdir = prevdir;
				if(rng.nextInt(2) == 0) {
					newdir = rng.nextInt(6);
				}
				if(newdir == 1 || newdir == 4) continue; //TODO
				if(newdir != (prevdir + 3) % 6) {
					switch(newdir) {
					default:
					case 0:
						cx++;
						break;
					case 1:
						cy++;
						break;
					case 2:
						cz++;
						break;
					case 3:
						cx--;
						break;
					case 4:
						cy--;
						break;
					case 5:
						cz--;
						break;
					}
				}

				/*if(this.grid.getCell(cx, cy, cz) != null) {
					cx = px;
					cy = py;
					cz = pz;
					continue;
				} else*/ if(j == maxretries - 1) {
					return;
				} else {
					prevdir = newdir;
					break;
				}
			}
		}
	}
}
