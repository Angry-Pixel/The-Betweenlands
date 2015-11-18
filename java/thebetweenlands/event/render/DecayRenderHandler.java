package thebetweenlands.event.render;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.lib.ModInfo;

public class DecayRenderHandler {
	public static final DecayRenderHandler INSTANCE = new DecayRenderHandler();

	private static final ResourceLocation PLAYER_CORRUPTION_TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/player/playerCorruption.png");

	static class ModelBipedOverride extends ModelBiped {
		private ModelBiped parent;

		@Override
		public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float partialTicks) {
			this.parent.heldItemRight = DecayRenderHandler.INSTANCE.holdingItem ? 1 : 0;
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(PLAYER_CORRUPTION_TEXTURE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float glow = (float)((Math.cos(entity.ticksExisted / 10.0D) + 1.0D) / 2.0D) * 0.15F;
			float transparency = 0.85F * DecayManager.getCorruptionLevel((EntityPlayer)entity) / 10.0F - glow;
			boolean isHurt = ((EntityPlayer)entity).hurtTime > 0;
			GL11.glColor4f(1, isHurt ? 0.5F : 1.0F, isHurt ? 0.5F : 1.0F, isHurt ? transparency / 2.0F : transparency);
			this.parent.render(entity, x, y, z, yaw, pitch, partialTicks);
			GL11.glPopMatrix();
		}
	}

	private final ModelBipedOverride modelBipedOverride = new ModelBipedOverride();

	private boolean ignoreEvent = false;

	private boolean holdingItem = false;

	private Field f_mainModel = ReflectionHelper.findField(RendererLivingEntity.class, "mainModel", "field_77045_g", "i");

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if(!DecayManager.isDecayEnabled(event.entityPlayer) || DecayManager.getCorruptionLevel(event.entityPlayer) == 0 || this.ignoreEvent) return;

		event.setCanceled(true);

		this.ignoreEvent = true;

		double renderX = event.entity.posX + (event.entity.posX - event.entity.lastTickPosX) * event.partialRenderTick - RenderManager.renderPosX;
		double renderY = event.entity.posY + (event.entity.posY - event.entity.lastTickPosY) * event.partialRenderTick - RenderManager.renderPosY;
		double renderZ = event.entity.posZ + (event.entity.posZ - event.entity.lastTickPosZ) * event.partialRenderTick - RenderManager.renderPosZ;
		float yaw = event.entity.rotationYaw + (event.entity.rotationYaw - event.entity.prevRotationYaw) * event.partialRenderTick;

		//Render normal model with small depth offset to prevent z-fighting
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glPolygonOffset(0.01F, 1);
		event.renderer.doRender(event.entityPlayer, renderX, renderY, renderZ, yaw, event.partialRenderTick);
		GL11.glPolygonOffset(-0.01F, 1);
		GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

		ModelBiped prev = null;

		//Set model override
		try {
			prev = (ModelBiped) f_mainModel.get(event.renderer);
			this.modelBipedOverride.parent = prev;
			f_mainModel.set(event.renderer, this.modelBipedOverride);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		//Remove armor and held item
		ItemStack[] prevArmor = new ItemStack[4];
		for(int i = 0; i < 4; i++) {
			prevArmor[i] = event.entityPlayer.inventory.armorInventory[i];
			event.entityPlayer.inventory.armorInventory[i] = null;
		}
		ItemStack prevHeldItem = event.entityPlayer.getHeldItem();
		this.holdingItem = prevHeldItem != null;
		event.entityPlayer.setCurrentItemOrArmor(0, null);

		//Render model with overlay
		event.renderer.doRender(event.entityPlayer, renderX, renderY, renderZ, yaw, event.partialRenderTick);

		//Restore armor and held item
		for(int i = 0; i < 4; i++) {
			event.entityPlayer.inventory.armorInventory[i] = prevArmor[i];
		}
		event.entityPlayer.setCurrentItemOrArmor(0, prevHeldItem);

		//Restore previous model
		try {
			f_mainModel.set(event.renderer, prev);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		this.ignoreEvent = false;
	}
}
