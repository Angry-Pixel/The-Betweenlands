package thebetweenlands.common.block.plant;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.IConnectedTextureBlock;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.item.ItemWaterPlaceable;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockAlgae extends BlockPlant implements IConnectedTextureBlock, ICustomItemBlock {
	protected static final AxisAlignedBB ALGAE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1D, 0.08D, 1D);

	public BlockAlgae() {
		this.setReplaceable(true);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if(entityIn instanceof EntityWeedwoodRowboat == false) {
			entityIn.motionX *= 0.95D;
			entityIn.motionZ *= 0.95D;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return ALGAE_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		//No solid collision
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getMaterial() == Material.WATER;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			IBlockState iblockstate = worldIn.getBlockState(pos.down());
			Material material = iblockstate.getMaterial();
			return material == Material.WATER && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess iblockaccess, BlockPos pos, EnumFacing side) {
		Block block = iblockaccess.getBlockState(pos.offset(side)).getBlock();
		return block != BlockRegistry.ALGAE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[0]));
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;
		return this.getExtendedConnectedTextureState(state, worldIn, pos, p -> {
			return p.getY() <= pos.getY() && (worldIn.getBlockState(p).getBlock() == this || worldIn.getBlockState(p.down()).isFullCube());
		}, false);
	}

	@Override
	public boolean isFaceConnectedTexture(EnumFacing face) {
		return face == EnumFacing.UP;
	}

	@Override
	public ItemBlock getItemBlock() {
		return new ItemWaterPlaceable(this);
	}
}
