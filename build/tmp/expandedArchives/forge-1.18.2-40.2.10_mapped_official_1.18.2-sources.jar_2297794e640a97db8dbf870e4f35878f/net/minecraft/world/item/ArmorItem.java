package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ArmorItem extends Item implements Wearable {
   private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      protected ItemStack execute(BlockSource p_40408_, ItemStack p_40409_) {
         return ArmorItem.dispenseArmor(p_40408_, p_40409_) ? p_40409_ : super.execute(p_40408_, p_40409_);
      }
   };
   protected final EquipmentSlot slot;
   private final int defense;
   private final float toughness;
   protected final float knockbackResistance;
   protected final ArmorMaterial material;
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public static boolean dispenseArmor(BlockSource p_40399_, ItemStack p_40400_) {
      BlockPos blockpos = p_40399_.getPos().relative(p_40399_.getBlockState().getValue(DispenserBlock.FACING));
      List<LivingEntity> list = p_40399_.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(p_40400_)));
      if (list.isEmpty()) {
         return false;
      } else {
         LivingEntity livingentity = list.get(0);
         EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(p_40400_);
         ItemStack itemstack = p_40400_.split(1);
         livingentity.setItemSlot(equipmentslot, itemstack);
         if (livingentity instanceof Mob) {
            ((Mob)livingentity).setDropChance(equipmentslot, 2.0F);
            ((Mob)livingentity).setPersistenceRequired();
         }

         return true;
      }
   }

   public ArmorItem(ArmorMaterial p_40386_, EquipmentSlot p_40387_, Item.Properties p_40388_) {
      super(p_40388_.defaultDurability(p_40386_.getDurabilityForSlot(p_40387_)));
      this.material = p_40386_;
      this.slot = p_40387_;
      this.defense = p_40386_.getDefenseForSlot(p_40387_);
      this.toughness = p_40386_.getToughness();
      this.knockbackResistance = p_40386_.getKnockbackResistance();
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[p_40387_.getIndex()];
      builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
      builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
      if (this.knockbackResistance > 0) {
         builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
      }

      this.defaultModifiers = builder.build();
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }

   public int getEnchantmentValue() {
      return this.material.getEnchantmentValue();
   }

   public ArmorMaterial getMaterial() {
      return this.material;
   }

   public boolean isValidRepairItem(ItemStack p_40392_, ItemStack p_40393_) {
      return this.material.getRepairIngredient().test(p_40393_) || super.isValidRepairItem(p_40392_, p_40393_);
   }

   public InteractionResultHolder<ItemStack> use(Level p_40395_, Player p_40396_, InteractionHand p_40397_) {
      ItemStack itemstack = p_40396_.getItemInHand(p_40397_);
      EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemstack);
      ItemStack itemstack1 = p_40396_.getItemBySlot(equipmentslot);
      if (itemstack1.isEmpty()) {
         p_40396_.setItemSlot(equipmentslot, itemstack.copy());
         if (!p_40395_.isClientSide()) {
            p_40396_.awardStat(Stats.ITEM_USED.get(this));
         }

         itemstack.setCount(0);
         return InteractionResultHolder.sidedSuccess(itemstack, p_40395_.isClientSide());
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_40390_) {
      return p_40390_ == this.slot ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_40390_);
   }

   public int getDefense() {
      return this.defense;
   }

   public float getToughness() {
      return this.toughness;
   }

   @Nullable
   public SoundEvent getEquipSound() {
      return this.getMaterial().getEquipSound();
   }
}
