package thebetweenlands.client.renderer;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.util.RandomSource;
import thebetweenlands.util.StalactiteHelper;

public class SpikeRenderer {

	public final int length;
	public final float widthScale;
	public final float heightScale;
	public final float offsetScale;
	public final int bx, by, bz;
	public final float x, y, z;

	public SpikeRenderer(int length, float widthScale, float heightScale, float offsetScale, long seed) {
		this(length, widthScale, heightScale, offsetScale, seed, 0, 0, 0);
	}

	public SpikeRenderer(int length, float widthScale, float heightScale, float offsetScale, long seed, float x, float y, float z) {
		this.length = length;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
		this.offsetScale = offsetScale;
		RandomSource rand = RandomSource.create(seed);
		this.bx = rand.nextInt();
		this.by = rand.nextInt(128);
		this.bz = rand.nextInt();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void build(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, int color) {
		for(int y = 0; y < this.length; y++) {
			int distUp = this.length - 1 - y;
			boolean noTop = true;
			boolean noBottom = false;
			float height = 1.0F;

			int totalHeight = 1 + y + distUp;
			float distToMidBottom, distToMidTop;

			double squareAmount = 1.2D;
			double halfTotalHeightSQ;

			halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
			distToMidBottom = Math.abs(distUp + 1);
			distToMidTop = Math.abs(distUp);

			int minValBottom = 1;
			int minValTop = (noTop && distUp == 0) ? 0 : 1;
			int scaledValBottom = (int) (Math.pow(distToMidBottom, squareAmount) / halfTotalHeightSQ * (8 - minValBottom)) + minValBottom;
			int scaledValTop = (int) (Math.pow(distToMidTop, squareAmount) / halfTotalHeightSQ * (8 - minValTop)) + minValTop;

			float umin = 0;
			float umax = 16;
			float vmin = 0;
			float vmax = 16;

			float halfSize = (float) scaledValBottom / 16 * this.widthScale;
			float halfSizeTexW = halfSize * (umax - umin);
			float halfSize1 = (float) (scaledValTop) / 16 * this.widthScale;
			float halfSizeTex1 = halfSize1 * (umax - umin);

			StalactiteHelper core = StalactiteHelper.getValsFor(this.bx, this.by + y, this.bz);

			if(y == 0 && !noBottom) {
				core.bX = 0.5F;
				core.bZ = 0.5F;
			}

			core.bX *= this.offsetScale;
			core.tX *= this.offsetScale;
			core.bZ *= this.offsetScale;
			core.tZ *= this.offsetScale;

			core.bX += this.x;
			core.tX += this.x;
			core.bZ += this.z;
			core.tZ += this.z;

			// front
			consumer.addVertex(pose, core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize).setUv(umin + halfSizeTexW * 2, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 0.0F, -1.0F);
			consumer.addVertex(pose, core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize).setUv(umin, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 0.0F, -1.0F);
			consumer.addVertex(pose, core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1).setUv(umin, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 0.0F, -1.0F);
			consumer.addVertex(pose, core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1).setUv(umin + halfSizeTex1 * 2, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 0.0F, -1.0F);
			// back
			consumer.addVertex(pose, core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize).setUv(umin + halfSizeTexW * 2, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
			consumer.addVertex(pose, core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize).setUv(umin, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
			consumer.addVertex(pose, core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1).setUv(umin, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
			consumer.addVertex(pose, core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1).setUv(umin + halfSizeTex1 * 2, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
			// left
			consumer.addVertex(pose, core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize).setUv(umin + halfSizeTexW * 2, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, -1.0F, 0.0F, 0.0F);
			consumer.addVertex(pose, core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize).setUv(umin, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, -1.0F, 0.0F, 0.0F);
			consumer.addVertex(pose, core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1).setUv(umin, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, -1.0F, 0.0F, 0.0F);
			consumer.addVertex(pose, core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1).setUv(umin + halfSizeTex1 * 2, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, -1.0F, 0.0F, 0.0F);
			// right
			consumer.addVertex(pose, core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize).setUv(umin + halfSizeTexW * 2, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 1.0F, 0.0F, 0.0F);
			consumer.addVertex(pose, core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize).setUv(umin, vmax).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 1.0F, 0.0F, 0.0F);
			consumer.addVertex(pose, core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1).setUv(umin, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 1.0F, 0.0F, 0.0F);
			consumer.addVertex(pose, core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1).setUv(umin + halfSizeTex1 * 2, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 1.0F, 0.0F, 0.0F);

			// top
			if(distUp == 0) {
				consumer.addVertex(pose, core.tX - halfSize1, this.y + y + height, core.tZ - halfSize1).setUv(umin, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
				consumer.addVertex(pose, core.tX - halfSize1, this.y + y + height, core.tZ + halfSize1).setUv(umin + halfSizeTex1 * 2, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
				consumer.addVertex(pose, core.tX + halfSize1, this.y + y + height, core.tZ + halfSize1).setUv(umin + halfSizeTex1 * 2, vmin + halfSizeTex1 * 2).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
				consumer.addVertex(pose, core.tX + halfSize1, this.y + y + height, core.tZ - halfSize1).setUv(umin, vmin + halfSizeTex1 * 2).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, -1.0F, 0.0F);
			}

			// bottom
			if(y == 0) {
				consumer.addVertex(pose, core.bX - halfSize, this.y + y, core.bZ + halfSize).setUv(umin + halfSizeTexW * 2, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, core.bX - halfSize, this.y + y, core.bZ - halfSize).setUv(umin, vmin).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, core.bX + halfSize, this.y + y, core.bZ - halfSize).setUv(umin, vmin + halfSizeTexW * 2).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, core.bX + halfSize, this.y + y, core.bZ + halfSize).setUv(umin + halfSizeTexW * 2, vmin + halfSizeTexW * 2).setColor(color).setLight(light).setOverlay(overlay).setNormal(pose, 0.0F, 1.0F, 0.0F);
			}
		}
	}
}
