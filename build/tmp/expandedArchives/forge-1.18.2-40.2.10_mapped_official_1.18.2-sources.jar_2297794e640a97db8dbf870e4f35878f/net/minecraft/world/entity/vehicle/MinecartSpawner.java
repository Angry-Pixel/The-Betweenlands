package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartSpawner extends AbstractMinecart {
   private final BaseSpawner spawner = new BaseSpawner() {
      public void broadcastEvent(Level p_150342_, BlockPos p_150343_, int p_150344_) {
         p_150342_.broadcastEntityEvent(MinecartSpawner.this, (byte)p_150344_);
      }

      @Override
      @javax.annotation.Nullable
      public net.minecraft.world.entity.Entity getSpawnerEntity() {
         return MinecartSpawner.this;
      }
   };
   private final Runnable ticker;

   public MinecartSpawner(EntityType<? extends MinecartSpawner> p_38623_, Level p_38624_) {
      super(p_38623_, p_38624_);
      this.ticker = this.createTicker(p_38624_);
   }

   public MinecartSpawner(Level p_38626_, double p_38627_, double p_38628_, double p_38629_) {
      super(EntityType.SPAWNER_MINECART, p_38626_, p_38627_, p_38628_, p_38629_);
      this.ticker = this.createTicker(p_38626_);
   }

   private Runnable createTicker(Level p_150335_) {
      return p_150335_ instanceof ServerLevel ? () -> {
         this.spawner.serverTick((ServerLevel)p_150335_, this.blockPosition());
      } : () -> {
         this.spawner.clientTick(p_150335_, this.blockPosition());
      };
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.SPAWNER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.SPAWNER.defaultBlockState();
   }

   protected void readAdditionalSaveData(CompoundTag p_38633_) {
      super.readAdditionalSaveData(p_38633_);
      this.spawner.load(this.level, this.blockPosition(), p_38633_);
   }

   protected void addAdditionalSaveData(CompoundTag p_38635_) {
      super.addAdditionalSaveData(p_38635_);
      this.spawner.save(p_38635_);
   }

   public void handleEntityEvent(byte p_38631_) {
      this.spawner.onEventTriggered(this.level, p_38631_);
   }

   public void tick() {
      super.tick();
      this.ticker.run();
   }

   public BaseSpawner getSpawner() {
      return this.spawner;
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }
}
