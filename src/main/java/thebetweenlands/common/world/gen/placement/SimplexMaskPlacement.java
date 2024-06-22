package thebetweenlands.common.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import thebetweenlands.common.registries.PlacementRegistry;

import java.util.Random;

// Emulates tree noise
// TODO: add datapack functionality
public class SimplexMaskPlacement extends PlacementFilter implements PlacementModifierType<SimplexMaskPlacement> {
	private static final SimplexMaskPlacement INSTANCE = new SimplexMaskPlacement();
	public static Codec<SimplexMaskPlacement> CODEC = Codec.unit(() -> {
		return INSTANCE;
	});

	public final SimplexNoise noise;

	public SimplexMaskPlacement() {
		XoroshiroRandomSource source = new XoroshiroRandomSource(678);
		this.noise = new SimplexNoise(source);
	}

	@Override
	protected boolean shouldPlace(PlacementContext context, Random rand, BlockPos pos) {
		return noise.getValue(pos.getX() * 0.01, pos.getZ() * 0.01) > -0.25;
	}

	@Override
	public PlacementModifierType<?> type() {
		return PlacementRegistry.SimplexMaskPlacementType.get();
	}

	@Override
	public Codec<SimplexMaskPlacement> codec() {
		return CODEC;
	}
}
