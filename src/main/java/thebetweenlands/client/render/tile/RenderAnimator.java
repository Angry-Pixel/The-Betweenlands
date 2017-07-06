package thebetweenlands.client.render.tile;

import java.util.SplittableRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.client.render.model.tile.ModelAnimator;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAnimator;

public class RenderAnimator extends TileEntitySpecialRenderer<TileEntityAnimator> {
	private static final ModelAnimator model = new ModelAnimator();
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/animator.png");
	public static RenderAnimator instance;
	private RenderManager renderManager;
	private final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

	public RenderAnimator() {
		renderManager = Minecraft.getMinecraft().getRenderManager();
	}

	@Override
	public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
		super.setRendererDispatcher(rendererDispatcherIn);
		instance = this;
	}

	public void renderTileAsItem(double x, double y, double z) {
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		renderMainModel(x, y, z);
		GlStateManager.popMatrix();
	}

	private void renderMainModel(double x, double y, double z) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 2F, z + 0.5F);
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scale(1.5F, 1.5F, 1.5F);
		model.renderAll(0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntityAnimator te, double x, double y, double z, float partialTicks, int destroyStage) {
		int meta = 0;

		if(te != null) {
			meta = te.getBlockMetadata();
		}

		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.rotate(meta * 90.0F - 180.0F, 0.0F, 1F, 0F);
		GlStateManager.disableCull();
		model.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		if(te != null) {
			SplittableRandom rand = new SplittableRandom((long) (te.getPos().getX() + te.getPos().getY() + te.getPos().getZ()));
			double viewRot = 180D + Math.toDegrees(Math.atan2(x + 0.5D, z + 0.5D));

			// Sulfur rendering
			if (te.getStackInSlot(2) != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + 0.5D, y + 0.27D, z + 0.5D);
				GlStateManager.rotate(180, 1, 0, 0);
				int items = te.getStackInSlot(2).getCount();
				for (int i = 0; i < items; i++) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(rand.nextDouble() / 3.0D - 1.0D / 6.0D, 0.0D, rand.nextDouble() / 3.0D - 1.0D / 6.0D);
					GlStateManager.rotate((float)rand.nextDouble() * 30.0f - 15.0f, 1, 0, 0);
					GlStateManager.rotate((float)rand.nextDouble() * 30.0f - 15.0f, 0, 0, 1);
					GlStateManager.scale(0.125D, 0.125D, 0.125D);
					GlStateManager.rotate(90, 1, 0, 0);
					GlStateManager.rotate((float)rand.nextDouble() * 360.0F, 0, 0, 1);
					ItemStack stack = ItemMisc.EnumItemMisc.SULFUR.create(1);
					Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
					renderItem.renderItem(stack, renderItem.getItemModelMesher().getItemModel(stack));
					Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
					GlStateManager.popMatrix();
				}
				GlStateManager.popMatrix();
			}

			// Life crystal
			if (te.getStackInSlot(1) != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + 0.5D, y + 0.43D, z + 0.5D);
				GlStateManager.scale(0.18D, 0.18D, 0.18D);
				GlStateManager.rotate((float) viewRot, 0, 1, 0);
				ItemStack stack = new ItemStack(ItemRegistry.LIFE_CRYSTAL);
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
				renderItem.renderItem(stack, renderItem.getItemModelMesher().getItemModel(stack));
				Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
				GlStateManager.popMatrix();
			}

			// Item
			ItemStack input = te.getStackInSlot(0);
			if (input != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + 0.5D, y + 1.43D, z + 0.5D);

				IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(input);

				if(recipe != null) {
					if (!(input.getItem() instanceof ItemMonsterPlacer) && (recipe == null || recipe.getRenderEntity(input) == null)) {
						GlStateManager.scale(0.3D, 0.3D, 0.3D);
						GlStateManager.rotate((float) viewRot, 0, 1, 0);
						ItemStack stack = te.getStackInSlot(0);
						Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
						renderItem.renderItem(stack, renderItem.getItemModelMesher().getItemModel(stack));
						Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
					} else {
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
						GlStateManager.color(1.0F, 1.0F, 1.0F, 0.65F);
						Entity entity = null;
						if (recipe.getRenderEntity(input) != null) {
							entity = recipe.getRenderEntity(input);
						} else if (input.getItem() instanceof ItemMonsterPlacer) {
							entity = EntityList.createEntityByID(input.getItemDamage(), te.getWorld());
						}
						if (entity != null) {
							GlStateManager.translate(0.0D, -entity.height / 4.0D, 0.0D);
							GlStateManager.rotate((float) viewRot, 0, 1, 0);
							GlStateManager.scale(0.75D, 0.75D, 0.75D);
							entity.setWorld(te.getWorld());
							entity.setRotationYawHead(0F);
							entity.rotationPitch = 0F;
							entity.ticksExisted = (int) this.getWorld().getWorldTime();
							renderManager.doRenderEntity(entity, 0D, 0D, 0D, 0F, 0F, true);
						}
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					}
				}

				GlStateManager.popMatrix();
			}
		}
	}
}
