package net.minecraft.world.entity.vehicle;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartCommandBlock extends AbstractMinecart {
   static final EntityDataAccessor<String> DATA_ID_COMMAND_NAME = SynchedEntityData.defineId(MinecartCommandBlock.class, EntityDataSerializers.STRING);
   static final EntityDataAccessor<Component> DATA_ID_LAST_OUTPUT = SynchedEntityData.defineId(MinecartCommandBlock.class, EntityDataSerializers.COMPONENT);
   private final BaseCommandBlock commandBlock = new MinecartCommandBlock.MinecartCommandBase();
   private static final int ACTIVATION_DELAY = 4;
   private int lastActivated;

   public MinecartCommandBlock(EntityType<? extends MinecartCommandBlock> p_38509_, Level p_38510_) {
      super(p_38509_, p_38510_);
   }

   public MinecartCommandBlock(Level p_38512_, double p_38513_, double p_38514_, double p_38515_) {
      super(EntityType.COMMAND_BLOCK_MINECART, p_38512_, p_38513_, p_38514_, p_38515_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_ID_COMMAND_NAME, "");
      this.getEntityData().define(DATA_ID_LAST_OUTPUT, TextComponent.EMPTY);
   }

   protected void readAdditionalSaveData(CompoundTag p_38525_) {
      super.readAdditionalSaveData(p_38525_);
      this.commandBlock.load(p_38525_);
      this.getEntityData().set(DATA_ID_COMMAND_NAME, this.getCommandBlock().getCommand());
      this.getEntityData().set(DATA_ID_LAST_OUTPUT, this.getCommandBlock().getLastOutput());
   }

   protected void addAdditionalSaveData(CompoundTag p_38529_) {
      super.addAdditionalSaveData(p_38529_);
      this.commandBlock.save(p_38529_);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.COMMAND_BLOCK;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.COMMAND_BLOCK.defaultBlockState();
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   public void activateMinecart(int p_38517_, int p_38518_, int p_38519_, boolean p_38520_) {
      if (p_38520_ && this.tickCount - this.lastActivated >= 4) {
         this.getCommandBlock().performCommand(this.level);
         this.lastActivated = this.tickCount;
      }

   }

   public InteractionResult interact(Player p_38522_, InteractionHand p_38523_) {
      InteractionResult ret = super.interact(p_38522_, p_38523_);
      if (ret.consumesAction()) return ret;
      return this.commandBlock.usedBy(p_38522_);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_38527_) {
      super.onSyncedDataUpdated(p_38527_);
      if (DATA_ID_LAST_OUTPUT.equals(p_38527_)) {
         try {
            this.commandBlock.setLastOutput(this.getEntityData().get(DATA_ID_LAST_OUTPUT));
         } catch (Throwable throwable) {
         }
      } else if (DATA_ID_COMMAND_NAME.equals(p_38527_)) {
         this.commandBlock.setCommand(this.getEntityData().get(DATA_ID_COMMAND_NAME));
      }

   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public class MinecartCommandBase extends BaseCommandBlock {
      public ServerLevel getLevel() {
         return (ServerLevel)MinecartCommandBlock.this.level;
      }

      public void onUpdated() {
         MinecartCommandBlock.this.getEntityData().set(MinecartCommandBlock.DATA_ID_COMMAND_NAME, this.getCommand());
         MinecartCommandBlock.this.getEntityData().set(MinecartCommandBlock.DATA_ID_LAST_OUTPUT, this.getLastOutput());
      }

      public Vec3 getPosition() {
         return MinecartCommandBlock.this.position();
      }

      public MinecartCommandBlock getMinecart() {
         return MinecartCommandBlock.this;
      }

      public CommandSourceStack createCommandSourceStack() {
         return new CommandSourceStack(this, MinecartCommandBlock.this.position(), MinecartCommandBlock.this.getRotationVector(), this.getLevel(), 2, this.getName().getString(), MinecartCommandBlock.this.getDisplayName(), this.getLevel().getServer(), MinecartCommandBlock.this);
      }
   }
}
