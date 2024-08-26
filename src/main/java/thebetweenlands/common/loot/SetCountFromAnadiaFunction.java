package thebetweenlands.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.registries.LootFunctionRegistry;

import java.util.List;

public class SetCountFromAnadiaFunction extends LootItemConditionalFunction {

	public static final MapCodec<SetCountFromAnadiaFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(instance.group(
			Codec.FLOAT.fieldOf("size_start").forGetter(o -> o.sizeStart),
			Codec.FLOAT.fieldOf("size_end").forGetter(o -> o.sizeEnd),
			Codec.INT.fieldOf("min_count").forGetter(o -> o.minCount),
			Codec.INT.fieldOf("max_count").forGetter(o -> o.maxCount)))
		.apply(instance, SetCountFromAnadiaFunction::new));

	private final float sizeStart;
	private final float sizeEnd;
	private final int minCount;
	private final int maxCount;

	public SetCountFromAnadiaFunction(List<LootItemCondition> conditions, float sizeStart, float sizeEnd, int minCount, int maxCount) {
		super(conditions);
		this.sizeStart = sizeStart;
		this.sizeEnd = sizeEnd;
		this.minCount = minCount;
		this.maxCount = maxCount;
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		Entity entity = context.getParam(LootContextParams.THIS_ENTITY);

		if (entity instanceof Anadia anadia) {
			float size = anadia.getFishSize();

			int count = this.minCount + Mth.floor((Mth.clamp(size, this.sizeStart, this.sizeEnd) - this.sizeStart) / (this.sizeEnd - this.sizeStart) * (this.maxCount - this.minCount));

			if (count > stack.getMaxStackSize()) {
				TheBetweenlands.LOGGER.warn("Stack size {} exceeds max. stack size {} of loot item {}", count, stack.getMaxStackSize(), stack);
			} else {
				stack.setCount(count);
			}
		}

		return stack;
	}

	@Override
	public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
		return LootFunctionRegistry.SET_ANADIA_COUNT.get();
	}

	public static LootItemFunction.Builder setAnadiaCount(float sizeStart, float sizeEnd, int minCount, int maxCount) {
		return simpleBuilder(conditions -> new SetCountFromAnadiaFunction(conditions, sizeStart, sizeEnd, minCount, maxCount));
	}
}
