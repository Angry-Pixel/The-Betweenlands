package thebetweenlands.blocks.lanterns;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.utils.vectormath.Point3f;

import java.util.Random;

public class BlockConnectionFastenerFence extends BlockConnectionFastener {
	public BlockConnectionFastenerFence() {
		super(Material.wood);
		setHardness(2);
		setResistance(5);
		setStepSound(soundTypeWood);
		setBlockBounds(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
		setBlockName("thebetweenlands.connectionFastenerFence");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return Blocks.fence.getIcon(0, 0);
	}

	@Override
	public Item getItemDropped(int data, Random random, int fortune) {
		return Item.getItemFromBlock(Blocks.fence);
	}

	@Override
	public Point3f getOffsetForData(int data, float offset) {
		return new Point3f(offset + 0.375F, offset + 0.375F, offset + 0.375F);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		setBlockBounds(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.UP || side == ForgeDirection.DOWN;
	}

	@Override
	public void registerBlockIcons(IIconRegister registrar) {
	}
}
