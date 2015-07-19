package thebetweenlands.client.render.item;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelInfuser;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemInfuserRenderer implements IItemRenderer {
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/infuser.png");
	private final ModelInfuser model = new ModelInfuser();

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
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TEXTURE);
		switch (type) {
			case ENTITY:
				renderBlock(0.0F, 0.5F, 0.0F, 0.5D);
				break;
			case EQUIPPED:
				renderHeld(0.5F, 2.0F, 1.0F, 1.0D);
				break;
			case EQUIPPED_FIRST_PERSON:
				renderHeld(0.5F, 1.5F, 0.5F, 1.0D);
				break;
			case INVENTORY:
				renderBlock(0.0F, 1.0F, 0.0F, 1.0D);
				break;
			default:
				break;
		}
	}

	private void renderBlock(float x, float y, float z, double size) {
		if (RenderItem.renderInFrame) {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y + 0.25F, z + 0.175F);
			GL11.glRotatef(180F, 1F, 0F, 0F);
			GL11.glRotatef(0F, 0F, 1F, 0F);
			GL11.glScaled(size, size, size);
			model.render();
			GL11.glPopMatrix();
		} else {
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y, z);
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
		GL11.glRotatef(90F, 0F, 1F, 0F);
		GL11.glScaled(size, size, size);
		model.render();
		GL11.glPopMatrix();
	}
}
