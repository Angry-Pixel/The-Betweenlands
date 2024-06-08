package net.minecraft.world.entity.animal.horse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public abstract class AbstractChestedHorse extends AbstractHorse {
   private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData.defineId(AbstractChestedHorse.class, EntityDataSerializers.BOOLEAN);
   public static final int INV_CHEST_COUNT = 15;

   protected AbstractChestedHorse(EntityType<? extends AbstractChestedHorse> p_30485_, Level p_30486_) {
      super(p_30485_, p_30486_);
      this.canGallop = false;
   }

   protected void randomizeAttributes() {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_CHEST, false);
   }

   public static AttributeSupplier.Builder createBaseChestedHorseAttributes() {
      return createBaseHorseAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.175F).add(Attributes.JUMP_STRENGTH, 0.5D);
   }

   public boolean hasChest() {
      return this.entityData.get(DATA_ID_CHEST);
   }

   public void setChest(boolean p_30505_) {
      this.entityData.set(DATA_ID_CHEST, p_30505_);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 17 : super.getInventorySize();
   }

   public double getPassengersRidingOffset() {
      return super.getPassengersRidingOffset() - 0.25D;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.hasChest()) {
         if (!this.level.isClientSide) {
            this.spawnAtLocation(Blocks.CHEST);
         }

         this.setChest(false);
      }

   }

   public void addAdditionalSaveData(CompoundTag p_30496_) {
      super.addAdditionalSaveData(p_30496_);
      p_30496_.putBoolean("ChestedHorse", this.hasChest());
      if (this.hasChest()) {
         ListTag listtag = new ListTag();

         for(int i = 2; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
               CompoundTag compoundtag = new CompoundTag();
               compoundtag.putByte("Slot", (byte)i);
               itemstack.save(compoundtag);
               listtag.add(compoundtag);
            }
         }

         p_30496_.put("Items", listtag);
      }

   }

   public void readAdditionalSaveData(CompoundTag p_30488_) {
      super.readAdditionalSaveData(p_30488_);
      this.setChest(p_30488_.getBoolean("ChestedHorse"));
      this.createInventory();
      if (this.hasChest()) {
         ListTag listtag = p_30488_.getList("Items", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 2 && j < this.inventory.getContainerSize()) {
               this.inventory.setItem(j, ItemStack.of(compoundtag));
            }
         }
      }

      this.updateContainerEquipment();
   }

   public SlotAccess getSlot(int p_149479_) {
      return p_149479_ == 499 ? new SlotAccess() {
         public ItemStack get() {
            return AbstractChestedHorse.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
         }

         public boolean set(ItemStack p_149485_) {
            if (p_149485_.isEmpty()) {
               if (AbstractChestedHorse.this.hasChest()) {
                  AbstractChestedHorse.this.setChest(false);
                  AbstractChestedHorse.this.createInventory();
               }

               return true;
            } else if (p_149485_.is(Items.CHEST)) {
               if (!AbstractChestedHorse.this.hasChest()) {
                  AbstractChestedHorse.this.setChest(true);
                  AbstractChestedHorse.this.createInventory();
               }

               return true;
            } else {
               return false;
            }
         }
      } : super.getSlot(p_149479_);
   }

   public InteractionResult mobInteract(Player p_30493_, InteractionHand p_30494_) {
      ItemStack itemstack = p_30493_.getItemInHand(p_30494_);
      if (!this.isBaby()) {
         if (this.isTamed() && p_30493_.isSecondaryUseActive()) {
            this.openInventory(p_30493_);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }

         if (this.isVehicle()) {
            return super.mobInteract(p_30493_, p_30494_);
         }
      }

      if (!itemstack.isEmpty()) {
         if (this.isFood(itemstack)) {
            return this.fedFood(p_30493_, itemstack);
         }

         if (!this.isTamed()) {
            this.makeMad();
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }

         if (!this.hasChest() && itemstack.is(Blocks.CHEST.asItem())) {
            this.setChest(true);
            this.playChestEquipsSound();
            if (!p_30493_.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            this.createInventory();
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }

         if (!this.isBaby() && !this.isSaddled() && itemstack.is(Items.SADDLE)) {
            this.openInventory(p_30493_);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }
      }

      if (this.isBaby()) {
         return super.mobInteract(p_30493_, p_30494_);
      } else {
         this.doPlayerRide(p_30493_);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      }
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return 5;
   }
}