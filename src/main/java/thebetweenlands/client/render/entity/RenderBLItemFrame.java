package thebetweenlands.client.render.entity;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderItemFrame;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityBLItemFrame;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderBLItemFrame extends RenderItemFrame
{
    private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");
    private final Minecraft mc = Minecraft.getMinecraft();
    public static final ModelResourceLocation FRAME_MODEL = new ModelResourceLocation(new ResourceLocation(ModInfo.ID, "item_frame"), "normal");
    public static final ModelResourceLocation FRAME_BG_MODEL = new ModelResourceLocation(new ResourceLocation(ModInfo.ID, "item_frame"), "background");

    protected final RenderItem itemRenderer;

    public RenderBLItemFrame(RenderManager renderManager) {
        super(renderManager, Minecraft.getMinecraft().getRenderItem());
        itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityItemFrame entity) {
        return null;
    }

    @Override
    public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityBLItemFrame blItemFrame = (EntityBLItemFrame)entity;

        GlStateManager.pushMatrix();
        BlockPos blockpos = entity.getHangingPosition();
        double d0 = (double)blockpos.getX() - entity.posX + x;
        double d1 = (double)blockpos.getY() - entity.posY + y;
        double d2 = (double)blockpos.getZ() - entity.posZ + z;
        GlStateManager.translate(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);

        if(blItemFrame.realFacingDirection.getAxis() == EnumFacing.Axis.Y) {
            GlStateManager.rotate(blItemFrame.realFacingDirection == EnumFacing.DOWN ? -90.0F : 90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(blItemFrame.realFacingDirection == EnumFacing.UP ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
        } else {
            GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        }

        this.renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        renderModel(entity, mc);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        if(blItemFrame.IsFrameGlowing()) {
            GlStateManager.enableLighting();
        }

        GlStateManager.popMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.4375F);

        boolean flipItem = blItemFrame.realFacingDirection == EnumFacing.DOWN && !blItemFrame.getDisplayedItem().isEmpty() && blItemFrame.getDisplayedItem().getItem() instanceof ItemCompass;
        GlStateManager.rotate(flipItem ? -180.0F : 0.0F, 0.0F, 1.0F, 0.0F);

        this.renderItem(entity);
        GlStateManager.popMatrix();
    }


    private void renderItem(EntityItemFrame itemFrame)
    {
        EntityBLItemFrame blItemFrame = (EntityBLItemFrame)itemFrame;

        ItemStack itemstack = itemFrame.getDisplayedItem();

        if (!itemstack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            boolean flag = itemstack.getItem() instanceof net.minecraft.item.ItemMap;
            int i = flag ? itemFrame.getRotation() % 4 * 2 : itemFrame.getRotation();
            GlStateManager.rotate((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);

            net.minecraftforge.client.event.RenderItemInFrameEvent event = new net.minecraftforge.client.event.RenderItemInFrameEvent(itemFrame, this);
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
            {
                if (flag)
                {
                    this.renderManager.renderEngine.bindTexture(MAP_BACKGROUND_TEXTURES);
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                    float f = 0.0078125F;
                    GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
                    GlStateManager.translate(-64.0F, -64.0F, 0.0F);
                    MapData mapdata = ((net.minecraft.item.ItemMap) itemstack.getItem()).getMapData(itemstack, itemFrame.world);
                    GlStateManager.translate(0.0F, 0.0F, -1.0F);

                    if (mapdata != null)
                    {
                        this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
                    }
                }
                else
                {
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);

                    GlStateManager.pushAttrib();
                    RenderHelper.enableStandardItemLighting();

                    if(itemstack.getItem() instanceof ItemMob) {
                        ItemMob itemMob = (ItemMob)itemstack.getItem();
                        Entity entity = itemMob.createCapturedEntity(itemFrame.world, 0, 0, 0, itemstack, false);

                        if (entity != null && !(entity instanceof IEntityMultiPart)) {
                            float horizontalOffset = 0;

                            if(entity instanceof EntityAnadia) {
                                horizontalOffset = entity.width / 4;
                            }

                            entity.setPositionAndRotation(0, 0, 0, 0, 0);

                            if(blItemFrame.realFacingDirection.getAxis().equals(EnumFacing.Axis.Y)) {
                                GlStateManager.rotate(-90F, 1F, 0F, 0F);
                            } else {
                                GlStateManager.translate(horizontalOffset, -entity.height / 2D, -0.05D);
                            }

                            GlStateManager.rotate(90, 0, 1, 0);
                            entity.rotationPitch = 0F;
                            entity.ticksExisted = 0;
                            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0D, 0D, 0D, 0F, 0F, true);
                        } else {
                            this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
                        }
                    } else {
                        this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
                    }

                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popAttrib();
                }
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    protected void renderModel(EntityItemFrame entity, Minecraft mc) {
        EntityBLItemFrame itemFrame = (EntityBLItemFrame)entity;

        if(itemFrame.IsFrameInvisible() && !itemFrame.getDisplayedItem().isEmpty()) {
            if(itemFrame.IsFrameGlowing()) {
                GlStateManager.disableLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            }

            return;
        }

        BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();

        if (entity.getDisplayedItem().getItem() instanceof net.minecraft.item.ItemMap) {
            if(itemFrame.IsFrameGlowing()) {
                GlStateManager.disableLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            }

            IBakedModel model = modelmanager.getModel(RenderDraeton.FRAME_MAP_MODEL);
            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(model, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            IBakedModel woodModel, coloredModel;

            woodModel = modelmanager.getModel(FRAME_MODEL);
            coloredModel = modelmanager.getModel(FRAME_BG_MODEL);

            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(woodModel, 1.0F, 1.0F, 1.0F, 1.0F);

            int color = EnumBLDyeColor.byMetadata(((EntityBLItemFrame) entity).getColor()).getColorValue();
            float r = (color >> 16 & 0xFF) / 255F;
            float g = (color >> 8 & 0xFF) / 255F;
            float b = (color & 0xFF) / 255F;

            if(itemFrame.IsFrameGlowing()) {
                GlStateManager.disableLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            }

            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(coloredModel, 1.0F, r, g, b);
        }
    }
}
