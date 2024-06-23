package thebetweenlands.common.world;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsSurfaceRules {

	private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(BlockRegistry.BETWEENLANDS_BEDROCK.get());
	private static final SurfaceRules.RuleSource PITSTONE = makeStateRule(BlockRegistry.PITSTONE.get());
	private static final SurfaceRules.RuleSource GRASS = makeStateRule(BlockRegistry.SWAMP_GRASS.get());
	private static final SurfaceRules.RuleSource DIRT = makeStateRule(BlockRegistry.SWAMP_DIRT.get());

	private static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	public static SurfaceRules.RuleSource buildRules() {
		SurfaceRules.RuleSource bedrockLayer = SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK);
		SurfaceRules.RuleSource pitstone = SurfaceRules.ifTrue(SurfaceRules.verticalGradient("pitstone", VerticalAnchor.bottom(), VerticalAnchor.absolute(47)), PITSTONE);

		return SurfaceRules.sequence(
			bedrockLayer,
			pitstone,
			makeGrassSurface()
		);
	}

	private static SurfaceRules.RuleSource makeGrassSurface() {
		SurfaceRules.RuleSource grassAboveSeaLevel = SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1), GRASS);
		SurfaceRules.RuleSource grassSurface = SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), grassAboveSeaLevel);

		SurfaceRules.RuleSource underwaterSurface = SurfaceRules.ifTrue(
			SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1)),
			SurfaceRules.ifTrue(
				SurfaceRules.not(SurfaceRules.waterBlockCheck(-1, 0)),
				DIRT
			)
		);

		SurfaceRules.RuleSource onFloor = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(grassSurface, underwaterSurface));

		SurfaceRules.RuleSource underFloor = SurfaceRules.ifTrue(
			SurfaceRules.waterStartCheck(-6, -1),
			SurfaceRules.ifTrue(
				SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1),
				SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
			)
		);

		return SurfaceRules.sequence(onFloor, underFloor);
	}
}
