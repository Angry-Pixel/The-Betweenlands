package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class TreeDecorator {
   public static final Codec<TreeDecorator> CODEC = Registry.TREE_DECORATOR_TYPES.byNameCodec().dispatch(TreeDecorator::type, TreeDecoratorType::codec);

   protected abstract TreeDecoratorType<?> type();

   public abstract void place(LevelSimulatedReader p_161745_, BiConsumer<BlockPos, BlockState> p_161746_, Random p_161747_, List<BlockPos> p_161748_, List<BlockPos> p_161749_);

   protected static void placeVine(BiConsumer<BlockPos, BlockState> p_161751_, BlockPos p_161752_, BooleanProperty p_161753_) {
      p_161751_.accept(p_161752_, Blocks.VINE.defaultBlockState().setValue(p_161753_, Boolean.valueOf(true)));
   }
}