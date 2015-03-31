package thebetweenlands.blocks.plants;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockModelPlant extends BlockBLSmallPlants {
	public IIcon modelTexture;
	
	public BlockModelPlant() {
		super("modelPlant");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture = reg.registerIcon("thebetweenlands:fernModelTexture");
	}
}
