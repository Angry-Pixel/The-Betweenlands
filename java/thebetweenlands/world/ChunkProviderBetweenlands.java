package thebetweenlands.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class ChunkProviderBetweenlands implements IChunkProvider {
	
	//TODO: Everything
	
	//The world
	private World worldObj;
	
	//RNG with the world seed as seed
	private Random rand;
	
	//The base noise for base generation
	private NoiseGeneratorOctaves baseNoiseOctave;
	
	//The octaves for the base generation
	//Octaves are used for additional features for the base noise
    private NoiseGeneratorOctaves noiseOctave1;
    private NoiseGeneratorOctaves noiseOctave2;
    private NoiseGeneratorOctaves noiseOctave3;
    //5th octave not used
    //private NoiseGeneratorOctaves octave5;
    
    //Generates a stone noise. At a certain threshold of this noise, the baseBlock will be the top most block and the fillerBlock and topBlock of the Biome are ignored.
    private NoiseGeneratorPerlin stoneNoiseGen;
    //Stone noise at any Y value in the current XZ stack
    private double[] stoneNoise = new double[256];
    
    //Noise at any Y value in the current XZ stack
    private double[] noiseXZ;
    
    //Holds the octave noised at the current XZ stack
    double[] noise3;
    double[] noise1;
    double[] noise2;
    double[] baseNoise;
    
    //Holds the biome height gradient at the current XZ stack
    private float[] parabolicField;
    
    //Holds the biomes in the current XZ stack
    private BiomeGenBase[] biomesForGeneration;
    
    //Base block. Vanilla: stone
    private final Block baseBlock;
    //Layer block generated below layerHeight. Vanilla: water
    private final Block layerBlock;
    //layerBlock generates below this height
    private final int layerHeight;
    
	public ChunkProviderBetweenlands(World world, long seed, Block baseBlock, Block layerBlock, int layerHeight) {
		this.worldObj = world;
		this.initializeNoiseGen(seed);
		this.baseBlock = baseBlock;
		this.layerBlock = layerBlock;
		this.layerHeight = layerHeight;
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
		//Generate seeded RNG
        this.rand = new Random(seed);
        
        //Initialize noise octaves
        this.baseNoiseOctave = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseOctave1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseOctave2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseOctave3 = new NoiseGeneratorOctaves(this.rand, 8);
        
        //5th octave not used
        //this.octave5 = new NoiseGeneratorOctaves(this.rand, 10);
        
        this.stoneNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);
        
        //Holds the generated noise at any (x|z) coordinate
        this.noiseXZ = new double[825];
        
        //Holds the biome height gradient
        this.parabolicField = new float[25];

        //Generate parabolic field
        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.sqrt_float((float)(j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }

        //Allows mods to change the noise gen, not needed for the BL right now
        /*NoiseGenerator[] noiseGens = {field_147431_j, field_147432_k, field_147429_l, field_147430_m, noiseGen5, noiseGen6};
        noiseGens = TerrainGen.getModdedNoiseGenerators(this.worldObj, this.rand, noiseGens);
        this.field_147431_j = (NoiseGeneratorOctaves)noiseGens[0];
        this.field_147432_k = (NoiseGeneratorOctaves)noiseGens[1];
        this.field_147429_l = (NoiseGeneratorOctaves)noiseGens[2];
        this.field_147430_m = (NoiseGeneratorPerlin)noiseGens[3];
        this.noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
        this.noiseGen6 = (NoiseGeneratorOctaves)noiseGens[5];*/
	}
	
	private void generateNoiseXZ(double[] noiseArray, int x, int z) {
		//Generate noise XZ components
        this.baseNoise = this.baseNoiseOctave.generateNoiseOctaves(this.baseNoise, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
        this.noise3 = this.noiseOctave3.generateNoiseOctaves(this.noise3, x, 0, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.noise1 = this.noiseOctave1.generateNoiseOctaves(this.noise1, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.noise2 = this.noiseOctave2.generateNoiseOctaves(this.noise2, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        
        
        int l = 0;
        int i1 = 0;

        for (int j1 = 0; j1 < 5; ++j1)
        {
            for (int k1 = 0; k1 < 5; ++k1)
            {
                float f = 0.0F;
                float f1 = 0.0F;
                float f2 = 0.0F;
                byte b0 = 2;
                BiomeGenBase biomegenbase = this.biomesForGeneration[j1 + 2 + (k1 + 2) * 10];

                for (int l1 = -b0; l1 <= b0; ++l1)
                {
                    for (int i2 = -b0; i2 <= b0; ++i2)
                    {
                        BiomeGenBase biomegenbase1 = this.biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
                        float f3 = biomegenbase1.rootHeight;
                        float f4 = biomegenbase1.heightVariation;

                        //No amplified terrain in the BL
                        /*if (this.field_147435_p == WorldType.AMPLIFIED && f3 > 0.0F)
                        {
                            f3 = 1.0F + f3 * 2.0F;
                            f4 = 1.0F + f4 * 4.0F;
                        }*/

                        float f5 = this.parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);

                        if (biomegenbase1.rootHeight > biomegenbase.rootHeight)
                        {
                            f5 /= 2.0F;
                        }

                        f += f4 * f5;
                        f1 += f3 * f5;
                        f2 += f5;
                    }
                }

                f /= f2;
                f1 /= f2;
                f = f * 0.9F + 0.1F;
                f1 = (f1 * 4.0F - 1.0F) / 8.0F;
                double d12 = this.baseNoise[i1] / 8000.0D;

                if (d12 < 0.0D)
                {
                    d12 = -d12 * 0.3D;
                }

                d12 = d12 * 3.0D - 2.0D;

                if (d12 < 0.0D)
                {
                    d12 /= 2.0D;

                    if (d12 < -1.0D)
                    {
                        d12 = -1.0D;
                    }

                    d12 /= 1.4D;
                    d12 /= 2.0D;
                }
                else
                {
                    if (d12 > 1.0D)
                    {
                        d12 = 1.0D;
                    }

                    d12 /= 8.0D;
                }

                ++i1;
                double d13 = (double)f1;
                double d14 = (double)f;
                d13 += d12 * 0.2D;
                d13 = d13 * 8.5D / 8.0D;
                double d5 = 8.5D + d13 * 4.0D;

                for (int j2 = 0; j2 < 33; ++j2)
                {
                    double d6 = ((double)j2 - d5) * 12.0D * 128.0D / 256.0D / d14;

                    if (d6 < 0.0D)
                    {
                        d6 *= 4.0D;
                    }

                    double d7 = this.noise1[l] / 512.0D;
                    double d8 = this.noise2[l] / 512.0D;
                    double d9 = (this.noise3[l] / 10.0D + 1.0D) / 2.0D;
                    double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

                    if (j2 > 29)
                    {
                        double d11 = (double)((float)(j2 - 29) / 3.0F);
                        d10 = d10 * (1.0D - d11) + -10.0D * d11;
                    }

                    //this.field_147434_q[l] = d10;
                    noiseArray[l] = d10;
                    ++l;
                }
            }
        }
	}
	
	private void generateTerrain(int x, int z, Block[] chunkBlocks) {
		//Water layer height
		byte b0 = 63;
		
		//BiomeGenBase[] at the X and Z coordinates
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
       
        //Generates the noise array at the X and Z coordinates
        this.generateNoiseXZ(this.noiseXZ, x * 4, z * 4);

        for (int k = 0; k < 4; ++k)
        {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for (int j1 = 0; j1 < 4; ++j1)
            {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for (int k2 = 0; k2 < 32; ++k2)
                {
                    double d0 = 0.125D;
                    double d1 = this.noiseXZ[k1 + k2];
                    double d2 = this.noiseXZ[l1 + k2];
                    double d3 = this.noiseXZ[i2 + k2];
                    double d4 = this.noiseXZ[j2 + k2];
                    double d5 = (this.noiseXZ[k1 + k2 + 1] - d1) * d0;
                    double d6 = (this.noiseXZ[l1 + k2 + 1] - d2) * d0;
                    double d7 = (this.noiseXZ[i2 + k2 + 1] - d3) * d0;
                    double d8 = (this.noiseXZ[j2 + k2 + 1] - d4) * d0;

                    for (int l2 = 0; l2 < 8; ++l2)
                    {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i3 = 0; i3 < 4; ++i3)
                        {
                            int j3 = i3 + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + l2;
                            short short1 = 256;
                            j3 -= short1;
                            double d14 = 0.25D;
                            double d16 = (d11 - d10) * d14;
                            double d15 = d10 - d16;

                            for (int k3 = 0; k3 < 4; ++k3)
                            {
                                if ((d15 += d16) > 0.0D)
                                {
                                	chunkBlocks[j3 += short1] = Blocks.stone;
                                }
                                else if (k2 * 8 + l2 < b0)
                                {
                                	chunkBlocks[j3 += short1] = Blocks.water;
                                }
                                else
                                {
                                	chunkBlocks[j3 += short1] = null;
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
	}
}
