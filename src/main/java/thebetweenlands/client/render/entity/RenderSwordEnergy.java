package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;
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

	@SideOnly(Side.CLIENT)
	private EntityItem ghostItem;
	
	public RenderSwordEnergy(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntitySwordEnergy entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		renderSwordEnergy(entity, x, y, z, rotationYaw, partialTickTime);
	}

	public void renderSwordEnergy(EntitySwordEnergy energyBall, double x, double y, double z, float rotationYaw, float partialTickTime) {
		if(ShaderHelper.INSTANCE.canUseShaders()) {
			ShaderHelper.INSTANCE.addDynLight(new LightSource(energyBall.posX, energyBall.posY + 0.5D, energyBall.posZ, 
					2f,
					5.0f / 255.0f * 13.0F, 
					40.0f / 255.0f * 13.0F, 
					60.0f / 255.0f * 13.0F));
		}

		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y - 0.0625D - energyBall.pulseFloat, z);
		float f1 = ticks;
		bindTexture(FORCE_TEXTURE);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		float f2 = f1 * 0.01F;
		float f3 = f1 * 0.01F;
		GL11.glTranslatef(f2, f3, 0.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_BLEND);
		float f4 = 0.5F;
		GL11.glColor4f(f4, f4, f4, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glScalef(1F + energyBall.pulseFloat, 1F + energyBall.pulseFloat, 1F + energyBall.pulseFloat);
		model.render(0.0625F);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

		ghostItem = new EntityItem(energyBall.worldObj);
		ghostItem.hoverStart = 0.0F;
		
		ghostItem.setEntityItemStack(new ItemStack(EnumItemMisc.SHOCKWAVE_SWORD_1.getItem(), 1, EnumItemMisc.SHOCKWAVE_SWORD_1.getID()));
		double interpPos1 = energyBall.lastPos1 + (energyBall.pos1 - energyBall.lastPos1) * partialTickTime;
		renderItemInBlock(x - interpPos1, y + 0.725F, z - interpPos1, ghostItem, ticks);
		double interpPos2 = energyBall.lastPos2 + (energyBall.pos2 - energyBall.lastPos2) * partialTickTime;
		ghostItem.setEntityItemStack(new ItemStack(EnumItemMisc.SHOCKWAVE_SWORD_2.getItem(), 1, EnumItemMisc.SHOCKWAVE_SWORD_2.getID()));
		renderItemInBlock(x + interpPos2, y + 0.725F, z - interpPos2, ghostItem, ticks);
		double interpPos3 = energyBall.lastPos3 + (energyBall.pos3 - energyBall.lastPos3) * partialTickTime;
		ghostItem.setEntityItemStack(new ItemStack(EnumItemMisc.SHOCKWAVE_SWORD_3.getItem(), 1, EnumItemMisc.SHOCKWAVE_SWORD_3.getID()));
		renderItemInBlock(x + interpPos3, y + 0.725F, z + interpPos3, ghostItem, ticks);
		double interpPos4 = energyBall.lastPos4 + (energyBall.pos4 - energyBall.lastPos4) * partialTickTime;
		ghostItem.setEntityItemStack(new ItemStack(EnumItemMisc.SHOCKWAVE_SWORD_4.getItem(), 1, EnumItemMisc.SHOCKWAVE_SWORD_4.getID()));
		renderItemInBlock(x - interpPos4, y + 0.725F, z + interpPos4, ghostItem, ticks);


		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(FORCE_TEXTURE);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glTranslatef(f2, 0, 0.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(f4, f4, f4, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		this.renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x - interpPos1 - 0.1F, y + 0.9F, z - interpPos1 - 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x - interpPos1, y - 0.14F, z - interpPos1);
		if(energyBall.pos1 < 3.5F)
			model.render(0.0625F);
		GL11.glPopMatrix();
		this.renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x + interpPos2 + 0.1F, y + 0.9F, z - interpPos2 - 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x + interpPos2, y - 0.14F, z - interpPos2);
		if(energyBall.pos2 < 3.5F)
			model.render(0.0625F);
		GL11.glPopMatrix();
		this.renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x + interpPos3 + 0.1F, y + 0.9F, z + interpPos3 + 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x + interpPos3, y - 0.14F, z + interpPos3);
		if(energyBall.pos3 < 3.5F)
			model.render(0.0625F);
		GL11.glPopMatrix();
		this.renderBeam(new Vec3d(x, y + 0.85F, z), new Vec3d(x - interpPos4 - 0.1F, y + 0.9F, z + interpPos4 + 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x - interpPos4, y - 0.14F, z + interpPos4);
		if(energyBall.pos4 < 3.5F)
			model.render(0.0625F);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void renderItemInBlock(double x, double y, double z, EntityItem ghostItem, float ticks) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) (y), (float) z);
		GL11.glScalef(1.25F, 1.25F, 1.25F);
		GL11.glRotatef(ticks * 4F, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(ghostItem.getEntityItem(), ItemCameraTransforms.TransformType.GROUND);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySwordEnergy entity) {
		return null;
	}

	public void renderBeam(Vec3d start, Vec3d end, float startWidth, float endWidth) {
		Vec3d diff = start.subtract(end);
		Vec3d dir = diff.normalize();
		Vec3d upVec = new Vec3d(0, 1, 0);
		Vec3d localSide = dir.crossProduct(upVec).normalize();
		Vec3d localUp = localSide.crossProduct(dir).normalize();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();

		/*tessellator.startDrawing(3);
		tessellator.addVertex(start.xCoord, start.yCoord, start.zCoord);
		tessellator.addVertex(start.xCoord + diff.xCoord, start.yCoord + diff.yCoord, start.zCoord + diff.zCoord);
		tessellator.addVertex(start.xCoord, start.yCoord, start.zCoord);
		tessellator.addVertex(start.xCoord + localUp.xCoord, start.yCoord + localUp.yCoord, start.zCoord + localUp.zCoord);
		tessellator.addVertex(start.xCoord, start.yCoord, start.zCoord);
		tessellator.addVertex(start.xCoord + localSide.xCoord, start.yCoord + localSide.yCoord, start.zCoord + localSide.zCoord);
		tessellator.draw();*/

		double maxVStart = diff.lengthVector() / 8.0D;
		double maxVEnd = diff.lengthVector() / 8.0D;
		double minVStart = 0.0D;
		double minVEnd = 0.0D;
		double maxU = diff.lengthVector() / 2.0D;

		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexbuffer.pos(start.xCoord + (localSide.xCoord + localUp.xCoord) * startWidth, start.yCoord + (localSide.yCoord + localUp.yCoord) * startWidth, start.zCoord + (localSide.zCoord + localUp.zCoord) * startWidth).tex(0, minVStart).endVertex();
		vertexbuffer.pos(start.xCoord + (localSide.xCoord - localUp.xCoord) * startWidth, start.yCoord + (localSide.yCoord - localUp.yCoord) * startWidth, start.zCoord + (localSide.zCoord - localUp.zCoord) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(end.xCoord + (localSide.xCoord - localUp.xCoord) * endWidth, end.yCoord + (localSide.yCoord - localUp.yCoord) * endWidth, end.zCoord + (localSide.zCoord - localUp.zCoord) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(end.xCoord + (localSide.xCoord + localUp.xCoord) * endWidth, end.yCoord + (localSide.yCoord + localUp.yCoord) * endWidth, end.zCoord + (localSide.zCoord + localUp.zCoord) * endWidth).tex(maxU, minVEnd).endVertex();

		vertexbuffer.pos(end.xCoord + (-localSide.xCoord + localUp.xCoord) * endWidth, end.yCoord + (-localSide.yCoord + localUp.yCoord) * endWidth, end.zCoord + (-localSide.zCoord + localUp.zCoord) * endWidth).tex(maxU, minVEnd).endVertex();
		vertexbuffer.pos(end.xCoord + (-localSide.xCoord - localUp.xCoord) * endWidth, end.yCoord + (-localSide.yCoord - localUp.yCoord) * endWidth, end.zCoord + (-localSide.zCoord - localUp.zCoord) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(start.xCoord + (-localSide.xCoord - localUp.xCoord) * startWidth, start.yCoord + (-localSide.yCoord - localUp.yCoord) * startWidth, start.zCoord + (-localSide.zCoord - localUp.zCoord) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(start.xCoord + (-localSide.xCoord + localUp.xCoord) * startWidth, start.yCoord + (-localSide.yCoord + localUp.yCoord) * startWidth, start.zCoord + (-localSide.zCoord + localUp.zCoord) * startWidth).tex(0, minVStart).endVertex();

		vertexbuffer.pos(end.xCoord + (localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord + localSide.zCoord) * endWidth).tex(maxU, minVEnd).endVertex();
		vertexbuffer.pos(end.xCoord + (localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord - localSide.zCoord) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(start.xCoord + (localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord - localSide.zCoord) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(start.xCoord + (localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord + localSide.zCoord) * startWidth).tex(0, minVStart).endVertex();

		vertexbuffer.pos(start.xCoord + (-localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord + localSide.zCoord) * startWidth).tex(0, minVStart).endVertex();
		vertexbuffer.pos(start.xCoord + (-localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord - localSide.zCoord) * startWidth).tex(0, maxVStart).endVertex();
		vertexbuffer.pos(end.xCoord + (-localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord - localSide.zCoord) * endWidth).tex(maxU, maxVEnd).endVertex();
		vertexbuffer.pos(end.xCoord + (-localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord + localSide.zCoord) * endWidth).tex(maxU, minVEnd).endVertex();

		vertexbuffer.pos(start.xCoord + (localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord - localSide.zCoord) * startWidth).tex(0, 1).endVertex();
		vertexbuffer.pos(start.xCoord + (-localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord - localSide.zCoord) * startWidth).tex(1, 1).endVertex();
		vertexbuffer.pos(start.xCoord + (-localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord + localSide.zCoord) * startWidth).tex(1, 0).endVertex();
		vertexbuffer.pos(start.xCoord + (localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord + localSide.zCoord) * startWidth).tex(0, 0).endVertex();

		vertexbuffer.pos(end.xCoord + (localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord + localSide.zCoord) * endWidth).tex(0, 0).endVertex();
		vertexbuffer.pos(end.xCoord + (-localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord + localSide.zCoord) * endWidth).tex(1, 0).endVertex();
		vertexbuffer.pos(end.xCoord + (-localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord - localSide.zCoord) * endWidth).tex(1, 1).endVertex();
		vertexbuffer.pos(end.xCoord + (localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord - localSide.zCoord) * endWidth).tex(0, 1).endVertex();
		tessellator.draw();
	}
}