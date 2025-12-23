package thebetweenlands.common.world.gen.layer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.SingleParentBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public class ZoomBiomeLayer implements SingleParentBiomeLayer {

	public static final MapCodec<ZoomBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryOps.retrieveGetter(Registries.BIOME),
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parent)
				).apply(instance, ZoomBiomeLayer::new)
		);

	private final BiomeLayerConfigured parent;
	
	public ZoomBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent) {
		this.parent = parent;
	}
	
	@Override
	public BiomeLayerConfigured getParentLayer() {
		return this.parent;
	}
	
	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

	public static int getParentX(int x) {
		return x >> 1;
	}

	public  static int getParentY(int y) {
		return y >> 1;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, A parentArea, int x, int z) {
		int initialBiome = parentArea.get(getParentX(x), getParentY(z));
		RandomSource random = context.createRandom(x >> 1 << 1, z >> 1 << 1);
		int pX = x & 1;
		int pZ = z & 1;

		if (pX == 0 && pZ == 0) {
			return initialBiome;
		} else {
			int initialBiomeZ = parentArea.get(getParentX(x), getParentY(z + 1));
			int rand1 = context.random(random, initialBiome, initialBiomeZ);

			if (pX == 0 && pZ == 1) {
				return rand1;
			} else {
				int initialBiomeX = parentArea.get(getParentX(x + 1), getParentY(z));
				int rand2 = context.random(random, initialBiome, initialBiomeX);

				if (pX == 1 && pZ == 0) {
					return rand2;
				} else {
					int initialBiomeXZ = parentArea.get(getParentX(x + 1), getParentY(z + 1));
					return this.modeOrRandom(context, random, initialBiome, initialBiomeZ, initialBiomeX, initialBiomeXZ);
				}
			}
		}
	}

	protected <A extends Area> int modeOrRandom(BiomeLayerContext<A> context, RandomSource random, int first, int second, int third, int fourth) {
		if (second == third && third == fourth) {
			return second;
		} else if (first == second && first == third) {
			return first;
		} else if (first == second && first == fourth) {
			return first;
		} else if (first == third && first == fourth) {
			return first;
		} else if (first == second && third != fourth) {
			return first;
		} else if (first == third && second != fourth) {
			return first;
		} else if (first == fourth && second != third) {
			return first;
		} else if (second == third && first != fourth) {
			return second;
		} else if (second == fourth && first != third) {
			return second;
		} else {
			return third == fourth && first != second ? third : context.random(random, first, second, third, fourth);
		}
	}
	
}
