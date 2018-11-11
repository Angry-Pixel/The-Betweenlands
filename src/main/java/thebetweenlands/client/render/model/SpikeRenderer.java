package thebetweenlands.client.render.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.StalactiteHelper;

public class SpikeRenderer {
	private List<BakedQuad> quads = new ArrayList<>();

	public final int length;
	public final float widthScale;
	public final float heightScale;
	public final float offsetScale;
	public final int bx, by, bz;
	public final double x, y, z;

	private VertexFormat format;

	private TextureAtlasSprite sprite;

	public SpikeRenderer(int length, float widthScale, float heightScale, float offsetScale, long seed) {
		this(length, widthScale, heightScale, offsetScale, seed, 0, 0, 0);
	}

	public SpikeRenderer(int length, float widthScale, float heightScale, float offsetScale, long seed, double x, double y, double z) {
		this.length = length;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
		this.offsetScale = offsetScale;
		Random rand = new Random();
		rand.setSeed(seed);
		this.bx = rand.nextInt();
		this.by = rand.nextInt(128);
		this.bz = rand.nextInt();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public TextureAtlasSprite getSprite() {
		return this.sprite;
	}

	public VertexFormat getFormat() {
		return this.format;
	}

	public SpikeRenderer build(VertexFormat format, TextureAtlasSprite sprite) {
		return this.build(format, sprite, sprite);
	}

	public SpikeRenderer build(VertexFormat format, TextureAtlasSprite bottomSprite, TextureAtlasSprite midSprite) {
		this.sprite = bottomSprite;
		this.format = format;

		QuadBuilder builder = new QuadBuilder(format);

		for(int y = 0; y < this.length; y++) {
			int distUp = this.length - 1 - y;
			int distDown = y;
			boolean noTop = true;
			boolean noBottom = false;
			float height = 1.0F;

			int totalHeight = 1 + distDown + distUp;
			float distToMidBottom, distToMidTop;

			double squareAmount = 1.2D;
			double halfTotalHeightSQ;

			if(noTop) {
				halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
				distToMidBottom = Math.abs(distUp + 1);
				distToMidTop = Math.abs(distUp);
			} else if(noBottom) {
				halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
				distToMidBottom = Math.abs(distDown);
				distToMidTop = Math.abs(distDown + 1);
			} else {
				float halfTotalHeight = totalHeight * 0.5F;
				halfTotalHeightSQ = Math.pow(halfTotalHeight, squareAmount);
				distToMidBottom = Math.abs(halfTotalHeight - distUp - 1);
				distToMidTop = Math.abs(halfTotalHeight - distUp);
			}

			int minValBottom = (noBottom && distDown == 0) ? 0 : 1;
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

			if(distDown == 0 && !noBottom) {
				core.bX = 0.5D;
				core.bZ = 0.5D;
			}
			if(distUp == 0 && !noTop) {
				core.tX = 0.5D;
				core.tZ = 0.5D;
			}

			core.bX *= this.offsetScale;
			core.tX *= this.offsetScale;
			core.bZ *= this.offsetScale;
			core.tZ *= this.offsetScale;

			core.bX += this.x;
			core.tX += this.x;
			core.bZ += this.z;
			core.tZ += this.z;

			if(y == 0) {
				builder.setSprite(bottomSprite);
			} else {
				builder.setSprite(midSprite);
			}

			// front
			builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin + halfSizeTexW * 2, vmax);
			builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin, vmax);
			builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin, vmin);
			builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin + halfSizeTex1 * 2, vmin);
			// back
			builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin + halfSizeTexW * 2, vmax);
			builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin, vmax);
			builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin, vmin);
			builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);
			// left
			builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin + halfSizeTexW * 2, vmax);
			builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin, vmax);
			builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin, vmin);
			builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin + halfSizeTex1 * 2, vmin);
			// right
			builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin + halfSizeTexW * 2, vmax);
			builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin, vmax);
			builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin, vmin);
			builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);

			// top
			if(distUp == 0) {
				builder.addVertex(core.tX - halfSize1, this.y + y + height, core.tZ - halfSize1, umin, vmin);
				builder.addVertex(core.tX - halfSize1, this.y + y + height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);
				builder.addVertex(core.tX + halfSize1, this.y + y + height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin + halfSizeTex1 * 2);
				builder.addVertex(core.tX + halfSize1, this.y + y + height, core.tZ - halfSize1, umin, vmin + halfSizeTex1 * 2);
			}

			// bottom
			if(distDown == 0) {
				builder.addVertex(core.bX - halfSize, this.y + y, core.bZ + halfSize, umin + halfSizeTexW * 2, vmin);
				builder.addVertex(core.bX - halfSize, this.y + y, core.bZ - halfSize, umin, vmin);
				builder.addVertex(core.bX + halfSize, this.y + y, core.bZ - halfSize, umin, vmin + halfSizeTexW * 2);
				builder.addVertex(core.bX + halfSize, this.y + y, core.bZ + halfSize, umin + halfSizeTexW * 2, vmin + halfSizeTexW * 2);
			}
		}

		this.quads = builder.build().nonCulledQuads;

		return this;
	}

	public void upload(BufferBuilder buffer) {
		for(BakedQuad quad : this.quads) {
			buffer.addVertexData(quad.getVertexData());
		}
	}

	public void render() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, this.format);

		this.upload(buffer);

		tessellator.draw();
	}
}
