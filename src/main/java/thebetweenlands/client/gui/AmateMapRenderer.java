package thebetweenlands.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.MapDecorationTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.joml.Matrix4f;
import thebetweenlands.common.colors.AmateMapColor;
import thebetweenlands.common.savedata.AmateMapData;

public class AmateMapRenderer implements AutoCloseable {
	public final Int2ObjectMap<AmateMapInstance> maps = new Int2ObjectOpenHashMap<>();
	public final TextureManager textureManager;
	public final MapDecorationTextureManager decorationTextures;

	public AmateMapRenderer(TextureManager textureManager, MapDecorationTextureManager decorationTextures) {
		this.textureManager = textureManager;
		this.decorationTextures = decorationTextures;
	}

	public void update(MapId mapID, AmateMapData data) {
		this.getOrCreateMapInstance(mapID, data).forceUpload();
	}

	public void render(PoseStack stack, MultiBufferSource buffer, MapId mapID, AmateMapData data, boolean active, int light) {
		this.getOrCreateMapInstance(mapID, data).draw(stack, buffer, active, light);
	}

	public AmateMapInstance getOrCreateMapInstance(MapId mapID, AmateMapData data) {
		return this.maps.compute(mapID.id(), (id, instance) -> {
			if (instance == null) {
				return new AmateMapInstance(mapID, data);
			} else {
				instance.replaceMapData(data);
				return instance;
			}
		});
	}

	public void resetData() {
		for (AmateMapInstance maprenderer$mapinstance : this.maps.values()) {
			maprenderer$mapinstance.close();
		}

		this.maps.clear();
	}

	@Override
	public void close() {
		this.resetData();
	}

	public class AmateMapInstance implements AutoCloseable {
		public AmateMapData data;
		public final DynamicTexture texture;
		public final RenderType renderType;
		public boolean requiresUpload = true;

		public AmateMapInstance(MapId mapID, AmateMapData data) {
			this.data = data;
			this.texture = new DynamicTexture(128, 128, true);
			ResourceLocation resourcelocation = AmateMapRenderer.this.textureManager.register("amatemap/" + mapID, this.texture);
			this.renderType = RenderType.text(resourcelocation);
		}

		public void replaceMapData(AmateMapData data) {
			boolean flag = this.data != data;
			this.data = data;
			this.requiresUpload |= flag;
		}

		public void forceUpload() {
			this.requiresUpload = true;
		}

		// use amate map color list
		public void updateTexture() {
			for (int i = 0; i < 128; ++i) {
				for (int j = 0; j < 128; ++j) {
					int k = j + i * 128;
					this.texture.getPixels().setPixelRGBA(j, i, AmateMapColor.MAP_COLORS[this.data.colors[k]].color);
				}
			}

			this.texture.upload();
		}

		public void draw(PoseStack stack, MultiBufferSource buffer, boolean active, int light) {
			if (this.requiresUpload) {
				this.updateTexture();
				this.requiresUpload = false;
			}

			Matrix4f matrix4f = stack.last().pose();
			VertexConsumer vertexconsumer = buffer.getBuffer(this.renderType);
			vertexconsumer.addVertex(matrix4f, 0.0F, 128.0F, -0.01F).setColor(-1).setUv(0.0F, 1.0F).setLight(light);
			vertexconsumer.addVertex(matrix4f, 128.0F, 128.0F, -0.01F).setColor(1).setUv(1.0F, 1.0F).setLight(light);
			vertexconsumer.addVertex(matrix4f, 128.0F, 0.0F, -0.01F).setColor(1).setUv(1.0F, 0.0F).setLight(light);
			vertexconsumer.addVertex(matrix4f, 0.0F, 0.0F, -0.01F).setColor(1).setUv(0.0F, 0.0F).setLight(light);
			int k = 0;

			for (MapDecoration mapdecoration : this.data.getDecorations()) {
				if (!active || mapdecoration.renderOnFrame()) {
					stack.pushPose();
					stack.translate(0.0F + (float) mapdecoration.x() / 2.0F + 64.0F, 0.0F + (float) mapdecoration.y() / 2.0F + 64.0F, (double) -0.02F);
					stack.mulPose(Axis.ZP.rotationDegrees((mapdecoration.rot() * 360.0F) / 16.0F));
					stack.scale(4.0F, 4.0F, 3.0F);
					stack.translate(-0.125D, 0.125D, 0.0D);
					Matrix4f matrix4f1 = stack.last().pose();
					TextureAtlasSprite textureatlassprite = AmateMapRenderer.this.decorationTextures.get(mapdecoration);
					float f2 = textureatlassprite.getU0();
					float f3 = textureatlassprite.getV0();
					float f4 = textureatlassprite.getU1();
					float f5 = textureatlassprite.getV1();
					VertexConsumer vertexconsumer1 = buffer.getBuffer(RenderType.text(textureatlassprite.atlasLocation()));
					vertexconsumer1.addVertex(matrix4f1, -1.0F, 1.0F, (float) k * -0.001F).setColor(-1).setUv(f2, f3).setLight(light);
					vertexconsumer1.addVertex(matrix4f1, 1.0F, 1.0F, (float) k * -0.001F).setColor(-1).setUv(f4, f3).setLight(light);
					vertexconsumer1.addVertex(matrix4f1, 1.0F, -1.0F, (float) k * -0.001F).setColor(-1).setUv(f4, f5).setLight(light);
					vertexconsumer1.addVertex(matrix4f1, -1.0F, -1.0F, (float) k * -0.001F).setColor(-1).setUv(f2, f5).setLight(light);
					stack.popPose();
					if (mapdecoration.name().isPresent()) {
						Font font = Minecraft.getInstance().font;
						Component component = mapdecoration.name().get();
						float f6 = (float) font.width(component);
						float f7 = Mth.clamp(25.0F / f6, 0.0F, 6.0F / 9.0F);
						stack.pushPose();
						stack.translate(0.0F + (float) mapdecoration.x() / 2.0F + 64.0F - f6 * f7 / 2.0F, 0.0F + (float) mapdecoration.y() / 2.0F + 64.0F + 4.0F, -0.025F);
						stack.scale(f7, f7, 1.0F);
						stack.translate(0.0D, 0.0D, -0.1F);
						font.drawInBatch(component, 0.0F, 0.0F, -1, false, stack.last().pose(), buffer, Font.DisplayMode.NORMAL, Integer.MIN_VALUE, light);
						stack.popPose();
					}

					k++;
				}
			}
		}

		@Override
		public void close() {
			this.texture.close();
		}
	}
}
