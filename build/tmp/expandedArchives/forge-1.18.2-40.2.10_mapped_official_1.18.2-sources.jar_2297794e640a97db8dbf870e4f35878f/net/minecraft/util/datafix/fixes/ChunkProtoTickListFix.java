package net.minecraft.util.datafix.fixes;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableInt;

public class ChunkProtoTickListFix extends DataFix {
   private static final int SECTION_WIDTH = 16;
   private static final ImmutableSet<String> ALWAYS_WATERLOGGED = ImmutableSet.of("minecraft:bubble_column", "minecraft:kelp", "minecraft:kelp_plant", "minecraft:seagrass", "minecraft:tall_seagrass");

   public ChunkProtoTickListFix(Schema p_184988_) {
      super(p_184988_, false);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> opticfinder = type.findField("Level");
      OpticFinder<?> opticfinder1 = opticfinder.type().findField("Sections");
      OpticFinder<?> opticfinder2 = ((ListType)opticfinder1.type()).getElement().finder();
      OpticFinder<?> opticfinder3 = opticfinder2.type().findField("block_states");
      OpticFinder<?> opticfinder4 = opticfinder2.type().findField("biomes");
      OpticFinder<?> opticfinder5 = opticfinder3.type().findField("palette");
      OpticFinder<Typed<?>> opticfinder6 = (OpticFinder<Typed<?>>) opticfinder.type().findField("TileTicks");
      return this.fixTypeEverywhereTyped("ChunkProtoTickListFix", type, (p_185002_) -> {
         return p_185002_.updateTyped(opticfinder, (p_185010_) -> {
            p_185010_ = p_185010_.update(DSL.remainderFinder(), (p_185078_) -> {
               return DataFixUtils.orElse(p_185078_.get("LiquidTicks").result().map((p_185072_) -> {
                  return p_185078_.set("fluid_ticks", p_185072_).remove("LiquidTicks");
               }), p_185078_);
            });
            Dynamic<?> dynamic = p_185010_.get(DSL.remainderFinder());
            MutableInt mutableint = new MutableInt();
            Int2ObjectMap<Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer>> int2objectmap = new Int2ObjectArrayMap<>();
            p_185010_.getOptionalTyped(opticfinder1).ifPresent((p_185018_) -> {
               p_185018_.getAllTyped(opticfinder2).forEach((p_185025_) -> {
                  Dynamic<?> dynamic3 = p_185025_.get(DSL.remainderFinder());
                  int k = dynamic3.get("Y").asInt(Integer.MAX_VALUE);
                  if (k != Integer.MAX_VALUE) {
                     if (p_185025_.getOptionalTyped(opticfinder4).isPresent()) {
                        mutableint.setValue(Math.min(k, mutableint.getValue()));
                     }

                     p_185025_.getOptionalTyped(opticfinder3).ifPresent((p_185064_) -> {
                        int2objectmap.put(k, Suppliers.memoize(() -> {
                           List<? extends Dynamic<?>> list = p_185064_.getOptionalTyped(opticfinder5).map((p_185027_) -> {
                              return p_185027_.write().result().map((p_185076_) -> {
                                 return p_185076_.asList(Function.identity());
                              }).orElse(Collections.emptyList());
                           }).orElse(Collections.emptyList());
                           long[] along = p_185064_.get(DSL.remainderFinder()).get("data").asLongStream().toArray();
                           return new ChunkProtoTickListFix.PoorMansPalettedContainer(list, along);
                        }));
                     });
                  }
               });
            });
            byte b0 = mutableint.getValue().byteValue();
            p_185010_ = p_185010_.update(DSL.remainderFinder(), (p_184991_) -> {
               return p_184991_.update("yPos", (p_185067_) -> {
                  return p_185067_.createByte(b0);
               });
            });
            if (!p_185010_.getOptionalTyped(opticfinder6).isPresent() && !dynamic.get("fluid_ticks").result().isPresent()) {
               int i = dynamic.get("xPos").asInt(0);
               int j = dynamic.get("zPos").asInt(0);
               Dynamic<?> dynamic1 = this.makeTickList(dynamic, int2objectmap, b0, i, j, "LiquidsToBeTicked", ChunkProtoTickListFix::getLiquid);
               Dynamic<?> dynamic2 = this.makeTickList(dynamic, int2objectmap, b0, i, j, "ToBeTicked", ChunkProtoTickListFix::getBlock);
               Optional<? extends Pair<? extends Typed<Typed<?>>, ?>> optional = opticfinder6.type().readTyped(dynamic2).result();
               if (optional.isPresent()) {
                  p_185010_ = p_185010_.<Typed<?>, Typed<?>>set(opticfinder6, optional.get().getFirst());
               }

               return p_185010_.update(DSL.remainderFinder(), (p_185035_) -> {
                  return p_185035_.remove("ToBeTicked").remove("LiquidsToBeTicked").set("fluid_ticks", dynamic1);
               });
            } else {
               return p_185010_;
            }
         });
      });
   }

