package thebetweenlands.manual.achievement;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by Bart on 10-8-2015.
 */
public class AchievementList {

    public static final BLAchievement achievement1 = new BLAchievement("achievement1", "betweenlands.achievement1.name", "betweenlands.achievement1.description", new ItemStack(Items.potato));
    public static final BLAchievement achievement2 = new BLAchievement("achievement2", "betweenlands.achievement2.name", "betweenlands.achievement2.description", new ItemStack(Items.bucket)).setParent(achievement1);
}
