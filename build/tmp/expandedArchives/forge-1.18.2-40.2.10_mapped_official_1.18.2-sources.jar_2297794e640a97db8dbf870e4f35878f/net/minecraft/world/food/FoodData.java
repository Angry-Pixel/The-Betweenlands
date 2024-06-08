package net.minecraft.world.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public class FoodData {
   private int foodLevel = 20;
   private float saturationLevel;
   private float exhaustionLevel;
   private int tickTimer;
   private int lastFoodLevel = 20;

   public FoodData() {
      this.saturationLevel = 5.0F;
   }

   public void eat(int p_38708_, float p_38709_) {
      this.foodLevel = Math.min(p_38708_ + this.foodLevel, 20);
      this.saturationLevel = Math.min(this.saturationLevel + (float)p_38708_ * p_38709_ * 2.0F, (float)this.foodLevel);
   }

   // Use the LivingEntity sensitive version in favour of this.
   @Deprecated
   public void eat(Item p_38713_, ItemStack p_38714_) {
      this.eat(p_38713_, p_38714_, null);
   }

   public void eat(Item p_38713_, ItemStack p_38714_, @javax.annotation.Nullable net.minecraft.world.entity.LivingEntity entity) {
      if (p_38713_.isEdible()) {
         FoodProperties foodproperties = p_38714_.getFoodProperties(entity);
         this.eat(foodproperties.getNutrition(), foodproperties.getSaturationModifier());
      }

   }

   public void tick(Player p_38711_) {
      Difficulty difficulty = p_38711_.level.getDifficulty();
      this.lastFoodLevel = this.foodLevel;
      if (this.exhaustionLevel > 4.0F) {
         this.exhaustionLevel -= 4.0F;
         if (this.saturationLevel > 0.0F) {
            this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
         } else if (difficulty != Difficulty.PEACEFUL) {
            this.foodLevel = Math.max(this.foodLevel - 1, 0);
         }
      }

      boolean flag = p_38711_.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
      if (flag && this.saturationLevel > 0.0F && p_38711_.isHurt() && this.foodLevel >= 20) {
         ++this.tickTimer;
         if (this.tickTimer >= 10) {
            float f = Math.min(this.saturationLevel, 6.0F);
            p_38711_.heal(f / 6.0F);
            this.addExhaustion(f);
            this.tickTimer = 0;
         }
      } else if (flag && this.foodLevel >= 18 && p_38711_.isHurt()) {
         ++this.tickTimer;
         if (this.tickTimer >= 80) {
            p_38711_.heal(1.0F);
            this.addExhaustion(6.0F);
            this.tickTimer = 0;
         }
      } else if (this.foodLevel <= 0) {
         ++this.tickTimer;
         if (this.tickTimer >= 80) {
            if (p_38711_.getHealth() > 10.0F || difficulty == Difficulty.HARD || p_38711_.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
               p_38711_.hurt(DamageSource.STARVE, 1.0F);
            }

            this.tickTimer = 0;
         }
      } else {
         this.tickTimer = 0;
      }

   }

   public void readAdditionalSaveData(CompoundTag p_38716_) {
      if (p_38716_.contains("foodLevel", 99)) {
         this.foodLevel = p_38716_.getInt("foodLevel");
         this.tickTimer = p_38716_.getInt("foodTickTimer");
         this.saturationLevel = p_38716_.getFloat("foodSaturationLevel");
         this.exhaustionLevel = p_38716_.getFloat("foodExhaustionLevel");
      }

   }

   public void addAdditionalSaveData(CompoundTag p_38720_) {
      p_38720_.putInt("foodLevel", this.foodLevel);
      p_38720_.putInt("foodTickTimer", this.tickTimer);
      p_38720_.putFloat("foodSaturationLevel", this.saturationLevel);
      p_38720_.putFloat("foodExhaustionLevel", this.exhaustionLevel);
   }

   public int getFoodLevel() {
      return this.foodLevel;
   }

   public int getLastFoodLevel() {
      return this.lastFoodLevel;
   }

   public boolean needsFood() {
      return this.foodLevel < 20;
   }

   public void addExhaustion(float p_38704_) {
      this.exhaustionLevel = Math.min(this.exhaustionLevel + p_38704_, 40.0F);
   }

   public float getExhaustionLevel() {
      return this.exhaustionLevel;
   }

   public float getSaturationLevel() {
      return this.saturationLevel;
   }

   public void setFoodLevel(int p_38706_) {
      this.foodLevel = p_38706_;
   }

   public void setSaturation(float p_38718_) {
      this.saturationLevel = p_38718_;
   }

   public void setExhaustion(float p_150379_) {
      this.exhaustionLevel = p_150379_;
   }
}
