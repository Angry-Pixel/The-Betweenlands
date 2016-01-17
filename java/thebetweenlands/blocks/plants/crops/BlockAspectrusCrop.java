package thebetweenlands.blocks.plants.crops;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.tileentities.TileEntityAspectrusCrop;

public class BlockAspectrusCrop extends BlockBLGenericCrop implements ITileEntityProvider {
	private static final float ASPECT_MULTIPLIER = 0.5F;
	private static final int MAX_HEIGHT = 3;

	public BlockAspectrusCrop(String blockName) {
		super(blockName);
		this.setBlockTextureName("thebetweenlands:weedwoodLeavesFast");
		float min = 0.375F - 0.03F;
		float max = 0.625F + 0.03F;
		this.setBlockBounds(min, 0, min, max, 1, max);
		this.setMaxHeight(MAX_HEIGHT);
	}

	public void setAspect(World world, int x, int y, int z, Aspect aspect){
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityAspectrusCrop)
			((TileEntityAspectrusCrop)tile).setAspect(aspect);
	}

	public Aspect getAspect(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityAspectrusCrop)
			return ((TileEntityAspectrusCrop)tile).getAspect();
		return null;
	}

	@Override
	public ItemStack getSeedDrop(World world, int x, int y, int z) {
		return new ItemStack(BLItemRegistry.aspectrusCropSeed, 1);	
	}

	@Override
	public ItemStack getCropDrop(World world, int x, int y, int z) {
		ItemStack stack = ItemGeneric.createStack(EnumItemGeneric.ASPECTRUS_FRUIT);
		Aspect aspect = this.getAspect(world, x, y, z);
		if(aspect != null)
			AspectManager.addAspects(stack, new Aspect(aspect.type, aspect.amount * ASPECT_MULTIPLIER));
		return stack;	
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
	public boolean destroyCrop(World world, int x, int y, int z, int meta) {
		return world.setBlock(x, y, z, BLBlockRegistry.rubberTreePlankFence);
	}

	/**
	 * Whether this crop can be grown with a fertilizer
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote) {
		Aspect aspect = this.getAspect(world, x, y, z);
		if(aspect == null)
			return false;
		return super.func_149851_a(world, x, y, z, isRemote);
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

	@Override
	public boolean canGrowTo(World world, int x, int y, int z) {
		if(world.getBlock(x, y, z) == BLBlockRegistry.rubberTreePlankFence) {
			if(!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x+1, y, z) &&
					!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x-1, y, z) &&
					!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x, y, z+1) &&
					!BLBlockRegistry.rubberTreePlankFence.canConnectFenceTo(world, x, y, z-1)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onGrow(World world, int x, int y, int z) {
		Block blockBelow = world.getBlock(x, y - 1, z);
		if(blockBelow instanceof BlockAspectrusCrop)
			this.setAspect(world, x, y, z, ((BlockAspectrusCrop)blockBelow).getAspect(world, x, y - 1, z));
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
		return new TileEntityAspectrusCrop();
	}
}
