package thebetweenlands.common.world.gen.layer;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.SingleParentBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;

public class SurroundedBiomeLayer implements SingleParentBiomeLayer {

	public static final MapCodec<SurroundedBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryOps.retrieveGetter(Registries.BIOME),
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parent),
					Codec.INT.fieldOf("surround_range").forGetter(o -> o.checkRange),
					Codec.xor(Codec.INT, Codec.FLOAT).fieldOf("spawn_chance").<Integer>xmap(t -> t.map(Function.identity(), (f) -> (int)(f * 10000)), t -> Either.left(t)).forGetter(o -> o.spawnChance),
					Codec.BOOL.fieldOf("mask").forGetter(o -> o.mask),
					Biome.CODEC.fieldOf("surrounding_biome").forGetter(o -> o.surroundingBiome),
					Biome.CODEC.fieldOf("surrounded_biome").forGetter(o -> o.surroundedBiome)
				).apply(instance, SurroundedBiomeLayer::new)
		);

	private final HolderGetter<Biome> registry;
	private final BiomeLayerConfigured parent;
	// How many cells to check are surrounded
	private final int checkRange;
	// The spawn chance of the biome
	private final int spawnChance;
	// Whether to return the original value (mask = false) or -1 (mask = true) when the biome isn't placed
	private final boolean mask;
	private final Holder<Biome> surroundingBiome;
	private final Holder<Biome> surroundedBiome;

	public SurroundedBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, float spawnChance, boolean mask, Holder<Biome> surroundingBiome, Holder<Biome> surroundedBiome) {
		this(registry, parent, checkRange, (int)(spawnChance * 10000), mask, surroundingBiome, surroundedBiome);
	}
	
	public SurroundedBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int spawnChance, boolean mask, Holder<Biome> surroundingBiome, Holder<Biome> surroundedBiome) {
		this.registry = registry;
		this.parent = parent;
		this.checkRange = checkRange;
		this.spawnChance = spawnChance;
		this.mask = mask;
		this.surroundingBiome = surroundingBiome;
		this.surroundedBiome = surroundedBiome;
	}

	@Override
	public BiomeLayerConfigured getParentLayer() {
		return this.parent;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, A parentArea, int x, int z) {
		biomePlaceCheck: {
			int surroundingBiomeId = BetweenlandsBiomeSource.getBiomeId(this.surroundingBiome);
			
			for(int xo = -this.checkRange; xo <= this.checkRange; xo++) {
				for(int zo = -this.checkRange; zo <= this.checkRange; zo++) {
					if(xo * xo + zo * zo <= this.checkRange * this.checkRange + 1) {
						int biomeID = parentArea.get(x + xo, z + zo);
						if(biomeID != surroundingBiomeId) {
							break biomePlaceCheck;
						}
					}
				}
			}
			
			RandomSource random = context.createRandom(x, z);
			
			if(random.nextInt(10000) <= this.spawnChance) {
				int surroundedBiomeId = BetweenlandsBiomeSource.getBiomeId(this.surroundedBiome);
				
				return surroundedBiomeId;
			}
		}

		// The biome couldn't be placed
		return this.mask ? -1 : parentArea.get(x, z);
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}
}
