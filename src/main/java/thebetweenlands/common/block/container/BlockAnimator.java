package thebetweenlands.common.block.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;

public class BlockAnimator extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockAnimator() {
		super(Material.ROCK);
		setHardness(2.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAnimator();
	}


	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if (world.isRemote) {
			return true;
		}
		if (world.getTileEntity(pos) instanceof TileEntityAnimator) {
			TileEntityAnimator animator = (TileEntityAnimator) world.getTileEntity(pos);
			if (!animator.itemAnimated) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, pos.getX(), pos.getY(), pos.getZ());
			} else {
				IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(animator.itemToAnimate);
				if (recipe == null || recipe.onRetrieved(player, pos, animator.itemToAnimate)) {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, pos.getX(), pos.getY(), pos.getZ());
				}
				animator.fuelConsumed = 0;
			}
			animator.itemToAnimate = ItemStack.EMPTY;
			animator.itemAnimated = false;
		}

		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileEntity);
			world.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileEntityAnimator te = (TileEntityAnimator) worldIn.getTileEntity(pos);
		if (te != null && te.isRunning()) {
			int meta = te.getBlockMetadata();

			double xOff = 0;
			double zOff = 0;

			switch (meta) {
			case 0:
				xOff = -0.5F;
				zOff = 0.14F;
				break;
			case 1:
				xOff = -0.14F;
				zOff = -0.5F;
				break;
			case 2:
				xOff = 0.5F;
				zOff = -0.14F;
				break;
			case 3:
				xOff = 0.14F;
				zOff = 0.5F;
				break;
			}

			// Runes
			List<Vec3d> points = new ArrayList<Vec3d>();
			points.add(new Vec3d(te.getPos().getX() + 0.5D + (rand.nextFloat() - 0.5F) * 0.3D + xOff, te.getPos().getY() + 0.9, te.getPos().getZ() + 0.5 + (rand.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vec3d(te.getPos().getX() + 0.5D + (rand.nextFloat() - 0.5F) * 0.3D + xOff, te.getPos().getY() + 1.36, te.getPos().getZ() + 0.5 + (rand.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vec3d(te.getPos().getX() + 0.5D, te.getPos().getY() + 1.45D, te.getPos().getZ() + 0.5D));
			BLParticles.ANIMATOR.spawn(worldIn, te.getPos().getX(), te.getPos().getY() + 0.9, te.getPos().getZ() + 0.65, ParticleArgs.get().withData(points));
			BLParticles.SMOKE.spawn(worldIn, te.getPos().getX() + 0.5 + rand.nextFloat() * 0.3D - 0.15D, te.getPos().getY() + 0.3, te.getPos().getZ() + 0.5 + rand.nextFloat() * 0.3D - 0.15D);
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		TileEntity tileEntityAnimator = world.getTileEntity(pos);
		if (tileEntityAnimator instanceof TileEntityAnimator ) {
			return Math.round(((float) ((TileEntityAnimator) tileEntityAnimator).fuelConsumed / (float) ((TileEntityAnimator) tileEntityAnimator).requiredFuelCount) * 16.0f);
		}
		return 0;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN;
	}
}