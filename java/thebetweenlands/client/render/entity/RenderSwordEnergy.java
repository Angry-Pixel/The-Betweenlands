package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import thebetweenlands.client.model.entity.ModelSwordEnergy;
import thebetweenlands.entities.EntitySwordEnergy;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

@SideOnly(Side.CLIENT)
public class RenderSwordEnergy extends Render {
	private static final ResourceLocation FORCE_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
	private final ModelSwordEnergy model;
	private final RenderItem renderItem;
	@SideOnly(Side.CLIENT)
	private EntityItem ghostItem;

	public RenderSwordEnergy() {
		model = new ModelSwordEnergy();
		renderItem = new RenderItem() {
			@Override
			public boolean shouldBob() {
				return false;
			}
		};
		renderItem.setRenderManager(RenderManager.instance);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		renderSwordEnergy((EntitySwordEnergy) entity, x, y, z, rotationYaw, partialTickTime);
	}

	public void renderSwordEnergy(EntitySwordEnergy energyBall, double x, double y, double z, float rotationYaw, float partialTickTime) {
		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y - 0.0625D - energyBall.pulseFloat, z);
		float f1 = ticks;
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(FORCE_TEXTURE);
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
		ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_1));
		double interpPos1 = energyBall.lastPos1 + (energyBall.pos1 - energyBall.lastPos1) * partialTickTime;
		renderItemInBlock(x - interpPos1, y + 0.725F, z - interpPos1, ghostItem, ticks);
		double interpPos2 = energyBall.lastPos2 + (energyBall.pos2 - energyBall.lastPos2) * partialTickTime;
		ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_2));
		renderItemInBlock(x + interpPos2, y + 0.725F, z - interpPos2, ghostItem, ticks);
		double interpPos3 = energyBall.lastPos3 + (energyBall.pos3 - energyBall.lastPos3) * partialTickTime;
		ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_3));
		renderItemInBlock(x + interpPos3, y + 0.725F, z + interpPos3, ghostItem, ticks);
		double interpPos4 = energyBall.lastPos4 + (energyBall.pos4 - energyBall.lastPos4) * partialTickTime;
		ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_4));
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
		this.renderBeam(Vec3.createVectorHelper(x, y + 0.85F, z), Vec3.createVectorHelper(x - interpPos1 - 0.1F, y + 0.9F, z - interpPos1 - 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x - interpPos1, y - 0.14F, z - interpPos1);
		model.render(0.0625F);
		GL11.glPopMatrix();
		this.renderBeam(Vec3.createVectorHelper(x, y + 0.85F, z), Vec3.createVectorHelper(x + interpPos2 + 0.1F, y + 0.9F, z - interpPos2 - 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x + interpPos2, y - 0.14F, z - interpPos2);
		model.render(0.0625F);
		GL11.glPopMatrix();
		this.renderBeam(Vec3.createVectorHelper(x, y + 0.85F, z), Vec3.createVectorHelper(x + interpPos3 + 0.1F, y + 0.9F, z + interpPos3 + 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x + interpPos3, y - 0.14F, z + interpPos3);
		model.render(0.0625F);
		GL11.glPopMatrix();
		this.renderBeam(Vec3.createVectorHelper(x, y + 0.85F, z), Vec3.createVectorHelper(x - interpPos4 - 0.1F, y + 0.9F, z + interpPos4 + 0.1F), 0.05F, 0.25F);
		GL11.glPushMatrix();
		GL11.glTranslated(x - interpPos4, y - 0.14F, z + interpPos4);
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
		renderItem.doRender(ghostItem, 0, 0, 0, 0, 0);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

	public void renderBeam(Vec3 start, Vec3 end, float startWidth, float endWidth) {
		Vec3 diff = start.subtract(end);
		Vec3 dir = diff.normalize();
		Vec3 upVec = Vec3.createVectorHelper(0, 1, 0);
		Vec3 localSide = dir.crossProduct(upVec).normalize();
		Vec3 localUp = localSide.crossProduct(dir).normalize();
		
		Tessellator tessellator = Tessellator.instance;
		
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
		
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(start.xCoord + (localSide.xCoord + localUp.xCoord) * startWidth, start.yCoord + (localSide.yCoord + localUp.yCoord) * startWidth, start.zCoord + (localSide.zCoord + localUp.zCoord) * startWidth, 0, minVStart);
		tessellator.addVertexWithUV(start.xCoord + (localSide.xCoord - localUp.xCoord) * startWidth, start.yCoord + (localSide.yCoord - localUp.yCoord) * startWidth, start.zCoord + (localSide.zCoord - localUp.zCoord) * startWidth, 0, maxVStart);
		tessellator.addVertexWithUV(end.xCoord + (localSide.xCoord - localUp.xCoord) * endWidth, end.yCoord + (localSide.yCoord - localUp.yCoord) * endWidth, end.zCoord + (localSide.zCoord - localUp.zCoord) * endWidth, maxU, maxVEnd);
		tessellator.addVertexWithUV(end.xCoord + (localSide.xCoord + localUp.xCoord) * endWidth, end.yCoord + (localSide.yCoord + localUp.yCoord) * endWidth, end.zCoord + (localSide.zCoord + localUp.zCoord) * endWidth, maxU, minVEnd);
		
		tessellator.addVertexWithUV(end.xCoord + (-localSide.xCoord + localUp.xCoord) * endWidth, end.yCoord + (-localSide.yCoord + localUp.yCoord) * endWidth, end.zCoord + (-localSide.zCoord + localUp.zCoord) * endWidth, maxU, minVEnd);
		tessellator.addVertexWithUV(end.xCoord + (-localSide.xCoord - localUp.xCoord) * endWidth, end.yCoord + (-localSide.yCoord - localUp.yCoord) * endWidth, end.zCoord + (-localSide.zCoord - localUp.zCoord) * endWidth, maxU, maxVEnd);
		tessellator.addVertexWithUV(start.xCoord + (-localSide.xCoord - localUp.xCoord) * startWidth, start.yCoord + (-localSide.yCoord - localUp.yCoord) * startWidth, start.zCoord + (-localSide.zCoord - localUp.zCoord) * startWidth, 0, maxVStart);
		tessellator.addVertexWithUV(start.xCoord + (-localSide.xCoord + localUp.xCoord) * startWidth, start.yCoord + (-localSide.yCoord + localUp.yCoord) * startWidth, start.zCoord + (-localSide.zCoord + localUp.zCoord) * startWidth, 0, minVStart);
		
		tessellator.addVertexWithUV(end.xCoord + (localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord + localSide.zCoord) * endWidth, maxU, minVEnd);
		tessellator.addVertexWithUV(end.xCoord + (localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord - localSide.zCoord) * endWidth, maxU, maxVEnd);
		tessellator.addVertexWithUV(start.xCoord + (localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord - localSide.zCoord) * startWidth, 0, maxVStart);
		tessellator.addVertexWithUV(start.xCoord + (localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord + localSide.zCoord) * startWidth, 0, minVStart);
		
		tessellator.addVertexWithUV(start.xCoord + (-localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord + localSide.zCoord) * startWidth, 0, minVStart);
		tessellator.addVertexWithUV(start.xCoord + (-localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord - localSide.zCoord) * startWidth, 0, maxVStart);
		tessellator.addVertexWithUV(end.xCoord + (-localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord - localSide.zCoord) * endWidth, maxU, maxVEnd);
		tessellator.addVertexWithUV(end.xCoord + (-localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord + localSide.zCoord) * endWidth, maxU, minVEnd);
		
		tessellator.addVertexWithUV(start.xCoord + (localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord - localSide.zCoord) * startWidth, 0, 1);
		tessellator.addVertexWithUV(start.xCoord + (-localUp.xCoord - localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord - localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord - localSide.zCoord) * startWidth, 1, 1);
		tessellator.addVertexWithUV(start.xCoord + (-localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (-localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (-localUp.zCoord + localSide.zCoord) * startWidth, 1, 0);
		tessellator.addVertexWithUV(start.xCoord + (localUp.xCoord + localSide.xCoord) * startWidth, start.yCoord + (localUp.yCoord + localSide.yCoord) * startWidth, start.zCoord + (localUp.zCoord + localSide.zCoord) * startWidth, 0, 0);
		
		tessellator.addVertexWithUV(end.xCoord + (localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord + localSide.zCoord) * endWidth, 0, 0);
		tessellator.addVertexWithUV(end.xCoord + (-localUp.xCoord + localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord + localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord + localSide.zCoord) * endWidth, 1, 0);
		tessellator.addVertexWithUV(end.xCoord + (-localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (-localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (-localUp.zCoord - localSide.zCoord) * endWidth, 1, 1);
		tessellator.addVertexWithUV(end.xCoord + (localUp.xCoord - localSide.xCoord) * endWidth, end.yCoord + (localUp.yCoord - localSide.yCoord) * endWidth, end.zCoord + (localUp.zCoord - localSide.zCoord) * endWidth, 0, 1);
		tessellator.draw();
	}
}