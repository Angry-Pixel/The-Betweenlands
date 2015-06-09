package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.Random;

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
	public int getDamageValue(World world, int x, int y, int z) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.BULB_CAPPED_MUSHROOM).getItemDamage();
	}

	@Override
	public int quantityDropped(Random rnd) {
        return 1;
    }
	
	@Override
	public int damageDropped(int p_149692_1_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.BULB_CAPPED_MUSHROOM).getItemDamage();
    }
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.BULB_CAPPED_MUSHROOM).getItem();
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
		}
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
}
