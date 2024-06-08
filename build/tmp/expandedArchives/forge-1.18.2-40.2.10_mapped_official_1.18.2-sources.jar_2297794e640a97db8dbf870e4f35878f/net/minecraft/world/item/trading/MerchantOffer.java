package net.minecraft.world.item.trading;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class MerchantOffer {
   private final ItemStack baseCostA;
   private final ItemStack costB;
   private final ItemStack result;
   private int uses;
   private final int maxUses;
   private boolean rewardExp = true;
   private int specialPriceDiff;
   private int demand;
   private float priceMultiplier;
   private int xp = 1;

   public MerchantOffer(CompoundTag p_45351_) {
      this.baseCostA = ItemStack.of(p_45351_.getCompound("buy"));
      this.costB = ItemStack.of(p_45351_.getCompound("buyB"));
      this.result = ItemStack.of(p_45351_.getCompound("sell"));
      this.uses = p_45351_.getInt("uses");
      if (p_45351_.contains("maxUses", 99)) {
         this.maxUses = p_45351_.getInt("maxUses");
      } else {
         this.maxUses = 4;
      }

      if (p_45351_.contains("rewardExp", 1)) {
         this.rewardExp = p_45351_.getBoolean("rewardExp");
      }

      if (p_45351_.contains("xp", 3)) {
         this.xp = p_45351_.getInt("xp");
      }

      if (p_45351_.contains("priceMultiplier", 5)) {
         this.priceMultiplier = p_45351_.getFloat("priceMultiplier");
      }

      this.specialPriceDiff = p_45351_.getInt("specialPrice");
      this.demand = p_45351_.getInt("demand");
   }

   public MerchantOffer(ItemStack p_45321_, ItemStack p_45322_, int p_45323_, int p_45324_, float p_45325_) {
      this(p_45321_, ItemStack.EMPTY, p_45322_, p_45323_, p_45324_, p_45325_);
   }

   public MerchantOffer(ItemStack p_45327_, ItemStack p_45328_, ItemStack p_45329_, int p_45330_, int p_45331_, float p_45332_) {
      this(p_45327_, p_45328_, p_45329_, 0, p_45330_, p_45331_, p_45332_);
   }

   public MerchantOffer(ItemStack p_45334_, ItemStack p_45335_, ItemStack p_45336_, int p_45337_, int p_45338_, int p_45339_, float p_45340_) {
      this(p_45334_, p_45335_, p_45336_, p_45337_, p_45338_, p_45339_, p_45340_, 0);
   }

   public MerchantOffer(ItemStack p_45342_, ItemStack p_45343_, ItemStack p_45344_, int p_45345_, int p_45346_, int p_45347_, float p_45348_, int p_45349_) {
      this.baseCostA = p_45342_;
      this.costB = p_45343_;
      this.result = p_45344_;
      this.uses = p_45345_;
      this.maxUses = p_45346_;
      this.xp = p_45347_;
      this.priceMultiplier = p_45348_;
      this.demand = p_45349_;
   }

   public ItemStack getBaseCostA() {
      return this.baseCostA;
   }

   public ItemStack getCostA() {
      int i = this.baseCostA.getCount();
      ItemStack itemstack = this.baseCostA.copy();
      int j = Math.max(0, Mth.floor((float)(i * this.demand) * this.priceMultiplier));
      itemstack.setCount(Mth.clamp(i + j + this.specialPriceDiff, 1, this.baseCostA.getMaxStackSize()));
      return itemstack;
   }

   public ItemStack getCostB() {
      return this.costB;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public void updateDemand() {
      this.demand = this.demand + this.uses - (this.maxUses - this.uses);
   }

   public ItemStack assemble() {
      return this.result.copy();
   }

   public int getUses() {
      return this.uses;
   }

   public void resetUses() {
      this.uses = 0;
   }

   public int getMaxUses() {
      return this.maxUses;
   }

   public void increaseUses() {
      ++this.uses;
   }

   public int getDemand() {
      return this.demand;
   }

   public void addToSpecialPriceDiff(int p_45354_) {
      this.specialPriceDiff += p_45354_;
   }

   public void resetSpecialPriceDiff() {
      this.specialPriceDiff = 0;
   }

   public int getSpecialPriceDiff() {
      return this.specialPriceDiff;
   }

   public void setSpecialPriceDiff(int p_45360_) {
      this.specialPriceDiff = p_45360_;
   }

   public float getPriceMultiplier() {
      return this.priceMultiplier;
   }

   public int getXp() {
      return this.xp;
   }

   public boolean isOutOfStock() {
      return this.uses >= this.maxUses;
   }

   public void setToOutOfStock() {
      this.uses = this.maxUses;
   }

   public boolean needsRestock() {
      return this.uses > 0;
   }

   public boolean shouldRewardExp() {
      return this.rewardExp;
   }

   public CompoundTag createTag() {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.put("buy", this.baseCostA.save(new CompoundTag()));
      compoundtag.put("sell", this.result.save(new CompoundTag()));
      compoundtag.put("buyB", this.costB.save(new CompoundTag()));
      compoundtag.putInt("uses", this.uses);
      compoundtag.putInt("maxUses", this.maxUses);
      compoundtag.putBoolean("rewardExp", this.rewardExp);
      compoundtag.putInt("xp", this.xp);
      compoundtag.putFloat("priceMultiplier", this.priceMultiplier);
      compoundtag.putInt("specialPrice", this.specialPriceDiff);
      compoundtag.putInt("demand", this.demand);
      return compoundtag;
   }

   public boolean satisfiedBy(ItemStack p_45356_, ItemStack p_45357_) {
      return this.isRequiredItem(p_45356_, this.getCostA()) && p_45356_.getCount() >= this.getCostA().getCount() && this.isRequiredItem(p_45357_, this.costB) && p_45357_.getCount() >= this.costB.getCount();
   }

   private boolean isRequiredItem(ItemStack p_45366_, ItemStack p_45367_) {
      if (p_45367_.isEmpty() && p_45366_.isEmpty()) {
         return true;
      } else {
         ItemStack itemstack = p_45366_.copy();
         if (itemstack.getItem().isDamageable(itemstack)) {
            itemstack.setDamageValue(itemstack.getDamageValue());
         }

         return ItemStack.isSame(itemstack, p_45367_) && (!p_45367_.hasTag() || itemstack.hasTag() && NbtUtils.compareNbt(p_45367_.getTag(), itemstack.getTag(), false));
      }
   }

   public boolean take(ItemStack p_45362_, ItemStack p_45363_) {
      if (!this.satisfiedBy(p_45362_, p_45363_)) {
         return false;
      } else {
         p_45362_.shrink(this.getCostA().getCount());
         if (!this.getCostB().isEmpty()) {
            p_45363_.shrink(this.getCostB().getCount());
         }

         return true;
      }
   }
}
