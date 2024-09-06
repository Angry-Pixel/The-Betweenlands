package thebetweenlands.util;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.textures.UnitTextureAtlasSprite;

// More flexible than the ones provided by default, but there are still improvements I want to make
public class CustomQuadVertexConsumer implements VertexConsumer {
	
	private final int[] elementOffsets;
	private final int vertexStride;
	private final int[] quadData;
	private final VertexFormat format;
	
	private int vertexIndex = 0;
	private boolean building = false;

	private int tintIndex = -1;
	private Direction direction = Direction.DOWN;
	private TextureAtlasSprite sprite = UnitTextureAtlasSprite.INSTANCE;
	private boolean shade;
	private boolean hasAmbientOcclusion;
	
	public CustomQuadVertexConsumer(VertexFormat format) {
		this.format = format;
		this.vertexStride = this.format.getVertexSize() >> 2;
		this.quadData = new int[4 * vertexStride];
		this.elementOffsets = this.format.getOffsetsByElement().clone();
	}

	public VertexFormat getVertexFormat() {
		return this.format;
	}
	
	public int getElementOffset(VertexFormatElement element) {
		final int offset = this.elementOffsets[element.id()];
		return offset != -1 ? offset >> 2 : offset;
	}
	
	public int getElementOffsetOrThrow(VertexFormatElement element) {
		final int offset = this.elementOffsets[element.id()];
		if(offset == -1)
			throw new IllegalArgumentException("This vertex format does not support elements of type " + element);
		return offset >> 2;
	}
	
	public CustomQuadVertexConsumer beginVertex() {
		if (building) {
			if (++vertexIndex >= 4) {
				throw new IllegalStateException("Expected quad export after fourth vertex");
			}
		}
		building = true;
		
		return this;
	}
	
	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		this.beginVertex();
		this.setPosition(x, y, z);
		return this;
	}
	
	public CustomQuadVertexConsumer setPosition(float x, float y, float z) {
		int offset = this.vertexIndex * this.vertexStride + this.getElementOffsetOrThrow(VertexFormatElement.POSITION);
		this.quadData[offset] = Float.floatToRawIntBits(x);
		this.quadData[offset + 1] = Float.floatToRawIntBits(y);
		this.quadData[offset + 2] = Float.floatToRawIntBits(z);
		return this;
	}

	@Override
	public VertexConsumer setColor(int red, int green, int blue, int alpha) {
		int offset = this.vertexIndex * this.vertexStride + this.getElementOffsetOrThrow(VertexFormatElement.COLOR);
		this.quadData[offset] = ((alpha & 0xFF) << 24) |
				((blue & 0xFF) << 16) |
				((green & 0xFF) << 8) |
				(red & 0xFF);
		return this;
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		int offset = this.vertexIndex * this.vertexStride + this.getElementOffsetOrThrow(VertexFormatElement.UV0);
		quadData[offset] = Float.floatToRawIntBits(u);
		quadData[offset + 1] = Float.floatToRawIntBits(v);
		return this;
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		int offset = this.vertexIndex * this.vertexStride + this.getElementOffsetOrThrow(VertexFormatElement.UV1);
		this.quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
		return this;
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		int offset = this.vertexIndex * this.vertexStride + this.getElementOffsetOrThrow(VertexFormatElement.UV2);
		this.quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
		return this;
	}

	@Override
	public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
		int offset = this.vertexIndex * this.vertexStride + this.getElementOffsetOrThrow(VertexFormatElement.NORMAL);
		this.quadData[offset] = ((int) (normalX * 127.0f) & 0xFF) |
				(((int) (normalY * 127.0f) & 0xFF) << 8) |
				(((int) (normalZ * 127.0f) & 0xFF) << 16);
		return this;
	}

	@Override
	public VertexConsumer misc(VertexFormatElement element, int... rawData) {
		int offset = this.vertexIndex * this.vertexStride + this.getElementOffsetOrThrow(element);
		System.arraycopy(rawData, 0, this.quadData, offset, rawData.length);
		return this;
	}

	@Deprecated
	public CustomQuadVertexConsumer putRaw(int rawOffset, int... rawData) {
		assert rawOffset > 0;
		int offset = this.vertexIndex * this.vertexStride + rawOffset;
		System.arraycopy(rawData, 0, this.quadData, offset, rawData.length);
		return this;
	}

	public void setTintIndex(int tintIndex) {
		this.tintIndex = tintIndex;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setSprite(TextureAtlasSprite sprite) {
		this.sprite = sprite;
	}

	public void setShade(boolean shade) {
		this.shade = shade;
	}

	public void setHasAmbientOcclusion(boolean hasAmbientOcclusion) {
		this.hasAmbientOcclusion = hasAmbientOcclusion;
	}

	public BakedQuad bakeQuad() {
		if (!building || ++vertexIndex != 4) {
			throw new IllegalStateException("Not enough vertices available. Vertices in buffer: " + vertexIndex);
		}
		
		BakedQuad quad = new BakedQuad(quadData.clone(), tintIndex, direction, sprite, shade, hasAmbientOcclusion);
		vertexIndex = 0;
		building = false;
		Arrays.fill(quadData, 0);
		return quad;
	}
	
}
