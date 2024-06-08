package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Item extends net.minecraftforge.registries.ForgeRegistryEntry<Item> implements ItemLike, net.minecraftforge.common.extensions.IForgeItem {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Map<Block, Item> BY_BLOCK = net.minecraftforge.registries.GameData.getBlockItemMap();
   protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   public static final int MAX_STACK_SIZE = 64;
   public static final int EAT_DURATION = 32;
   public static final int MAX_BAR_WIDTH = 13;
   private final Holder.Reference<Item> builtInRegistryHolder = Registry.ITEM.createIntrusiveHolder(this);
   @Nullable
   protected final CreativeModeTab category;
   private final Rarity rarity;
   private final int maxStackSize;
   private final int maxDamage;
   private final boolean isFireResistant;
   @Nullable
   private final Item craftingRemainingItem;
   @Nullable
   private String descriptionId;
   @Nullable
   private final FoodProperties foodProperties;

   public static int getId(Item p_41394_) {
      return p_41394_ == null ? 0 : Registry.ITEM.getId(p_41394_);
   }

   public static Item byId(int p_41446_) {
      return Registry.ITEM.byId(p_41446_);
   }

   /** @deprecated */
   @Deprecated
   public static Item byBlock(Block p_41440_) {
      return BY_BLOCK.getOrDefault(p_41440_, Items.AIR);
   }

   public Item(Item.Properties p_41383_) {
      this.category = p_41383_.category;
      this.rarity = p_41383_.rarity;
      this.craftingRemainingItem = p_41383_.craftingRemainingItem;
      this.maxDamage = p_41383_.maxDamage;
      this.maxStackSize = p_41383_.maxStackSize;
      this.foodProperties = p_41383_.foodProperties;
      this.isFireResistant = p_41383_.isFireResistant;
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         String s = this.getClass().getSimpleName();
         if (!s.endsWith("Item")) {
            LOGGER.error("Item classes should end with Item and {} doesn't.", (Object)s);
         }
      }
      this.canRepair = p_41383_.canRepair;
      initClient();
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<Item> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   /** @deprecated Forge: Use damage source sensitive version */
   @Deprecated
   public void onUseTick(Level p_41428_, LivingEntity p_41429_, ItemStack p_41430_, int p_41431_) {
   }

   public void onDestroyed(ItemEntity p_150887_) {
   }

   public void verifyTagAfterLoad(CompoundTag p_150898_) {
   }

   public boolean canAttackBlock(BlockState p_41441_, Level p_41442_, BlockPos p_41443_, Player p_41444_) {
      return true;
   }

   public Item asItem() {
      return this;
   }

   public InteractionResult useOn(UseOnContext p_41427_) {
      return InteractionResult.PASS;
   }

   public float getDestroySpeed(ItemStack p_41425_, BlockState p_41426_) {
      return 1.0F;
   }

   public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
      ItemStack itemstack = p_41433_.getItemInHand(p_41434_);
      if (itemstack.isEdible()) {
         if (p_41433_.canEat(itemstack.getFoodProperties(p_41433_).canAlwaysEat())) {
            p_41433_.startUsingItem(p_41434_);
            return InteractionResultHolder.consume(itemstack);
         } else {
            return InteractionResultHolder.fail(itemstack);
         }
      } else {
         return InteractionResultHolder.pass(p_41433_.getItemInHand(p_41434_));
      }
   }

   public ItemStack finishUsingItem(ItemStack p_41409_, Level p_41410_, LivingEntity p_41411_) {
      return this.isEdible() ? p_41411_.eat(p_41410_, p_41409_) : p_41409_;
   }

   @Deprecated // Use ItemStack sensitive version.
   public final int getMaxStackSize() {
      return this.maxStackSize;
   }

   @Deprecated // Use ItemStack sensitive version.
   public final int getMaxDamage() {
      return this.maxDamage;
   }

   public boolean canBeDepleted() {
      return this.maxDamage > 0;
   }

   public boolean isBarVisible(ItemStack p_150899_) {
      return p_150899_.isDamaged();
   }

   public int getBarWidth(ItemStack p_150900_) {
      return Math.round(13.0F - (float)p_150900_.getDamageValue() * 13.0F / (float)this.getMaxDamage(p_150900_));
   }

   public int getBarColor(ItemStack p_150901_) {
      float stackMaxDamage = this.getMaxDamage(p_150901_);
      float f = Math.max(0.0F, (stackMaxDamage - (float)p_150901_.getDamageValue()) / stackMaxDamage);
      return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
   }

   public boolean overrideStackedOnOther(ItemStack p_150888_, Slot p_150889_, ClickAction p_150890_, Player p_150891_) {
      return false;
   }

   public boolean overrideOtherStackedOnMe(ItemStack p_150892_, ItemStack p_150893_, Slot p_150894_, ClickAction p_150895_, Player p_150896_, SlotAccess p_150897_) {
      return false;
   }

   public boolean hurtEnemy(ItemStack p_41395_, LivingEntity p_41396_, LivingEntity p_41397_) {
      return false;
   }

   public boolean mineBlock(ItemStack p_41416_, Level p_41417_, BlockState p_41418_, BlockPos p_41419_, LivingEntity p_41420_) {
      return false;
   }

   public boolean isCorrectToolForDrops(BlockState p_41450_) {
      return false;
   }

   public InteractionResult interactLivingEntity(ItemStack p_41398_, Player p_41399_, LivingEntity p_41400_, InteractionHand p_41401_) {
      return InteractionResult.PASS;
   }

   public Component getDescription() {
      return new TranslatableComponent(this.getDescriptionId());
   }

   public String toString() {
      return Registry.ITEM.getKey(this).getPath();
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("item", Registry.ITEM.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public String getDescriptionId(ItemStack p_41455_) {
      return this.getDescriptionId();
   }

   public boolean shouldOverrideMultiplayerNbt() {
      return true;
   }

   @Nullable
   @Deprecated // Use ItemStack sensitive version.
   public final Item getCraftingRemainingItem() {
      return this.craftingRemainingItem;
   }

   @Deprecated // Use ItemStack sensitive version.
   public boolean hasCraftingRemainingItem() {
      return this.craftingRemainingItem != null;
   }

   public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
   }

   public void onCraftedBy(ItemStack p_41447_, Level p_41448_, Player p_41449_) {
   }

   public boolean isComplex() {
      return false;
   }

   public UseAnim getUseAnimation(ItemStack p_41452_) {
      return p_41452_.getItem().isEdible() ? UseAnim.EAT : UseAnim.NONE;
   }

   public int getUseDuration(ItemStack p_41454_) {
      if (p_41454_.getItem().isEdible()) {
         return p_41454_.getFoodProperties(null).isFastFood() ? 16 : 32;
      } else {
         return 0;
      }
   }

   public void releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity p_41414_, int p_41415_) {
   }

   public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
      return Optional.empty();
   }

   public Component getName(ItemStack p_41458_) {
      return new TranslatableComponent(this.getDescriptionId(p_41458_));
   }

   public boolean isFoil(ItemStack p_41453_) {
      return p_41453_.isEnchanted();
   }

   public Rarity getRarity(ItemStack p_41461_) {
      if (!p_41461_.isEnchanted()) {
         return this.rarity;
      } else {
         switch(this.rarity) {
         case COMMON:
         case UNCOMMON:
            return Rarity.RARE;
         case RARE:
            return Rarity.EPIC;
         case EPIC:
         default:
            return this.rarity;
         }
      }
   }

   public boolean isEnchantable(ItemStack p_41456_) {
      return this.getItemStackLimit(p_41456_) == 1 && this.isDamageable(p_41456_);
   }

   protected static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
      float f = p_41437_.getXRot();
      float f1 = p_41437_.getYRot();
      Vec3 vec3 = p_41437_.getEyePosition();
      float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
      float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d0 = p_41437_.getReachDistance();
      Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
      return p_41436_.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, p_41437_));
   }

   public int getEnchantmentValue() {
      return 0;
   }

   public void fillItemCategory(CreativeModeTab p_41391_, NonNullList<ItemStack> p_41392_) {
      if (this.allowdedIn(p_41391_)) {
         p_41392_.add(new ItemStack(this));
      }

   }

   protected boolean allowdedIn(CreativeModeTab p_41390_) {
      if (getCreativeTabs().stream().anyMatch(tab -> tab == p_41390_)) return true;
      CreativeModeTab creativemodetab = this.getItemCategory();
      return creativemodetab != null && (p_41390_ == CreativeModeTab.TAB_SEARCH || p_41390_ == creativemodetab);
   }

   @Nullable
   public final CreativeModeTab getItemCategory() {
      return this.category;
   }

   public boolean isValidRepairItem(ItemStack p_41402_, ItemStack p_41403_) {
      return false;
   }

   @Deprecated // Use ItemStack sensitive version.
   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_41388_) {
      return ImmutableMultimap.of();
   }

   protected final boolean canRepair;

   @Override
   public boolean isRepairable(ItemStack stack) {
      return canRepair && isDamageable(stack);
   }

   public boolean useOnRelease(ItemStack p_41464_) {
      return p_41464_.getItem() == Items.CROSSBOW;
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean isEdible() {
      return this.foodProperties != null;
   }

   // Use IForgeItem#getFoodProperties(ItemStack, LivingEntity) in favour of this.
   @Deprecated
   @Nullable
   public FoodProperties getFoodProperties() {
      return this.foodProperties;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.GENERIC_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.GENERIC_EAT;
   }

   public boolean isFireResistant() {
      return this.isFireResistant;
   }

   public boolean canBeHurtBy(DamageSource p_41387_) {
      return !this.isFireResistant || !p_41387_.isFire();
   }

   @Nullable
   public SoundEvent getEquipSound() {
      return null;
   }

   public boolean canFitInsideContainerItems() {
      return true;
   }

   // FORGE START
   private Object renderProperties;

   /*
      DO NOT CALL, IT WILL DISAPPEAR IN THE FUTURE
      Call RenderProperties.get instead
    */
   public Object getRenderPropertiesInternal() {
      return renderProperties;
   }

   private void initClient() {
      // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
      if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT && !net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) {
         initializeClient(properties -> {
            if (properties == this)
               throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
            this.renderProperties = properties;
         });
      }
   }

   public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
   }
   // END FORGE

   public static class Properties {
      int maxStackSize = 64;
      int maxDamage;
      @Nullable
      Item craftingRemainingItem;
      @Nullable
      CreativeModeTab category;
      Rarity rarity = Rarity.COMMON;
      @Nullable
      FoodProperties foodProperties;
      boolean isFireResistant;
      private boolean canRepair = true;

      public Item.Properties food(FoodProperties p_41490_) {
         this.foodProperties = p_41490_;
         return this;
      }

      public Item.Properties stacksTo(int p_41488_) {
         if (this.maxDamage > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.maxStackSize = p_41488_;
            return this;
         }
      }

      public Item.Properties defaultDurability(int p_41500_) {
         return this.maxDamage == 0 ? this.durability(p_41500_) : this;
      }

      public Item.Properties durability(int p_41504_) {
         this.maxDamage = p_41504_;
         this.maxStackSize = 1;
         return this;
      }

      public Item.Properties craftRemainder(Item p_41496_) {
         this.craftingRemainingItem = p_41496_;
         return this;
      }

      public Item.Properties tab(CreativeModeTab p_41492_) {
         this.category = p_41492_;
         return this;
      }

      public Item.Properties rarity(Rarity p_41498_) {
         this.rarity = p_41498_;
         return this;
      }

      public Item.Properties fireResistant() {
         this.isFireResistant = true;
         return this;
      }

      public Item.Properties setNoRepair() {
         canRepair = false;
         return this;
      }
   }
}
