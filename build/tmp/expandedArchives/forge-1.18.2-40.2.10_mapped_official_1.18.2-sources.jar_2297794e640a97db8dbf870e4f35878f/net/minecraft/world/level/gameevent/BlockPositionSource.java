package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class BlockPositionSource implements PositionSource {
   public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create((p_157710_) -> {
      return p_157710_.group(BlockPos.CODEC.fieldOf("pos").xmap(Optional::of, Optional::get).forGetter((p_157712_) -> {
         return p_157712_.pos;
      })).apply(p_157710_, BlockPositionSource::new);
   });
   final Optional<BlockPos> pos;

   public BlockPositionSource(BlockPos p_157703_) {
      this(Optional.of(p_157703_));
   }

   public BlockPositionSource(Optional<BlockPos> p_157705_) {
      this.pos = p_157705_;
   }

   public Optional<BlockPos> getPosition(Level p_157708_) {
      return this.pos;
   }

   public PositionSourceType<?> getType() {
      return PositionSourceType.BLOCK;
   }

   public static class Type implements PositionSourceType<BlockPositionSource> {
      public BlockPositionSource read(FriendlyByteBuf p_157716_) {
         return new BlockPositionSource(Optional.of(p_157716_.readBlockPos()));
      }

      public void write(FriendlyByteBuf p_157718_, BlockPositionSource p_157719_) {
         p_157719_.pos.ifPresent(p_157718_::writeBlockPos);
      }

      public Codec<BlockPositionSource> codec() {
         return BlockPositionSource.CODEC;
      }
   }
}