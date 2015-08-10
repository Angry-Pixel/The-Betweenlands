package thebetweenlands.client.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import thebetweenlands.client.render.shader.MainShader;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.effect.DeferredEffect;
import thebetweenlands.client.render.shader.effect.GodRayEffect;
import thebetweenlands.client.render.shader.effect.OcclusionExtractor;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.event.render.FogHandler;
import thebetweenlands.event.render.ShaderHandler;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.utils.FogGenerator;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui
{
	public ResourceLocation decayBarTexture = new ResourceLocation("thebetweenlands:textures/gui/decayBar.png");
	public Minecraft mc = Minecraft.getMinecraft();
	public Random random = new Random();

	public int updateCounter;

	private DeferredEffect de = null;
	private DeferredEffect de2 = null;
	private Framebuffer tb1 = null;

	@SubscribeEvent
	public void renderGui(RenderGameOverlayEvent.Post event)
	{
		updateCounter++;

		//GLUProjection test
		/*for(Entity e : (List<Entity>)Minecraft.getMinecraft().theWorld.loadedEntityList) {
			Projection p = GLUProjection.getInstance().project(e.posX - Minecraft.getMinecraft().thePlayer.posX, e.posY - Minecraft.getMinecraft().thePlayer.posY, e.posZ - Minecraft.getMinecraft().thePlayer.posZ, ClampMode.NONE, false);
			if(p.isType(Type.INSIDE)) Gui.drawRect((int)p.getX(), (int)p.getY(), (int)p.getX() + 5, (int)p.getY() + 5, 0xFFFF0000);
		}*/
		
		/*BLSkyRenderer.INSTANCE.clipPlaneBuffer.bind();
		GL11.glPushMatrix();
		GL11.glTranslated(0, 80, 0);
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(0, 0);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(0, 1);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(1, 1);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(1, 1);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(1, 0);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(0, 0);
		GL11.glEnd();
		GL11.glPopMatrix();*/
		
		/*Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 80, 0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, BLSkyRenderer.INSTANCE.clipPlaneBuffer.getGeometryBuffer().framebufferTexture);
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(0, 0);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(0, 100);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(100, 100);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(100, 100);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(100, 0);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(0, 0);
		GL11.glEnd();
		GL11.glPopMatrix();*/
		
		//Just some shader debugging stuff. Applies gaussian blur to the top half of the screen
		if(DebugHandler.INSTANCE.debugDeferredEffect && event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			if(ShaderHelper.INSTANCE.canUseShaders()) {
				MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
				if(shader != null) {
					if(this.tb1 == null) {
						this.tb1 = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
						this.de = new OcclusionExtractor();
						this.de.init();
						this.de2 = new GodRayEffect();
						this.de2.init();
					} else {
						if(this.tb1.framebufferWidth != Minecraft.getMinecraft().displayWidth || this.tb1.framebufferHeight != Minecraft.getMinecraft().displayHeight) {
							this.tb1.deleteFramebuffer();
							this.tb1 = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
						}
					}

					//((SwirlEffect)this.de).setAngle((float)(Math.sin(System.nanoTime() / 1000000000.0D) / 5.0D));
					((OcclusionExtractor)this.de).setFBOs(shader.getDepthBuffer(), BLSkyRenderer.INSTANCE.clipPlaneBuffer.getGeometryDepthBuffer());
					
					for(int i = 0; i < 1; i++) {
						this.de.apply(Minecraft.getMinecraft().getFramebuffer().framebufferTexture, this.tb1, null, Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
						
						//exposure, decay, density, weight, illuminationDecay
						((GodRayEffect)this.de2).setOcclusionMap(this.tb1).setParams(0.8F, 0.95F, 0.5F, 0.1F, 0.75F).setRayPos(0.5F, 1.0F);
						
						this.de2.apply(this.tb1.framebufferTexture, shader.getBlitBuffer("bloodSkyBlitBuffer"), null, Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
						
						ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

						GL11.glBindTexture(GL11.GL_TEXTURE_2D, shader.getBlitBuffer("bloodSkyBlitBuffer").framebufferTexture/*this.tb1.framebufferTexture*//*BLSkyRenderer.INSTANCE.clipPlaneBuffer.getGeometryDepthBuffer().framebufferTexture*/);
						
						/*GL11.glEnable(GL11.GL_BLEND);
						GL11.glColor4f(1.0F, 0.1F, 0.0F, 0.45F);
						GL11.glBegin(GL11.GL_TRIANGLES);
						GL11.glTexCoord2d(0, 1);
						GL11.glVertex2d(0, 0);
						GL11.glTexCoord2d(0, 0.5);
						GL11.glVertex2d(0, sr.getScaledHeight()/2);
						GL11.glTexCoord2d(1, 0.5);
						GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight() / 2);
						GL11.glTexCoord2d(1, 0.5);
						GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight() / 2);
						GL11.glTexCoord2d(1, 1);
						GL11.glVertex2d(sr.getScaledWidth(), 0);
						GL11.glTexCoord2d(0, 1);
						GL11.glVertex2d(0, 0);
						GL11.glEnd();*/
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glColor4f(0.95F, 0.15F, 0.0F, 0.35F);
						GL11.glBegin(GL11.GL_TRIANGLES);
						GL11.glTexCoord2d(0, 1);
						GL11.glVertex2d(0, 0);
						GL11.glTexCoord2d(0, 0.0F);
						GL11.glVertex2d(0, sr.getScaledHeight());
						GL11.glTexCoord2d(1, 0.0F);
						GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight());
						GL11.glTexCoord2d(1, 0.0F);
						GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight());
						GL11.glTexCoord2d(1, 1);
						GL11.glVertex2d(sr.getScaledWidth(), 0);
						GL11.glTexCoord2d(0, 1);
						GL11.glVertex2d(0, 0);
						GL11.glEnd();
					}
					
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_BLEND);
					ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
					GL11.glPushMatrix();
					GL11.glTranslated(0, 60, 0);
					GL11.glColor4f(0.0F, 0.8F, 0.25F, 1.0F);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, ShaderHandler.INSTANCE.getGasTextureID());
					GL11.glBegin(GL11.GL_TRIANGLES);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glTexCoord2d(0, 0);
					GL11.glVertex2d(0, 128);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128, 128);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128, 128);
					GL11.glTexCoord2d(1, 1);
					GL11.glVertex2d(128, 0);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glEnd();
					/*GL11.glTranslated(128, 0, 0);
					GL11.glBegin(GL11.GL_TRIANGLES);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glTexCoord2d(0, 0);
					GL11.glVertex2d(0, 128);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128, 128);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128, 128);
					GL11.glTexCoord2d(1, 1);
					GL11.glVertex2d(128, 0);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glEnd();*/
					GL11.glPopMatrix();
				}
			}
		}
		
		if (DecayManager.enableDecay(mc.thePlayer) && !mc.thePlayer.capabilities.isCreativeMode)
		{
			int width = event.resolution.getScaledWidth();
			int height = event.resolution.getScaledHeight();

			int startX = (width / 2) - (27 / 2) + 23;
			int startY = height - 49;

			int decayLevel = DecayManager.getDecayLevel(mc.thePlayer);

			if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
			{
				mc.getTextureManager().bindTexture(decayBarTexture);

				GL11.glEnable(GL11.GL_BLEND);
				for (int i = 0; i < 10; i++)
				{
					int offsetY = mc.thePlayer.isInsideOfMaterial(Material.water) ? -10 : 0;

					if (updateCounter % (decayLevel * 3 + 1) == 0) offsetY += random.nextInt(3) - 1;

					GL11.glColor4f(1, 1, 1, 1);

					drawTexturedModalRect(startX + i * 8, startY + offsetY, 18, 0, 9, 9);
					if (i * 2 + 1 < decayLevel) drawTexturedModalRect(startX + i * 8, startY + offsetY, 0, 0, 9, 9);
					if (i * 2 + 1 == decayLevel) drawTexturedModalRect(startX + i * 8, startY + offsetY, 9, 0, 9, 9);
				}
			}
		}
	}
}
