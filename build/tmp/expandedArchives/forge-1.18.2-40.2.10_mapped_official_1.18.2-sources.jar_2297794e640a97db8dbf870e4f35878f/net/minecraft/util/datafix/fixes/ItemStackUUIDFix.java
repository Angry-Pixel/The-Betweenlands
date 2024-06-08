package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackUUIDFix extends AbstractUUIDFix {
   public ItemStackUUIDFix(Schema p_16129_) {
      super(p_16129_, References.ITEM_STACK);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      return this.fixTypeEverywhereTyped("ItemStackUUIDFix", this.getInputSchema().getType(this.typeReference), (p_16132_) -> {
         OpticFinder<?> opticfinder1 = p_16132_.getType().findField("tag");
         return p_16132_.updateTyped(opticfinder1, (p_145429_) -> {
            return p_145429_.update(DSL.remainderFinder(), (p_145433_) -> {
               p_145433_ = this.updateAttributeModifiers(p_145433_);
               if (p_16132_.getOptional(opticfinder).map((p_145435_) -> {
                  return "minecraft:player_head".equals(p_145435_.getSecond());
               }).orElse(false)) {
                  p_145433_ = this.updateSkullOwner(p_145433_);
               }

               return p_145433_;
            });
         });
      });
   }

   private Dynamic<?> updateAttributeModifiers(Dynamic<?> p_16147_) {
      return p_16147_.update("AttributeModifiers", (p_16145_) -> {
         return p_16147_.createList(p_16145_.asStream().map((p_145437_) -> {
            return replaceUUIDLeastMost(p_145437_, "UUID", "UUID").orElse(p_145437_);
         }));
      });
   }

   private Dynamic<?> updateSkullOwner(Dynamic<?> p_16149_) {
      return p_16149_.update("SkullOwner", (p_16151_) -> {
         return replaceUUIDString(p_16151_, "Id", "Id").orElse(p_16151_);
      });
   }
}