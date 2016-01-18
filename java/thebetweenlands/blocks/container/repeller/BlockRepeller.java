package thebetweenlands.blocks.container.repeller;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.tileentities.TileEntityRepeller;

public class BlockRepeller extends BlockContainer {
	public BlockRepeller() {
		super(Material.wood);
		this.setCreativeTab(ModCreativeTabs.blocks);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if(world.getBlockMetadata(x, y, z) >= 4 && world.getBlock(x, y - 1, z) == this) {
			return this.onBlockActivated(world, x, y - 1, z, player, metadata, hitX, hitY, hitZ);
		} else if(world.getBlockMetadata(x, y, z) < 4) {
			TileEntityRepeller tile = (TileEntityRepeller) world.getTileEntity(x, y, z);
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack is) {
		int rotation = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rotation, 3);
		world.setBlock(x, y + 1, z, this, rotation + 4, 3);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return (world.getBlock(x, y - 1, z) != this && world.isSideSolid(x, y - 1, z, ForgeDirection.UP) && world.getBlock(x, y + 1, z) == this) || 
				world.getBlock(x, y - 1, z) == this;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		this.checkAndBreakBlock(world, x, y, z);
	}

	protected void checkAndBreakBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			world.setBlock(x, y, z, getBlockById(0), 0, 3);
		}
	}

	@Override
	public int getRenderType() {
		return -1;
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return meta < 4 ? new TileEntityRepeller() : null;
	}
}
