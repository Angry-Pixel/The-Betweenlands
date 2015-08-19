package thebetweenlands.client.render.item;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelTarminion;
import thebetweenlands.entities.EntityThrownTarminion;
import thebetweenlands.items.ItemTarminion;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemTarminionRenderer extends Render implements IItemRenderer {

	private final ModelTarminion model;
	public final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/tarminion.png");
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
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(texture);
		switch (type) {
			case ENTITY:
				render(0.0F, 2.5F, 0.0F, 1.5D, (ItemTarminion) item.getItem());
				break;
			case EQUIPPED:
				renderEquipped(0.3F, 5F, 1F, 3D, (ItemTarminion) item.getItem());
				break;
			case EQUIPPED_FIRST_PERSON:
				renderFirstPerson(0F, 4.75F, -1F, 3D, (ItemTarminion) item.getItem());
				break;
			case INVENTORY:
				renderInventory(0.1F, 4.625F, 0.0F, 3D, (ItemTarminion) item.getItem());
				break;
			default:
				break;
		}
	}

	private void renderEquipped(float x, float y, float z, double size, ItemTarminion item) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glScaled(-size, -size, size);
		model.render();
		GL11.glPopMatrix();
	}

	private void render(float x, float y, float z, double size, ItemTarminion item) {
		if (RenderItem.renderInFrame) {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y +0.28F, z);
			GL11.glRotatef(180F, 1F, 0, 0);
			GL11.glScaled(2F, 2F, 2F);
			model.render();
			GL11.glPopMatrix();
		} else {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y, z);
			GL11.glRotatef(180F, 1F, 0, 0);
			GL11.glScaled(size, size, size);
			model.render();
			GL11.glPopMatrix();
		}
	}

	private void renderFirstPerson(float x, float y, float z, double size, ItemTarminion item) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(160F, 1F, 0, 0);
		GL11.glRotatef(65F, 0, 1F, 0);
		GL11.glScaled(size, size, size);
		model.render();
		GL11.glPopMatrix();
	}

	private void renderInventory(float x, float y, float z, double size, ItemTarminion item) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(135F, 1F, 0, 0);
		GL11.glRotatef(-45F, 0, 1F, 0);
		GL11.glRotatef(-30F, 0, 0, 1F);
		GL11.glScaled(size, size, size);
		model.render();
		GL11.glPopMatrix();
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