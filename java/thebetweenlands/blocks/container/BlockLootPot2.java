package thebetweenlands.blocks.container;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.tileentities.TileEntityLootPot;

public class BlockLootPot2 extends BlockLootPot1 {

	public BlockLootPot2() {
		super();
		setBlockName("thebetweenlands.lootPot1");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack is) {
		if (world.getTileEntity(x, y, z) instanceof TileEntityLootPot) {
			TileEntityLootPot tile = (TileEntityLootPot) world.getTileEntity(x,y, z);
			if (tile != null && !world.isRemote) {
				tile.setPotModelType((byte) 1);
				tile.setModelRotationOffset(world.rand.nextInt(41) - 20);
				world.markBlockForUpdate(x, y, z);
			}
		}
		byte rotationMeta = 0;
		int rotation = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if (rotation == 0)
			rotationMeta = 2;
		if (rotation == 1)
			rotationMeta = 5;
		if (rotation == 2)
			rotationMeta = 3;
		if (rotation == 3)
			rotationMeta = 4;
		world.setBlockMetadataWithNotify(x, y, z, rotationMeta, 3);
	}

}
