package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

public class MobBucketItem extends BucketItem {
   private final java.util.function.Supplier<? extends EntityType<?>> entityTypeSupplier;
   private final java.util.function.Supplier<? extends SoundEvent> emptySoundSupplier;

   @Deprecated
   public MobBucketItem(EntityType<?> p_151137_, Fluid p_151138_, SoundEvent p_151139_, Item.Properties p_151140_) {
      this(() -> p_151137_, () -> p_151138_, () -> p_151139_, p_151140_);
   }

   public MobBucketItem(java.util.function.Supplier<? extends EntityType<?>> entitySupplier, java.util.function.Supplier<? extends Fluid> fluidSupplier, java.util.function.Supplier<? extends SoundEvent> soundSupplier, Item.Properties properties) {
      super(fluidSupplier, properties);
      this.emptySoundSupplier = soundSupplier;
      this.entityTypeSupplier = entitySupplier;
   }

   public void checkExtraContent(@Nullable Player p_151146_, Level p_151147_, ItemStack p_151148_, BlockPos p_151149_) {
      if (p_151147_ instanceof ServerLevel) {
         this.spawn((ServerLevel)p_151147_, p_151148_, p_151149_);
         p_151147_.gameEvent(p_151146_, GameEvent.ENTITY_PLACE, p_151149_);
      }

   }

   protected void playEmptySound(@Nullable Player p_151151_, LevelAccessor p_151152_, BlockPos p_151153_) {
      p_151152_.playSound(p_151151_, p_151153_, getEmptySound(), SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   private void spawn(ServerLevel p_151142_, ItemStack p_151143_, BlockPos p_151144_) {
      Entity entity = getFishType().spawn(p_151142_, p_151143_, (Player)null, p_151144_, MobSpawnType.BUCKET, true, false);
      if (entity instanceof Bucketable) {
         Bucketable bucketable = (Bucketable)entity;
         bucketable.loadFromBucketTag(p_151143_.getOrCreateTag());
         bucketable.setFromBucket(true);
      }

   }

   public void appendHoverText(ItemStack p_151155_, @Nullable Level p_151156_, List<Component> p_151157_, TooltipFlag p_151158_) {
      if (getFishType() == EntityType.TROPICAL_FISH) {
         CompoundTag compoundtag = p_151155_.getTag();
         if (compoundtag != null && compoundtag.contains("BucketVariantTag", 3)) {
            int i = compoundtag.getInt("BucketVariantTag");
            ChatFormatting[] achatformatting = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
            String s = "color.minecraft." + TropicalFish.getBaseColor(i);
            String s1 = "color.minecraft." + TropicalFish.getPatternColor(i);

            for(int j = 0; j < TropicalFish.COMMON_VARIANTS.length; ++j) {
               if (i == TropicalFish.COMMON_VARIANTS[j]) {
                  p_151157_.add((new TranslatableComponent(TropicalFish.getPredefinedName(j))).withStyle(achatformatting));
                  return;
               }
            }

            p_151157_.add((new TranslatableComponent(TropicalFish.getFishTypeName(i))).withStyle(achatformatting));
            MutableComponent mutablecomponent = new TranslatableComponent(s);
            if (!s.equals(s1)) {
               mutablecomponent.append(", ").append(new TranslatableComponent(s1));
            }

            mutablecomponent.withStyle(achatformatting);
            p_151157_.add(mutablecomponent);
         }
      }

   }

   // Forge Start
   protected EntityType<?> getFishType() {
      return entityTypeSupplier.get();
   }

   protected SoundEvent getEmptySound() {
      return emptySoundSupplier.get();
   }
}
