package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import thebetweenlands.common.features.SapTreeConfig;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FeatureHelperConfiguration implements FeatureConfiguration {
    public static FeatureHelperConfiguration NONE = new FeatureHelperConfiguration(List.of());
    public static List<TargetBlockState> SURFACE = List.of(
            new TargetBlockState(new BlockMatchTest(BlockRegistry.SWAMP_DIRT.get()), BlockRegistry.SWAMP_DIRT.get().defaultBlockState()),
            new TargetBlockState(new BlockMatchTest(BlockRegistry.SWAMP_GRASS.get()), BlockRegistry.SWAMP_GRASS.get().defaultBlockState()),
            new TargetBlockState(new BlockMatchTest(BlockRegistry.DEAD_SWAMP_GRASS.get()), BlockRegistry.DEAD_SWAMP_GRASS.get().defaultBlockState()),
            new TargetBlockState(new BlockMatchTest(BlockRegistry.MUD.get()), BlockRegistry.MUD.get().defaultBlockState())
    );
    public static List<TargetBlockState> LEAVES = List.of(
            new TargetBlockState(new BlockMatchTest(BlockRegistry.LEAVES_NIBBLETWIG_TREE.get()), BlockRegistry.LEAVES_NIBBLETWIG_TREE.get().defaultBlockState()),
            new TargetBlockState(new BlockMatchTest(BlockRegistry.LEAVES_RUBBER_TREE.get()), BlockRegistry.LEAVES_RUBBER_TREE.get().defaultBlockState()),
            new TargetBlockState(new BlockMatchTest(BlockRegistry.LEAVES_WEEDWOOD_TREE.get()), BlockRegistry.LEAVES_WEEDWOOD_TREE.get().defaultBlockState()),
            new TargetBlockState(new BlockMatchTest(BlockRegistry.LEAVES_SAP_TREE.get()), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState())
    );
    public static List<TargetBlockState> CAVERNS = List.of(
            new TargetBlockState(new BlockMatchTest(BlockRegistry.BETWEENSTONE.get()), BlockRegistry.BETWEENSTONE.get().defaultBlockState())
    );
    public static List<TargetBlockState> PITSTONE = List.of(
            new TargetBlockState(new BlockMatchTest(BlockRegistry.PITSTONE.get()), BlockRegistry.PITSTONE.get().defaultBlockState())
    );

    // Replaceable blocks list
    public List<TargetBlockState> replaceable;

    public static final Codec<FeatureHelperConfiguration> CODEC = RecordCodecBuilder.create((p_67849_) -> {
        return p_67849_.group(Codec.list(TargetBlockState.CODEC).fieldOf("replaceable").forGetter((p_161027_) -> {
            return p_161027_.replaceable;
        })).apply(p_67849_, FeatureHelperConfiguration::new);
    });

    public FeatureHelperConfiguration(List<TargetBlockState> replaceable) {
        this.replaceable = replaceable;
    }

    // Target helper
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

        public TargetBlockState(RuleTest p_161036_, BlockState p_161037_) {
            this.target = p_161036_;
            this.state = p_161037_;
        }
    }
}
