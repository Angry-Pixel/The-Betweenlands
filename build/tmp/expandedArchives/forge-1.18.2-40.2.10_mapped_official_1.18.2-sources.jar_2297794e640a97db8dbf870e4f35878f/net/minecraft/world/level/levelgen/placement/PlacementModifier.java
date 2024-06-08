package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;

public abstract class PlacementModifier {
   public static final Codec<PlacementModifier> CODEC = Registry.PLACEMENT_MODIFIERS.byNameCodec().dispatch(PlacementModifier::type, PlacementModifierType::codec);

   public abstract Stream<BlockPos> getPositions(PlacementContext p_191845_, Random p_191846_, BlockPos p_191847_);

   public abstract PlacementModifierType<?> type();
}