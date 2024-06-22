package thebetweenlands.common.world.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.ChunkPos;


// make a line, get block's closest point to the line and return true if in range

public class VaneRiver {

	// Position
	public final int x, y;
	public final int count;

	public final List<Point> vectors = new ArrayList<Point>();

	public VaneRiver(Random random, ChunkPos chunkpos) {

		this.x = random.nextInt(16) + (chunkpos.x * 16);
		this.y = random.nextInt(16) + (chunkpos.z * 16);
		this.count = random.nextInt(2) + 2;

		List<Float> angles = new ArrayList<Float>();

		// generate points
		for (int line = 0; line < this.count * 3; line++) {
			float angle = random.nextFloat();
			if (line < 3) {
				angles.add(angle);
				angle = angle * 360;
				this.vectors.add(new Point((int) Math.round(Math.sin(Math.toRadians(angle)) * 8) + this.x, (int) Math.round(Math.cos(Math.toRadians(angle)) * 16) + this.y));
				continue;
			}

			int index = line % this.count;
			float outangle = angles.get(index);

			this.vectors.add(new Point((int) Math.round(Math.sin(Math.toRadians((outangle * 360) + (angle * 1))) * 8) + this.vectors.get(line - this.count).x, (int) Math.round(Math.cos(Math.toRadians((outangle * 360) + (angle * 1))) * 16) + this.vectors.get(line - this.count).y));
		}
	}

	// Sample at block pos
	public double sample(int x, int y) {
		double outvalue = 0;

		// Eatch point
		for (int pointer = 0; pointer < this.count * 3; pointer++) {

			int connector = -1;

			int conx = this.x;
			int cony = this.y;

			if (pointer > 3) {
				connector = pointer - this.count;

				conx = this.vectors.get(connector).x;
				cony = this.vectors.get(connector).y;
			}


			double thisvalue = 0;
			double dist = Math.sqrt(Math.pow(conx - this.vectors.get(pointer).x, 2) + Math.pow(cony - this.vectors.get(pointer).y, 2));

			double dot = (((x - conx) * (this.vectors.get(pointer).x - conx)) + ((y - cony) * (this.vectors.get(pointer).y - cony))) / (dist * dist);

			double linepointx = (conx + (dot * (this.vectors.get(pointer).x - conx)));
			double linepointy = (cony + (dot * (this.vectors.get(pointer).y - cony)));

			// Check if out of range
			// X
			if (conx < this.vectors.get(pointer).x) {
				if (linepointx < conx) {
					linepointx = conx;
				} else if (linepointx > this.vectors.get(pointer).x) {
					linepointx = this.vectors.get(pointer).x;
				}
			} else if (conx > this.vectors.get(pointer).x) {
				if (linepointx > conx) {
					linepointx = conx;
				} else if (linepointx < this.vectors.get(pointer).x) {
					linepointx = this.vectors.get(pointer).x;
				}
			}

			// Y
			if (cony < this.vectors.get(pointer).y) {
				if (linepointy < cony) {
					linepointy = cony;
				} else if (linepointy > this.vectors.get(pointer).y) {
					linepointy = this.vectors.get(pointer).y;
				}
			} else if (cony > this.vectors.get(pointer).y) {
				if (linepointy > cony) {
					linepointy = cony;
				} else if (linepointy < this.vectors.get(pointer).y) {
					linepointy = this.vectors.get(pointer).y;
				}
			}


			double dist2 = Math.sqrt(Math.pow(x - linepointx, 2) + Math.pow(y - linepointy, 2));

			if (dist2 < 1) {
				thisvalue = 1;
			} else if (dist2 <= 2) {
				thisvalue = (-dist2 + 2) / 2;
			}

			if (thisvalue > outvalue) {
				outvalue = thisvalue;
			}
		}

		return outvalue;
	}
}
