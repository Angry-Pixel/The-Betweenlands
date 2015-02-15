package thebetweenlands.client.render.tileentity;

import java.util.Random;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thebetweenlands.client.model.block.ModelAltar;
import thebetweenlands.client.model.block.ModelStone4;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import thebetweenlands.utils.ItemRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityDruidAltarRenderer extends TileEntitySpecialRenderer {
	private final ModelAltar model = new ModelAltar();
	private final ModelStone4 stone4 = new ModelStone4();

	private final ResourceLocation ACTIVE = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarActive.png");
	private final ResourceLocation ACTIVEGLOW = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarActiveGlow.png");
	private final ResourceLocation NORMAL = new ResourceLocation("thebetweenlands:textures/tiles/druidAltar.png");
	private final ResourceLocation NORMALGLOW = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarGlow.png");

	public static TileEntityDruidAltarRenderer instance;

	public TileEntityDruidAltarRenderer() {
	}

	@Override
	public void func_147497_a(TileEntityRendererDispatcher renderer) {
		super.func_147497_a(renderer);
		instance = this;
	}

	public void renderTileAsItem(double x, double y, double z) {
		bindTexture(NORMAL);

		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		renderStones(x, y, z, 0);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glDepthMask(true);
		char c0 = 61680;
		int j = c0 % 65536;
		int k = c0 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
		bindTexture(NORMALGLOW);

		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		renderStones(x, y, z, 0);
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}

	private void renderTile(TileEntityDruidAltar tile, double x, double y, double z, float partialTicks) {
		//Render main model
		if (tile.blockMetadata == 1) {
			bindTexture(ACTIVE);
		} else {
			bindTexture(NORMAL);
		}
		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();

		//Update rotation
		float prevRotation = tile.renderRotation;
		tile.renderRotation = tile.rotation;
		tile.renderRotation = prevRotation + (tile.renderRotation - prevRotation) * partialTicks;

		//Render floating stones
		GL11.glPushMatrix();
		renderStones(x, y, z, tile.renderRotation);
		GL11.glPopMatrix();

		//Full brightness for items
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0f, 200.0f);

		//Animate the 4 talisman pieces
		if (tile.blockMetadata == 1 && tile.craftingProgress != 0) {
			double finalHeight = 2.0D;
			float prevYOff = tile.renderYOffset;
			double yOff = (double)tile.craftingProgress / (double)TileEntityDruidAltar.CRAFTING_TIME * finalHeight + 1.0D;
			tile.renderYOffset = (float)yOff;
			tile.renderYOffset = prevYOff + (tile.renderYOffset - prevYOff) * partialTicks;
			yOff = tile.renderYOffset;
			if(yOff > finalHeight + 1.0D) {
				yOff = finalHeight + 1.0D;
			}
			GL11.glPushMatrix();
			GL11.glTranslated(x+0.5D, y+3.1D, z+0.5D);
			GL11.glRotated(tile.renderRotation*2.0D, 0, 1, 0);
			double shineScale = 0.04f * Math.pow(1.0D-(finalHeight+1.0D-yOff)/finalHeight, 12);
			GL11.glScaled(shineScale, shineScale, shineScale);
			this.renderShine((float)Math.sin(Math.toRadians(tile.renderRotation))/2.0f-0.2f, (int)(80*Math.pow(1.0D-(finalHeight+1.0D-yOff)/finalHeight, 12)));
			GL11.glPopMatrix();
			boolean exit = false;
			for(int xi = 0; xi < 2; xi++) {
				for(int zi = 0; zi < 2; zi++) {
					ItemStack item = tile.getStackInSlot(zi*2+xi+1);
					if(item == null) {
						exit = true; break;
					}
					double xOff = xi == 0 ? -0.18 : 1.18;
					double zOff = zi == 0 ? -0.18 : 1.18;
					GL11.glPushMatrix();
					GL11.glTranslated(x+xOff, y+1, z+zOff);
					this.renderCone(5);
					GL11.glPopMatrix();
					Vector3d midVec = new Vector3d(x+0.5D, 0, z+0.5D);
					Vector3d diffVec = new Vector3d(x+xOff, 0, z+zOff);
					diffVec.sub(midVec);
					double rProgress = 1.0D-Math.pow(1.0D-(finalHeight+1.0D-yOff)/finalHeight, 6);
					diffVec.scale(rProgress);
					midVec.add(diffVec);
					GL11.glPushMatrix();
					GL11.glTranslated(midVec.x, y+yOff, midVec.z);
					GL11.glScaled(0.3f, 0.3f, 0.3f);
					GL11.glRotated(-tile.renderRotation*2.0D, 0, y, 0);
					ItemRenderHelper.renderItem(item, 0);
					GL11.glPopMatrix();
				}
				if(exit) {
					break;
				}
			}
		}

		//Render swamp talisman
		ItemStack itemTalisman = tile.getStackInSlot(0);
		if(itemTalisman != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x+0.5D, y+3.1D, z+0.5D);
			GL11.glRotated(tile.renderRotation*2.0D, 0, 1, 0);
			double shineScale = 0.04f;
			GL11.glScaled(shineScale, shineScale, shineScale);
			this.renderShine((float)Math.sin(Math.toRadians(tile.renderRotation))/2.0f-0.2f, 80);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(x+0.5D, y+3.0D, z+0.5D);
			GL11.glScaled(0.3f, 0.3f, 0.3f);
			GL11.glRotated(-tile.renderRotation*2.0D, 0, 1, 0);
			ItemRenderHelper.renderItem(itemTalisman, 0);
			GL11.glPopMatrix();
		}

		//Render glow overlay
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glDepthMask(true);
		float lightTexCoords = 160f;
		if (tile.blockMetadata == 1) {
			lightTexCoords = (float)Math.sin(Math.toRadians(tile.renderRotation)*4.0f)*80.0f+100.0f;
		}
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightTexCoords, lightTexCoords);
		if (tile.blockMetadata == 1) {
			bindTexture(ACTIVEGLOW);
		} else {
			bindTexture(NORMALGLOW);
		}
		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		renderStones(x, y, z, tile.renderRotation);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}

	private void renderMainModel(double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.renderAll();
		GL11.glPopMatrix();
	}

	private void renderStones(double x, double y, double z, float rotation) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		stone4.renderAll();
		GL11.glPopMatrix();
	}

	private void renderShine(float rotation, int iterations) {
		Random random = new Random(432L);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glPushMatrix();
		float f1 = rotation;
		float f2 = 0.0f;
		if (f1 > 0.8F) {
			f2 = (f1 - 0.8F) / 0.2F;
		}
		Tessellator tessellator = Tessellator.instance;
		for (int i = 0; (float)i < iterations; ++i) {
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
			tessellator.startDrawing(6);
			float pos1 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
			float pos2 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
			tessellator.setColorRGBA_I(16777215, (int)(255.0F * (1.0F - f2)));
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.setColorRGBA(0, 0, 255, 0);
			tessellator.addVertex(-0.866D * (double)pos2, (double)pos1, (double)(-0.5F * pos2));
			tessellator.addVertex(0.866D * (double)pos2, (double)pos1, (double)(-0.5F * pos2));
			tessellator.addVertex(0.0D, (double)pos1, (double)(1.0F * pos2));
			tessellator.addVertex(-0.866D * (double)pos2, (double)pos1, (double)(-0.5F * pos2));
			tessellator.draw();
		}
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	private void renderCone(int faces) {
		GL11.glPushMatrix();
		float step = 360.0f/(float)faces;
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		
		for(float i = 0; i < 360.0f; i+=step) {
			Tessellator tessellator = Tessellator.instance;
			double lr = 0.1D;
			double ur = 0.3D;
			double height = 0.2D;
			double sin = Math.sin(Math.toRadians(i));
			double cos = Math.cos(Math.toRadians(i));
			double sin2 = Math.sin(Math.toRadians(i+step));
			double cos2 = Math.cos(Math.toRadians(i+step));
			
			tessellator.startDrawing(6);
			tessellator.setColorRGBA(255, 255, 255, 0);
			tessellator.addVertex(sin*lr, 0, cos*lr);
			tessellator.addVertex(sin2*lr, 0, cos2*lr);
			
			tessellator.setColorRGBA(0, 0, 255, 60);
			tessellator.addVertex(sin2*ur, height, cos2*ur);
			tessellator.addVertex(sin*ur, height, cos*ur);
			
			tessellator.setColorRGBA(0, 0, 255, 60);
			tessellator.addVertex(sin*ur, height, cos*ur);
			tessellator.addVertex(sin2*ur, height, cos2*ur);
			
			tessellator.setColorRGBA(255, 255, 255, 0);
			tessellator.addVertex(sin2*lr, 0, cos2*lr);
			tessellator.addVertex(sin*lr, 0, cos*lr);
			tessellator.draw();
		}
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
		renderTile((TileEntityDruidAltar) tile, x, y, z, partialTick);
	}
}