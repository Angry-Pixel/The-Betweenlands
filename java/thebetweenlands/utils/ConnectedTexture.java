package thebetweenlands.utils;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Texture segment indices (arrangement depends on the texture size):
 * <pre>
 *  -------
 * | 0 1 2 |
 * | 3 4 . |
 * | . . . |
 *  ------- </pre>
 * <ol start = "0">
 * <li>No connections</li>
 * <li>Straight connection to the left and right</li>
 * <li>Straight connection to the top and bottom</li>
 * <li>Sharp corner</li>
 * <li>Smooth corner</li>
 * </ol>
 * 
 * Quadrant/Corner indices:
 * <pre>
 *  -----
 * | 0 1 |
 * | 2 3 |
 *  ----- </pre>
 *
 * Neighbour indices, index 4 is the center:
 * <pre>
 *  -------
 * | 0 1 2 |
 * | 3 4 5 |
 * | 6 7 8 |
 *  ------- </pre>
 */
public class ConnectedTexture {
	private final TextureAtlasHelper atlas;

	/**
	 * Creates a new connected texture.
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
	public ConnectedTexture(int texWidth, int texHeight, int segmentWidth, int segmentHeight, int horizontalPadding, int verticalPadding) {
		this.atlas = new TextureAtlasHelper(texWidth, texHeight, segmentWidth, segmentHeight, horizontalPadding, verticalPadding);
	}

	/**
	 * Creates a new connected texture with equal texture, segment and padding width/height.
	 * Some padding may be required in order to prevent texture bleeding
	 * caused by anisotropic filtering. The padding must be the same on all
	 * sides of a segment.
	 * @param texSize Texture size
	 * @param segmentSize Segment size
	 * @param padding Padding size
	 */
	public ConnectedTexture(int texSize, int segmentSize, int padding) {
		this(texSize, texSize, segmentSize, segmentSize, padding, padding);
	}

	/**
	 * Creates a new connected texture from a texture atlas helper.
	 * @param atlas
	 */
	public ConnectedTexture(TextureAtlasHelper atlas) {
		this.atlas = atlas;
	}

