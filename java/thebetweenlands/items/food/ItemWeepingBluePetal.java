package thebetweenlands.items.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.herblore.elixirs.ElixirRegistry;

public class ItemWeepingBluePetal
extends ItemFood
{
	public ItemWeepingBluePetal() {
		super(4, 1.2F, false);
		this.setAlwaysEdible();
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		player.addPotionEffect(ElixirRegistry.EFFECT_RIPENING.createEffect(600, 2));
	}
}
