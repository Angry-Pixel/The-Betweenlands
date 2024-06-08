package net.minecraft.server.level;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

public class ServerPlayerGameMode {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected ServerLevel level;
   protected final ServerPlayer player;
   private GameType gameModeForPlayer = GameType.DEFAULT_MODE;
   @Nullable
   private GameType previousGameModeForPlayer;
   private boolean isDestroyingBlock;
   private int destroyProgressStart;
   private BlockPos destroyPos = BlockPos.ZERO;
   private int gameTicks;
   private boolean hasDelayedDestroy;
   private BlockPos delayedDestroyPos = BlockPos.ZERO;
   private int delayedTickStart;
   private int lastSentState = -1;

   public ServerPlayerGameMode(ServerPlayer p_143472_) {
      this.player = p_143472_;
      this.level = p_143472_.getLevel();
   }

   public boolean changeGameModeForPlayer(GameType p_143474_) {
      if (p_143474_ == this.gameModeForPlayer) {
         return false;
      } else {
         this.setGameModeForPlayer(p_143474_, this.gameModeForPlayer);
         return true;
      }
   }

   protected void setGameModeForPlayer(GameType p_9274_, @Nullable GameType p_9275_) {
      this.previousGameModeForPlayer = p_9275_;
      this.gameModeForPlayer = p_9274_;
      p_9274_.updatePlayerAbilities(this.player.getAbilities());
      this.player.onUpdateAbilities();
      this.player.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, this.player));
      this.level.updateSleepingPlayerList();
   }

   public GameType getGameModeForPlayer() {
      return this.gameModeForPlayer;
   }

   @Nullable
   public GameType getPreviousGameModeForPlayer() {
      return this.previousGameModeForPlayer;
   }

   public boolean isSurvival() {
      return this.gameModeForPlayer.isSurvival();
   }

   public boolean isCreative() {
      return this.gameModeForPlayer.isCreative();
   }

   public void tick() {
      ++this.gameTicks;
      if (this.hasDelayedDestroy) {
         BlockState blockstate = this.level.getBlockState(this.delayedDestroyPos);
         if (blockstate.isAir()) {
            this.hasDelayedDestroy = false;
         } else {
            float f = this.incrementDestroyProgress(blockstate, this.delayedDestroyPos, this.delayedTickStart);
            if (f >= 1.0F) {
               this.hasDelayedDestroy = false;
               this.destroyBlock(this.delayedDestroyPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         BlockState blockstate1 = this.level.getBlockState(this.destroyPos);
         if (blockstate1.isAir()) {
            this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
            this.lastSentState = -1;
            this.isDestroyingBlock = false;
         } else {
            this.incrementDestroyProgress(blockstate1, this.destroyPos, this.destroyProgressStart);
         }
      }

   }

   private float incrementDestroyProgress(BlockState p_9277_, BlockPos p_9278_, int p_9279_) {
      int i = this.gameTicks - p_9279_;
      float f = p_9277_.getDestroyProgress(this.player, this.player.level, p_9278_) * (float)(i + 1);
      int j = (int)(f * 10.0F);
      if (j != this.lastSentState) {
         this.level.destroyBlockProgress(this.player.getId(), p_9278_, j);
         this.lastSentState = j;
      }

      return f;
   }

   public void handleBlockBreakAction(BlockPos p_9282_, ServerboundPlayerActionPacket.Action p_9283_, Direction p_9284_, int p_9285_) {
      double d0 = this.player.getX() - ((double)p_9282_.getX() + 0.5D);
      double d1 = this.player.getY() - ((double)p_9282_.getY() + 0.5D) + 1.5D;
      double d2 = this.player.getZ() - ((double)p_9282_.getZ() + 0.5D);
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(player, p_9282_, p_9284_);
      if (event.isCanceled() || (!this.isCreative() && event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY)) { // Restore block and te data
         player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, level.getBlockState(p_9282_), p_9283_, false, "mod canceled"));
         level.sendBlockUpdated(p_9282_, level.getBlockState(p_9282_), level.getBlockState(p_9282_), 3);
         return;
      }
      if (!this.player.canInteractWith(p_9282_, 1)) {
         BlockState blockstate1;
         if (this.player.level.getServer() != null && this.player.chunkPosition().getChessboardDistance(new ChunkPos(p_9282_)) < this.player.level.getServer().getPlayerList().getViewDistance()) {
            blockstate1 = this.level.getBlockState(p_9282_);
         } else {
            blockstate1 = Blocks.AIR.defaultBlockState();
         }

         this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, blockstate1, p_9283_, false, "too far"));
      } else if (p_9282_.getY() >= p_9285_) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, this.level.getBlockState(p_9282_), p_9283_, false, "too high"));
      } else {
         if (p_9283_ == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, p_9282_)) {
               this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, this.level.getBlockState(p_9282_), p_9283_, false, "may not interact"));
               return;
            }

            if (this.isCreative()) {
               this.destroyAndAck(p_9282_, p_9283_, "creative destroy");
               return;
            }

            if (this.player.blockActionRestricted(this.level, p_9282_, this.gameModeForPlayer)) {
               this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, this.level.getBlockState(p_9282_), p_9283_, false, "block action restricted"));
               return;
            }

            this.destroyProgressStart = this.gameTicks;
            float f = 1.0F;
            BlockState blockstate = this.level.getBlockState(p_9282_);
            if (!blockstate.isAir()) {
               if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY)
               blockstate.attack(this.level, p_9282_, this.player);
               f = blockstate.getDestroyProgress(this.player, this.player.level, p_9282_);
            }

            if (!blockstate.isAir() && f >= 1.0F) {
               this.destroyAndAck(p_9282_, p_9283_, "insta mine");
            } else {
               if (this.isDestroyingBlock) {
                  this.player.connection.send(new ClientboundBlockBreakAckPacket(this.destroyPos, this.level.getBlockState(this.destroyPos), ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
               }

               this.isDestroyingBlock = true;
               this.destroyPos = p_9282_.immutable();
               int i = (int)(f * 10.0F);
               this.level.destroyBlockProgress(this.player.getId(), p_9282_, i);
               this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, this.level.getBlockState(p_9282_), p_9283_, true, "actual start of destroying"));
               this.lastSentState = i;
            }
         } else if (p_9283_ == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            if (p_9282_.equals(this.destroyPos)) {
               int j = this.gameTicks - this.destroyProgressStart;
               BlockState blockstate2 = this.level.getBlockState(p_9282_);
               if (!blockstate2.isAir()) {
                  float f1 = blockstate2.getDestroyProgress(this.player, this.player.level, p_9282_) * (float)(j + 1);
                  if (f1 >= 0.7F) {
                     this.isDestroyingBlock = false;
                     this.level.destroyBlockProgress(this.player.getId(), p_9282_, -1);
                     this.destroyAndAck(p_9282_, p_9283_, "destroyed");
                     return;
                  }

                  if (!this.hasDelayedDestroy) {
                     this.isDestroyingBlock = false;
                     this.hasDelayedDestroy = true;
                     this.delayedDestroyPos = p_9282_;
                     this.delayedTickStart = this.destroyProgressStart;
                  }
               }
            }

            this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, this.level.getBlockState(p_9282_), p_9283_, true, "stopped destroying"));
         } else if (p_9283_ == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            if (!Objects.equals(this.destroyPos, p_9282_)) {
               LOGGER.warn("Mismatch in destroy block pos: {} {}", this.destroyPos, p_9282_);
               this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
               this.player.connection.send(new ClientboundBlockBreakAckPacket(this.destroyPos, this.level.getBlockState(this.destroyPos), p_9283_, true, "aborted mismatched destroying"));
            }

            this.level.destroyBlockProgress(this.player.getId(), p_9282_, -1);
            this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9282_, this.level.getBlockState(p_9282_), p_9283_, true, "aborted destroying"));
         }

      }
   }

   public void destroyAndAck(BlockPos p_9287_, ServerboundPlayerActionPacket.Action p_9288_, String p_9289_) {
      if (this.destroyBlock(p_9287_)) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9287_, this.level.getBlockState(p_9287_), p_9288_, true, p_9289_));
      } else {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(p_9287_, this.level.getBlockState(p_9287_), p_9288_, false, p_9289_));
      }

   }

   public boolean destroyBlock(BlockPos p_9281_) {
      BlockState blockstate = this.level.getBlockState(p_9281_);
      int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(level, gameModeForPlayer, player, p_9281_);
      if (exp == -1) {
         return false;
      } else {
         BlockEntity blockentity = this.level.getBlockEntity(p_9281_);
         Block block = blockstate.getBlock();
         if (block instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(p_9281_, blockstate, blockstate, 3);
            return false;
         } else if (player.getMainHandItem().onBlockStartBreak(p_9281_, player)) {
            return false;
         } else if (this.player.blockActionRestricted(this.level, p_9281_, this.gameModeForPlayer)) {
            return false;
         } else {
            if (this.isCreative()) {
               removeBlock(p_9281_, false);
               return true;
            } else {
               ItemStack itemstack = this.player.getMainHandItem();
               ItemStack itemstack1 = itemstack.copy();
               boolean flag1 = blockstate.canHarvestBlock(this.level, p_9281_, this.player); // previously player.hasCorrectToolForDrops(blockstate)
               itemstack.mineBlock(this.level, blockstate, p_9281_, this.player);
               if (itemstack.isEmpty() && !itemstack1.isEmpty())
                  net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this.player, itemstack1, InteractionHand.MAIN_HAND);
               boolean flag = removeBlock(p_9281_, flag1);

               if (flag && flag1) {
                  block.playerDestroy(this.level, this.player, p_9281_, blockstate, blockentity, itemstack1);
               }

               if (flag && exp > 0)
                  blockstate.getBlock().popExperience(level, p_9281_, exp);

               return true;
            }
         }
      }
   }

   private boolean removeBlock(BlockPos p_180235_1_, boolean canHarvest) {
      BlockState state = this.level.getBlockState(p_180235_1_);
      boolean removed = state.onDestroyedByPlayer(this.level, p_180235_1_, this.player, canHarvest, this.level.getFluidState(p_180235_1_));
      if (removed)
         state.getBlock().destroy(this.level, p_180235_1_, state);
      return removed;
   }

   public InteractionResult useItem(ServerPlayer p_9262_, Level p_9263_, ItemStack p_9264_, InteractionHand p_9265_) {
      if (this.gameModeForPlayer == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else if (p_9262_.getCooldowns().isOnCooldown(p_9264_.getItem())) {
         return InteractionResult.PASS;
      } else {
         InteractionResult cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(p_9262_, p_9265_);
         if (cancelResult != null) return cancelResult;
         int i = p_9264_.getCount();
         int j = p_9264_.getDamageValue();
         InteractionResultHolder<ItemStack> interactionresultholder = p_9264_.use(p_9263_, p_9262_, p_9265_);
         ItemStack itemstack = interactionresultholder.getObject();
         if (itemstack == p_9264_ && itemstack.getCount() == i && itemstack.getUseDuration() <= 0 && itemstack.getDamageValue() == j) {
            return interactionresultholder.getResult();
         } else if (interactionresultholder.getResult() == InteractionResult.FAIL && itemstack.getUseDuration() > 0 && !p_9262_.isUsingItem()) {
            return interactionresultholder.getResult();
         } else {
            p_9262_.setItemInHand(p_9265_, itemstack);
            if (this.isCreative()) {
               itemstack.setCount(i);
               if (itemstack.isDamageableItem() && itemstack.getDamageValue() != j) {
                  itemstack.setDamageValue(j);
               }
            }

            if (itemstack.isEmpty()) {
               p_9262_.setItemInHand(p_9265_, ItemStack.EMPTY);
            }

            if (!p_9262_.isUsingItem()) {
               p_9262_.inventoryMenu.sendAllDataToRemote();
            }

            return interactionresultholder.getResult();
         }
      }
   }

   public InteractionResult useItemOn(ServerPlayer p_9266_, Level p_9267_, ItemStack p_9268_, InteractionHand p_9269_, BlockHitResult p_9270_) {
      BlockPos blockpos = p_9270_.getBlockPos();
      BlockState blockstate = p_9267_.getBlockState(blockpos);
      net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks.onRightClickBlock(p_9266_, p_9269_, blockpos, p_9270_);
      if (event.isCanceled()) return event.getCancellationResult();
      if (this.gameModeForPlayer == GameType.SPECTATOR) {
         MenuProvider menuprovider = blockstate.getMenuProvider(p_9267_, blockpos);
         if (menuprovider != null) {
            p_9266_.openMenu(menuprovider);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         UseOnContext useoncontext = new UseOnContext(p_9266_, p_9269_, p_9270_);
         if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
            InteractionResult result = p_9268_.onItemUseFirst(useoncontext);
            if (result != InteractionResult.PASS) return result;
         }
         boolean flag = !p_9266_.getMainHandItem().isEmpty() || !p_9266_.getOffhandItem().isEmpty();
         boolean flag1 = (p_9266_.isSecondaryUseActive() && flag) && !(p_9266_.getMainHandItem().doesSneakBypassUse(p_9267_, blockpos, p_9266_) && p_9266_.getOffhandItem().doesSneakBypassUse(p_9267_, blockpos, p_9266_));
         ItemStack itemstack = p_9268_.copy();
         if (event.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY && !flag1)) {
            InteractionResult interactionresult = blockstate.use(p_9267_, p_9266_, p_9269_, p_9270_);
            if (interactionresult.consumesAction()) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(p_9266_, blockpos, itemstack);
               return interactionresult;
            }
         }

         if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (!p_9268_.isEmpty() && !p_9266_.getCooldowns().isOnCooldown(p_9268_.getItem()))) {
            if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) return InteractionResult.PASS;
            InteractionResult interactionresult1;
            if (this.isCreative()) {
               int i = p_9268_.getCount();
               interactionresult1 = p_9268_.useOn(useoncontext);
               p_9268_.setCount(i);
            } else {
               interactionresult1 = p_9268_.useOn(useoncontext);
            }

            if (interactionresult1.consumesAction()) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(p_9266_, blockpos, itemstack);
            }

            return interactionresult1;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public void setLevel(ServerLevel p_9261_) {
      this.level = p_9261_;
   }
}
