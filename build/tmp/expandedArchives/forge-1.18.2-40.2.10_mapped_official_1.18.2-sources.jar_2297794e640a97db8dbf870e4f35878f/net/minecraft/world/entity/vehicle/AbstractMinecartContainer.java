package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public abstract class AbstractMinecartContainer extends AbstractMinecart implements Container, MenuProvider {
   private NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;

   protected AbstractMinecartContainer(EntityType<?> p_38213_, Level p_38214_) {
      super(p_38213_, p_38214_);
   }

   protected AbstractMinecartContainer(EntityType<?> p_38207_, double p_38208_, double p_38209_, double p_38210_, Level p_38211_) {
      super(p_38207_, p_38211_, p_38208_, p_38209_, p_38210_);
   }

   public void destroy(DamageSource p_38228_) {
      super.destroy(p_38228_);
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         Containers.dropContents(this.level, this, this);
         if (!this.level.isClientSide) {
            Entity entity = p_38228_.getDirectEntity();
            if (entity != null && entity.getType() == EntityType.PLAYER) {
               PiglinAi.angerNearbyPiglins((Player)entity, true);
            }
         }
      }

   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.itemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int p_38218_) {
      this.unpackLootTable((Player)null);
      return this.itemStacks.get(p_38218_);
   }

   public ItemStack removeItem(int p_38220_, int p_38221_) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.removeItem(this.itemStacks, p_38220_, p_38221_);
   }

   public ItemStack removeItemNoUpdate(int p_38244_) {
      this.unpackLootTable((Player)null);
      ItemStack itemstack = this.itemStacks.get(p_38244_);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.itemStacks.set(p_38244_, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setItem(int p_38225_, ItemStack p_38226_) {
      this.unpackLootTable((Player)null);
      this.itemStacks.set(p_38225_, p_38226_);
      if (!p_38226_.isEmpty() && p_38226_.getCount() > this.getMaxStackSize()) {
         p_38226_.setCount(this.getMaxStackSize());
      }

   }

   public SlotAccess getSlot(final int p_150257_) {
      return p_150257_ >= 0 && p_150257_ < this.getContainerSize() ? new SlotAccess() {
         public ItemStack get() {
            return AbstractMinecartContainer.this.getItem(p_150257_);
         }

         public boolean set(ItemStack p_150265_) {
            AbstractMinecartContainer.this.setItem(p_150257_, p_150265_);
            return true;
         }
      } : super.getSlot(p_150257_);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player p_38230_) {
      if (this.isRemoved()) {
         return false;
      } else {
         return !(p_38230_.distanceToSqr(this) > 64.0D);
      }
   }

   public void remove(Entity.RemovalReason p_150255_) {
      if (!this.level.isClientSide && p_150255_.shouldDestroy()) {
         Containers.dropContents(this.level, this, this);
      }

      super.remove(p_150255_);
   }

   protected void addAdditionalSaveData(CompoundTag p_38248_) {
      super.addAdditionalSaveData(p_38248_);
      if (this.lootTable != null) {
         p_38248_.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_38248_.putLong("LootTableSeed", this.lootTableSeed);
         }
      } else {
         ContainerHelper.saveAllItems(p_38248_, this.itemStacks);
      }

   }

   protected void readAdditionalSaveData(CompoundTag p_38235_) {
      super.readAdditionalSaveData(p_38235_);
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (p_38235_.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_38235_.getString("LootTable"));
         this.lootTableSeed = p_38235_.getLong("LootTableSeed");
      } else {
         ContainerHelper.loadAllItems(p_38235_, this.itemStacks);
      }

   }

   public InteractionResult interact(Player p_38232_, InteractionHand p_38233_) {
      InteractionResult ret = super.interact(p_38232_, p_38233_);
      if (ret.consumesAction()) return ret;
      p_38232_.openMenu(this);
      if (!p_38232_.level.isClientSide) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, p_38232_);
         PiglinAi.angerNearbyPiglins(p_38232_, true);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   protected void applyNaturalSlowdown() {
      float f = 0.98F;
      if (this.lootTable == null) {
         int i = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
         f += (float)i * 0.001F;
      }

      if (this.isInWater()) {
         f *= 0.95F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.0D, (double)f));
   }

   public void unpackLootTable(@Nullable Player p_38255_) {
      if (this.lootTable != null && this.level.getServer() != null) {
         LootTable loottable = this.level.getServer().getLootTables().get(this.lootTable);
         if (p_38255_ instanceof ServerPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)p_38255_, this.lootTable);
         }

         this.lootTable = null;
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, this.position()).withOptionalRandomSeed(this.lootTableSeed);
         // Forge: add this entity to loot context, however, currently Vanilla uses 'this' for the player creating the chests. So we take over 'killer_entity' for this.
         lootcontext$builder.withParameter(LootContextParams.KILLER_ENTITY, this);
         if (p_38255_ != null) {
            lootcontext$builder.withLuck(p_38255_.getLuck()).withParameter(LootContextParams.THIS_ENTITY, p_38255_);
         }

         loottable.fill(this, lootcontext$builder.create(LootContextParamSets.CHEST));
      }

   }

   public void clearContent() {
      this.unpackLootTable((Player)null);
      this.itemStacks.clear();
   }

   public void setLootTable(ResourceLocation p_38237_, long p_38238_) {
      this.lootTable = p_38237_;
      this.lootTableSeed = p_38238_;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int p_38251_, Inventory p_38252_, Player p_38253_) {
      if (this.lootTable != null && p_38253_.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(p_38252_.player);
         return this.createMenu(p_38251_, p_38252_);
      }
   }

   protected abstract AbstractContainerMenu createMenu(int p_38222_, Inventory p_38223_);

   // Forge Start
   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
      if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
         return itemHandler.cast();
      return super.getCapability(capability, facing);
   }

   @Override
   public void invalidateCaps() {
      super.invalidateCaps();
      itemHandler.invalidate();
   }

   @Override
   public void reviveCaps() {
      super.reviveCaps();
      itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));
   }
}
