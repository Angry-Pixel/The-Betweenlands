package thebetweenlands.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.FMLClientHandler;
import thebetweenlands.common.config.BetweenlandsConfig;

public class QuadBuilder {
	static class Vertex {
		public final Vec3d pos;
		public final float u;
		public final float v;
		public final TextureAtlasSprite sprite;
		public final boolean switchUV;
		public final TRSRTransformation transformation;
		public final float[] color;
		public final Vec3d normal;
		public final int blockLight, skyLight;
		public final int tintIndex;
		public final EnumFacing cullFace, orientation;
		public final boolean diffuse;

		private Vertex(Vec3d pos, float u, float v, TextureAtlasSprite sprite, boolean switchUV, TRSRTransformation transformation, 
				float[] color, Vec3d normal, int blockLight, int skyLight, int tintIndex, EnumFacing cullFace, EnumFacing orientation, boolean diffuse) {
			this.pos = pos;
			this.u = u;
			this.v = v;
			this.sprite = sprite;
			this.switchUV = switchUV;
			this.transformation = transformation;
			this.color = color;
			this.normal = normal;
			this.blockLight = blockLight;
			this.skyLight = skyLight;
			this.tintIndex = tintIndex;
			this.cullFace = cullFace;
			this.orientation = orientation;
			this.diffuse = diffuse;
		}
	}

	public final VertexFormat format;
	private TextureAtlasSprite sprite;
	private boolean switchUV = false;
	private TRSRTransformation transformation;
	private float[] color = new float[]{1f, 1f, 1f, 1f};
	private Vec3d normal;
	private int tintIndex = -1;
	private EnumFacing cullFace, orientation;
	private boolean diffuseLighting = true;

	private final List<Vertex> vertices;

	private int blockLight = -1, skyLight = -1;
	private boolean hasLightmapElement;

	public QuadBuilder(VertexFormat format) {
		this(50, format);
	}

	public QuadBuilder(int vertices, VertexFormat format) {
		this.vertices = new ArrayList<Vertex>(vertices);
		this.format = new VertexFormat(format);
		this.hasLightmapElement = this.format.getElements().contains(DefaultVertexFormats.TEX_2S);
	}

	/**
	 * Sets the quad's cull face
	 * @return
	 */
	public QuadBuilder setCullFace(@Nullable EnumFacing cullFace) {
		this.cullFace = cullFace;
		return this;
	}

	/**
	 * Sets the quad's orientation. If null, the orientation that matches the normal
	 * the most is used.
	 * @param orientation
	 * @return
	 */
	public QuadBuilder setOrientation(@Nullable EnumFacing orientation) {
		this.orientation = orientation;
		return this;
	}

	/**
	 * Sets whether the quad should use diffuse lighting
	 * @param diffuseLighting
	 * @return
	 */
	public QuadBuilder setDiffuseLighting(boolean diffuseLighting) {
		this.diffuseLighting = diffuseLighting;
		return this;
	}

	/**
	 * Sets the tint index
	 * @param index
	 * @return
	 */
	public QuadBuilder setTintIndex(int index) {
		this.tintIndex = index;
		return this;
	}

	/**
	 * Sets the lightmap values
	 * @param blockLight
	 * @param skyLight
	 * @return
	 */
	public QuadBuilder setLightmap(int blockLight, int skyLight) {
		if(BetweenlandsConfig.RENDERING.fullbrightBlocks && ForgeModContainer.forgeLightPipelineEnabled && !FMLClientHandler.instance().hasOptifine()) {
			this.blockLight = blockLight;
			this.skyLight = skyLight;
			if(!this.hasLightmapElement) {
				this.format.addElement(DefaultVertexFormats.TEX_2S);
				this.hasLightmapElement = true;
			}
		} else {
			this.removeLightmap();
		}
		return this;
	}

	/**
	 * Removes the lightmap values and uses the default ones
	 * @return
	 */
	public QuadBuilder removeLightmap() {
		this.blockLight = -1;
		this.skyLight = -1;
		return this;
	}

	/**
	 * Sets the normal.
	 * Set to null for cross product normal.
	 * @param normal
	 * @return
	 */
	public QuadBuilder setNormal(Vec3d normal) {
		this.normal = normal;
		return this;
	}

	/**
	 * Returns the normal
	 * @return
	 */
	public Vec3d getNormal() {
		return this.normal;
	}

	/**
	 * Sets the color
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return
	 */
	public QuadBuilder setColor(float r, float g, float b, float a) {
		this.color = new float[4];
		this.color[0] = r;
		this.color[1] = g;
		this.color[2] = b;
		this.color[3] = a;
		return this;
	}

	/**
	 * Returns the current color
	 * @return
	 */
	public float[] getColor() {
		return this.color;
	}

