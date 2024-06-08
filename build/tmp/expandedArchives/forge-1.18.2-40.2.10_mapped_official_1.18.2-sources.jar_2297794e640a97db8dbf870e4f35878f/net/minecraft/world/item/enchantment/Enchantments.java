package net.minecraft.world.item.enchantment;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EquipmentSlot;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public class Enchantments {
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   public static final Enchantment ALL_DAMAGE_PROTECTION = register("protection", new ProtectionEnchantment(Enchantment.Rarity.COMMON, ProtectionEnchantment.Type.ALL, ARMOR_SLOTS));
   public static final Enchantment FIRE_PROTECTION = register("fire_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FIRE, ARMOR_SLOTS));
   public static final Enchantment FALL_PROTECTION = register("feather_falling", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FALL, ARMOR_SLOTS));
   public static final Enchantment BLAST_PROTECTION = register("blast_protection", new ProtectionEnchantment(Enchantment.Rarity.RARE, ProtectionEnchantment.Type.EXPLOSION, ARMOR_SLOTS));
   public static final Enchantment PROJECTILE_PROTECTION = register("projectile_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ARMOR_SLOTS));
   public static final Enchantment RESPIRATION = register("respiration", new OxygenEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment AQUA_AFFINITY = register("aqua_affinity", new WaterWorkerEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment THORNS = register("thorns", new ThornsEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
   public static final Enchantment DEPTH_STRIDER = register("depth_strider", new WaterWalkerEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment FROST_WALKER = register("frost_walker", new FrostWalkerEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.FEET));
   public static final Enchantment BINDING_CURSE = register("binding_curse", new BindingCurseEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
   public static final Enchantment SOUL_SPEED = register("soul_speed", new SoulSpeedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.FEET));
   public static final Enchantment SHARPNESS = register("sharpness", new DamageEnchantment(Enchantment.Rarity.COMMON, 0, EquipmentSlot.MAINHAND));
   public static final Enchantment SMITE = register("smite", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, EquipmentSlot.MAINHAND));
   public static final Enchantment BANE_OF_ARTHROPODS = register("bane_of_arthropods", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, EquipmentSlot.MAINHAND));
   public static final Enchantment KNOCKBACK = register("knockback", new KnockbackEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
   public static final Enchantment FIRE_ASPECT = register("fire_aspect", new FireAspectEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment MOB_LOOTING = register("looting", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));
   public static final Enchantment SWEEPING_EDGE = register("sweeping", new SweepingEdgeEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment BLOCK_EFFICIENCY = register("efficiency", new DiggingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
   public static final Enchantment SILK_TOUCH = register("silk_touch", new UntouchingEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment UNBREAKING = register("unbreaking", new DigDurabilityEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
   public static final Enchantment BLOCK_FORTUNE = register("fortune", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND));
   public static final Enchantment POWER_ARROWS = register("power", new ArrowDamageEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
   public static final Enchantment PUNCH_ARROWS = register("punch", new ArrowKnockbackEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment FLAMING_ARROWS = register("flame", new ArrowFireEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment INFINITY_ARROWS = register("infinity", new ArrowInfiniteEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment FISHING_LUCK = register("luck_of_the_sea", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.FISHING_ROD, EquipmentSlot.MAINHAND));
   public static final Enchantment FISHING_SPEED = register("lure", new FishingSpeedEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.FISHING_ROD, EquipmentSlot.MAINHAND));
   public static final Enchantment LOYALTY = register("loyalty", new TridentLoyaltyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
   public static final Enchantment IMPALING = register("impaling", new TridentImpalerEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment RIPTIDE = register("riptide", new TridentRiptideEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment CHANNELING = register("channeling", new TridentChannelingEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment MULTISHOT = register("multishot", new MultiShotEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
   public static final Enchantment QUICK_CHARGE = register("quick_charge", new QuickChargeEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
   public static final Enchantment PIERCING = register("piercing", new ArrowPiercingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
   public static final Enchantment MENDING = register("mending", new MendingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.values()));
   public static final Enchantment VANISHING_CURSE = register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.values()));

   private static Enchantment register(String p_44993_, Enchantment p_44994_) {
      return Registry.register(Registry.ENCHANTMENT, p_44993_, p_44994_);
   }
}
