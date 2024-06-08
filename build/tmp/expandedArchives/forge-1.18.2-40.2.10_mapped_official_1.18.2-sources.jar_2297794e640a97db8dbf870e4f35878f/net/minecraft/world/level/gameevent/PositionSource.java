package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;

public interface PositionSource {
   Codec<PositionSource> CODEC = Registry.POSITION_SOURCE_TYPE.byNameCodec().dispatch(PositionSource::getType, PositionSourceType::codec);

   Optional<BlockPos> getPosition(Level p_157870_);

   PositionSourceType<?> getType();
}