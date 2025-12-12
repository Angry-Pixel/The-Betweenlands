package thebetweenlands.common.world.gen.generators.util;

import java.util.Objects;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;

/**
 * Used to easily represent which blocks need to be set to terrain in a section
 * @param minBlockY an array of 256 integers, representing the lowest (exclusive) y value to be set to terrain
 * @param maxBlockY an array of 256 integers, representing the highest (exclusive) y value to be set to terrain
 * @param globalMinBlockY the smallest value in minBlockY that is in a valid column
 * @param globalMaxBlockY the largest value in maxBlockY that is in a valid column
 */
public record ColumnVolumeResult(int[] minBlockY, int[] maxBlockY, int globalMinBlockY, int globalMaxBlockY) {
	
	public ColumnVolumeResult(int[] minBlockY, int[] maxBlockY, int globalMinBlockY, int globalMaxBlockY) {
		this.minBlockY = Objects.requireNonNull(minBlockY);
		this.maxBlockY = Objects.requireNonNull(maxBlockY);
		this.globalMinBlockY = globalMinBlockY;
		this.globalMaxBlockY = globalMaxBlockY;
		
		if(minBlockY.length != 256 || maxBlockY.length != 256) {
			throw new IllegalArgumentException("Both minBlockY and maxBlockY must be arrays of 256 integers");
		}
	}
	
	/**
	 * Simple implementation that fills all blocks in a chunk that are defined by a ColumnVolumeResult with the same blockstate, and updates the chunk heightmaps
	 * @param volumeResult The volume result
	 * @param chunkAccess The chunk to set blocks in
	 * @param terrainBlock The block we will be setting
	 * @param heightmaps The heightmaps (for updating)
	 * @return if blocks were placed
	 */
	public static boolean placeColumnVolumeResult(ColumnVolumeResult volumeResult, ChunkAccess chunkAccess, BlockState terrainBlock, ChunkHeightmaps heightmaps) {
		final int[] minBlockY = volumeResult.minBlockY();
		final int[] maxBlockY = volumeResult.maxBlockY();
		final int totalMinBlockY = volumeResult.globalMinBlockY();
		final int totalMaxBlockY = volumeResult.globalMaxBlockY();
		
		// If the min block is above the max block, then no blocks can be placed
		if(totalMinBlockY >= totalMaxBlockY || totalMinBlockY + 1 > totalMaxBlockY - 1) {
			return false;
		}

		// Minimum section index (inclusive) that needs blocks placed
		int minSectionIndex = chunkAccess.getSectionIndex(totalMinBlockY + 1);

		// Maximum section index (inclusive) that needs blocks placed
		int maxSectionIndex = chunkAccess.getSectionIndex(totalMaxBlockY - 1);
		
		boolean blocksPlaced = false;
		
		for(int sectionIndex = minSectionIndex; sectionIndex <= maxSectionIndex; ++sectionIndex) {
			// The section we'll be setting blocks in
			LevelChunkSection section = chunkAccess.getSection(sectionIndex);
			
			// The y value of block 0 in this section
			int sectionMinY = SectionPos.sectionToBlockCoord(chunkAccess.getSectionYFromSectionIndex(sectionIndex));
			
			for(int x = 0; x < 16; ++x) {
				for(int z = 0; z < 16; ++z) {
					final int index = x * 16 + z;
					
					final int yMin = Math.max(
							minBlockY[index] - sectionMinY + 1,
							0
						);
					
					// If lowest block for this x/z is above this section, don't place anything
					if(yMin >= 16) {
						continue;
					}
					
					final int yMax = Math.min(
							maxBlockY[index] - sectionMinY - 1,
							15
						);
					
					// if highest block for this x/z is below this section, don't place anything
					if(yMax < 0) {
						continue;
					}
					
					blocksPlaced = blocksPlaced || yMax >= yMin;
					
					for(int y = yMin; y <= yMax; ++y) {
						// Important: disable locks since the section was already acquired by the chunk generator
						section.setBlockState(x, y, z, terrainBlock, false);
						heightmaps.update(x, sectionMinY + y, z, terrainBlock);
					}
				}
			}
		}
		
		return blocksPlaced;
	}

	@FunctionalInterface
	public static interface VolumeBlockstateProvider {
		/**
		 * Returns the {@linkplain BlockState} to place at the relative chunk coordinates {@code x}, {@code y}, {@code z}
		 * @param x The X position of the block relative to the chunk
		 * @param y The Y position of the block relative to the chunk
		 * @param z The Z position of the block relative to the chunk
		 * @return the {@linkplain BlockState} to place
		 */
		public BlockState getBlockState(int x, int y, int z);
	}
	
	/**
	 * Less-simple implementation that fills all blocks in a chunk that are defined by a ColumnVolumeResult with the a provided blockstate, and updates the chunk heightmaps
	 * @param volumeResult The volume result
	 * @param chunkAccess The chunk to set blocks in
	 * @param terrainBlockProvider Converts x, y, z coordinates into a target blockstate
	 * @param heightmaps The heightmaps (for updating)
	 * @return if blocks were placed
	 */
	public static boolean placeColumnVolumeResult(ColumnVolumeResult volumeResult, ChunkAccess chunkAccess, VolumeBlockstateProvider terrainBlockProvider, ChunkHeightmaps heightmaps) {
		final int[] minBlockY = volumeResult.minBlockY();
		final int[] maxBlockY = volumeResult.maxBlockY();
		final int totalMinBlockY = volumeResult.globalMinBlockY();
		final int totalMaxBlockY = volumeResult.globalMaxBlockY();
		
		// If the min block is above the max block, then no blocks can be placed
		if(totalMinBlockY >= totalMaxBlockY || totalMinBlockY + 1 > totalMaxBlockY - 1) {
			return false;
		}

		// Minimum section index (inclusive) that needs blocks placed
		int minSectionIndex = chunkAccess.getSectionIndex(totalMinBlockY + 1);

		// Maximum section index (inclusive) that needs blocks placed
		int maxSectionIndex = chunkAccess.getSectionIndex(totalMaxBlockY - 1);
		
		boolean blocksPlaced = false;
		
		for(int sectionIndex = minSectionIndex; sectionIndex <= maxSectionIndex; ++sectionIndex) {
			// The section we'll be setting blocks in
			LevelChunkSection section = chunkAccess.getSection(sectionIndex);
			
			// The y value of block 0 in this section
			int sectionMinY = SectionPos.sectionToBlockCoord(chunkAccess.getSectionYFromSectionIndex(sectionIndex));
			
			for(int x = 0; x < 16; ++x) {
				for(int z = 0; z < 16; ++z) {
					final int index = x * 16 + z;
					
					final int yMin = Math.max(
							minBlockY[index] - sectionMinY + 1,
							0
						);
					
					// If lowest block for this x/z is above this section, don't place anything
					if(yMin >= 16) {
						continue;
					}
					
					final int yMax = Math.min(
							maxBlockY[index] - sectionMinY - 1,
							15
						);
					
					// if highest block for this x/z is below this section, don't place anything
					if(yMax < 0) {
						continue;
					}
					
					blocksPlaced = blocksPlaced || yMax >= yMin;
					
					for(int y = yMin; y <= yMax; ++y) {
						// Important: disable locks since the section was already acquired by the chunk generator
						BlockState terrainBlock = terrainBlockProvider.getBlockState(x, sectionMinY + y, z);
						section.setBlockState(x, y, z, terrainBlock, false);
						heightmaps.update(x, sectionMinY + y, z, terrainBlock);
					}
				}
			}
		}
		
		return blocksPlaced;
	}
}
