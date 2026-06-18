package thebetweenlands.client.model.baked.aspectruscrop;

import thebetweenlands.util.QuadBuilder;

/**
 * Procedurally generated geometry from the 1.12.2 Tabula
 */
public final class AspectrusCropGeometry {

	private AspectrusCropGeometry() {
	}

	public static void buildFence(QuadBuilder builder) {
		final float min = 6F / 16F;
		final float max = 10F / 16F;
		final float yBot = 0F;
		final float yTop = 1F;
		final float uMin = 6F;
		final float uMax = 10F;
		final float vTop = 0F;
		final float vBot = 16F;
		final float capV = 4F;

		// west
		builder.addVertex(max, yBot, max, uMin, vBot);
		builder.addVertex(max, yBot, min, uMax, vBot);
		builder.addVertex(max, yTop, min, uMax, vTop);
		builder.addVertex(max, yTop, max, uMin, vTop);


		// east 
		builder.addVertex(min, yBot, min, uMin, vBot);
		builder.addVertex(min, yBot, max, uMax, vBot);
		builder.addVertex(min, yTop, max, uMax, vTop);
		builder.addVertex(min, yTop, min, uMin, vTop);

		// south 
		builder.addVertex(min, yBot, max, uMin, vBot);
		builder.addVertex(max, yBot, max, uMax, vBot);
		builder.addVertex(max, yTop, max, uMax, vTop);
		builder.addVertex(min, yTop, max, uMin, vTop);

		// north
		builder.addVertex(max, yBot, min, uMin, vBot);
		builder.addVertex(min, yBot, min, uMax, vBot);
		builder.addVertex(min, yTop, min, uMax, vTop);
		builder.addVertex(max, yTop, min, uMin, vTop);

		// up
		builder.addVertex(min, yTop, min, uMin, vTop);
		builder.addVertex(min, yTop, max, uMin, capV);
		builder.addVertex(max, yTop, max, uMax, capV);
		builder.addVertex(max, yTop, min, uMax, vTop);

		// down
		builder.addVertex(min, yBot, max, uMin, vTop);
		builder.addVertex(min, yBot, min, uMin, capV);
		builder.addVertex(max, yBot, min, uMax, capV);
		builder.addVertex(max, yBot, max, uMax, vTop);
	}

