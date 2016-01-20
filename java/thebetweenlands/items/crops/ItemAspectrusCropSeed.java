package thebetweenlands.items.crops;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.tileentities.TileEntityAspectrusCrop;

public class ItemAspectrusCropSeed extends ItemBLGenericSeed {
	public ItemAspectrusCropSeed(int healAmount, float saturation) {
		super(healAmount, saturation, BLBlockRegistry.aspectrusCrop, BLBlockRegistry.farmedDirt);
		this.setCreativeTab(ModCreativeTabs.plants);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (side != 1)
			return false;
		else if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
			int meta = world.getBlockMetadata(x, y, z);
			if (world.getBlock(x, y, z) == this.soilId && meta >= 4 && meta <= 10 && meta != 7 && meta != 8 && world.getBlock(x, y + 1, z) == BLBlockRegistry.rubberTreePlankFence) {
				world.setBlock(x, y + 1, z, this.cropId);
				if(!world.isRemote) {
					List<Aspect> seedAspects = AspectManager.getDynamicAspects(stack);
					if(!seedAspects.isEmpty()) {
						TileEntity tile = world.getTileEntity(x, y + 1, z);
						if(tile instanceof TileEntityAspectrusCrop) {
							((TileEntityAspectrusCrop) tile).setAspect(seedAspects.get(0));
						}
					}
				}
				--stack.stackSize;
				return true;
			} else
				return false;
		} else
			return false;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		List<Aspect> itemAspects = AspectManager.getDynamicAspects(stack);
		if(!itemAspects.isEmpty()) {
			Aspect aspect = itemAspects.get(0);
			return super.getItemStackDisplayName(stack) + " - " + aspect.type.getName() + " (" + aspect.getAmount() + ")";
		}
		return super.getItemStackDisplayName(stack);
	}
}
