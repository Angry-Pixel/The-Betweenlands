package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelDungeonDoorRunes;
import thebetweenlands.client.render.model.tile.ModelDungeonDoorRunesLayer;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;

@SideOnly(Side.CLIENT)
public class RenderDungeonDoorRunes extends TileEntitySpecialRenderer<TileEntityDungeonDoorRunes> {

	private static final ModelDungeonDoorRunes RUNE_BLOCK = new ModelDungeonDoorRunes();
	private static final ModelDungeonDoorRunesLayer RUNE_BLOCK_LAYER = new ModelDungeonDoorRunesLayer();
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/tiles/dungeon_door_runes.png");

	private static final ResourceLocation TEXTURE_1 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_1.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_2.png");
	private static final ResourceLocation TEXTURE_3 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_3.png");
	private static final ResourceLocation TEXTURE_4 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_4.png");
	private static final ResourceLocation TEXTURE_5 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_5.png");
	private static final ResourceLocation TEXTURE_6 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_6.png");
	private static final ResourceLocation TEXTURE_7 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_7.png");
	private static final ResourceLocation TEXTURE_8 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_8.png");

	private static final ResourceLocation TEXTURE_RUNE_GLOW = new ResourceLocation(ModInfo.ID, "textures/tiles/dungeon_runes_glow.png");
	
	public void renderTile(TileEntityDungeonDoorRunes tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		if (state == null || state.getBlock() instanceof BlockDungeonDoorRunes == false)
			return;
		EnumFacing facing = state.getValue(BlockDungeonDoorRunes.FACING);
		boolean invisiBlock = state.getValue(BlockDungeonDoorRunes.INVISIBLE);
		if (invisiBlock)
			return;
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		switch (facing) {
		case UP:
		case DOWN:
		case NORTH:
			GlStateManager.rotate(180F, 0F, 1F, 0F);
			break;
		case SOUTH:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case WEST:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			break;
		case EAST:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			break;
		}
		RUNE_BLOCK.render(tile, 0.0625F, partialTick);
		if(!tile.isMimic())
			GlStateManager.translate(0F, 0F + 0.1375F * (tile.last_tick_slate_1_rotate + (tile.slate_1_rotate - tile.last_tick_slate_1_rotate) * partialTick) * 0.0625F, 0F + 0.275F * (tile.last_tick_recess_pos + (tile.recess_pos - tile.last_tick_recess_pos) * partialTick) * 0.0625F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		if(!tile.is_gate_entrance) {
			bindTexture(getTextureFromRotationIndex(tile.top_state_prev));
			RUNE_BLOCK_LAYER.renderTopOverlay(tile, TEXTURE_RUNE_GLOW, tile.renderTicks, 0.0625F, partialTick);

			bindTexture(getTextureFromRotationIndex(tile.mid_state_prev));
			RUNE_BLOCK_LAYER.renderMidOverlay(tile, TEXTURE_RUNE_GLOW, tile.renderTicks, 0.0625F, partialTick);

			bindTexture(getTextureFromRotationIndex(tile.bottom_state_prev));
			RUNE_BLOCK_LAYER.renderBottomOverlay(tile, TEXTURE_RUNE_GLOW, tile.renderTicks, 0.0625F, partialTick);
		}
		
		if(tile.is_gate_entrance && tile.slate_1_rotate <= 270) {
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			
			ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			texture.setBlurMipmap(false, false);
			
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			
			GlStateManager.enableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.03125F, 1.03125F, 1.03125F);
			
			IBakedModel itemModel = renderItem.getItemModelWithOverrides(tile.cachedStack(), (World) null, (EntityLivingBase) null);
			itemModel = ForgeHooksClient.handleCameraTransforms(itemModel, ItemCameraTransforms.TransformType.NONE, false);
			
			renderItem.renderItem(tile.cachedStack(), itemModel);
			
			GlStateManager.popMatrix();
			
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			
			texture.restoreLastBlurMipmap();
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

	}

	@Override
	public void render(TileEntityDungeonDoorRunes tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tile == null || !tile.hasWorld()) {
			renderTileAsItem(x, y, z);
			return;
		}
		renderTile(tile, x, y, z, partialTicks, destroyStage, alpha);
	}

	private void renderTileAsItem(double x, double y, double z) {
		GlStateManager.pushMatrix();
		bindTexture(TEXTURE);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);
		RUNE_BLOCK.renderItem(0.0625F);
		GlStateManager.popMatrix();
	}

    public ResourceLocation getTextureFromRotationIndex(int index) {
		switch (index) {
		case 0 :
			return TEXTURE_1;
		case 1 :
			return TEXTURE_2;
		case 2 :
			return TEXTURE_3;
		case 3 :
			return TEXTURE_4;
		case 4 :
			return TEXTURE_5;
		case 5 :
			return TEXTURE_6;
		case 6 :
			return TEXTURE_7;
		case 7 :
			return TEXTURE_8;
		}
		return TEXTURE_1;
	}
}