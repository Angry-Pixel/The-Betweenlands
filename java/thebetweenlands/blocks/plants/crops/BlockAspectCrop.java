package thebetweenlands.blocks.plants.crops;

import java.util.List;

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
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityAspectCrop;

public class BlockAspectCrop extends BlockBLGenericCrop implements ITileEntityProvider {
	private static final float ASPECT_MULTIPLIER = 0.66F;
	private static final int MAX_HEIGHT = 3;

	public BlockAspectCrop(String blockName) {
		super(blockName);
		this.setBlockTextureName("thebetweenlands:weedwoodLeavesFast");
		float min = 0.375F - 0.03F;
		float max = 0.625F + 0.03F;
		this.setBlockBounds(min, 0, min, max, 1, max);
	}

	public void setAspect(World world, int x, int y, int z, Aspect aspect){
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityAspectCrop)
			((TileEntityAspectCrop)tile).setAspect(aspect);
	}

	public Aspect getAspect(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityAspectCrop)
			return ((TileEntityAspectCrop)tile).getAspect();
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(this.getAspect(world, x, y, z) == null) {
			ItemStack heldItem = player.getHeldItem();
			if(heldItem != null && heldItem.getItem() == BLItemRegistry.aspectVial) {
				List<Aspect> aspects = AspectManager.get(world).getDiscoveredAspects(heldItem, null);
				if(aspects.size() > 0) {
					if(!world.isRemote) {
						Aspect aspect = aspects.get(0);
						this.setAspect(world, x, y, z, aspect);
						if (!player.capabilities.isCreativeMode) {
							--heldItem.stackSize;
							if(heldItem.stackSize == 0)
								player.setCurrentItemOrArmor(0, null);
							ItemStack newStack;
							if(heldItem.getItemDamage() % 2 == 0) {
								newStack = new ItemStack(BLItemRegistry.dentrothystVial, 1, 0);
							} else {
								newStack = new ItemStack(BLItemRegistry.dentrothystVial, 1, 2);
							}
							if(!player.inventory.addItemStackToInventory(newStack))
								player.dropPlayerItemWithRandomChoice(newStack, false);
						}
					}
					return true;
				}
			}
		}
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
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
					if(world.getBlock(x, y + 1, z) instanceof BlockAspectCrop)
						((BlockAspectCrop)world.getBlock(x, y + 1, z)).setAspect(world, x, y + 1, z, this.getAspect(world, x, y, z));
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
		Aspect aspect = this.getAspect(world, x, y, z);
		if(aspect == null)
			return false;
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
	public ItemStack getSeedDrops(World world, int x, int y, int z) {
		return new ItemStack(Blocks.dirt, 1);	
	}

	@Override
	public ItemStack getCropDrops(World world, int x, int y, int z) {
		ItemStack stack = new ItemStack(Blocks.stone, 1);
		Aspect aspect = this.getAspect(world, x, y, z);
		if(aspect != null)
			AspectManager.addAspects(stack, new Aspect(aspect.type, aspect.amount * ASPECT_MULTIPLIER));
		return stack;	
	}

	@Override
	public boolean shouldGrow(World world, int x, int y, int z) {
		Aspect aspect = this.getAspect(world, x, y, z);
		if(aspect != null)
			return world.rand.nextInt((int)(10 + aspect.amount * 20)) == 0;
		return false;
	}

	@Override
	public boolean shouldDecay(World world, int x, int y, int z) {
		Aspect aspect = this.getAspect(world, x, y, z);
		if(aspect != null)
			return world.rand.nextInt(Math.max((int)(BlockFarmedDirt.DECAY_CHANCE - aspect.amount * 35), 2)) == 0;
		return world.rand.nextInt(BlockFarmedDirt.DECAY_CHANCE) == 0;
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
