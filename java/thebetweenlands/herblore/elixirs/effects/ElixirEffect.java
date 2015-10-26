package thebetweenlands.herblore.elixirs.effects;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.utils.PotionHelper;

public class ElixirEffect {
	private static class ElixirPotionEffect extends Potion {
		private final ElixirEffect effect;
		private final ResourceLocation icon;

		protected ElixirPotionEffect(ElixirEffect effect, String name, int color, ResourceLocation icon) {
			super(effect.potionID, false, color);
			this.setPotionName(name);
			this.effect = effect;
			this.icon = icon;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean hasStatusIcon() {
			return this.icon != null;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
			if(this.icon != null) {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				Minecraft.getMinecraft().renderEngine.bindTexture(this.icon);
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(x+6, y+6, 0, 0, 0);
				tessellator.addVertexWithUV(x+6, y+6+20, 0, 0, 1);
				tessellator.addVertexWithUV(x+6+20, y+6+20, 0, 1, 1);
				tessellator.addVertexWithUV(x+6+20, y+6, 0, 1, 0);
				tessellator.draw();
			}
		}

		@Override
		public boolean isReady(int ticks, int strength) {
			return this.id == this.effect.potionID ? this.effect.isReady(ticks, strength) : false;
		}

		@Override
		public void performEffect(EntityLivingBase entity, int strength) {
			if(this.id == this.effect.potionID) this.effect.performEffect(entity, strength);
		}

		@Override
		public void affectEntity(EntityLivingBase attacker, EntityLivingBase target, int strength, double distance) { 
			if(this.id == this.effect.potionID) this.effect.affectEntity(attacker, target, strength, distance);
		}
		
		@Override
		public double func_111183_a(int strength, AttributeModifier attributeModifier) {
			if(this.id == this.effect.potionID) return this.effect.getAttributeModifier(attributeModifier, strength);
	        return super.func_111183_a(strength, attributeModifier);
	    }
	}

	public PotionEffect createEffect(int duration, int strength) {
		return new PotionEffect(this.potionID, duration, strength);
	}

	private final ElixirPotionEffect potionEffect;
	private final String effectName;
	private final int potionID;
	private final int effectID;

	public ElixirEffect(int id, String name, ResourceLocation icon) {
		this.effectID = id;
		this.potionID = PotionHelper.getFreePotionId();
		this.potionEffect = new ElixirPotionEffect(this, name, 0xFFFF0000, icon);
		this.effectName = name;
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
	protected void affectEntity(EntityLivingBase attacker, EntityLivingBase target, int strength, double distance) { }

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
		this.potionEffect.func_111184_a(attribute, uuid, modifier, operation);
		return this;
	}

	public boolean isActive(EntityLivingBase entity) {
		Collection<PotionEffect> activePotions = entity.getActivePotionEffects();
		for(PotionEffect effect : activePotions) {
			if(effect.getPotionID() == this.potionID) {
				return true;
			}
		}
		return false;
	}

	public int getDuration(EntityLivingBase entity) {
		Collection<PotionEffect> activePotions = entity.getActivePotionEffects();
		for(PotionEffect effect : activePotions) {
			if(effect.getPotionID() == this.potionID) {
				return effect.getDuration();
			}
		}
		return -1;
	}

	public int getStrength(EntityLivingBase entity) {
		Collection<PotionEffect> activePotions = entity.getActivePotionEffects();
		for(PotionEffect effect : activePotions) {
			if(effect.getPotionID() == this.potionID) {
				return effect.getAmplifier();
			}
		}
		return -1;
	}
}
