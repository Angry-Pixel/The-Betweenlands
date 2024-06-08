package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DiggerItem extends TieredItem implements Vanishable {
   private final TagKey<Block> blocks;
   protected final float speed;
   private final float attackDamageBaseline;
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public DiggerItem(float p_204108_, float p_204109_, Tier p_204110_, TagKey<Block> p_204111_, Item.Properties p_204112_) {
      super(p_204110_, p_204112_);
      this.blocks = p_204111_;
      this.speed = p_204110_.getSpeed();
      this.attackDamageBaseline = p_204108_ + p_204110_.getAttackDamageBonus();
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double)this.attackDamageBaseline, AttributeModifier.Operation.ADDITION));
      builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)p_204109_, AttributeModifier.Operation.ADDITION));
      this.defaultModifiers = builder.build();
   }

   public float getDestroySpeed(ItemStack p_41004_, BlockState p_41005_) {
      return p_41005_.is(this.blocks) ? this.speed : 1.0F;
   }

   public boolean hurtEnemy(ItemStack p_40994_, LivingEntity p_40995_, LivingEntity p_40996_) {
      p_40994_.hurtAndBreak(2, p_40996_, (p_41007_) -> {
         p_41007_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack p_40998_, Level p_40999_, BlockState p_41000_, BlockPos p_41001_, LivingEntity p_41002_) {
      if (!p_40999_.isClientSide && p_41000_.getDestroySpeed(p_40999_, p_41001_) != 0.0F) {
         p_40998_.hurtAndBreak(1, p_41002_, (p_40992_) -> {
            p_40992_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return true;
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_40990_) {
      return p_40990_ == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_40990_);
   }

   public float getAttackDamage() {
      return this.attackDamageBaseline;
   }

   @Deprecated // FORGE: Use stack sensitive variant below
   public boolean isCorrectToolForDrops(BlockState p_150816_) {
      if (net.minecraftforge.common.TierSortingRegistry.isTierSorted(getTier())) {
         return net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(getTier(), p_150816_) && p_150816_.is(this.blocks);
      }
      int i = this.getTier().getLevel();
      if (i < 3 && p_150816_.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
         return false;
      } else if (i < 2 && p_150816_.is(BlockTags.NEEDS_IRON_TOOL)) {
         return false;
      } else {
         return i < 1 && p_150816_.is(BlockTags.NEEDS_STONE_TOOL) ? false : p_150816_.is(this.blocks);
      }
   }

   // FORGE START
   @Override
   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return state.is(blocks) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
   }
}
