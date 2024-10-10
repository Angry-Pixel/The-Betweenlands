package thebetweenlands.common.item.misc;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import thebetweenlands.common.registries.EntityRegistry;

public class CryptCrawlerSpawnEggItem extends DeferredSpawnEggItem {

	private static final RandomSource RANDOM = RandomSource.create();

	public CryptCrawlerSpawnEggItem(Properties properties) {
		super(EntityRegistry.CRYPT_CRAWLER, 0xD9B88C, 0x8F5F44, properties);
	}

	@Override
	public EntityType<?> getType(ItemStack stack) {
		if (RANDOM.nextInt(3) == 0) {
			if (RANDOM.nextInt(3) == 0) {
				return EntityRegistry.CHIEF_CRYPT_CRAWLER.get();
			}
			return EntityRegistry.BIPED_CRYPT_CRAWLER.get();
		}
		return super.getType(stack);
	}
}
