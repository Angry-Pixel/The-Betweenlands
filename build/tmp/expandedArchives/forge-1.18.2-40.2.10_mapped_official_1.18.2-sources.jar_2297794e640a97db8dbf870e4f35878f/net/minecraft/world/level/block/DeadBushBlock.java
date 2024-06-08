package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeadBushBlock extends BushBlock implements net.minecraftforge.common.IForgeShearable {
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   public DeadBushBlock(BlockBehaviour.Properties p_52417_) {
      super(p_52417_);
   }

   public VoxelShape getShape(BlockState p_52419_, BlockGetter p_52420_, BlockPos p_52421_, CollisionContext p_52422_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_52424_, BlockGetter p_52425_, BlockPos p_52426_) {
      return p_52424_.is(Blocks.SAND) || p_52424_.is(Blocks.RED_SAND) || p_52424_.is(Blocks.TERRACOTTA) || p_52424_.is(Blocks.WHITE_TERRACOTTA) || p_52424_.is(Blocks.ORANGE_TERRACOTTA) || p_52424_.is(Blocks.MAGENTA_TERRACOTTA) || p_52424_.is(Blocks.LIGHT_BLUE_TERRACOTTA) || p_52424_.is(Blocks.YELLOW_TERRACOTTA) || p_52424_.is(Blocks.LIME_TERRACOTTA) || p_52424_.is(Blocks.PINK_TERRACOTTA) || p_52424_.is(Blocks.GRAY_TERRACOTTA) || p_52424_.is(Blocks.LIGHT_GRAY_TERRACOTTA) || p_52424_.is(Blocks.CYAN_TERRACOTTA) || p_52424_.is(Blocks.PURPLE_TERRACOTTA) || p_52424_.is(Blocks.BLUE_TERRACOTTA) || p_52424_.is(Blocks.BROWN_TERRACOTTA) || p_52424_.is(Blocks.GREEN_TERRACOTTA) || p_52424_.is(Blocks.RED_TERRACOTTA) || p_52424_.is(Blocks.BLACK_TERRACOTTA) || p_52424_.is(BlockTags.DIRT);
   }
}
