package thebetweenlands.common.herblore.elixir.effects;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.Potion;

public class ElixirEffect {
	public static final int VIAL_INFUSION_MAX_POTENCY = 5;

	private final ResourceLocation icon;
	private final int color;
	public final Map<Holder<Attribute>, AttributeTemplate> elixirAttributeModifiers = new Object2ObjectOpenHashMap<>();
	private Holder<MobEffect> elixirEffect;
	private Holder<MobEffect> effect;
	private boolean isAntiInfusion = false;
	private boolean showInBook = false;


	public ElixirEffect() {
		this(null, 0x00000000);
	}

	public ElixirEffect(ResourceLocation icon) {
		this(icon, 0x00000000);
	}

	public ElixirEffect(int color) {
		this(null, color);
	}

	public ElixirEffect(ResourceLocation icon, int color) {
		this.icon = icon;
		this.color = color;
	}

	public ElixirEffect setShowInBook() {
		this.showInBook = true;
		return this;
	}

	public boolean shouldShowInBook() {
		return this.showInBook;
	}

	public MobEffectInstance createEffect(int duration, int strength) {
		return new MobEffectInstance(this.effect, duration, strength);
	}

	public MobEffectInstance createEffect(int duration, int strength, boolean ambient, boolean showParticles) {
		return new MobEffectInstance(this.effect, duration, strength, ambient, showParticles);
	}

	public int getColor() {
		return this.color;
	}

	public ResourceLocation getIcon() {
		return this.icon;
	}

	protected boolean isReady(int ticks, int strength) {
		return true;
	}

	protected void performEffect(LivingEntity entity, int strength) {
	}

	protected boolean isInstant() {
		return false;
	}

	public ElixirEffect addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double modifier, AttributeModifier.Operation operation) {
		if (this.elixirEffect != null) {
			this.elixirEffect.value().addAttributeModifier(attribute, id, modifier, operation);
		} else {
			this.elixirAttributeModifiers.put(attribute, new AttributeTemplate(id, modifier, operation));
		}
		return this;
	}

	public void createModifiers(int amplifier, BiConsumer<Holder<Attribute>, AttributeModifier> output) {
		this.elixirAttributeModifiers.forEach((p_349971_, p_349972_) -> output.accept(p_349971_, p_349972_.create(amplifier)));
	}

	public ElixirEffect setElixirEffect(Holder<MobEffect> effectHolder) {
		this.effect = effectHolder;
		this.elixirEffect = effectHolder;
		return this;
	}

	public ElixirEffect setGeneralEffect(Holder<MobEffect> effectHolder) {
		this.effect = effectHolder;
		this.elixirEffect = null;
		return this;
	}

	public ElixirEffect setAntiInfusion() {
		this.isAntiInfusion = true;
		return this;
	}

	public boolean isAntiInfusion() {
		return this.isAntiInfusion;
	}

	public boolean isActive(LivingEntity entity) {
		if (entity == null) return false;
		Collection<MobEffectInstance> activePotions = entity.getActiveEffects();
		for (MobEffectInstance effect : activePotions) {
			if (effect.is(this.effect)) {
				return true;
			}
		}
		return false;
	}

	public int getDuration(LivingEntity entity) {
		if (entity == null) return -1;
		Collection<MobEffectInstance> activePotions = entity.getActiveEffects();
		for (MobEffectInstance effect : activePotions) {
			if (effect.is(this.effect)) {
				return effect.getDuration();
			}
		}
		return -1;
	}

	public int getStrength(LivingEntity entity) {
		if (entity == null) return -1;
		Collection<MobEffectInstance> activePotions = entity.getActiveEffects();
		for (MobEffectInstance effect : activePotions) {
			if (effect.is(this.effect)) {
				return effect.getAmplifier();
			}
		}
		return -1;
	}

	public MobEffectInstance getPotionEffect(LivingEntity entity) {
		if (entity.hasEffect(this.effect)) {
			return entity.getEffect(this.elixirEffect);
		}
		return null;
	}

	public void removeElixir(LivingEntity entity) {
		entity.removeEffect(this.effect);
	}

	public Holder<MobEffect> getElixirEffect() {
		return this.elixirEffect;
	}

	public static String getName(Optional<Holder<ElixirEffect>> elixir, String descriptionId) {
		return elixir.map(elixirEffectHolder -> "item.thebetweenlands.elixir." + elixirEffectHolder.getKey().location().getPath()).orElse(descriptionId);
	}

	public static class ElixirPotionEffect extends MobEffect {
		private final ElixirEffect effect;
		private final ResourceLocation icon;

		public ElixirPotionEffect(ElixirEffect effect, int color, ResourceLocation icon) {
			super(MobEffectCategory.BENEFICIAL, color);
			this.effect = effect;
			this.icon = icon;
		}

		public ResourceLocation getIcon() {
			return this.icon;
		}

		@Override
		public boolean isInstantenous() {
			return this.effect.isInstant();
		}

		@Override
		public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
			return this.effect.isReady(duration, amplifier);
		}

		@Override
		public boolean applyEffectTick(LivingEntity entity, int amplifier) {
			this.effect.performEffect(entity, amplifier);
			return true;
		}
	}

	public record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
		public AttributeModifier create(int level) {
			return new AttributeModifier(this.id, this.amount * (double)(level + 1), this.operation);
		}
	}
}
