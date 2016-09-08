package thebetweenlands.common.block.terrain;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockRubber extends BlockFluidClassic implements IStateMappedBlock {
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
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode)) {
			int level = state.getValue(BlockFluidClassic.LEVEL);
			if(level < 0 || level >= 8)
				return;
			float height = (8 - level) / 8.0F;
			if((pos.getY() + 1) - entity.getEntityBoundingBox().minY >= 1.0F - height)
				entity.setInWeb();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockRubber.LEVEL);
	}
}