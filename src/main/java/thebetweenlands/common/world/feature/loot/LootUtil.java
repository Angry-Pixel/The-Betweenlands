package thebetweenlands.common.world.feature.loot;

import net.minecraft.inventory.IInventory;

import java.util.Random;

public class LootUtil {
    public static void generateLoot(IInventory inventory, Random rand, WeightedLootList lootList, int minAmount, int maxAmount) {
        int amount = rand.nextInt(Math.max(1, maxAmount - minAmount + 1)) + minAmount;

        for (int a = 0; a < amount; a++)
            inventory.setInventorySlotContents(rand.nextInt(inventory.getSizeInventory()), lootList.generateIS(rand));
    }
}
