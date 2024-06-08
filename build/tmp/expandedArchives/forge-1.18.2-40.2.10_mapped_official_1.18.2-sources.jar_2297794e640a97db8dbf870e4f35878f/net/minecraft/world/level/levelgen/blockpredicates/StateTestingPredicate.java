package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StateTestingPredicate implements BlockPredicate {
   protected final Vec3i offset;

   protected static <P extends StateTestingPredicate> P1<Mu<P>, Vec3i> stateTestingCodec(Instance<P> p_190547_) {
      return p_190547_.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((p_190549_) -> {
         return p_190549_.offset;
      }));
   }

   protected StateTestingPredicate(Vec3i p_190541_) {
      this.offset = p_190541_;
   }

   public final boolean test(WorldGenLevel p_190543_, BlockPos p_190544_) {
      return this.test(p_190543_.getBlockState(p_190544_.offset(this.offset)));
   }

   protected abstract boolean test(BlockState p_190545_);
}