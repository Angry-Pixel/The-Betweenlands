package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.feature.OreGens;
import thebetweenlands.common.world.gen.feature.tree.WorldGenGiantTree;
import thebetweenlands.common.world.gen.progressivegen.ProgressiveGenChunkMarker;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;

public class BiomeDecoratorBetweenlands {
	private World world;
	private int x, y, z, seaGroundY;
	private Random rand;
	private ChunkGeneratorBetweenlands generator;

	/**
	 * Returns a random X/Z offset for generating a feature with a padding of 8 blocks
	 * @return
	 */
	public final int offsetXZ() {
		return this.rand.nextInt(16) + 8;
	}

	/**
	 * Returns a random X/Z offset for generating a feature with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public final int offsetXZ(int padding) {
		return this.rand.nextInt(32 - padding * 2) + padding;
	}

	/**
	 * Returns a random Y offset in the range of -8 to 8
	 * @return
	 */
	public final int offsetY() {
		return this.rand.nextInt(16) - 8;
	}

	/**
	 * Returns a random position near the surface with a padding of 8 blocks
	 * @return
	 */
	public final BlockPos getRandomPos() {
		return new BlockPos(this.x + this.offsetXZ(), this.y + this.offsetY(), this.z + this.offsetXZ());
	}

	/**
	 * Returns a random position near the surface with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public final BlockPos getRandomPos(int padding) {
		return new BlockPos(this.x + this.offsetXZ(padding), this.y + this.offsetY(), this.z + this.offsetXZ(padding));
	}

	/**
	 * Returns a random position near the sea ground (or surface if there's no water) with a padding of 8 blocks
	 * @return
	 */
	public final BlockPos getRandomPosSeaGround() {
		return new BlockPos(this.x + this.offsetXZ(), this.seaGroundY + this.offsetY(), this.z + this.offsetXZ());
	}

	/**
	 * Returns a random position near the sea ground (or surface if there's no water) with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public final BlockPos getRandomPosSeaGround(int padding) {
		return new BlockPos(this.x + this.offsetXZ(padding), this.seaGroundY + this.offsetY(), this.z + this.offsetXZ(padding));
	}

	/**
	 * Returns a random X position with a padding of 8 blocks
	 * @return
	 */
	public final int getRandomPosX() {
		return this.x + this.offsetXZ();
	}

	/**
	 * Returns a random X position with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public final int getRandomPosX(int padding) {
		return this.x + this.offsetXZ(padding);
	}

	/**
	 * Returns a random Z position with a padding of 8 blocks
	 * @return
	 */
	public final int getRandomPosZ() {
		return this.z + this.offsetXZ();
	}

	/**
	 * Returns a random Z position with a custom padding
	 * @param padding Block padding from chunk borders, from 0 to 15
	 * @return
	 */
	public final int getRandomPosZ(int padding) {
		return this.z + this.offsetXZ(padding);
	}

	/**
	 * Returns a random Y position near the surface
	 * @return
	 */
	public final int getRandomPosY() {
		return this.y + this.offsetY();
	}

	/**
	 * Returns a random Y position near the sea ground (or surface if there's no water)
	 * @return
	 */
	public final int getRandomPosYSeaGround() {
		return this.seaGroundY + this.offsetY();
	}

	public final World getWorld() {
		return this.world;
	}

	public final ChunkGeneratorBetweenlands getChunkGenerator() {
		return this.generator;
	}

	public final int getX() {
		return this.x;
	}

	public final int getY() {
		return this.y;
	}

	public final int getSeaGroundY() {
		return this.seaGroundY;
	}

	public final int getZ() {
		return this.z;
	}

	public final Random getRand() {
		return this.rand;
	}

	/**
	 * Decorates the specified chunk
	 * @param world
	 * @param rand
	 * @param x
	 * @param z
	 */
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.y = world.getHeight(new BlockPos(x, 0, z)).getY();
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
		this.generator = generator;

