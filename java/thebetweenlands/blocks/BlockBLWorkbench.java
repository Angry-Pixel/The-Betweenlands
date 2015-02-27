package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBLWorkbench extends Block {
	
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
	
    protected BlockBLWorkbench() {
    	super(Material.wood);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.weedWoodCraftingTable");
		setBlockTextureName("thebetweenlands:weedWoodCraftingTable");
    }
    
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		else {
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_WEEDWOOD_CRAFT, world, x, y, z);
			return true;
		}
	}
	
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? topIcon : (side == 0 ? BLBlockRegistry.weedWoodPlanks.getBlockTextureFromSide(side) : (side != 2 && side != 4 ? blockIcon : sideIcon));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        blockIcon = icon.registerIcon(getTextureName() + "Side");
        topIcon = icon.registerIcon(getTextureName() + "Top");
        sideIcon = icon.registerIcon(getTextureName() + "Front");
    }

}
