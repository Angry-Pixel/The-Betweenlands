package thebetweenlands.common.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.neoforged.neoforge.registries.DeferredBlock;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsSurfaceRuleData extends SurfaceRuleData {

	public static final SurfaceRules.RuleSource SWAMP_GRASS = blockState(BlockRegistry.SWAMP_GRASS);
	public static final SurfaceRules.RuleSource SWAMP_DIRT = blockState(BlockRegistry.SWAMP_DIRT);
	public static final SurfaceRules.RuleSource DEAD_SWAMP_GRASS = blockState(BlockRegistry.DEAD_SWAMP_GRASS);
	public static final SurfaceRules.RuleSource MUD = blockState(BlockRegistry.MUD);
	public static final SurfaceRules.RuleSource MUD_UNDERWATER = blockState(BlockRegistry.MUD);
	public static final SurfaceRules.RuleSource BETWEENLANDS_BEDROCK = blockState(BlockRegistry.BETWEENLANDS_BEDROCK);
	public static final SurfaceRules.RuleSource PITSTONE = blockState(BlockRegistry.PITSTONE);

	public static SurfaceRules.RuleSource blockState(DeferredBlock<Block> block) {
		return SurfaceRules.state(block.get().defaultBlockState());
	}

	public static SurfaceRules.RuleSource betweenlands() {
		ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
		// Bedrock And pitstone
		builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BETWEENLANDS_BEDROCK));
		builder.add(SurfaceRules.ifTrue(BetweenlandsSurfaceRules.simplexGradient("pitstone", VerticalAnchor.absolute(40), VerticalAnchor.absolute(45), 0.06), PITSTONE));

		// Swamplands
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SWAMPLANDS), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), DEAD_SWAMP_GRASS),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Swamplands clearing
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SWAMPLANDS_CLEARING), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SWAMP_GRASS),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Patchy islands
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.PATCHY_ISLANDS), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SWAMP_GRASS),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Coarse islands
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.COARSE_ISLANDS), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SWAMP_GRASS),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Raised isles
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.RAISED_ISLES), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SWAMP_GRASS),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Marsh
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.MARSH), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SWAMP_GRASS),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Eroded marsh
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.ERODED_MARSH), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SWAMP_GRASS),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Sludge plains
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SLUDGE_PLAINS), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Sludge plains
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.SLUDGE_PLAINS_CLEARING), SurfaceRules.sequence(
			// Top layer: e.g. dirt and grass
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), SWAMP_DIRT)
				)
			),
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));
		// Deep waters
		builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(BiomeRegistry.DEEP_WATERS), SurfaceRules.sequence(
			// Underwater blocks
			SurfaceRules.sequence(
				// Underwater state
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), MUD_UNDERWATER),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(2, false, CaveSurface.FLOOR), MUD)
			)
		)));

		return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
	}
}
