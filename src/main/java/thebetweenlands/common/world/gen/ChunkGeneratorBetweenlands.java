package thebetweenlands.common.world.gen;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;

public class ChunkGeneratorBetweenlands implements IChunkGenerator {
	@Override
	public Chunk provideChunk(int x, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populate(int x, int z) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		// TODO Auto-generated method stub

	}
}
