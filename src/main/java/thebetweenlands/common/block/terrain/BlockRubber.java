package thebetweenlands.common.block.terrain;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockRubber extends BlockFluidClassic implements IStateMappedBlock, ICustomItemBlock {
	public BlockRubber() {
		super(FluidRegistry.RUBBER, BLMaterialRegistry.RUBBER);
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.displaceIfPossible(world, pos);
	}

	@Override
	public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
		if(materialIn == Material.WATER) {
			double liquidHeight = (double)((float)(blockpos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)state.getValue(BlockLiquid.LEVEL)).intValue()));
			if(testingHead) {
				double liquidHeightBelow = 0;
				if(world.getBlockState(blockpos.up()).getBlock() == state.getBlock()) {
					liquidHeightBelow = (double)((float)(blockpos.getY() + 2) - BlockLiquid.getLiquidHeightPercent(((Integer)world.getBlockState(blockpos.up()).getValue(BlockLiquid.LEVEL)).intValue()));
				}
				return entity.posY + entity.getEyeHeight() < 0.1D + liquidHeight || entity.posY + entity.getEyeHeight() < 0.1D + liquidHeightBelow;
			} else {
				return entity.getEntityBoundingBox().maxY >= liquidHeight && entity.getEntityBoundingBox().minY < liquidHeight;
			}
		}
		return null;
	}

	@Override
	public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
		BlockPos entityPos = new BlockPos(entityIn.posX, entityIn.posY + 0.5D, entityIn.posZ);
		if(pos.equals(entityPos) /*make sure it's only changed once*/) {
			entityIn.motionX *= 0.35D;
			entityIn.motionY *= 0.8D;
			entityIn.motionY -= 0.01D;
			entityIn.motionZ *= 0.35D;
		}
		return new Vec3d(0, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockRubber.LEVEL);
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}