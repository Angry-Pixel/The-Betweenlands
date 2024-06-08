package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
   public static final int MOVE_ITEM_SPEED = 8;
   public static final int HOPPER_CONTAINER_SIZE = 5;
   private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
   private int cooldownTime = -1;
   private long tickedGameTime;

   public HopperBlockEntity(BlockPos p_155550_, BlockState p_155551_) {
      super(BlockEntityType.HOPPER, p_155550_, p_155551_);
   }

   public void load(CompoundTag p_155588_) {
      super.load(p_155588_);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(p_155588_)) {
         ContainerHelper.loadAllItems(p_155588_, this.items);
      }

      this.cooldownTime = p_155588_.getInt("TransferCooldown");
   }

   protected void saveAdditional(CompoundTag p_187502_) {
      super.saveAdditional(p_187502_);
      if (!this.trySaveLootTable(p_187502_)) {
         ContainerHelper.saveAllItems(p_187502_, this.items);
      }

      p_187502_.putInt("TransferCooldown", this.cooldownTime);
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public ItemStack removeItem(int p_59309_, int p_59310_) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.removeItem(this.getItems(), p_59309_, p_59310_);
   }

   public void setItem(int p_59315_, ItemStack p_59316_) {
      this.unpackLootTable((Player)null);
      this.getItems().set(p_59315_, p_59316_);
      if (p_59316_.getCount() > this.getMaxStackSize()) {
         p_59316_.setCount(this.getMaxStackSize());
      }

   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.hopper");
   }

   public static void pushItemsTick(Level p_155574_, BlockPos p_155575_, BlockState p_155576_, HopperBlockEntity p_155577_) {
      --p_155577_.cooldownTime;
      p_155577_.tickedGameTime = p_155574_.getGameTime();
      if (!p_155577_.isOnCooldown()) {
         p_155577_.setCooldown(0);
         tryMoveItems(p_155574_, p_155575_, p_155576_, p_155577_, () -> {
            return suckInItems(p_155574_, p_155577_);
         });
      }

   }

   private static boolean tryMoveItems(Level p_155579_, BlockPos p_155580_, BlockState p_155581_, HopperBlockEntity p_155582_, BooleanSupplier p_155583_) {
      if (p_155579_.isClientSide) {
         return false;
      } else {
         if (!p_155582_.isOnCooldown() && p_155581_.getValue(HopperBlock.ENABLED)) {
            boolean flag = false;
            if (!p_155582_.isEmpty()) {
               flag = ejectItems(p_155579_, p_155580_, p_155581_, p_155582_);
            }

            if (!p_155582_.inventoryFull()) {
               flag |= p_155583_.getAsBoolean();
            }

            if (flag) {
               p_155582_.setCooldown(8);
               setChanged(p_155579_, p_155580_, p_155581_);
               return true;
            }
         }

         return false;
      }
   }

   private boolean inventoryFull() {
      for(ItemStack itemstack : this.items) {
         if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private static boolean ejectItems(Level p_155563_, BlockPos p_155564_, BlockState p_155565_, HopperBlockEntity p_155566_) {
      if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(p_155566_)) return true;
      Container container = getAttachedContainer(p_155563_, p_155564_, p_155565_);
      if (container == null) {
         return false;
      } else {
         Direction direction = p_155565_.getValue(HopperBlock.FACING).getOpposite();
         if (isFullContainer(container, direction)) {
            return false;
         } else {
            for(int i = 0; i < p_155566_.getContainerSize(); ++i) {
               if (!p_155566_.getItem(i).isEmpty()) {
                  ItemStack itemstack = p_155566_.getItem(i).copy();
                  ItemStack itemstack1 = addItem(p_155566_, container, p_155566_.removeItem(i, 1), direction);
                  if (itemstack1.isEmpty()) {
                     container.setChanged();
                     return true;
                  }

                  p_155566_.setItem(i, itemstack);
               }
            }

            return false;
         }
      }
   }

   private static IntStream getSlots(Container p_59340_, Direction p_59341_) {
      return p_59340_ instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer)p_59340_).getSlotsForFace(p_59341_)) : IntStream.range(0, p_59340_.getContainerSize());
   }

   private static boolean isFullContainer(Container p_59386_, Direction p_59387_) {
      return getSlots(p_59386_, p_59387_).allMatch((p_59379_) -> {
         ItemStack itemstack = p_59386_.getItem(p_59379_);
         return itemstack.getCount() >= itemstack.getMaxStackSize();
      });
   }

   private static boolean isEmptyContainer(Container p_59398_, Direction p_59399_) {
      return getSlots(p_59398_, p_59399_).allMatch((p_59319_) -> {
         return p_59398_.getItem(p_59319_).isEmpty();
      });
   }

   public static boolean suckInItems(Level p_155553_, Hopper p_155554_) {
      Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(p_155553_, p_155554_);
      if (ret != null) return ret;
      Container container = getSourceContainer(p_155553_, p_155554_);
      if (container != null) {
         Direction direction = Direction.DOWN;
         return isEmptyContainer(container, direction) ? false : getSlots(container, direction).anyMatch((p_59363_) -> {
            return tryTakeInItemFromSlot(p_155554_, container, p_59363_, direction);
         });
      } else {
         for(ItemEntity itementity : getItemsAtAndAbove(p_155553_, p_155554_)) {
            if (addItem(p_155554_, itementity)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean tryTakeInItemFromSlot(Hopper p_59355_, Container p_59356_, int p_59357_, Direction p_59358_) {
      ItemStack itemstack = p_59356_.getItem(p_59357_);
      if (!itemstack.isEmpty() && canTakeItemFromContainer(p_59356_, itemstack, p_59357_, p_59358_)) {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = addItem(p_59356_, p_59355_, p_59356_.removeItem(p_59357_, 1), (Direction)null);
         if (itemstack2.isEmpty()) {
            p_59356_.setChanged();
            return true;
         }

         p_59356_.setItem(p_59357_, itemstack1);
      }

      return false;
   }

   public static boolean addItem(Container p_59332_, ItemEntity p_59333_) {
      boolean flag = false;
      ItemStack itemstack = p_59333_.getItem().copy();
      ItemStack itemstack1 = addItem((Container)null, p_59332_, itemstack, (Direction)null);
      if (itemstack1.isEmpty()) {
         flag = true;
         p_59333_.discard();
      } else {
         p_59333_.setItem(itemstack1);
      }

      return flag;
   }

   public static ItemStack addItem(@Nullable Container p_59327_, Container p_59328_, ItemStack p_59329_, @Nullable Direction p_59330_) {
      if (p_59328_ instanceof WorldlyContainer && p_59330_ != null) {
         WorldlyContainer worldlycontainer = (WorldlyContainer)p_59328_;
         int[] aint = worldlycontainer.getSlotsForFace(p_59330_);

         for(int k = 0; k < aint.length && !p_59329_.isEmpty(); ++k) {
            p_59329_ = tryMoveInItem(p_59327_, p_59328_, p_59329_, aint[k], p_59330_);
         }
      } else {
         int i = p_59328_.getContainerSize();

         for(int j = 0; j < i && !p_59329_.isEmpty(); ++j) {
            p_59329_ = tryMoveInItem(p_59327_, p_59328_, p_59329_, j, p_59330_);
         }
      }

      return p_59329_;
   }

   private static boolean canPlaceItemInContainer(Container p_59335_, ItemStack p_59336_, int p_59337_, @Nullable Direction p_59338_) {
      if (!p_59335_.canPlaceItem(p_59337_, p_59336_)) {
         return false;
      } else {
         return !(p_59335_ instanceof WorldlyContainer) || ((WorldlyContainer)p_59335_).canPlaceItemThroughFace(p_59337_, p_59336_, p_59338_);
      }
   }

   private static boolean canTakeItemFromContainer(Container p_59381_, ItemStack p_59382_, int p_59383_, Direction p_59384_) {
      return !(p_59381_ instanceof WorldlyContainer) || ((WorldlyContainer)p_59381_).canTakeItemThroughFace(p_59383_, p_59382_, p_59384_);
   }

   private static ItemStack tryMoveInItem(@Nullable Container p_59321_, Container p_59322_, ItemStack p_59323_, int p_59324_, @Nullable Direction p_59325_) {
      ItemStack itemstack = p_59322_.getItem(p_59324_);
      if (canPlaceItemInContainer(p_59322_, p_59323_, p_59324_, p_59325_)) {
         boolean flag = false;
         boolean flag1 = p_59322_.isEmpty();
         if (itemstack.isEmpty()) {
            p_59322_.setItem(p_59324_, p_59323_);
            p_59323_ = ItemStack.EMPTY;
            flag = true;
         } else if (canMergeItems(itemstack, p_59323_)) {
            int i = p_59323_.getMaxStackSize() - itemstack.getCount();
            int j = Math.min(p_59323_.getCount(), i);
            p_59323_.shrink(j);
            itemstack.grow(j);
            flag = j > 0;
         }

         if (flag) {
            if (flag1 && p_59322_ instanceof HopperBlockEntity) {
               HopperBlockEntity hopperblockentity1 = (HopperBlockEntity)p_59322_;
               if (!hopperblockentity1.isOnCustomCooldown()) {
                  int k = 0;
                  if (p_59321_ instanceof HopperBlockEntity) {
                     HopperBlockEntity hopperblockentity = (HopperBlockEntity)p_59321_;
                     if (hopperblockentity1.tickedGameTime >= hopperblockentity.tickedGameTime) {
                        k = 1;
                     }
                  }

                  hopperblockentity1.setCooldown(8 - k);
               }
            }

            p_59322_.setChanged();
         }
      }

      return p_59323_;
   }

   @Nullable
   private static Container getAttachedContainer(Level p_155593_, BlockPos p_155594_, BlockState p_155595_) {
      Direction direction = p_155595_.getValue(HopperBlock.FACING);
      return getContainerAt(p_155593_, p_155594_.relative(direction));
   }

   @Nullable
   private static Container getSourceContainer(Level p_155597_, Hopper p_155598_) {
      return getContainerAt(p_155597_, p_155598_.getLevelX(), p_155598_.getLevelY() + 1.0D, p_155598_.getLevelZ());
   }

   public static List<ItemEntity> getItemsAtAndAbove(Level p_155590_, Hopper p_155591_) {
      return p_155591_.getSuckShape().toAabbs().stream().flatMap((p_155558_) -> {
         return p_155590_.getEntitiesOfClass(ItemEntity.class, p_155558_.move(p_155591_.getLevelX() - 0.5D, p_155591_.getLevelY() - 0.5D, p_155591_.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static Container getContainerAt(Level p_59391_, BlockPos p_59392_) {
      return getContainerAt(p_59391_, (double)p_59392_.getX() + 0.5D, (double)p_59392_.getY() + 0.5D, (double)p_59392_.getZ() + 0.5D);
   }

   @Nullable
   private static Container getContainerAt(Level p_59348_, double p_59349_, double p_59350_, double p_59351_) {
      Container container = null;
      BlockPos blockpos = new BlockPos(p_59349_, p_59350_, p_59351_);
      BlockState blockstate = p_59348_.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block instanceof WorldlyContainerHolder) {
         container = ((WorldlyContainerHolder)block).getContainer(blockstate, p_59348_, blockpos);
      } else if (blockstate.hasBlockEntity()) {
         BlockEntity blockentity = p_59348_.getBlockEntity(blockpos);
         if (blockentity instanceof Container) {
            container = (Container)blockentity;
            if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
               container = ChestBlock.getContainer((ChestBlock)block, blockstate, p_59348_, blockpos, true);
            }
         }
      }

      if (container == null) {
         List<Entity> list = p_59348_.getEntities((Entity)null, new AABB(p_59349_ - 0.5D, p_59350_ - 0.5D, p_59351_ - 0.5D, p_59349_ + 0.5D, p_59350_ + 0.5D, p_59351_ + 0.5D), EntitySelector.CONTAINER_ENTITY_SELECTOR);
         if (!list.isEmpty()) {
            container = (Container)list.get(p_59348_.random.nextInt(list.size()));
         }
      }

      return container;
   }

   private static boolean canMergeItems(ItemStack p_59345_, ItemStack p_59346_) {
      if (!p_59345_.is(p_59346_.getItem())) {
         return false;
      } else if (p_59345_.getDamageValue() != p_59346_.getDamageValue()) {
         return false;
      } else if (p_59345_.getCount() > p_59345_.getMaxStackSize()) {
         return false;
      } else {
         return ItemStack.tagMatches(p_59345_, p_59346_);
      }
   }

   public double getLevelX() {
      return (double)this.worldPosition.getX() + 0.5D;
   }

   public double getLevelY() {
      return (double)this.worldPosition.getY() + 0.5D;
   }

   public double getLevelZ() {
      return (double)this.worldPosition.getZ() + 0.5D;
   }

   public void setCooldown(int p_59396_) {
      this.cooldownTime = p_59396_;
   }

   private boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   public boolean isOnCustomCooldown() {
      return this.cooldownTime > 8;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> p_59371_) {
      this.items = p_59371_;
   }

   public static void entityInside(Level p_155568_, BlockPos p_155569_, BlockState p_155570_, Entity p_155571_, HopperBlockEntity p_155572_) {
      if (p_155571_ instanceof ItemEntity && Shapes.joinIsNotEmpty(Shapes.create(p_155571_.getBoundingBox().move((double)(-p_155569_.getX()), (double)(-p_155569_.getY()), (double)(-p_155569_.getZ()))), p_155572_.getSuckShape(), BooleanOp.AND)) {
         tryMoveItems(p_155568_, p_155569_, p_155570_, p_155572_, () -> {
            return addItem(p_155572_, (ItemEntity)p_155571_);
         });
      }

   }

   protected AbstractContainerMenu createMenu(int p_59312_, Inventory p_59313_) {
      return new HopperMenu(p_59312_, p_59313_, this);
   }

   @Override
   protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
      return new net.minecraftforge.items.VanillaHopperItemHandler(this);
   }

   public long getLastUpdateTime() {
      return this.tickedGameTime;
   }
}
