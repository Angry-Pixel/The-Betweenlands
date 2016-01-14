package thebetweenlands.blocks.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.tileentities.TileEntityGeckoCage;
import thebetweenlands.tileentities.TileEntityPurifier;

public class BlockGeckoCage extends BlockContainer {
	public BlockGeckoCage() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("thebetweenlands.geckoCage");
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockTextureName("thebetweenlands:weedwood");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.weedwood.getIcon(0, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int rot = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot == 0 ? 2 : rot == 1 ? 5 : rot == 2 ? 3 : 4, 3);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		if (world.getTileEntity(x, y, z) instanceof TileEntityGeckoCage ) {
			TileEntityGeckoCage tile = (TileEntityGeckoCage ) world.getTileEntity(x, y, z);

			if (player.isSneaking())
				return false;

			//TODO: Gecko Cage - Implement interaction
		}
		return true;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (world.getTileEntity(x, y, z) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getTileEntity(x, y, z);
			if (tile == null)
				return 0;
			return tile.lightOn ? 13 : 0;
		}
		return 0;
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityGeckoCage();
	}
}