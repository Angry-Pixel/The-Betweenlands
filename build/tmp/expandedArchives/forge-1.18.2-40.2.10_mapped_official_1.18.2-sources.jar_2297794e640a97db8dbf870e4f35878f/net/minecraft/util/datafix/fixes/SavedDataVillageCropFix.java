package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;

public class SavedDataVillageCropFix extends DataFix {
   public SavedDataVillageCropFix(Schema p_16882_, boolean p_16883_) {
      super(p_16882_, p_16883_);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE), this::fixTag);
   }

   private <T> Dynamic<T> fixTag(Dynamic<T> p_16885_) {
      return p_16885_.update("Children", SavedDataVillageCropFix::updateChildren);
   }

   private static <T> Dynamic<T> updateChildren(Dynamic<T> p_16892_) {
      return p_16892_.asStreamOpt().map(SavedDataVillageCropFix::updateChildren).map(p_16892_::createList).result().orElse(p_16892_);
   }

   private static Stream<? extends Dynamic<?>> updateChildren(Stream<? extends Dynamic<?>> p_16890_) {
      return p_16890_.map((Dynamic<?> p_16898_) -> {
         String s = p_16898_.get("id").asString("");
         if ("ViF".equals(s)) {
            return updateSingleField(p_16898_);
         } else {
            return "ViDF".equals(s) ? updateDoubleField(p_16898_) : p_16898_;
         }
      });
   }

   private static <T> Dynamic<T> updateSingleField(Dynamic<T> p_16894_) {
      p_16894_ = updateCrop(p_16894_, "CA");
      return updateCrop(p_16894_, "CB");
   }

   private static <T> Dynamic<T> updateDoubleField(Dynamic<T> p_16896_) {
      p_16896_ = updateCrop(p_16896_, "CA");
      p_16896_ = updateCrop(p_16896_, "CB");
      p_16896_ = updateCrop(p_16896_, "CC");
      return updateCrop(p_16896_, "CD");
   }

   private static <T> Dynamic<T> updateCrop(Dynamic<T> p_16887_, String p_16888_) {
      return p_16887_.get(p_16888_).asNumber().result().isPresent() ? p_16887_.set(p_16888_, BlockStateData.getTag(p_16887_.get(p_16888_).asInt(0) << 4)) : p_16887_;
   }
}