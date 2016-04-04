package thebetweenlands.blocks.tree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;


public class BlockRubberLog extends BlockBLLog {
	public BlockRubberLog(String blockName) {
		super("rubberTreeLog");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.RUBBER_LOG.id();
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon cutIcon, cutIconTop;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		this.cutIcon = iconRegister.registerIcon("thebetweenlands:rubberTreeLogCut");
		this.cutIconTop = iconRegister.registerIcon("thebetweenlands:rubberTreeLogCutTop");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getSideIcon(int meta) {
		if(meta == 0) {
			return this.cutIcon;
		}
		return super.getSideIcon(meta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getTopIcon(int meta) {
		if(meta == 0) {
			return this.cutIconTop;
		}
		return super.getTopIcon(meta);
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		return meta;
	}
}
