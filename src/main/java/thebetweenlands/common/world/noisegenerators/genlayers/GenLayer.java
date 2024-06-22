package thebetweenlands.common.world.noisegenerators.genlayers;

// GenLayer template class
public abstract class GenLayer {
	// Codec for registration
	// See here for how to register a new genlayer
	//public static final MapCodec<ResourceLocation> CODEC = RecordCodecBuilder.mapCodec(obj -> {
	//    return obj.group(ResourceLocation.CODEC.fieldOf("name").forGetter());
	//});


	private long worldGenSeed;
	protected GenLayer parent;
	private long chunkSeed;
	protected long baseSeed;

	public static GenLayer[] initializeAllBiomeGenerators(long seed) {
		// allways null, override in betweenlands genlayer
		return new GenLayer[]{null};
	}

	public GenLayer(long p_i2125_1_) {
		this.baseSeed = p_i2125_1_;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += p_i2125_1_;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += p_i2125_1_;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += p_i2125_1_;
	}

	public void initWorldGenSeed(long seed) {
		this.worldGenSeed = seed;

		if (this.parent != null) {
			this.parent.initWorldGenSeed(seed);
		}

		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
	}

	public void initChunkSeed(long p_75903_1_, long p_75903_3_) {
		this.chunkSeed = this.worldGenSeed;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += p_75903_1_;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += p_75903_3_;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += p_75903_1_;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += p_75903_3_;
	}

	protected int nextInt(int p_75902_1_) {
		int i = (int) ((this.chunkSeed >> 24) % (long) p_75902_1_);

		if (i < 0) {
			i += p_75902_1_;
		}

		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += this.worldGenSeed;
		return i;
	}

	public abstract int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight);


	/* ======================================== FORGE START =====================================*/
	protected long nextLong(long par1) {
		long j = (this.chunkSeed >> 24) % par1;

		if (j < 0) {
			j += par1;
		}

		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += this.worldGenSeed;
		return j;
	}

	protected int selectRandom(int... p_151619_1_) {
		return p_151619_1_[this.nextInt(p_151619_1_.length)];
	}

	protected int selectModeOrRandom(int p_151617_1_, int p_151617_2_, int p_151617_3_, int p_151617_4_) {
		if (p_151617_2_ == p_151617_3_ && p_151617_3_ == p_151617_4_) {
			return p_151617_2_;
		} else if (p_151617_1_ == p_151617_2_ && p_151617_1_ == p_151617_3_) {
			return p_151617_1_;
		} else if (p_151617_1_ == p_151617_2_ && p_151617_1_ == p_151617_4_) {
			return p_151617_1_;
		} else if (p_151617_1_ == p_151617_3_ && p_151617_1_ == p_151617_4_) {
			return p_151617_1_;
		} else if (p_151617_1_ == p_151617_2_ && p_151617_3_ != p_151617_4_) {
			return p_151617_1_;
		} else if (p_151617_1_ == p_151617_3_ && p_151617_2_ != p_151617_4_) {
			return p_151617_1_;
		} else if (p_151617_1_ == p_151617_4_ && p_151617_2_ != p_151617_3_) {
			return p_151617_1_;
		} else if (p_151617_2_ == p_151617_3_ && p_151617_1_ != p_151617_4_) {
			return p_151617_2_;
		} else if (p_151617_2_ == p_151617_4_ && p_151617_1_ != p_151617_3_) {
			return p_151617_2_;
		} else {
			return p_151617_3_ == p_151617_4_ && p_151617_1_ != p_151617_2_ ? p_151617_3_ : this.selectRandom(p_151617_1_, p_151617_2_, p_151617_3_, p_151617_4_);
		}
	}
}