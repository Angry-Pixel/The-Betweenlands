package net.minecraft.world.item.alchemy;

import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Potions {
   public static final Potion EMPTY = register("empty", new Potion());
   public static final Potion WATER = register("water", new Potion());
   public static final Potion MUNDANE = register("mundane", new Potion());
   public static final Potion THICK = register("thick", new Potion());
   public static final Potion AWKWARD = register("awkward", new Potion());
   public static final Potion NIGHT_VISION = register("night_vision", new Potion(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600)));
   public static final Potion LONG_NIGHT_VISION = register("long_night_vision", new Potion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 9600)));
   public static final Potion INVISIBILITY = register("invisibility", new Potion(new MobEffectInstance(MobEffects.INVISIBILITY, 3600)));
   public static final Potion LONG_INVISIBILITY = register("long_invisibility", new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 9600)));
   public static final Potion LEAPING = register("leaping", new Potion(new MobEffectInstance(MobEffects.JUMP, 3600)));
   public static final Potion LONG_LEAPING = register("long_leaping", new Potion("leaping", new MobEffectInstance(MobEffects.JUMP, 9600)));
   public static final Potion STRONG_LEAPING = register("strong_leaping", new Potion("leaping", new MobEffectInstance(MobEffects.JUMP, 1800, 1)));
   public static final Potion FIRE_RESISTANCE = register("fire_resistance", new Potion(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600)));
   public static final Potion LONG_FIRE_RESISTANCE = register("long_fire_resistance", new Potion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600)));
   public static final Potion SWIFTNESS = register("swiftness", new Potion(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600)));
   public static final Potion LONG_SWIFTNESS = register("long_swiftness", new Potion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 9600)));
   public static final Potion STRONG_SWIFTNESS = register("strong_swiftness", new Potion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1800, 1)));
   public static final Potion SLOWNESS = register("slowness", new Potion(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800)));
   public static final Potion LONG_SLOWNESS = register("long_slowness", new Potion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 4800)));
   public static final Potion STRONG_SLOWNESS = register("strong_slowness", new Potion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 3)));
   public static final Potion TURTLE_MASTER = register("turtle_master", new Potion("turtle_master", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 3), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 2)));
   public static final Potion LONG_TURTLE_MASTER = register("long_turtle_master", new Potion("turtle_master", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 800, 3), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 800, 2)));
   public static final Potion STRONG_TURTLE_MASTER = register("strong_turtle_master", new Potion("turtle_master", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 5), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 3)));
   public static final Potion WATER_BREATHING = register("water_breathing", new Potion(new MobEffectInstance(MobEffects.WATER_BREATHING, 3600)));
   public static final Potion LONG_WATER_BREATHING = register("long_water_breathing", new Potion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 9600)));
   public static final Potion HEALING = register("healing", new Potion(new MobEffectInstance(MobEffects.HEAL, 1)));
   public static final Potion STRONG_HEALING = register("strong_healing", new Potion("healing", new MobEffectInstance(MobEffects.HEAL, 1, 1)));
   public static final Potion HARMING = register("harming", new Potion(new MobEffectInstance(MobEffects.HARM, 1)));
   public static final Potion STRONG_HARMING = register("strong_harming", new Potion("harming", new MobEffectInstance(MobEffects.HARM, 1, 1)));
   public static final Potion POISON = register("poison", new Potion(new MobEffectInstance(MobEffects.POISON, 900)));
   public static final Potion LONG_POISON = register("long_poison", new Potion("poison", new MobEffectInstance(MobEffects.POISON, 1800)));
   public static final Potion STRONG_POISON = register("strong_poison", new Potion("poison", new MobEffectInstance(MobEffects.POISON, 432, 1)));
   public static final Potion REGENERATION = register("regeneration", new Potion(new MobEffectInstance(MobEffects.REGENERATION, 900)));
   public static final Potion LONG_REGENERATION = register("long_regeneration", new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 1800)));
   public static final Potion STRONG_REGENERATION = register("strong_regeneration", new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 450, 1)));
   public static final Potion STRENGTH = register("strength", new Potion(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600)));
   public static final Potion LONG_STRENGTH = register("long_strength", new Potion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 9600)));
   public static final Potion STRONG_STRENGTH = register("strong_strength", new Potion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 1)));
   public static final Potion WEAKNESS = register("weakness", new Potion(new MobEffectInstance(MobEffects.WEAKNESS, 1800)));
   public static final Potion LONG_WEAKNESS = register("long_weakness", new Potion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 4800)));
   public static final Potion LUCK = register("luck", new Potion("luck", new MobEffectInstance(MobEffects.LUCK, 6000)));
   public static final Potion SLOW_FALLING = register("slow_falling", new Potion(new MobEffectInstance(MobEffects.SLOW_FALLING, 1800)));
   public static final Potion LONG_SLOW_FALLING = register("long_slow_falling", new Potion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 4800)));

   private static Potion register(String p_43626_, Potion p_43627_) {
      return Registry.register(Registry.POTION, p_43626_, p_43627_);
   }
}