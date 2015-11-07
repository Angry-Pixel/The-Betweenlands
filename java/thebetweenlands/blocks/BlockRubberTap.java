package thebetweenlands.blocks;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

public class BlockRubberTap extends Block {
	public IIcon icon;

	public BlockRubberTap() {
		super(Material.wood);
		setBlockName("thebetweenlands.rubberTap");
		setHardness(0.8F);
		setCreativeTab(null);
		setStepSound(Block.soundTypeWood);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		this.icon = register.registerIcon("thebetweenlands:blockBucket");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return this.icon;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.RUBBER_TAP.id();
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return (world.getBlock(x-1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x-1, y, z) == 1)||
				(world.getBlock(x+1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x+1, y, z) == 1) ||
				(world.getBlock(x, y, z-1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z-1) == 1) ||
				(world.getBlock(x, y, z+1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z+1) == 1);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		int res = meta;

		if (side == 2 && world.getBlock(x, y, z+1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z+1) == 1) {
			res = 4;
		}

		if (side == 3 && world.getBlock(x, y, z-1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z-1) == 1) {
			res = 3;
		}

		if (side == 4 && world.getBlock(x+1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x+1, y, z) == 1) {
			res = 2;
		}

		if (side == 5 && world.getBlock(x-1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x-1, y, z) == 1) {
			res = 1;
		}

		return res;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if(world.isRemote) return;

		//5 min. schedule
		world.scheduleBlockUpdate(x, y, z, this, 20*60*5);

		if (world.getBlockMetadata(x, y, z) == 0) {
			if (world.getBlock(x-1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x-1, y, z) == 1) {
				world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			} else if (world.getBlock(x+1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x+1, y, z) == 1) {
				world.setBlockMetadataWithNotify(x, y, z, 2, 2);
			} else if (world.getBlock(x, y, z-1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z-1) == 1) {
				world.setBlockMetadataWithNotify(x, y, z, 3, 2);
			} else if (world.getBlock(x, y, z+1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z+1) == 1) {
				world.setBlockMetadataWithNotify(x, y, z, 4, 2);
			}
		}

		this.checkAndDrop(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		this.canBlockStay(world, x, y, z, block);
	}

	protected boolean canBlockStay(World world, int x, int y, int z, Block block) {
		if (this.checkAndDrop(world, x, y, z)) {
			int meta = world.getBlockMetadata(x, y, z);
			boolean mustDrop = false;

			if (!(world.getBlock(x - 1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x-1, y, z) == 1) && meta == 1) {
				mustDrop = true;
			}

			if (!(world.getBlock(x + 1, y, z) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x+1, y, z) == 1) && meta == 2) {
				mustDrop = true;
			}

			if (!(world.getBlock(x, y, z - 1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z-1) == 1) && meta == 3) {
				mustDrop = true;
			}

			if (!(world.getBlock(x, y, z + 1) == BLBlockRegistry.rubberTreeLog && world.getBlockMetadata(x, y, z+1) == 1) && meta == 4) {
				mustDrop = true;
			}

			if (mustDrop) {
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	protected boolean checkAndDrop(World world, int x, int y, int z) {
		if (!this.canPlaceBlockAt(world, x, y, z)) {
			if (world.getBlock(x, y, z) == this) {
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
			}
			return false;
		} else {
			return true;
		}
	}

	public void setFull(World world, int x, int y, int z, boolean full) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta) {
		case 1:
		case 5:
			world.setBlockMetadataWithNotify(x, y, z, full ? 5 : 1, 2);
			break;
		case 2:
		case 6:
			world.setBlockMetadataWithNotify(x, y, z, full ? 6 : 2, 2);
			break;
		case 3:
		case 7:
			world.setBlockMetadataWithNotify(x, y, z, full ? 7 : 3, 2);
			break;
		case 4:
		case 8:
			world.setBlockMetadataWithNotify(x, y, z, full ? 8 : 4, 2);
			break;
		}
	}

	public boolean isFull(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) >= 5;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(!world.isRemote) {
			this.setFull(world, x, y, z, true);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		if(world.isRemote) return new ArrayList<ItemStack>();
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if(meta < 5) {
			drops.add(ItemGeneric.createStack(BLItemRegistry.weedwoodBucket, 1, 0));
		} else {
			drops.add(ItemGeneric.createStack(BLItemRegistry.weedwoodBucketRubber, 1, 0));
		}
		drops.add(ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED_ROPE));
		return drops;
	}
}