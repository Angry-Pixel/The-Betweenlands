package net.minecraft.world.level.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;

public class CarvedPumpkinBlock extends HorizontalDirectionalBlock implements Wearable {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   @Nullable
   private BlockPattern snowGolemBase;
   @Nullable
   private BlockPattern snowGolemFull;
   @Nullable
   private BlockPattern ironGolemBase;
   @Nullable
   private BlockPattern ironGolemFull;
   private static final Predicate<BlockState> PUMPKINS_PREDICATE = (p_51396_) -> {
      return p_51396_ != null && (p_51396_.is(Blocks.CARVED_PUMPKIN) || p_51396_.is(Blocks.JACK_O_LANTERN));
   };

   public CarvedPumpkinBlock(BlockBehaviour.Properties p_51375_) {
      super(p_51375_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   public void onPlace(BlockState p_51387_, Level p_51388_, BlockPos p_51389_, BlockState p_51390_, boolean p_51391_) {
      if (!p_51390_.is(p_51387_.getBlock())) {
         this.trySpawnGolem(p_51388_, p_51389_);
      }
   }

   public boolean canSpawnGolem(LevelReader p_51382_, BlockPos p_51383_) {
      return this.getOrCreateSnowGolemBase().find(p_51382_, p_51383_) != null || this.getOrCreateIronGolemBase().find(p_51382_, p_51383_) != null;
   }

   private void trySpawnGolem(Level p_51379_, BlockPos p_51380_) {
      BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.getOrCreateSnowGolemFull().find(p_51379_, p_51380_);
      if (blockpattern$blockpatternmatch != null) {
         for(int i = 0; i < this.getOrCreateSnowGolemFull().getHeight(); ++i) {
            BlockInWorld blockinworld = blockpattern$blockpatternmatch.getBlock(0, i, 0);
            p_51379_.setBlock(blockinworld.getPos(), Blocks.AIR.defaultBlockState(), 2);
            p_51379_.levelEvent(2001, blockinworld.getPos(), Block.getId(blockinworld.getState()));
         }

         SnowGolem snowgolem = EntityType.SNOW_GOLEM.create(p_51379_);
         BlockPos blockpos1 = blockpattern$blockpatternmatch.getBlock(0, 2, 0).getPos();
         snowgolem.moveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.05D, (double)blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
         p_51379_.addFreshEntity(snowgolem);

         for(ServerPlayer serverplayer : p_51379_.getEntitiesOfClass(ServerPlayer.class, snowgolem.getBoundingBox().inflate(5.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, snowgolem);
         }

         for(int l = 0; l < this.getOrCreateSnowGolemFull().getHeight(); ++l) {
            BlockInWorld blockinworld3 = blockpattern$blockpatternmatch.getBlock(0, l, 0);
            p_51379_.blockUpdated(blockinworld3.getPos(), Blocks.AIR);
         }
      } else {
         blockpattern$blockpatternmatch = this.getOrCreateIronGolemFull().find(p_51379_, p_51380_);
         if (blockpattern$blockpatternmatch != null) {
            for(int j = 0; j < this.getOrCreateIronGolemFull().getWidth(); ++j) {
               for(int k = 0; k < this.getOrCreateIronGolemFull().getHeight(); ++k) {
                  BlockInWorld blockinworld2 = blockpattern$blockpatternmatch.getBlock(j, k, 0);
                  p_51379_.setBlock(blockinworld2.getPos(), Blocks.AIR.defaultBlockState(), 2);
                  p_51379_.levelEvent(2001, blockinworld2.getPos(), Block.getId(blockinworld2.getState()));
               }
            }

            BlockPos blockpos = blockpattern$blockpatternmatch.getBlock(1, 2, 0).getPos();
            IronGolem irongolem = EntityType.IRON_GOLEM.create(p_51379_);
            irongolem.setPlayerCreated(true);
            irongolem.moveTo((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
            p_51379_.addFreshEntity(irongolem);

            for(ServerPlayer serverplayer1 : p_51379_.getEntitiesOfClass(ServerPlayer.class, irongolem.getBoundingBox().inflate(5.0D))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer1, irongolem);
            }

            for(int i1 = 0; i1 < this.getOrCreateIronGolemFull().getWidth(); ++i1) {
               for(int j1 = 0; j1 < this.getOrCreateIronGolemFull().getHeight(); ++j1) {
                  BlockInWorld blockinworld1 = blockpattern$blockpatternmatch.getBlock(i1, j1, 0);
                  p_51379_.blockUpdated(blockinworld1.getPos(), Blocks.AIR);
               }
            }
         }
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext p_51377_) {
      return this.defaultBlockState().setValue(FACING, p_51377_.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51385_) {
      p_51385_.add(FACING);
   }

   private BlockPattern getOrCreateSnowGolemBase() {
      if (this.snowGolemBase == null) {
         this.snowGolemBase = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowGolemBase;
   }

   private BlockPattern getOrCreateSnowGolemFull() {
      if (this.snowGolemFull == null) {
         this.snowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowGolemFull;
   }

   private BlockPattern getOrCreateIronGolemBase() {
      if (this.ironGolemBase == null) {
         this.ironGolemBase = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return this.ironGolemBase;
   }

   private BlockPattern getOrCreateIronGolemFull() {
      if (this.ironGolemFull == null) {
         this.ironGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return this.ironGolemFull;
   }
}