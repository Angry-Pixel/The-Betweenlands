package thebetweenlands.common.world.gen.carver;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import thebetweenlands.common.world.gen.carver.config.BetweenlandsCaveCarverConfiguration;

public class BetweenlandsCaveCarver extends WorldCarver<BetweenlandsCaveCarverConfiguration> {

	public BetweenlandsCaveCarver(Codec<BetweenlandsCaveCarverConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean carve(CarvingContext context, BetweenlandsCaveCarverConfiguration config, ChunkAccess chunk,
			Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos,
			CarvingMask carvingMask) {
		// How possible is it to make this as a carver?
		return false;
	}

	@Override
	public boolean isStartChunk(BetweenlandsCaveCarverConfiguration config, RandomSource random) {
		return true;
	}

}
