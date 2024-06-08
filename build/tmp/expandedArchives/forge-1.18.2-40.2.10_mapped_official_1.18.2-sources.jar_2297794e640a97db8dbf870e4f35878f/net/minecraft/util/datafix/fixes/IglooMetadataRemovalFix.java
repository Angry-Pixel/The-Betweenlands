package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class IglooMetadataRemovalFix extends DataFix {
   public IglooMetadataRemovalFix(Schema p_15902_, boolean p_15903_) {
      super(p_15902_, p_15903_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
      Type<?> type1 = this.getOutputSchema().getType(References.STRUCTURE_FEATURE);
      return this.writeFixAndRead("IglooMetadataRemovalFix", type, type1, IglooMetadataRemovalFix::fixTag);
   }

   private static <T> Dynamic<T> fixTag(Dynamic<T> p_15905_) {
      boolean flag = p_15905_.get("Children").asStreamOpt().map((p_15911_) -> {
         return p_15911_.allMatch(IglooMetadataRemovalFix::isIglooPiece);
      }).result().orElse(false);
      return flag ? p_15905_.set("id", p_15905_.createString("Igloo")).remove("Children") : p_15905_.update("Children", IglooMetadataRemovalFix::removeIglooPieces);
   }

   private static <T> Dynamic<T> removeIglooPieces(Dynamic<T> p_15909_) {
      return p_15909_.asStreamOpt().map((p_15907_) -> {
         return p_15907_.filter((p_145382_) -> {
            return !isIglooPiece(p_145382_);
         });
      }).map(p_15909_::createList).result().orElse(p_15909_);
   }

   private static boolean isIglooPiece(Dynamic<?> p_15913_) {
      return p_15913_.get("id").asString("").equals("Iglu");
   }
}