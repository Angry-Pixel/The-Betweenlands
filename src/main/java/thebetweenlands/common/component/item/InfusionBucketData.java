package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.herblore.aspect.AspectManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record InfusionBucketData(List<ItemStack> ingredients, int infusionTime) {

	public static InfusionBucketData EMPTY = new InfusionBucketData(List.of(), 0);

	public static final Codec<InfusionBucketData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ItemStack.OPTIONAL_CODEC.listOf().fieldOf("ingredients").forGetter(InfusionBucketData::ingredients),
		Codec.INT.fieldOf("infusion_time").forGetter(InfusionBucketData::infusionTime)
	).apply(instance, InfusionBucketData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, InfusionBucketData> STREAM_CODEC = StreamCodec.composite(
		ItemStack.OPTIONAL_LIST_STREAM_CODEC, InfusionBucketData::ingredients,
		ByteBufCodecs.INT, InfusionBucketData::infusionTime,
		InfusionBucketData::new
	);

	public void addTooltipInformation(Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.thebetweenlands.infusion_bucket.time", StringUtil.formatTickDuration(this.infusionTime(), context.tickRate()), this.infusionTime()).withStyle(ChatFormatting.GREEN));
		tooltip.add(Component.translatable("item.thebetweenlands.infusion_bucket.ingredients").withStyle(ChatFormatting.GREEN));
		Map<ItemStack, Integer> stackMap = new LinkedHashMap<>();
		for (ItemStack ingredient : this.ingredients()) {
			boolean contained = false;
			for (Map.Entry<ItemStack, Integer> stackCount : stackMap.entrySet()) {
				if (ItemStack.isSameItemSameComponents(stackCount.getKey(), ingredient)) {
					stackMap.put(stackCount.getKey(), stackCount.getValue() + 1);
					contained = true;
				}
			}
			if (!contained) {
				stackMap.put(ingredient, 1);
			}
		}
		for (Map.Entry<ItemStack, Integer> stackCount : stackMap.entrySet()) {
			ItemStack ingredient = stackCount.getKey();
			int count = stackCount.getValue();
			if (!ingredient.isEmpty()) {
				if (count > 1) {
					tooltip.add(Component.translatable("item.thebetweenlands.infusion_bucket.ingredient_amount", count, ingredient.getDisplayName().getString()).withStyle(ChatFormatting.GRAY));
				} else {
					tooltip.add(ingredient.getHoverName().copy().withStyle(ChatFormatting.GRAY));
				}
				List<Aspect> ingredientAspects = AspectContents.getAspectsFromContainer(ingredient, context.registries(), AspectManager.get(context.level()), DiscoveryContainerData.getMergedDiscoveryContainer(BetweenlandsClient.getClientPlayer()));
				if (!ingredientAspects.isEmpty()) {
					if (flag.hasShiftDown()) {
						for (Aspect aspect : ingredientAspects) {
							tooltip.add(Component.translatable("item.thebetweenlands.infusion_bucket.aspect", AspectType.getAspectName(aspect.type()), Aspect.ASPECT_AMOUNT_FORMAT.format(aspect.getDisplayAmount() * count)).withStyle(ChatFormatting.GRAY));
						}
					}
				}
			}
		}
	}
}
