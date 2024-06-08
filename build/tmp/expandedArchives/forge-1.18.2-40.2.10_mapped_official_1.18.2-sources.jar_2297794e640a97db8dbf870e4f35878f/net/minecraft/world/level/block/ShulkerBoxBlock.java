package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShulkerBoxBlock extends BaseEntityBlock {
   public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
   public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
   @Nullable
   private final DyeColor color;

   public ShulkerBoxBlock(@Nullable DyeColor p_56188_, BlockBehaviour.Properties p_56189_) {
      super(p_56189_);
      this.color = p_56188_;
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
   }

   public BlockEntity newBlockEntity(BlockPos p_154552_, BlockState p_154553_) {
      return new ShulkerBoxBlockEntity(this.color, p_154552_, p_154553_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_154543_, BlockState p_154544_, BlockEntityType<T> p_154545_) {
      return createTickerHelper(p_154545_, BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
   }

   public RenderShape getRenderShape(BlockState p_56255_) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public InteractionResult use(BlockState p_56227_, Level p_56228_, BlockPos p_56229_, Player p_56230_, InteractionHand p_56231_, BlockHitResult p_56232_) {
      if (p_56228_.isClientSide) {
         return InteractionResult.SUCCESS;
      } else if (p_56230_.isSpectator()) {
         return InteractionResult.CONSUME;
      } else {
         BlockEntity blockentity = p_56228_.getBlockEntity(p_56229_);
         if (blockentity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerboxblockentity = (ShulkerBoxBlockEntity)blockentity;
            if (canOpen(p_56227_, p_56228_, p_56229_, shulkerboxblockentity)) {
               p_56230_.openMenu(shulkerboxblockentity);
               p_56230_.awardStat(Stats.OPEN_SHULKER_BOX);
               PiglinAi.angerNearbyPiglins(p_56230_, true);
            }

            return InteractionResult.CONSUME;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   private static boolean canOpen(BlockState p_154547_, Level p_154548_, BlockPos p_154549_, ShulkerBoxBlockEntity p_154550_) {
      if (p_154550_.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
         return true;
      } else {
         AABB aabb = Shulker.getProgressDeltaAabb(p_154547_.getValue(FACING), 0.0F, 0.5F).move(p_154549_).deflate(1.0E-6D);
         return p_154548_.noCollision(aabb);
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_56198_) {
      return this.defaultBlockState().setValue(FACING, p_56198_.getClickedFace());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56249_) {
      p_56249_.add(FACING);
   }

   public void playerWillDestroy(Level p_56212_, BlockPos p_56213_, BlockState p_56214_, Player p_56215_) {
      BlockEntity blockentity = p_56212_.getBlockEntity(p_56213_);
      if (blockentity instanceof ShulkerBoxBlockEntity) {
         ShulkerBoxBlockEntity shulkerboxblockentity = (ShulkerBoxBlockEntity)blockentity;
         if (!p_56212_.isClientSide && p_56215_.isCreative() && !shulkerboxblockentity.isEmpty()) {
            ItemStack itemstack = getColoredItemStack(this.getColor());
            blockentity.saveToItem(itemstack);
            if (shulkerboxblockentity.hasCustomName()) {
               itemstack.setHoverName(shulkerboxblockentity.getCustomName());
            }

            ItemEntity itementity = new ItemEntity(p_56212_, (double)p_56213_.getX() + 0.5D, (double)p_56213_.getY() + 0.5D, (double)p_56213_.getZ() + 0.5D, itemstack);
            itementity.setDefaultPickUpDelay();
            p_56212_.addFreshEntity(itementity);
         } else {
            shulkerboxblockentity.unpackLootTable(p_56215_);
         }
      }

      super.playerWillDestroy(p_56212_, p_56213_, p_56214_, p_56215_);
   }

   public List<ItemStack> getDrops(BlockState p_56246_, LootContext.Builder p_56247_) {
      BlockEntity blockentity = p_56247_.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (blockentity instanceof ShulkerBoxBlockEntity) {
         ShulkerBoxBlockEntity shulkerboxblockentity = (ShulkerBoxBlockEntity)blockentity;
         p_56247_ = p_56247_.withDynamicDrop(CONTENTS, (p_56218_, p_56219_) -> {
            for(int i = 0; i < shulkerboxblockentity.getContainerSize(); ++i) {
               p_56219_.accept(shulkerboxblockentity.getItem(i));
            }

         });
      }

      return super.getDrops(p_56246_, p_56247_);
   }

   public void setPlacedBy(Level p_56206_, BlockPos p_56207_, BlockState p_56208_, LivingEntity p_56209_, ItemStack p_56210_) {
      if (p_56210_.hasCustomHoverName()) {
         BlockEntity blockentity = p_56206_.getBlockEntity(p_56207_);
         if (blockentity instanceof ShulkerBoxBlockEntity) {
            ((ShulkerBoxBlockEntity)blockentity).setCustomName(p_56210_.getHoverName());
         }
      }

   }

   public void onRemove(BlockState p_56234_, Level p_56235_, BlockPos p_56236_, BlockState p_56237_, boolean p_56238_) {
      if (!p_56234_.is(p_56237_.getBlock())) {
         BlockEntity blockentity = p_56235_.getBlockEntity(p_56236_);
         if (blockentity instanceof ShulkerBoxBlockEntity) {
            p_56235_.updateNeighbourForOutputSignal(p_56236_, p_56234_.getBlock());
         }

         super.onRemove(p_56234_, p_56235_, p_56236_, p_56237_, p_56238_);
      }
   }

   public void appendHoverText(ItemStack p_56193_, @Nullable BlockGetter p_56194_, List<Component> p_56195_, TooltipFlag p_56196_) {
      super.appendHoverText(p_56193_, p_56194_, p_56195_, p_56196_);
      CompoundTag compoundtag = BlockItem.getBlockEntityData(p_56193_);
      if (compoundtag != null) {
         if (compoundtag.contains("LootTable", 8)) {
            p_56195_.add(new TextComponent("???????"));
         }

         if (compoundtag.contains("Items", 9)) {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(compoundtag, nonnulllist);
            int i = 0;
            int j = 0;

            for(ItemStack itemstack : nonnulllist) {
               if (!itemstack.isEmpty()) {
                  ++j;
                  if (i <= 4) {
                     ++i;
                     MutableComponent mutablecomponent = itemstack.getHoverName().copy();
                     mutablecomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                     p_56195_.add(mutablecomponent);
                  }
               }
            }

            if (j - i > 0) {
               p_56195_.add((new TranslatableComponent("container.shulkerBox.more", j - i)).withStyle(ChatFormatting.ITALIC));
            }
         }
      }

   }

   public PushReaction getPistonPushReaction(BlockState p_56265_) {
      return PushReaction.DESTROY;
   }

   public VoxelShape getShape(BlockState p_56257_, BlockGetter p_56258_, BlockPos p_56259_, CollisionContext p_56260_) {
      BlockEntity blockentity = p_56258_.getBlockEntity(p_56259_);
      return blockentity instanceof ShulkerBoxBlockEntity ? Shapes.create(((ShulkerBoxBlockEntity)blockentity).getBoundingBox(p_56257_)) : Shapes.block();
   }

   public boolean hasAnalogOutputSignal(BlockState p_56221_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_56223_, Level p_56224_, BlockPos p_56225_) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)p_56224_.getBlockEntity(p_56225_));
   }

   public ItemStack getCloneItemStack(BlockGetter p_56202_, BlockPos p_56203_, BlockState p_56204_) {
      ItemStack itemstack = super.getCloneItemStack(p_56202_, p_56203_, p_56204_);
      p_56202_.getBlockEntity(p_56203_, BlockEntityType.SHULKER_BOX).ifPresent((p_187446_) -> {
         p_187446_.saveToItem(itemstack);
      });
      return itemstack;
   }

   @Nullable
   public static DyeColor getColorFromItem(Item p_56253_) {
      return getColorFromBlock(Block.byItem(p_56253_));
   }

   @Nullable
   public static DyeColor getColorFromBlock(Block p_56263_) {
      return p_56263_ instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)p_56263_).getColor() : null;
   }

   public static Block getBlockByColor(@Nullable DyeColor p_56191_) {
      if (p_56191_ == null) {
         return Blocks.SHULKER_BOX;
      } else {
         switch(p_56191_) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor p_56251_) {
      return new ItemStack(getBlockByColor(p_56251_));
   }

   public BlockState rotate(BlockState p_56243_, Rotation p_56244_) {
      return p_56243_.setValue(FACING, p_56244_.rotate(p_56243_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_56240_, Mirror p_56241_) {
      return p_56240_.rotate(p_56241_.getRotation(p_56240_.getValue(FACING)));
   }
}