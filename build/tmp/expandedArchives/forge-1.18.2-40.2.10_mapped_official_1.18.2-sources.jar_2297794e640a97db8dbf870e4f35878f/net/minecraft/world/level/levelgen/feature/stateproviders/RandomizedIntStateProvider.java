package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RandomizedIntStateProvider extends BlockStateProvider {
   public static final Codec<RandomizedIntStateProvider> CODEC = RecordCodecBuilder.create((p_161576_) -> {
      return p_161576_.group(BlockStateProvider.CODEC.fieldOf("source").forGetter((p_161592_) -> {
         return p_161592_.source;
      }), Codec.STRING.fieldOf("property").forGetter((p_161590_) -> {
         return p_161590_.propertyName;
      }), IntProvider.CODEC.fieldOf("values").forGetter((p_161578_) -> {
         return p_161578_.values;
      })).apply(p_161576_, RandomizedIntStateProvider::new);
   });
   private final BlockStateProvider source;
   private final String propertyName;
   @Nullable
   private IntegerProperty property;
   private final IntProvider values;

   public RandomizedIntStateProvider(BlockStateProvider p_161562_, IntegerProperty p_161563_, IntProvider p_161564_) {
      this.source = p_161562_;
      this.property = p_161563_;
      this.propertyName = p_161563_.getName();
      this.values = p_161564_;
      Collection<Integer> collection = p_161563_.getPossibleValues();

      for(int i = p_161564_.getMinValue(); i <= p_161564_.getMaxValue(); ++i) {
         if (!collection.contains(i)) {
            throw new IllegalArgumentException("Property value out of range: " + p_161563_.getName() + ": " + i);
         }
      }

   }

   public RandomizedIntStateProvider(BlockStateProvider p_161566_, String p_161567_, IntProvider p_161568_) {
      this.source = p_161566_;
      this.propertyName = p_161567_;
      this.values = p_161568_;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.RANDOMIZED_INT_STATE_PROVIDER;
   }

   public BlockState getState(Random p_161585_, BlockPos p_161586_) {
      BlockState blockstate = this.source.getState(p_161585_, p_161586_);
      if (this.property == null || !blockstate.hasProperty(this.property)) {
         this.property = findProperty(blockstate, this.propertyName);
      }

      return blockstate.setValue(this.property, Integer.valueOf(this.values.sample(p_161585_)));
   }

   private static IntegerProperty findProperty(BlockState p_161571_, String p_161572_) {
      Collection<Property<?>> collection = p_161571_.getProperties();
      Optional<IntegerProperty> optional = collection.stream().filter((p_161583_) -> {
         return p_161583_.getName().equals(p_161572_);
      }).filter((p_161588_) -> {
         return p_161588_ instanceof IntegerProperty;
      }).map((p_161574_) -> {
         return (IntegerProperty)p_161574_;
      }).findAny();
      return optional.orElseThrow(() -> {
         return new IllegalArgumentException("Illegal property: " + p_161572_);
      });
   }
}