package thebetweenlands.common.world.gen.feature.loot;

import java.util.Random;

import net.minecraft.item.ItemStack;
import thebetweenlands.util.WeightedList;

public class WeightedLootList extends WeightedList<LootItemStack> {

    private static final long serialVersionUID = 6987523146089169906L;

    private static final IPostProcess genericPostProcessor = new IPostProcess() {
        @Override
        public ItemStack postProcessItem(ItemStack is, Random rand) {
            return is;
        }
    };

    private IPostProcess postProcessor = genericPostProcessor;

    public WeightedLootList(LootItemStack[] items) {
        for (LootItemStack item : items)
            add(item);
    }

    public WeightedLootList setPostProcessor(IPostProcess postProcessor) {
        this.postProcessor = postProcessor;
        if (this.postProcessor == null)
            this.postProcessor = genericPostProcessor;
        return this;
    }

    public ItemStack generateIS(Random rand) {
        LootItemStack is = super.getRandomItem(rand);
        return is == null ? null : postProcessor.postProcessItem(is.getIS(rand), rand);
    }
}
