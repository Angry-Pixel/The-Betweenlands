package thebetweenlands.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.items.BLItemRegistry;

public class BlockRope extends BlockLadder {
	private IIcon iconTop;
	private IIcon iconMiddle;
	private IIcon iconBottom;

	public BlockRope() {
		this.setBlockName("thebeteenlands.rope");
		this.setStepSound(soundTypeGrass);
		this.setBlockBounds(0.4375f, 0f, 0.4375f, 0.5625f, 1f, 0.5625f);
		this.setHardness(0.5F);
		this.setCreativeTab(null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
		blockIcon = icon.registerIcon("thebetweenlands:climbingRopeTopSingle");
		iconTop = icon.registerIcon("thebetweenlands:climbingRopeTop");
		iconMiddle = icon.registerIcon("thebetweenlands:climbingRopeMiddle");
		iconBottom = icon.registerIcon("thebetweenlands:climbingRopeBottom");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float faceX, float faceY, float faceZ) {
		if(player.getHeldItem() == null && player.isSneaking()) {
			if(!world.isRemote) {
				int yr = y - 1;
				for(; yr > 0; yr--) {
					if(world.getBlock(x, yr, z) != this) break;
				}
				if(yr + 1 != y) {
					world.setBlock(x, yr + 1, z, Blocks.air);
					if(!player.capabilities.isCreativeMode) world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(BLItemRegistry.rope)));
				}
			}
			return world.getBlock(x, y-1, z) == this;
		}
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return world.getBlock(x, y + 1, z).isBlockSolid(world, x, y + 1, z, 1) || world.getBlock(x, y + 1, z) == BLBlockRegistry.rope;
	}

	@Override
	public int getRenderType() {
		return 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch(meta) {
		default:
		case 0:
			return blockIcon;
		case 1:
			return iconTop;
		case 2: 
			return iconBottom;
		case 3:
			return iconMiddle;
		}
	}

	@Override
	public Item getItemDropped(int meta, Random random, int p_149650_3_) {
		return BLItemRegistry.rope;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if(!world.isRemote) {
			if (!world.getBlock(x, y + 1, z).isBlockSolid(world, x, y + 1, z, 0) && world.getBlock(x, y + 1, z) != BLBlockRegistry.rope) {
				this.dropBlockAsItem(world, x, y, z, 1, 0);
				world.setBlockToAir(x, y, z);
			} else {
				this.updateMeta(world, x, y, z);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		this.updateMeta(world, x, y, z);
	}

	public void updateMeta(World world, int x, int y, int z) {
		if(world.getBlock(x, y + 1, z) != BLBlockRegistry.rope && world.getBlock(x, y - 1, z) != BLBlockRegistry.rope) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		} else if (world.getBlock(x, y + 1, z) != BLBlockRegistry.rope) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		} else if (world.getBlock(x, y - 1, z) != BLBlockRegistry.rope) {
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		} else {
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}
	}

	@Override
	public void func_149797_b(int meta) {} //Block bounds shouldn't change
}
