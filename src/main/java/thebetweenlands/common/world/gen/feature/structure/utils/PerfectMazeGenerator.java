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

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Generator for perfect mazes. In a perfect maze, from each position in the maze there is always a path to each other position.
 *
 * @author synopia
 */
public class PerfectMazeGenerator implements MazeGenerator {

	private int[][] maze;
	private int width;
	private int height;
	private Random random;

	public PerfectMazeGenerator(int width, int height) {
		this.width = width;
		this.height = height;
		maze = new int[width][height];
		random = new Random();
	}

	@Override
	public int[][] generateMaze() {
		generateMaze(0, 0);
		return maze;
	}

	private void generateMaze(int cx, int cy) {
		Direction[] dirs = Direction.values();
		Collections.shuffle(Arrays.asList(dirs), random);
		for (Direction dir : dirs) {
			int nx = cx + dir.dx;
			int ny = cy + dir.dy;
			if (between(nx, width) && between(ny, height) && maze[nx][ny] == 0) {
				maze[cx][cy] |= dir.bit;
				maze[nx][ny] |= dir.opposite.bit;
				generateMaze(nx, ny);
			}
		}
	}

	private static boolean between(int v, int upper) {
		return v >= 0 && v < upper;
	}

}
