package thebetweenlands.common.component.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record ElixirContents(Optional<Holder<ElixirEffect>> elixir, int duration, int strength, Optional<Integer> customColor) {
	public static final ElixirContents EMPTY = new ElixirContents(Optional.empty(), 0, 0, Optional.empty());
	private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

	public static final Codec<ElixirContents> CODEC = RecordCodecBuilder.create(p_348387_ -> p_348387_.group(
		BLRegistries.ELIXIR_EFFECTS.holderByNameCodec().optionalFieldOf("elixir").forGetter(ElixirContents::elixir),
		Codec.INT.fieldOf("duration").forGetter(ElixirContents::duration),
		Codec.INT.fieldOf("strength").forGetter(ElixirContents::strength),
		Codec.INT.optionalFieldOf("custom_color").forGetter(ElixirContents::customColor)
	).apply(p_348387_, ElixirContents::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ElixirContents> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.holderRegistry(BLRegistries.Keys.ELIXIR_EFFECTS).apply(ByteBufCodecs::optional), ElixirContents::elixir,
		ByteBufCodecs.INT, ElixirContents::duration,
		ByteBufCodecs.INT, ElixirContents::strength,
		ByteBufCodecs.INT.apply(ByteBufCodecs::optional), ElixirContents::customColor,
		ElixirContents::new
	);

	public ElixirContents(Holder<ElixirEffect> elixir, int duration, int strength) {
		this(Optional.of(elixir), duration, strength, Optional.empty());
	}

	public static ItemStack createItemStack(Item item, Holder<ElixirEffect> elixir, int duration, int strength) {
		ItemStack itemstack = new ItemStack(item);
		itemstack.set(DataComponentRegistry.ELIXIR_CONTENTS, new ElixirContents(elixir, duration, strength));
		return itemstack;
	}

	public int getElixirColor() {
		return this.elixir().map(elixir -> elixir.value().getColor()).orElse(-13083194);
	}

	public MobEffectInstance createEffect(Holder<ElixirEffect> effect, double modifier) {
		return effect.value().createEffect((int) (this.duration() * modifier), this.strength());
	}

	public void addElixirTooltip(Consumer<Component> tooltip, HolderLookup.Provider provider, float durationFactor, float ticksPerSecond) {
		if (this.elixir().isPresent()) {
			List<Pair<Holder<Attribute>, AttributeModifier>> list = Lists.newArrayList();
			ElixirEffect effect = this.elixir().get().value();
			MobEffectInstance mobEffect = this.createEffect(this.elixir().get(), 1.0D);
			effect.createModifiers(mobEffect.getAmplifier(), (holder, modifier) -> list.add(new Pair<>(holder, modifier)));

			tooltip.accept(Component.translatable("item.thebetweenlands.elixir.potency", this.getRomanNumeral(mobEffect.getAmplifier() + 1)).withStyle(ChatFormatting.BLUE));

			int durationLevel;
			Holder<ElixirRecipe> recipe = ElixirRecipe.getRecipeFor(this.elixir().get(), provider);
			if (recipe != null) {
				int baseDuration = effect.isAntiInfusion() ? recipe.value().negativeBaseDuration() : recipe.value().baseDuration();
				int durationModifier = effect.isAntiInfusion() ? recipe.value().negativeDurationModifier() : recipe.value().durationModifier();
				durationLevel = Math.max(0, Mth.floor((mobEffect.getDuration() - baseDuration) / (float)durationModifier * ElixirEffect.VIAL_INFUSION_MAX_POTENCY));
			} else {
				durationLevel = Mth.floor(mobEffect.getDuration() / 3600.0F);
			}

			tooltip.accept(Component.translatable("item.thebetweenlands.elixir.duration", this.getRomanNumeral(durationLevel + 1), MobEffectUtil.formatDuration(mobEffect, durationFactor, ticksPerSecond)).withStyle(ChatFormatting.BLUE));

			boolean hasEffectDescription = I18n.exists("item.thebetweenlands.elixir." + this.elixir().get().getKey().location().getPath() + "_effect");

			if (!list.isEmpty() || hasEffectDescription) {
				tooltip.accept(CommonComponents.EMPTY);
				tooltip.accept(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

				for (Pair<Holder<Attribute>, AttributeModifier> pair : list) {
					AttributeModifier attributemodifier = pair.getSecond();
					double d1 = attributemodifier.amount();
					double d0;
					if (attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE
						&& attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
						d0 = attributemodifier.amount();
					} else {
						d0 = attributemodifier.amount() * 100.0D;
					}

					if (d1 > 0.0) {
						tooltip.accept(Component.translatable("attribute.modifier.plus." + attributemodifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable(pair.getFirst().value().getDescriptionId())).withStyle(ChatFormatting.BLUE));
					} else if (d1 < 0.0) {
						d0 *= -1.0;
						tooltip.accept(Component.translatable("attribute.modifier.take." + attributemodifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable(pair.getFirst().value().getDescriptionId())).withStyle(ChatFormatting.RED));
					}
				}

				if (hasEffectDescription) {
					tooltip.accept(Component.translatable("item.thebetweenlands.elixir." + this.elixir().get().getKey().location().getPath() + "_effect").withStyle(ChatFormatting.GRAY));
				}
			}
		} else {
			tooltip.accept(NO_EFFECT);
		}
	}

	public String getRomanNumeral(int number) {
		return String.join("", "I".repeat(number))
			.replace("IIIII", "V")
			.replace("IIII", "IV")
			.replace("VV", "X")
			.replace("VIV", "IX")
			.replace("XXXXX", "L")
			.replace("XXXX", "XL")
			.replace("LL", "C")
			.replace("LXL", "XC")
			.replace("CCCCC", "D")
			.replace("CCCC", "CD")
			.replace("DD", "M")
			.replace("DCD", "CM");
	}
}
