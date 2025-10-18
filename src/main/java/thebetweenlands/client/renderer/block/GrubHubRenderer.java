package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.client.renderer.entity.SpiritTreeFaceMaskRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.GrubHubBlockEntity;

import java.util.SplittableRandom;

public class GrubHubRenderer implements BlockEntityRenderer<GrubHubBlockEntity> {

	private final RenderType MASK_TYPE = RenderType.entityCutout(SpiritTreeFaceMaskRenderer.TEXTURE_SMALL);
	private final RenderType EYE_TYPE = RenderType.EYES.apply(TheBetweenlands.prefix("textures/entity/small_spirit_tree_face_glow.png"), RenderType.TRANSLUCENT_TRANSPARENCY);
	private static final RenderType TEXTURE_BLOCKS = RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
	private final ModelPart mask;
	private final ItemRenderer itemRenderer;

	public GrubHubRenderer(BlockEntityRendererProvider.Context context) {
		this.mask = context.bakeLayer(BLModelLayers.SMALL_SPIRIT_TREE_FACE_2);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(GrubHubBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		float fluidLevel = entity.tank.getFluidAmount();
		SplittableRandom rand = new SplittableRandom(entity.getBlockPos().asLong());

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			stack.pushPose();
			stack.translate(0.5F + (0.9F * dir.getStepX()), 0.0F, 0.5F + (0.9F * dir.getStepZ()));
			stack.scale(1.0F, -1.0F, -1.0F);
			stack.mulPose(Axis.YP.rotationDegrees(dir.toYRot()));
			this.mask.render(stack, buffer.getBuffer(MASK_TYPE), light, overlay);
			if (entity.switchTextureCount > 0) {
				float opacity = Math.min(1.0F, entity.switchTextureCount / 10.0F);
				this.mask.render(stack, buffer.getBuffer(EYE_TYPE), light, overlay, FastColor.ARGB32.colorFromFloat(opacity, 1.0F, 1.0F, 1.0F));
			}
			stack.popPose();
		}

		if (fluidLevel > 0) {
			stack.pushPose();
			stack.translate(0.0D, -0.3D, 0.0D);
			renderFluid(buffer, stack, entity.tank, light, overlay);
			stack.popPose();
		}

		ItemStack grubs = entity.getItem(0);

		if (!grubs.isEmpty()) {
			stack.pushPose();
			double yUp = 0.8125D;
			stack.translate(0.5D, yUp, 0.5D);
			for (int i = 0; i < grubs.getCount(); i++) {
				stack.pushPose();
				stack.translate(rand.nextDouble() / 2.0D - 1.0D / 4.0D, 0.0D, rand.nextDouble() / 2.0D - 1.0D / 4.0D);
				stack.mulPose(Axis.YP.rotationDegrees(rand.nextFloat() * 360.0F - 180.0F));
				stack.scale(0.25F, 0.25F, 0.25F);
				this.itemRenderer.renderStatic(grubs, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
				stack.popPose();
			}
			stack.popPose();
		}
	}

	public static void renderFluid(MultiBufferSource buffer, PoseStack stack, FluidTank tank, int combinedLight, int overlay) {
		float textureYPos = (0.675F * ((float) tank.getFluidAmount() / tank.getCapacity()));
		stack.pushPose();
		AABB boundingBox = new AABB(0.15F, 0.35F, 0.15F, 0.85F, 0.35F + textureYPos, 0.85F);
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(tank.getFluid().getFluid()).getStillTexture());
		int color = IClientFluidTypeExtensions.of(tank.getFluid().getFluid()).getTintColor(tank.getFluid());
		VertexConsumer vertexbuffer = buffer.getBuffer(TEXTURE_BLOCKS);
		Matrix4f matrix4f = stack.last().pose();
		double avgY = boundingBox.maxY - boundingBox.minY;
		double avgX = Math.abs(boundingBox.maxX - boundingBox.minX);
		double avgZ = Math.abs(boundingBox.maxZ - boundingBox.minZ);
		float f1 = sprite.getU0();
		float f2_alt_x = (float) Math.min(sprite.getU1(), f1 + avgX * Math.abs(sprite.getU1() - sprite.getU0()));
		float f2_alt_z = (float) Math.min(sprite.getU1(), f1 + avgZ * Math.abs(sprite.getU1() - sprite.getU0()));
		float f3 = sprite.getV0();
		float f4_alt = (float) Math.min(sprite.getV1(), f3 + avgY * Math.abs(sprite.getV1() - sprite.getV0()));
		float f4_alt_z = (float) Math.min(sprite.getV1(), f3 + avgZ * Math.abs(sprite.getV1() - sprite.getV0()));
		//north
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(color).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(color).setUv(f2_alt_x, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(color).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(color).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		//south
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(color).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(color).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(color).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(color).setUv(f2_alt_x, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		//east
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(color).setUv(f2_alt_z, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(color).setUv(f2_alt_z, f3).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(color).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(color).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);
		//west
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(color).setUv(f2_alt_z, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(color).setUv(f2_alt_z, f3).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(color).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(color).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
		//bottom
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(color).setUv(f1, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(color).setUv(f2_alt_x, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(color).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(color).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);
		//top
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(color).setUv(f1, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(color).setUv(f2_alt_x, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(color).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(color).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		stack.popPose();
	}
}
