package net.minecraft.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem extends Item {
   public static final String EFFECTS_TAG = "Effects";
   public static final String EFFECT_ID_TAG = "EffectId";
   public static final String EFFECT_DURATION_TAG = "EffectDuration";

   public SuspiciousStewItem(Item.Properties p_43257_) {
      super(p_43257_);
   }

   public static void saveMobEffect(ItemStack p_43259_, MobEffect p_43260_, int p_43261_) {
      CompoundTag compoundtag = p_43259_.getOrCreateTag();
      ListTag listtag = compoundtag.getList("Effects", 9);
      CompoundTag compoundtag1 = new CompoundTag();
      compoundtag1.putByte("EffectId", (byte)MobEffect.getId(p_43260_));
      net.minecraftforge.common.ForgeHooks.saveMobEffect(compoundtag1, "forge:effect_id", p_43260_);
      compoundtag1.putInt("EffectDuration", p_43261_);
      listtag.add(compoundtag1);
      compoundtag.put("Effects", listtag);
   }

   public ItemStack finishUsingItem(ItemStack p_43263_, Level p_43264_, LivingEntity p_43265_) {
      ItemStack itemstack = super.finishUsingItem(p_43263_, p_43264_, p_43265_);
      CompoundTag compoundtag = p_43263_.getTag();
      if (compoundtag != null && compoundtag.contains("Effects", 9)) {
         ListTag listtag = compoundtag.getList("Effects", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            int j = 160;
            CompoundTag compoundtag1 = listtag.getCompound(i);
            if (compoundtag1.contains("EffectDuration", 3)) {
               j = compoundtag1.getInt("EffectDuration");
            }

            MobEffect mobeffect = MobEffect.byId(compoundtag1.getByte("EffectId"));
            mobeffect = net.minecraftforge.common.ForgeHooks.loadMobEffect(compoundtag1, "forge:effect_id", mobeffect);
            if (mobeffect != null) {
               p_43265_.addEffect(new MobEffectInstance(mobeffect, j));
            }
         }
      }

      return p_43265_ instanceof Player && ((Player)p_43265_).getAbilities().instabuild ? itemstack : new ItemStack(Items.BOWL);
   }
}
