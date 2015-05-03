package thebetweenlands.blocks.plants;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBulbCappedMushroom extends BlockBLSmallPlants {
	public IIcon modelTexture1;
	
	public BlockBulbCappedMushroom() {
		super("bulbCappedMushroom");
        setLightLevel(0.9375F);
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:bulbCappedMushroom");
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}
}
