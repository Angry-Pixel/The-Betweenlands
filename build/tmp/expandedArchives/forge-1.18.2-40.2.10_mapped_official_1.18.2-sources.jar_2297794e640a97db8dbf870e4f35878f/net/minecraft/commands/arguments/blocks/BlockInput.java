package net.minecraft.commands.arguments.blocks;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockInput implements Predicate<BlockInWorld> {
   private final BlockState state;
   private final Set<Property<?>> properties;
   @Nullable
   private final CompoundTag tag;

   public BlockInput(BlockState p_114666_, Set<Property<?>> p_114667_, @Nullable CompoundTag p_114668_) {
      this.state = p_114666_;
      this.properties = p_114667_;
      this.tag = p_114668_;
   }

   public BlockState getState() {
      return this.state;
   }

   public Set<Property<?>> getDefinedProperties() {
      return this.properties;
   }

   public boolean test(BlockInWorld p_114675_) {
      BlockState blockstate = p_114675_.getState();
      if (!blockstate.is(this.state.getBlock())) {
         return false;
      } else {
         for(Property<?> property : this.properties) {
            if (blockstate.getValue(property) != this.state.getValue(property)) {
               return false;
            }
         }

         if (this.tag == null) {
            return true;
         } else {
            BlockEntity blockentity = p_114675_.getEntity();
            return blockentity != null && NbtUtils.compareNbt(this.tag, blockentity.saveWithFullMetadata(), true);
         }
      }
   }

   public boolean test(ServerLevel p_173524_, BlockPos p_173525_) {
      return this.test(new BlockInWorld(p_173524_, p_173525_, false));
   }

   public boolean place(ServerLevel p_114671_, BlockPos p_114672_, int p_114673_) {
      BlockState blockstate = Block.updateFromNeighbourShapes(this.state, p_114671_, p_114672_);
      if (blockstate.isAir()) {
         blockstate = this.state;
      }

      if (!p_114671_.setBlock(p_114672_, blockstate, p_114673_)) {
         return false;
      } else {
         if (this.tag != null) {
            BlockEntity blockentity = p_114671_.getBlockEntity(p_114672_);
            if (blockentity != null) {
               blockentity.load(this.tag);
            }
         }

         return true;
      }
   }
}