package thebetweenlands.utils.connectedtexture;

public class ConnectedTexture {
	private final int texWidth, texHeight, segmentWidth, segmentHeight;
	private final float horizontalSegments, verticalSegments, horizontalPadding, verticalPadding;

	public ConnectedTexture(int texWidth, int texHeight, int segmentWidth, int segmentHeight, int horizontalPadding, int verticalPadding) {
		this.texWidth = texWidth;
		this.texHeight = texHeight;
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		this.horizontalSegments = (float)texWidth / (float)segmentWidth;
		this.verticalSegments = (float)texHeight / (float)segmentHeight;
		this.horizontalPadding = (float)horizontalPadding / (float)texWidth;
		this.verticalPadding = (float)verticalPadding / (float)texHeight;
	}

	public double[][] getRelUV(int segment, int quadrant) {
		double[][] ret = new double[2][2];
		float diffU = 1.0F;
		float diffV = 1.0F;
		float segmentX = segment % (int)this.horizontalSegments;
		float segmentY = segment / (int)this.verticalSegments;
		float relU = (float)segmentX / (float)this.horizontalSegments;
		float relV = (float)segmentY / (float)this.verticalSegments;
		float segmentMinU = diffU * (relU + this.horizontalPadding);
		float segmentMinV = diffV * (relV + this.verticalPadding);
		float segmentMaxU = diffU * (relU + 1.0F / (float)this.horizontalSegments - this.horizontalPadding);
		float segmentMaxV = diffV * (relV + 1.0F / (float)this.verticalSegments - this.verticalPadding);
		float segmentDiffU = segmentMaxU - segmentMinU;
		float segmentDiffV = segmentMaxV - segmentMinV;
		int quadrantX = quadrant % 2;
		int quadrantY = quadrant / 2;
		float quadrantRelU = (float)quadrantX / 2.0F;
		float quadrantRelV = (float)quadrantY / 2.0F;
		ret[0][0] = (float)(segmentMinU + segmentDiffU * quadrantRelU);
		ret[0][1] = (float)(segmentMinV + segmentDiffV * quadrantRelV);
		ret[1][0] = (float)(segmentMinU + segmentDiffU * (quadrantRelU + 0.5F));
		ret[1][1] = (float)(segmentMinV + segmentDiffV * (quadrantRelV + 0.5F));
		return ret;
	}
}
