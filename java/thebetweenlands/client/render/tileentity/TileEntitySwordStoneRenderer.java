package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.ModelSwordStoneShield;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.tileentities.TileEntitySwordStone;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySwordStoneRenderer extends TileEntitySpecialRenderer {

	private static final ResourceLocation FORCE_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
	private final RenderBlocks blockRenderer = new RenderBlocks();
	private final ModelSwordStoneShield model = new ModelSwordStoneShield();
	private final RenderItem renderItem;
	@SideOnly(Side.CLIENT)
	private EntityItem ghostItem;
	public static TileEntitySwordStoneRenderer instance;

	public TileEntitySwordStoneRenderer() {
		renderItem = new RenderItem() {
			@Override
			public boolean shouldBob() {
				return false;
			}
		};
		renderItem.setRenderManager(RenderManager.instance);
	}

	@Override
	public void func_147497_a(TileEntityRendererDispatcher renderer) {
		super.func_147497_a(renderer);
		instance = this;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntitySwordStone swordStone = (TileEntitySwordStone) tile;
		int type = swordStone.type;
		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
		
		int brightness = 0;
		brightness = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
		int lightmapX = brightness % 65536;
		int lightmapY = brightness / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX / 1.0F, (float)lightmapY / 1.0F);

		ghostItem = new EntityItem(swordStone.getWorldObj());
		ghostItem.hoverStart = 0.0F;
		switch (swordStone.type) {
		case 0:
			ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_1));
			break;
		case 1:
			ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_2));
			break;
		case 2:
			ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_3));
			break;
		case 3:
			ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_4));
			break;
		default:
				ghostItem.setEntityItemStack(ItemGeneric.createStack(EnumItemGeneric.SHOCKWAVE_SWORD_1));	
		}
		renderItemInBlock(x, y, z, ghostItem, ticks);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y -0.4F, (float) z + 0.5F);
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
		model.render();
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		bindTexture(TextureMap.locationBlocksTexture);
		GL11.glScalef(0.9F, 0.9F, 0.9F);
		blockRenderer.renderBlockAsItem(BLBlockRegistry.polishedDentrothyst2, 0, 10F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public void renderItemInBlock(double x, double y, double z, EntityItem ghostItem, float ticks) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) (y + 0.25F), (float) z + 0.5F);
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		GL11.glRotatef(ticks, 0, 1, 0);
		renderItem.doRender(ghostItem, 0, 0, 0, 0, 0);
		GL11.glPopMatrix();
	}

}
