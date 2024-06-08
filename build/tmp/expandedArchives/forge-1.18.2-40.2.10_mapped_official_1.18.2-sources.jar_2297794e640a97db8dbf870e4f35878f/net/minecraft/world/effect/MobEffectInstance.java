package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class MobEffectInstance implements Comparable<MobEffectInstance>, net.minecraftforge.common.extensions.IForgeMobEffectInstance {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MobEffect effect;
   private int duration;
   private int amplifier;
   private boolean ambient;
   private boolean noCounter;
   private boolean visible;
   private boolean showIcon;
   @Nullable
   private MobEffectInstance hiddenEffect;

   public MobEffectInstance(MobEffect p_19513_) {
      this(p_19513_, 0, 0);
   }

   public MobEffectInstance(MobEffect p_19515_, int p_19516_) {
      this(p_19515_, p_19516_, 0);
   }

   public MobEffectInstance(MobEffect p_19518_, int p_19519_, int p_19520_) {
      this(p_19518_, p_19519_, p_19520_, false, true);
   }

   public MobEffectInstance(MobEffect p_19522_, int p_19523_, int p_19524_, boolean p_19525_, boolean p_19526_) {
      this(p_19522_, p_19523_, p_19524_, p_19525_, p_19526_, p_19526_);
   }

   public MobEffectInstance(MobEffect p_19528_, int p_19529_, int p_19530_, boolean p_19531_, boolean p_19532_, boolean p_19533_) {
      this(p_19528_, p_19529_, p_19530_, p_19531_, p_19532_, p_19533_, (MobEffectInstance)null);
   }

   public MobEffectInstance(MobEffect p_19535_, int p_19536_, int p_19537_, boolean p_19538_, boolean p_19539_, boolean p_19540_, @Nullable MobEffectInstance p_19541_) {
      this.effect = p_19535_;
      this.duration = p_19536_;
      this.amplifier = p_19537_;
      this.ambient = p_19538_;
      this.visible = p_19539_;
      this.showIcon = p_19540_;
      this.hiddenEffect = p_19541_;
   }

   public MobEffectInstance(MobEffectInstance p_19543_) {
      this.effect = p_19543_.effect;
      this.setDetailsFrom(p_19543_);
   }

   void setDetailsFrom(MobEffectInstance p_19549_) {
      this.duration = p_19549_.duration;
      this.amplifier = p_19549_.amplifier;
      this.ambient = p_19549_.ambient;
      this.visible = p_19549_.visible;
      this.showIcon = p_19549_.showIcon;
      this.curativeItems = p_19549_.curativeItems == null ? null : new java.util.ArrayList<net.minecraft.world.item.ItemStack>(p_19549_.curativeItems);
   }

   public boolean update(MobEffectInstance p_19559_) {
      if (this.effect != p_19559_.effect) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (p_19559_.amplifier > this.amplifier) {
         if (p_19559_.duration < this.duration) {
            MobEffectInstance mobeffectinstance = this.hiddenEffect;
            this.hiddenEffect = new MobEffectInstance(this);
            this.hiddenEffect.hiddenEffect = mobeffectinstance;
         }

         this.amplifier = p_19559_.amplifier;
         this.duration = p_19559_.duration;
         flag = true;
      } else if (p_19559_.duration > this.duration) {
         if (p_19559_.amplifier == this.amplifier) {
            this.duration = p_19559_.duration;
            flag = true;
         } else if (this.hiddenEffect == null) {
            this.hiddenEffect = new MobEffectInstance(p_19559_);
         } else {
            this.hiddenEffect.update(p_19559_);
         }
      }

      if (!p_19559_.ambient && this.ambient || flag) {
         this.ambient = p_19559_.ambient;
         flag = true;
      }

      if (p_19559_.visible != this.visible) {
         this.visible = p_19559_.visible;
         flag = true;
      }

      if (p_19559_.showIcon != this.showIcon) {
         this.showIcon = p_19559_.showIcon;
         flag = true;
      }

      return flag;
   }

   public MobEffect getEffect() {
      return this.effect == null ? null : this.effect.delegate.get();
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean showIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity p_19553_, Runnable p_19554_) {
      if (this.duration > 0) {
         if (this.effect.isDurationEffectTick(this.duration, this.amplifier)) {
            this.applyEffect(p_19553_);
         }

         this.tickDownDuration();
         if (this.duration == 0 && this.hiddenEffect != null) {
            this.setDetailsFrom(this.hiddenEffect);
            this.hiddenEffect = this.hiddenEffect.hiddenEffect;
            p_19554_.run();
         }
      }

      return this.duration > 0;
   }

   private int tickDownDuration() {
      if (this.hiddenEffect != null) {
         this.hiddenEffect.tickDownDuration();
      }

      return --this.duration;
   }

   public void applyEffect(LivingEntity p_19551_) {
      if (this.duration > 0) {
         this.effect.applyEffectTick(p_19551_, this.amplifier);
      }

   }

   public String getDescriptionId() {
      return this.effect.getDescriptionId();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getDescriptionId() + ", Duration: " + this.duration;
      }

      if (!this.visible) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_19574_) {
      if (this == p_19574_) {
         return true;
      } else if (!(p_19574_ instanceof MobEffectInstance)) {
         return false;
      } else {
         MobEffectInstance mobeffectinstance = (MobEffectInstance)p_19574_;
         return this.duration == mobeffectinstance.duration && this.amplifier == mobeffectinstance.amplifier && this.ambient == mobeffectinstance.ambient && this.effect.equals(mobeffectinstance.effect);
      }
   }

   public int hashCode() {
      int i = this.effect.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      return 31 * i + (this.ambient ? 1 : 0);
   }

   public CompoundTag save(CompoundTag p_19556_) {
      p_19556_.putByte("Id", (byte)MobEffect.getId(this.getEffect()));
      net.minecraftforge.common.ForgeHooks.saveMobEffect(p_19556_, "forge:id", this.getEffect());
      this.writeDetailsTo(p_19556_);
      return p_19556_;
   }

   private void writeDetailsTo(CompoundTag p_19568_) {
      p_19568_.putByte("Amplifier", (byte)this.getAmplifier());
      p_19568_.putInt("Duration", this.getDuration());
      p_19568_.putBoolean("Ambient", this.isAmbient());
      p_19568_.putBoolean("ShowParticles", this.isVisible());
      p_19568_.putBoolean("ShowIcon", this.showIcon());
      if (this.hiddenEffect != null) {
         CompoundTag compoundtag = new CompoundTag();
         this.hiddenEffect.save(compoundtag);
         p_19568_.put("HiddenEffect", compoundtag);
      }
      writeCurativeItems(p_19568_);

   }

   @Nullable
   public static MobEffectInstance load(CompoundTag p_19561_) {
      int i = p_19561_.getByte("Id") & 0xFF;
      MobEffect mobeffect = MobEffect.byId(i);
      mobeffect = net.minecraftforge.common.ForgeHooks.loadMobEffect(p_19561_, "forge:id", mobeffect);
      return mobeffect == null ? null : loadSpecifiedEffect(mobeffect, p_19561_);
   }

   private static MobEffectInstance loadSpecifiedEffect(MobEffect p_19546_, CompoundTag p_19547_) {
      int i = p_19547_.getByte("Amplifier");
      int j = p_19547_.getInt("Duration");
      boolean flag = p_19547_.getBoolean("Ambient");
      boolean flag1 = true;
      if (p_19547_.contains("ShowParticles", 1)) {
         flag1 = p_19547_.getBoolean("ShowParticles");
      }

      boolean flag2 = flag1;
      if (p_19547_.contains("ShowIcon", 1)) {
         flag2 = p_19547_.getBoolean("ShowIcon");
      }

      MobEffectInstance mobeffectinstance = null;
      if (p_19547_.contains("HiddenEffect", 10)) {
         mobeffectinstance = loadSpecifiedEffect(p_19546_, p_19547_.getCompound("HiddenEffect"));
      }

      return readCurativeItems(new MobEffectInstance(p_19546_, j, i < 0 ? 0 : i, flag, flag1, flag2, mobeffectinstance), p_19547_);
   }

   public void setNoCounter(boolean p_19563_) {
      this.noCounter = p_19563_;
   }

   public boolean isNoCounter() {
      return this.noCounter;
   }

   public int compareTo(MobEffectInstance p_19566_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_19566_.getDuration() <= 32147) && (!this.isAmbient() || !p_19566_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_19566_.isAmbient()).compare(this.getDuration(), p_19566_.getDuration()).compare(this.getEffect().getSortOrder(this), p_19566_.getEffect().getSortOrder(this)).result() : ComparisonChain.start().compare(this.isAmbient(), p_19566_.isAmbient()).compare(this.getEffect().getSortOrder(this), p_19566_.getEffect().getSortOrder(this)).result();
   }

   //======================= FORGE START ===========================
   private java.util.List<net.minecraft.world.item.ItemStack> curativeItems;

   @Override
   public java.util.List<net.minecraft.world.item.ItemStack> getCurativeItems() {
      if (this.curativeItems == null) //Lazy load this so that we don't create a circular dep on Items.
         this.curativeItems = getEffect().getCurativeItems();
      return this.curativeItems;
   }
   @Override
   public void setCurativeItems(java.util.List<net.minecraft.world.item.ItemStack> curativeItems) {
      this.curativeItems = curativeItems;
   }
   private static MobEffectInstance readCurativeItems(MobEffectInstance effect, CompoundTag nbt) {
      if (nbt.contains("CurativeItems", net.minecraft.nbt.Tag.TAG_LIST)) {
         java.util.List<net.minecraft.world.item.ItemStack> items = new java.util.ArrayList<net.minecraft.world.item.ItemStack>();
         net.minecraft.nbt.ListTag list = nbt.getList("CurativeItems", net.minecraft.nbt.Tag.TAG_COMPOUND);
         for (int i = 0; i < list.size(); i++) {
            items.add(net.minecraft.world.item.ItemStack.of(list.getCompound(i)));
         }
         effect.setCurativeItems(items);
      }

      return effect;
   }
}
