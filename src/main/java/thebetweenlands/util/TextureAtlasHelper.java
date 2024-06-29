package thebetweenlands.util;

public class TextureAtlasHelper {
	private final float horizontalSegments, verticalSegments, horizontalPadding, verticalPadding;

	/**
	 * Creates a new texture atlas helper.
	 * Some padding may be required in order to prevent texture bleeding
	 * caused by anisotropic filtering. The padding must be the same on all
	 * sides of a segment.
	 * @param texWidth Texture width
	 * @param texHeight Texture height
	 * @param segmentWidth Segment width (including padding)
	 * @param segmentHeight Segment height (including padding)
	 * @param horizontalPadding Horizontal padding
	 * @param verticalPadding Vertical padding
	 */
	public TextureAtlasHelper(int texWidth, int texHeight, int segmentWidth, int segmentHeight, int horizontalPadding, int verticalPadding) {
		this.horizontalSegments = (float)texWidth / (float)segmentWidth;
		this.verticalSegments = (float)texHeight / (float)segmentHeight;
		this.horizontalPadding = (float)horizontalPadding / (float)texWidth;
		this.verticalPadding = (float)verticalPadding / (float)texHeight;
	}

	/**
	 * Creates a new texture atlas helper with equal texture, segment and padding width/height.
	 * Some padding may be required in order to prevent texture bleeding
	 * caused by anisotropic filtering. The padding must be the same on all
	 * sides of a segment.
	 * @param texSize Texture size
	 * @param segmentSize Segment size
	 * @param padding Padding size
	 */
	public TextureAtlasHelper(int texSize, int segmentSize, int padding) {
		this(texSize, texSize, segmentSize, segmentSize, padding, padding);
	}

	/**
	 * Returns the relative (0.0 - 1.0) min. and max. UVs for the given segment of the texture.
	 * Index 0 is min. UV coordinates and index 1 is max. UV coordinates.
	 * @param segment Texture segment
	 * @return Relative min. and max. UVs
	 */
	public float[][] getUVs(int segment) {
		float[][] ret = new float[2][2];
		float diffU = 1.0F;
		float diffV = 1.0F;
		float segmentX = segment % (int)this.horizontalSegments;
		float segmentY = segment / (int)this.verticalSegments;
		float relU = segmentX / this.horizontalSegments;
		float relV = segmentY / this.verticalSegments;
		float segmentMinU = diffU * (relU + this.horizontalPadding);
		float segmentMinV = diffV * (relV + this.verticalPadding);
		float segmentMaxU = diffU * (relU + 1.0F / this.horizontalSegments - this.horizontalPadding);
		float segmentMaxV = diffV * (relV + 1.0F / this.verticalSegments - this.verticalPadding);
		ret[0][0] = segmentMinU;
		ret[0][1] = segmentMinV;
		ret[1][0] = segmentMaxU;
		ret[1][1] = segmentMaxV;
		return ret;
	}

	/**
	 * Returns the relative (0.0 - 1.0) min. and max. interpolated UVs for the given segment of the texture.
	 * Index 0 is min. UV coordinates and index 1 is max. UV coordinates.
	 * @param segment Texture segment
	 * @param umin
	 * @param vmin
	 * @param umax
	 * @param vmax
	 * @return Relative interpolated min. and max. UVs
	 */
	public float[][] getInterpolatedUVs(int segment, float umin, float vmin, float umax, float vmax) {
		float[][] ret = new float[2][2];
		float[][] UVs = this.getUVs(segment);
		float uDiff = UVs[1][0] - UVs[0][0];
		float vDiff = UVs[1][1] - UVs[0][1];
		ret[0][0] = UVs[0][0] + uDiff * umin;
		ret[0][1] = UVs[0][1] + vDiff * vmin;
		ret[1][0] = UVs[0][0] + uDiff * umax;
		ret[1][1] = UVs[0][1] + vDiff * vmax;
		return ret;
	}
}
