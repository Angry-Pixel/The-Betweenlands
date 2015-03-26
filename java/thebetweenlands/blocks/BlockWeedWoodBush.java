package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.tileentities.TileEntityBush;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWeedWoodBush extends BlockContainer /*Block*/ {

	@SideOnly(Side.CLIENT)
	public IIcon iconFancy, iconFast, iconStick;
	
	protected BlockWeedWoodBush() 
	{
		super(Material.leaves);
		setBlockTextureName("thebetweenlands:" + "weedWoodLeavesFancy");
	}
    
    @Override
    public TileEntity createNewTileEntity(World world, int var2)
    {
        return new TileEntityBush();
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public int getRenderType()
    {
        return BlockRenderIDs.BUSH.id();
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconFancy = iconRegister.registerIcon("thebetweenlands:weedWoodLeavesFancy");
		this.iconFast = iconRegister.registerIcon("thebetweenlands:weedWoodLeavesFast");
		this.iconStick = iconRegister.registerIcon("thebetweenlands:weedWoodStick");
	}
}
