package thebetweenlands.blocks.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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