package thebetweenlands.blocks;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.ArrayList;
import java.util.Random;

import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockRubberTap extends Block {

	public BlockRubberTap() {
		super(Material.wood);
		setBlockName("thebetweenlands.rubberTap");
		setHardness(0.8F);
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeWood);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
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
		return world.isSideSolid(x - 1, y, z, EAST,  true) ||
				world.isSideSolid(x + 1, y, z, WEST,  true) ||
				world.isSideSolid(x, y, z - 1, SOUTH, true) ||
				world.isSideSolid(x, y, z + 1, NORTH, true);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		int res = meta;

		if (side == 2 && world.isSideSolid(x, y, z + 1, NORTH, true)) {
			res = 3;
		}

		if (side == 3 && world.isSideSolid(x, y, z - 1, SOUTH, true)) {
			res = 2;
		}

		if (side == 4 && world.isSideSolid(x + 1, y, z, WEST, true)) {
			res = 1;
		}

		if (side == 5 && world.isSideSolid(x - 1, y, z, EAST, true)) {
			res = 0;
		}

		return res;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if(world.isRemote) {
			return;
		}

		//15 min. schedule
		world.scheduleBlockUpdate(x, y, z, this, /*20*60*15*/20);

		if (world.getBlockMetadata(x, y, z) == 0) {
			if (world.isSideSolid(x - 1, y, z, EAST, true)) {
				world.setBlockMetadataWithNotify(x, y, z, 0, 2);
			} else if (world.isSideSolid(x + 1, y, z, WEST, true)) {
				world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			} else if (world.isSideSolid(x, y, z - 1, SOUTH, true)) {
				world.setBlockMetadataWithNotify(x, y, z, 2, 2);
			} else if (world.isSideSolid(x, y, z + 1, NORTH, true)) {
				world.setBlockMetadataWithNotify(x, y, z, 3, 2);
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

			if (!world.isSideSolid(x - 1, y, z, EAST, true) && meta == 0) {
				mustDrop = true;
			}

			if (!world.isSideSolid(x + 1, y, z, WEST, true) && meta == 1) {
				mustDrop = true;
			}

			if (!world.isSideSolid(x, y, z - 1, SOUTH, true) && meta == 2) {
				mustDrop = true;
			}

			if (!world.isSideSolid(x, y, z + 1, NORTH, true) && meta == 3) {
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
		case 0:
		case 4:
			world.setBlockMetadataWithNotify(x, y, z, full ? 4 : 0, 2);
			break;
		case 1:
		case 5:
			world.setBlockMetadataWithNotify(x, y, z, full ? 5 : 0, 2);
			break;
		case 2:
		case 6:
			world.setBlockMetadataWithNotify(x, y, z, full ? 6 : 0, 2);
			break;
		case 3:
		case 7:
			world.setBlockMetadataWithNotify(x, y, z, full ? 7 : 0, 2);
			break;
		}
	}

	public boolean isFull(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta >= 4;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		this.setFull(world, x, y, z, true);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if(!this.isFull(world, x, y, z)) {
			drops.add(ItemMaterialsBL.createStack(BLItemRegistry.weedwoodBucket, 1, 0));
		} else {
			drops.add(ItemMaterialsBL.createStack(BLItemRegistry.weedwoodBucketRubber, 1, 0));
		}
		return drops;
	}
}