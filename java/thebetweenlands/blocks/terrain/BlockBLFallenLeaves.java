package thebetweenlands.blocks.terrain;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockBLFallenLeaves extends BlockBush{

	@SideOnly(Side.CLIENT)
	private IIcon spoopyIcon;

	private String type;
	public BlockBLFallenLeaves(String blockName) {
		setHardness(0.1F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.plants);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
		setBlockBounds(0, 0.0F, 0, 1.0F, 0.05F, 1.0F);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		entity.motionX *= 0.9D;
		entity.motionZ *= 0.9D;
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block.isOpaqueCube();
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return world.getBlock(x, y - 1, z).isOpaqueCube();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		if(blockAccess.getBlock(x, y, z) == this) {
			return false;
		}
		return super.shouldSideBeRendered(blockAccess, x, y, z, side);
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		spoopyIcon = reg.registerIcon(this.getTextureName() + "Spoopy");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld)) {
			return spoopyIcon;
		}
		return super.getIcon(side, meta);
	}
}