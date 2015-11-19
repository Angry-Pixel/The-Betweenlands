package thebetweenlands.client.render.item;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemWeedWoodChestRenderer implements IItemRenderer {
	private final ModelChest modelChest = new ModelChest();
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/tiles/weedwoodChest.png");

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY: {
			renderChest(0.5F, 0.5F, 0.5F);
			break;
		}
		case EQUIPPED: {
			renderChest(1.0F, 1.0F, 1.0F);
			break;
		}
		case EQUIPPED_FIRST_PERSON: {
			renderChest(1.0F, 1.0F, 1.0F);
			break;
		}
		case INVENTORY: {
			renderChest(0.0F, 0.075F, 0.0F);
			break;
		}
		default:
			break;
		}
	}

	private void renderChest(float x, float y, float z) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(180, 1, 0, 0);
		GL11.glRotatef(-90, 0, 1, 0);
		modelChest.renderAll();
		GL11.glPopMatrix();
	}
}