package thebetweenlands.common.world.gen.feature.config;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;

import java.util.List;

public class BlockReplacementConfiguration implements FeatureConfiguration {

	public static final Codec<BlockReplacementConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			ExtraCodecs.POSITIVE_INT.fieldOf("offset").forGetter(obj -> obj.offset),
			ExtraCodecs.POSITIVE_INT.fieldOf("attempts").forGetter(obj -> obj.attempts),
			Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter(obj -> obj.targets),
			Codec.BOOL.fieldOf("inherit_properties").forGetter(obj -> obj.inherit)
		).apply(instance, BlockReplacementConfiguration::new));

	public final List<OreConfiguration.TargetBlockState> targets;
	public final int offset;
	public final int attempts;
	public final boolean inherit;

	public BlockReplacementConfiguration(BlockState state, int offset, int attempts, BlockState target, boolean inherit) {
		this(offset, attempts, ImmutableList.of(OreConfiguration.target(new BlockStateMatchTest(target), state)), inherit);
	}

	public BlockReplacementConfiguration(int offset, int attempts, List<OreConfiguration.TargetBlockState> targets, boolean inherit) {
		this.offset = offset;
		this.attempts = attempts;
		this.targets = targets;
		this.inherit = inherit;
	}
}
