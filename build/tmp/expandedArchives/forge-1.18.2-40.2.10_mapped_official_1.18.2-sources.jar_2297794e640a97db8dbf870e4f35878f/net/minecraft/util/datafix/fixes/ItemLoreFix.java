package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ItemLoreFix extends DataFix {
   public ItemLoreFix(Schema p_15958_, boolean p_15959_) {
      super(p_15958_, p_15959_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> opticfinder = type.findField("tag");
      return this.fixTypeEverywhereTyped("Item Lore componentize", type, (p_15962_) -> {
         return p_15962_.updateTyped(opticfinder, (p_145392_) -> {
            return p_145392_.update(DSL.remainderFinder(), (p_145394_) -> {
               return p_145394_.update("display", (p_145396_) -> {
                  return p_145396_.update("Lore", (p_145398_) -> {
                     return DataFixUtils.orElse(p_145398_.asStreamOpt().map(ItemLoreFix::fixLoreList).map(p_145398_::createList).result(), p_145398_);
                  });
               });
            });
         });
      });
   }

   private static <T> Stream<Dynamic<T>> fixLoreList(Stream<Dynamic<T>> p_15970_) {
      return p_15970_.map((p_15966_) -> {
         return DataFixUtils.orElse(p_15966_.asString().map(ItemLoreFix::fixLoreEntry).map(p_15966_::createString).result(), p_15966_);
      });
   }

   private static String fixLoreEntry(String p_15968_) {
      return Component.Serializer.toJson(new TextComponent(p_15968_));
   }
}