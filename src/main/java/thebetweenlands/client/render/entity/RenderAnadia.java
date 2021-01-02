package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelAnadia;
import thebetweenlands.common.entity.mobs.EntityAnadia;

@SideOnly(Side.CLIENT)
public class RenderAnadia extends RenderLiving<EntityAnadia> {
	private static final ResourceLocation[] TEXTURE_1 = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_smoked.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_rotten.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_base.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_silver.png")
			};
	
	private static final ResourceLocation[] TEXTURE_2 = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_smoked.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_rotten.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_base.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_silver.png")
			};
	
	private static final ResourceLocation[] TEXTURE_3 = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_smoked.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_rotten.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_base.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_silver.png")
			};
	
	public static final ResourceLocation NET_TEXTURE = new ResourceLocation("thebetweenlands:textures/items/net.png");
	
	public final static ModelAnadia ANADIA_MODEL = new ModelAnadia();

	public RenderAnadia(RenderManager manager) {
		super(manager, ANADIA_MODEL, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAnadia entity) {
		return TEXTURE_1[2];
	}

	@Override
	public void doRender(EntityAnadia anadia, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(anadia, x, y, z, entityYaw, partialTicks);
		float smoothedYaw = anadia.prevRotationYaw + (anadia.rotationYaw - anadia.prevRotationYaw) * partialTicks;
		float smoothedPitch = anadia.prevRotationPitch + (anadia.rotationPitch - anadia.prevRotationPitch) * partialTicks;
		float scale = anadia.getFishSize();
		shadowSize = scale * 0.5F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + scale * 1.5F, z);
		GlStateManager.scale(scale, -scale, -scale);
		GlStateManager.rotate(smoothedYaw, 0F, 1F, 0F);
		GlStateManager.rotate(smoothedPitch, 1F, 0F, 0F);
		switch (anadia.getHeadType()) {
			case 0:
				bindTexture(TEXTURE_1[anadia.getFishColour()]);
				ANADIA_MODEL.renderHead(anadia.getHeadType(), 0.0625F);
				break;
			case 1:
				bindTexture(TEXTURE_2[anadia.getFishColour()]);
				ANADIA_MODEL.renderHead(anadia.getHeadType(), 0.0625F);
				break;
			case 2:
				bindTexture(TEXTURE_3[anadia.getFishColour()]);
				ANADIA_MODEL.renderHead(anadia.getHeadType(), 0.0625F);
				break;
		}

		switch (anadia.getBodyType()) {
			case 0:
				bindTexture(TEXTURE_1[anadia.getFishColour()]);
				ANADIA_MODEL.renderBody(anadia.getBodyType(), 0.0625F);
				break;
			case 1:
				bindTexture(TEXTURE_2[anadia.getFishColour()]);
				ANADIA_MODEL.renderBody(anadia.getBodyType(), 0.0625F);
				break;
			case 2:
				bindTexture(TEXTURE_3[anadia.getFishColour()]);
				ANADIA_MODEL.renderBody(anadia.getBodyType(), 0.0625F);
				break;
		}

		switch (anadia.getTailType()) {
			case 0:
				bindTexture(TEXTURE_1[anadia.getFishColour()]);
				ANADIA_MODEL.renderTail(anadia.getTailType(), 0.0625F);
				break;
			case 1:
				bindTexture(TEXTURE_2[anadia.getFishColour()]);
				ANADIA_MODEL.renderTail(anadia.getTailType(), 0.0625F);
				break;
			case 2:
				bindTexture(TEXTURE_3[anadia.getFishColour()]);
				ANADIA_MODEL.renderTail(anadia.getTailType(), 0.0625F);
				break;
		} 
		GlStateManager.popMatrix();
		ANADIA_MODEL.setLivingAnimations(anadia, anadia.limbSwing, anadia.limbSwingAmount, partialTicks);

	//	if(anadia.getStaminaTicks() <= 0) {
		EntityPlayer player = Minecraft.getMinecraft().player;
			GlStateManager.pushMatrix();
			GlStateManager.translate(anadia.posX - player.posX, anadia.posY - player.posY, anadia.posZ - player.posZ);
			renderNetCaptureIcon(anadia, 0D, 1D, 0D);
			GlStateManager.popMatrix();
		//}
	}

	private void renderNetCaptureIcon(EntityAnadia anadia, double x, double y, double z) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(NET_TEXTURE);
			GlStateManager.translate(x, y, z + 0.25D);
			GlStateManager.scale(0.5D, 0.5D, 0.5D);
			GlStateManager.rotate(mc.getRenderManager().playerViewY, 0.0F, -1.0F, 0.0F);
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			double widthX = 0.5D;
			double heightY = 0.5D;
			vertexbuffer.pos(x - widthX, y - heightY, z).tex(1, 1).endVertex();
			vertexbuffer.pos(x - widthX, y + heightY, z).tex(1, 0).endVertex();
			vertexbuffer.pos(x + widthX, y + heightY, z).tex(0, 0).endVertex();
			vertexbuffer.pos(x + widthX, y - heightY, z).tex(0, 1).endVertex();
			GlStateManager.rotate(90 - anadia.animationFrame, 0.0F, 0.0F, 1.0F);
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}
}
