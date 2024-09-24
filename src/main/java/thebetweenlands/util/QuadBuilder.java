package thebetweenlands.util;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

public class QuadBuilder {
	static class Vertex {
		public final Vec3 pos;
		public final float u;
		public final float v;
		public final TextureAtlasSprite sprite;
		public final boolean switchUV;
		public final Transformation transformation;
		public final float[] color;
		public final Vec3 normal;
		public final int blockLight, skyLight;
		public final int tintIndex;
		public final Direction cullFace, orientation;
		public final boolean diffuse;

		private Vertex(Vec3 pos, float u, float v, TextureAtlasSprite sprite, boolean switchUV, Transformation transformation,
				float[] color, Vec3 normal, int blockLight, int skyLight, int tintIndex, Direction cullFace, Direction orientation, boolean diffuse) {
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
	private Transformation transformation;
	private float[] color = new float[]{1f, 1f, 1f, 1f};
	private Vec3 normal;
	private int tintIndex = -1;
	private Direction cullFace, orientation;
	private boolean diffuseLighting = true;

	private final List<Vertex> vertices;

	private int blockLight = -1, skyLight = -1;
	private boolean hasLightmapElement;

	public QuadBuilder(VertexFormat format) {
		this(50, format);
	}

	public QuadBuilder(int vertices, VertexFormat format) {
		this.vertices = new ArrayList<>(vertices);
		this.format = format;
		this.hasLightmapElement = format.contains(VertexFormatElement.UV1);
	}


	/**
	 * Sets the quad's cull face
	 * @return
	 */
	public QuadBuilder setCullFace(@Nullable Direction cullFace) {
		this.cullFace = cullFace;
		return this;
	}

	/**
	 * Sets the quad's orientation. If null, the orientation that matches the normal
	 * the most is used.
	 * @param orientation
	 * @return
	 */
	public QuadBuilder setOrientation(@Nullable Direction orientation) {
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
		if(BetweenlandsConfig.fullbrightBlocks) {
			this.blockLight = blockLight;
			this.skyLight = skyLight;
			// TODO
			if(!this.hasLightmapElement) {
//				this.format.addElement(DefaultVertexFormats.TEX_2S);
				TheBetweenlands.LOGGER.warn("Attempt to set lightmap value on a vertex format that doesn't support it.");
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
	public QuadBuilder setNormal(Vec3 normal) {
		this.normal = normal;
		return this;
	}

	/**
	 * Returns the normal
	 * @return
	 */
	public Vec3 getNormal() {
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
	public QuadBuilder setTransformation(Transformation transformation) {
		this.transformation = transformation;
		return this;
	}

	/**
	 * Returns the current transformation
	 * @return
	 */
	public Transformation getTransformation() {
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
		return this.addVertex(new Vec3(x, y, z), u, v);
	}

	/**
	 * Adds a vertex
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public QuadBuilder addVertex(double x, double y, double z) {
		return this.addVertex(new Vec3(x, y, z));
	}

	/**
	 * Adds a vertex with UV coordinates
	 * @param pos
	 * @param u
	 * @param v
	 * @return
	 */
	public QuadBuilder addVertex(Vec3 pos, float u, float v) {
		this.vertices.add(new Vertex(pos, u, v, this.sprite, this.switchUV, this.transformation, this.color, this.normal, this.blockLight, this.skyLight, this.tintIndex, this.cullFace, this.orientation, this.diffuseLighting));
		return this;
	}

	/**
	 * Adds a vertex
	 * @param pos
	 * @return
	 */
	public QuadBuilder addVertex(Vec3 pos) {
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
		return this.addVertexInferUV(new Vec3(x, y, z));
	}

	/**
	 * Adds a vertex and infers the UV coordinates from the vertex index
	 * @param pos
	 * @return
	 */
	public QuadBuilder addVertexInferUV(Vec3 pos) {
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
		public final Map<Direction, ImmutableList<BakedQuad>> culledQuads;
		public final ImmutableList<BakedQuad> nonCulledQuads;
		private ImmutableList<BakedQuad> allQuads;

		private Quads(Map<Direction, ImmutableList<BakedQuad>> culledQuads, ImmutableList<BakedQuad> nonCulledQuads) {
			this.culledQuads = culledQuads;
			this.nonCulledQuads = nonCulledQuads;
		}

		public ImmutableList<BakedQuad> getAllQuads() {
			if(this.allQuads != null) return this.allQuads;
			final ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
			builder.addAll(this.nonCulledQuads);
			for(Direction direction : Direction.values()) { // Same order each time
				final ImmutableList<BakedQuad> quads = culledQuads.get(direction);
				if(quads != null) builder.addAll(quads);
			}
			this.allQuads = builder.build();
			return this.allQuads;
		}
	}

	/**
	 * Builds the quads
	 * @param builderConsumer Called whenever a quad is baked
	 * @return
	 */
	public Quads build(@Nullable Consumer<CustomQuadVertexConsumer> builderConsumer) {
		if(this.vertices.size() % 4 != 0)
			throw new RuntimeException("Invalid number of vertices");

		ImmutableList.Builder<BakedQuad> nonCulledBuilder = ImmutableList.builder();
		Map<Direction, ImmutableList.Builder<BakedQuad>> builders = new EnumMap<>(Direction.class);
		Map<Direction, ImmutableList<BakedQuad>> quads = new EnumMap<>(Direction.class);

		for(Direction face : Direction.values()) {
			builders.put(face, ImmutableList.builder());
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
				builders.get(quad.getDirection()).add(quad);
			}
		}
		this.vertices.clear();

		for(Direction face : Direction.values()) {
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


//	private void putVertex(VertexFormat format, UnpackedBakedQuad.Builder builder, Vec3 quadNormal, Vertex vert) {
//		boolean hasTransform = vert.transformation != null && !vert.transformation.equals(TRSRTransformation.identity());
//		for (int e = 0; e < format.getElementCount(); e++) {
//			switch (format.getElement(e).getUsage()) {
//			case POSITION:
//				float[] positionData = new float[]{ (float) vert.pos.x, (float) vert.pos.y, (float) vert.pos.z, 1f };
//				if(hasTransform) {
//					Vector4f vec = new Vector4f(positionData);
//					vert.transformation.getMatrix().transform(vec);
//					vec.get(positionData);
//				}
//				builder.put(e, positionData);
//				break;
//			case COLOR:
//				builder.put(e, vert.color);
//				break;
//			case NORMAL:
//				float[] normalData;
//				if(vert.normal != null) {
//					normalData = new float[]{ (float) vert.normal.x, (float) vert.normal.y, (float) vert.normal.z, 0f };
//				} else {
//					normalData = new float[]{ (float) quadNormal.x, (float) quadNormal.y, (float) quadNormal.z, 0f };
//				}
//				if(hasTransform) {
//					Vector4f vec = new Vector4f(normalData);
//					Matrix4f matrix = vert.transformation.getMatrix();
//					matrix.invert();
//					matrix.transpose();
//					matrix.transform(vec);
//					vec.get(normalData);
//				}
//				float dx = normalData[0];
//				float dy = normalData[1];
//				float dz = normalData[2];
//				float len = (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
//				normalData[0] = dx / len;
//				normalData[1] = dy / len;
//				normalData[2] = dz / len;
//				normalData[3] = 0f;
//				builder.put(e, normalData);
//				break;
//			case UV:
//				if (format.getElement(e).getIndex() == 0) {
//					float u = vert.u;
//					float v = vert.v;
//					if(vert.sprite != null) {
//						float pu = u;
//						float pv = v;
//						u = vert.sprite.getInterpolatedU(vert.switchUV ? pv : pu);
//						v = vert.sprite.getInterpolatedV(vert.switchUV ? pu : pv);
//					}
//					builder.put(e, u, v, vert.switchUV ? 1f : 0f, vert.switchUV ? 0f : 1f);
//					break;
//				} else if (vert.blockLight >= 0 && vert.skyLight >= 0 && format.getElement(e).getIndex() == 1){
//					builder.put(e, ((float)vert.blockLight * 0x20) / 0xFFFF, ((float)vert.skyLight * 0x20) / 0xFFFF);
//					break;
//				}
//			default:
//				builder.put(e);
//				break;
//			}
//		}
//	}

	private void putVertex(VertexFormat format, CustomQuadVertexConsumer builder, Vec3 quadNormal, Vertex vert) {
		boolean hasTransform = vert.transformation != null && !vert.transformation.equals(Transformation.identity());
		builder.beginVertex();
		List<VertexFormatElement> elements = format.getElements();
		for (VertexFormatElement element : elements) {
			switch (element.usage()) {
				case POSITION:
					float[] positionData = new float[]{(float) vert.pos.x, (float) vert.pos.y, (float) vert.pos.z, 1f};

					if (hasTransform) {
						Vector4f vec = new Vector4f(positionData);
						vert.transformation.getMatrix().transform(vec);
						vec.get(FloatBuffer.wrap(positionData));
					}

					builder.setPosition(positionData[0], positionData[1], positionData[2]);
					break;
				case COLOR:
					builder.setColor(vert.color[0], vert.color[1], vert.color[2], vert.color[3]);
					break;
				case NORMAL:
					float[] normalData;
					if (vert.normal != null) {
						normalData = new float[]{(float) vert.normal.x, (float) vert.normal.y, (float) vert.normal.z, 0f};
					} else {
						normalData = new float[]{(float) quadNormal.x, (float) quadNormal.y, (float) quadNormal.z, 0f};
					}
					if (hasTransform) {
						Vector4f vec = new Vector4f(normalData);
						Matrix4f matrix = vert.transformation.getMatrix();
						matrix.invert();
						matrix.transpose();
						matrix.transform(vec);
						vec.get(FloatBuffer.wrap(normalData));
					}
					float dx = normalData[0];
					float dy = normalData[1];
					float dz = normalData[2];
					float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
					normalData[0] = dx / len;
					normalData[1] = dy / len;
					normalData[2] = dz / len;
					normalData[3] = 0f;

					builder.setNormal(normalData[0], normalData[1], normalData[2]);
					break;
				case UV:
					if (element.index() == 0) { // is UV0?
						float u = vert.u / 16f;
						float v = vert.v / 16f;
						if (vert.sprite != null) { // We're not allowed to switch the UVs without a sprite, it seems
							float pu = u;
							float pv = v;
							u = vert.sprite.getU(vert.switchUV ? pv : pu);
							v = vert.sprite.getV(vert.switchUV ? pu : pv);
						}
						// 1.12 code
						// builder.put(e, u, v, vert.switchUV ? 1f : 0f, vert.switchUV ? 0f : 1f);
						builder.setUv(u, v);
						break;
					} else if (vert.blockLight >= 0 && vert.skyLight >= 0 && element.index() == 2) { // is UV2?
						// Note: the 1.12 code checks for if the element index is 1 (UV1 in this version); however, I have reason to believe that UV2 is correct here for packed light (see VertexConsumer::setLight)

						final int u = vert.blockLight << 4;
						final int v = vert.skyLight << 4;

						builder.setUv2(u, v);

						break;
					}
				default:
//				builder.put(e);
//				builder.misc(element, new int[element.byteSize() / Integer.BYTES]);
					break;
			}
		}
	}

	private BakedQuad createQuad(VertexFormat format, Vertex vert1, Vertex vert2, Vertex vert3, Vertex vert4, @Nullable Consumer<CustomQuadVertexConsumer> builderConsumer) {
		Vec3 quadNormal = vert4.normal;
		if(quadNormal == null) {
			//Find 3 vertices that aren't at the exact same position to calculate the quad normal
			Vec3[] verts = new Vec3[] {vert1.pos, vert2.pos, vert3.pos, vert4.pos};
			for(int i = 0; i < 4; i++) {
				Vec3 prev = verts[i];
				Vec3 corner = verts[(i + 1) % 4];
				Vec3 next = verts[(i + 2) % 4];
				if(!corner.equals(next) && !corner.equals(prev)) {
					quadNormal = prev.subtract(corner).cross(next.subtract(corner)).scale(-1).normalize();
					break;
				}
			}
			if(quadNormal == null) {
				//What a bizarre quad we have here
				quadNormal = new Vec3(0, 0, 0);
			}
		}

		CustomQuadVertexConsumer builder = new CustomQuadVertexConsumer(format);

		builder.setTintIndex(vert4.tintIndex);
		if(vert4.orientation != null) {
			builder.setDirection(vert4.orientation);
		} else {
			//Use orientation that matches the normal the most
			builder.setDirection(Direction.getNearest(quadNormal.x, quadNormal.y, quadNormal.z));
		}
		// TODO setApplyDiffuseLighting
//		builder.setApplyDiffuseLighting(vert4.diffuse);
		builder.setSprite(vert4.sprite); // Sprite might've changed

		putVertex(format, builder, quadNormal, vert1);
		putVertex(format, builder, quadNormal, vert2);
		putVertex(format, builder, quadNormal, vert3);
		putVertex(format, builder, quadNormal, vert4);

		if(builderConsumer != null) {
			builderConsumer.accept(builder);
		}

		return builder.bakeQuad();
	}


}
