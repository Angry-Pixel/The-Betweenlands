package thebetweenlands.common.blocks;

import java.util.Random;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsGrassBlock extends GrassBlock implements BlockColor {

	public BetweenlandsGrassBlock(Properties p_49795_) {
		super(p_49795_);
	}

	private static boolean canBeGrass(BlockState p_56824_, LevelReader p_56825_, BlockPos p_56826_) {
	      BlockPos blockpos = p_56826_.above();
	      BlockState blockstate = p_56825_.getBlockState(blockpos);
	      if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
	         return true;
	      } else if (blockstate.getFluidState().getAmount() == 8) {
	         return false;
	      } else {
	         int i = LayerLightEngine.getLightBlockInto(p_56825_, p_56824_, p_56826_, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(p_56825_, blockpos));
	         return i < p_56825_.getMaxLightLevel();
	      }
	}
	
	private static boolean canPropagate(BlockState p_56828_, LevelReader p_56829_, BlockPos p_56830_) {
	      BlockPos blockpos = p_56830_.above();
	      return canBeGrass(p_56828_, p_56829_, p_56830_) && !p_56829_.getFluidState(blockpos).is(FluidTags.WATER);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState p_56819_, ServerLevel p_56820_, BlockPos p_56821_, Random p_56822_) {
	      if (!canBeGrass(p_56819_, p_56820_, p_56821_)) {
	         if (!p_56820_.isAreaLoaded(p_56821_, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
	         p_56820_.setBlockAndUpdate(p_56821_, BlockRegistry.SWAMP_DIRT.get().defaultBlockState());
	      } else {
	         if (p_56820_.getMaxLocalRawBrightness(p_56821_.above()) >= 9) {
	            BlockState blockstate = this.defaultBlockState();

	            for(int i = 0; i < 4; ++i) {
	               BlockPos blockpos = p_56821_.offset(p_56822_.nextInt(3) - 1, p_56822_.nextInt(5) - 3, p_56822_.nextInt(3) - 1);
	               if (p_56820_.getBlockState(blockpos).is(BlockRegistry.SWAMP_DIRT.get()) && canPropagate(blockstate, p_56820_, blockpos)) {
	                  p_56820_.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, p_56820_.getBlockState(blockpos.above()).is(Blocks.SNOW)));
	               }
	            }
	         }

	     }
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		net.minecraftforge.common.PlantType type = plantable.getPlantType(world, pos.relative(facing));
		return net.minecraftforge.common.PlantType.PLAINS.equals(type);
	}

	// TODO: Change block tint colors but not particles
	@Override
	public int getColor(BlockState p_92567_, BlockAndTintGetter p_92568_, BlockPos p_92569_, int p_92570_) {
		return BiomeColors.getAverageGrassColor(p_92568_, p_92569_) | 0xFF000000;
	}
}