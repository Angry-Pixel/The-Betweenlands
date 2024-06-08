package net.minecraft.world.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Foods {
   public static final FoodProperties APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
   public static final FoodProperties BAKED_POTATO = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).build();
   public static final FoodProperties BEEF = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
   public static final FoodProperties BEETROOT = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.6F).build();
   public static final FoodProperties BEETROOT_SOUP = stew(6).build();
   public static final FoodProperties BREAD = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).build();
   public static final FoodProperties CARROT = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.6F).build();
   public static final FoodProperties CHICKEN = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F).meat().build();
   public static final FoodProperties CHORUS_FRUIT = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).alwaysEat().build();
   public static final FoodProperties COD = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final FoodProperties COOKED_BEEF = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).meat().build();
   public static final FoodProperties COOKED_CHICKEN = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.6F).meat().build();
   public static final FoodProperties COOKED_COD = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).build();
   public static final FoodProperties COOKED_MUTTON = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.8F).meat().build();
   public static final FoodProperties COOKED_PORKCHOP = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).meat().build();
   public static final FoodProperties COOKED_RABBIT = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).meat().build();
   public static final FoodProperties COOKED_SALMON = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.8F).build();
   public static final FoodProperties COOKIE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final FoodProperties DRIED_KELP = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.3F).fast().build();
   public static final FoodProperties ENCHANTED_GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(1.2F).effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F).effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F).effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0F).alwaysEat().build();
   public static final FoodProperties GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(1.2F).effect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1.0F).alwaysEat().build();
   public static final FoodProperties GOLDEN_CARROT = (new FoodProperties.Builder()).nutrition(6).saturationMod(1.2F).build();
   public static final FoodProperties HONEY_BOTTLE = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.1F).build();
   public static final FoodProperties MELON_SLICE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).build();
   public static final FoodProperties MUSHROOM_STEW = stew(6).build();
   public static final FoodProperties MUTTON = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).meat().build();
   public static final FoodProperties POISONOUS_POTATO = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 0.6F).build();
   public static final FoodProperties PORKCHOP = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
   public static final FoodProperties POTATO = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.3F).build();
   public static final FoodProperties PUFFERFISH = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).effect(new MobEffectInstance(MobEffects.POISON, 1200, 1), 1.0F).effect(new MobEffectInstance(MobEffects.HUNGER, 300, 2), 1.0F).effect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 1.0F).build();
   public static final FoodProperties PUMPKIN_PIE = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.3F).build();
   public static final FoodProperties RABBIT = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
   public static final FoodProperties RABBIT_STEW = stew(10).build();
   public static final FoodProperties ROTTEN_FLESH = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.1F).effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8F).meat().build();
   public static final FoodProperties SALMON = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final FoodProperties SPIDER_EYE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.8F).effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 1.0F).build();
   public static final FoodProperties SUSPICIOUS_STEW = stew(6).alwaysEat().build();
   public static final FoodProperties SWEET_BERRIES = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final FoodProperties GLOW_BERRIES = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final FoodProperties TROPICAL_FISH = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).build();

   private static FoodProperties.Builder stew(int p_150384_) {
      return (new FoodProperties.Builder()).nutrition(p_150384_).saturationMod(0.6F);
   }
}