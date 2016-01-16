package thebetweenlands.blocks.plants.crops;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityAspectCrop;

public class BlockAspectCrop extends BlockBLGenericCrop implements ITileEntityProvider {
	private static final int MAX_HEIGHT = 3;

	public BlockAspectCrop(String blockName) {
		super(blockName);
		this.setBlockTextureName("thebetweenlands:weedwoodLeavesFast");
		float min = 0.375F - 0.03F;
		float max = 0.625F + 0.03F;
		this.setBlockBounds(min, 0, min, max, 1, max);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(this.getTextureName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return this.blockIcon;
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == BLBlockRegistry.aspectCrop || super.canPlaceBlockOn(block);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block soil = world.getBlock(x, y - 1, z);
		return (soil == BLBlockRegistry.aspectCrop && ((BlockBLGenericCrop)soil).isFullyGrown(world, x, y - 1, z)) || super.canBlockStay(world, x, y, z);
	}

	@Override
	public void onGrow(World world, int x, int y, int z, int meta) { 
		if(meta == BlockBLGenericCrop.MATURE_CROP) {
			//Max height: 3
			if(world.getBlock(x, y - (MAX_HEIGHT - 1), z) == this)
				return;
			if(world.getBlock(x, y + 1, z) == BLBlockRegistry.rubberTreePlankFence) {
				if(!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x+1, y+1, z) &&
						!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x-1, y+1, z) &&
						!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x, y+1, z+1) &&
						!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x, y+1, z-1)) {
					world.setBlock(x, y + 1, z, this);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return -1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float min = 0.375F;
		float max = 0.625F;
		return AxisAlignedBB.getBoundingBox((double)x + min, (double)y, (double)z + min, (double)x + max, (double)y + 1.5D, (double)z + max);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		super.removedByPlayer(world, player, x, y, z, willHarvest);
		for(int yo = 1; yo <= MAX_HEIGHT; yo++) {
			Block block = world.getBlock(x, y-yo, z);
			if(block instanceof BlockAspectCrop) {
				block.dropBlockAsItem(world, x, y-yo, z, world.getBlockMetadata(x, y-yo, z), 0);
				block.onBlockHarvested(world, x, y-yo, z, world.getBlockMetadata(x, y-yo, z), player);
				world.setBlock(x, y-yo, z, BLBlockRegistry.rubberTreePlankFence);
			}
		}
		return world.setBlock(x, y, z, BLBlockRegistry.rubberTreePlankFence);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
		world.setBlock(x, y, z, BLBlockRegistry.rubberTreePlankFence);
	}

	@Override
	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, BLBlockRegistry.rubberTreePlankFence);
		}
	}

	/**
	 * Whether this crop can be grown with a fertilizer
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote) {
		int meta = world.getBlockMetadata(x, y, z);
		return (world.getBlock(x, y + 1, z) != BLBlockRegistry.rubberTreePlankFence || world.getBlock(x, y - (MAX_HEIGHT - 1), z) == this) ? meta < MATURE_CROP : meta <= MATURE_CROP;
	}

	@Override
	public void onDecayed(World world, int x, int y, int z) {
		Block blockAbove = world.getBlock(x, y+1, z);
		if(blockAbove instanceof BlockAspectCrop)
			((BlockAspectCrop)blockAbove).setDecayed(world, x, y+1, z, true);
		Block blockBelow = world.getBlock(x, y-1, z);
		if(blockBelow instanceof BlockAspectCrop)
			((BlockAspectCrop)blockBelow).setDecayed(world, x, y-1, z, true);
	}

	@Override
	public void onCure(World world, int x, int y, int z) {
		Block blockAbove = world.getBlock(x, y+1, z);
		if(blockAbove instanceof BlockAspectCrop)
			((BlockAspectCrop)blockAbove).setDecayed(world, x, y+1, z, false);
		Block blockBelow = world.getBlock(x, y-1, z);
		if(blockBelow instanceof BlockAspectCrop)
			((BlockAspectCrop)blockBelow).setDecayed(world, x, y-1, z, false);
	}

	@Override
	public ItemStack getSeedDrops() {
		return new ItemStack(Blocks.dirt, 1);	
	}

	@Override
	public ItemStack getCropDrops() {
		return new ItemStack(Blocks.stone, 1);	
	}








	//////// TE Stuff /////////
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int event, int arg) {
		super.onBlockEventReceived(world, x, y, z, event, arg);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null ? tileentity.receiveClientEvent(event, arg) : false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAspectCrop();
	}
}
