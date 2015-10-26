package thebetweenlands.herblore.elixirs.effects;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
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
	public boolean isReady(int ticks, int strength) {
		return true;
	}

	/**
	 * Effect over time
	 */
	public void performEffect(EntityLivingBase entity, int strength) { }

	/**
	 * Instant effect
	 */
	public void affectEntity(EntityLivingBase attacker, EntityLivingBase target, int strength, double distance) { }

	public boolean isActive(EntityLivingBase entity) {
		Collection<PotionEffect> activePotions = entity.getActivePotionEffects();
		for(PotionEffect effect : activePotions) {
			if(effect.getPotionID() == this.potionID) {
				return true;
			}
		}
		return false;
	}
}
