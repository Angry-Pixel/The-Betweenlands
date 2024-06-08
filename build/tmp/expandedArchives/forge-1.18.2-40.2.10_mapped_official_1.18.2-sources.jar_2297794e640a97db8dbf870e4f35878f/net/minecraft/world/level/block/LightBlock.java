package net.minecraft.world.level.block;

import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightBlock extends Block implements SimpleWaterloggedBlock {
   public static final int MAX_LEVEL = 15;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final ToIntFunction<BlockState> LIGHT_EMISSION = (p_153701_) -> {
      return p_153701_.getValue(LEVEL);
   };

   public LightBlock(BlockBehaviour.Properties p_153662_) {
      super(p_153662_);
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(15)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153687_) {
      p_153687_.add(LEVEL, WATERLOGGED);
   }

   public InteractionResult use(BlockState p_153673_, Level p_153674_, BlockPos p_153675_, Player p_153676_, InteractionHand p_153677_, BlockHitResult p_153678_) {
      if (!p_153674_.isClientSide) {
         p_153674_.setBlock(p_153675_, p_153673_.cycle(LEVEL), 2);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.CONSUME;
      }
   }

   public VoxelShape getShape(BlockState p_153668_, BlockGetter p_153669_, BlockPos p_153670_, CollisionContext p_153671_) {
      return p_153671_.isHoldingItem(Items.LIGHT) ? Shapes.block() : Shapes.empty();
   }

   public boolean propagatesSkylightDown(BlockState p_153695_, BlockGetter p_153696_, BlockPos p_153697_) {
      return true;
   }

   public RenderShape getRenderShape(BlockState p_153693_) {
      return RenderShape.INVISIBLE;
   }

   public float getShadeBrightness(BlockState p_153689_, BlockGetter p_153690_, BlockPos p_153691_) {
      return 1.0F;
   }

   public BlockState updateShape(BlockState p_153680_, Direction p_153681_, BlockState p_153682_, LevelAccessor p_153683_, BlockPos p_153684_, BlockPos p_153685_) {
      if (p_153680_.getValue(WATERLOGGED)) {
         p_153683_.scheduleTick(p_153684_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153683_));
      }

      return super.updateShape(p_153680_, p_153681_, p_153682_, p_153683_, p_153684_, p_153685_);
   }

   public FluidState getFluidState(BlockState p_153699_) {
      return p_153699_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_153699_);
   }

   public ItemStack getCloneItemStack(BlockGetter p_153664_, BlockPos p_153665_, BlockState p_153666_) {
      ItemStack itemstack = super.getCloneItemStack(p_153664_, p_153665_, p_153666_);
      if (p_153666_.getValue(LEVEL) != 15) {
         CompoundTag compoundtag = new CompoundTag();
         compoundtag.putString(LEVEL.getName(), String.valueOf((Object)p_153666_.getValue(LEVEL)));
         itemstack.addTagElement("BlockStateTag", compoundtag);
      }

      return itemstack;
   }
}