		this.generateOres();
		this.generate(DecorationHelper::populateCaves);
		this.generate(2, DecorationHelper::generateStagnantWaterPool);
		this.generate(120, DecorationHelper::generateUndergroundRuins);
	}

	/**
	 * Generates the default ores
	 */
	protected void generateOres() {
		//TODO: Tweak these values for new terrain height
		this.generateOre(22, 10, OreGens.SULFUR, 0, 128);
		this.generateOre(10, 8, OreGens.SYRMORITE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, 8, OreGens.BONE_ORE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, 8, OreGens.OCTINE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);	
		this.generateOre(4, 12, OreGens.SWAMP_DIRT, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(0.2F, 14, OreGens.LIMESTONE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(3, 8, OreGens.VALONITE, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(4, 8, OreGens.SCABYST, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(80, 8, OreGens.LIFE_GEM, 0, WorldProviderBetweenlands.CAVE_WATER_HEIGHT);

		//Generate middle gems
		int cycles = 1 + (this.rand.nextBoolean() ? this.rand.nextInt(2) : 0);
		for(int i = 0; i < cycles; i++) {
			if(this.rand.nextInt(9 / cycles + 1) == 0) {
				int xx = this.x + this.offsetXZ();
				int zz = this.z + this.offsetXZ();
				int yy = this.world.getHeight(new BlockPos(xx, 0, zz)).getY() - 1;
				boolean hasMud = false;
				for(int yo = 0; yo < 16; yo++) {
					int bx = yy + yo;
					if(this.world.getBlockState(new BlockPos(xx, yy + yo, zz)).getBlock() == BlockRegistry.SWAMP_WATER
							&& this.world.getBlockState(new BlockPos(xx, yy + yo - 1, zz)).getBlock() == BlockRegistry.MUD) {
						hasMud = true;
						yy = bx - 1;
					}
				}
				if(hasMud) {
					switch(this.rand.nextInt(3)) {
					case 0:
						this.world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.AQUA_MIDDLE_GEM_ORE.getDefaultState());
						break;
					case 1:
						this.world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.getDefaultState());
						break;
					case 2:
						this.world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.GREEN_MIDDLE_GEM_ORE.getDefaultState());
						break;
					}
				}
			}
		}
	}

	/**
	 * Attempts to generate an ore vein
	 * @param tries
	 * @param padding
	 * @param oreGen
	 * @param minY
	 * @param maxY
	 */
	protected void generateOre(float tries, int padding, WorldGenerator oreGen, int minY, int maxY) {
		tries = tries >= 1.0F ? tries : (this.rand.nextFloat() <= tries ? 1 : 0);
		for (int i = 0; i < tries; i++) {
			int xx = this.x + this.offsetXZ(padding);
			int yy = minY + this.rand.nextInt(maxY - minY);
			int zz = this.z + this.offsetXZ(padding);
			oreGen.generate(this.world, this.rand, new BlockPos(xx, yy, zz));
		}
	}

	/**
	 * Tries to generate the specified feature
	 * @param tries How many attempts should be run. If this value is < 1 it is used as a probability
	 * @param generator
	 * @return
	 */
	public boolean generate(float tries, Function<BiomeDecoratorBetweenlands, Boolean> generator) {
		boolean generated = false;
		if(tries < 1.0F) {
			if(this.rand.nextFloat() <= tries)
				generated = generator.apply(this);
		} else {
			float remainder = tries % 1.0F;
			if(this.rand.nextFloat() <= remainder)
				tries++;
			for(int i = 0; i < tries; i++) {
				if(generator.apply(this))
					generated = true;
			}
		}
		return generated;
	}

	/**
	 * Tries to generate the specified feature
	 * @param generator
	 * @return
	 */
	public boolean generate(Function<BiomeDecoratorBetweenlands, Boolean> generator) {
		return this.generate(1, generator);
	}
}
