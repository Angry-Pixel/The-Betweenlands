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

public class ElixirEffect {
	public static final int VIAL_INFUSION_MAX_POTENCY = 5;

	private final ResourceLocation icon;
	private final int color;
	private final Map<Holder<Attribute>, AttributeTemplate> elixirAttributeModifiers = new Object2ObjectOpenHashMap<>();;
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
		private String localizedElixirName;
		//private TextContainer nameContainer;

		public ElixirPotionEffect(ElixirEffect effect, int color, ResourceLocation icon) {
			super(MobEffectCategory.BENEFICIAL, color);
			this.effect = effect;
			this.icon = icon;
		}

		@Override
		public boolean isInstantenous() {
			return this.effect.isInstant();
		}

		@Override
		public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
			consumer.accept(new IClientMobEffectExtensions() {
				@Override
				public boolean isVisibleInInventory(MobEffectInstance instance) {
					return ElixirPotionEffect.this.icon != null;
				}

				@Override
				public boolean isVisibleInGui(MobEffectInstance instance) {
					return ElixirPotionEffect.this.icon != null;
				}

				@Override
				public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
					if (ElixirPotionEffect.this.icon != null) {
						RenderSystem.enableBlend();
						RenderSystem.setShaderTexture(0, ElixirPotionEffect.this.icon);
						Tesselator tesselator = Tesselator.getInstance();

						BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
						builder.addVertex(x + 6, y + 6, 0).setUv(0, 0);
						builder.addVertex(x + 6, y + 6 + 20, 0).setUv(0, 1);
						builder.addVertex(x + 6 + 20, y + 6 + 20, 0).setUv(1, 1);
						builder.addVertex(x + 6 + 20, y + 6, 0).setUv(1, 0);
						BufferUploader.drawWithShader(builder.buildOrThrow());
					}
					if (ElixirPotionEffect.this.localizedElixirName == null) {
						ElixirPotionEffect.this.localizedElixirName = ElixirPotionEffect.this.getDisplayName().getString();
					}
					//TODO reimplement once book is added
//					if (ElixirPotionEffect.this.nameContainer == null) {
//						ElixirPotionEffect.this.nameContainer = new TextContainer(88, 100, this.localizedElixirName, Minecraft.getInstance().font);
//						int width = Minecraft.getInstance().font.width(ElixirPotionEffect.this.localizedElixirName);
//						float scale = 1.0F;
//						if (width > 88) {
//							scale = 88.0F / (float) width;
//							scale -= scale % 0.25F;
//						}
//						if (scale < 0.5F) {
//							scale = 0.5F;
//						}
//						ElixirPotionEffect.this.nameContainer.setCurrentScale(scale);
//						ElixirPotionEffect.this.nameContainer.setCurrentColor(0xFFFFFFFF);
//						try {
//							ElixirPotionEffect.this.nameContainer.parse();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//					if (ElixirPotionEffect.this.nameContainer != null && ElixirPotionEffect.this.nameContainer.getPages().size() > 0) {
//						TextContainer.TextPage page0 = this.nameContainer.getPages().get(0);
//						page0.render(x + 28, y + 6);
//						String s = Potion.getPotionDurationString(ElixirPotionEffect.this.effect, 1.0F);
//						graphics.drawString(Minecraft.getInstance().font, s, (float) (x + 10 + 18), (float) (y + 6 + 10), 8355711, true);
//					}
					return true;
				}

				@Override
				public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
					return true;
				}

				@Override
				public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
					if (ElixirPotionEffect.this.icon != null) {
						RenderSystem.enableBlend();
						RenderSystem.setShaderTexture(0, ElixirPotionEffect.this.icon);

						Tesselator tesselator = Tesselator.getInstance();

						BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
						builder.addVertex(x + 2, y + 2, 0).setUv(0, 0);
						builder.addVertex(x + 2, y + 2 + 20, 0).setUv(0, 1);
						builder.addVertex(x + 2 + 20, y + 2 + 20, 0).setUv(1, 1);
						builder.addVertex(x + 2 + 20, y + 2, 0).setUv(1, 0);
						BufferUploader.drawWithShader(builder.buildOrThrow());
					}
					return true;
				}
			});
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
