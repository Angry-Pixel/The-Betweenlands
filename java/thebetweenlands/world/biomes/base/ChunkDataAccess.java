package thebetweenlands.world.biomes.base;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class ChunkDataAccess {
	private final int chunkX, chunkZ;
	private final Block[] blockData;
	private final byte[] metaData;
	private final BiomeGenBase[] biomes;
	private final int sliceSize;

	public ChunkDataAccess(int chunkX, int chunkZ, Block[] blockData, byte[] metaData, BiomeGenBase[] biomes) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.blockData = blockData;
		this.metaData = metaData;
		this.biomes = biomes;
		this.sliceSize = this.blockData.length / 256;
	}

	public boolean isAir(int x, int y, int z) {
		Block block = this.blockData[getBlockArrayIndex(x, y, z, this.sliceSize)];
		return block == Blocks.air || block == null;
	}
	
	public Block getBlock(int x, int y, int z) {
		return this.blockData[getBlockArrayIndex(x, y, z, this.sliceSize)];
	}

	public int getMetadata(int x, int y, int z) {
		return this.metaData[getBlockArrayIndex(x, y, z, this.sliceSize)];
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		this.blockData[getBlockArrayIndex(x, y, z, this.sliceSize)] = block;
	}
	
	public void setBlock(int x, int y, int z, Block block, int metadata) {
		int index = getBlockArrayIndex(x, y, z, this.sliceSize);
		this.blockData[index] = block;
		this.metaData[index] = (byte) metadata;
	}
	
	public void setMetadata(int x, int y, int z, int metadata) {
		this.metaData[getBlockArrayIndex(x, y, z, this.sliceSize)] = (byte) metadata;
	}
	
	public BiomeGenBase getBiome(int x, int z) {
		return this.biomes[x + z * 16];
	}
	
	public int getChunkX() {
		return this.chunkX;
	}
	
	public int getChunkZ() {
		return this.chunkZ;
	}
	
	/**
	 * Returns the index in the block array.
	 * @param x
	 * @param y
	 * @param z
	 * @param sliceSize
	 * @return int
	 */
	public static int getBlockArrayIndex(int x, int y, int z, int sliceSize) {
		return (z * 16 + x) * sliceSize + y;
	}
}
