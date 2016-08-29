package thebetweenlands.common.block.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;

public class BlockStagnantWater extends BlockFluidClassic implements IStateMappedBlock {
	public BlockStagnantWater() {
		super(FluidRegistry.STAGNANT_WATER, Material.WATER);
		this.setLightLevel(1.0F);
		this.setMaxScaledLight(0);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		//TODO: Requires exliris
		//		if (entity instanceof EntityPlayer && !world.isRemote && !((EntityPlayer)entity).isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect())) {
		//			((EntityPlayer)entity).addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
		//		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(StateMap.Builder builder) {
		builder.ignore(BlockStagnantWater.LEVEL);
	}
}
