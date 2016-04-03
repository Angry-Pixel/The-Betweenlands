package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWeepingBluePetal extends ItemBLFood {
    public ItemWeepingBluePetal() {
        super(4, 1.2F, false);
        this.setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        //player.addPotionEffect(ElixirEffectRegistry.EFFECT_RIPENING.createEffect(600, 2));
    }
}
