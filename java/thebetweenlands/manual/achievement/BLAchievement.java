package thebetweenlands.manual.achievement;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;

/**
 * Created by Bart on 10-8-2015.
 */
public class BLAchievement {
    public String name;
    public String displayName;
    public String description;
    public ArrayList<BLAchievement> parents = new ArrayList<>();
    public ItemStack displayItem;
    public ResourceLocation resourceLocation;
    public int resourceX;
    public int resourceY;
    public int resourceWidth;
    public int resourceHeight;
    public boolean isCompleted = false;

    public ArrayList<ItemStack> rewards = new ArrayList<>();

    public BLAchievement(String name, String unlocalizedDisplayName, String unlocalizedDescription, ItemStack displayItem) {
        this.name = name;
        this.description = StatCollector.translateToLocal(unlocalizedDescription);
        this.displayName = StatCollector.translateToLocal(unlocalizedDisplayName);
        this.displayItem = displayItem;
    }

    public BLAchievement(String name, String unlocalizedDisplayName, String unlocalizedDescription, String resourceLocation) {
        this.name = name;
        this.description = StatCollector.translateToLocal(unlocalizedDescription);
        this.displayName = StatCollector.translateToLocal(unlocalizedDisplayName);
        this.resourceLocation = new ResourceLocation(resourceLocation);
    }

    public BLAchievement(String name, String unlocalizedDisplayName, String unlocalizedDescription, String resourceLocation, int x, int y, int width, int height) {
        this.name = name;
        this.description = StatCollector.translateToLocal(unlocalizedDescription);
        this.displayName = StatCollector.translateToLocal(unlocalizedDisplayName);
        this.resourceLocation = new ResourceLocation(resourceLocation);
        this.resourceX = x;
        this.resourceY = y;
        this.resourceWidth = width;
        this.resourceHeight = height;
    }

    public BLAchievement setParent(BLAchievement parent) {
        this.parents.add(parent);
        return this;
    }

    public BLAchievement setParents(ArrayList<BLAchievement> parents) {
        this.parents = parents;
        return this;
    }

    public BLAchievement setReward(ItemStack reward) {
        this.rewards.add(reward);
        return this;
    }

    public BLAchievement setRewards(ArrayList<ItemStack> rewards) {
        this.rewards = rewards;
        return this;
    }

    public void setCompleted() {
        this.isCompleted = true;
        AchievementCompleteManager.achievementComplete(this);
    }

    public boolean hasParents() {
        return parents.size() > 0;
    }

    public boolean canComplete() {
        boolean canComplete = true;
        for (BLAchievement achievement : parents)
            if (!achievement.isCompleted)
                canComplete = false;
        return canComplete;
    }
}
