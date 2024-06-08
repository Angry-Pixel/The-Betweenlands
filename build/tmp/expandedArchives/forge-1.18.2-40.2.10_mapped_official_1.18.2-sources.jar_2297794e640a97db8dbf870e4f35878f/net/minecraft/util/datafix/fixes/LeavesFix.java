package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.PackedBitStorage;

public class LeavesFix extends DataFix {
   private static final int NORTH_WEST_MASK = 128;
   private static final int WEST_MASK = 64;
   private static final int SOUTH_WEST_MASK = 32;
   private static final int SOUTH_MASK = 16;
   private static final int SOUTH_EAST_MASK = 8;
   private static final int EAST_MASK = 4;
   private static final int NORTH_EAST_MASK = 2;
   private static final int NORTH_MASK = 1;
   private static final int[][] DIRECTIONS = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final int DECAY_DISTANCE = 7;
   private static final int SIZE_BITS = 12;
   private static final int SIZE = 4096;
   static final Object2IntMap<String> LEAVES = DataFixUtils.make(new Object2IntOpenHashMap<>(), (p_16235_) -> {
      p_16235_.put("minecraft:acacia_leaves", 0);
      p_16235_.put("minecraft:birch_leaves", 1);
      p_16235_.put("minecraft:dark_oak_leaves", 2);
      p_16235_.put("minecraft:jungle_leaves", 3);
      p_16235_.put("minecraft:oak_leaves", 4);
      p_16235_.put("minecraft:spruce_leaves", 5);
   });
   static final Set<String> LOGS = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", "minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log");

