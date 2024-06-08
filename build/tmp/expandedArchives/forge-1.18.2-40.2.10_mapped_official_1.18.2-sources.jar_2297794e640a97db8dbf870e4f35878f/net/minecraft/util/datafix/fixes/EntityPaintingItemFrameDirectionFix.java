package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class EntityPaintingItemFrameDirectionFix extends DataFix {
   private static final int[][] DIRECTIONS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

   public EntityPaintingItemFrameDirectionFix(Schema p_15499_, boolean p_15500_) {
      super(p_15499_, p_15500_);
   }

   private Dynamic<?> doFix(Dynamic<?> p_15510_, boolean p_15511_, boolean p_15512_) {
      if ((p_15511_ || p_15512_) && !p_15510_.get("Facing").asNumber().result().isPresent()) {
         int i;
         if (p_15510_.get("Direction").asNumber().result().isPresent()) {
            i = p_15510_.get("Direction").asByte((byte)0) % DIRECTIONS.length;
            int[] aint = DIRECTIONS[i];
            p_15510_ = p_15510_.set("TileX", p_15510_.createInt(p_15510_.get("TileX").asInt(0) + aint[0]));
            p_15510_ = p_15510_.set("TileY", p_15510_.createInt(p_15510_.get("TileY").asInt(0) + aint[1]));
            p_15510_ = p_15510_.set("TileZ", p_15510_.createInt(p_15510_.get("TileZ").asInt(0) + aint[2]));
            p_15510_ = p_15510_.remove("Direction");
            if (p_15512_ && p_15510_.get("ItemRotation").asNumber().result().isPresent()) {
               p_15510_ = p_15510_.set("ItemRotation", p_15510_.createByte((byte)(p_15510_.get("ItemRotation").asByte((byte)0) * 2)));
            }
         } else {
            i = p_15510_.get("Dir").asByte((byte)0) % DIRECTIONS.length;
            p_15510_ = p_15510_.remove("Dir");
         }

         p_15510_ = p_15510_.set("Facing", p_15510_.createByte((byte)i));
      }

      return p_15510_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getChoiceType(References.ENTITY, "Painting");
      OpticFinder<?> opticfinder = DSL.namedChoice("Painting", type);
      Type<?> type1 = this.getInputSchema().getChoiceType(References.ENTITY, "ItemFrame");
      OpticFinder<?> opticfinder1 = DSL.namedChoice("ItemFrame", type1);
      Type<?> type2 = this.getInputSchema().getType(References.ENTITY);
      TypeRewriteRule typerewriterule = this.fixTypeEverywhereTyped("EntityPaintingFix", type2, (p_15516_) -> {
         return p_15516_.updateTyped(opticfinder, type, (p_145300_) -> {
            return p_145300_.update(DSL.remainderFinder(), (p_145302_) -> {
               return this.doFix(p_145302_, true, false);
            });
         });
      });
      TypeRewriteRule typerewriterule1 = this.fixTypeEverywhereTyped("EntityItemFrameFix", type2, (p_15504_) -> {
         return p_15504_.updateTyped(opticfinder1, type1, (p_145296_) -> {
            return p_145296_.update(DSL.remainderFinder(), (p_145298_) -> {
               return this.doFix(p_145298_, false, true);
            });
         });
      });
      return TypeRewriteRule.seq(typerewriterule, typerewriterule1);
   }
}