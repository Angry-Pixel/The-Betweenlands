package thebetweenlands.common.features;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class SapTreeConfig implements FeatureConfiguration {
	
	public final List<TargetBlockState> targetStates;
	
	public static final Codec<SapTreeConfig> CODEC = RecordCodecBuilder.create((p_67849_) -> {
		return p_67849_.group(Codec.list(TargetBlockState.CODEC).fieldOf("targets").forGetter((p_161027_) -> {
			return p_161027_.targetStates;
		})).apply(p_67849_, SapTreeConfig::new);
	});
	
	public SapTreeConfig(List<TargetBlockState> targetstates) {
		this.targetStates = targetstates;
	}
	
	// Blocks set to place vegitation onto (reusing names to make it fimilier to use)
	public static class TargetBlockState {
		public static final Codec<TargetBlockState> CODEC = RecordCodecBuilder.create((p_161039_) -> {
			return p_161039_.group(RuleTest.CODEC.fieldOf("target").forGetter((p_161043_) -> {
				return p_161043_.target;
			}), BlockState.CODEC.fieldOf("state").forGetter((p_161041_) -> {
				return p_161041_.state;
			})).apply(p_161039_, TargetBlockState::new);
		});
		public final RuleTest target;
		public final BlockState state;

		TargetBlockState(RuleTest p_161036_, BlockState p_161037_) {
			this.target = p_161036_;
			this.state = p_161037_;
		}
	}
}
