package net.minecraft.world.level.block.entity;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandBlockEntity extends BlockEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private final BaseCommandBlock commandBlock = new BaseCommandBlock() {
      public void setCommand(String p_59157_) {
         super.setCommand(p_59157_);
         CommandBlockEntity.this.setChanged();
      }

      public ServerLevel getLevel() {
         return (ServerLevel)CommandBlockEntity.this.level;
      }

      public void onUpdated() {
         BlockState blockstate = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
         this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, blockstate, blockstate, 3);
      }

      public Vec3 getPosition() {
         return Vec3.atCenterOf(CommandBlockEntity.this.worldPosition);
      }

      public CommandSourceStack createCommandSourceStack() {
         return new CommandSourceStack(this, Vec3.atCenterOf(CommandBlockEntity.this.worldPosition), Vec2.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), (Entity)null);
      }
   };

   public CommandBlockEntity(BlockPos p_155380_, BlockState p_155381_) {
      super(BlockEntityType.COMMAND_BLOCK, p_155380_, p_155381_);
   }

   protected void saveAdditional(CompoundTag p_187491_) {
      super.saveAdditional(p_187491_);
      this.commandBlock.save(p_187491_);
      p_187491_.putBoolean("powered", this.isPowered());
      p_187491_.putBoolean("conditionMet", this.wasConditionMet());
      p_187491_.putBoolean("auto", this.isAutomatic());
   }

   public void load(CompoundTag p_155383_) {
      super.load(p_155383_);
      this.commandBlock.load(p_155383_);
      this.powered = p_155383_.getBoolean("powered");
      this.conditionMet = p_155383_.getBoolean("conditionMet");
      this.setAutomatic(p_155383_.getBoolean("auto"));
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   public void setPowered(boolean p_59136_) {
      this.powered = p_59136_;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAutomatic() {
      return this.auto;
   }

   public void setAutomatic(boolean p_59138_) {
      boolean flag = this.auto;
      this.auto = p_59138_;
      if (!flag && p_59138_ && !this.powered && this.level != null && this.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
         this.scheduleTick();
      }

   }

   public void onModeSwitch() {
      CommandBlockEntity.Mode commandblockentity$mode = this.getMode();
      if (commandblockentity$mode == CommandBlockEntity.Mode.AUTO && (this.powered || this.auto) && this.level != null) {
         this.scheduleTick();
      }

   }

   private void scheduleTick() {
      Block block = this.getBlockState().getBlock();
      if (block instanceof CommandBlock) {
         this.markConditionMet();
         this.level.scheduleTick(this.worldPosition, block, 1);
      }

   }

   public boolean wasConditionMet() {
      return this.conditionMet;
   }

   public boolean markConditionMet() {
      this.conditionMet = true;
      if (this.isConditional()) {
         BlockPos blockpos = this.worldPosition.relative(this.level.getBlockState(this.worldPosition).getValue(CommandBlock.FACING).getOpposite());
         if (this.level.getBlockState(blockpos).getBlock() instanceof CommandBlock) {
            BlockEntity blockentity = this.level.getBlockEntity(blockpos);
            this.conditionMet = blockentity instanceof CommandBlockEntity && ((CommandBlockEntity)blockentity).getCommandBlock().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public CommandBlockEntity.Mode getMode() {
      BlockState blockstate = this.getBlockState();
      if (blockstate.is(Blocks.COMMAND_BLOCK)) {
         return CommandBlockEntity.Mode.REDSTONE;
      } else if (blockstate.is(Blocks.REPEATING_COMMAND_BLOCK)) {
         return CommandBlockEntity.Mode.AUTO;
      } else {
         return blockstate.is(Blocks.CHAIN_COMMAND_BLOCK) ? CommandBlockEntity.Mode.SEQUENCE : CommandBlockEntity.Mode.REDSTONE;
      }
   }

   public boolean isConditional() {
      BlockState blockstate = this.level.getBlockState(this.getBlockPos());
      return blockstate.getBlock() instanceof CommandBlock ? blockstate.getValue(CommandBlock.CONDITIONAL) : false;
   }

   public static enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE;
   }
}