package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.Random;

public class BlockBlackHatMushroom extends BlockBLSmallPlants {
	public IIcon modelTexture1;
	public IIcon modelTexture2;
	public IIcon modelTexture3;
	
	public BlockBlackHatMushroom() {
		super("blackHatMushroom");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:blackHatMushroom");
		modelTexture2 = reg.registerIcon("thebetweenlands:blackHatMushroom2");
		modelTexture3 = reg.registerIcon("thebetweenlands:blackHatMushroom3");
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}
}
