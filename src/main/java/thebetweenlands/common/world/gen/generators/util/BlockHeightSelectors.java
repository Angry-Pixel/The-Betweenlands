package thebetweenlands.common.world.gen.generators.util;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;
import thebetweenlands.common.TheBetweenlands;

// This should probably be a registry
public class BlockHeightSelectors {

	private static final Map<ResourceLocation, MapCodec<? extends BlockHeightSelector>> REGISTRY = new Object2ObjectArrayMap<>();
	
	public static void register(ResourceLocation type, MapCodec<? extends BlockHeightSelector> codec) {
		REGISTRY.put(type, codec);
	}

	public static final Codec<? extends BlockHeightSelector> FULL_CODEC = ResourceLocation.CODEC.dispatch(BlockHeightSelector::getType, REGISTRY::get);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final Codec<? extends BlockHeightSelector> CODEC = NeoForgeExtraCodecs.withAlternative(
			(Codec)ConstantHeightSelector.INTEGER_CODEC,
			FULL_CODEC
		);
	
	// Generics
	@SuppressWarnings("unchecked")
	public static Codec<BlockHeightSelector> codec() {
		return (Codec<BlockHeightSelector>) CODEC;
	}
	
	public static interface BlockHeightSelector {
		/**
		 * Provide a Block Y from the provided context
		 * @param offsetX the X of the target column relative to the chunk (range 0-15)
		 * @param offsetZ the Z of the target column relative to the chunk (range 0-15)
		 * @param chunkPos the chunk position
		 * @param heightmaps the chunk's heightmaps
		 * @return
		 */
		public int getHeight(int offsetX, int offsetZ, ChunkPos chunkPos, ChunkHeightmaps heightmaps);
		
		public ResourceLocation getType();
	}

	public static record ConstantHeightSelector(int y) implements BlockHeightSelector {
		public static final Codec<ConstantHeightSelector> INTEGER_CODEC = Codec.INT.flatXmap((i) -> DataResult.success(new ConstantHeightSelector(i)), (BlockHeightSelector x) -> x instanceof ConstantHeightSelector selector ? DataResult.success(selector.y()) : DataResult.error(() -> "Expected instance of ConstantHeightSelector, got " + x.getClass()));
		
		public static final MapCodec<ConstantHeightSelector> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
					Codec.INT.fieldOf("value").forGetter(ConstantHeightSelector::y)
				).apply(instance, ConstantHeightSelector::new));
		
		public static final ResourceLocation TYPE = TheBetweenlands.prefix("constant");
		
		@Override
		public int getHeight(int offsetX, int offsetZ, ChunkPos chunkPos, ChunkHeightmaps heightmaps) {
			return this.y();
		}
		
		@Override
		public ResourceLocation getType() {
			return TYPE;
		}
	}

	public static record HeightmapBasedHeightSelector(Heightmap.Types type) implements BlockHeightSelector {
		public static final Codec<Heightmap.Types> HEIGHTMAP_TYPE = Codec.stringResolver(
				(type) -> switch(type) {
					case WORLD_SURFACE_WG: yield "surface";
					case OCEAN_FLOOR_WG: yield "ocean_floor";
					default: yield null;
				},
				(string) -> {
					if("surface".equalsIgnoreCase(string)) return Heightmap.Types.WORLD_SURFACE_WG;
					if("ocean_floor".equalsIgnoreCase(string)) return Heightmap.Types.OCEAN_FLOOR_WG;
					return null;
				}
			);
		
		public static final MapCodec<HeightmapBasedHeightSelector> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						HEIGHTMAP_TYPE.fieldOf("heightmap").forGetter(HeightmapBasedHeightSelector::type)
				).apply(instance, HeightmapBasedHeightSelector::new));

		public static final ResourceLocation TYPE = TheBetweenlands.prefix("heightmap");
		
		public HeightmapBasedHeightSelector(Heightmap.Types type) {
			if(type != Types.WORLD_SURFACE_WG && type != Types.OCEAN_FLOOR_WG) {
				throw new IllegalArgumentException("Heightmap type must be either WORLD_SURFACE_WG or OCEAN_FLOOR_WG, got " + type);
			}
			this.type = type;
		}
		
		@Override
		public int getHeight(int offsetX, int offsetZ, ChunkPos chunkPos, ChunkHeightmaps heightmaps) {
			switch(this.type()) {
			case WORLD_SURFACE_WG:
				return heightmaps.surfaceHeightmap().getFirstAvailable(offsetX, offsetZ);
			case OCEAN_FLOOR_WG:
				return heightmaps.oceanfloorHeightmap().getFirstAvailable(offsetX, offsetZ);
			default:
				throw new IllegalStateException("Heightmap type must be either WORLD_SURFACE_WG or OCEAN_FLOOR_WG, got " + this.type);
			}
		}
		
		@Override
		public ResourceLocation getType() {
			return TYPE;
		}
	}
	
	public static record OffsetHeightSelector(int offset, BlockHeightSelector delegate) implements BlockHeightSelector {
		public static final MapCodec<OffsetHeightSelector> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						Codec.INT.fieldOf("offset").forGetter(OffsetHeightSelector::offset),
						BlockHeightSelectors.codec().fieldOf("selector").forGetter(OffsetHeightSelector::delegate)
				).apply(instance, OffsetHeightSelector::new));

		public BlockHeightSelector getDelegate() {
			return this.delegate();
		}
		
		public static final ResourceLocation TYPE = TheBetweenlands.prefix("offset");
		
		@Override
		public int getHeight(int offsetX, int offsetZ, ChunkPos chunkPos, ChunkHeightmaps heightmaps) {
			return delegate.getHeight(offsetX, offsetZ, chunkPos, heightmaps) + this.offset();
		}

		@Override
		public ResourceLocation getType() {
			return TYPE;
		}
	}
	
	static {
		register(ConstantHeightSelector.TYPE, ConstantHeightSelector.MAP_CODEC);
		register(HeightmapBasedHeightSelector.TYPE, HeightmapBasedHeightSelector.MAP_CODEC);
		register(OffsetHeightSelector.TYPE, OffsetHeightSelector.MAP_CODEC);
	}
}
