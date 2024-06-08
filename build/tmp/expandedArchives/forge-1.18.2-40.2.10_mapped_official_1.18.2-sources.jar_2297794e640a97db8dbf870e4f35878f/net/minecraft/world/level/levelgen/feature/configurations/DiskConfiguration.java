package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public record DiskConfiguration(BlockState state, IntProvider radius, int halfHeight, List<BlockState> targets) implements FeatureConfiguration {
   public static final Codec<DiskConfiguration> CODEC = RecordCodecBuilder.create((p_191250_) -> {
      return p_191250_.group(BlockState.CODEC.fieldOf("state").forGetter(DiskConfiguration::state), IntProvider.codec(0, 8).fieldOf("radius").forGetter(DiskConfiguration::radius), Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskConfiguration::halfHeight), BlockState.CODEC.listOf().fieldOf("targets").forGetter(DiskConfiguration::targets)).apply(p_191250_, DiskConfiguration::new);
   });
}