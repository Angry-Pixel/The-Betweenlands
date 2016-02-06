package thebetweenlands.blocks.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityBLCraftingTable;

public class BlockBLWorkbench extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;

    public BlockBLWorkbench() {
    	super(Material.wood);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.weedwoodCraftingTable");
		setHardness(2.5F);
		setBlockTextureName("thebetweenlands:weedwoodCraftingTable");
    }

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if( !world.isRemote ) {
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_WEEDWOOD_CRAFT, world, x, y, z);
		}

        return true;
	}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int rotation = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        ++rotation;
        rotation %= 4;

        ((TileEntityBLCraftingTable) world.getTileEntity(x, y, z)).rotation = (byte) rotation;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? topIcon : (side == 0 ? BLBlockRegistry.weedwoodPlanks.getBlockTextureFromSide(side) : (side != 2 && side != 4 ? blockIcon : sideIcon));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        blockIcon = icon.registerIcon(getTextureName() + "Side");
        topIcon = icon.registerIcon(getTextureName() + "Top");
        sideIcon = icon.registerIcon(getTextureName() + "Front");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityBLCraftingTable();
    }
}
