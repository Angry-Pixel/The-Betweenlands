package thebetweenlands.blocks.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.tileentities.TileEntitySpikeTrap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpikeTrap extends BlockContainer {
	
	@SideOnly(Side.CLIENT)
	private IIcon crackedTexture;

	public BlockSpikeTrap() {
		super(Material.rock);
		setStepSound(Block.soundTypeStone);
		setBlockUnbreakable();
		setResistance(6000000.0F);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockTextureName("thebetweenlands:polishedLimestone");
		setBlockName("thebetweenlands.spikeTrap");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySpikeTrap();
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
		int meta = world.getBlockMetadata(x, y, z);
		TileEntitySpikeTrap tile = (TileEntitySpikeTrap) world.getTileEntity(x, y, z);
		if (!world.isRemote && tile != null && meta == 1) {
			if (!tile.active && tile.animationTicks == 0)
				tile.setActive(true);
		}
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		crackedTexture = reg.registerIcon("thebetweenlands:chiseledLimestone");
		blockIcon = reg.registerIcon("thebetweenlands:polishedLimestone");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 1 && meta == 1 ? crackedTexture : blockIcon;
	}
}