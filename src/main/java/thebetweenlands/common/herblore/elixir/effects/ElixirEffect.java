package thebetweenlands.common.herblore.elixir.effects;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class ElixirEffect {
	public static final int VIAL_INFUSION_MAX_POTENCY = 5;

	private final ResourceLocation icon;
	private final int color;
	private final Map<Holder<Attribute>, AttributeTemplate> elixirAttributeModifiers = new Object2ObjectOpenHashMap<>();
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

	/**
	 * Whether this effect should be applied this tick
	 */
	protected boolean isReady(int ticks, int strength) {
		return true;
	}

	/**
	 * Effect over time
	 */
	protected void performEffect(LivingEntity entity, int strength) {
	}

	/**
	 * Instant effect
	 */
	protected void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity target, int amplifier, double health) {
	}

	/**
	 * Whether this affect should be applied instantly
	 *
	 * @return
	 */
	protected boolean isInstant() {
		return false;
	}

	/**
	 * Calculates the modifier from the attribute and elixir strength
	 *
	 * @param attributeModifier
	 * @param strength
	 * @return
	 */
	protected double getAttributeModifier(AttributeModifier attributeModifier, int strength) {
		return attributeModifier.amount() * (double) (strength + 1);
	}

	/**
	 * Adds an entity attribute modifier that is applied when the potion is active.
	 *
	 * @param attribute
	 * @param id
	 * @param modifier
	 * @param operation
	 * @return
	 */
	public ElixirEffect addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double modifier, AttributeModifier.Operation operation) {
		if (this.elixirEffect != null) {
			this.elixirEffect.value().addAttributeModifier(attribute, id, modifier, operation);
		} else {
			this.elixirAttributeModifiers.put(attribute, new AttributeTemplate(id, modifier, operation));
		}
		return this;
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

	public static class ElixirPotionEffect extends MobEffect {
		private final ElixirEffect effect;
		private final ResourceLocation icon;
		public String localizedElixirName;
		//private TextContainer nameContainer;

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

	record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
		public AttributeModifier create(int level) {
			return new AttributeModifier(this.id, this.amount * (double)(level + 1), this.operation);
		}
	}
}
