package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Mth;

public class ItemCooldowns {
   private final Map<Item, ItemCooldowns.CooldownInstance> cooldowns = Maps.newHashMap();
   private int tickCount;

   public boolean isOnCooldown(Item p_41520_) {
      return this.getCooldownPercent(p_41520_, 0.0F) > 0.0F;
   }

   public float getCooldownPercent(Item p_41522_, float p_41523_) {
      ItemCooldowns.CooldownInstance itemcooldowns$cooldowninstance = this.cooldowns.get(p_41522_);
      if (itemcooldowns$cooldowninstance != null) {
         float f = (float)(itemcooldowns$cooldowninstance.endTime - itemcooldowns$cooldowninstance.startTime);
         float f1 = (float)itemcooldowns$cooldowninstance.endTime - ((float)this.tickCount + p_41523_);
         return Mth.clamp(f1 / f, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      ++this.tickCount;
      if (!this.cooldowns.isEmpty()) {
         Iterator<Entry<Item, ItemCooldowns.CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<Item, ItemCooldowns.CooldownInstance> entry = iterator.next();
            if ((entry.getValue()).endTime <= this.tickCount) {
               iterator.remove();
               this.onCooldownEnded(entry.getKey());
            }
         }
      }

   }

   public void addCooldown(Item p_41525_, int p_41526_) {
      this.cooldowns.put(p_41525_, new ItemCooldowns.CooldownInstance(this.tickCount, this.tickCount + p_41526_));
      this.onCooldownStarted(p_41525_, p_41526_);
   }

   public void removeCooldown(Item p_41528_) {
      this.cooldowns.remove(p_41528_);
      this.onCooldownEnded(p_41528_);
   }

   protected void onCooldownStarted(Item p_41529_, int p_41530_) {
   }

   protected void onCooldownEnded(Item p_41531_) {
   }

   static class CooldownInstance {
      final int startTime;
      final int endTime;

      CooldownInstance(int p_186358_, int p_186359_) {
         this.startTime = p_186358_;
         this.endTime = p_186359_;
      }
   }
}