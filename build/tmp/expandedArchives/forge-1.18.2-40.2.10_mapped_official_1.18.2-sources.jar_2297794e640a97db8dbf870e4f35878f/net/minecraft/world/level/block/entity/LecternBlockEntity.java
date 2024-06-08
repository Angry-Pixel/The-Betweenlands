package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LecternBlockEntity extends BlockEntity implements Clearable, MenuProvider {
   public static final int DATA_PAGE = 0;
   public static final int NUM_DATA = 1;
   public static final int SLOT_BOOK = 0;
   public static final int NUM_SLOTS = 1;
   private final Container bookAccess = new Container() {
      public int getContainerSize() {
         return 1;
      }

      public boolean isEmpty() {
         return LecternBlockEntity.this.book.isEmpty();
      }

      public ItemStack getItem(int p_59580_) {
         return p_59580_ == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
      }

      public ItemStack removeItem(int p_59582_, int p_59583_) {
         if (p_59582_ == 0) {
            ItemStack itemstack = LecternBlockEntity.this.book.split(p_59583_);
            if (LecternBlockEntity.this.book.isEmpty()) {
               LecternBlockEntity.this.onBookItemRemove();
            }

            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public ItemStack removeItemNoUpdate(int p_59590_) {
         if (p_59590_ == 0) {
            ItemStack itemstack = LecternBlockEntity.this.book;
            LecternBlockEntity.this.book = ItemStack.EMPTY;
            LecternBlockEntity.this.onBookItemRemove();
            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public void setItem(int p_59585_, ItemStack p_59586_) {
      }

      public int getMaxStackSize() {
         return 1;
      }

      public void setChanged() {
         LecternBlockEntity.this.setChanged();
      }

      public boolean stillValid(Player p_59588_) {
         if (LecternBlockEntity.this.level.getBlockEntity(LecternBlockEntity.this.worldPosition) != LecternBlockEntity.this) {
            return false;
         } else {
            return p_59588_.distanceToSqr((double)LecternBlockEntity.this.worldPosition.getX() + 0.5D, (double)LecternBlockEntity.this.worldPosition.getY() + 0.5D, (double)LecternBlockEntity.this.worldPosition.getZ() + 0.5D) > 64.0D ? false : LecternBlockEntity.this.hasBook();
         }
      }

      public boolean canPlaceItem(int p_59592_, ItemStack p_59593_) {
         return false;
      }

      public void clearContent() {
      }
   };
   private final ContainerData dataAccess = new ContainerData() {
      public int get(int p_59600_) {
         return p_59600_ == 0 ? LecternBlockEntity.this.page : 0;
      }

      public void set(int p_59602_, int p_59603_) {
         if (p_59602_ == 0) {
            LecternBlockEntity.this.setPage(p_59603_);
         }

      }

      public int getCount() {
         return 1;
      }
   };
   ItemStack book = ItemStack.EMPTY;
   int page;
   private int pageCount;

   public LecternBlockEntity(BlockPos p_155622_, BlockState p_155623_) {
      super(BlockEntityType.LECTERN, p_155622_, p_155623_);
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean hasBook() {
      return this.book.is(Items.WRITABLE_BOOK) || this.book.is(Items.WRITTEN_BOOK);
   }

   public void setBook(ItemStack p_59537_) {
      this.setBook(p_59537_, (Player)null);
   }

   void onBookItemRemove() {
      this.page = 0;
      this.pageCount = 0;
      LecternBlock.resetBookState(this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
   }

   public void setBook(ItemStack p_59539_, @Nullable Player p_59540_) {
      this.book = this.resolveBook(p_59539_, p_59540_);
      this.page = 0;
      this.pageCount = WrittenBookItem.getPageCount(this.book);
      this.setChanged();
   }

   void setPage(int p_59533_) {
      int i = Mth.clamp(p_59533_, 0, this.pageCount - 1);
      if (i != this.page) {
         this.page = i;
         this.setChanged();
         LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   public int getPage() {
      return this.page;
   }

   public int getRedstoneSignal() {
      float f = this.pageCount > 1 ? (float)this.getPage() / ((float)this.pageCount - 1.0F) : 1.0F;
      return Mth.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
   }

   private ItemStack resolveBook(ItemStack p_59555_, @Nullable Player p_59556_) {
      if (this.level instanceof ServerLevel && p_59555_.is(Items.WRITTEN_BOOK)) {
         WrittenBookItem.resolveBookComponents(p_59555_, this.createCommandSourceStack(p_59556_), p_59556_);
      }

      return p_59555_;
   }

   private CommandSourceStack createCommandSourceStack(@Nullable Player p_59535_) {
      String s;
      Component component;
      if (p_59535_ == null) {
         s = "Lectern";
         component = new TextComponent("Lectern");
      } else {
         s = p_59535_.getName().getString();
         component = p_59535_.getDisplayName();
      }

      Vec3 vec3 = Vec3.atCenterOf(this.worldPosition);
      return new CommandSourceStack(CommandSource.NULL, vec3, Vec2.ZERO, (ServerLevel)this.level, 2, s, component, this.level.getServer(), p_59535_);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public void load(CompoundTag p_155625_) {
      super.load(p_155625_);
      if (p_155625_.contains("Book", 10)) {
         this.book = this.resolveBook(ItemStack.of(p_155625_.getCompound("Book")), (Player)null);
      } else {
         this.book = ItemStack.EMPTY;
      }

      this.pageCount = WrittenBookItem.getPageCount(this.book);
      this.page = Mth.clamp(p_155625_.getInt("Page"), 0, this.pageCount - 1);
   }

   protected void saveAdditional(CompoundTag p_187509_) {
      super.saveAdditional(p_187509_);
      if (!this.getBook().isEmpty()) {
         p_187509_.put("Book", this.getBook().save(new CompoundTag()));
         p_187509_.putInt("Page", this.page);
      }

   }

   public void clearContent() {
      this.setBook(ItemStack.EMPTY);
   }

   public AbstractContainerMenu createMenu(int p_59562_, Inventory p_59563_, Player p_59564_) {
      return new LecternMenu(p_59562_, this.bookAccess, this.dataAccess);
   }

   public Component getDisplayName() {
      return new TranslatableComponent("container.lectern");
   }
}