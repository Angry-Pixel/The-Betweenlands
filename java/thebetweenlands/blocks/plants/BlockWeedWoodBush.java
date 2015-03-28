package thebetweenlands.blocks.plants;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWeedWoodBush extends /*BlockContainer*/ Block {

	@SideOnly(Side.CLIENT)
	public IIcon iconFancy, iconFast, iconStick;
	
	public BlockWeedWoodBush() 
	{
		super(Material.leaves);
		setHardness(0.35F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.weedwoodbush");
		setBlockTextureName("thebetweenlands:weedWoodLeavesFancy");
        setBlockBounds(0.1f, 0.1f, 0.1f, 0.9f, 0.9f, 0.9f);
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
