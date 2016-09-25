package thebetweenlands.common.herblore.elixir.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElixirEffect {
	private final String effectName;
	private final int effectID;
	private final ResourceLocation icon;
	private final int color;
	private List<ElixirAttributeModifier> elixirAttributeModifiers = new ArrayList<ElixirAttributeModifier>();
	private ElixirPotionEffect potionEffect;
	private ResourceLocation potionID;
	private boolean isAntiInfusion = false;
	public ElixirEffect(int id, String name) {
		this(id, name, null, 0x00000000);
	}
	public ElixirEffect(int id, String name, ResourceLocation icon) {
		this(id, name, icon, 0x00000000);
	}
	public ElixirEffect(int id, String name, int color) {
		this(id, name, null, color);
	}

	public ElixirEffect(int id, String name, ResourceLocation icon, int color) {
		this.effectID = id;
		this.effectName = name;
		this.icon = icon;
		this.color = color;
	}

	public PotionEffect createEffect(int duration, int strength) {
		return new PotionEffect(Potion.getPotionFromResourceLocation(this.potionID.toString()), duration, strength);
	}

	public void registerPotion() {
		this.potionID = null;//TODO Potion IDs/*ConfigHandler.potionIDs.get(this.getID())*/;
		this.potionEffect = new ElixirPotionEffect(this, this.effectName, this.color, this.icon);
		Potion.REGISTRY.register(this.getID(), this.potionID, this.potionEffect);
		for(ElixirAttributeModifier modifier : this.elixirAttributeModifiers) {
			this.potionEffect.registerPotionAttributeModifier(modifier.attribute, modifier.uuid.toString(), modifier.modifier, modifier.operation);
		}
	}

	public int getID() {
		return this.effectID;
	}

	public String getEffectName() {
		return this.effectName;
	}

	public ResourceLocation getIcon() {
		return this.potionEffect.icon;
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
	protected void performEffect(EntityLivingBase entity, int strength) { }

	/**
	 * Instant effect
	 */
	protected void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase target, int amplifier, double health) { }

	/**
	 * Whether this affect should be applied instantly
	 * @return
	 */
	protected boolean isInstant() {
		return false;
	}

	/**
	 * Calculates the modifier from the attribute and elixir strength
	 * @param attributeModifier
	 * @param strength
	 * @return
	 */
	protected double getAttributeModifier(AttributeModifier attributeModifier, int strength) {
		return attributeModifier.getAmount() * (double)(strength + 1);
	}

	/**
	 * Adds an entity attribute modifier that is applied when the potion is active.
	 * @param attribute
	 * @param uuid
	 * @param modifier
	 * @param operation
	 * @return
	 */
	public ElixirEffect addAttributeModifier(IAttribute attribute, String uuid, double modifier, int operation) {
		if(this.potionEffect != null) {
			this.potionEffect.registerPotionAttributeModifier(attribute, uuid, modifier, operation);
		} else {
			this.elixirAttributeModifiers.add(new ElixirAttributeModifier(attribute, uuid, modifier, operation));
		}
		return this;
	}

	public ElixirEffect setAntiInfusion(){
		isAntiInfusion = true;
		return this;
	}

	public boolean isAntiInfusion(){
		return isAntiInfusion;
	}

	public boolean isActive(EntityLivingBase entity) {
		if(entity == null) return false;
		Collection<PotionEffect> activePotions = entity.getActivePotionEffects();
		for(PotionEffect effect : activePotions) {
			if(effect.getPotion().getRegistryName() == this.potionID) {
				return true;
			}
		}
		return false;
	}

	public int getDuration(EntityLivingBase entity) {
		if(entity == null) return -1;
		Collection<PotionEffect> activePotions = entity.getActivePotionEffects();
		for(PotionEffect effect : activePotions) {
			if(effect.getPotion().getRegistryName() == this.potionID) {
				return effect.getDuration();
			}
		}
		return -1;
	}

	public int getStrength(EntityLivingBase entity) {
		if(entity == null) return -1;
		Collection<PotionEffect> activePotions = entity.getActivePotionEffects();
		for(PotionEffect effect : activePotions) {
			if(effect.getPotion().getRegistryName() == this.potionID) {
				return effect.getAmplifier();
			}
		}
		return -1;
	}

	public PotionEffect getPotionEffect(EntityLivingBase entity) {
		if(entity.isPotionActive(Potion.getPotionFromResourceLocation(this.potionID.toString()))) {
			return entity.getActivePotionEffect(this.potionEffect);
		}
		return null;
	}

	public void removeElixir(EntityLivingBase entity) {
		entity.removePotionEffect(Potion.getPotionFromResourceLocation(this.potionID.toString()));
	}

	public ElixirPotionEffect getPotionEffect() {
		return this.potionEffect;
	}

	private static class ElixirAttributeModifier {
		private final IAttribute attribute;
		private final String uuid;
		private final double modifier;
		private final int operation;
		private ElixirAttributeModifier(IAttribute attribute, String uuid, double modifier, int operation) {
			this.attribute = attribute;
			this.uuid = uuid;
			this.modifier = modifier;
			this.operation = operation;
		}
	}

	public static class ElixirPotionEffect extends Potion {
		private final ElixirEffect effect;
		private final ResourceLocation icon;

		//private final String elixirName;
		//private TextContainer nameContainer;

		protected ElixirPotionEffect(ElixirEffect effect, String name, int color, ResourceLocation icon) {
			super(false, color);
			this.setPotionName(name);
			//this.elixirName = I18n.format(name);
			this.effect = effect;
			this.icon = icon;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean hasStatusIcon() {
			return this.icon != null;
		}

		@Override
		public boolean isInstant() {
			return this.effect.isInstant();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
			super.renderInventoryEffect(x, y, effect, mc);
			//TODO: Potion icon renderer
			/*if(this.icon != null) {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				Minecraft.getMinecraft().renderEngine.bindTexture(this.icon);
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(x+6, y+6, 0, 0, 0);
				tessellator.addVertexWithUV(x+6, y+6+20, 0, 0, 1);
				tessellator.addVertexWithUV(x+6+20, y+6+20, 0, 1, 1);
				tessellator.addVertexWithUV(x+6+20, y+6, 0, 1, 0);
				tessellator.draw();
			}
			if(this.nameContainer == null) {
				this.nameContainer = new TextContainer(88, 100, this.elixirName, Minecraft.getMinecraft().fontRenderer);
				int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(this.elixirName);
				float scale = 1.0F;
				if(width > 88) {
					scale = 88.0F / (float)width;
					scale -= scale % 0.25F;
				}
				if(scale < 0.5F) {
					scale = 0.5F;
				}
				this.nameContainer.setCurrentScale(scale);
				this.nameContainer.setCurrentColor(0xFFFFFFFF);
				try {
					this.nameContainer.parse();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(this.nameContainer != null && this.nameContainer.getPages().size() > 0) {
				this.setPotionName("");
				TextPage page0 = this.nameContainer.getPages().get(0);
				page0.render(x + 28, y + 6);
			}*/
		}

		@Override
		public boolean isReady(int ticks, int strength) {
			return this.effect.isReady(ticks, strength);
		}

		@Override
		public void performEffect(EntityLivingBase entity, int strength) {
			this.effect.performEffect(entity, strength);
		}

		@Override
		public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase target, int amplifier, double health) {
			this.effect.affectEntity(source, indirectSource, target, amplifier, health);
		}

		@Override
		public double getAttributeModifierAmount(int strength, AttributeModifier attributeModifier) {
			return this.effect.getAttributeModifier(attributeModifier, strength);
		}
	}
}
