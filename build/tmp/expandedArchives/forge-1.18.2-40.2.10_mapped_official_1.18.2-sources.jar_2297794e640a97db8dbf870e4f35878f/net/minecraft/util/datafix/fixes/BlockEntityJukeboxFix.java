package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class BlockEntityJukeboxFix extends NamedEntityFix {
   public BlockEntityJukeboxFix(Schema p_14842_, boolean p_14843_) {
      super(p_14842_, p_14843_, "BlockEntityJukeboxFix", References.BLOCK_ENTITY, "minecraft:jukebox");
   }

   protected Typed<?> fix(Typed<?> p_14846_) {
      Type<?> type = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:jukebox");
      Type<?> type1 = type.findFieldType("RecordItem");
      OpticFinder<?> opticfinder = DSL.fieldFinder("RecordItem", type1);
      Dynamic<?> dynamic = p_14846_.get(DSL.remainderFinder());
      int i = dynamic.get("Record").asInt(0);
      if (i > 0) {
         dynamic.remove("Record");
         String s = ItemStackTheFlatteningFix.updateItem(ItemIdFix.getItem(i), 0);
         if (s != null) {
            Dynamic<?> dynamic1 = dynamic.emptyMap();
            dynamic1 = dynamic1.set("id", dynamic1.createString(s));
            dynamic1 = dynamic1.set("Count", dynamic1.createByte((byte)1));
            return p_14846_.set(opticfinder, type1.readTyped(dynamic1).result().orElseThrow(() -> {
               return new IllegalStateException("Could not create record item stack.");
            }).getFirst()).set(DSL.remainderFinder(), dynamic);
         }
      }

      return p_14846_;
   }
}