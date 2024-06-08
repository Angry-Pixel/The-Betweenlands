package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockState extends BlockBehaviour.BlockStateBase implements net.minecraftforge.common.extensions.IForgeBlockState {
   public static final Codec<BlockState> CODEC = codec(Registry.BLOCK.byNameCodec(), Block::defaultBlockState).stable();

   public BlockState(Block p_61042_, ImmutableMap<Property<?>, Comparable<?>> p_61043_, MapCodec<BlockState> p_61044_) {
      super(p_61042_, p_61043_, p_61044_);
   }

   protected BlockState asState() {
      return this;
   }
}
