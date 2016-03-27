package thebetweenlands.client.render.item;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelVolarpad;
import thebetweenlands.client.model.item.ModelVolarkite;
import thebetweenlands.entities.EntityVolarkite;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemVolarKiteRenderer implements IItemRenderer {

	private final ModelVolarkite model = new ModelVolarkite();
	private final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/items/volarkite.png");

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		EntityLivingBase entity = (EntityLivingBase)data[1]; 
		if (EntityVolarkite.isEntityHoldingGlider(entity))
			return; 

		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TEXTURE);
		switch (type) {
		case ENTITY:
			renderBlock(0.0F, 2F, 0.0F, 0.125D);
			break;
		case EQUIPPED:
			renderHeld(0.5F, 0F, 0.0F, 0.75D);
			break;
		case EQUIPPED_FIRST_PERSON:
			renderHeld(0.5F, 0.25F, 0.5F, 0.5D);
			break;
		case INVENTORY:
			renderBlock(0.0F, 1.75F, 0.0F, 0.4D);
			break;
		default:
			break;
		}
	}

	private void renderBlock(float x, float y, float z, double size) {
		if (RenderItem.renderInFrame) {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y -1.825F, z);
			GL11.glRotatef(180F, 1F, 0F, 0F);
			GL11.glRotatef(180F, 0F, 1F, 0F);
			GL11.glScaled(size + 0.2F, size + 0.2F, size + 0.2F);
			model.render();
			GL11.glPopMatrix();
		} else {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y -1.825F, z);
			GL11.glRotatef(180F, 1F, 0F, 0F);
			GL11.glRotatef(-90F, 0F, 1F, 0F);
			GL11.glScaled(size, size, size);
			model.render();
			GL11.glPopMatrix();
		}
	}

	private void renderHeld(float x, float y, float z, double size) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(180F, 1F, 0F, 0F);
		//GL11.glRotatef(90F, 0F, 1F, 0F);
		GL11.glScaled(size, size, size);
		model.render();
		GL11.glPopMatrix();
	}
}
