package thebetweenlands.common.block.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BlockBLColored;
import thebetweenlands.common.entity.EntityFishingTackleBoxSeat;
import thebetweenlands.common.item.EnumBLDyeColor;

public class BlockReedMatColored extends BlockBLColored {
	protected static final AxisAlignedBB MAT_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
	
	public BlockReedMatColored(Material materialIn, SoundType soundType) {
		super(materialIn, soundType);
		setHardness(0.2F);
		setResistance(5.0F);
	}
	
	@Override
	public String getSubtypeName(int meta) {
		return "reed_mat_colored_" + EnumBLDyeColor.byMetadata(meta).getName();
	}
	
	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(COLOR, EnumBLDyeColor.byMetadata(meta));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return MAT_AABB;
	}

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			if (world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2)) && !isSatOn(world, pos) && side == EnumFacing.UP && !player.isRiding())
				seatPlayer(world, player, pos);
		}
		return true;
	}

	public void seatPlayer(World world, EntityPlayer player, BlockPos pos) {
		EntityFishingTackleBoxSeat entitySeat = new EntityFishingTackleBoxSeat(world);
		entitySeat.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ()  + 0.5D);
		entitySeat.setSeatOffset(-0.45F);
		world.spawnEntity(entitySeat);
		player.startRiding(entitySeat, true);
	}

	private boolean isSatOn(World world, BlockPos pos) {
		AxisAlignedBB detectionbox = new AxisAlignedBB(pos);
		List<EntityFishingTackleBoxSeat> list = world.<EntityFishingTackleBoxSeat>getEntitiesWithinAABB(EntityFishingTackleBoxSeat.class, detectionbox, EntitySelectors.IS_ALIVE);
		for (EntityFishingTackleBoxSeat entity : list) {
			if (!list.isEmpty())
				if (list.get(0) instanceof EntityFishingTackleBoxSeat)
					return true;
		}
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

}