	/**
	 * Returns the relative (0.0 - 1.0) min. and max. UVs for the given segment and quadrant of the texture.
	 * Index 0 is min. UV coordinates and index 1 is max. UV coordinates.
	 * <p>
	 * See {@link ConnectedTexture} for the segment and quadrant indices
	 * @param segment Texture segment
	 * @param quadrant Texture quadrant
	 * @return Relative Min. and max. UVs
	 */
	public float[][] getUVs(int segment, int quadrant) {
		float[][] ret = new float[2][2];
		float[][] segmentUVs = this.atlas.getUVs(segment);
		float segmentMinU = segmentUVs[0][0];
		float segmentMinV = segmentUVs[0][1];
		float segmentMaxU = segmentUVs[1][0];
		float segmentMaxV = segmentUVs[1][1];
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

	/**
	 * Calculates an index for the given coordinates and the matrix width
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param width Matrix width
	 * @return Index
	 */
	public static int getIndex(int x, int y, int width) {
		return x % width + y * width;
	}

	/**
	 * Calculates the relative (0.0 - 1.0) min. and max. UVs for each corner of a face.
	 * <p>
	 * See {@link ConnectedTexture} for the indices
	 * @param connectionArray Connected neighbours
	 * @return Face UVs
	 */
	public float[][][] getFaceUVs(boolean[] connectionArray) {
		float[][][] ret = new float[4][2][2];
		int tls = 0;
		int trs = 0;
		int bls = 0;
		int brs = 0;
		for(int xo = 0; xo <= 2; xo++) {
			for(int zo = 0; zo <= 2; zo++) {
				boolean currentNeighbourState = connectionArray[getIndex(xo, zo, 3)];
				if((xo != 1 && zo == 1) || (xo == 1 && zo != 1)) {
					//Adjacent neighbour
					if(currentNeighbourState) {
						if(xo == 0) {
							if(!connectionArray[getIndex(1, 2, 3)]) bls = 1;
							if(!connectionArray[getIndex(1, 0, 3)]) tls = 1;
						} else if (xo == 2){
							if(!connectionArray[getIndex(1, 2, 3)]) brs = 1;
							if(!connectionArray[getIndex(1, 0, 3)]) trs = 1;
						} else if(zo == 0) {
							if(!connectionArray[getIndex(0, 1, 3)]) tls = 2;
							if(!connectionArray[getIndex(2, 1, 3)]) trs = 2;
						} else if (zo == 2){
							if(!connectionArray[getIndex(0, 1, 3)]) bls = 2;
							if(!connectionArray[getIndex(2, 1, 3)]) brs = 2;
						}
					}
				} else if(xo != 1 && zo != 1) {
					//Diagonal neighbour
					if(connectionArray[getIndex(xo, 1, 3)] && connectionArray[getIndex(1, zo, 3)]) {
						int segment;
						if(currentNeighbourState) {
							//Full sharp corner
							segment = 3;
						} else {
							//Smooth half corner
							segment = 4;
						}
						if(xo == 2 && zo == 0) {
							trs = segment;
						} else if(xo == 2 && zo == 2) {
							brs = segment;
						} else if(xo == 0 && zo == 2) {
							bls = segment;
						} else {
							tls = segment;
						}
					}
				}
			}
		}
		ret[0] = this.getUVs(tls, 0);
		ret[1] = this.getUVs(trs, 1);
		ret[2] = this.getUVs(bls, 2);
		ret[3] = this.getUVs(brs, 3);
		return ret;
	}

	/**
	 * Creates the connection array
	 * @param blockAccess Block access
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @param dir Face
	 * @param ignoreMeta Ignore metadata
	 * @return Connection array
	 */
	public static boolean[] getConnectionArray(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection dir, boolean ignoreMeta) {
		Block centerBlock = blockAccess.getBlock(x, y, z);
		int centerBlockMeta = blockAccess.getBlockMetadata(x, y, z);
		boolean xp = true;
		boolean yp = true;
		boolean xr = false;
		boolean yr = false;
		boolean zr = false;
		boolean[] connectionArray = new boolean[9];
		switch(dir) {
		case DOWN:
			xp = false;
		case UP:
			xr = true;
			zr = true;
			break;
		case NORTH:
			yp = false;
		case SOUTH:
			xr = true;
			yr = true;
			break;
		case EAST:
			xp = false;
		case WEST:
			zr = true;
			yr = true;
			break;
		default:
			return connectionArray;
		}
		for(int xo = xr ? -1 : 0; xo <= (xr ? 1 : 0); xo++) {
			for(int yo = yr ? -1 : 0; yo <= (yr ? 1 : 0); yo++) {
				for(int zo = zr ? -1 : 0; zo <= (zr ? 1 : 0); zo++) {
					int mx = (xr ? xo : yo) + 1;
					int my = (zr ? zo : (xr ? yo : zo)) + 1;
					int blockIndex = getIndex(xp ? mx : 2 - mx, yp ? my : 2 - my, 3);
					connectionArray[blockIndex] = blockAccess.getBlock(x+xo, y+yo, z+zo) == centerBlock && (ignoreMeta || blockAccess.getBlockMetadata(x+xo, y+yo, z+zo) == centerBlockMeta);
				}
			}
		}
		return connectionArray;
	}

	/**
	 * Calculates the relative (0.0 - 1.0) min. and max. UVs for each corner of a face.
	 * @param blockAccess Block access
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @param dir Face
	 * @param ignoreMeta Ignore metadata
	 * @return Face UVs
	 */
	public float[][][] getFaceUVs(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection dir, boolean ignoreMeta) {
		return this.getFaceUVs(getConnectionArray(blockAccess, x, y, z, dir, ignoreMeta));
	}
}