	public static void buildStage4(QuadBuilder builder) {
		//leaf7b
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);

		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 2.25F, 10.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 2.25F, 10.75F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);

		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 2.25F, 10.75F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 2.25F, 10.5F);

		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 1.125F, 10.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.75F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.75F, 9.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 1.125F, 9.5F);

		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 1.125F, 9.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.75F, 9.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.75F, 10.5F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 1.125F, 10.5F);

		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0.5F, 9.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.125F, 9.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);

		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.125F, 9.5F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0.5F, 9.5F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);

		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);

		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 0F, 10.75F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0F, 10.5F);

		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0F, 10.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 0F, 10.75F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		//leaf8b
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);

		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 1.75F, 12.75F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 1.75F, 13F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);

		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 1.75F, 13F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 1.75F, 12.75F);

		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.875F, 12.75F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 1.375F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.375F, 12F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0.875F, 12F);

		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0.875F, 12F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.375F, 12F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 1.375F, 12.75F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.875F, 12.75F);

		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 12F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 12F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);

		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 12F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 12F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);

		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);

		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0F, 13F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0F, 12.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0F, 12.75F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0F, 13F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		//leaf9b
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);

		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 4.5F, 7.5F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 4.5F, 7.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);

		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 4.5F, 7.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 4.5F, 7.5F);

		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.625F, 7.5F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 4.125F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4.125F, 6.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 3.625F, 6.75F);

		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 3.625F, 6.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4.125F, 6.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 4.125F, 7.5F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.625F, 7.5F);

		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.75F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);

		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.75F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);

		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);

		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 2.75F, 7.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 2.75F, 7.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 2.75F, 7.5F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 2.75F, 7.75F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		//leaf11b
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);

		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 5F, 12.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 5F, 12.5F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);

		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 5F, 12.5F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 5F, 12.25F);

		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.875F, 12.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 4.5F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.5F, 11.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 3.875F, 11.25F);

		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 3.875F, 11.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.5F, 11.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 4.5F, 12.25F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.875F, 12.25F);

		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.25F, 11.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.875F, 11.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);

		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.875F, 11.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.25F, 11.25F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);

		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);

		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 2.75F, 12.5F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 2.75F, 12.25F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 2.75F, 12.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 2.75F, 12.5F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		//leaf11
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.75F, 11F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 11F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 4.375F, 10.25F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.75F, 10.25F);

		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.75F, 10.25F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 4.375F, 10.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 11F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.75F, 11F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.125F, 11F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.125F, 10.25F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 3.75F, 10.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.75F, 11F);

		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.75F, 11F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 3.75F, 10.25F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.125F, 10.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.125F, 11F);

		//leaf6
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.875F, 6.5F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.375F, 6.5F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 1.375F, 5.75F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.875F, 5.75F);

		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.875F, 5.75F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 1.375F, 5.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.375F, 6.5F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.875F, 6.5F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.375F, 6.5F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.375F, 5.75F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 0.875F, 5.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 0.875F, 6.5F);

		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 0.875F, 6.5F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 0.875F, 5.75F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.375F, 5.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.375F, 6.5F);

		//leaf9
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.625F, 6.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4.125F, 6.5F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 4.125F, 5.75F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.625F, 5.75F);

		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.625F, 5.75F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 4.125F, 5.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4.125F, 6.5F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.625F, 6.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.5F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.125F, 5.75F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 3.625F, 5.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.5F);

		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.5F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 3.625F, 5.75F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.125F, 5.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.5F);

		//leaf1
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.75F, 0.75F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 4.375F, 0.75F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 4.375F, 0F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.75F, 0F);

		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.75F, 0F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 4.375F, 0F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 4.375F, 0.75F);
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.75F, 0.75F);

		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.125F, 0.75F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.125F, 0F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 3.75F, 0F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 3.75F, 0.75F);

		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 3.75F, 0.75F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 3.75F, 0F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.125F, 0F);
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.125F, 0.75F);

		//leaf3
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 3.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 3.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 4.375F, 2.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.75F, 2.75F);

		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.75F, 2.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 4.375F, 2.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 3.75F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 3.75F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 3.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.125F, 2.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 3.75F, 2.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 3.75F, 3.75F);

		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 3.75F, 3.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 3.75F, 2.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.125F, 2.75F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 3.75F);

		//leaf8
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.875F, 11.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.375F, 11.75F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 1.375F, 11F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.875F, 11F);

		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.875F, 11F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 1.375F, 11F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.375F, 11.75F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.875F, 11.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 11.75F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.375F, 11F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 0.875F, 11F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 11.75F);

		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 11.75F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 0.875F, 11F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.375F, 11F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 11.75F);

		//leaf5
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.375F, 4F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9F, 4F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 9F, 3F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 8.375F, 3F);

		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 8.375F, 3F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 9F, 3F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9F, 4F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.375F, 4F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 4F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 7.75F, 3F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 8.375F, 3F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.375F, 4F);

		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.375F, 4F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 8.375F, 3F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 7.75F, 3F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 4F);

		//leaf4b
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);

		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 7.625F, 5.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 7.625F, 5.5F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);

		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 7.625F, 5.5F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 7.625F, 5.25F);

		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.625F, 5.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 4.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 6.625F, 4.25F);

		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 6.625F, 4.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 4.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 7.125F, 5.25F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.625F, 5.25F);

		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);

		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4.25F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4.25F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);

		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);

		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 5.625F, 5.5F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 5.25F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 5.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 5.625F, 5.5F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		//leaf1b
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);

		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 5.375F, 2.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 5.375F, 2.5F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);

		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 5.375F, 2.5F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 5.375F, 2.25F);

		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 4.25F, 2.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.875F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.875F, 1.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 4.25F, 1.25F);

		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 4.25F, 1.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.875F, 1.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.875F, 2.25F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 4.25F, 2.25F);

		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.625F, 1.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.25F, 1.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);

		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.25F, 1.25F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.625F, 1.25F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);

		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);

		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 3.125F, 2.5F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.125F, 2.25F);

		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.125F, 2.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 3.125F, 2.5F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		//leaf2b
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);

		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 8.25F, 2.5F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 8.25F, 2.75F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);

		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 8.25F, 2.75F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 8.25F, 2.5F);

		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.875F, 2.5F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 7.625F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.625F, 1.25F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 6.875F, 1.25F);

		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 6.875F, 1.25F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.625F, 1.25F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 7.625F, 2.5F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.875F, 2.5F);

		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.125F, 1.25F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.875F, 1.25F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);

		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.875F, 1.25F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.125F, 1.25F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);

		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);

		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 5.5F, 2.75F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 2.5F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 2.5F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 5.5F, 2.75F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		//leaf10
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.625F, 8.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4.125F, 8.75F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 4.125F, 8F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.625F, 8F);

		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.625F, 8F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 4.125F, 8F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4.125F, 8.75F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.625F, 8.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 8.75F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.125F, 8F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 3.625F, 8F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 8.75F);

		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 8.75F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 3.625F, 8F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.125F, 8F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 8.75F);

		//leaf4
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.625F, 3F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 6.125F, 3F);

		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 6.125F, 3F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.625F, 3F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 4F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 5.625F, 3F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.125F, 3F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.125F, 4F);

		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.125F, 4F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.125F, 3F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 5.625F, 3F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 4F);

		//leaf2
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.25F, 1F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7F, 1F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 7F, 0F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 6.25F, 0F);

		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 6.25F, 0F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 7F, 0F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7F, 1F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.25F, 1F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 1F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 5.5F, 0F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 6.25F, 0F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.25F, 1F);

		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.25F, 1F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 6.25F, 0F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 5.5F, 0F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 1F);

		//leaf3b
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);

		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 5.625F, 5.25F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 5.625F, 5.5F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);

		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 5.625F, 5.5F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 5.625F, 5.25F);

		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 4.375F, 5.25F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 4F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 4.375F, 4F);

		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 4.375F, 4F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 4F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 5F, 5.25F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 4.375F, 5.25F);

		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 4F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 4F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);

		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 4F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 4F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);

		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);

		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 3.125F, 5.5F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 5.25F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 5.25F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 3.125F, 5.5F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		//leaf7
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 1.125F, 9.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.75F, 9.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.75F, 8.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 1.125F, 8.25F);

		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 1.125F, 8.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.75F, 8.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.75F, 9.25F);
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 1.125F, 9.25F);

		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 0.5F, 9.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 0.5F, 8.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.125F, 8.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.125F, 9.25F);

		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.125F, 9.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.125F, 8.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 0.5F, 8.25F);
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 0.5F, 9.25F);

		//leaf10b
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);

		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 4.5F, 9.75F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 4.5F, 10F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);

		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 4.5F, 10F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 4.5F, 9.75F);

		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.625F, 9.75F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 4.125F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4.125F, 9F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 3.625F, 9F);

		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 3.625F, 9F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4.125F, 9F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 4.125F, 9.75F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.625F, 9.75F);

		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 9F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 9F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);

		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 9F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 9F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);

		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);

		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 2.75F, 10F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 2.75F, 9.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 2.75F, 9.75F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 2.75F, 10F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		//leaf5b
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);

		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 10F, 5.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 10F, 5.5F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);

		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 10F, 5.5F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 10F, 5.25F);

		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.875F, 5.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 9.5F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.5F, 4.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 8.875F, 4.25F);

		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 8.875F, 4.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.5F, 4.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 9.5F, 5.25F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.875F, 5.25F);

		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.25F, 4.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.875F, 4.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);

		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.875F, 4.25F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.25F, 4.25F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);

		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);

		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 7.75F, 5.5F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 5.25F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 5.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 7.75F, 5.5F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		//leaf6b
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);

		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 2F, 7.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 2F, 8F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);

		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 2F, 8F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 2F, 7.75F);

		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 1F, 7.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 6.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 1F, 6.75F);

		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 1F, 6.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 6.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1.5F, 7.75F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 1F, 7.75F);

		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.5F, 6.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1F, 6.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);

		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1F, 6.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.5F, 6.75F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);

		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);

		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 0F, 8F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0F, 7.75F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0F, 7.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 0F, 8F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		//crop1
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);

		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.6875F, 3F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 3F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 3F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 3F, 1.5F);

		builder.addVertex(0.3125F, 0F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 0F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 1.5F, 0F);

		builder.addVertex(0.3125F, 0F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 0F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 2.25F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0.75F, 0F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0.75F, 0F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);

		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 0F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.6875F, 0F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 0F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
	}

	public static void buildStage4Fruits(QuadBuilder builder) {
		//fruit1
		builder.addVertex(1.082311F, 0.299627F, 0.740168F, 10.375F, 0.75F);
		builder.addVertex(0.996053F, 0.299627F, 0.906649F, 10.75F, 0.75F);
		builder.addVertex(0.955868F, 0.053757F, 0.885828F, 10.75F, 1.75F);
		builder.addVertex(1.042126F, 0.053757F, 0.719347F, 10.375F, 1.75F);

		builder.addVertex(0.918582F, 0.333571F, 0.655335F, 11.5F, 0.75F);
		builder.addVertex(0.878397F, 0.087701F, 0.634514F, 11.5F, 1.75F);
		builder.addVertex(0.792138F, 0.087701F, 0.800994F, 11.125F, 1.75F);
		builder.addVertex(0.832323F, 0.333571F, 0.821815F, 11.125F, 0.75F);

		builder.addVertex(1.042126F, 0.053757F, 0.719347F, 10.75F, 0.75F);
		builder.addVertex(0.955868F, 0.053757F, 0.885828F, 11.125F, 0.75F);
		builder.addVertex(0.792138F, 0.087701F, 0.800994F, 11.125F, 0F);
		builder.addVertex(0.878397F, 0.087701F, 0.634514F, 10.75F, 0F);

		builder.addVertex(1.082311F, 0.299627F, 0.740168F, 10.375F, 0.75F);
		builder.addVertex(0.918582F, 0.333571F, 0.655335F, 10.375F, 0F);
		builder.addVertex(0.832323F, 0.333571F, 0.821815F, 10.75F, 0F);
		builder.addVertex(0.996053F, 0.299627F, 0.906649F, 10.75F, 0.75F);

		builder.addVertex(0.996053F, 0.299627F, 0.906649F, 10.75F, 0.75F);
		builder.addVertex(0.832323F, 0.333571F, 0.821815F, 11.125F, 0.75F);
		builder.addVertex(0.792138F, 0.087701F, 0.800994F, 11.125F, 1.75F);
		builder.addVertex(0.955868F, 0.053757F, 0.885828F, 10.75F, 1.75F);

		builder.addVertex(1.082311F, 0.299627F, 0.740168F, 10.375F, 0.75F);
		builder.addVertex(1.042126F, 0.053757F, 0.719347F, 10.375F, 1.75F);
		builder.addVertex(0.878397F, 0.087701F, 0.634514F, 10F, 1.75F);
		builder.addVertex(0.918582F, 0.333571F, 0.655335F, 10F, 0.75F);

		//fruit2
		builder.addVertex(0.218247F, 0.215741F, 1.05748F, 10.375F, 2.75F);
		builder.addVertex(0.062637F, 0.215741F, 0.952875F, 10.75F, 2.75F);
		builder.addVertex(0.100262F, -0.024991F, 0.896906F, 10.75F, 3.75F);
		builder.addVertex(0.255871F, -0.024991F, 1.00151F, 10.375F, 3.75F);

		builder.addVertex(0.318973F, 0.266321F, 0.90764F, 11.5F, 2.75F);
		builder.addVertex(0.356597F, 0.025589F, 0.85167F, 11.5F, 3.75F);
		builder.addVertex(0.200988F, 0.025589F, 0.747066F, 11.125F, 3.75F);
		builder.addVertex(0.163364F, 0.266321F, 0.803035F, 11.125F, 2.75F);

		builder.addVertex(0.255871F, -0.024991F, 1.00151F, 10.75F, 2.75F);
		builder.addVertex(0.100262F, -0.024991F, 0.896906F, 11.125F, 2.75F);
		builder.addVertex(0.200988F, 0.025589F, 0.747066F, 11.125F, 2F);
		builder.addVertex(0.356597F, 0.025589F, 0.85167F, 10.75F, 2F);

		builder.addVertex(0.218247F, 0.215741F, 1.05748F, 10.375F, 2.75F);
		builder.addVertex(0.318973F, 0.266321F, 0.90764F, 10.375F, 2F);
		builder.addVertex(0.163364F, 0.266321F, 0.803035F, 10.75F, 2F);
		builder.addVertex(0.062637F, 0.215741F, 0.952875F, 10.75F, 2.75F);

		builder.addVertex(0.062637F, 0.215741F, 0.952875F, 10.75F, 2.75F);
		builder.addVertex(0.163364F, 0.266321F, 0.803035F, 11.125F, 2.75F);
		builder.addVertex(0.200988F, 0.025589F, 0.747066F, 11.125F, 3.75F);
		builder.addVertex(0.100262F, -0.024991F, 0.896906F, 10.75F, 3.75F);

		builder.addVertex(0.218247F, 0.215741F, 1.05748F, 10.375F, 2.75F);
		builder.addVertex(0.255871F, -0.024991F, 1.00151F, 10.375F, 3.75F);
		builder.addVertex(0.356597F, 0.025589F, 0.85167F, 10F, 3.75F);
		builder.addVertex(0.318973F, 0.266321F, 0.90764F, 10F, 2.75F);

		//fruit3
		builder.addVertex(0.188613F, 0.124438F, -0.001423F, 10.375F, 4.75F);
		builder.addVertex(0.363811F, 0.124438F, -0.06822F, 10.75F, 4.75F);
		builder.addVertex(0.37995F, -0.121423F, -0.02589F, 10.75F, 5.75F);
		builder.addVertex(0.204752F, -0.121423F, 0.040906F, 10.375F, 5.75F);

		builder.addVertex(0.254304F, 0.158414F, 0.170875F, 11.5F, 4.75F);
		builder.addVertex(0.270442F, -0.087447F, 0.213204F, 11.5F, 5.75F);
		builder.addVertex(0.445641F, -0.087447F, 0.146408F, 11.125F, 5.75F);
		builder.addVertex(0.429502F, 0.158414F, 0.104078F, 11.125F, 4.75F);

		builder.addVertex(0.204752F, -0.121423F, 0.040906F, 10.75F, 4.75F);
		builder.addVertex(0.37995F, -0.121423F, -0.02589F, 11.125F, 4.75F);
		builder.addVertex(0.445641F, -0.087447F, 0.146408F, 11.125F, 4F);
		builder.addVertex(0.270442F, -0.087447F, 0.213204F, 10.75F, 4F);

		builder.addVertex(0.188613F, 0.124438F, -0.001423F, 10.375F, 4.75F);
		builder.addVertex(0.254304F, 0.158414F, 0.170875F, 10.375F, 4F);
		builder.addVertex(0.429502F, 0.158414F, 0.104078F, 10.75F, 4F);
		builder.addVertex(0.363811F, 0.124438F, -0.06822F, 10.75F, 4.75F);

		builder.addVertex(0.363811F, 0.124438F, -0.06822F, 10.75F, 4.75F);
		builder.addVertex(0.429502F, 0.158414F, 0.104078F, 11.125F, 4.75F);
		builder.addVertex(0.445641F, -0.087447F, 0.146408F, 11.125F, 5.75F);
		builder.addVertex(0.37995F, -0.121423F, -0.02589F, 10.75F, 5.75F);

		builder.addVertex(0.188613F, 0.124438F, -0.001423F, 10.375F, 4.75F);
		builder.addVertex(0.204752F, -0.121423F, 0.040906F, 10.375F, 5.75F);
		builder.addVertex(0.270442F, -0.087447F, 0.213204F, 10F, 5.75F);
		builder.addVertex(0.254304F, 0.158414F, 0.170875F, 10F, 4.75F);

		//fruit4
		builder.addVertex(0.456126F, 0.614174F, 1.104169F, 10.375F, 6.75F);
		builder.addVertex(0.271725F, 0.614174F, 1.070225F, 10.75F, 6.75F);
		builder.addVertex(0.289757F, 0.384874F, 0.972264F, 10.75F, 7.75F);
		builder.addVertex(0.474159F, 0.384874F, 1.006208F, 10.375F, 7.75F);

		builder.addVertex(0.48726F, 0.688879F, 0.935036F, 11.5F, 6.75F);
		builder.addVertex(0.505292F, 0.45958F, 0.837074F, 11.5F, 7.75F);
		builder.addVertex(0.32089F, 0.45958F, 0.803131F, 11.125F, 7.75F);
		builder.addVertex(0.302858F, 0.688879F, 0.901092F, 11.125F, 6.75F);

		builder.addVertex(0.474159F, 0.384874F, 1.006208F, 10.75F, 6.75F);
		builder.addVertex(0.289757F, 0.384874F, 0.972264F, 11.125F, 6.75F);
		builder.addVertex(0.32089F, 0.45958F, 0.803131F, 11.125F, 6F);
		builder.addVertex(0.505292F, 0.45958F, 0.837074F, 10.75F, 6F);

		builder.addVertex(0.456126F, 0.614174F, 1.104169F, 10.375F, 6.75F);
		builder.addVertex(0.48726F, 0.688879F, 0.935036F, 10.375F, 6F);
		builder.addVertex(0.302858F, 0.688879F, 0.901092F, 10.75F, 6F);
		builder.addVertex(0.271725F, 0.614174F, 1.070225F, 10.75F, 6.75F);

		builder.addVertex(0.271725F, 0.614174F, 1.070225F, 10.75F, 6.75F);
		builder.addVertex(0.302858F, 0.688879F, 0.901092F, 11.125F, 6.75F);
		builder.addVertex(0.32089F, 0.45958F, 0.803131F, 11.125F, 7.75F);
		builder.addVertex(0.289757F, 0.384874F, 0.972264F, 10.75F, 7.75F);

		builder.addVertex(0.456126F, 0.614174F, 1.104169F, 10.375F, 6.75F);
		builder.addVertex(0.474159F, 0.384874F, 1.006208F, 10.375F, 7.75F);
		builder.addVertex(0.505292F, 0.45958F, 0.837074F, 10F, 7.75F);
		builder.addVertex(0.48726F, 0.688879F, 0.935036F, 10F, 6.75F);

		//fruit5
		builder.addVertex(0.989213F, 0.895962F, 0.878556F, 10.375F, 8.75F);
		builder.addVertex(0.843766F, 0.895962F, 0.996884F, 10.75F, 8.75F);
		builder.addVertex(0.808141F, 0.652418F, 0.953095F, 10.75F, 9.75F);
		builder.addVertex(0.953588F, 0.652418F, 0.834767F, 10.375F, 9.75F);

		builder.addVertex(0.873941F, 0.938299F, 0.736866F, 11.5F, 8.75F);
		builder.addVertex(0.838316F, 0.694756F, 0.693077F, 11.5F, 9.75F);
		builder.addVertex(0.692869F, 0.694756F, 0.811404F, 11.125F, 9.75F);
		builder.addVertex(0.728494F, 0.938299F, 0.855194F, 11.125F, 8.75F);

		builder.addVertex(0.953588F, 0.652418F, 0.834767F, 10.75F, 8.75F);
		builder.addVertex(0.808141F, 0.652418F, 0.953095F, 11.125F, 8.75F);
		builder.addVertex(0.692869F, 0.694756F, 0.811404F, 11.125F, 8F);
		builder.addVertex(0.838316F, 0.694756F, 0.693077F, 10.75F, 8F);

		builder.addVertex(0.989213F, 0.895962F, 0.878556F, 10.375F, 8.75F);
		builder.addVertex(0.873941F, 0.938299F, 0.736866F, 10.375F, 8F);
		builder.addVertex(0.728494F, 0.938299F, 0.855194F, 10.75F, 8F);
		builder.addVertex(0.843766F, 0.895962F, 0.996884F, 10.75F, 8.75F);

		builder.addVertex(0.843766F, 0.895962F, 0.996884F, 10.75F, 8.75F);
		builder.addVertex(0.728494F, 0.938299F, 0.855194F, 11.125F, 8.75F);
		builder.addVertex(0.692869F, 0.694756F, 0.811404F, 11.125F, 9.75F);
		builder.addVertex(0.808141F, 0.652418F, 0.953095F, 10.75F, 9.75F);

		builder.addVertex(0.989213F, 0.895962F, 0.878556F, 10.375F, 8.75F);
		builder.addVertex(0.953588F, 0.652418F, 0.834767F, 10.375F, 9.75F);
		builder.addVertex(0.838316F, 0.694756F, 0.693077F, 10F, 9.75F);
		builder.addVertex(0.873941F, 0.938299F, 0.736866F, 10F, 8.75F);

		//fruit6
		builder.addVertex(-0.106278F, 0.422563F, 0.745283F, 10.375F, 10.75F);
		builder.addVertex(-0.144429F, 0.422563F, 0.561705F, 10.75F, 10.75F);
		builder.addVertex(-0.078399F, 0.181831F, 0.547983F, 10.75F, 11.75F);
		builder.addVertex(-0.040249F, 0.181831F, 0.731561F, 10.375F, 11.75F);

		builder.addVertex(0.070494F, 0.473143F, 0.708546F, 11.5F, 10.75F);
		builder.addVertex(0.136523F, 0.232411F, 0.694824F, 11.5F, 11.75F);
		builder.addVertex(0.098373F, 0.232411F, 0.511247F, 11.125F, 11.75F);
		builder.addVertex(0.032343F, 0.473143F, 0.524969F, 11.125F, 10.75F);

		builder.addVertex(-0.040249F, 0.181831F, 0.731561F, 10.75F, 10.75F);
		builder.addVertex(-0.078399F, 0.181831F, 0.547983F, 11.125F, 10.75F);
		builder.addVertex(0.098373F, 0.232411F, 0.511247F, 11.125F, 10F);
		builder.addVertex(0.136523F, 0.232411F, 0.694824F, 10.75F, 10F);

		builder.addVertex(-0.106278F, 0.422563F, 0.745283F, 10.375F, 10.75F);
		builder.addVertex(0.070494F, 0.473143F, 0.708546F, 10.375F, 10F);
		builder.addVertex(0.032343F, 0.473143F, 0.524969F, 10.75F, 10F);
		builder.addVertex(-0.144429F, 0.422563F, 0.561705F, 10.75F, 10.75F);

		builder.addVertex(-0.144429F, 0.422563F, 0.561705F, 10.75F, 10.75F);
		builder.addVertex(0.032343F, 0.473143F, 0.524969F, 11.125F, 10.75F);
		builder.addVertex(0.098373F, 0.232411F, 0.511247F, 11.125F, 11.75F);
		builder.addVertex(-0.078399F, 0.181831F, 0.547983F, 10.75F, 11.75F);

		builder.addVertex(-0.106278F, 0.422563F, 0.745283F, 10.375F, 10.75F);
		builder.addVertex(-0.040249F, 0.181831F, 0.731561F, 10.375F, 11.75F);
		builder.addVertex(0.136523F, 0.232411F, 0.694824F, 10F, 11.75F);
		builder.addVertex(0.070494F, 0.473143F, 0.708546F, 10F, 10.75F);
	}

	public static void buildStage4FruitAspects(QuadBuilder builder) {
		//fruit1aspect
		builder.addVertex(1.035623F, 0.27455F, 0.751173F, 12.75F, 0.5F);
		builder.addVertex(0.978118F, 0.27455F, 0.86216F, 13F, 0.5F);
		builder.addVertex(0.947979F, 0.090148F, 0.846545F, 13F, 1.25F);
		builder.addVertex(1.005485F, 0.090148F, 0.735558F, 12.75F, 1.25F);

		builder.addVertex(0.92647F, 0.29718F, 0.694618F, 13.5F, 0.5F);
		builder.addVertex(0.896332F, 0.112778F, 0.679002F, 13.5F, 1.25F);
		builder.addVertex(0.838826F, 0.112778F, 0.789989F, 13.25F, 1.25F);
		builder.addVertex(0.868965F, 0.29718F, 0.805605F, 13.25F, 0.5F);

		builder.addVertex(1.005485F, 0.090148F, 0.735558F, 13F, 0.5F);
		builder.addVertex(0.947979F, 0.090148F, 0.846545F, 13.25F, 0.5F);
		builder.addVertex(0.838826F, 0.112778F, 0.789989F, 13.25F, 0F);
		builder.addVertex(0.896332F, 0.112778F, 0.679002F, 13F, 0F);

		builder.addVertex(1.035623F, 0.27455F, 0.751173F, 12.75F, 0.5F);
		builder.addVertex(0.92647F, 0.29718F, 0.694618F, 12.75F, 0F);
		builder.addVertex(0.868965F, 0.29718F, 0.805605F, 13F, 0F);
		builder.addVertex(0.978118F, 0.27455F, 0.86216F, 13F, 0.5F);

		builder.addVertex(0.978118F, 0.27455F, 0.86216F, 13F, 0.5F);
		builder.addVertex(0.868965F, 0.29718F, 0.805605F, 13.25F, 0.5F);
		builder.addVertex(0.838826F, 0.112778F, 0.789989F, 13.25F, 1.25F);
		builder.addVertex(0.947979F, 0.090148F, 0.846545F, 13F, 1.25F);

		builder.addVertex(1.035623F, 0.27455F, 0.751173F, 12.75F, 0.5F);
		builder.addVertex(1.005485F, 0.090148F, 0.735558F, 12.75F, 1.25F);
		builder.addVertex(0.896332F, 0.112778F, 0.679002F, 12.5F, 1.25F);
		builder.addVertex(0.92647F, 0.29718F, 0.694618F, 12.5F, 0.5F);

		//fruit2aspect
		builder.addVertex(0.213802F, 0.194079F, 1.008076F, 12.75F, 0.5F);
		builder.addVertex(0.110063F, 0.194079F, 0.93834F, 13F, 0.5F);
		builder.addVertex(0.138281F, 0.01353F, 0.896363F, 13F, 1.25F);
		builder.addVertex(0.242021F, 0.01353F, 0.966099F, 12.75F, 1.25F);

		builder.addVertex(0.280953F, 0.227799F, 0.908183F, 13.5F, 0.5F);
		builder.addVertex(0.309171F, 0.04725F, 0.866206F, 13.5F, 1.25F);
		builder.addVertex(0.205432F, 0.04725F, 0.796469F, 13.25F, 1.25F);
		builder.addVertex(0.177214F, 0.227799F, 0.838446F, 13.25F, 0.5F);

		builder.addVertex(0.242021F, 0.01353F, 0.966099F, 13F, 0.5F);
		builder.addVertex(0.138281F, 0.01353F, 0.896363F, 13.25F, 0.5F);
		builder.addVertex(0.205432F, 0.04725F, 0.796469F, 13.25F, 0F);
		builder.addVertex(0.309171F, 0.04725F, 0.866206F, 13F, 0F);

		builder.addVertex(0.213802F, 0.194079F, 1.008076F, 12.75F, 0.5F);
		builder.addVertex(0.280953F, 0.227799F, 0.908183F, 12.75F, 0F);
		builder.addVertex(0.177214F, 0.227799F, 0.838446F, 13F, 0F);
		builder.addVertex(0.110063F, 0.194079F, 0.93834F, 13F, 0.5F);

		builder.addVertex(0.110063F, 0.194079F, 0.93834F, 13F, 0.5F);
		builder.addVertex(0.177214F, 0.227799F, 0.838446F, 13.25F, 0.5F);
		builder.addVertex(0.205432F, 0.04725F, 0.796469F, 13.25F, 1.25F);
		builder.addVertex(0.138281F, 0.01353F, 0.896363F, 13F, 1.25F);

		builder.addVertex(0.213802F, 0.194079F, 1.008076F, 12.75F, 0.5F);
		builder.addVertex(0.242021F, 0.01353F, 0.966099F, 12.75F, 1.25F);
		builder.addVertex(0.309171F, 0.04725F, 0.866206F, 12.5F, 1.25F);
		builder.addVertex(0.280953F, 0.227799F, 0.908183F, 12.5F, 0.5F);

		//fruit3aspect
		builder.addVertex(0.230778F, 0.099368F, 0.021452F, 12.75F, 0.5F);
		builder.addVertex(0.347577F, 0.099368F, -0.023079F, 13F, 0.5F);
		builder.addVertex(0.359681F, -0.085028F, 0.008668F, 13F, 1.25F);
		builder.addVertex(0.242882F, -0.085028F, 0.053199F, 12.75F, 1.25F);

		builder.addVertex(0.274572F, 0.122019F, 0.136317F, 13.5F, 0.5F);
		builder.addVertex(0.286676F, -0.062377F, 0.168064F, 13.5F, 1.25F);
		builder.addVertex(0.403475F, -0.062377F, 0.123533F, 13.25F, 1.25F);
		builder.addVertex(0.391371F, 0.122019F, 0.091786F, 13.25F, 0.5F);

		builder.addVertex(0.242882F, -0.085028F, 0.053199F, 13F, 0.5F);
		builder.addVertex(0.359681F, -0.085028F, 0.008668F, 13.25F, 0.5F);
		builder.addVertex(0.403475F, -0.062377F, 0.123533F, 13.25F, 0F);
		builder.addVertex(0.286676F, -0.062377F, 0.168064F, 13F, 0F);

		builder.addVertex(0.230778F, 0.099368F, 0.021452F, 12.75F, 0.5F);
		builder.addVertex(0.274572F, 0.122019F, 0.136317F, 12.75F, 0F);
		builder.addVertex(0.391371F, 0.122019F, 0.091786F, 13F, 0F);
		builder.addVertex(0.347577F, 0.099368F, -0.023079F, 13F, 0.5F);

		builder.addVertex(0.347577F, 0.099368F, -0.023079F, 13F, 0.5F);
		builder.addVertex(0.391371F, 0.122019F, 0.091786F, 13.25F, 0.5F);
		builder.addVertex(0.403475F, -0.062377F, 0.123533F, 13.25F, 1.25F);
		builder.addVertex(0.359681F, -0.085028F, 0.008668F, 13F, 1.25F);

		builder.addVertex(0.230778F, 0.099368F, 0.021452F, 12.75F, 0.5F);
		builder.addVertex(0.242882F, -0.085028F, 0.053199F, 12.75F, 1.25F);
		builder.addVertex(0.286676F, -0.062377F, 0.168064F, 12.5F, 1.25F);
		builder.addVertex(0.274572F, 0.122019F, 0.136317F, 12.5F, 0.5F);

		//fruit4aspect
		builder.addVertex(0.432836F, 0.597962F, 1.058078F, 12.75F, 0.5F);
		builder.addVertex(0.309901F, 0.597962F, 1.035449F, 13F, 0.5F);
		builder.addVertex(0.323425F, 0.425988F, 0.961977F, 13F, 1.25F);
		builder.addVertex(0.44636F, 0.425988F, 0.984607F, 12.75F, 1.25F);

		builder.addVertex(0.453591F, 0.647766F, 0.945322F, 13.5F, 0.5F);
		builder.addVertex(0.467115F, 0.475791F, 0.871851F, 13.5F, 1.25F);
		builder.addVertex(0.344181F, 0.475791F, 0.849222F, 13.25F, 1.25F);
		builder.addVertex(0.330657F, 0.647766F, 0.922693F, 13.25F, 0.5F);

		builder.addVertex(0.44636F, 0.425988F, 0.984607F, 13F, 0.5F);
		builder.addVertex(0.323425F, 0.425988F, 0.961977F, 13.25F, 0.5F);
		builder.addVertex(0.344181F, 0.475791F, 0.849222F, 13.25F, 0F);
		builder.addVertex(0.467115F, 0.475791F, 0.871851F, 13F, 0F);

		builder.addVertex(0.432836F, 0.597962F, 1.058078F, 12.75F, 0.5F);
		builder.addVertex(0.453591F, 0.647766F, 0.945322F, 12.75F, 0F);
		builder.addVertex(0.330657F, 0.647766F, 0.922693F, 13F, 0F);
		builder.addVertex(0.309901F, 0.597962F, 1.035449F, 13F, 0.5F);

		builder.addVertex(0.309901F, 0.597962F, 1.035449F, 13F, 0.5F);
		builder.addVertex(0.330657F, 0.647766F, 0.922693F, 13.25F, 0.5F);
		builder.addVertex(0.344181F, 0.475791F, 0.849222F, 13.25F, 1.25F);
		builder.addVertex(0.323425F, 0.425988F, 0.961977F, 13F, 1.25F);

		builder.addVertex(0.432836F, 0.597962F, 1.058078F, 12.75F, 0.5F);
		builder.addVertex(0.44636F, 0.425988F, 0.984607F, 12.75F, 1.25F);
		builder.addVertex(0.467115F, 0.475791F, 0.871851F, 12.5F, 1.25F);
		builder.addVertex(0.453591F, 0.647766F, 0.945322F, 12.5F, 0.5F);

		//fruit5aspect
		builder.addVertex(0.941307F, 0.872575F, 0.869189F, 12.75F, 0.5F);
		builder.addVertex(0.844342F, 0.872575F, 0.948074F, 13F, 0.5F);
		builder.addVertex(0.817624F, 0.689918F, 0.915232F, 13F, 1.25F);
		builder.addVertex(0.914588F, 0.689918F, 0.836347F, 12.75F, 1.25F);

		builder.addVertex(0.864459F, 0.9008F, 0.774729F, 13.5F, 0.5F);
		builder.addVertex(0.83774F, 0.718143F, 0.741887F, 13.5F, 1.25F);
		builder.addVertex(0.740776F, 0.718143F, 0.820772F, 13.25F, 1.25F);
		builder.addVertex(0.767494F, 0.9008F, 0.853614F, 13.25F, 0.5F);

		builder.addVertex(0.914588F, 0.689918F, 0.836347F, 13F, 0.5F);
		builder.addVertex(0.817624F, 0.689918F, 0.915232F, 13.25F, 0.5F);
		builder.addVertex(0.740776F, 0.718143F, 0.820772F, 13.25F, 0F);
		builder.addVertex(0.83774F, 0.718143F, 0.741887F, 13F, 0F);

		builder.addVertex(0.941307F, 0.872575F, 0.869189F, 12.75F, 0.5F);
		builder.addVertex(0.864459F, 0.9008F, 0.774729F, 12.75F, 0F);
		builder.addVertex(0.767494F, 0.9008F, 0.853614F, 13F, 0F);
		builder.addVertex(0.844342F, 0.872575F, 0.948074F, 13F, 0.5F);

		builder.addVertex(0.844342F, 0.872575F, 0.948074F, 13F, 0.5F);
		builder.addVertex(0.767494F, 0.9008F, 0.853614F, 13.25F, 0.5F);
		builder.addVertex(0.740776F, 0.718143F, 0.820772F, 13.25F, 1.25F);
		builder.addVertex(0.817624F, 0.689918F, 0.915232F, 13F, 1.25F);

		builder.addVertex(0.941307F, 0.872575F, 0.869189F, 12.75F, 0.5F);
		builder.addVertex(0.914588F, 0.689918F, 0.836347F, 12.75F, 1.25F);
		builder.addVertex(0.83774F, 0.718143F, 0.741887F, 12.5F, 1.25F);
		builder.addVertex(0.864459F, 0.9008F, 0.774729F, 12.5F, 0.5F);

		//fruit6aspect
		builder.addVertex(-0.074921F, 0.400902F, 0.706848F, 12.75F, 0.5F);
		builder.addVertex(-0.100355F, 0.400902F, 0.584463F, 13F, 0.5F);
		builder.addVertex(-0.050833F, 0.220353F, 0.574172F, 13F, 1.25F);
		builder.addVertex(-0.025399F, 0.220353F, 0.696557F, 12.75F, 1.25F);

		builder.addVertex(0.042927F, 0.434622F, 0.682358F, 13.5F, 0.5F);
		builder.addVertex(0.092449F, 0.254073F, 0.672066F, 13.5F, 1.25F);
		builder.addVertex(0.067015F, 0.254073F, 0.549681F, 13.25F, 1.25F);
		builder.addVertex(0.017493F, 0.434622F, 0.559972F, 13.25F, 0.5F);

		builder.addVertex(-0.025399F, 0.220353F, 0.696557F, 13F, 0.5F);
		builder.addVertex(-0.050833F, 0.220353F, 0.574172F, 13.25F, 0.5F);
		builder.addVertex(0.067015F, 0.254073F, 0.549681F, 13.25F, 0F);
		builder.addVertex(0.092449F, 0.254073F, 0.672066F, 13F, 0F);

		builder.addVertex(-0.074921F, 0.400902F, 0.706848F, 12.75F, 0.5F);
		builder.addVertex(0.042927F, 0.434622F, 0.682358F, 12.75F, 0F);
		builder.addVertex(0.017493F, 0.434622F, 0.559972F, 13F, 0F);
		builder.addVertex(-0.100355F, 0.400902F, 0.584463F, 13F, 0.5F);

		builder.addVertex(-0.100355F, 0.400902F, 0.584463F, 13F, 0.5F);
		builder.addVertex(0.017493F, 0.434622F, 0.559972F, 13.25F, 0.5F);
		builder.addVertex(0.067015F, 0.254073F, 0.549681F, 13.25F, 1.25F);
		builder.addVertex(-0.050833F, 0.220353F, 0.574172F, 13F, 1.25F);

		builder.addVertex(-0.074921F, 0.400902F, 0.706848F, 12.75F, 0.5F);
		builder.addVertex(-0.025399F, 0.220353F, 0.696557F, 12.75F, 1.25F);
		builder.addVertex(0.092449F, 0.254073F, 0.672066F, 12.5F, 1.25F);
		builder.addVertex(0.042927F, 0.434622F, 0.682358F, 12.5F, 0.5F);
	}

	public static void buildStage3(QuadBuilder builder) {
		//crop1
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);

		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.6875F, 3F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 3F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 3F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 3F, 1.5F);

		builder.addVertex(0.3125F, 0F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 0F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 1.5F, 0F);

		builder.addVertex(0.3125F, 0F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 0F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 2.25F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0.75F, 0F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0.75F, 0F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);

		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 0F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.6875F, 0F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 0F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		//leaf10b
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);

		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 4.5F, 9.75F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 4.5F, 10F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);

		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 4.5F, 10F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 4.5F, 9.75F);

		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.625F, 9.75F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 4.125F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4.125F, 9F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 3.625F, 9F);

		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 3.625F, 9F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4.125F, 9F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 4.125F, 9.75F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.625F, 9.75F);

		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 9F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 9F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);

		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 9F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 9F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);

		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);

		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 2.75F, 10F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 2.75F, 9.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 2.75F, 9.75F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 2.75F, 10F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		//leaf1
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.75F, 0.75F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 4.375F, 0.75F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 4.375F, 0F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.75F, 0F);

		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.75F, 0F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 4.375F, 0F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 4.375F, 0.75F);
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.75F, 0.75F);

		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.125F, 0.75F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.125F, 0F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 3.75F, 0F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 3.75F, 0.75F);

		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 3.75F, 0.75F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 3.75F, 0F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.125F, 0F);
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.125F, 0.75F);

		//leaf5b
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);

		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 10F, 5.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 10F, 5.5F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);

		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 10F, 5.5F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 10F, 5.25F);

		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.875F, 5.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 9.5F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.5F, 4.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 8.875F, 4.25F);

		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 8.875F, 4.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.5F, 4.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 9.5F, 5.25F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.875F, 5.25F);

		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.25F, 4.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.875F, 4.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);

		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.875F, 4.25F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.25F, 4.25F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);

		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);

		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 7.75F, 5.5F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 5.25F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 5.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 7.75F, 5.5F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		//leaf6
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.875F, 6.5F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.375F, 6.5F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 1.375F, 5.75F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.875F, 5.75F);

		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.875F, 5.75F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 1.375F, 5.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.375F, 6.5F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.875F, 6.5F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.375F, 6.5F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.375F, 5.75F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 0.875F, 5.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 0.875F, 6.5F);

		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 0.875F, 6.5F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 0.875F, 5.75F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.375F, 5.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.375F, 6.5F);

		//leaf9
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.625F, 6.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4.125F, 6.5F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 4.125F, 5.75F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.625F, 5.75F);

		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.625F, 5.75F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 4.125F, 5.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4.125F, 6.5F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.625F, 6.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.5F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.125F, 5.75F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 3.625F, 5.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.5F);

		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.5F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 3.625F, 5.75F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.125F, 5.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.5F);

		//leaf7
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 1.125F, 9.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.75F, 9.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.75F, 8.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 1.125F, 8.25F);

		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 1.125F, 8.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.75F, 8.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.75F, 9.25F);
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 1.125F, 9.25F);

		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 0.5F, 9.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 0.5F, 8.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.125F, 8.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.125F, 9.25F);

		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.125F, 9.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.125F, 8.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 0.5F, 8.25F);
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 0.5F, 9.25F);

		//leaf3b
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);

		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 5.625F, 5.25F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 5.625F, 5.5F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);

		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 5.625F, 5.5F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 5.625F, 5.25F);

		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 4.375F, 5.25F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 4F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 4.375F, 4F);

		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 4.375F, 4F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 4F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 5F, 5.25F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 4.375F, 5.25F);

		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 4F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 4F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);

		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 4F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 4F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);

		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);

		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 3.125F, 5.5F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 5.25F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 5.25F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 3.125F, 5.5F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		//leaf5
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.375F, 4F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9F, 4F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 9F, 3F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 8.375F, 3F);

		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 8.375F, 3F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 9F, 3F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9F, 4F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.375F, 4F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 4F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 7.75F, 3F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 8.375F, 3F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.375F, 4F);

		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.375F, 4F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 8.375F, 3F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 7.75F, 3F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 4F);

		//leaf2
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.25F, 1F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7F, 1F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 7F, 0F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 6.25F, 0F);

		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 6.25F, 0F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 7F, 0F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7F, 1F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.25F, 1F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 1F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 5.5F, 0F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 6.25F, 0F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.25F, 1F);

		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.25F, 1F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 6.25F, 0F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 5.5F, 0F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 1F);

		//leaf4b
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);

		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 7.625F, 5.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 7.625F, 5.5F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);

		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 7.625F, 5.5F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 7.625F, 5.25F);

		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.625F, 5.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 4.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 6.625F, 4.25F);

		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 6.625F, 4.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 4.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 7.125F, 5.25F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.625F, 5.25F);

		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);

		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4.25F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4.25F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);

		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);

		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 5.625F, 5.5F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 5.25F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 5.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 5.625F, 5.5F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		//leaf11
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.75F, 11F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 11F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 4.375F, 10.25F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.75F, 10.25F);

		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.75F, 10.25F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 4.375F, 10.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 11F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.75F, 11F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.125F, 11F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.125F, 10.25F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 3.75F, 10.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.75F, 11F);

		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.75F, 11F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 3.75F, 10.25F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.125F, 10.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.125F, 11F);

		//leaf4
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.625F, 3F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 6.125F, 3F);

		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 6.125F, 3F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.625F, 3F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 4F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 5.625F, 3F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.125F, 3F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.125F, 4F);

		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.125F, 4F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.125F, 3F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 5.625F, 3F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 4F);

		//leaf6b
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);

		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 2F, 7.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 2F, 8F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);

		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 2F, 8F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 2F, 7.75F);

		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 1F, 7.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 6.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 1F, 6.75F);

		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 1F, 6.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 6.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1.5F, 7.75F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 1F, 7.75F);

		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.5F, 6.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1F, 6.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);

		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1F, 6.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.5F, 6.75F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);

		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);

		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 0F, 8F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0F, 7.75F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0F, 7.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 0F, 8F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		//leaf10
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.625F, 8.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4.125F, 8.75F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 4.125F, 8F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.625F, 8F);

		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.625F, 8F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 4.125F, 8F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4.125F, 8.75F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.625F, 8.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 8.75F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.125F, 8F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 3.625F, 8F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 8.75F);

		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 8.75F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 3.625F, 8F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.125F, 8F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 8.75F);

		//leaf7b
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);

		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 2.25F, 10.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 2.25F, 10.75F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);

		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 2.25F, 10.75F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 2.25F, 10.5F);

		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 1.125F, 10.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.75F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.75F, 9.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 1.125F, 9.5F);

		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 1.125F, 9.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.75F, 9.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.75F, 10.5F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 1.125F, 10.5F);

		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0.5F, 9.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.125F, 9.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);

		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.125F, 9.5F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0.5F, 9.5F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);

		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);

		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 0F, 10.75F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0F, 10.5F);

		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0F, 10.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 0F, 10.75F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		//leaf11b
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);

		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 5F, 12.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 5F, 12.5F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);

		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 5F, 12.5F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 5F, 12.25F);

		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.875F, 12.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 4.5F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.5F, 11.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 3.875F, 11.25F);

		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 3.875F, 11.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.5F, 11.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 4.5F, 12.25F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.875F, 12.25F);

		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.25F, 11.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.875F, 11.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);

		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.875F, 11.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.25F, 11.25F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);

		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);

		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 2.75F, 12.5F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 2.75F, 12.25F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 2.75F, 12.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 2.75F, 12.5F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		//leaf2b
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);

		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 8.25F, 2.5F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 8.25F, 2.75F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);

		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 8.25F, 2.75F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 8.25F, 2.5F);

		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.875F, 2.5F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 7.625F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.625F, 1.25F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 6.875F, 1.25F);

		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 6.875F, 1.25F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.625F, 1.25F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 7.625F, 2.5F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.875F, 2.5F);

		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.125F, 1.25F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.875F, 1.25F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);

		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.875F, 1.25F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.125F, 1.25F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);

		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);

		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 5.5F, 2.75F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 2.5F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 2.5F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 5.5F, 2.75F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		//leaf9b
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);

		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 4.5F, 7.5F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 4.5F, 7.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);

		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 4.5F, 7.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 4.5F, 7.5F);

		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.625F, 7.5F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 4.125F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4.125F, 6.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 3.625F, 6.75F);

		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 3.625F, 6.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4.125F, 6.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 4.125F, 7.5F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.625F, 7.5F);

		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.75F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);

		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.75F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);

		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);

		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 2.75F, 7.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 2.75F, 7.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 2.75F, 7.5F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 2.75F, 7.75F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		//leaf8
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.875F, 11.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.375F, 11.75F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 1.375F, 11F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.875F, 11F);

		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.875F, 11F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 1.375F, 11F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.375F, 11.75F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.875F, 11.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 11.75F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.375F, 11F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 0.875F, 11F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 11.75F);

		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 11.75F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 0.875F, 11F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.375F, 11F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 11.75F);

		//leaf1b
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);

		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 5.375F, 2.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 5.375F, 2.5F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);

		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 5.375F, 2.5F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 5.375F, 2.25F);

		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 4.25F, 2.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.875F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.875F, 1.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 4.25F, 1.25F);

		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 4.25F, 1.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.875F, 1.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.875F, 2.25F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 4.25F, 2.25F);

		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.625F, 1.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.25F, 1.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);

		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.25F, 1.25F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.625F, 1.25F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);

		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);

		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 3.125F, 2.5F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.125F, 2.25F);

		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.125F, 2.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 3.125F, 2.5F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		//leaf3
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 3.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 3.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 4.375F, 2.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.75F, 2.75F);

		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.75F, 2.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 4.375F, 2.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 3.75F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 3.75F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 3.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.125F, 2.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 3.75F, 2.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 3.75F, 3.75F);

		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 3.75F, 3.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 3.75F, 2.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.125F, 2.75F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 3.75F);

		//leaf8b
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);

		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 1.75F, 12.75F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 1.75F, 13F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);

		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 1.75F, 13F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 1.75F, 12.75F);

		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.875F, 12.75F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 1.375F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.375F, 12F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0.875F, 12F);

		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0.875F, 12F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.375F, 12F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 1.375F, 12.75F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.875F, 12.75F);

		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 12F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 12F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);

		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 12F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 12F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);

		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);

		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0F, 13F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0F, 12.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0F, 12.75F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0F, 13F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
	}

	public static void buildStage3Fruits(QuadBuilder builder) {
		//fruit1
		builder.addVertex(1.040646F, 0.305284F, 0.753776F, 10.25F, 0.5F);
		builder.addVertex(0.983141F, 0.305284F, 0.864763F, 10.5F, 0.5F);
		builder.addVertex(0.963048F, 0.182349F, 0.854352F, 10.5F, 1F);
		builder.addVertex(1.020554F, 0.182349F, 0.743365F, 10.25F, 1F);

		builder.addVertex(0.931493F, 0.327913F, 0.697221F, 11F, 0.5F);
		builder.addVertex(0.911401F, 0.204979F, 0.68681F, 11F, 1F);
		builder.addVertex(0.853895F, 0.204979F, 0.797797F, 10.75F, 1F);
		builder.addVertex(0.873988F, 0.327913F, 0.808207F, 10.75F, 0.5F);

		builder.addVertex(1.020554F, 0.182349F, 0.743365F, 10.5F, 0.5F);
		builder.addVertex(0.963048F, 0.182349F, 0.854352F, 10.75F, 0.5F);
		builder.addVertex(0.853895F, 0.204979F, 0.797797F, 10.75F, 0F);
		builder.addVertex(0.911401F, 0.204979F, 0.68681F, 10.5F, 0F);

		builder.addVertex(1.040646F, 0.305284F, 0.753776F, 10.25F, 0.5F);
		builder.addVertex(0.931493F, 0.327913F, 0.697221F, 10.25F, 0F);
		builder.addVertex(0.873988F, 0.327913F, 0.808207F, 10.5F, 0F);
		builder.addVertex(0.983141F, 0.305284F, 0.864763F, 10.5F, 0.5F);

		builder.addVertex(0.983141F, 0.305284F, 0.864763F, 10.5F, 0.5F);
		builder.addVertex(0.873988F, 0.327913F, 0.808207F, 10.75F, 0.5F);
		builder.addVertex(0.853895F, 0.204979F, 0.797797F, 10.75F, 1F);
		builder.addVertex(0.963048F, 0.182349F, 0.854352F, 10.5F, 1F);

		builder.addVertex(1.040646F, 0.305284F, 0.753776F, 10.25F, 0.5F);
		builder.addVertex(1.020554F, 0.182349F, 0.743365F, 10.25F, 1F);
		builder.addVertex(0.911401F, 0.204979F, 0.68681F, 10F, 1F);
		builder.addVertex(0.931493F, 0.327913F, 0.697221F, 10F, 0.5F);

		//fruit2
		builder.addVertex(0.209099F, 0.224171F, 1.015072F, 10.25F, 1.75F);
		builder.addVertex(0.10536F, 0.224171F, 0.945336F, 10.5F, 1.75F);
		builder.addVertex(0.124172F, 0.103805F, 0.917351F, 10.5F, 2.25F);
		builder.addVertex(0.227911F, 0.103805F, 0.987088F, 10.25F, 2.25F);

		builder.addVertex(0.27625F, 0.257891F, 0.915179F, 11F, 1.75F);
		builder.addVertex(0.295062F, 0.137525F, 0.887194F, 11F, 2.25F);
		builder.addVertex(0.191323F, 0.137525F, 0.817458F, 10.75F, 2.25F);
		builder.addVertex(0.172511F, 0.257891F, 0.845443F, 10.75F, 1.75F);

		builder.addVertex(0.227911F, 0.103805F, 0.987088F, 10.5F, 1.75F);
		builder.addVertex(0.124172F, 0.103805F, 0.917351F, 10.75F, 1.75F);
		builder.addVertex(0.191323F, 0.137525F, 0.817458F, 10.75F, 1.25F);
		builder.addVertex(0.295062F, 0.137525F, 0.887194F, 10.5F, 1.25F);

		builder.addVertex(0.209099F, 0.224171F, 1.015072F, 10.25F, 1.75F);
		builder.addVertex(0.27625F, 0.257891F, 0.915179F, 10.25F, 1.25F);
		builder.addVertex(0.172511F, 0.257891F, 0.845443F, 10.5F, 1.25F);
		builder.addVertex(0.10536F, 0.224171F, 0.945336F, 10.5F, 1.75F);

		builder.addVertex(0.10536F, 0.224171F, 0.945336F, 10.5F, 1.75F);
		builder.addVertex(0.172511F, 0.257891F, 0.845443F, 10.75F, 1.75F);
		builder.addVertex(0.191323F, 0.137525F, 0.817458F, 10.75F, 2.25F);
		builder.addVertex(0.124172F, 0.103805F, 0.917351F, 10.5F, 2.25F);

		builder.addVertex(0.209099F, 0.224171F, 1.015072F, 10.25F, 1.75F);
		builder.addVertex(0.227911F, 0.103805F, 0.987088F, 10.25F, 2.25F);
		builder.addVertex(0.295062F, 0.137525F, 0.887194F, 10F, 2.25F);
		builder.addVertex(0.27625F, 0.257891F, 0.915179F, 10F, 1.75F);

		//fruit3
		builder.addVertex(0.228761F, 0.130101F, 0.016161F, 10.25F, 3F);
		builder.addVertex(0.34556F, 0.130101F, -0.02837F, 10.5F, 3F);
		builder.addVertex(0.353629F, 0.00717F, -0.007206F, 10.5F, 3.5F);
		builder.addVertex(0.23683F, 0.00717F, 0.037325F, 10.25F, 3.5F);

		builder.addVertex(0.272555F, 0.152751F, 0.131026F, 11F, 3F);
		builder.addVertex(0.280624F, 0.029821F, 0.152191F, 11F, 3.5F);
		builder.addVertex(0.397423F, 0.029821F, 0.107659F, 10.75F, 3.5F);
		builder.addVertex(0.389354F, 0.152751F, 0.086495F, 10.75F, 3F);

		builder.addVertex(0.23683F, 0.00717F, 0.037325F, 10.5F, 3F);
		builder.addVertex(0.353629F, 0.00717F, -0.007206F, 10.75F, 3F);
		builder.addVertex(0.397423F, 0.029821F, 0.107659F, 10.75F, 2.5F);
		builder.addVertex(0.280624F, 0.029821F, 0.152191F, 10.5F, 2.5F);

		builder.addVertex(0.228761F, 0.130101F, 0.016161F, 10.25F, 3F);
		builder.addVertex(0.272555F, 0.152751F, 0.131026F, 10.25F, 2.5F);
		builder.addVertex(0.389354F, 0.152751F, 0.086495F, 10.5F, 2.5F);
		builder.addVertex(0.34556F, 0.130101F, -0.02837F, 10.5F, 3F);

		builder.addVertex(0.34556F, 0.130101F, -0.02837F, 10.5F, 3F);
		builder.addVertex(0.389354F, 0.152751F, 0.086495F, 10.75F, 3F);
		builder.addVertex(0.397423F, 0.029821F, 0.107659F, 10.75F, 3.5F);
		builder.addVertex(0.353629F, 0.00717F, -0.007206F, 10.5F, 3.5F);

		builder.addVertex(0.228761F, 0.130101F, 0.016161F, 10.25F, 3F);
		builder.addVertex(0.23683F, 0.00717F, 0.037325F, 10.25F, 3.5F);
		builder.addVertex(0.280624F, 0.029821F, 0.152191F, 10F, 3.5F);
		builder.addVertex(0.272555F, 0.152751F, 0.131026F, 10F, 3F);

		//fruit4
		builder.addVertex(0.430582F, 0.626625F, 1.070323F, 10.25F, 4.25F);
		builder.addVertex(0.307647F, 0.626625F, 1.047694F, 10.5F, 4.25F);
		builder.addVertex(0.316663F, 0.511975F, 0.998713F, 10.5F, 4.75F);
		builder.addVertex(0.439598F, 0.511975F, 1.021342F, 10.25F, 4.75F);

		builder.addVertex(0.451337F, 0.676429F, 0.957567F, 11F, 4.25F);
		builder.addVertex(0.460353F, 0.561779F, 0.908587F, 11F, 4.75F);
		builder.addVertex(0.337419F, 0.561779F, 0.885957F, 10.75F, 4.75F);
		builder.addVertex(0.328403F, 0.676429F, 0.934938F, 10.75F, 4.25F);

		builder.addVertex(0.439598F, 0.511975F, 1.021342F, 10.5F, 4.25F);
		builder.addVertex(0.316663F, 0.511975F, 0.998713F, 10.75F, 4.25F);
		builder.addVertex(0.337419F, 0.561779F, 0.885957F, 10.75F, 3.75F);
		builder.addVertex(0.460353F, 0.561779F, 0.908587F, 10.5F, 3.75F);

		builder.addVertex(0.430582F, 0.626625F, 1.070323F, 10.25F, 4.25F);
		builder.addVertex(0.451337F, 0.676429F, 0.957567F, 10.25F, 3.75F);
		builder.addVertex(0.328403F, 0.676429F, 0.934938F, 10.5F, 3.75F);
		builder.addVertex(0.307647F, 0.626625F, 1.047694F, 10.5F, 4.25F);

		builder.addVertex(0.307647F, 0.626625F, 1.047694F, 10.5F, 4.25F);
		builder.addVertex(0.328403F, 0.676429F, 0.934938F, 10.75F, 4.25F);
		builder.addVertex(0.337419F, 0.561779F, 0.885957F, 10.75F, 4.75F);
		builder.addVertex(0.316663F, 0.511975F, 0.998713F, 10.5F, 4.75F);

		builder.addVertex(0.430582F, 0.626625F, 1.070323F, 10.25F, 4.25F);
		builder.addVertex(0.439598F, 0.511975F, 1.021342F, 10.25F, 4.75F);
		builder.addVertex(0.460353F, 0.561779F, 0.908587F, 10F, 4.75F);
		builder.addVertex(0.451337F, 0.676429F, 0.957567F, 10F, 4.25F);

		//fruit5
		builder.addVertex(0.941917F, 0.904429F, 0.86994F, 10.25F, 5.5F);
		builder.addVertex(0.844953F, 0.904429F, 0.948825F, 10.5F, 5.5F);
		builder.addVertex(0.827141F, 0.782658F, 0.92693F, 10.5F, 6F);
		builder.addVertex(0.924105F, 0.782658F, 0.848045F, 10.25F, 6F);

		builder.addVertex(0.865069F, 0.932654F, 0.775479F, 11F, 5.5F);
		builder.addVertex(0.847257F, 0.810883F, 0.753585F, 11F, 6F);
		builder.addVertex(0.750293F, 0.810883F, 0.83247F, 10.75F, 6F);
		builder.addVertex(0.768105F, 0.932654F, 0.854365F, 10.75F, 5.5F);

		builder.addVertex(0.924105F, 0.782658F, 0.848045F, 10.5F, 5.5F);
		builder.addVertex(0.827141F, 0.782658F, 0.92693F, 10.75F, 5.5F);
		builder.addVertex(0.750293F, 0.810883F, 0.83247F, 10.75F, 5F);
		builder.addVertex(0.847257F, 0.810883F, 0.753585F, 10.5F, 5F);

		builder.addVertex(0.941917F, 0.904429F, 0.86994F, 10.25F, 5.5F);
		builder.addVertex(0.865069F, 0.932654F, 0.775479F, 10.25F, 5F);
		builder.addVertex(0.768105F, 0.932654F, 0.854365F, 10.5F, 5F);
		builder.addVertex(0.844953F, 0.904429F, 0.948825F, 10.5F, 5.5F);

		builder.addVertex(0.844953F, 0.904429F, 0.948825F, 10.5F, 5.5F);
		builder.addVertex(0.768105F, 0.932654F, 0.854365F, 10.75F, 5.5F);
		builder.addVertex(0.750293F, 0.810883F, 0.83247F, 10.75F, 6F);
		builder.addVertex(0.827141F, 0.782658F, 0.92693F, 10.5F, 6F);

		builder.addVertex(0.941917F, 0.904429F, 0.86994F, 10.25F, 5.5F);
		builder.addVertex(0.924105F, 0.782658F, 0.848045F, 10.25F, 6F);
		builder.addVertex(0.847257F, 0.810883F, 0.753585F, 10F, 6F);
		builder.addVertex(0.865069F, 0.932654F, 0.775479F, 10F, 5.5F);

		//fruit6
		builder.addVertex(-0.083174F, 0.430993F, 0.708564F, 10.25F, 6.75F);
		builder.addVertex(-0.108608F, 0.430993F, 0.586179F, 10.5F, 6.75F);
		builder.addVertex(-0.075594F, 0.310627F, 0.579318F, 10.5F, 7.25F);
		builder.addVertex(-0.05016F, 0.310627F, 0.701703F, 10.25F, 7.25F);

		builder.addVertex(0.034674F, 0.464713F, 0.684073F, 11F, 6.75F);
		builder.addVertex(0.067688F, 0.344347F, 0.677212F, 11F, 7.25F);
		builder.addVertex(0.042254F, 0.344347F, 0.554827F, 10.75F, 7.25F);
		builder.addVertex(0.00924F, 0.464713F, 0.561688F, 10.75F, 6.75F);

		builder.addVertex(-0.05016F, 0.310627F, 0.701703F, 10.5F, 6.75F);
		builder.addVertex(-0.075594F, 0.310627F, 0.579318F, 10.75F, 6.75F);
		builder.addVertex(0.042254F, 0.344347F, 0.554827F, 10.75F, 6.25F);
		builder.addVertex(0.067688F, 0.344347F, 0.677212F, 10.5F, 6.25F);

		builder.addVertex(-0.083174F, 0.430993F, 0.708564F, 10.25F, 6.75F);
		builder.addVertex(0.034674F, 0.464713F, 0.684073F, 10.25F, 6.25F);
		builder.addVertex(0.00924F, 0.464713F, 0.561688F, 10.5F, 6.25F);
		builder.addVertex(-0.108608F, 0.430993F, 0.586179F, 10.5F, 6.75F);

		builder.addVertex(-0.108608F, 0.430993F, 0.586179F, 10.5F, 6.75F);
		builder.addVertex(0.00924F, 0.464713F, 0.561688F, 10.75F, 6.75F);
		builder.addVertex(0.042254F, 0.344347F, 0.554827F, 10.75F, 7.25F);
		builder.addVertex(-0.075594F, 0.310627F, 0.579318F, 10.5F, 7.25F);

		builder.addVertex(-0.083174F, 0.430993F, 0.708564F, 10.25F, 6.75F);
		builder.addVertex(-0.05016F, 0.310627F, 0.701703F, 10.25F, 7.25F);
		builder.addVertex(0.067688F, 0.344347F, 0.677212F, 10F, 7.25F);
		builder.addVertex(0.034674F, 0.464713F, 0.684073F, 10F, 6.75F);
	}

	public static void buildStage3FruitAspects(QuadBuilder builder) {
		//fruit1aspect
		builder.addVertex(0.994188F, 0.280076F, 0.76462F, 0.125F, 0.25F);
		builder.addVertex(0.965435F, 0.280076F, 0.820113F, 0.25F, 0.25F);
		builder.addVertex(0.955389F, 0.218609F, 0.814908F, 0.25F, 0.5F);
		builder.addVertex(0.984142F, 0.218609F, 0.759414F, 0.125F, 0.5F);

		builder.addVertex(0.939611F, 0.291391F, 0.736342F, 0.5F, 0.25F);
		builder.addVertex(0.929565F, 0.229924F, 0.731137F, 0.5F, 0.5F);
		builder.addVertex(0.900812F, 0.229924F, 0.78663F, 0.375F, 0.5F);
		builder.addVertex(0.910858F, 0.291391F, 0.791836F, 0.375F, 0.25F);

		builder.addVertex(0.984142F, 0.218609F, 0.759414F, 0.25F, 0.25F);
		builder.addVertex(0.955389F, 0.218609F, 0.814908F, 0.375F, 0.25F);
		builder.addVertex(0.900812F, 0.229924F, 0.78663F, 0.375F, 0F);
		builder.addVertex(0.929565F, 0.229924F, 0.731137F, 0.25F, 0F);

		builder.addVertex(0.994188F, 0.280076F, 0.76462F, 0.125F, 0.25F);
		builder.addVertex(0.939611F, 0.291391F, 0.736342F, 0.125F, 0F);
		builder.addVertex(0.910858F, 0.291391F, 0.791836F, 0.25F, 0F);
		builder.addVertex(0.965435F, 0.280076F, 0.820113F, 0.25F, 0.25F);

		builder.addVertex(0.965435F, 0.280076F, 0.820113F, 0.25F, 0.25F);
		builder.addVertex(0.910858F, 0.291391F, 0.791836F, 0.375F, 0.25F);
		builder.addVertex(0.900812F, 0.229924F, 0.78663F, 0.375F, 0.5F);
		builder.addVertex(0.955389F, 0.218609F, 0.814908F, 0.25F, 0.5F);

		builder.addVertex(0.994188F, 0.280076F, 0.76462F, 0.125F, 0.25F);
		builder.addVertex(0.984142F, 0.218609F, 0.759414F, 0.125F, 0.5F);
		builder.addVertex(0.929565F, 0.229924F, 0.731137F, 0F, 0.5F);
		builder.addVertex(0.939611F, 0.291391F, 0.736342F, 0F, 0.25F);

		//fruit2aspect
		builder.addVertex(0.203715F, 0.208528F, 0.967068F, 0.125F, 0.25F);
		builder.addVertex(0.151845F, 0.208528F, 0.9322F, 0.25F, 0.25F);
		builder.addVertex(0.161251F, 0.148345F, 0.918207F, 0.25F, 0.5F);
		builder.addVertex(0.213121F, 0.148345F, 0.953076F, 0.125F, 0.5F);

		builder.addVertex(0.23729F, 0.225388F, 0.917121F, 0.5F, 0.25F);
		builder.addVertex(0.246696F, 0.165205F, 0.903129F, 0.5F, 0.5F);
		builder.addVertex(0.194827F, 0.165205F, 0.868261F, 0.375F, 0.5F);
		builder.addVertex(0.18542F, 0.225388F, 0.882253F, 0.375F, 0.25F);

		builder.addVertex(0.213121F, 0.148345F, 0.953076F, 0.25F, 0.25F);
		builder.addVertex(0.161251F, 0.148345F, 0.918207F, 0.375F, 0.25F);
		builder.addVertex(0.194827F, 0.165205F, 0.868261F, 0.375F, 0F);
		builder.addVertex(0.246696F, 0.165205F, 0.903129F, 0.25F, 0F);

		builder.addVertex(0.203715F, 0.208528F, 0.967068F, 0.125F, 0.25F);
		builder.addVertex(0.23729F, 0.225388F, 0.917121F, 0.125F, 0F);
		builder.addVertex(0.18542F, 0.225388F, 0.882253F, 0.25F, 0F);
		builder.addVertex(0.151845F, 0.208528F, 0.9322F, 0.25F, 0.25F);

		builder.addVertex(0.151845F, 0.208528F, 0.9322F, 0.25F, 0.25F);
		builder.addVertex(0.18542F, 0.225388F, 0.882253F, 0.375F, 0.25F);
		builder.addVertex(0.194827F, 0.165205F, 0.868261F, 0.375F, 0.5F);
		builder.addVertex(0.161251F, 0.148345F, 0.918207F, 0.25F, 0.5F);

		builder.addVertex(0.203715F, 0.208528F, 0.967068F, 0.125F, 0.25F);
		builder.addVertex(0.213121F, 0.148345F, 0.953076F, 0.125F, 0.5F);
		builder.addVertex(0.246696F, 0.165205F, 0.903129F, 0F, 0.5F);
		builder.addVertex(0.23729F, 0.225388F, 0.917121F, 0F, 0.25F);

		//fruit3aspect
		builder.addVertex(0.270007F, 0.105266F, 0.032538F, 0.125F, 0.25F);
		builder.addVertex(0.32853F, 0.102467F, 0.010778F, 0.25F, 0.25F);
		builder.addVertex(0.329901F, 0.041065F, 0.022363F, 0.25F, 0.5F);
		builder.addVertex(0.271378F, 0.043864F, 0.044123F, 0.125F, 0.5F);

		builder.addVertex(0.291904F, 0.116591F, 0.089971F, 0.5F, 0.25F);
		builder.addVertex(0.293275F, 0.05519F, 0.101556F, 0.5F, 0.5F);
		builder.addVertex(0.351798F, 0.052391F, 0.079795F, 0.375F, 0.5F);
		builder.addVertex(0.350427F, 0.113792F, 0.06821F, 0.375F, 0.25F);

		builder.addVertex(0.271378F, 0.043864F, 0.044123F, 0.25F, 0.25F);
		builder.addVertex(0.329901F, 0.041065F, 0.022363F, 0.375F, 0.25F);
		builder.addVertex(0.351798F, 0.052391F, 0.079795F, 0.375F, 0F);
		builder.addVertex(0.293275F, 0.05519F, 0.101556F, 0.25F, 0F);

		builder.addVertex(0.270007F, 0.105266F, 0.032538F, 0.125F, 0.25F);
		builder.addVertex(0.291904F, 0.116591F, 0.089971F, 0.125F, 0F);
		builder.addVertex(0.350427F, 0.113792F, 0.06821F, 0.25F, 0F);
		builder.addVertex(0.32853F, 0.102467F, 0.010778F, 0.25F, 0.25F);

		builder.addVertex(0.32853F, 0.102467F, 0.010778F, 0.25F, 0.25F);
		builder.addVertex(0.350427F, 0.113792F, 0.06821F, 0.375F, 0.25F);
		builder.addVertex(0.351798F, 0.052391F, 0.079795F, 0.375F, 0.5F);
		builder.addVertex(0.329901F, 0.041065F, 0.022363F, 0.25F, 0.5F);

		builder.addVertex(0.270007F, 0.105266F, 0.032538F, 0.125F, 0.25F);
		builder.addVertex(0.271378F, 0.043864F, 0.044123F, 0.125F, 0.5F);
		builder.addVertex(0.293275F, 0.05519F, 0.101556F, 0F, 0.5F);
		builder.addVertex(0.291904F, 0.116591F, 0.089971F, 0F, 0.25F);

		//fruit4aspect
		builder.addVertex(0.407291F, 0.610413F, 1.024232F, 0.125F, 0.25F);
		builder.addVertex(0.345824F, 0.610413F, 1.012917F, 0.25F, 0.25F);
		builder.addVertex(0.350332F, 0.553088F, 0.988427F, 0.25F, 0.5F);
		builder.addVertex(0.411799F, 0.553088F, 0.999741F, 0.125F, 0.5F);

		builder.addVertex(0.417669F, 0.635315F, 0.967854F, 0.5F, 0.25F);
		builder.addVertex(0.422177F, 0.57799F, 0.943363F, 0.5F, 0.5F);
		builder.addVertex(0.360709F, 0.57799F, 0.932049F, 0.375F, 0.5F);
		builder.addVertex(0.356201F, 0.635315F, 0.956539F, 0.375F, 0.25F);

		builder.addVertex(0.411799F, 0.553088F, 0.999741F, 0.25F, 0.25F);
		builder.addVertex(0.350332F, 0.553088F, 0.988427F, 0.375F, 0.25F);
		builder.addVertex(0.360709F, 0.57799F, 0.932049F, 0.375F, 0F);
		builder.addVertex(0.422177F, 0.57799F, 0.943363F, 0.25F, 0F);

		builder.addVertex(0.407291F, 0.610413F, 1.024232F, 0.125F, 0.25F);
		builder.addVertex(0.417669F, 0.635315F, 0.967854F, 0.125F, 0F);
		builder.addVertex(0.356201F, 0.635315F, 0.956539F, 0.25F, 0F);
		builder.addVertex(0.345824F, 0.610413F, 1.012917F, 0.25F, 0.25F);

		builder.addVertex(0.345824F, 0.610413F, 1.012917F, 0.25F, 0.25F);
		builder.addVertex(0.356201F, 0.635315F, 0.956539F, 0.375F, 0.25F);
		builder.addVertex(0.360709F, 0.57799F, 0.932049F, 0.375F, 0.5F);
		builder.addVertex(0.350332F, 0.553088F, 0.988427F, 0.25F, 0.5F);

		builder.addVertex(0.407291F, 0.610413F, 1.024232F, 0.125F, 0.25F);
		builder.addVertex(0.411799F, 0.553088F, 0.999741F, 0.125F, 0.5F);
		builder.addVertex(0.422177F, 0.57799F, 0.943363F, 0F, 0.5F);
		builder.addVertex(0.417669F, 0.635315F, 0.967854F, 0F, 0.25F);

		//fruit5aspect
		builder.addVertex(0.898744F, 0.88572F, 0.86639F, 0.125F, 0.25F);
		builder.addVertex(0.850262F, 0.88572F, 0.905833F, 0.25F, 0.25F);
		builder.addVertex(0.841356F, 0.824834F, 0.894885F, 0.25F, 0.5F);
		builder.addVertex(0.889838F, 0.824834F, 0.855443F, 0.125F, 0.5F);

		builder.addVertex(0.86032F, 0.899833F, 0.81916F, 0.5F, 0.25F);
		builder.addVertex(0.851414F, 0.838947F, 0.808212F, 0.5F, 0.5F);
		builder.addVertex(0.802932F, 0.838947F, 0.847655F, 0.375F, 0.5F);
		builder.addVertex(0.811838F, 0.899833F, 0.858602F, 0.375F, 0.25F);

		builder.addVertex(0.889838F, 0.824834F, 0.855443F, 0.25F, 0.25F);
		builder.addVertex(0.841356F, 0.824834F, 0.894885F, 0.375F, 0.25F);
		builder.addVertex(0.802932F, 0.838947F, 0.847655F, 0.375F, 0F);
		builder.addVertex(0.851414F, 0.838947F, 0.808212F, 0.25F, 0F);

		builder.addVertex(0.898744F, 0.88572F, 0.86639F, 0.125F, 0.25F);
		builder.addVertex(0.86032F, 0.899833F, 0.81916F, 0.125F, 0F);
		builder.addVertex(0.811838F, 0.899833F, 0.858602F, 0.25F, 0F);
		builder.addVertex(0.850262F, 0.88572F, 0.905833F, 0.25F, 0.25F);

		builder.addVertex(0.850262F, 0.88572F, 0.905833F, 0.25F, 0.25F);
		builder.addVertex(0.811838F, 0.899833F, 0.858602F, 0.375F, 0.25F);
		builder.addVertex(0.802932F, 0.838947F, 0.847655F, 0.375F, 0.5F);
		builder.addVertex(0.841356F, 0.824834F, 0.894885F, 0.25F, 0.5F);

		builder.addVertex(0.898744F, 0.88572F, 0.86639F, 0.125F, 0.25F);
		builder.addVertex(0.889838F, 0.824834F, 0.855443F, 0.125F, 0.5F);
		builder.addVertex(0.851414F, 0.838947F, 0.808212F, 0F, 0.5F);
		builder.addVertex(0.86032F, 0.899833F, 0.81916F, 0F, 0.25F);

		//fruit6aspect
		builder.addVertex(-0.051817F, 0.409332F, 0.670129F, 0.125F, 0.25F);
		builder.addVertex(-0.064534F, 0.409332F, 0.608937F, 0.25F, 0.25F);
		builder.addVertex(-0.048027F, 0.349149F, 0.605506F, 0.25F, 0.5F);
		builder.addVertex(-0.03531F, 0.349149F, 0.666699F, 0.125F, 0.5F);

		builder.addVertex(0.007107F, 0.426192F, 0.657884F, 0.5F, 0.25F);
		builder.addVertex(0.023614F, 0.366009F, 0.654453F, 0.5F, 0.5F);
		builder.addVertex(0.010897F, 0.366009F, 0.593261F, 0.375F, 0.5F);
		builder.addVertex(-0.00561F, 0.426192F, 0.596691F, 0.375F, 0.25F);

		builder.addVertex(-0.03531F, 0.349149F, 0.666699F, 0.25F, 0.25F);
		builder.addVertex(-0.048027F, 0.349149F, 0.605506F, 0.375F, 0.25F);
		builder.addVertex(0.010897F, 0.366009F, 0.593261F, 0.375F, 0F);
		builder.addVertex(0.023614F, 0.366009F, 0.654453F, 0.25F, 0F);

		builder.addVertex(-0.051817F, 0.409332F, 0.670129F, 0.125F, 0.25F);
		builder.addVertex(0.007107F, 0.426192F, 0.657884F, 0.125F, 0F);
		builder.addVertex(-0.00561F, 0.426192F, 0.596691F, 0.25F, 0F);
		builder.addVertex(-0.064534F, 0.409332F, 0.608937F, 0.25F, 0.25F);

		builder.addVertex(-0.064534F, 0.409332F, 0.608937F, 0.25F, 0.25F);
		builder.addVertex(-0.00561F, 0.426192F, 0.596691F, 0.375F, 0.25F);
		builder.addVertex(0.010897F, 0.366009F, 0.593261F, 0.375F, 0.5F);
		builder.addVertex(-0.048027F, 0.349149F, 0.605506F, 0.25F, 0.5F);

		builder.addVertex(-0.051817F, 0.409332F, 0.670129F, 0.125F, 0.25F);
		builder.addVertex(-0.03531F, 0.349149F, 0.666699F, 0.125F, 0.5F);
		builder.addVertex(0.023614F, 0.366009F, 0.654453F, 0F, 0.5F);
		builder.addVertex(0.007107F, 0.426192F, 0.657884F, 0F, 0.25F);
	}

	public static void buildStage2(QuadBuilder builder) {
		//leaf6b
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);

		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 2F, 7.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 2F, 8F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);

		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 2F, 8F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 2F, 7.75F);

		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 1F, 7.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 6.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 1F, 6.75F);

		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 1F, 6.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 6.75F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1.5F, 7.75F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 1F, 7.75F);

		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.5F, 6.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1F, 6.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);

		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1F, 6.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.5F, 6.75F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);

		builder.addVertex(1.086472F, 0.487314F, 0.347405F, 1F, 8F);
		builder.addVertex(0.860934F, 0.543764F, 0.439308F, 1.5F, 8F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.5F, 7.75F);
		builder.addVertex(1.099541F, 0.5482F, 0.34208F, 1F, 7.75F);

		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 0F, 8F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0F, 7.75F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0F, 7.75F);
		builder.addVertex(0.766595F, 0.543764F, 0.207791F, 0F, 8F);
		builder.addVertex(0.992133F, 0.487314F, 0.115888F, 0.5F, 8F);
		builder.addVertex(1.005202F, 0.5482F, 0.110563F, 0.5F, 7.75F);

		//leaf7
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 1.125F, 9.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.75F, 9.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.75F, 8.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 1.125F, 8.25F);

		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 1.125F, 8.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.75F, 8.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.75F, 9.25F);
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 1.125F, 9.25F);

		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 0.5F, 9.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 0.5F, 8.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.125F, 8.25F);
		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.125F, 9.25F);

		builder.addVertex(0.245252F, 0.705607F, 0.868801F, 1.125F, 9.25F);
		builder.addVertex(0.289343F, 0.649199F, 0.629272F, 1.125F, 8.25F);
		builder.addVertex(0.59668F, 0.649199F, 0.685845F, 0.5F, 8.25F);
		builder.addVertex(0.552588F, 0.705607F, 0.925374F, 0.5F, 9.25F);

		//leaf4b
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);

		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 7.625F, 5.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 7.625F, 5.5F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);

		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 7.625F, 5.5F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 7.625F, 5.25F);

		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.625F, 5.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 4.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 6.625F, 4.25F);

		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 6.625F, 4.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 4.25F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 7.125F, 5.25F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.625F, 5.25F);

		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);

		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4.25F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4.25F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);

		builder.addVertex(0.802246F, 0.369521F, -0.079914F, 6.625F, 5.5F);
		builder.addVertex(0.736547F, 0.425971F, 0.154601F, 7.125F, 5.5F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 7.125F, 5.25F);
		builder.addVertex(0.806053F, 0.430406F, -0.093503F, 6.625F, 5.25F);

		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 5.625F, 5.5F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 5.25F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 5.25F);
		builder.addVertex(0.495815F, 0.425971F, 0.087161F, 5.625F, 5.5F);
		builder.addVertex(0.561514F, 0.369521F, -0.147354F, 6.125F, 5.5F);
		builder.addVertex(0.565321F, 0.430406F, -0.160943F, 6.125F, 5.25F);

		//leaf4
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.625F, 3F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 6.125F, 3F);

		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 6.125F, 3F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.625F, 3F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.625F, 4F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 6.125F, 4F);

		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 4F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 5.625F, 3F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.125F, 3F);
		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.125F, 4F);

		builder.addVertex(0.740354F, 0.486857F, 0.141011F, 6.125F, 4F);
		builder.addVertex(0.674653F, 0.430449F, 0.375535F, 6.125F, 3F);
		builder.addVertex(0.433921F, 0.430449F, 0.308095F, 5.625F, 3F);
		builder.addVertex(0.499622F, 0.486857F, 0.073571F, 5.625F, 4F);

		//leaf8
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.875F, 11.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.375F, 11.75F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 1.375F, 11F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.875F, 11F);

		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.875F, 11F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 1.375F, 11F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.375F, 11.75F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.875F, 11.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 11.75F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.375F, 11F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 0.875F, 11F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 11.75F);

		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 11.75F);
		builder.addVertex(0.62164F, 0.713093F, 0.673098F, 0.875F, 11F);
		builder.addVertex(0.694566F, 0.713093F, 0.433971F, 0.375F, 11F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 11.75F);

		//leaf9b
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);

		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 4.5F, 7.5F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 4.5F, 7.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);

		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 4.5F, 7.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 4.5F, 7.5F);

		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.625F, 7.5F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 4.125F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4.125F, 6.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 3.625F, 6.75F);

		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 3.625F, 6.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4.125F, 6.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 4.125F, 7.5F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.625F, 7.5F);

		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.75F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);

		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.75F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);

		builder.addVertex(0.10142F, 0.549518F, 0.140045F, 3.625F, 7.75F);
		builder.addVertex(0.25778F, 0.600098F, 0.23032F, 4F, 7.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4F, 7.5F);
		builder.addVertex(0.086818F, 0.609701F, 0.131615F, 3.625F, 7.5F);

		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 2.75F, 7.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 2.75F, 7.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 2.75F, 7.5F);
		builder.addVertex(0.13278F, 0.600099F, 0.446826F, 2.75F, 7.75F);
		builder.addVertex(-0.02358F, 0.549518F, 0.356551F, 3.125F, 7.75F);
		builder.addVertex(-0.038182F, 0.609701F, 0.348121F, 3.125F, 7.5F);

		//crop1
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);

		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.6875F, 3F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 3F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 3F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 3F, 1.5F);

		builder.addVertex(0.3125F, 0F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 0F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 1.5F, 0F);

		builder.addVertex(0.3125F, 0F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 0F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 2.25F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0.75F, 0F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 1.5F, 0F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0.75F, 0F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);

		builder.addVertex(0.6875F, -0F, 0.3125F, 1.5F, 5.5F);
		builder.addVertex(0.6875F, -0F, 0.6875F, 2.25F, 5.5F);
		builder.addVertex(0.6875F, 1F, 0.6875F, 2.25F, 1.5F);
		builder.addVertex(0.6875F, 1F, 0.3125F, 1.5F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 0F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.6875F, 0F, 1.5F);

		builder.addVertex(0.3125F, 1F, 0.6875F, 0F, 1.5F);
		builder.addVertex(0.3125F, 0F, 0.6875F, 0F, 5.5F);
		builder.addVertex(0.3125F, 0F, 0.3125F, 0.75F, 5.5F);
		builder.addVertex(0.3125F, 1F, 0.3125F, 0.75F, 1.5F);

		//leaf7b
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);

		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 2.25F, 10.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 2.25F, 10.75F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);

		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 2.25F, 10.75F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 2.25F, 10.5F);

		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 1.125F, 10.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.75F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.75F, 9.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 1.125F, 9.5F);

		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 1.125F, 9.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.75F, 9.5F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.75F, 10.5F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 1.125F, 10.5F);

		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0.5F, 9.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.125F, 9.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);

		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.125F, 9.5F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0.5F, 9.5F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);

		builder.addVertex(0.209351F, 0.547265F, 1.063834F, 1.125F, 10.75F);
		builder.addVertex(0.250862F, 0.646872F, 0.838323F, 1.625F, 10.75F);
		builder.addVertex(0.246354F, 0.704197F, 0.862813F, 1.625F, 10.5F);
		builder.addVertex(0.204843F, 0.604589F, 1.088324F, 1.125F, 10.5F);

		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 0F, 10.75F);
		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0F, 10.5F);

		builder.addVertex(0.553691F, 0.704197F, 0.919386F, 0F, 10.5F);
		builder.addVertex(0.558199F, 0.646872F, 0.894896F, 0F, 10.75F);
		builder.addVertex(0.516688F, 0.547265F, 1.120407F, 0.5F, 10.75F);
		builder.addVertex(0.51218F, 0.604589F, 1.144897F, 0.5F, 10.5F);

		//leaf3b
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);

		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 5.625F, 5.25F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 5.625F, 5.5F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);

		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 5.625F, 5.5F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 5.625F, 5.25F);

		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 4.375F, 5.25F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 4F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 4.375F, 4F);

		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 4.375F, 4F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 4F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 5F, 5.25F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 4.375F, 5.25F);

		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 4F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 4F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);

		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 4F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 4F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);

		builder.addVertex(-0.03066F, 0.133277F, 0.979635F, 4.375F, 5.5F);
		builder.addVertex(0.137218F, 0.217577F, 0.729901F, 5F, 5.5F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 5F, 5.25F);
		builder.addVertex(-0.040066F, 0.19346F, 0.993627F, 4.375F, 5.25F);

		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 3.125F, 5.5F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 5.25F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 5.25F);
		builder.addVertex(0.396566F, 0.217577F, 0.904242F, 3.125F, 5.5F);
		builder.addVertex(0.228689F, 0.133277F, 1.153975F, 3.75F, 5.5F);
		builder.addVertex(0.219283F, 0.19346F, 1.167968F, 3.75F, 5.25F);

		//leaf2
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.25F, 1F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7F, 1F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 7F, 0F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 6.25F, 0F);

		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 6.25F, 0F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 7F, 0F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7F, 1F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.25F, 1F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 1F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 5.5F, 0F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 6.25F, 0F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.25F, 1F);

		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.25F, 1F);
		builder.addVertex(0.542504F, 0.308243F, 0.777238F, 6.25F, 0F);
		builder.addVertex(0.71502F, 0.308243F, 0.444277F, 5.5F, 0F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 1F);

		//leaf1
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.75F, 0.75F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 4.375F, 0.75F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 4.375F, 0F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.75F, 0F);

		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.75F, 0F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 4.375F, 0F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 4.375F, 0.75F);
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.75F, 0.75F);

		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.125F, 0.75F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.125F, 0F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 3.75F, 0F);
		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 3.75F, 0.75F);

		builder.addVertex(0.498048F, 0.161132F, 0.108425F, 3.75F, 0.75F);
		builder.addVertex(0.562368F, 0.110552F, 0.277128F, 3.75F, 0F);
		builder.addVertex(0.270371F, 0.110552F, 0.388456F, 3.125F, 0F);
		builder.addVertex(0.20605F, 0.161132F, 0.219752F, 3.125F, 0.75F);

		//leaf5
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.375F, 4F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9F, 4F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 9F, 3F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 8.375F, 3F);

		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 8.375F, 3F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 9F, 3F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9F, 4F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.375F, 4F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 4F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 7.75F, 3F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 8.375F, 3F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.375F, 4F);

		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.375F, 4F);
		builder.addVertex(0.280708F, 0.40625F, 0.409519F, 8.375F, 3F);
		builder.addVertex(0.344292F, 0.40625F, 0.715481F, 7.75F, 3F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 4F);

		//leaf6
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.875F, 6.5F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.375F, 6.5F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 1.375F, 5.75F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.875F, 5.75F);

		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.875F, 5.75F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 1.375F, 5.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 1.375F, 6.5F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.875F, 6.5F);

		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.375F, 6.5F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.375F, 5.75F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 0.875F, 5.75F);
		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 0.875F, 6.5F);

		builder.addVertex(0.874003F, 0.60465F, 0.433982F, 0.875F, 6.5F);
		builder.addVertex(0.706803F, 0.55407F, 0.502114F, 0.875F, 5.75F);
		builder.addVertex(0.612464F, 0.55407F, 0.270597F, 0.375F, 5.75F);
		builder.addVertex(0.779664F, 0.60465F, 0.202465F, 0.375F, 6.5F);

		//leaf10b
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);

		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 4.5F, 9.75F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 4.5F, 10F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);

		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 4.5F, 10F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 4.5F, 9.75F);

		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.625F, 9.75F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 4.125F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4.125F, 9F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 3.625F, 9F);

		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 3.625F, 9F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4.125F, 9F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 4.125F, 9.75F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.625F, 9.75F);

		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 9F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 9F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);

		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 9F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 9F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);

		builder.addVertex(0.708838F, 0.736548F, 0.016221F, 3.625F, 10F);
		builder.addVertex(0.692639F, 0.795297F, 0.193541F, 4F, 10F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4F, 9.75F);
		builder.addVertex(0.71062F, 0.795901F, -0.003281F, 3.625F, 9.75F);

		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 2.75F, 10F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 2.75F, 9.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 2.75F, 9.75F);
		builder.addVertex(0.443675F, 0.795297F, 0.170796F, 2.75F, 10F);
		builder.addVertex(0.459875F, 0.736548F, -0.006524F, 3.125F, 10F);
		builder.addVertex(0.461657F, 0.795901F, -0.026026F, 3.125F, 9.75F);

		//leaf9
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.625F, 6.5F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4.125F, 6.5F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 4.125F, 5.75F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.625F, 5.75F);

		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.625F, 5.75F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 4.125F, 5.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 4.125F, 6.5F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.625F, 6.5F);

		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.5F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.125F, 5.75F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 3.625F, 5.75F);
		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.5F);

		builder.addVertex(0.243178F, 0.660281F, 0.221889F, 3.625F, 6.5F);
		builder.addVertex(0.401364F, 0.617944F, 0.313218F, 3.625F, 5.75F);
		builder.addVertex(0.276364F, 0.617944F, 0.529725F, 3.125F, 5.75F);
		builder.addVertex(0.118178F, 0.660281F, 0.438396F, 3.125F, 6.5F);

		//leaf8b
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);

		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 1.75F, 12.75F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 1.75F, 13F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);

		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 1.75F, 13F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 1.75F, 12.75F);

		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.875F, 12.75F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 1.375F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.375F, 12F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0.875F, 12F);

		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0.875F, 12F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.375F, 12F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 1.375F, 12.75F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.875F, 12.75F);

		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 12F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 12F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);

		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 0.875F, 12F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0.375F, 12F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);

		builder.addVertex(0.954573F, 0.636245F, 0.774632F, 0.875F, 13F);
		builder.addVertex(0.781885F, 0.686857F, 0.721968F, 1.25F, 13F);
		builder.addVertex(0.798022F, 0.747037F, 0.726889F, 1.25F, 12.75F);
		builder.addVertex(0.97071F, 0.696425F, 0.779553F, 0.875F, 12.75F);

		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0F, 13F);
		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0F, 12.75F);

		builder.addVertex(0.870948F, 0.747037F, 0.487762F, 0F, 12.75F);
		builder.addVertex(0.854811F, 0.686857F, 0.482841F, 0F, 13F);
		builder.addVertex(1.027499F, 0.636245F, 0.535505F, 0.375F, 13F);
		builder.addVertex(1.043636F, 0.696425F, 0.540426F, 0.375F, 12.75F);

		//leaf11
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.75F, 11F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 11F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 4.375F, 10.25F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.75F, 10.25F);

		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.75F, 10.25F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 4.375F, 10.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 11F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.75F, 11F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.125F, 11F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.125F, 10.25F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 3.75F, 10.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.75F, 11F);

		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.75F, 11F);
		builder.addVertex(0.547304F, 0.89782F, 0.731514F, 3.75F, 10.25F);
		builder.addVertex(0.789716F, 0.89782F, 0.534301F, 3.125F, 10.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.125F, 11F);

		//leaf11b
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);

		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 5F, 12.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 5F, 12.5F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);

		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 5F, 12.5F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 5F, 12.25F);

		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.875F, 12.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 4.5F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.5F, 11.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 3.875F, 11.25F);

		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 3.875F, 11.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.5F, 11.25F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 4.5F, 12.25F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.875F, 12.25F);

		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.25F, 11.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.875F, 11.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);

		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 3.875F, 11.25F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 3.25F, 11.25F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);

		builder.addVertex(0.806035F, 0.831064F, 1.049542F, 3.875F, 12.5F);
		builder.addVertex(0.652339F, 0.887514F, 0.860621F, 4.375F, 12.5F);
		builder.addVertex(0.661245F, 0.9484F, 0.871569F, 4.375F, 12.25F);
		builder.addVertex(0.814941F, 0.89195F, 1.060489F, 3.875F, 12.25F);

		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 2.75F, 12.5F);
		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 2.75F, 12.25F);

		builder.addVertex(0.903657F, 0.9484F, 0.674356F, 2.75F, 12.25F);
		builder.addVertex(0.894751F, 0.887514F, 0.663408F, 2.75F, 12.5F);
		builder.addVertex(1.048446F, 0.831064F, 0.852329F, 3.25F, 12.5F);
		builder.addVertex(1.057353F, 0.89195F, 0.863276F, 3.25F, 12.25F);

		//leaf5b
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);

		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 10F, 5.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 10F, 5.5F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);

		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 10F, 5.5F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 10F, 5.25F);

		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.875F, 5.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 9.5F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.5F, 4.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 8.875F, 4.25F);

		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 8.875F, 4.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.5F, 4.25F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 9.5F, 5.25F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.875F, 5.25F);

		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.25F, 4.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.875F, 4.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);

		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 8.875F, 4.25F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 8.25F, 4.25F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);

		builder.addVertex(-0.170926F, 0.356959F, 0.503376F, 8.875F, 5.5F);
		builder.addVertex(0.06477F, 0.424399F, 0.454394F, 9.375F, 5.5F);
		builder.addVertex(0.048263F, 0.484582F, 0.457825F, 9.375F, 5.25F);
		builder.addVertex(-0.187433F, 0.417142F, 0.506807F, 8.875F, 5.25F);

		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 7.75F, 5.5F);
		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 5.25F);

		builder.addVertex(0.111848F, 0.484582F, 0.763788F, 7.75F, 5.25F);
		builder.addVertex(0.128355F, 0.424399F, 0.760357F, 7.75F, 5.5F);
		builder.addVertex(-0.107341F, 0.356959F, 0.809339F, 8.25F, 5.5F);
		builder.addVertex(-0.123848F, 0.417142F, 0.81277F, 8.25F, 5.25F);

		//leaf2b
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);

		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 8.25F, 2.5F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 8.25F, 2.75F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);

		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 8.25F, 2.75F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 8.25F, 2.5F);

		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.875F, 2.5F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 7.625F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.625F, 1.25F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 6.875F, 1.25F);

		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 6.875F, 1.25F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.625F, 1.25F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 7.625F, 2.5F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.875F, 2.5F);

		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.125F, 1.25F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.875F, 1.25F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);

		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 6.875F, 1.25F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 6.125F, 1.25F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);

		builder.addVertex(1.025245F, 0.224261F, 1.02736F, 6.875F, 2.75F);
		builder.addVertex(0.752362F, 0.280834F, 0.885972F, 7.5F, 2.75F);
		builder.addVertex(0.762408F, 0.342301F, 0.891177F, 7.5F, 2.5F);
		builder.addVertex(1.035291F, 0.285728F, 1.032565F, 6.875F, 2.5F);

		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 5.5F, 2.75F);
		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 2.5F);

		builder.addVertex(0.934925F, 0.342301F, 0.558216F, 5.5F, 2.5F);
		builder.addVertex(0.924879F, 0.280834F, 0.553011F, 5.5F, 2.75F);
		builder.addVertex(1.197761F, 0.224261F, 0.694399F, 6.125F, 2.75F);
		builder.addVertex(1.207808F, 0.285728F, 0.699605F, 6.125F, 2.5F);

		//leaf1b
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);

		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 5.375F, 2.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 5.375F, 2.5F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);

		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 5.375F, 2.5F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 5.375F, 2.25F);

		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 4.25F, 2.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.875F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.875F, 1.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 4.25F, 1.25F);

		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 4.25F, 1.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.875F, 1.25F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.875F, 2.25F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 4.25F, 2.25F);

		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.625F, 1.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.25F, 1.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);

		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.25F, 1.25F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.625F, 1.25F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);

		builder.addVertex(0.415095F, 0.060383F, -0.109148F, 4.25F, 2.5F);
		builder.addVertex(0.502683F, 0.105685F, 0.120582F, 4.75F, 2.5F);
		builder.addVertex(0.498648F, 0.16715F, 0.11F, 4.75F, 2.25F);
		builder.addVertex(0.41106F, 0.121849F, -0.119731F, 4.25F, 2.25F);

		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 3.125F, 2.5F);
		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.125F, 2.25F);

		builder.addVertex(0.206651F, 0.16715F, 0.221328F, 3.125F, 2.25F);
		builder.addVertex(0.210686F, 0.105685F, 0.23191F, 3.125F, 2.5F);
		builder.addVertex(0.123098F, 0.060383F, 0.00218F, 3.625F, 2.5F);
		builder.addVertex(0.119063F, 0.121849F, -0.008403F, 3.625F, 2.25F);

		//leaf10
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.625F, 8.75F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4.125F, 8.75F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 4.125F, 8F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.625F, 8F);

		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.625F, 8F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 4.125F, 8F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 4.125F, 8.75F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.625F, 8.75F);

		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 8.75F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.125F, 8F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 3.625F, 8F);
		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 8.75F);

		builder.addVertex(0.69442F, 0.85465F, 0.174039F, 3.625F, 8.75F);
		builder.addVertex(0.677994F, 0.80407F, 0.353839F, 3.625F, 8F);
		builder.addVertex(0.429031F, 0.80407F, 0.331094F, 3.125F, 8F);
		builder.addVertex(0.445457F, 0.85465F, 0.151294F, 3.125F, 8.75F);

		//leaf3
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 3.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 3.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 4.375F, 2.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.75F, 2.75F);

		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.75F, 2.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 4.375F, 2.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 4.375F, 3.75F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.75F, 3.75F);

		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 3.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.125F, 2.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 3.75F, 2.75F);
		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 3.75F, 3.75F);

		builder.addVertex(0.127812F, 0.27776F, 0.743893F, 3.75F, 3.75F);
		builder.addVertex(0.262114F, 0.21032F, 0.544106F, 3.75F, 2.75F);
		builder.addVertex(0.521462F, 0.21032F, 0.718447F, 3.125F, 2.75F);
		builder.addVertex(0.38716F, 0.27776F, 0.918234F, 3.125F, 3.75F);
	}

	public static void buildStage1(QuadBuilder builder) {
		//leaf3
		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 6.25F, 7.5F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 7.25F, 7.5F);
		builder.addVertex(0.288048F, 0.17907F, 0.56154F, 7.25F, 5.5F);
		builder.addVertex(0.495527F, 0.17907F, 0.701013F, 6.25F, 5.5F);

		builder.addVertex(0.495527F, 0.17907F, 0.701013F, 6.25F, 5.5F);
		builder.addVertex(0.288048F, 0.17907F, 0.56154F, 7.25F, 5.5F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 7.25F, 7.5F);
		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 6.25F, 7.5F);

		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 5.25F, 7.5F);
		builder.addVertex(0.495527F, 0.17907F, 0.701013F, 5.25F, 5.5F);
		builder.addVertex(0.288048F, 0.17907F, 0.56154F, 6.25F, 5.5F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 6.25F, 7.5F);

		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 6.25F, 7.5F);
		builder.addVertex(0.288048F, 0.17907F, 0.56154F, 6.25F, 5.5F);
		builder.addVertex(0.495527F, 0.17907F, 0.701013F, 5.25F, 5.5F);
		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 5.25F, 7.5F);

		//leaf2
		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 10.25F, 2F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 11.5F, 2F);
		builder.addVertex(0.55688F, 0.245743F, 0.718241F, 11.5F, 0F);
		builder.addVertex(0.700644F, 0.245743F, 0.440774F, 10.25F, 0F);

		builder.addVertex(0.700644F, 0.245743F, 0.440774F, 10.25F, 0F);
		builder.addVertex(0.55688F, 0.245743F, 0.718241F, 11.5F, 0F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 11.5F, 2F);
		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 10.25F, 2F);

		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 9F, 2F);
		builder.addVertex(0.700644F, 0.245743F, 0.440774F, 9F, 0F);
		builder.addVertex(0.55688F, 0.245743F, 0.718241F, 10.25F, 0F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 10.25F, 2F);

		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 10.25F, 2F);
		builder.addVertex(0.55688F, 0.245743F, 0.718241F, 10.25F, 0F);
		builder.addVertex(0.700644F, 0.245743F, 0.440774F, 9F, 0F);
		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 9F, 2F);

		//leaf5
		builder.addVertex(0.223906F, 0.361458F, 0.715077F, 10F, 9F);
		builder.addVertex(0.173038F, 0.361458F, 0.470306F, 11F, 9F);
		builder.addVertex(0.347372F, 0.302708F, 0.434077F, 11F, 7.5F);
		builder.addVertex(0.398239F, 0.302708F, 0.678847F, 10F, 7.5F);

		builder.addVertex(0.398239F, 0.302708F, 0.678847F, 10F, 7.5F);
		builder.addVertex(0.347372F, 0.302708F, 0.434077F, 11F, 7.5F);
		builder.addVertex(0.173038F, 0.361458F, 0.470306F, 11F, 9F);
		builder.addVertex(0.223906F, 0.361458F, 0.715077F, 10F, 9F);

		builder.addVertex(0.223906F, 0.361458F, 0.715077F, 9F, 9F);
		builder.addVertex(0.398239F, 0.302708F, 0.678847F, 9F, 7.5F);
		builder.addVertex(0.347372F, 0.302708F, 0.434077F, 10F, 7.5F);
		builder.addVertex(0.173038F, 0.361458F, 0.470306F, 10F, 9F);

		builder.addVertex(0.173038F, 0.361458F, 0.470306F, 10F, 9F);
		builder.addVertex(0.347372F, 0.302708F, 0.434077F, 10F, 7.5F);
		builder.addVertex(0.398239F, 0.302708F, 0.678847F, 9F, 7.5F);
		builder.addVertex(0.223906F, 0.361458F, 0.715077F, 9F, 9F);

		//crop1
		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);
		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 2.5F, 6.5F);
		builder.addVertex(0.34375F, 0F, 0.34375F, 1.25F, 6.5F);

		builder.addVertex(0.34375F, 0F, 0.34375F, 1.25F, 6.5F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 2.5F, 6.5F);
		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);

		builder.addVertex(0.34375F, 0.5F, 0.65625F, 5F, 2.5F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 5F, 6.5F);
		builder.addVertex(0.65625F, -0F, 0.65625F, 3.75F, 6.5F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 3.75F, 2.5F);

		builder.addVertex(0.65625F, 0.5F, 0.65625F, 3.75F, 2.5F);
		builder.addVertex(0.65625F, -0F, 0.65625F, 3.75F, 6.5F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 5F, 6.5F);
		builder.addVertex(0.34375F, 0.5F, 0.65625F, 5F, 2.5F);

		builder.addVertex(0.34375F, 0F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 3.75F, 2.5F);
		builder.addVertex(0.65625F, -0F, 0.65625F, 3.75F, 0F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 2.5F, 0F);

		builder.addVertex(0.34375F, 0F, 0.65625F, 2.5F, 0F);
		builder.addVertex(0.65625F, -0F, 0.65625F, 3.75F, 0F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 3.75F, 2.5F);
		builder.addVertex(0.34375F, 0F, 0.34375F, 2.5F, 2.5F);

		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);
		builder.addVertex(0.34375F, 0.5F, 0.65625F, 1.25F, 0F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 2.5F, 0F);
		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);

		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 2.5F, 0F);
		builder.addVertex(0.34375F, 0.5F, 0.65625F, 1.25F, 0F);
		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);

		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 3.75F, 2.5F);
		builder.addVertex(0.65625F, -0F, 0.65625F, 3.75F, 6.5F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 2.5F, 6.5F);

		builder.addVertex(0.65625F, -0F, 0.34375F, 2.5F, 6.5F);
		builder.addVertex(0.65625F, -0F, 0.65625F, 3.75F, 6.5F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 3.75F, 2.5F);
		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);

		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);
		builder.addVertex(0.34375F, 0F, 0.34375F, 1.25F, 6.5F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 0F, 6.5F);
		builder.addVertex(0.34375F, 0.5F, 0.65625F, 0F, 2.5F);

		builder.addVertex(0.34375F, 0.5F, 0.65625F, 0F, 2.5F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 0F, 6.5F);
		builder.addVertex(0.34375F, 0F, 0.34375F, 1.25F, 6.5F);
		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);

		//leaf3b
		builder.addVertex(0.226923F, 0.17907F, 1.100587F, 6.25F, 10F);
		builder.addVertex(0.019445F, 0.17907F, 0.961114F, 7.25F, 10F);
		builder.addVertex(0.028851F, 0.118887F, 0.947122F, 7.25F, 10.5F);
		builder.addVertex(0.236329F, 0.118887F, 1.086594F, 6.25F, 10.5F);

		builder.addVertex(0.236329F, 0.118887F, 1.086594F, 6.25F, 10.5F);
		builder.addVertex(0.028851F, 0.118887F, 0.947122F, 7.25F, 10.5F);
		builder.addVertex(0.019445F, 0.17907F, 0.961114F, 7.25F, 10F);
		builder.addVertex(0.226923F, 0.17907F, 1.100587F, 6.25F, 10F);

		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 9.25F, 10F);
		builder.addVertex(0.370631F, 0.186327F, 0.886807F, 9.25F, 10.5F);
		builder.addVertex(0.163153F, 0.186327F, 0.747335F, 8.25F, 10.5F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 8.25F, 10F);

		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 8.25F, 10F);
		builder.addVertex(0.163153F, 0.186327F, 0.747335F, 8.25F, 10.5F);
		builder.addVertex(0.370631F, 0.186327F, 0.886807F, 9.25F, 10.5F);
		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 9.25F, 10F);

		builder.addVertex(0.236329F, 0.118887F, 1.086594F, 7.25F, 10F);
		builder.addVertex(0.028851F, 0.118887F, 0.947122F, 8.25F, 10F);
		builder.addVertex(0.163153F, 0.186327F, 0.747335F, 8.25F, 8F);
		builder.addVertex(0.370631F, 0.186327F, 0.886807F, 7.25F, 8F);

		builder.addVertex(0.370631F, 0.186327F, 0.886807F, 7.25F, 8F);
		builder.addVertex(0.163153F, 0.186327F, 0.747335F, 8.25F, 8F);
		builder.addVertex(0.028851F, 0.118887F, 0.947122F, 8.25F, 10F);
		builder.addVertex(0.236329F, 0.118887F, 1.086594F, 7.25F, 10F);

		builder.addVertex(0.226923F, 0.17907F, 1.100587F, 6.25F, 10F);
		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 6.25F, 8F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 7.25F, 8F);
		builder.addVertex(0.019445F, 0.17907F, 0.961114F, 7.25F, 10F);

		builder.addVertex(0.019445F, 0.17907F, 0.961114F, 7.25F, 10F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 7.25F, 8F);
		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 6.25F, 8F);
		builder.addVertex(0.226923F, 0.17907F, 1.100587F, 6.25F, 10F);

		builder.addVertex(0.019445F, 0.17907F, 0.961114F, 7.25F, 10F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 8.25F, 10F);
		builder.addVertex(0.163153F, 0.186327F, 0.747335F, 8.25F, 10.5F);
		builder.addVertex(0.028851F, 0.118887F, 0.947122F, 7.25F, 10.5F);

		builder.addVertex(0.028851F, 0.118887F, 0.947122F, 7.25F, 10.5F);
		builder.addVertex(0.163153F, 0.186327F, 0.747335F, 8.25F, 10.5F);
		builder.addVertex(0.153747F, 0.24651F, 0.761327F, 8.25F, 10F);
		builder.addVertex(0.019445F, 0.17907F, 0.961114F, 7.25F, 10F);

		builder.addVertex(0.226923F, 0.17907F, 1.100587F, 6.25F, 10F);
		builder.addVertex(0.236329F, 0.118887F, 1.086594F, 6.25F, 10.5F);
		builder.addVertex(0.370631F, 0.186327F, 0.886807F, 5.25F, 10.5F);
		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 5.25F, 10F);

		builder.addVertex(0.361225F, 0.24651F, 0.9008F, 5.25F, 10F);
		builder.addVertex(0.370631F, 0.186327F, 0.886807F, 5.25F, 10.5F);
		builder.addVertex(0.236329F, 0.118887F, 1.086594F, 6.25F, 10.5F);
		builder.addVertex(0.226923F, 0.17907F, 1.100587F, 6.25F, 10F);

		//leaf1b
		builder.addVertex(0.172221F, 0.161743F, 0.080802F, 6F, 4F);
		builder.addVertex(0.405819F, 0.161743F, -0.00826F, 7F, 4F);
		builder.addVertex(0.408848F, 0.099824F, -0.000314F, 7F, 4.5F);
		builder.addVertex(0.17525F, 0.099824F, 0.088748F, 6F, 4.5F);

		builder.addVertex(0.17525F, 0.099824F, 0.088748F, 6F, 4.5F);
		builder.addVertex(0.408848F, 0.099824F, -0.000314F, 7F, 4.5F);
		builder.addVertex(0.405819F, 0.161743F, -0.00826F, 7F, 4F);
		builder.addVertex(0.172221F, 0.161743F, 0.080802F, 6F, 4F);

		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 8.75F, 4F);
		builder.addVertex(0.241426F, 0.125336F, 0.262317F, 8.75F, 4.5F);
		builder.addVertex(0.475023F, 0.125336F, 0.173255F, 7.75F, 4.5F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7.75F, 4F);

		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7.75F, 4F);
		builder.addVertex(0.475023F, 0.125336F, 0.173255F, 7.75F, 4.5F);
		builder.addVertex(0.241426F, 0.125336F, 0.262317F, 8.75F, 4.5F);
		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 8.75F, 4F);

		builder.addVertex(0.17525F, 0.099824F, 0.088748F, 7F, 4F);
		builder.addVertex(0.408848F, 0.099824F, -0.000314F, 8F, 4F);
		builder.addVertex(0.475023F, 0.125336F, 0.173255F, 8F, 2.5F);
		builder.addVertex(0.241426F, 0.125336F, 0.262317F, 7F, 2.5F);

		builder.addVertex(0.241426F, 0.125336F, 0.262317F, 7F, 2.5F);
		builder.addVertex(0.475023F, 0.125336F, 0.173255F, 8F, 2.5F);
		builder.addVertex(0.408848F, 0.099824F, -0.000314F, 8F, 4F);
		builder.addVertex(0.17525F, 0.099824F, 0.088748F, 7F, 4F);

		builder.addVertex(0.172221F, 0.161743F, 0.080802F, 6F, 4F);
		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 6F, 2.5F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7F, 2.5F);
		builder.addVertex(0.405819F, 0.161743F, -0.00826F, 7F, 4F);

		builder.addVertex(0.405819F, 0.161743F, -0.00826F, 7F, 4F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7F, 2.5F);
		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 6F, 2.5F);
		builder.addVertex(0.172221F, 0.161743F, 0.080802F, 6F, 4F);

		builder.addVertex(0.405819F, 0.161743F, -0.00826F, 7F, 4F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7.75F, 4F);
		builder.addVertex(0.475023F, 0.125336F, 0.173255F, 7.75F, 4.5F);
		builder.addVertex(0.408848F, 0.099824F, -0.000314F, 7F, 4.5F);

		builder.addVertex(0.408848F, 0.099824F, -0.000314F, 7F, 4.5F);
		builder.addVertex(0.475023F, 0.125336F, 0.173255F, 7.75F, 4.5F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7.75F, 4F);
		builder.addVertex(0.405819F, 0.161743F, -0.00826F, 7F, 4F);

		builder.addVertex(0.172221F, 0.161743F, 0.080802F, 6F, 4F);
		builder.addVertex(0.17525F, 0.099824F, 0.088748F, 6F, 4.5F);
		builder.addVertex(0.241426F, 0.125336F, 0.262317F, 5.25F, 4.5F);
		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 5.25F, 4F);

		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 5.25F, 4F);
		builder.addVertex(0.241426F, 0.125336F, 0.262317F, 5.25F, 4.5F);
		builder.addVertex(0.17525F, 0.099824F, 0.088748F, 6F, 4.5F);
		builder.addVertex(0.172221F, 0.161743F, 0.080802F, 6F, 4F);

		//leaf4
		builder.addVertex(0.481519F, 0.430664F, 0.169444F, 10F, 7F);
		builder.addVertex(0.722251F, 0.430664F, 0.236884F, 11F, 7F);
		builder.addVertex(0.674989F, 0.363867F, 0.405587F, 11F, 5.5F);
		builder.addVertex(0.434257F, 0.363867F, 0.338147F, 10F, 5.5F);

		builder.addVertex(0.434257F, 0.363867F, 0.338147F, 10F, 5.5F);
		builder.addVertex(0.674989F, 0.363867F, 0.405587F, 11F, 5.5F);
		builder.addVertex(0.722251F, 0.430664F, 0.236884F, 11F, 7F);
		builder.addVertex(0.481519F, 0.430664F, 0.169444F, 10F, 7F);

		builder.addVertex(0.481519F, 0.430664F, 0.169444F, 9F, 7F);
		builder.addVertex(0.434257F, 0.363867F, 0.338147F, 9F, 5.5F);
		builder.addVertex(0.674989F, 0.363867F, 0.405587F, 10F, 5.5F);
		builder.addVertex(0.722251F, 0.430664F, 0.236884F, 10F, 7F);

		builder.addVertex(0.722251F, 0.430664F, 0.236884F, 10F, 7F);
		builder.addVertex(0.674989F, 0.363867F, 0.405587F, 10F, 5.5F);
		builder.addVertex(0.434257F, 0.363867F, 0.338147F, 9F, 5.5F);
		builder.addVertex(0.481519F, 0.430664F, 0.169444F, 9F, 7F);

		//leafb
		builder.addVertex(1.138855F, 0.234543F, 0.667824F, 10F, 4.5F);
		builder.addVertex(0.995091F, 0.234543F, 0.945291F, 11.25F, 4.5F);
		builder.addVertex(0.985044F, 0.173075F, 0.940086F, 11.25F, 5F);
		builder.addVertex(1.128808F, 0.173075F, 0.662618F, 10F, 5F);

		builder.addVertex(1.128808F, 0.173075F, 0.662618F, 10F, 5F);
		builder.addVertex(0.985044F, 0.173075F, 0.940086F, 11.25F, 5F);
		builder.addVertex(0.995091F, 0.234543F, 0.945291F, 11.25F, 4.5F);
		builder.addVertex(1.138855F, 0.234543F, 0.667824F, 10F, 4.5F);

		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 13.5F, 4.5F);
		builder.addVertex(0.910502F, 0.218334F, 0.549508F, 13.5F, 5F);
		builder.addVertex(0.766738F, 0.218334F, 0.826975F, 12.25F, 5F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 12.25F, 4.5F);

		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 12.25F, 4.5F);
		builder.addVertex(0.766738F, 0.218334F, 0.826975F, 12.25F, 5F);
		builder.addVertex(0.910502F, 0.218334F, 0.549508F, 13.5F, 5F);
		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 13.5F, 4.5F);

		builder.addVertex(1.128808F, 0.173075F, 0.662618F, 11.25F, 4.5F);
		builder.addVertex(0.985044F, 0.173075F, 0.940086F, 12.5F, 4.5F);
		builder.addVertex(0.766738F, 0.218334F, 0.826975F, 12.5F, 2.5F);
		builder.addVertex(0.910502F, 0.218334F, 0.549508F, 11.25F, 2.5F);

		builder.addVertex(0.910502F, 0.218334F, 0.549508F, 11.25F, 2.5F);
		builder.addVertex(0.766738F, 0.218334F, 0.826975F, 12.5F, 2.5F);
		builder.addVertex(0.985044F, 0.173075F, 0.940086F, 12.5F, 4.5F);
		builder.addVertex(1.128808F, 0.173075F, 0.662618F, 11.25F, 4.5F);

		builder.addVertex(1.138855F, 0.234543F, 0.667824F, 10F, 4.5F);
		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 10F, 2.5F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 11.25F, 2.5F);
		builder.addVertex(0.995091F, 0.234543F, 0.945291F, 11.25F, 4.5F);

		builder.addVertex(0.995091F, 0.234543F, 0.945291F, 11.25F, 4.5F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 11.25F, 2.5F);
		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 10F, 2.5F);
		builder.addVertex(1.138855F, 0.234543F, 0.667824F, 10F, 4.5F);

		builder.addVertex(0.995091F, 0.234543F, 0.945291F, 11.25F, 4.5F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 12.25F, 4.5F);
		builder.addVertex(0.766738F, 0.218334F, 0.826975F, 12.25F, 5F);
		builder.addVertex(0.985044F, 0.173075F, 0.940086F, 11.25F, 5F);

		builder.addVertex(0.985044F, 0.173075F, 0.940086F, 11.25F, 5F);
		builder.addVertex(0.766738F, 0.218334F, 0.826975F, 12.25F, 5F);
		builder.addVertex(0.776784F, 0.279801F, 0.83218F, 12.25F, 4.5F);
		builder.addVertex(0.995091F, 0.234543F, 0.945291F, 11.25F, 4.5F);

		builder.addVertex(1.138855F, 0.234543F, 0.667824F, 10F, 4.5F);
		builder.addVertex(1.128808F, 0.173075F, 0.662618F, 10F, 5F);
		builder.addVertex(0.910502F, 0.218334F, 0.549508F, 9F, 5F);
		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 9F, 4.5F);

		builder.addVertex(0.920548F, 0.279801F, 0.554713F, 9F, 4.5F);
		builder.addVertex(0.910502F, 0.218334F, 0.549508F, 9F, 5F);
		builder.addVertex(1.128808F, 0.173075F, 0.662618F, 10F, 5F);
		builder.addVertex(1.138855F, 0.234543F, 0.667824F, 10F, 4.5F);

		//leaf1
		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 6.25F, 1.5F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7.25F, 1.5F);
		builder.addVertex(0.53326F, 0.112549F, 0.326001F, 7.25F, 0F);
		builder.addVertex(0.299662F, 0.112549F, 0.415063F, 6.25F, 0F);

		builder.addVertex(0.299662F, 0.112549F, 0.415063F, 6.25F, 0F);
		builder.addVertex(0.53326F, 0.112549F, 0.326001F, 7.25F, 0F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 7.25F, 1.5F);
		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 6.25F, 1.5F);

		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 5.25F, 1.5F);
		builder.addVertex(0.299662F, 0.112549F, 0.415063F, 5.25F, 0F);
		builder.addVertex(0.53326F, 0.112549F, 0.326001F, 6.25F, 0F);
		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 6.25F, 1.5F);

		builder.addVertex(0.471994F, 0.187254F, 0.165309F, 6.25F, 1.5F);
		builder.addVertex(0.53326F, 0.112549F, 0.326001F, 6.25F, 0F);
		builder.addVertex(0.299662F, 0.112549F, 0.415063F, 5.25F, 0F);
		builder.addVertex(0.238396F, 0.187255F, 0.254371F, 5.25F, 1.5F);

	}
}
