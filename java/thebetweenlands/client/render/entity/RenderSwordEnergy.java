package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelSwordEnergy;
import thebetweenlands.entities.EntitySwordEnergy;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		renderItemInBlock(x - energyBall.getSwordPart1Pos(), y + 0.75F, z - energyBall.getSwordPart1Pos(), ghostItem, ticks);
		ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_2));
		renderItemInBlock(x + energyBall.getSwordPart2Pos(), y + 0.75F, z - energyBall.getSwordPart2Pos(), ghostItem, ticks);
		ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_3));
		renderItemInBlock(x + energyBall.getSwordPart3Pos(), y + 0.75F, z + energyBall.getSwordPart3Pos(), ghostItem, ticks);
		ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_4));
		renderItemInBlock(x - energyBall.getSwordPart4Pos(), y + 0.75F, z + energyBall.getSwordPart4Pos(), ghostItem, ticks);
	}

	public void renderItemInBlock(double x, double y, double z, EntityItem ghostItem, float ticks) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) (y), (float) z);
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		GL11.glRotatef(ticks, 0, 1, 0);
		renderItem.doRender(ghostItem, 0, 0, 0, 0, 0);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}