	/**
	 * Sets the transformation
	 * @param transformation
	 * @return
	 */
	public QuadBuilder setTransformation(TRSRTransformation transformation) {
		this.transformation = transformation;
		return this;
	}

	/**
	 * Returns the current transformation
	 * @return
	 */
	public TRSRTransformation getTransformation() {
		return this.transformation;
	}

	/**
	 * Sets the sprite
	 * @param sprite
	 * @return
	 */
	public QuadBuilder setSprite(TextureAtlasSprite sprite) {
		this.sprite = sprite;
		return this;
	}

	/**
	 * Returns the current sprite
	 * @return
	 */
	public TextureAtlasSprite getSprite() {
		return this.sprite;
	}

	/**
	 * Sets whether the UV coordinates should be switched (U -> V, V -> U)
	 * @return
	 */
	public QuadBuilder setSwitchUV(boolean switchUV) {
		this.switchUV = switchUV;
		return this;
	}

	/**
	 * Returns whether the UV coordinates are currently switched
	 * @return
	 */
	public boolean getSwitchUV() {
		return this.switchUV;
	}

	/**
	 * Adds a vertex with UV coordinates
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @return
	 */
	public QuadBuilder addVertex(double x, double y, double z, float u, float v) {
		return this.addVertex(new Vec3d(x, y, z), u, v);
	}

	/**
	 * Adds a vertex
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public QuadBuilder addVertex(double x, double y, double z) {
		return this.addVertex(new Vec3d(x, y, z));
	}

	/**
	 * Adds a vertex with UV coordinates
	 * @param pos
	 * @param u
	 * @param v
	 * @return
	 */
	public QuadBuilder addVertex(Vec3d pos, float u, float v) {
		this.vertices.add(new Vertex(pos, u, v, this.sprite, this.switchUV, this.transformation, this.color, this.normal, this.blockLight, this.skyLight, this.tintIndex, this.cullFace, this.orientation, this.diffuseLighting));
		return this;
	}

	/**
	 * Adds a vertex
	 * @param pos
	 * @return
	 */
	public QuadBuilder addVertex(Vec3d pos) {
		this.vertices.add(new Vertex(pos, 0.0F, 0.0F, this.sprite, this.switchUV, this.transformation, this.color, this.normal, this.blockLight, this.skyLight, this.tintIndex, this.cullFace, this.orientation, this.diffuseLighting));
		return this;
	}

