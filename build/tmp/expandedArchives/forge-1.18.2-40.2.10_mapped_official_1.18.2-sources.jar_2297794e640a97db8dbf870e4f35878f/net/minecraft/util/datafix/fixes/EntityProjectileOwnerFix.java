package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.function.Function;

public class EntityProjectileOwnerFix extends DataFix {
   public EntityProjectileOwnerFix(Schema p_15558_) {
      super(p_15558_, false);
   }

   protected TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      return this.fixTypeEverywhereTyped("EntityProjectileOwner", schema.getType(References.ENTITY), this::updateProjectiles);
   }

   private Typed<?> updateProjectiles(Typed<?> p_15563_) {
      p_15563_ = this.updateEntity(p_15563_, "minecraft:egg", this::updateOwnerThrowable);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:ender_pearl", this::updateOwnerThrowable);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:experience_bottle", this::updateOwnerThrowable);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:snowball", this::updateOwnerThrowable);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:potion", this::updateOwnerThrowable);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:potion", this::updateItemPotion);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:llama_spit", this::updateOwnerLlamaSpit);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:arrow", this::updateOwnerArrow);
      p_15563_ = this.updateEntity(p_15563_, "minecraft:spectral_arrow", this::updateOwnerArrow);
      return this.updateEntity(p_15563_, "minecraft:trident", this::updateOwnerArrow);
   }

   private Dynamic<?> updateOwnerArrow(Dynamic<?> p_15569_) {
      long i = p_15569_.get("OwnerUUIDMost").asLong(0L);
      long j = p_15569_.get("OwnerUUIDLeast").asLong(0L);
      return this.setUUID(p_15569_, i, j).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
   }

   private Dynamic<?> updateOwnerLlamaSpit(Dynamic<?> p_15578_) {
      OptionalDynamic<?> optionaldynamic = p_15578_.get("Owner");
      long i = optionaldynamic.get("OwnerUUIDMost").asLong(0L);
      long j = optionaldynamic.get("OwnerUUIDLeast").asLong(0L);
      return this.setUUID(p_15578_, i, j).remove("Owner");
   }

   private Dynamic<?> updateItemPotion(Dynamic<?> p_15580_) {
      OptionalDynamic<?> optionaldynamic = p_15580_.get("Potion");
      return p_15580_.set("Item", optionaldynamic.orElseEmptyMap()).remove("Potion");
   }

   private Dynamic<?> updateOwnerThrowable(Dynamic<?> p_15582_) {
      String s = "owner";
      OptionalDynamic<?> optionaldynamic = p_15582_.get("owner");
      long i = optionaldynamic.get("M").asLong(0L);
      long j = optionaldynamic.get("L").asLong(0L);
      return this.setUUID(p_15582_, i, j).remove("owner");
   }

   private Dynamic<?> setUUID(Dynamic<?> p_15571_, long p_15572_, long p_15573_) {
      String s = "OwnerUUID";
      return p_15572_ != 0L && p_15573_ != 0L ? p_15571_.set("OwnerUUID", p_15571_.createIntList(Arrays.stream(createUUIDArray(p_15572_, p_15573_)))) : p_15571_;
   }

   private static int[] createUUIDArray(long p_15560_, long p_15561_) {
      return new int[]{(int)(p_15560_ >> 32), (int)p_15560_, (int)(p_15561_ >> 32), (int)p_15561_};
   }

   private Typed<?> updateEntity(Typed<?> p_15565_, String p_15566_, Function<Dynamic<?>, Dynamic<?>> p_15567_) {
      Type<?> type = this.getInputSchema().getChoiceType(References.ENTITY, p_15566_);
      Type<?> type1 = this.getOutputSchema().getChoiceType(References.ENTITY, p_15566_);
      return p_15565_.updateTyped(DSL.namedChoice(p_15566_, type), type1, (p_15576_) -> {
         return p_15576_.update(DSL.remainderFinder(), p_15567_);
      });
   }
}