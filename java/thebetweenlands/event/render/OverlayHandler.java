package thebetweenlands.event.render;

import java.lang.reflect.Method;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderHandEvent;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.recipes.BLMaterials;

public class OverlayHandler {
	public static final OverlayHandler INSTANCE = new OverlayHandler();

	private Method mERrenderHand;
	private boolean cancelOverlay = false;

	private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
	private static final ResourceLocation RES_TAR_OVERLAY = new ResourceLocation("thebetweenlands:textures/blocks/tar.png");
	private static final ResourceLocation RES_MUD_OVERLAY = new ResourceLocation("thebetweenlands:textures/blocks/mud.png");

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
		if(this.cancelOverlay) {
			event.setCanceled(true);
			return;
		}
		if(event.overlayType == OverlayType.WATER) {
			event.setCanceled(true);

			Minecraft mc = Minecraft.getMinecraft();
			mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
			int colorMultiplier = BLBlockRegistry.swampWater.colorMultiplier(mc.theWorld, MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.posY), MathHelper.floor_double(mc.thePlayer.posZ));
			float r = (float)(colorMultiplier >> 16 & 255) / 255.0F / 2.0F;
			float g = (float)(colorMultiplier >> 8 & 255) / 255.0F / 2.0F;
			float b = (float)(colorMultiplier & 255) / 255.0F / 2.0F;
			GL11.glColor4f(r, g, b, 1.0F);
			this.renderWarpedTextureOverlay(event.renderPartialTicks);
		}
	}

	public void renderHand(float partialTicks, int renderPass, boolean overlay) {
		if(this.mERrenderHand == null) {
			try {
				this.mERrenderHand = ReflectionHelper.findMethod(EntityRenderer.class, null, new String[]{"renderHand", "func_78476_b", "b"}, new Class[]{float.class, int.class});
				this.mERrenderHand.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.cancelOverlay = !overlay;
		try {
			this.mERrenderHand.invoke(Minecraft.getMinecraft().entityRenderer, partialTicks, renderPass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.cancelOverlay = false;
	}

	private ModelArmOverride modelArmOverride = null;

	static class ModelArmOverride extends ModelRenderer {
		public ModelArmOverride(ModelBase modelBase) {
			super(modelBase);
		}

		public boolean holdingItem = false;

		private ModelRenderer parent;

		private EntityPlayer entity;

		@Override
		@SideOnly(Side.CLIENT)
		public void render(float partialTicks) {
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(DecayRenderHandler.PLAYER_CORRUPTION_TEXTURE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float glow = (float)((Math.cos(entity.ticksExisted / 10.0D) + 1.0D) / 2.0D) * 0.15F;
			float transparency = 0.85F * DecayManager.getCorruptionLevel((EntityPlayer)entity) / 10.0F - glow;
			GL11.glColor4f(1, 1, 1, transparency);
			this.parent.render(partialTicks);
			Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());
			GL11.glPopMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		EntityLivingBase view = Minecraft.getMinecraft().renderViewEntity;
		World world = Minecraft.getMinecraft().theWorld;

		event.setCanceled(true);
		
		GL11.glPushMatrix();
		
		//Render normal hand with overlays
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		this.renderHand(event.partialTicks, event.renderPass, true);

		//Render decay overlay
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		RenderPlayer playerRenderer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(Minecraft.getMinecraft().thePlayer);
		if(this.modelArmOverride == null && playerRenderer.modelBipedMain != null) {
			this.modelArmOverride = new ModelArmOverride(playerRenderer.modelBipedMain);
		}
		this.modelArmOverride.parent = playerRenderer.modelBipedMain.bipedRightArm;
		this.modelArmOverride.entity = Minecraft.getMinecraft().thePlayer;
		ModelRenderer previousModel = playerRenderer.modelBipedMain.bipedRightArm;
		playerRenderer.modelBipedMain.bipedRightArm = this.modelArmOverride;
		this.renderHand(event.partialTicks, event.renderPass, false);
		playerRenderer.modelBipedMain.bipedRightArm = previousModel;

		//Render other overlays
		if(view == null || world == null) {
			GL11.glPopMatrix();
			return;
		}
		Block viewBlock = ActiveRenderInfo.getBlockAtEntityViewpoint(world, view, (float) event.partialTicks);
		List<EntityTarBeast> entitiesInside = world.getEntitiesWithinAABB(EntityTarBeast.class, view.boundingBox.expand(-0.25F, -0.25F, -0.25F));
		boolean inTar = (viewBlock.getMaterial() == BLMaterials.tar || (entitiesInside != null && entitiesInside.size() > 0));
		int bx = MathHelper.floor_double(view.posX);
		int by = MathHelper.floor_double(view.posY);
		int bz = MathHelper.floor_double(view.posZ);
		Block block = world.getBlock(bx, by, bz);
		boolean inMud = block.getMaterial() == BLMaterials.mud;
		boolean inBlock = inTar || inMud;
		if(inBlock && !this.cancelOverlay) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			Minecraft mc = Minecraft.getMinecraft();
			if(inTar) {
				mc.getTextureManager().bindTexture(RES_TAR_OVERLAY);
				GL11.glColor4f(1, 1, 1, 0.985F);
				GL11.glScaled(1, 20, 1);
			} else if(inMud) {
				GL11.glColor4f(0.25F, 0.25F, 0.25F, 1);
				mc.getTextureManager().bindTexture(RES_MUD_OVERLAY);
			}

			this.renderWarpedTextureOverlay(event.partialTicks);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		GL11.glPopMatrix();
	}

	/**
	 * Renders the water overlay.
	 * Set color and bind texture before calling this method.
	 */
	private void renderWarpedTextureOverlay(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		Tessellator tessellator = Tessellator.instance;
		float brightness = mc.thePlayer.getBrightness(partialTicks);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glPushMatrix();
		float tu = 4.0F;
		float minX = -1.0F;
		float maxX = 1.0F;
		float minY = -1.0F;
		float maxY = 1.0F;
		float z = -0.5F;
		float tuOffset = -mc.thePlayer.rotationYaw / 64.0F;
		float tvOffset = mc.thePlayer.rotationPitch / 64.0F;
		tessellator.setBrightness((int)(brightness*255.0F));
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double)minX, (double)minY, (double)z, (double)(tu + tuOffset), (double)(tu + tvOffset));
		tessellator.addVertexWithUV((double)maxX, (double)minY, (double)z, (double)(0.0F + tuOffset), (double)(tu + tvOffset));
		tessellator.addVertexWithUV((double)maxX, (double)maxY, (double)z, (double)(0.0F + tuOffset), (double)(0.0F + tvOffset));
		tessellator.addVertexWithUV((double)minX, (double)maxY, (double)z, (double)(tu + tuOffset), (double)(0.0F + tvOffset));
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
