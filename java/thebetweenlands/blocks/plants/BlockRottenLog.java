package thebetweenlands.blocks.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRottenLog extends Block {
	public IIcon modelTexture1, modelTexture2;

	public BlockRottenLog() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setBlockName("thebetweenlands.rottenLog");
		setBlockTextureName("thebetweenlands:rottenLog");
		setCreativeTab(ModCreativeTabs.plants);
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:rottenLog");
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
	public int getDamageValue(World world, int x, int y, int z) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.ROTTEN_BARK).getItemDamage();
	}

	@Override
	public int quantityDropped(Random rnd) {
		return 1;
	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.ROTTEN_BARK).getItemDamage();
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.ROTTEN_BARK).getItem();
	}
}
