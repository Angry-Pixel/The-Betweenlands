package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.BLItemFrame;

public class BLItemFrameRenderer extends ItemFrameRenderer<BLItemFrame> {

	public static final ModelResourceLocation FRAME_MODEL = ModelResourceLocation.standalone(TheBetweenlands.prefix("block/item_frame"));
	public static final ModelResourceLocation FRAME_MAP_MODEL = ModelResourceLocation.standalone(TheBetweenlands.prefix("block/item_frame_map"));
	public static final ModelResourceLocation FRAME_BG_MODEL = ModelResourceLocation.standalone(TheBetweenlands.prefix("block/item_frame_background"));
	private final ItemRenderer itemRenderer;
	private final BlockRenderDispatcher blockRenderer;

	public BLItemFrameRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.blockRenderer = context.getBlockRenderDispatcher();
	}

	@Override
	protected int getBlockLightLevel(BLItemFrame entity, BlockPos pos) {
		return entity.isFrameGlowing() ? Math.max(5, super.getBlockLightLevel(entity, pos)) : super.getBlockLightLevel(entity, pos);
	}

	@Override
	public void render(BLItemFrame entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		var event = new RenderNameTagEvent(entity, entity.getDisplayName(), this, poseStack, buffer, packedLight, partialTicks);
		NeoForge.EVENT_BUS.post(event);
		if (event.canRender().isTrue() || event.canRender().isDefault() && this.shouldShowName(entity)) {
			this.renderNameTag(entity, event.getContent(), poseStack, buffer, packedLight, partialTicks);
		}

		poseStack.pushPose();
		Direction direction = entity.getDirection();
		Vec3 vec3 = this.getRenderOffset(entity, partialTicks);
		poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
		poseStack.translate((double)direction.getStepX() * 0.46875, (double)direction.getStepY() * 0.46875, (double)direction.getStepZ() * 0.46875);
		poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
		boolean flag = entity.isInvisible();
		ItemStack itemstack = entity.getItem();
		if (!flag) {
			ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
			poseStack.pushPose();
			poseStack.translate(-0.5F, -0.5F, -0.5F);
			int light = entity.isFrameGlowing() ? LightTexture.FULL_BRIGHT : packedLight;
			if (itemstack.getItem() instanceof MapItem) {
				this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(Sheets.solidBlockSheet()), null, modelmanager.getModel(FRAME_MAP_MODEL), 1.0F, 1.0F, 1.0F, light, OverlayTexture.NO_OVERLAY);
			} else {
				this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(Sheets.solidBlockSheet()), null, modelmanager.getModel(FRAME_MODEL), 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
				int color = entity.getColor();
				float r = ((color >> 16) & 0xFF) / 255.0f;
				float g = ((color >> 8) & 0xFF) / 255.0f;
				float b = ((color) & 0xFF) / 255.0f;
				this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(Sheets.solidBlockSheet()), null, modelmanager.getModel(FRAME_BG_MODEL), r, g, b, light, OverlayTexture.NO_OVERLAY);
			}
			poseStack.popPose();
		}

		if (!itemstack.isEmpty()) {
			MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, entity.level());
			if (flag) {
				poseStack.translate(0.0F, 0.0F, 0.5F);
			} else {
				poseStack.translate(0.0F, 0.0F, 0.4375F);
			}

			int j = mapitemsaveddata != null ? entity.getRotation() % 4 * 2 : entity.getRotation();
			poseStack.mulPose(Axis.ZP.rotationDegrees((float)j * 360.0F / 8.0F));
			if (!NeoForge.EVENT_BUS.post(new RenderItemInFrameEvent(entity, this, poseStack, buffer, packedLight)).isCanceled()) {
				if (mapitemsaveddata != null) {
					poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
					float f = 0.0078125F;
					poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
					poseStack.translate(-64.0F, -64.0F, 0.0F);
					poseStack.translate(0.0F, 0.0F, -1.0F);
					if (mapitemsaveddata != null) {
						int i = entity.isFrameGlowing() ? 15728850 : packedLight;
						Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, buffer, entity.getFramedMapId(itemstack), mapitemsaveddata, true, i);
					}
				} else {
					int k = entity.isFrameGlowing() ? 15728880 : packedLight;
					poseStack.scale(0.5F, 0.5F, 0.5F);
					this.itemRenderer.renderStatic(itemstack, ItemDisplayContext.FIXED, k, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
				}
			}
		}

		poseStack.popPose();
	}
}
