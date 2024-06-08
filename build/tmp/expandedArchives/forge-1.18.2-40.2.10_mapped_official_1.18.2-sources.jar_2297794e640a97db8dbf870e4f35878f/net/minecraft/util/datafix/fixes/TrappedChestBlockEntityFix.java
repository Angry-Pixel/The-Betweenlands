package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class TrappedChestBlockEntityFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SIZE = 4096;
   private static final short SIZE_BITS = 12;

   public TrappedChestBlockEntityFix(Schema p_17018_, boolean p_17019_) {
      super(p_17018_, p_17019_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(References.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      Type<?> type2 = type1.findFieldType("TileEntities");
      if (!(type2 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType<?> listtype = (ListType)type2;
         OpticFinder<? extends List<?>> opticfinder = DSL.fieldFinder("TileEntities", listtype);
         Type<?> type3 = this.getInputSchema().getType(References.CHUNK);
         OpticFinder<?> opticfinder1 = type3.findField("Level");
         OpticFinder<?> opticfinder2 = opticfinder1.type().findField("Sections");
         Type<?> type4 = opticfinder2.type();
         if (!(type4 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type<?> type5 = ((ListType)type4).getElement();
            OpticFinder<?> opticfinder3 = DSL.typeFinder(type5);
            return TypeRewriteRule.seq((new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", References.BLOCK_ENTITY)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", type3, (p_17031_) -> {
               return p_17031_.updateTyped(opticfinder1, (p_145746_) -> {
                  Optional<? extends Typed<?>> optional = p_145746_.getOptionalTyped(opticfinder2);
                  if (!optional.isPresent()) {
                     return p_145746_;
                  } else {
                     List<? extends Typed<?>> list = optional.get().getAllTyped(opticfinder3);
                     IntSet intset = new IntOpenHashSet();

                     for(Typed<?> typed : list) {
                        TrappedChestBlockEntityFix.TrappedChestSection trappedchestblockentityfix$trappedchestsection = new TrappedChestBlockEntityFix.TrappedChestSection(typed, this.getInputSchema());
                        if (!trappedchestblockentityfix$trappedchestsection.isSkippable()) {
                           for(int i = 0; i < 4096; ++i) {
                              int j = trappedchestblockentityfix$trappedchestsection.getBlock(i);
                              if (trappedchestblockentityfix$trappedchestsection.isTrappedChest(j)) {
                                 intset.add(trappedchestblockentityfix$trappedchestsection.getIndex() << 12 | i);
                              }
                           }
                        }
                     }

                     Dynamic<?> dynamic = p_145746_.get(DSL.remainderFinder());
                     int k = dynamic.get("xPos").asInt(0);
                     int l = dynamic.get("zPos").asInt(0);
                     TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
                     return p_145746_.updateTyped(opticfinder, (p_145752_) -> {
                        return p_145752_.updateTyped(taggedchoicetype.finder(), (p_145741_) -> {
                           Dynamic<?> dynamic1 = p_145741_.getOrCreate(DSL.remainderFinder());
                           int i1 = dynamic1.get("x").asInt(0) - (k << 4);
                           int j1 = dynamic1.get("y").asInt(0);
                           int k1 = dynamic1.get("z").asInt(0) - (l << 4);
                           return intset.contains(LeavesFix.getIndex(i1, j1, k1)) ? p_145741_.update(taggedchoicetype.finder(), (p_145754_) -> {
                              return p_145754_.mapFirst((p_145756_) -> {
                                 if (!Objects.equals(p_145756_, "minecraft:chest")) {
                                    LOGGER.warn("Block Entity was expected to be a chest");
                                 }

                                 return "minecraft:trapped_chest";
                              });
                           }) : p_145741_;
                        });
                     });
                  }
               });
            }));
         }
      }
   }

   public static final class TrappedChestSection extends LeavesFix.Section {
      @Nullable
      private IntSet chestIds;

      public TrappedChestSection(Typed<?> p_17050_, Schema p_17051_) {
         super(p_17050_, p_17051_);
      }

      protected boolean skippable() {
         this.chestIds = new IntOpenHashSet();

         for(int i = 0; i < this.palette.size(); ++i) {
            Dynamic<?> dynamic = this.palette.get(i);
            String s = dynamic.get("Name").asString("");
            if (Objects.equals(s, "minecraft:trapped_chest")) {
               this.chestIds.add(i);
            }
         }

         return this.chestIds.isEmpty();
      }

      public boolean isTrappedChest(int p_17054_) {
         return this.chestIds.contains(p_17054_);
      }
   }
}