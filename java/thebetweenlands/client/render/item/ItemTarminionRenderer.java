package thebetweenlands.client.render.item;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import thebetweenlands.client.model.entity.ModelTarminion;
import thebetweenlands.entities.projectiles.EntityThrownTarminion;

@SideOnly(Side.CLIENT)
public class ItemTarminionRenderer extends Render implements IItemRenderer {

	private final ModelTarminion model;
	public final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/tarminion.png");
	public final ResourceLocation textureDrip = new ResourceLocation("thebetweenlands:textures/entity/tarminionOverlay.png");

	public ItemTarminionRenderer() {
		model = new ModelTarminion();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return helper != ItemRendererHelper.BLOCK_3D;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(texture);
		switch (type) {
		case ENTITY:
			render(0.0F, 2.5F, 0.0F, 1.5D, ticks);
			break;
		case EQUIPPED:
			renderEquipped(0.3F, 5F, 1F, 3D, ticks);
			break;
		case EQUIPPED_FIRST_PERSON:
			renderFirstPerson(0F, 4.75F, -1F, 3D, ticks);
			break;
		case INVENTORY:
			renderInventory(0.1F, 4.625F, 0.0F, 3D);
			break;
		default:
			break;
		}
	}

	private void renderEquipped(float x, float y, float z, double size, float ticks) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glScaled(-size, -size, size);
		model.render();
		renderDrips(ticks);
		GL11.glPopMatrix();
	}

	private void render(float x, float y, float z, double size, float ticks) {
		if (RenderItem.renderInFrame) {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y + 0.28F, z);
			GL11.glRotatef(180F, 1F, 0, 0);
			GL11.glScaled(2F, 2F, 2F);
			model.render();
			renderDrips(ticks);
			GL11.glPopMatrix();
		} else {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y, z);
			GL11.glRotatef(180F, 1F, 0, 0);
			GL11.glScaled(size, size, size);
			model.render();
			renderDrips(ticks);
			GL11.glPopMatrix();
		}
	}

	private void renderFirstPerson(float x, float y, float z, double size, float ticks) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(160F, 1F, 0, 0);
		GL11.glRotatef(65F, 0, 1F, 0);
		GL11.glScaled(size, size, size);
		model.render();
		renderDrips(ticks);
		GL11.glPopMatrix();
	}

	private void renderInventory(float x, float y, float z, double size) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(135F, 1F, 0, 0);
		GL11.glRotatef(-45F, 0, 1F, 0);
		GL11.glRotatef(-30F, 0, 0, 1F);
		GL11.glScaled(size, size, size);
		model.render();
		GL11.glPopMatrix();
	}

	public void renderDrips(float ticks) {
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(textureDrip);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		float yScroll = ticks * 0.004F;
		GL11.glTranslatef(0F, -yScroll, 0.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		float colour = 0.5F;
		GL11.glColor4f(colour, colour, colour, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		model.render();
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		renderTarminion((EntityThrownTarminion) entity, x, y, z, rotationYaw, partialTickTime);
	}

	public void renderTarminion(EntityThrownTarminion tarminion, double x, double y, double z, float rotationYaw, float partialTickTime) {
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y + 1.5F, (float) z);
		GL11.glRotatef(tarminion.prevRotationYaw + (tarminion.rotationYaw - tarminion.prevRotationYaw) * partialTickTime - tarminion.rotationticks, 0.0F, 1.0F, 0.0F);
		GL11.glScaled(1F, -1F, 1F);
		model.render();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}