package thebetweenlands.blocks.plants;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlackHatMushroom extends BlockBLSmallPlants {
	public IIcon modelTexture1;
	public IIcon modelTexture2;
	public IIcon modelTexture3;
	
	public BlockBlackHatMushroom() {
		super("modelPlant");
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
}
