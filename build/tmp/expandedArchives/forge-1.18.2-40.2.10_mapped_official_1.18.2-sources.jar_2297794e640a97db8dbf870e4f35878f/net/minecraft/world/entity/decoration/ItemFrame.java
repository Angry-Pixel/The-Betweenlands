package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ItemFrame extends HangingEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
   public static final int NUM_ROTATIONS = 8;
   private float dropChance = 1.0F;
   private boolean fixed;

   public ItemFrame(EntityType<? extends ItemFrame> p_31761_, Level p_31762_) {
      super(p_31761_, p_31762_);
   }

   public ItemFrame(Level p_31764_, BlockPos p_31765_, Direction p_31766_) {
      this(EntityType.ITEM_FRAME, p_31764_, p_31765_, p_31766_);
   }

   public ItemFrame(EntityType<? extends ItemFrame> p_149621_, Level p_149622_, BlockPos p_149623_, Direction p_149624_) {
      super(p_149621_, p_149622_, p_149623_);
      this.setDirection(p_149624_);
   }

   protected float getEyeHeight(Pose p_31784_, EntityDimensions p_31785_) {
      return 0.0F;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
      this.getEntityData().define(DATA_ROTATION, 0);
   }

   protected void setDirection(Direction p_31793_) {
      Validate.notNull(p_31793_);
      this.direction = p_31793_;
      if (p_31793_.getAxis().isHorizontal()) {
         this.setXRot(0.0F);
         this.setYRot((float)(this.direction.get2DDataValue() * 90));
      } else {
         this.setXRot((float)(-90 * p_31793_.getAxisDirection().getStep()));
         this.setYRot(0.0F);
      }

      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double d0 = 0.46875D;
         double d1 = (double)this.pos.getX() + 0.5D - (double)this.direction.getStepX() * 0.46875D;
         double d2 = (double)this.pos.getY() + 0.5D - (double)this.direction.getStepY() * 0.46875D;
         double d3 = (double)this.pos.getZ() + 0.5D - (double)this.direction.getStepZ() * 0.46875D;
         this.setPosRaw(d1, d2, d3);
         double d4 = (double)this.getWidth();
         double d5 = (double)this.getHeight();
         double d6 = (double)this.getWidth();
         Direction.Axis direction$axis = this.direction.getAxis();
         switch(direction$axis) {
         case X:
            d4 = 1.0D;
            break;
         case Y:
            d5 = 1.0D;
            break;
         case Z:
            d6 = 1.0D;
         }

         d4 /= 32.0D;
         d5 /= 32.0D;
         d6 /= 32.0D;
         this.setBoundingBox(new AABB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6));
      }
   }

   public boolean survives() {
      if (this.fixed) {
         return true;
      } else if (!this.level.noCollision(this)) {
         return false;
      } else {
         BlockState blockstate = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
         return blockstate.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate) ? this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty() : false;
      }
   }

   public void move(MoverType p_31781_, Vec3 p_31782_) {
      if (!this.fixed) {
         super.move(p_31781_, p_31782_);
      }

   }

   public void push(double p_31817_, double p_31818_, double p_31819_) {
      if (!this.fixed) {
         super.push(p_31817_, p_31818_, p_31819_);
      }

   }

   public float getPickRadius() {
      return 0.0F;
   }

   public void kill() {
      this.removeFramedMap(this.getItem());
      super.kill();
   }

   public boolean hurt(DamageSource p_31776_, float p_31777_) {
      if (this.fixed) {
         return p_31776_ != DamageSource.OUT_OF_WORLD && !p_31776_.isCreativePlayer() ? false : super.hurt(p_31776_, p_31777_);
      } else if (this.isInvulnerableTo(p_31776_)) {
         return false;
      } else if (!p_31776_.isExplosion() && !this.getItem().isEmpty()) {
         if (!this.level.isClientSide) {
            this.dropItem(p_31776_.getEntity(), false);
            this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.hurt(p_31776_, p_31777_);
      }
   }

   public SoundEvent getRemoveItemSound() {
      return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
   }

   public int getWidth() {
      return 12;
   }

   public int getHeight() {
      return 12;
   }

   public boolean shouldRenderAtSqrDistance(double p_31769_) {
      double d0 = 16.0D;
      d0 *= 64.0D * getViewScale();
      return p_31769_ < d0 * d0;
   }

   public void dropItem(@Nullable Entity p_31779_) {
      this.playSound(this.getBreakSound(), 1.0F, 1.0F);
      this.dropItem(p_31779_, true);
   }

   public SoundEvent getBreakSound() {
      return SoundEvents.ITEM_FRAME_BREAK;
   }

   public void playPlacementSound() {
      this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
   }

   public SoundEvent getPlaceSound() {
      return SoundEvents.ITEM_FRAME_PLACE;
   }

   private void dropItem(@Nullable Entity p_31803_, boolean p_31804_) {
      if (!this.fixed) {
         ItemStack itemstack = this.getItem();
         this.setItem(ItemStack.EMPTY);
         if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (p_31803_ == null) {
               this.removeFramedMap(itemstack);
            }

         } else {
            if (p_31803_ instanceof Player) {
               Player player = (Player)p_31803_;
               if (player.getAbilities().instabuild) {
                  this.removeFramedMap(itemstack);
                  return;
               }
            }

            if (p_31804_) {
               this.spawnAtLocation(this.getFrameItemStack());
            }

            if (!itemstack.isEmpty()) {
               itemstack = itemstack.copy();
               this.removeFramedMap(itemstack);
               if (this.random.nextFloat() < this.dropChance) {
                  this.spawnAtLocation(itemstack);
               }
            }

         }
      }
   }

   private void removeFramedMap(ItemStack p_31811_) {
      if (p_31811_.is(Items.FILLED_MAP)) {
         MapItemSavedData mapitemsaveddata = MapItem.getSavedData(p_31811_, this.level);
         if (mapitemsaveddata != null) {
            mapitemsaveddata.removedFromFrame(this.pos, this.getId());
            mapitemsaveddata.setDirty(true);
         }
      }

      p_31811_.setEntityRepresentation((Entity)null);
   }

   public ItemStack getItem() {
      return this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack p_31806_) {
      this.setItem(p_31806_, true);
   }

   public void setItem(ItemStack p_31790_, boolean p_31791_) {
      if (!p_31790_.isEmpty()) {
         p_31790_ = p_31790_.copy();
         p_31790_.setCount(1);
         p_31790_.setEntityRepresentation(this);
      }

      this.getEntityData().set(DATA_ITEM, p_31790_);
      if (!p_31790_.isEmpty()) {
         this.playSound(this.getAddItemSound(), 1.0F, 1.0F);
      }

      if (p_31791_ && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.ITEM_FRAME_ADD_ITEM;
   }

   public SlotAccess getSlot(int p_149629_) {
      return p_149629_ == 0 ? new SlotAccess() {
         public ItemStack get() {
            return ItemFrame.this.getItem();
         }

         public boolean set(ItemStack p_149635_) {
            ItemFrame.this.setItem(p_149635_);
            return true;
         }
      } : super.getSlot(p_149629_);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_31797_) {
      if (p_31797_.equals(DATA_ITEM)) {
         ItemStack itemstack = this.getItem();
         if (!itemstack.isEmpty() && itemstack.getFrame() != this) {
            itemstack.setEntityRepresentation(this);
         }
      }

   }

   public int getRotation() {
      return this.getEntityData().get(DATA_ROTATION);
   }

   public void setRotation(int p_31771_) {
      this.setRotation(p_31771_, true);
   }

   private void setRotation(int p_31773_, boolean p_31774_) {
      this.getEntityData().set(DATA_ROTATION, p_31773_ % 8);
      if (p_31774_ && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public void addAdditionalSaveData(CompoundTag p_31808_) {
      super.addAdditionalSaveData(p_31808_);
      if (!this.getItem().isEmpty()) {
         p_31808_.put("Item", this.getItem().save(new CompoundTag()));
         p_31808_.putByte("ItemRotation", (byte)this.getRotation());
         p_31808_.putFloat("ItemDropChance", this.dropChance);
      }

      p_31808_.putByte("Facing", (byte)this.direction.get3DDataValue());
      p_31808_.putBoolean("Invisible", this.isInvisible());
      p_31808_.putBoolean("Fixed", this.fixed);
   }

   public void readAdditionalSaveData(CompoundTag p_31795_) {
      super.readAdditionalSaveData(p_31795_);
      CompoundTag compoundtag = p_31795_.getCompound("Item");
      if (compoundtag != null && !compoundtag.isEmpty()) {
         ItemStack itemstack = ItemStack.of(compoundtag);
         if (itemstack.isEmpty()) {
            LOGGER.warn("Unable to load item from: {}", (Object)compoundtag);
         }

         ItemStack itemstack1 = this.getItem();
         if (!itemstack1.isEmpty() && !ItemStack.matches(itemstack, itemstack1)) {
            this.removeFramedMap(itemstack1);
         }

         this.setItem(itemstack, false);
         this.setRotation(p_31795_.getByte("ItemRotation"), false);
         if (p_31795_.contains("ItemDropChance", 99)) {
            this.dropChance = p_31795_.getFloat("ItemDropChance");
         }
      }

      this.setDirection(Direction.from3DDataValue(p_31795_.getByte("Facing")));
      this.setInvisible(p_31795_.getBoolean("Invisible"));
      this.fixed = p_31795_.getBoolean("Fixed");
   }

   public InteractionResult interact(Player p_31787_, InteractionHand p_31788_) {
      ItemStack itemstack = p_31787_.getItemInHand(p_31788_);
      boolean flag = !this.getItem().isEmpty();
      boolean flag1 = !itemstack.isEmpty();
      if (this.fixed) {
         return InteractionResult.PASS;
      } else if (!this.level.isClientSide) {
         if (!flag) {
            if (flag1 && !this.isRemoved()) {
               if (itemstack.is(Items.FILLED_MAP)) {
                  MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, this.level);
                  if (mapitemsaveddata != null && mapitemsaveddata.isTrackedCountOverLimit(256)) {
                     return InteractionResult.FAIL;
                  }
               }

               this.setItem(itemstack);
               if (!p_31787_.getAbilities().instabuild) {
                  itemstack.shrink(1);
               }
            }
         } else {
            this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
         }

         return InteractionResult.CONSUME;
      } else {
         return !flag && !flag1 ? InteractionResult.PASS : InteractionResult.SUCCESS;
      }
   }

   public SoundEvent getRotateItemSound() {
      return SoundEvents.ITEM_FRAME_ROTATE_ITEM;
   }

   public int getAnalogOutput() {
      return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket p_149626_) {
      super.recreateFromPacket(p_149626_);
      this.setDirection(Direction.from3DDataValue(p_149626_.getData()));
   }

   public ItemStack getPickResult() {
      ItemStack itemstack = this.getItem();
      return itemstack.isEmpty() ? this.getFrameItemStack() : itemstack.copy();
   }

   protected ItemStack getFrameItemStack() {
      return new ItemStack(Items.ITEM_FRAME);
   }
}