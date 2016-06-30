package thebetweenlands.client.render.models.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public class QuadBuilder {
	static class Vertex {
		public final Vec3d pos;
		public final float u;
		public final float v;
		public final TextureAtlasSprite sprite;

		private Vertex(Vec3d pos, float u, float v, TextureAtlasSprite sprite) {
			this.pos = pos;
			this.u = u;
			this.v = v;
			this.sprite = sprite;
		}
	}

	public final VertexFormat format;
	public TextureAtlasSprite sprite;

	private final List<Vertex> vertices = new ArrayList<Vertex>();

	public QuadBuilder(VertexFormat format) {
		this.format = format;
	}

	public QuadBuilder setSprite(TextureAtlasSprite sprite) {
		if(this.vertices.size() % 4 != 0)
			throw new RuntimeException("Can't change sprite in quad");
		this.sprite = sprite;
		return this;
	}

	public QuadBuilder addVertex(Vec3d pos, float u, float v) {
		this.vertices.add(new Vertex(pos, u, v, this.sprite));
		return this;
	}

	public QuadBuilder addVertex(Vec3d pos) {
		this.vertices.add(new Vertex(pos, 0.0F, 0.0F, this.sprite));
		return this;
	}

	public QuadBuilder addVertexInferUV(Vec3d pos) {
		int relIndex = this.vertices.size() % 4;
		float u = 0.0F;
		float v = 0.0F;
		switch(relIndex) {
		default:
		case 0:
			break;
		case 1:
			v = 16.0F;
			break;
		case 2:
			u = 16.0F;
			v = 16.0F;
			break;
		case 3:
			u = 16.0F;
			break;
		}
		this.vertices.add(new Vertex(pos, u, v, this.sprite));
		return this;
	}

	public List<BakedQuad> build(@Nullable Consumer<UnpackedBakedQuad.Builder> quadConsumer) {
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		if(this.vertices.size() % 4 != 0)
			throw new RuntimeException("Invalid number of vertices");
		for(int i = 0; i < this.vertices.size(); i += 4) {
			Vertex vert1 = this.vertices.get(i);
			Vertex vert2 = this.vertices.get(i + 1);
			Vertex vert3 = this.vertices.get(i + 2);
			Vertex vert4 = this.vertices.get(i + 3);
			quads.add(this.createQuad(this.format, 
					vert1.pos, vert1.u, vert1.v, 
					vert2.pos, vert2.u, vert2.v, 
					vert3.pos, vert3.u, vert3.v, 
					vert4.pos, vert4.u, vert4.v, 
					vert1.sprite, quadConsumer));
		}
		this.vertices.clear();
		return quads;
	}

	public List<BakedQuad> build() {
		return this.build(null);
	}

	private void putVertex(VertexFormat format, UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u, float v, @Nullable TextureAtlasSprite sprite) {
		for (int e = 0; e < format.getElementCount(); e++) {
			switch (format.getElement(e).getUsage()) {
			case POSITION:
				builder.put(e, (float)x, (float)y, (float)z, 1.0f);
				break;
			case COLOR:
				builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
				break;
			case UV:
				if (format.getElement(e).getIndex() == 0) {
					if(sprite != null) {
						u = sprite.getInterpolatedU(u);
						v = sprite.getInterpolatedV(v);
					}
					builder.put(e, u, v, 0f, 1f);
					break;
				}
			case NORMAL:
				builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
				break;
			default:
				builder.put(e);
				break;
			}
		}
	}

	private BakedQuad createQuad(
			VertexFormat format,
			Vec3d vert1, float u1, float v1,
			Vec3d vert2, float u2, float v2,
			Vec3d vert3, float u3, float v3,
			Vec3d vert4, float u4, float v4,
			@Nullable TextureAtlasSprite sprite,
			@Nullable Consumer<UnpackedBakedQuad.Builder> quadConsumer) {
		Vec3d normal = vert1.subtract(vert2).crossProduct(vert3.subtract(vert2));

		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setTexture(sprite);
		putVertex(format, builder, normal, vert1.xCoord, vert1.yCoord, vert1.zCoord, 0, 0, sprite);
		putVertex(format, builder, normal, vert2.xCoord, vert2.yCoord, vert2.zCoord, 0, 16, sprite);
		putVertex(format, builder, normal, vert3.xCoord, vert3.yCoord, vert3.zCoord, 16, 16, sprite);
		putVertex(format, builder, normal, vert4.xCoord, vert4.yCoord, vert4.zCoord, 16, 0, sprite);
		if(quadConsumer != null)
			quadConsumer.accept(builder);
		return builder.build();
	}
}
