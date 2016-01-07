package thebetweenlands.client.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
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
import thebetweenlands.client.render.shader.effect.StarfieldEffect;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.event.debugging.DebugHandlerClient;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui {
	public static final GuiOverlay INSTANCE = new GuiOverlay();
	
	private ResourceLocation decayBarTexture = new ResourceLocation("thebetweenlands:textures/gui/decayBar.png");
	private Minecraft mc = Minecraft.getMinecraft();
	private Random random = new Random();

	private int updateCounter;

	private DeferredEffect de = null;
	private Framebuffer tb1 = null;

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			this.updateCounter++;
		}
	}

	@SubscribeEvent
	public void renderGui(RenderGameOverlayEvent.Post event) {
		//GLUProjection test
		/*for(Entity e : (List<Entity>)Minecraft.getMinecraft().theWorld.loadedEntityList) {
			Projection p = GLUProjection.getInstance().project(e.posX - Minecraft.getMinecraft().thePlayer.posX, e.posY - Minecraft.getMinecraft().thePlayer.posY, e.posZ - Minecraft.getMinecraft().thePlayer.posZ, ClampMode.NONE, false);
			if(p.isType(Type.INSIDE)) Gui.drawRect((int)p.getX(), (int)p.getY(), (int)p.getX() + 5, (int)p.getY() + 5, 0xFFFF0000);
		}*/

		//Just some shader debugging stuff. Applies gaussian blur to the top half of the screen
		if(DebugHandlerClient.INSTANCE.debugDeferredEffect && event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			if(ShaderHelper.INSTANCE.canUseShaders()) {
				MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
				if(shader != null) {
					if(this.tb1 == null) {
						this.tb1 = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
						this.de = new StarfieldEffect().init();
					} else {
						if(this.tb1.framebufferWidth != Minecraft.getMinecraft().displayWidth / 2 || this.tb1.framebufferHeight != Minecraft.getMinecraft().displayHeight / 2) {
							this.tb1.deleteFramebuffer();
							this.tb1 = new Framebuffer(Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2, false);
						}
					}

					((StarfieldEffect)this.de).setTimeScale(0.000003F);

					this.de.apply(this.tb1.framebufferTexture, shader.getBlitBuffer("starfieldBlitBuffer"), null, Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2);

					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_BLEND);
					ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
					GL11.glPushMatrix();
					GL11.glTranslated(0, 60, 0);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, shader.getBlitBuffer("starfieldBlitBuffer").framebufferTexture);
					GL11.glBegin(GL11.GL_TRIANGLES);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glTexCoord2d(0, 0);
					GL11.glVertex2d(0, 128*5);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128*5, 128*5);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128*5, 128*5);
					GL11.glTexCoord2d(1, 1);
					GL11.glVertex2d(128*5, 0);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glEnd();

					/*GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
					GL11.glEnd();*/
					GL11.glPopMatrix();
				}
			}
		}

		if (DecayManager.isDecayEnabled(mc.thePlayer)) {
			int width = event.resolution.getScaledWidth();
			int height = event.resolution.getScaledHeight();

			int startX = (width / 2) - (27 / 2) + 23;
			int startY = height - 49;

			//Erebus compatibility
			if (mc.thePlayer.getEntityData().hasKey("antivenomDuration")) {
				int duration = mc.thePlayer.getEntityData().getInteger("antivenomDuration");
				if (duration > 0) {
					startY -= 12;
				}
			}

			int decayLevel = DecayManager.getDecayLevel(mc.thePlayer);

			if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
				mc.getTextureManager().bindTexture(decayBarTexture);

				GL11.glEnable(GL11.GL_BLEND);
				for (int i = 0; i < 10; i++) {
					int offsetY = mc.thePlayer.isInsideOfMaterial(Material.water) ? -10 : 0;

					if (updateCounter % (decayLevel * 3 + 1) == 0) offsetY += random.nextInt(3) - 1;

					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glColor4f(1, 1, 1, 1);

					drawTexturedModalRect(startX + 71 - i * 8, startY + offsetY, 18, 0, 9, 9);
					if (i * 2 + 1 < decayLevel) drawTexturedModalRect(startX + 71 - i * 8, startY + offsetY, 0, 0, 9, 9);
					if (i * 2 + 1 == decayLevel) drawTexturedModalRect(startX + 72 - i * 8, startY + offsetY, 9, 0, 9, 9);
				}
			}
		}
	}
}
