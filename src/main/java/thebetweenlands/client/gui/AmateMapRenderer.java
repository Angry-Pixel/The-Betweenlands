package thebetweenlands.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.colors.AmateMapColor;
import thebetweenlands.common.savedata.AmateMapItemSavedData;

@OnlyIn(Dist.CLIENT)
public class AmateMapRenderer implements AutoCloseable {
    public final Int2ObjectMap<AmateMapInstance> maps = new Int2ObjectOpenHashMap<>();
    public final TextureManager textureManager;
    public AmateMapRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void update(int p_168766_, AmateMapItemSavedData p_168767_) {
        this.getOrCreateMapInstance(p_168766_, p_168767_).forceUpload();
    }

    public void render(PoseStack p_168772_, MultiBufferSource p_168773_, int p_168774_, AmateMapItemSavedData p_168775_, boolean p_168776_, int p_168777_) {
        this.getOrCreateMapInstance(p_168774_, p_168775_).draw(p_168772_, p_168773_, p_168776_, p_168777_);
    }

    public AmateMapInstance getOrCreateMapInstance(int p_168779_, AmateMapItemSavedData p_168780_) {
        return this.maps.compute(p_168779_, (p_182563_, p_182564_) -> {
            if (p_182564_ == null) {
                return new AmateMapInstance(p_182563_, p_168780_);
            } else {
                p_182564_.replaceMapData(p_168780_);
                return p_182564_;
            }
        });
    }

    public void resetData() {
        for(AmateMapInstance maprenderer$mapinstance : this.maps.values()) {
            maprenderer$mapinstance.close();
        }

        this.maps.clear();
    }

    public void close() {
        this.resetData();
    }

    @OnlyIn(Dist.CLIENT)
    class AmateMapInstance implements AutoCloseable {
        public AmateMapItemSavedData data;
        public final DynamicTexture texture;
        public final RenderType renderType;
        public boolean requiresUpload = true;

        AmateMapInstance(int p_168783_, AmateMapItemSavedData p_168784_) {
            this.data = p_168784_;
            this.texture = new DynamicTexture(128, 128, true);
            ResourceLocation resourcelocation = AmateMapRenderer.this.textureManager.register("amatemap/" + p_168783_, this.texture);
            this.renderType = RenderType.text(resourcelocation);
        }

        void replaceMapData(AmateMapItemSavedData p_182568_) {
            boolean flag = this.data != p_182568_;
            this.data = p_182568_;
            this.requiresUpload |= flag;
        }

        public void forceUpload() {
            this.requiresUpload = true;
        }

            // use amate map color list
            public void updateTexture() {
                for(int i = 0; i < 128; ++i) {
                    for(int j = 0; j < 128; ++j) {
                        int k = j + i * 128;
                        this.texture.getPixels().setPixelRGBA(j, i, AmateMapColor.MAP_COLORS[this.data.colors[k]].color);
                    }
                }

                this.texture.upload();
            }

        public void draw(PoseStack p_93292_, MultiBufferSource p_93293_, boolean p_93294_, int p_93295_) {
            if (this.requiresUpload) {
                this.updateTexture();
                this.requiresUpload = false;
            }

            int i = 0;
            int j = 0;
            float f = 0.0F;
            Matrix4f matrix4f = p_93292_.last().pose();
            VertexConsumer vertexconsumer = p_93293_.getBuffer(this.renderType);
            vertexconsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(p_93295_).endVertex();
            vertexconsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(p_93295_).endVertex();
            vertexconsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(p_93295_).endVertex();
            vertexconsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(p_93295_).endVertex();
            int k = 0;

            for(MapDecoration mapdecoration : this.data.getDecorations()) {
                if (!p_93294_ || mapdecoration.renderOnFrame()) {
                    if (mapdecoration.render(k)) { k++; continue; }
                    p_93292_.pushPose();
                    p_93292_.translate((double)(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F), (double)(0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F), (double)-0.02F);
                        p_93292_.mulPose(Vector3f.ZP.rotationDegrees((float)(mapdecoration.getRot() * 360) / 16.0F));
                        p_93292_.scale(4.0F, 4.0F, 3.0F);
                        p_93292_.translate(-0.125D, 0.125D, 0.0D);
                        byte b0 = mapdecoration.getImage();
                        float f1 = (float)(b0 % 16 + 0) / 16.0F;
                        float f2 = (float)(b0 / 16 + 0) / 16.0F;
                        float f3 = (float)(b0 % 16 + 1) / 16.0F;
                        float f4 = (float)(b0 / 16 + 1) / 16.0F;
                        Matrix4f matrix4f1 = p_93292_.last().pose();
                        float f5 = -0.001F;
                        VertexConsumer vertexconsumer1 = p_93293_.getBuffer(MapRenderer.MAP_ICONS);
                        vertexconsumer1.vertex(matrix4f1, -1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f2).uv2(p_93295_).endVertex();
                        vertexconsumer1.vertex(matrix4f1, 1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f2).uv2(p_93295_).endVertex();
                        vertexconsumer1.vertex(matrix4f1, 1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f4).uv2(p_93295_).endVertex();
                        vertexconsumer1.vertex(matrix4f1, -1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f4).uv2(p_93295_).endVertex();
                        p_93292_.popPose();
                        if (mapdecoration.getName() != null) {
                            Font font = Minecraft.getInstance().font;
                            Component component = mapdecoration.getName();
                            float f6 = (float)font.width(component);
                            float f7 = Mth.clamp(25.0F / f6, 0.0F, 6.0F / 9.0F);
                            p_93292_.pushPose();
                            p_93292_.translate((double)(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F - f6 * f7 / 2.0F), (double)(0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F + 4.0F), (double)-0.025F);
                            p_93292_.scale(f7, f7, 1.0F);
                            p_93292_.translate(0.0D, 0.0D, (double)-0.1F);
                            font.drawInBatch(component, 0.0F, 0.0F, -1, false, p_93292_.last().pose(), p_93293_, false, Integer.MIN_VALUE, p_93295_);
                            p_93292_.popPose();
                        }

                    ++k;
                }
            }
        }
        public void close() {
            this.texture.close();
        }
    }
}
