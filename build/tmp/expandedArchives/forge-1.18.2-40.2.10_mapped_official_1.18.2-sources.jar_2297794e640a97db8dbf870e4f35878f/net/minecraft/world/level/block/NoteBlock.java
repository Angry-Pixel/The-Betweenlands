package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock extends Block {
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

   public NoteBlock(BlockBehaviour.Properties p_55016_) {
      super(p_55016_);
      this.registerDefaultState(this.stateDefinition.any().setValue(INSTRUMENT, NoteBlockInstrument.HARP).setValue(NOTE, Integer.valueOf(0)).setValue(POWERED, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_55018_) {
      return this.defaultBlockState().setValue(INSTRUMENT, NoteBlockInstrument.byState(p_55018_.getLevel().getBlockState(p_55018_.getClickedPos().below())));
   }

   public BlockState updateShape(BlockState p_55048_, Direction p_55049_, BlockState p_55050_, LevelAccessor p_55051_, BlockPos p_55052_, BlockPos p_55053_) {
      return p_55049_ == Direction.DOWN ? p_55048_.setValue(INSTRUMENT, NoteBlockInstrument.byState(p_55050_)) : super.updateShape(p_55048_, p_55049_, p_55050_, p_55051_, p_55052_, p_55053_);
   }

   public void neighborChanged(BlockState p_55041_, Level p_55042_, BlockPos p_55043_, Block p_55044_, BlockPos p_55045_, boolean p_55046_) {
      boolean flag = p_55042_.hasNeighborSignal(p_55043_);
      if (flag != p_55041_.getValue(POWERED)) {
         if (flag) {
            this.playNote(p_55042_, p_55043_);
         }

         p_55042_.setBlock(p_55043_, p_55041_.setValue(POWERED, Boolean.valueOf(flag)), 3);
      }

   }

   private void playNote(Level p_55020_, BlockPos p_55021_) {
      if (p_55020_.getBlockState(p_55021_.above()).isAir()) {
         p_55020_.blockEvent(p_55021_, this, 0, 0);
      }

   }

   public InteractionResult use(BlockState p_55034_, Level p_55035_, BlockPos p_55036_, Player p_55037_, InteractionHand p_55038_, BlockHitResult p_55039_) {
      if (p_55035_.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         int _new = net.minecraftforge.common.ForgeHooks.onNoteChange(p_55035_, p_55036_, p_55034_, p_55034_.getValue(NOTE), p_55034_.cycle(NOTE).getValue(NOTE));
         if (_new == -1) return InteractionResult.FAIL;
         p_55034_ = p_55034_.setValue(NOTE, _new);
         p_55035_.setBlock(p_55036_, p_55034_, 3);
         this.playNote(p_55035_, p_55036_);
         p_55037_.awardStat(Stats.TUNE_NOTEBLOCK);
         return InteractionResult.CONSUME;
      }
   }

   public void attack(BlockState p_55029_, Level p_55030_, BlockPos p_55031_, Player p_55032_) {
      if (!p_55030_.isClientSide) {
         this.playNote(p_55030_, p_55031_);
         p_55032_.awardStat(Stats.PLAY_NOTEBLOCK);
      }
   }

   public boolean triggerEvent(BlockState p_55023_, Level p_55024_, BlockPos p_55025_, int p_55026_, int p_55027_) {
      net.minecraftforge.event.world.NoteBlockEvent.Play e = new net.minecraftforge.event.world.NoteBlockEvent.Play(p_55024_, p_55025_, p_55023_, p_55023_.getValue(NOTE), p_55023_.getValue(INSTRUMENT));
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e)) return false;
      p_55023_ = p_55023_.setValue(NOTE, e.getVanillaNoteId()).setValue(INSTRUMENT, e.getInstrument());
      int i = p_55023_.getValue(NOTE);
      float f = (float)Math.pow(2.0D, (double)(i - 12) / 12.0D);
      p_55024_.playSound((Player)null, p_55025_, p_55023_.getValue(INSTRUMENT).getSoundEvent(), SoundSource.RECORDS, 3.0F, f);
      p_55024_.addParticle(ParticleTypes.NOTE, (double)p_55025_.getX() + 0.5D, (double)p_55025_.getY() + 1.2D, (double)p_55025_.getZ() + 0.5D, (double)i / 24.0D, 0.0D, 0.0D);
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55055_) {
      p_55055_.add(INSTRUMENT, POWERED, NOTE);
   }
}