   private Dynamic<?> makeTickList(Dynamic<?> p_185037_, Int2ObjectMap<Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer>> p_185038_, byte p_185039_, int p_185040_, int p_185041_, String p_185042_, Function<Dynamic<?>, String> p_185043_) {
      Stream<Dynamic<?>> stream = Stream.empty();
      List<? extends Dynamic<?>> list = p_185037_.get(p_185042_).asList(Function.identity());

      for(int i = 0; i < list.size(); ++i) {
         int j = i + p_185039_;
         Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer> supplier = p_185038_.get(j);
         Stream<? extends Dynamic<?>> stream1 = list.get(i).asStream().mapToInt((p_185074_) -> {
            return p_185074_.asShort((short)-1);
         }).filter((p_184993_) -> {
            return p_184993_ > 0;
         }).mapToObj((p_185059_) -> {
            return this.createTick(p_185037_, supplier, p_185040_, j, p_185041_, p_185059_, p_185043_);
         });
         stream = Stream.concat(stream, stream1);
      }

      return p_185037_.createList(stream);
   }

   private static String getBlock(@Nullable Dynamic<?> p_185032_) {
      return p_185032_ != null ? p_185032_.get("Name").asString("minecraft:air") : "minecraft:air";
   }

   private static String getLiquid(@Nullable Dynamic<?> p_185069_) {
      if (p_185069_ == null) {
         return "minecraft:empty";
      } else {
         String s = p_185069_.get("Name").asString("");
         if ("minecraft:water".equals(s)) {
            return p_185069_.get("Properties").get("level").asInt(0) == 0 ? "minecraft:water" : "minecraft:flowing_water";
         } else if ("minecraft:lava".equals(s)) {
            return p_185069_.get("Properties").get("level").asInt(0) == 0 ? "minecraft:lava" : "minecraft:flowing_lava";
         } else {
            return !ALWAYS_WATERLOGGED.contains(s) && !p_185069_.get("Properties").get("waterlogged").asBoolean(false) ? "minecraft:empty" : "minecraft:water";
         }
      }
   }

   private Dynamic<?> createTick(Dynamic<?> p_185045_, @Nullable Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer> p_185046_, int p_185047_, int p_185048_, int p_185049_, int p_185050_, Function<Dynamic<?>, String> p_185051_) {
      int i = p_185050_ & 15;
      int j = p_185050_ >>> 4 & 15;
      int k = p_185050_ >>> 8 & 15;
      String s = p_185051_.apply(p_185046_ != null ? p_185046_.get().get(i, j, k) : null);
      return p_185045_.createMap(ImmutableMap.<Dynamic<?>, Dynamic<?>>builder().put(p_185045_.createString("i"), p_185045_.createString(s)).put(p_185045_.createString("x"), p_185045_.createInt(p_185047_ * 16 + i)).put(p_185045_.createString("y"), p_185045_.createInt(p_185048_ * 16 + j)).put(p_185045_.createString("z"), p_185045_.createInt(p_185049_ * 16 + k)).put(p_185045_.createString("t"), p_185045_.createInt(0)).put(p_185045_.createString("p"), p_185045_.createInt(0)).build());
   }

   public static final class PoorMansPalettedContainer {
      private static final long SIZE_BITS = 4L;
      private final List<? extends Dynamic<?>> palette;
      private final long[] data;
      private final int bits;
      private final long mask;
      private final int valuesPerLong;

      public PoorMansPalettedContainer(List<? extends Dynamic<?>> p_185087_, long[] p_185088_) {
         this.palette = p_185087_;
         this.data = p_185088_;
         this.bits = Math.max(4, ChunkHeightAndBiomeFix.ceillog2(p_185087_.size()));
         this.mask = (1L << this.bits) - 1L;
         this.valuesPerLong = (char)(64 / this.bits);
      }

      @Nullable
      public Dynamic<?> get(int p_185091_, int p_185092_, int p_185093_) {
         int i = this.palette.size();
         if (i < 1) {
            return null;
         } else if (i == 1) {
            return this.palette.get(0);
         } else {
            int j = this.getIndex(p_185091_, p_185092_, p_185093_);
            int k = j / this.valuesPerLong;
            if (k >= 0 && k < this.data.length) {
               long l = this.data[k];
               int i1 = (j - k * this.valuesPerLong) * this.bits;
               int j1 = (int)(l >> i1 & this.mask);
               return j1 >= 0 && j1 < i ? this.palette.get(j1) : null;
            } else {
               return null;
            }
         }
      }

      private int getIndex(int p_185096_, int p_185097_, int p_185098_) {
         return (p_185097_ << 4 | p_185098_) << 4 | p_185096_;
      }

      public List<? extends Dynamic<?>> palette() {
         return this.palette;
      }

      public long[] data() {
         return this.data;
      }
   }
}