   public LeavesFix(Schema p_16205_, boolean p_16206_) {
      super(p_16205_, p_16206_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> opticfinder = type.findField("Level");
      OpticFinder<?> opticfinder1 = opticfinder.type().findField("Sections");
      Type<?> type1 = opticfinder1.type();
      if (!(type1 instanceof ListType)) {
         throw new IllegalStateException("Expecting sections to be a list.");
      } else {
         Type<?> type2 = ((ListType)type1).getElement();
         OpticFinder<?> opticfinder2 = DSL.typeFinder(type2);
         return this.fixTypeEverywhereTyped("Leaves fix", type, (p_16220_) -> {
            return p_16220_.updateTyped(opticfinder, (p_145461_) -> {
               int[] aint = new int[]{0};
               Typed<?> typed = p_145461_.updateTyped(opticfinder1, (p_145465_) -> {
                  Int2ObjectMap<LeavesFix.LeavesSection> int2objectmap = new Int2ObjectOpenHashMap<>(p_145465_.getAllTyped(opticfinder2).stream().map((p_145467_) -> {
                     return new LeavesFix.LeavesSection(p_145467_, this.getInputSchema());
                  }).collect(Collectors.toMap(LeavesFix.Section::getIndex, (p_145457_) -> {
                     return p_145457_;
                  })));
                  if (int2objectmap.values().stream().allMatch(LeavesFix.Section::isSkippable)) {
                     return p_145465_;
                  } else {
                     List<IntSet> list = Lists.newArrayList();

                     for(int i = 0; i < 7; ++i) {
                        list.add(new IntOpenHashSet());
                     }

                     for(LeavesFix.LeavesSection leavesfix$leavessection : int2objectmap.values()) {
                        if (!leavesfix$leavessection.isSkippable()) {
                           for(int j = 0; j < 4096; ++j) {
                              int k = leavesfix$leavessection.getBlock(j);
                              if (leavesfix$leavessection.isLog(k)) {
                                 list.get(0).add(leavesfix$leavessection.getIndex() << 12 | j);
                              } else if (leavesfix$leavessection.isLeaf(k)) {
                                 int l = this.getX(j);
                                 int i1 = this.getZ(j);
                                 aint[0] |= getSideMask(l == 0, l == 15, i1 == 0, i1 == 15);
                              }
                           }
                        }
                     }

                     for(int j3 = 1; j3 < 7; ++j3) {
                        IntSet intset = list.get(j3 - 1);
                        IntSet intset1 = list.get(j3);
                        IntIterator intiterator = intset.iterator();

                        while(intiterator.hasNext()) {
                           int k3 = intiterator.nextInt();
                           int l3 = this.getX(k3);
                           int j1 = this.getY(k3);
                           int k1 = this.getZ(k3);

                           for(int[] aint1 : DIRECTIONS) {
                              int l1 = l3 + aint1[0];
                              int i2 = j1 + aint1[1];
                              int j2 = k1 + aint1[2];
                              if (l1 >= 0 && l1 <= 15 && j2 >= 0 && j2 <= 15 && i2 >= 0 && i2 <= 255) {
                                 LeavesFix.LeavesSection leavesfix$leavessection1 = int2objectmap.get(i2 >> 4);
                                 if (leavesfix$leavessection1 != null && !leavesfix$leavessection1.isSkippable()) {
                                    int k2 = getIndex(l1, i2 & 15, j2);
                                    int l2 = leavesfix$leavessection1.getBlock(k2);
                                    if (leavesfix$leavessection1.isLeaf(l2)) {
                                       int i3 = leavesfix$leavessection1.getDistance(l2);
                                       if (i3 > j3) {
                                          leavesfix$leavessection1.setDistance(k2, l2, j3);
                                          intset1.add(getIndex(l1, i2, j2));
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }

                     return p_145465_.updateTyped(opticfinder2, (p_145470_) -> {
                        return int2objectmap.get(p_145470_.get(DSL.remainderFinder()).get("Y").asInt(0)).write(p_145470_);
                     });
                  }
               });
               if (aint[0] != 0) {
                  typed = typed.update(DSL.remainderFinder(), (p_145473_) -> {
                     Dynamic<?> dynamic = DataFixUtils.orElse(p_145473_.get("UpgradeData").result(), p_145473_.emptyMap());
                     return p_145473_.set("UpgradeData", dynamic.set("Sides", p_145473_.createByte((byte)(dynamic.get("Sides").asByte((byte)0) | aint[0]))));
                  });
               }

               return typed;
            });
         });
      }
   }

   public static int getIndex(int p_16211_, int p_16212_, int p_16213_) {
      return p_16212_ << 8 | p_16213_ << 4 | p_16211_;
   }

   private int getX(int p_16209_) {
      return p_16209_ & 15;
   }

   private int getY(int p_16246_) {
      return p_16246_ >> 8 & 255;
   }

   private int getZ(int p_16248_) {
      return p_16248_ >> 4 & 15;
   }

   public static int getSideMask(boolean p_16237_, boolean p_16238_, boolean p_16239_, boolean p_16240_) {
      int i = 0;
      if (p_16239_) {
         if (p_16238_) {
            i |= 2;
         } else if (p_16237_) {
            i |= 128;
         } else {
            i |= 1;
         }
      } else if (p_16240_) {
         if (p_16237_) {
            i |= 32;
         } else if (p_16238_) {
            i |= 8;
         } else {
            i |= 16;
         }
      } else if (p_16238_) {
         i |= 4;
      } else if (p_16237_) {
         i |= 64;
      }

      return i;
   }

   public static final class LeavesSection extends LeavesFix.Section {
      private static final String PERSISTENT = "persistent";
      private static final String DECAYABLE = "decayable";
      private static final String DISTANCE = "distance";
      @Nullable
      private IntSet leaveIds;
      @Nullable
      private IntSet logIds;
      @Nullable
      private Int2IntMap stateToIdMap;

      public LeavesSection(Typed<?> p_16254_, Schema p_16255_) {
         super(p_16254_, p_16255_);
      }

      protected boolean skippable() {
         this.leaveIds = new IntOpenHashSet();
         this.logIds = new IntOpenHashSet();
         this.stateToIdMap = new Int2IntOpenHashMap();

         for(int i = 0; i < this.palette.size(); ++i) {
            Dynamic<?> dynamic = this.palette.get(i);
            String s = dynamic.get("Name").asString("");
            if (LeavesFix.LEAVES.containsKey(s)) {
               boolean flag = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");
               this.leaveIds.add(i);
               this.stateToIdMap.put(this.getStateId(s, flag, 7), i);
               this.palette.set(i, this.makeLeafTag(dynamic, s, flag, 7));
            }

            if (LeavesFix.LOGS.contains(s)) {
               this.logIds.add(i);
            }
         }

         return this.leaveIds.isEmpty() && this.logIds.isEmpty();
      }

      private Dynamic<?> makeLeafTag(Dynamic<?> p_16272_, String p_16273_, boolean p_16274_, int p_16275_) {
         Dynamic<?> dynamic = p_16272_.emptyMap();
         dynamic = dynamic.set("persistent", dynamic.createString(p_16274_ ? "true" : "false"));
         dynamic = dynamic.set("distance", dynamic.createString(Integer.toString(p_16275_)));
         Dynamic<?> dynamic1 = p_16272_.emptyMap();
         dynamic1 = dynamic1.set("Properties", dynamic);
         return dynamic1.set("Name", dynamic1.createString(p_16273_));
      }

      public boolean isLog(int p_16258_) {
         return this.logIds.contains(p_16258_);
      }

      public boolean isLeaf(int p_16277_) {
         return this.leaveIds.contains(p_16277_);
      }

      int getDistance(int p_16279_) {
         return this.isLog(p_16279_) ? 0 : Integer.parseInt(this.palette.get(p_16279_).get("Properties").get("distance").asString(""));
      }

      void setDistance(int p_16260_, int p_16261_, int p_16262_) {
         Dynamic<?> dynamic = this.palette.get(p_16261_);
         String s = dynamic.get("Name").asString("");
         boolean flag = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true");
         int i = this.getStateId(s, flag, p_16262_);
         if (!this.stateToIdMap.containsKey(i)) {
            int j = this.palette.size();
            this.leaveIds.add(j);
            this.stateToIdMap.put(i, j);
            this.palette.add(this.makeLeafTag(dynamic, s, flag, p_16262_));
         }

         int l = this.stateToIdMap.get(i);
         if (1 << this.storage.getBits() <= l) {
            PackedBitStorage packedbitstorage = new PackedBitStorage(this.storage.getBits() + 1, 4096);

            for(int k = 0; k < 4096; ++k) {
               packedbitstorage.set(k, this.storage.get(k));
            }

            this.storage = packedbitstorage;
         }

         this.storage.set(p_16260_, l);
      }
   }

   public abstract static class Section {
      protected static final String BLOCK_STATES_TAG = "BlockStates";
      protected static final String NAME_TAG = "Name";
      protected static final String PROPERTIES_TAG = "Properties";
      private final Type<Pair<String, Dynamic<?>>> blockStateType = DSL.named(References.BLOCK_STATE.typeName(), DSL.remainderType());
      protected final OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder = DSL.fieldFinder("Palette", DSL.list(this.blockStateType));
      protected final List<Dynamic<?>> palette;
      protected final int index;
      @Nullable
      protected PackedBitStorage storage;

      public Section(Typed<?> p_16286_, Schema p_16287_) {
         if (!Objects.equals(p_16287_.getType(References.BLOCK_STATE), this.blockStateType)) {
            throw new IllegalStateException("Block state type is not what was expected.");
         } else {
            Optional<List<Pair<String, Dynamic<?>>>> optional = p_16286_.getOptional(this.paletteFinder);
            this.palette = optional.<List<Dynamic<?>>>map((p_16297_) -> {
               return p_16297_.stream().map(Pair::getSecond).collect(Collectors.toList());
            }).orElse(ImmutableList.of());
            Dynamic<?> dynamic = p_16286_.get(DSL.remainderFinder());
            this.index = dynamic.get("Y").asInt(0);
            this.readStorage(dynamic);
         }
      }

      protected void readStorage(Dynamic<?> p_16291_) {
         if (this.skippable()) {
            this.storage = null;
         } else {
            long[] along = p_16291_.get("BlockStates").asLongStream().toArray();
            int i = Math.max(4, DataFixUtils.ceillog2(this.palette.size()));
            this.storage = new PackedBitStorage(i, 4096, along);
         }

      }

      public Typed<?> write(Typed<?> p_16289_) {
         return this.isSkippable() ? p_16289_ : p_16289_.update(DSL.remainderFinder(), (p_16305_) -> {
            return p_16305_.set("BlockStates", p_16305_.createLongList(Arrays.stream(this.storage.getRaw())));
         }).set(this.paletteFinder, this.palette.stream().<Pair<String, Dynamic<?>>>map((p_16300_) -> {
            return Pair.of(References.BLOCK_STATE.typeName(), p_16300_);
         }).collect(Collectors.toList()));
      }

      public boolean isSkippable() {
         return this.storage == null;
      }

      public int getBlock(int p_16303_) {
         return this.storage.get(p_16303_);
      }

      protected int getStateId(String p_16293_, boolean p_16294_, int p_16295_) {
         return LeavesFix.LEAVES.get(p_16293_) << 5 | (p_16294_ ? 16 : 0) | p_16295_;
      }

      int getIndex() {
         return this.index;
      }

      protected abstract boolean skippable();
   }
}