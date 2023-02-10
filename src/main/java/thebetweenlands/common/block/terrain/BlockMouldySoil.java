package thebetweenlands.common.block.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.world.storage.location.LocationSporeHive;

public class BlockMouldySoil extends BasicBlock {

	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");

	public BlockMouldySoil() {
		super(Material.GROUND);

		this.setDefaultCreativeTab()
		.setHarvestLevel2("shovel", 0)
		.setSoundType2(SoundType.GROUND)
		.setHardness(0.5F);

		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, false)
				.withProperty(EAST, false)
				.withProperty(SOUTH, false)
				.withProperty(WEST, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { NORTH, EAST, SOUTH, WEST });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	protected BlockPos getAboveGroundPos(IBlockState state, IBlockAccess world, BlockPos pos) {
		return pos.up();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		BlockPos originalPos = pos;
		
		pos = this.getAboveGroundPos(state, worldIn, pos);
		
		PooledMutableBlockPos offset = PooledMutableBlockPos.retain();
		PooledMutableBlockPos offsetDown = PooledMutableBlockPos.retain();

		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			offset.setPos(pos.getX() + facing.getXOffset(), pos.getY(), pos.getZ() + facing.getZOffset());
			IBlockState offsetState = worldIn.getBlockState(offset);

			offsetDown.setPos(pos.getX() + facing.getXOffset(), pos.getY() - 1, pos.getZ() + facing.getZOffset());
			IBlockState offsetDownState = worldIn.getBlockState(offsetDown);

			PropertyBool prop;
			switch(facing) {
			default:
			case NORTH:
				prop = NORTH;
				break;
			case EAST:
				prop = EAST;
				break;
			case SOUTH:
				prop = SOUTH;
				break;
			case WEST:
				prop = WEST;
				break;
			}

			boolean canHaveEdge = false;
			
			if(offsetState.getBlock() instanceof BlockMouldySoil == false && offsetDownState.getBlock() instanceof BlockMouldySoil == false) {
				canHaveEdge = true;
			} else if(offsetState.getBlock() instanceof BlockMouldySoil && ((BlockMouldySoil)offsetState.getBlock()).getAboveGroundPos(offsetState, worldIn, offset).getY() == originalPos.getY() + 1) {
				canHaveEdge = true;
			}
			
			state = state.withProperty(prop, canHaveEdge && offsetDownState.isSideSolid(worldIn, offsetDown, EnumFacing.UP) && offsetDownState.getBlockFaceShape(worldIn, offsetDown, EnumFacing.UP) == BlockFaceShape.SOLID);
		}

		offset.release();
		offsetDown.release();

		return state;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		super.onEntityWalk(worldIn, pos, entityIn);

		if(worldIn.isRemote) {
			this.updateParticle(worldIn, pos, entityIn);
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateParticle(World worldIn, BlockPos pos, Entity entityIn) {
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();

		if(entityIn instanceof EntityPlayer && renderView != null && renderView.getDistanceSq(pos) < 64 && LocationSporeHive.getAtBlock(worldIn, pos) != null) {
			NBTTagCompound nbt = entityIn.getEntityData();

			float nextStep = nbt.getFloat("thebetweenlands.mouldySoil.nextStep");

			if(entityIn.distanceWalkedOnStepModified > nextStep) {
				BLParticles.MOULD_THROBBING.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				nbt.setFloat("thebetweenlands.mouldySoil.nextStep", entityIn.distanceWalkedOnStepModified + 3);
			}
		}
	}

}
