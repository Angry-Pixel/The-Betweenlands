package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelLootUrn1;
import thebetweenlands.client.render.model.tile.ModelLootUrn2;
import thebetweenlands.client.render.model.tile.ModelLootUrn3;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.container.BlockLootUrn.EnumLootUrn;
import thebetweenlands.common.tile.TileEntityLootUrn;
import thebetweenlands.util.TileEntityHelper;

public class RenderLootUrn extends TileEntitySpecialRenderer<TileEntityLootUrn> {

	private static final ModelLootUrn1 LOOT_URN_1 = new ModelLootUrn1();
	private static final ModelLootUrn2 LOOT_URN_2 = new ModelLootUrn2();
	private static final ModelLootUrn3 LOOT_URN_3 = new ModelLootUrn3();

	private static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/loot_urn_1.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/loot_urn_2.png");
	private static final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/loot_urn_3.png");

	@Override
	public void render(TileEntityLootUrn te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EnumLootUrn type = TileEntityHelper.getStatePropertySafely(te, BlockLootUrn.class, BlockLootUrn.VARIANT, EnumLootUrn.URN_1);
		EnumFacing rotation = TileEntityHelper.getStatePropertySafely(te, BlockLootUrn.class, BlockLootUrn.FACING, EnumFacing.NORTH);
		int offset = te.getModelRotationOffset();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		switch (type){
		default:
		case URN_1: {
			bindTexture(TEXTURE_1);
			break;
		}
		case URN_2: {
			bindTexture(TEXTURE_2);
			break;
		}
		case URN_3: {
			bindTexture(TEXTURE_3);
			break;
		}
		}
		switch (rotation) {
		default:
		case NORTH:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case EAST:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 90.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case SOUTH:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 180.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case WEST:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 270.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		}
	}

	private void renderType(EnumLootUrn type){
		switch (type){
		case URN_1: {
			LOOT_URN_1.render();
			break;
		}
		case URN_2: {
			LOOT_URN_2.render();
			break;
		}
		case URN_3: {
			LOOT_URN_3.render();
			break;
		}
		}
	}

}
