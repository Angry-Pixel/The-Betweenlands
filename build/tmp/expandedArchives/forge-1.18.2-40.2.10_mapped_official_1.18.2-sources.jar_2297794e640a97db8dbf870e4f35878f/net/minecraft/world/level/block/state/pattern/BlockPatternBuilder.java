package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
   private static final Joiner COMMA_JOINED = Joiner.on(",");
   private final List<String[]> pattern = Lists.newArrayList();
   private final Map<Character, Predicate<BlockInWorld>> lookup = Maps.newHashMap();
   private int height;
   private int width;

   private BlockPatternBuilder() {
      this.lookup.put(' ', (p_187549_) -> {
         return true;
      });
   }

   public BlockPatternBuilder aisle(String... p_61248_) {
      if (!ArrayUtils.isEmpty((Object[])p_61248_) && !StringUtils.isEmpty(p_61248_[0])) {
         if (this.pattern.isEmpty()) {
            this.height = p_61248_.length;
            this.width = p_61248_[0].length();
         }

         if (p_61248_.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + p_61248_.length + ")");
         } else {
            for(String s : p_61248_) {
               if (s.length() != this.width) {
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + s.length() + ")");
               }

               for(char c0 : s.toCharArray()) {
                  if (!this.lookup.containsKey(c0)) {
                     this.lookup.put(c0, (Predicate<BlockInWorld>)null);
                  }
               }
            }

            this.pattern.add(p_61248_);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static BlockPatternBuilder start() {
      return new BlockPatternBuilder();
   }

   public BlockPatternBuilder where(char p_61245_, Predicate<BlockInWorld> p_61246_) {
      this.lookup.put(p_61245_, p_61246_);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.createPattern());
   }

   private Predicate<BlockInWorld>[][][] createPattern() {
      this.ensureAllCharactersMatched();
      Predicate<BlockInWorld>[][][] predicate = (Predicate[][][])Array.newInstance(Predicate.class, this.pattern.size(), this.height, this.width);

      for(int i = 0; i < this.pattern.size(); ++i) {
         for(int j = 0; j < this.height; ++j) {
            for(int k = 0; k < this.width; ++k) {
               predicate[i][j][k] = this.lookup.get((this.pattern.get(i))[j].charAt(k));
            }
         }
      }

      return predicate;
   }

   private void ensureAllCharactersMatched() {
      List<Character> list = Lists.newArrayList();

      for(Entry<Character, Predicate<BlockInWorld>> entry : this.lookup.entrySet()) {
         if (entry.getValue() == null) {
            list.add(entry.getKey());
         }
      }

      if (!list.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join(list) + " are missing");
      }
   }
}