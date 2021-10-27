package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class ItemNettleSoup extends ItemBLFood {
	public ItemNettleSoup() {
		super(6, 1F, false);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return EnumItemMisc.WEEDWOOD_BOWL.create(1);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if (stack.getCount() != 0)
			player.inventory.addItemStackToInventory(getContainerItem(stack));
	}
}
