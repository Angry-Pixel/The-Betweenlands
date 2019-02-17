package thebetweenlands.common.world.gen.feature.loot;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.entity.spawning.IWeightProvider;

public class LootItemStack implements IWeightProvider {

    private final Item item;
    private short minDamage = 0, maxDamage = 0;
    private byte minAmount = 1, maxAmount = 1;
    private short weight = 1;

    public LootItemStack(Block block) {
        item = Item.getItemFromBlock(block);
    }

    public LootItemStack(Item item) {
        this.item = item;
    }

    public LootItemStack setDamage(int min, int max) {
        minDamage = (short) Math.max(1, min);
        maxDamage = (short) Math.max(1, max);
        return this;
    }

    public LootItemStack setDamage(int damage) {
        minDamage = maxDamage = (short) damage;
        return this;
    }

    public LootItemStack setAmount(int min, int max) {
        minAmount = (byte) Math.max(1, min);
        maxAmount = (byte) Math.max(1, max);
        return this;
    }

    public LootItemStack setAmount(int amount) {
        minAmount = maxAmount = (byte) Math.max(1, amount);
        return this;
    }

    public LootItemStack setWeight(int weight) {
        this.weight = (short) Math.max(1, weight);
        return this;
    }

    @Override
    public short getWeight() {
        return weight;
    }

    public ItemStack getIS(Random rand) {
        return new ItemStack(item, rand.nextInt(maxAmount - minAmount + 1) + minAmount, rand.nextInt(maxDamage - minDamage + 1) + minDamage);
    }
}