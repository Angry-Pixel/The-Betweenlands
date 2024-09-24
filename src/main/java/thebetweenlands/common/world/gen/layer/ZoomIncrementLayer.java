package thebetweenlands.common.world.gen.layer;

import thebetweenlands.common.world.gen.layer.util.*;

public class ZoomIncrementLayer implements AreaTransformer1 {

	@Override
	public int getParentX(int x) {
		return x >> 1;
	}

	@Override
	public int getParentY(int y) {
		return y >> 1;
	}

	@Override
	public int apply(BigContext<?> context, Area area, int x, int z) {
		int init = area.get(this.getParentX(x), this.getParentY(z));
		context.initRandom(x >> 1 << 1, z >> 1 << 1);
		int pX = x & 1;
		int pZ = z & 1;

		if (pX == 0 && pZ == 0) {
			return init;
		} else {
			int initZ = area.get(this.getParentX(x), this.getParentY(z + 1));
			int rand1 = context.random(init, initZ);

			if (pX == 0 && pZ == 1) {
				return rand1;
			} else {
				int initX = area.get(this.getParentX(x + 1), this.getParentY(z));
				int rand2 = context.random(init, initX);

				if (pX == 1 && pZ == 0) {
					return rand2;
				} else {
					int initXZ = area.get(this.getParentX(x + 1), this.getParentY(z + 1));
					return this.modeOrRandom(context, init, initZ, initX, initXZ);
				}
			}
		}
	}

	protected int modeOrRandom(BigContext<?> context, int first, int second, int third, int fourth) {
		if (second == third && third == fourth) {
			return second;
		} else if (first == second && first == third) {
			return first;
		} else if (first == second && first == fourth) {
			return first;
		} else if (first == third && first == fourth) {
			return first;
		} else if (first == second && third != fourth) {
			return first;
		} else if (first == third && second != fourth) {
			return first;
		} else if (first == fourth && second != third) {
			return first;
		} else if (second == third && first != fourth) {
			return second;
		} else if (second == fourth && first != third) {
			return second;
		} else {
			return third == fourth && first != second ? third : context.random(first, second, third, fourth);
		}
	}
}
