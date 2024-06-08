package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger<UsedEnderEyeTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedEnderEyeTrigger.TriggerInstance createInstance(JsonObject p_73939_, EntityPredicate.Composite p_73940_, DeserializationContext p_73941_) {
      MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromJson(p_73939_.get("distance"));
      return new UsedEnderEyeTrigger.TriggerInstance(p_73940_, minmaxbounds$doubles);
   }

   public void trigger(ServerPlayer p_73936_, BlockPos p_73937_) {
      double d0 = p_73936_.getX() - (double)p_73937_.getX();
      double d1 = p_73936_.getZ() - (double)p_73937_.getZ();
      double d2 = d0 * d0 + d1 * d1;
      this.trigger(p_73936_, (p_73934_) -> {
         return p_73934_.matches(d2);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Doubles level;

      public TriggerInstance(EntityPredicate.Composite p_73949_, MinMaxBounds.Doubles p_73950_) {
         super(UsedEnderEyeTrigger.ID, p_73949_);
         this.level = p_73950_;
      }

      public boolean matches(double p_73952_) {
         return this.level.matchesSqr(p_73952_);
      }
   }
}