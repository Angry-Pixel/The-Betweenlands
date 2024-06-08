package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
   ResourceLocation getId();

   void addPlayerListener(PlayerAdvancements p_13674_, CriterionTrigger.Listener<T> p_13675_);

   void removePlayerListener(PlayerAdvancements p_13676_, CriterionTrigger.Listener<T> p_13677_);

   void removePlayerListeners(PlayerAdvancements p_13673_);

   T createInstance(JsonObject p_13671_, DeserializationContext p_13672_);

   public static class Listener<T extends CriterionTriggerInstance> {
      private final T trigger;
      private final Advancement advancement;
      private final String criterion;

      public Listener(T p_13682_, Advancement p_13683_, String p_13684_) {
         this.trigger = p_13682_;
         this.advancement = p_13683_;
         this.criterion = p_13684_;
      }

      public T getTriggerInstance() {
         return this.trigger;
      }

      public void run(PlayerAdvancements p_13687_) {
         p_13687_.award(this.advancement, this.criterion);
      }

      public boolean equals(Object p_13689_) {
         if (this == p_13689_) {
            return true;
         } else if (p_13689_ != null && this.getClass() == p_13689_.getClass()) {
            CriterionTrigger.Listener<?> listener = (CriterionTrigger.Listener)p_13689_;
            if (!this.trigger.equals(listener.trigger)) {
               return false;
            } else {
               return !this.advancement.equals(listener.advancement) ? false : this.criterion.equals(listener.criterion);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int i = this.trigger.hashCode();
         i = 31 * i + this.advancement.hashCode();
         return 31 * i + this.criterion.hashCode();
      }
   }
}