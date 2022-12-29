package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.tile.TileEntityCompostBin;


public class BlockCompostBin extends BasicBlock implements ITileEntityProvider {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockCompostBin() {
		super(Material.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().rotateYCCW());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().rotateYCCW()), 2);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCompostBin();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byIndex(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer playerIn) {
		if(!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntityCompostBin) {
				TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(pos);
				tile.setOpen(!tile.isOpen());
				world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				tile.markDirty();
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);

		if (!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntityCompostBin) {
				TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(pos);
				boolean open = tile.isOpen();

				boolean interacted = false;
				
				if(open) {
					if(tile.getCompostedAmount() > 0) {
						if (tile.removeCompost(TileEntityCompostBin.COMPOST_PER_ITEM)) {
							world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, EnumItemMisc.COMPOST.create(1)));
							interacted = true;
						}
					}
					
					if(!interacted && !heldItem.isEmpty()) {
						ICompostBinRecipe compostRecipe = CompostRecipe.getCompostRecipe(heldItem);
						if (compostRecipe != null) {
							int amount = compostRecipe.getCompostAmount(heldItem);
							int time = compostRecipe.getCompostingTime(heldItem);
							switch (tile.addItemToBin(heldItem, amount, time, true)) {
							case 1:
								tile.addItemToBin(heldItem, amount, time, false);
								if (!player.capabilities.isCreativeMode) {
									player.getHeldItem(hand).shrink(1);
								}
								break;
							case -1:
							default:
								player.sendStatusMessage(new TextComponentTranslation("chat.compost.full"), true);
								break;
							}
						} else {
							player.sendStatusMessage(new TextComponentTranslation("chat.compost.not.compostable"), true);
						}
						interacted = true;
					}
				}
			}
		}
		
		return true;
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
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
		if (world.getTileEntity(pos) instanceof TileEntityCompostBin) {
			TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(pos);
			if(!tile.isOpen() && !tile.isEmpty()) {
				BLParticles.DIRT_DECAY.spawn(world, pos.getX() + 0.2F + rand.nextFloat() * 0.62F, pos.getY() + rand.nextFloat() * 0.75F, pos.getZ() + 0.2F + rand.nextFloat() * 0.6F);
			}
		}
	}
}