	/**
	 * Adds a vertex and infers the UV coordinates from the vertex index
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public QuadBuilder addVertexInferUV(double x, double y, double z) {
		return this.addVertexInferUV(new Vec3d(x, y, z));
	}

	/**
	 * Adds a vertex and infers the UV coordinates from the vertex index
	 * @param pos
	 * @return
	 */
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
		this.vertices.add(new Vertex(pos, u, v, this.sprite, this.switchUV, this.transformation, this.color, this.normal, this.blockLight, this.skyLight, this.tintIndex, this.cullFace, this.orientation, this.diffuseLighting));
		return this;
	}

	public static final class Quads {
		public final Map<EnumFacing, ImmutableList<BakedQuad>> culledQuads;
		public final ImmutableList<BakedQuad> nonCulledQuads;

		private Quads(Map<EnumFacing, ImmutableList<BakedQuad>> culledQuads, ImmutableList<BakedQuad> nonCulledQuads) {
			this.culledQuads = culledQuads;
			this.nonCulledQuads = nonCulledQuads;
		}
	}

	/**
	 * Builds the quads
	 * @param builderConsumer Called whenever a quad is baked
	 * @return
	 */
	public Quads build(@Nullable Consumer<UnpackedBakedQuad.Builder> builderConsumer) {
		if(this.vertices.size() % 4 != 0)
			throw new RuntimeException("Invalid number of vertices");

		ImmutableList.Builder<BakedQuad> nonCulledBuilder = ImmutableList.builder();
		Map<EnumFacing, ImmutableList.Builder<BakedQuad>> builders = new EnumMap<>(EnumFacing.class);
		Map<EnumFacing, ImmutableList<BakedQuad>> quads = new EnumMap<>(EnumFacing.class);

		for(EnumFacing face : EnumFacing.VALUES) {
			builders.put(face, ImmutableList.<BakedQuad>builder());
		}

		for(int i = 0; i < this.vertices.size(); i += 4) {
			Vertex vert1 = this.vertices.get(i);
			Vertex vert2 = this.vertices.get(i + 1);
			Vertex vert3 = this.vertices.get(i + 2);
			Vertex vert4 = this.vertices.get(i + 3);
			BakedQuad quad = this.createQuad(this.format, vert1, vert2, vert3, vert4, builderConsumer);
			if(vert4.cullFace == null) {
				nonCulledBuilder.add(quad);
			} else {
				builders.get(quad.getFace()).add(quad);
			}
		}
		this.vertices.clear();

		for(EnumFacing face : EnumFacing.VALUES) {
			quads.put(face, builders.get(face).build());
		}

		return new Quads(quads, nonCulledBuilder.build());
	}

	/**
	 * Builds the quads
	 * @return
	 */
	public Quads build() {
		return this.build(null);
	}

	private void putVertex(VertexFormat format, UnpackedBakedQuad.Builder builder, Vec3d quadNormal, Vertex vert) {
		boolean hasTransform = vert.transformation != null && !vert.transformation.equals(TRSRTransformation.identity());
		for (int e = 0; e < format.getElementCount(); e++) {
			switch (format.getElement(e).getUsage()) {
			case POSITION:
				float[] positionData = new float[]{ (float) vert.pos.x, (float) vert.pos.y, (float) vert.pos.z, 1f };
				if(hasTransform) {
					Vector4f vec = new Vector4f(positionData);
					vert.transformation.getMatrix().transform(vec);
					vec.get(positionData);
				}
				builder.put(e, positionData);
				break;
			case COLOR:
				builder.put(e, vert.color);
				break;
			case NORMAL:
				float[] normalData;
				if(vert.normal != null) {
					normalData = new float[]{ (float) vert.normal.x, (float) vert.normal.y, (float) vert.normal.z, 0f };
				} else {
					normalData = new float[]{ (float) quadNormal.x, (float) quadNormal.y, (float) quadNormal.z, 0f };
				}
				if(hasTransform) {
					Vector4f vec = new Vector4f(normalData);
					Matrix4f matrix = vert.transformation.getMatrix();
					matrix.invert();
					matrix.transpose();
					matrix.transform(vec);
					vec.get(normalData);
				}
				float dx = normalData[0];
				float dy = normalData[1];
				float dz = normalData[2];
				float len = (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
				normalData[0] = dx / len;
				normalData[1] = dy / len;
				normalData[2] = dz / len;
				normalData[3] = 0f;
				builder.put(e, normalData);
				break;
			case UV:
				if (format.getElement(e).getIndex() == 0) {
					float u = vert.u;
					float v = vert.v;
					if(vert.sprite != null) {
						float pu = u;
						float pv = v;
						u = vert.sprite.getInterpolatedU(vert.switchUV ? pv : pu);
						v = vert.sprite.getInterpolatedV(vert.switchUV ? pu : pv);
					}
					builder.put(e, u, v, vert.switchUV ? 1f : 0f, vert.switchUV ? 0f : 1f);
					break;
				} else if (vert.blockLight >= 0 && vert.skyLight >= 0 && format.getElement(e).getIndex() == 1){
					builder.put(e, ((float)vert.blockLight * 0x20) / 0xFFFF, ((float)vert.skyLight * 0x20) / 0xFFFF);
					break;
				}
			default:
				builder.put(e);
				break;
			}
		}
	}

	private BakedQuad createQuad(VertexFormat format, Vertex vert1, Vertex vert2, Vertex vert3, Vertex vert4, @Nullable Consumer<UnpackedBakedQuad.Builder> quadConsumer) {
		Vec3d quadNormal = null;
		if(vert1.normal == null || vert2.normal == null || vert3.normal == null || vert4.normal == null) {
			//Find 3 vertices that aren't at the exact same position to calculate the quad normal
			Vec3d[] verts = new Vec3d[] {vert1.pos, vert2.pos, vert3.pos, vert4.pos};
			for(int i = 0; i < 4; i++) {
				Vec3d prev = verts[i];
				Vec3d corner = verts[(i + 1) % 4];
				Vec3d next = verts[(i + 2) % 4];
				if(!corner.equals(next) && !corner.equals(prev)) {
					quadNormal = prev.subtract(corner).crossProduct(next.subtract(corner)).scale(-1).normalize();
					break;
				}
			}
			if(quadNormal == null) {
				//What a bizarre quad we have here
				quadNormal = new Vec3d(0, 0, 0);
			}
		}
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setQuadTint(vert4.tintIndex);
		if(vert4.orientation != null) {
			builder.setQuadOrientation(vert4.orientation);
		} else {
			//Use orientation that matches the normal the most
			builder.setQuadOrientation(EnumFacing.getFacingFromVector((float)quadNormal.x, (float)quadNormal.y, (float)quadNormal.z));
		}
		builder.setApplyDiffuseLighting(vert4.diffuse);
		builder.setTexture(vert4.sprite);
		putVertex(format, builder, quadNormal, vert1);
		putVertex(format, builder, quadNormal, vert2);
		putVertex(format, builder, quadNormal, vert3);
		putVertex(format, builder, quadNormal, vert4);
		if(quadConsumer != null)
			quadConsumer.accept(builder);
		return builder.build();
	}
}
