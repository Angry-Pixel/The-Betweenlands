/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package thebetweenlands.common.world.gen.feature.structure.utils;

/**
 * Interface for maze generators. A maze is a 2d array of integers, where each bit==1 is a wall. Use the Direction enum for the bit manipulations.
 *
 * @author synopia
 */
public interface MazeGenerator {

	int[][] generateMaze();

	public enum Direction {
		N(1, 0, -1),
		S(2, 0, 1),
		E(4, 1, 0),
		W(8, -1, 0);
		public final int bit;
		public Direction opposite;
		public final int dx;
		public final int dy;

		// use the static initializer to resolve forward references
		static {
			N.opposite = S;
			S.opposite = N;
			E.opposite = W;
			W.opposite = E;
		}

		Direction(int bit, int dx, int dy) {
			this.bit = bit;
			this.dx = dx;
			this.dy = dy;
		}
	}
}
