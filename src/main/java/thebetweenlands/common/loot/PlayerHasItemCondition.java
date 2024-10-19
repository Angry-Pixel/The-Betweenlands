package thebetweenlands.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.EnchantmentActiveCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import thebetweenlands.common.registries.LootFunctionRegistry;

public record PlayerHasItemCondition(ItemStack stack) implements LootItemCondition {

	public static final MapCodec<PlayerHasItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(PlayerHasItemCondition::stack))
		.apply(instance, PlayerHasItemCondition::new));



	@Override
	public LootItemConditionType getType() {
		return LootFunctionRegistry.HAS_ITEM.get();
	}

	public static LootItemCondition.Builder hasItem(ItemStack stack) {
		return () -> new PlayerHasItemCondition(stack);
	}

	public static LootItemCondition.Builder hasItem(ItemLike item) {
		return () -> new PlayerHasItemCondition(new ItemStack(item));
	}

	@Override
	public boolean test(LootContext context) {
		if (context.getParamOrNull(LootContextParams.ATTACKING_ENTITY) instanceof Player player) {
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack invStack = player.getInventory().getItem(i);
				if (!invStack.isEmpty() && ItemStack.isSameItemSameComponents(invStack, this.stack())) return true;
			}
		}
		return false;
	}
}
