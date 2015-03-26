package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWeedWoodBush extends /*BlockContainer*/ Block {

	@SideOnly(Side.CLIENT)
	public IIcon iconFancy, iconFast, iconStick;
	
	protected BlockWeedWoodBush() 
	{
		super(Material.leaves);
		setBlockTextureName("thebetweenlands:" + "weedWoodLeavesFancy");
	}
    
    @Override
    public boolean renderAsNormalBlock()
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
        return BlockRenderIDs.WEEDWOOD_BUSH.id();
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconFancy = iconRegister.registerIcon("thebetweenlands:weedWoodLeavesFancy");
		this.iconFast = iconRegister.registerIcon("thebetweenlands:weedWoodLeavesFast");
		this.iconStick = iconRegister.registerIcon("thebetweenlands:weedWoodStick");
		super.registerBlockIcons(iconRegister);
	}
}
