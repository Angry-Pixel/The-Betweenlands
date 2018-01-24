package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class ItemWeepingBluePetalSalad extends ItemBLFood {
	public ItemWeepingBluePetalSalad() {
		super(6, 1.2F, false);
		this.setAlwaysEdible();
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return EnumItemMisc.WEEDWOOD_BOWL.create(1);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			player.addPotionEffect(new PotionEffect(ElixirEffectRegistry.EFFECT_RIPENING.getPotionEffect(), 4200, 2));
		}

		if (stack.getCount() != 0)
			player.inventory.addItemStackToInventory(getContainerItem(stack));
	}
}
