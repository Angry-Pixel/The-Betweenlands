package thebetweenlands.blocks.structure;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityDruidAltar;

public class BlockDruidAltar
        extends BlockContainer
{
	public BlockDruidAltar() {
		super(Material.rock);
		setBlockUnbreakable();
		setResistance(100.0F);
		setStepSound(soundTypeStone);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.druidAltar");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDruidAltar();
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if( world.isRemote ) {
            return true;
        }

		if( world.getTileEntity(x, y, z) instanceof TileEntityDruidAltar ) {
            TileEntityDruidAltar altar = (TileEntityDruidAltar) world.getTileEntity(x, y, z);
            if( altar.craftingProgress == 0 ) {
                player.openGui(TheBetweenlands.instance, CommonProxy.GUI_DRUID_ALTAR, world, x, y, z);
            }
        }

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.betweenstone.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}
}
