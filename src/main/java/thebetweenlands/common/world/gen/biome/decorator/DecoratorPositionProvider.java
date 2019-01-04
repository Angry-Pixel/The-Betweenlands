package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class DecoratorPositionProvider {
	private Biome biome;
	private World world;
	private int x, y, z, seaGroundY;
	private Random rand;
	private IChunkGenerator generator;
	private int minOffsetXZ = 8, maxOffsetXZ = 24, minOffsetY = -8, maxOffsetY = 8;

	/**
	 * Returns the chunk generator
	 * @return
	 */
	@Nullable
	public IChunkGenerator getChunkGenerator() {
		return this.generator;
	}

	/**
	 * Returns the biome
	 * @return
	 */
	public Biome getBiome() {
		return this.biome;
	}

	/**
	 * Returns a random X/Z offset
	 * @return
	 */
	public int offsetXZ() {
		return this.rand.nextInt(this.maxOffsetXZ - this.minOffsetXZ) + this.minOffsetXZ;
	}

	/**
	 * Returns a random X/Z offset for generating a feature with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public int offsetXZ(int padding) {
		int bind = 32 - padding * 2;
		int rand = this.rand.nextInt(bind);
		return rand + padding;
	}

	/**
	 * Returns a random Y offset
	 * @return
	 */
	public int offsetY() {
		return this.rand.nextInt(this.maxOffsetY - this.minOffsetY) + this.minOffsetY;
	}

	/**
	 * Returns a random position
	 * @return
	 */
	public BlockPos getRandomPos() {
		return new BlockPos(this.x + this.offsetXZ(), this.y + this.offsetY(), this.z + this.offsetXZ());
	}

	/**
	 * Returns a random position with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public BlockPos getRandomPos(int padding) {
		return new BlockPos(this.x + this.offsetXZ(padding), this.y + this.offsetY(), this.z + this.offsetXZ(padding));
	}

	/**
	 * Returns a random position near the sea ground with a padding of 8 blocks
	 * @return
	 */
	public BlockPos getRandomPosSeaGround() {
		return new BlockPos(this.x + this.offsetXZ(), this.seaGroundY + this.offsetY(), this.z + this.offsetXZ());
	}

	/**
	 * Returns a random position near the sea ground with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public BlockPos getRandomPosSeaGround(int padding) {
		return new BlockPos(this.x + this.offsetXZ(padding), this.seaGroundY + this.offsetY(), this.z + this.offsetXZ(padding));
	}

	/**
	 * Returns a random X position with a padding of 8 blocks
	 * @return
	 */
	public int getRandomPosX() {
		return this.x + this.offsetXZ();
	}

	/**
	 * Returns a random X position with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public int getRandomPosX(int padding) {
		return this.x + this.offsetXZ(padding);
	}

	/**
	 * Returns a random Z position with a padding of 8 blocks
	 * @return
	 */
	public int getRandomPosZ() {
		return this.z + this.offsetXZ();
	}

	/**
	 * Returns a random Z position with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public int getRandomPosZ(int padding) {
		return this.z + this.offsetXZ(padding);
	}

	/**
	 * Returns a random Y position
	 * @return
	 */
	public int getRandomPosY() {
		return this.y + this.offsetY();
	}

	/**
	 * Returns a random Y position near the sea ground
	 * @return
	 */
	public int getRandomPosYSeaGround() {
		return this.seaGroundY + this.offsetY();
	}

	/**
	 * Returns the world
	 * @return
	 */
	public World getWorld() {
		return this.world;
	}

	/**
	 * Returns the X coordinate
	 * @return
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Returns the Y coordinate
	 * @return
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Returns the Y coordinate below liquids
	 * @return
	 */
	public int getSeaGroundY() {
		return this.seaGroundY;
	}

	/**
	 * Returns the Z coordinate
	 * @return
	 */
	public int getZ() {
		return this.z;
	}

	/**
	 * Returns the RNG
	 * @return
	 */
	public Random getRand() {
		return this.rand;
	}

	/**
	 * Sets the minimum and maximum offsets in X/Z direction
	 * @param min
	 * @param max
	 */
	public DecoratorPositionProvider setOffsetXZ(int min, int max) {
		this.minOffsetXZ = min;
		this.maxOffsetXZ = max;
		return this;
	}

	/**
	 * Sets the mimimum and maximum offsets in Y direction
	 * @param min
	 * @param max
	 */
	public DecoratorPositionProvider setOffsetY(int min, int max) {
		this.minOffsetY = min;
		this.maxOffsetY = max;
		return this;
	}

	/**
	 * Updates the positions
	 * @param world World
	 * @param biome Biome
	 * @param generator Chunk Generator
	 * @param rand Rng
	 * @param x X coordinate
	 * @param y Y coordinate, use -1 for surface
	 * @param z Z coordinate
	 */
	public void init(World world, Biome biome, @Nullable IChunkGenerator generator, Random rand, int x, int y, int z) {
		this.generator = generator;
		this.biome = biome;
		this.x = x;
		this.z = z;
		if(y == -1) {
			this.y = world.getChunk(x >> 4, z >> 4).getHeightValue(x & 15, z & 15);
		} else {
			this.y = y;
		}
		this.seaGroundY = this.y;
		if(this.y <= WorldProviderBetweenlands.LAYER_HEIGHT && world.getBlockState(new BlockPos(this.x, this.y, this.z)).getMaterial().isLiquid()) {
			MutableBlockPos offsetPos = new MutableBlockPos();
			for(int oy = this.y; oy > 0; oy--) {
				offsetPos.setPos(this.x, oy, this.z);
				if(!world.getBlockState(offsetPos).getMaterial().isLiquid()) {
					this.seaGroundY = oy;
					break;
				}
			}
		}
		this.rand = rand;
		this.world = world;
	}

	/**
	 * Updates the positions
	 * @param world
	 * @param biome
	 * @param generator
	 * @param rand
	 * @param x
	 * @param z
	 */
	public void init(World world, Biome biome, @Nullable IChunkGenerator generator, Random rand, int x, int z) {
		this.init(world, biome, generator, rand, x, -1, z);
	}
}
