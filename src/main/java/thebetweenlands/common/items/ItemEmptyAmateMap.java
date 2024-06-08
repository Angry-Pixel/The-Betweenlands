package thebetweenlands.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.DimensionRegistries;

public class ItemEmptyAmateMap extends ComplexItem {

    public static final String STR_ID = "amatemap";

    public ItemEmptyAmateMap(Properties p_41143_) {
        super(p_41143_);
    }

    // Change empty amate map item to filled amate map item
    public InteractionResultHolder<ItemStack> use(Level p_41145_, Player p_41146_, InteractionHand p_41147_) {
        ItemStack itemstack = p_41146_.getItemInHand(p_41147_);

        // Check if in the betweenlands
        if (p_41145_.dimension() != DimensionRegistries.BETWEENLANDS_DIMENSION_KEY) {
            return InteractionResultHolder.fail(itemstack);
        }

        if (p_41145_.isClientSide) {
            return InteractionResultHolder.success(itemstack);
        } else {
            if (!p_41146_.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            p_41146_.awardStat(Stats.ITEM_USED.get(this));
            p_41146_.level.playSound((Player)null, p_41146_, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, p_41146_.getSoundSource(), 1.0F, 1.0F);
            ItemStack itemstack1 = ItemAmateMap.create(p_41145_, p_41146_.getBlockX(), p_41146_.getBlockZ(), (byte)0, true, true);
            if (itemstack.isEmpty()) {
                return InteractionResultHolder.consume(itemstack1);
            } else {
                if (!p_41146_.getInventory().add(itemstack1.copy())) {
                    p_41146_.drop(itemstack1, false);
                }

                return InteractionResultHolder.consume(itemstack);
            }
        }
    }
}
