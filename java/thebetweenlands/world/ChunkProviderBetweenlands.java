package thebetweenlands.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class ChunkProviderBetweenlands implements IChunkProvider {
	
	//TODO: Everything
	
	private Random rand;
	
    private NoiseGeneratorOctaves field_147431_j;
    private NoiseGeneratorOctaves field_147432_k;
    private NoiseGeneratorOctaves field_147429_l;
    private NoiseGeneratorPerlin field_147430_m;
    public NoiseGeneratorOctaves noiseGen5;
    public NoiseGeneratorOctaves noiseGen6;
    private World worldObj;
    private double[] field_147434_q;
    private float[] parabolicField;
    private double[] stoneNoise = new double[256];
    private BiomeGenBase[] biomesForGeneration;
    double[] field_147427_d;
    double[] field_147428_e;
    double[] field_147425_f;
    double[] field_147426_g;
	
    
    
	public ChunkProviderBetweenlands(World world, long seed) {
		this.worldObj = world;
		this.initializeNoiseGen(seed);
	}
	
	
	@Override
	public void recreateStructures(int x, int z) { }

	@Override
	@SuppressWarnings("rawtypes")
	public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
		//TODO: Impl
		//BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);
		//return biome == null ? null : biome.getSpawnableList(creatureType);
		return null;
	}

	@Override
	public String makeString() {
		return "";
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	@Override
	public boolean saveChunks(boolean mode, IProgressUpdate progressUpdate) {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public void saveExtraData() { }

	@Override
	public ChunkPosition func_147416_a(World world, String structureIdentifier, int x, int y, int z) {
		return null;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		return null;
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		return this.provideChunk(x, z);
	}

	@Override
	public void populate(IChunkProvider cp, int x, int z) {
		
	}
	
	private void initializeNoiseGen(long seed) {
        this.rand = new Random(seed);
        this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147434_q = new double[825];
        this.parabolicField = new float[25];

        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.sqrt_float((float)(j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }

        //Allows mods to change the noise gen, not needed for the BL right now
        NoiseGenerator[] noiseGens = {field_147431_j, field_147432_k, field_147429_l, field_147430_m, noiseGen5, noiseGen6};
        /*noiseGens = TerrainGen.getModdedNoiseGenerators(this.worldObj, this.rand, noiseGens);
        this.field_147431_j = (NoiseGeneratorOctaves)noiseGens[0];
        this.field_147432_k = (NoiseGeneratorOctaves)noiseGens[1];
        this.field_147429_l = (NoiseGeneratorOctaves)noiseGens[2];
        this.field_147430_m = (NoiseGeneratorPerlin)noiseGens[3];
        this.noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
        this.noiseGen6 = (NoiseGeneratorOctaves)noiseGens[5];*/
	}
	
	private void generateTerrain(int x, int z, Block[] chunkBlocks) {
		
	}
}
