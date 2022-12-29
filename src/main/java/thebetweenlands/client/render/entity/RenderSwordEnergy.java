package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSwordEnergy;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

@SideOnly(Side.CLIENT)
public class RenderSwordEnergy extends Render<EntitySwordEnergy> {
	private static final ResourceLocation FORCE_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
	private static ModelSwordEnergy model = new ModelSwordEnergy();

	private final ItemStack swordPiece1 = EnumItemMisc.SHOCKWAVE_SWORD_1.create(1);
	private final ItemStack swordPiece2 = EnumItemMisc.SHOCKWAVE_SWORD_2.create(1);
	private final ItemStack swordPiece3 = EnumItemMisc.SHOCKWAVE_SWORD_3.create(1);
	private final ItemStack swordPiece4 = EnumItemMisc.SHOCKWAVE_SWORD_4.create(1);

	public RenderSwordEnergy(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntitySwordEnergy entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		renderSwordEnergy(entity, x, y, z, rotationYaw, partialTickTime);
	}

	public void renderSwordEnergy(EntitySwordEnergy energyBall, double x, double y, double z, float rotationYaw, float partialTickTime) {
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(energyBall.posX, energyBall.posY + 0.5D, energyBall.posZ, 
					2f,
					5.0f / 255.0f * 13.0F, 
					40.0f / 255.0f * 13.0F, 
					60.0f / 255.0f * 13.0F));
		}

		float ticks = energyBall.ticksExisted + partialTickTime;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y - 0.0625D - energyBall.pulseFloat, z);
		float f1 = ticks;
		bindTexture(FORCE_TEXTURE);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		float f2 = f1 * 0.01F;
		float f3 = f1 * 0.01F;
		GlStateManager.translate(f2, f3, 0.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableBlend();
		float f4 = 0.5F;
		GlStateManager.color(f4, f4, f4, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
		GlStateManager.scale(1F + energyBall.pulseFloat, 1F + energyBall.pulseFloat, 1F + energyBall.pulseFloat);
		model.render(0.0625F);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();

		double interpPos1 = energyBall.lastPos1 + (energyBall.pos1 - energyBall.lastPos1) * partialTickTime;
		renderItemInBlock(x - interpPos1, y + 0.725F, z - interpPos1, this.swordPiece1, ticks);
		double interpPos2 = energyBall.lastPos2 + (energyBall.pos2 - energyBall.lastPos2) * partialTickTime;
		renderItemInBlock(x + interpPos2, y + 0.725F, z - interpPos2, this.swordPiece2, ticks);
		double interpPos3 = energyBall.lastPos3 + (energyBall.pos3 - energyBall.lastPos3) * partialTickTime;
		renderItemInBlock(x + interpPos3, y + 0.725F, z + interpPos3, this.swordPiece3, ticks);
		double interpPos4 = energyBall.lastPos4 + (energyBall.pos4 - energyBall.lastPos4) * partialTickTime;
		renderItemInBlock(x - interpPos4, y + 0.725F, z + interpPos4, this.swordPiece4, ticks);


		bindTexture(FORCE_TEXTURE);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.translate(f2, 0, 0.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableBlend();
		GlStateManager.color(f4, f4, f4, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
		GlStateManager.enableTexture2D();
		GlStateManager.disableCull();
		renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x - interpPos1 - 0.1F, y + 0.9F, z - interpPos1 - 0.1F), 0.05F, 0.25F, true, true);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - interpPos1, y - 0.14F, z - interpPos1);
		if(energyBall.pos1 < 3.5F)
			model.render(0.0625F);
		GlStateManager.popMatrix();
		renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x + interpPos2 + 0.1F, y + 0.9F, z - interpPos2 - 0.1F), 0.05F, 0.25F, true, true);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + interpPos2, y - 0.14F, z - interpPos2);
		if(energyBall.pos2 < 3.5F)
			model.render(0.0625F);
		GlStateManager.popMatrix();
		renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x + interpPos3 + 0.1F, y + 0.9F, z + interpPos3 + 0.1F), 0.05F, 0.25F, true, true);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + interpPos3, y - 0.14F, z + interpPos3);
		if(energyBall.pos3 < 3.5F)
			model.render(0.0625F);
		GlStateManager.popMatrix();
		renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x - interpPos4 - 0.1F, y + 0.9F, z + interpPos4 + 0.1F), 0.05F, 0.25F, true, true);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - interpPos4, y - 0.14F, z + interpPos4);
		if(energyBall.pos4 < 3.5F)
			model.render(0.0625F);
		GlStateManager.popMatrix();
		GlStateManager.enableCull();
		GlStateManager.enableTexture2D();
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
	}

	public void renderItemInBlock(double x, double y, double z, ItemStack item, float ticks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) (y), (float) z);
		GlStateManager.scale(1.25F, 1.25F, 1.25F);
		GlStateManager.rotate(ticks * 4F, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySwordEnergy entity) {
		return null;
	}

	public static void renderBeam(Vec3d start, Vec3d end, float startWidth, float endWidth, boolean renderStartCap, boolean renderEndCap) {
		Vec3d diff = start.subtract(end);
		Vec3d dir = diff.normalize();
		Vec3d upVec = new Vec3d(0, 1, 0);
		Vec3d localSide = dir.crossProduct(upVec).normalize();
		Vec3d localUp = localSide.crossProduct(dir).normalize();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		double maxVStart = diff.length() / 8.0D;
		double maxVEnd = diff.length() / 8.0D;
		double minVStart = 0.0D;
		double minVEnd = 0.0D;
		double maxU = diff.length() / 2.0D;

		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(start.x + (localSide.x + localUp.x) * startWidth, start.y + (localSide.y + localUp.y) * startWidth, start.z + (localSide.z + localUp.z) * startWidth).tex(0, minVStart).endVertex();
		vertexbuffer.pos(start.x + (localSide.x - localUp.x) * startWidth, start.y + (localSide.y - localUp.y) * startWidth, start.z + (localSide.z - localUp.z) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(end.x + (localSide.x - localUp.x) * endWidth, end.y + (localSide.y - localUp.y) * endWidth, end.z + (localSide.z - localUp.z) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(end.x + (localSide.x + localUp.x) * endWidth, end.y + (localSide.y + localUp.y) * endWidth, end.z + (localSide.z + localUp.z) * endWidth).tex(maxU, minVEnd).endVertex();

		vertexbuffer.pos(end.x + (-localSide.x + localUp.x) * endWidth, end.y + (-localSide.y + localUp.y) * endWidth, end.z + (-localSide.z + localUp.z) * endWidth).tex(maxU, minVEnd).endVertex();
		vertexbuffer.pos(end.x + (-localSide.x - localUp.x) * endWidth, end.y + (-localSide.y - localUp.y) * endWidth, end.z + (-localSide.z - localUp.z) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(start.x + (-localSide.x - localUp.x) * startWidth, start.y + (-localSide.y - localUp.y) * startWidth, start.z + (-localSide.z - localUp.z) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(start.x + (-localSide.x + localUp.x) * startWidth, start.y + (-localSide.y + localUp.y) * startWidth, start.z + (-localSide.z + localUp.z) * startWidth).tex(0, minVStart).endVertex();

		vertexbuffer.pos(end.x + (localUp.x + localSide.x) * endWidth, end.y + (localUp.y + localSide.y) * endWidth, end.z + (localUp.z + localSide.z) * endWidth).tex(maxU, minVEnd).endVertex();
		vertexbuffer.pos(end.x + (localUp.x - localSide.x) * endWidth, end.y + (localUp.y - localSide.y) * endWidth, end.z + (localUp.z - localSide.z) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(start.x + (localUp.x - localSide.x) * startWidth, start.y + (localUp.y - localSide.y) * startWidth, start.z + (localUp.z - localSide.z) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(start.x + (localUp.x + localSide.x) * startWidth, start.y + (localUp.y + localSide.y) * startWidth, start.z + (localUp.z + localSide.z) * startWidth).tex(0, minVStart).endVertex();

		vertexbuffer.pos(start.x + (-localUp.x + localSide.x) * startWidth, start.y + (-localUp.y + localSide.y) * startWidth, start.z + (-localUp.z + localSide.z) * startWidth).tex(0, minVStart).endVertex();
		vertexbuffer.pos(start.x + (-localUp.x - localSide.x) * startWidth, start.y + (-localUp.y - localSide.y) * startWidth, start.z + (-localUp.z - localSide.z) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(end.x + (-localUp.x - localSide.x) * endWidth, end.y + (-localUp.y - localSide.y) * endWidth, end.z + (-localUp.z - localSide.z) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(end.x + (-localUp.x + localSide.x) * endWidth, end.y + (-localUp.y + localSide.y) * endWidth, end.z + (-localUp.z + localSide.z) * endWidth).tex(maxU, minVEnd).endVertex();

		if(renderStartCap) {
			vertexbuffer.pos(start.x + (localUp.x - localSide.x) * startWidth, start.y + (localUp.y - localSide.y) * startWidth, start.z + (localUp.z - localSide.z) * startWidth).tex(0, 1).endVertex();
			vertexbuffer.pos(start.x + (-localUp.x - localSide.x) * startWidth, start.y + (-localUp.y - localSide.y) * startWidth, start.z + (-localUp.z - localSide.z) * startWidth).tex(1, 1).endVertex();
			vertexbuffer.pos(start.x + (-localUp.x + localSide.x) * startWidth, start.y + (-localUp.y + localSide.y) * startWidth, start.z + (-localUp.z + localSide.z) * startWidth).tex(1, 0).endVertex();
			vertexbuffer.pos(start.x + (localUp.x + localSide.x) * startWidth, start.y + (localUp.y + localSide.y) * startWidth, start.z + (localUp.z + localSide.z) * startWidth).tex(0, 0).endVertex();
		}

		if(renderEndCap) {
			vertexbuffer.pos(end.x + (localUp.x + localSide.x) * endWidth, end.y + (localUp.y + localSide.y) * endWidth, end.z + (localUp.z + localSide.z) * endWidth).tex(0, 0).endVertex();
			vertexbuffer.pos(end.x + (-localUp.x + localSide.x) * endWidth, end.y + (-localUp.y + localSide.y) * endWidth, end.z + (-localUp.z + localSide.z) * endWidth).tex(1, 0).endVertex();
			vertexbuffer.pos(end.x + (-localUp.x - localSide.x) * endWidth, end.y + (-localUp.y - localSide.y) * endWidth, end.z + (-localUp.z - localSide.z) * endWidth).tex(1, 1).endVertex();
			vertexbuffer.pos(end.x + (localUp.x - localSide.x) * endWidth, end.y + (localUp.y - localSide.y) * endWidth, end.z + (localUp.z - localSide.z) * endWidth).tex(0, 1).endVertex();
		}
		tessellator.draw();
	}
}