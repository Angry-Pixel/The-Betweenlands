package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemMarshmallow extends ItemBLFood {
    public ItemMarshmallow() {
        super(4, 0.3F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 400, 1));
    }

}
