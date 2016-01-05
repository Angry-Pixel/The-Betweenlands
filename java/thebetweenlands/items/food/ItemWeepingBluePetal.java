package thebetweenlands.items.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.herblore.elixirs.ElixirEffectRegistry;
import thebetweenlands.manual.IManualEntryItem;

public class ItemWeepingBluePetal extends ItemFood implements IManualEntryItem
{
	public ItemWeepingBluePetal() {
		super(4, 1.2F, false);
		this.setAlwaysEdible();
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		player.addPotionEffect(ElixirEffectRegistry.EFFECT_RIPENING.createEffect(600, 2));
	}

	@Override
	public String manualName(int meta) {
		return "weepingBluePetal";
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
