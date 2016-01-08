package thebetweenlands.items.food;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.manual.IManualEntryItem;

public class ItemBulbCappedMushroom extends ItemFood implements IManualEntryItem {
	public ItemBulbCappedMushroom() {
		super(3, 0.6F, false);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		Block block = world.getBlock(x, y, z);
		if(block.isReplaceable(world, x, y, z) || (side == 1 && world.getBlock(x, ++y, z) == Blocks.air)) {
			block = world.getBlock(x, y, z);
			if(block != BLBlockRegistry.bulbCappedMushroom && BLBlockRegistry.bulbCappedMushroom.canPlaceBlockAt(world, x, y, z)) {
				if(!world.isRemote) {
					world.setBlock(x, y, z, BLBlockRegistry.bulbCappedMushroom);
					world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BLBlockRegistry.bulbCappedMushroom.stepSound.func_150496_b(), (BLBlockRegistry.bulbCappedMushroom.stepSound.getVolume() + 1.0F) / 2.0F, BLBlockRegistry.bulbCappedMushroom.stepSound.getPitch() * 0.8F);
					--stack.stackSize;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);

		if(player != null) {
			player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 200, 1));
			player.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 200, 1));
		}
	}

	@Override
	public String manualName(int meta) {
		return "bulbCappedMushroom";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[0];
	}

	@Override
	public int metas() {
		return 0;
	}
}