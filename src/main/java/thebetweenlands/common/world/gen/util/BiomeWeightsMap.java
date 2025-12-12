package thebetweenlands.common.world.gen.util;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import thebetweenlands.api.world.BiomeWeights;

public class BiomeWeightsMap {

	public final int maxChunkDistance;
	public final int width;
	public final ChunkPos centreChunkPos;
	public final BiomeWeights[] weightMap;
	
	/*
	 * For example: if maxChunkDistance = 1, then this creates a 3x3 grid centered on that chunk
	 */
	public BiomeWeightsMap(ChunkPos centreChunkPos, int maxChunkDistance) {
		this.centreChunkPos = centreChunkPos;
		this.maxChunkDistance = maxChunkDistance;
		this.width = 1 + 2 * maxChunkDistance;
		this.weightMap = new BiomeWeights[width * width];
	}

	/**
	 * Sets the weights at the specified offset from the centre to the specified weight if possible
	 * @param xOffset
	 * @param zOffset
	 * @param weights
	 * @return
	 */
	public boolean setWeights(int xOffset, int zOffset, BiomeWeights weights) {
		if(Mth.abs(xOffset) <= this.maxChunkDistance && Mth.abs(zOffset) <= this.maxChunkDistance) {
			weightMap[(xOffset + this.maxChunkDistance) * width + (zOffset + this.maxChunkDistance)] = weights;
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the weights at the specified chunk pos to the specified weight if possible
	 * @param chunkPos
	 * @param weights
	 * @return
	 */
	public boolean setWeightsFor(int chunkX, int chunkZ, BiomeWeights weights) {
		int xOffset = this.centreChunkPos.x - chunkX;
		int zOffset = this.centreChunkPos.z - chunkZ;
		return this.setWeights(xOffset, zOffset, weights);
	}
	
	/**
	 * Sets the weights at the specified chunk pos to the specified weight if possible
	 * @param chunkPos
	 * @param weights
	 * @return
	 */
	public boolean setWeightsFor(ChunkPos chunkPos, BiomeWeights weights) {
		int xOffset = this.centreChunkPos.x - chunkPos.x;
		int zOffset = this.centreChunkPos.z - chunkPos.z;
		return this.setWeights(xOffset, zOffset, weights);
	}
	
	/**
	 * Gets the weights from the specified offset if possible
	 * @param xOffset
	 * @param zOffset
	 * @param weights
	 * @return
	 */
	public BiomeWeights getWeights(int xOffset, int zOffset) {
		if(Mth.abs(xOffset) <= this.maxChunkDistance && Mth.abs(zOffset) <= this.maxChunkDistance) {
			return this.weightMap[(xOffset + this.maxChunkDistance) * this.width + (zOffset + this.maxChunkDistance)];
		}
		return null;
	}

	/**
	 * Gets the weights at the specified chunk pos if possible
	 * @param chunkPos
	 * @param weights
	 * @return
	 */
	public BiomeWeights getWeightsFor(int chunkX, int chunkZ) {
		int xOffset = this.centreChunkPos.x - chunkX;
		int zOffset = this.centreChunkPos.z - chunkZ;
		return this.getWeights(xOffset, zOffset);
	}
	
	/**
	 * Gets the weights at the specified chunk pos if possible
	 * @param chunkPos
	 * @param weights
	 * @return
	 */
	public BiomeWeights getWeightsFor(ChunkPos chunkPos) {
		int xOffset = this.centreChunkPos.x - chunkPos.x;
		int zOffset = this.centreChunkPos.z - chunkPos.z;
		return this.getWeights(xOffset, zOffset);
	}
	
}
