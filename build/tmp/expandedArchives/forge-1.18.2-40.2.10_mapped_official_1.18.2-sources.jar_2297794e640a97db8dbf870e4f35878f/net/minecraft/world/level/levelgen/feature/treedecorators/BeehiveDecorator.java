package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

public class BeehiveDecorator extends TreeDecorator {
   public static final Codec<BeehiveDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(BeehiveDecorator::new, (p_69971_) -> {
      return p_69971_.probability;
   }).codec();
   private static final Direction WORLDGEN_FACING = Direction.SOUTH;
   private static final Direction[] SPAWN_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().filter((p_202307_) -> {
      return p_202307_ != WORLDGEN_FACING.getOpposite();
   }).toArray((p_202297_) -> {
      return new Direction[p_202297_];
   });
   private final float probability;

   public BeehiveDecorator(float p_69958_) {
      this.probability = p_69958_;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.BEEHIVE;
   }

   public void place(LevelSimulatedReader p_161710_, BiConsumer<BlockPos, BlockState> p_161711_, Random p_161712_, List<BlockPos> p_161713_, List<BlockPos> p_161714_) {
      if (!(p_161712_.nextFloat() >= this.probability)) {
         int i = !p_161714_.isEmpty() ? Math.max(p_161714_.get(0).getY() - 1, p_161713_.get(0).getY() + 1) : Math.min(p_161713_.get(0).getY() + 1 + p_161712_.nextInt(3), p_161713_.get(p_161713_.size() - 1).getY());
         List<BlockPos> list = p_161713_.stream().filter((p_202300_) -> {
            return p_202300_.getY() == i;
         }).flatMap((p_202305_) -> {
            return Stream.of(SPAWN_DIRECTIONS).map(p_202305_::relative);
         }).collect(Collectors.toList());
         if (!list.isEmpty()) {
            Collections.shuffle(list);
            Optional<BlockPos> optional = list.stream().filter((p_202303_) -> {
               return Feature.isAir(p_161710_, p_202303_) && Feature.isAir(p_161710_, p_202303_.relative(WORLDGEN_FACING));
            }).findFirst();
            if (!optional.isEmpty()) {
               p_161711_.accept(optional.get(), Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
               p_161710_.getBlockEntity(optional.get(), BlockEntityType.BEEHIVE).ifPresent((p_202310_) -> {
                  int j = 2 + p_161712_.nextInt(2);

                  for(int k = 0; k < j; ++k) {
                     CompoundTag compoundtag = new CompoundTag();
                     compoundtag.putString("id", Registry.ENTITY_TYPE.getKey(EntityType.BEE).toString());
                     p_202310_.storeBee(compoundtag, p_161712_.nextInt(599), false);
                  }

               });
            }
         }
      }
   }
}