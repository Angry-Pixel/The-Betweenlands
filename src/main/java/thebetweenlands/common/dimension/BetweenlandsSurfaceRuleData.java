package thebetweenlands.common.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.BetweenlandsSurfaceRules;

public class BetweenlandsSurfaceRuleData extends SurfaceRuleData {

	public static final SurfaceRules.RuleSource SWAMP_GRASS = blockState(BlockRegistry.SWAMP_GRASS);
	public static final SurfaceRules.RuleSource SWAMP_DIRT = blockState(BlockRegistry.SWAMP_DIRT);
	public static final SurfaceRules.RuleSource DEAD_SWAMP_GRASS = blockState(BlockRegistry.DEAD_SWAMP_GRASS);
	public static final SurfaceRules.RuleSource MUD = blockState(BlockRegistry.MUD);
	public static final SurfaceRules.RuleSource MUD_UNDERWATER = blockState(BlockRegistry.MUD);
	public static final SurfaceRules.RuleSource BETWEENLANDS_BEDROCK = blockState(BlockRegistry.BETWEENLANDS_BEDROCK);
	public static final SurfaceRules.RuleSource PITSTONE = blockState(BlockRegistry.PITSTONE);

	public static SurfaceRules.RuleSource blockState(RegistryObject<Block> block) {
		return SurfaceRules.state(block.get().defaultBlockState());
	}

	public static SurfaceRules.RuleSource blockState(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	public static ImmutableList.Builder<SurfaceRules.RuleSource> applyBiomeSurfaceRules(ImmutableList.Builder<SurfaceRules.RuleSource> builder) {
		// Add all registered betweenlands biome surface rules
		return builder;
	}

	// The base betweenlands dimension surface using biome reg input
	public static SurfaceRules.RuleSource betweenlands() {
		ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
		// Bedrock And pitstone
		builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BETWEENLANDS_BEDROCK));
		builder.add(SurfaceRules.ifTrue(BetweenlandsSurfaceRules.simplexGradient("pitstone", VerticalAnchor.absolute(40), VerticalAnchor.absolute(45), 0.06), PITSTONE));
		// Biomes (TODO: possibly put biome surface rule in biome helper class and then append them to this instead)
		// Biome append snippet for whenever i get around to it
		// builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SWAMPLANDS.biome.getKey()), SurfaceRules.sequence()));

		// Swamplands
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SWAMPLANDS.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						DEAD_SWAMP_GRASS
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Swamplands clearing
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SWAMPLANDS_CLEARING.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						SWAMP_GRASS
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Patchy islands
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.PATCHY_ISLANDS.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						SWAMP_GRASS
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Coarse islands
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.COARSE_ISLANDS.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						SWAMP_GRASS
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Raised isles
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.RAISED_ISLES.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						SWAMP_GRASS
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Marsh
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.MARSH.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						SWAMP_GRASS
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Eroded marsh
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.ERODED_MARSH.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						SWAMP_GRASS
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Sludge plains
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SLUDGE_PLAINS.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						MUD
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Sludge plains
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SLUDGE_PLAINS_CLEARING.biome.getKey()), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
						MUD
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
						SWAMP_DIRT
					)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));
		// Deep waters
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.DEEP_WATERS.biome.getKey()), SurfaceRules.sequence(
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
					MUD_UNDERWATER
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR),
					MUD
				)
			)
		)));

		return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
	}

	// TODO: setup
	// Helper for data pack dimensions surfaces, uses a json input instead of hardcoded registry
	public static SurfaceRules.RuleSource betweenlandsLike() {
		ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
		return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
	}
}
