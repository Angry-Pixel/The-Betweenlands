package thebetweenlands.client.model.baked.siltglasslantern;

import thebetweenlands.util.QuadBuilder;

/**
 * BLLanternSiltGlass - TripleHeadedSheep
 * Created using Tabula 7.0.1
 * Procedurally generated geometry from the 1.12.2 Tabula
 */
public final class SiltGlassLanternGeometry {

	private SiltGlassLanternGeometry() {
	}

	public static void buildRope(QuadBuilder builder) {
		//rope
		final float min = 7F / 16F;   // 0.4375
		final float max = 9F / 16F;   // 0.5625
		final float yBot = 8F / 16F;
		final float yTop = 1.0F;
		final float uMin = 7F;
		final float uMax = 9F;
		final float vTop = 0F;
		final float vBot = 8F;

		// east 
		builder.addVertex(min, yBot, min, uMin, vBot);
		builder.addVertex(min, yBot, max, uMax, vBot);
		builder.addVertex(min, yTop, max, uMax, vTop);
		builder.addVertex(min, yTop, min, uMin, vTop);

		// west 
		builder.addVertex(max, yBot, max, uMin, vBot);
		builder.addVertex(max, yBot, min, uMax, vBot);
		builder.addVertex(max, yTop, min, uMax, vTop);
		builder.addVertex(max, yTop, max, uMin, vTop);

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

	}

	public static void buildInsides(QuadBuilder builder) {
		//lamp_base
		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);
		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, 0.125F, 0.34375F, 2.5F, 5.5F);
		builder.addVertex(0.34375F, 0.125F, 0.34375F, 1.25F, 5.5F);

		builder.addVertex(0.34375F, 0.5F, 0.65625F, 5F, 2.5F);
		builder.addVertex(0.34375F, 0.125F, 0.65625F, 5F, 5.5F);
		builder.addVertex(0.65625F, 0.125F, 0.65625F, 3.75F, 5.5F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 3.75F, 2.5F);

		builder.addVertex(0.34375F, 0.125F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, 0.125F, 0.34375F, 3.75F, 2.5F);
		builder.addVertex(0.65625F, 0.125F, 0.65625F, 3.75F, 0F);
		builder.addVertex(0.34375F, 0.125F, 0.65625F, 2.5F, 0F);

		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);
		builder.addVertex(0.34375F, 0.5F, 0.65625F, 1.25F, 0F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 2.5F, 0F);
		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);

		builder.addVertex(0.65625F, 0.5F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, 0.5F, 0.65625F, 3.75F, 2.5F);
		builder.addVertex(0.65625F, 0.125F, 0.65625F, 3.75F, 5.5F);
		builder.addVertex(0.65625F, 0.125F, 0.34375F, 2.5F, 5.5F);

		builder.addVertex(0.34375F, 0.5F, 0.34375F, 1.25F, 2.5F);
		builder.addVertex(0.34375F, 0.125F, 0.34375F, 1.25F, 5.5F);
		builder.addVertex(0.34375F, 0.125F, 0.65625F, 0F, 5.5F);
		builder.addVertex(0.34375F, 0.5F, 0.65625F, 0F, 2.5F);
	}

	public static void build(QuadBuilder builder) {
		//top_right
		builder.addVertex(0.256294F, 0.585834F, 0.313125F, 13F, 11.5F);
		builder.addVertex(0.375F, 0.625F, 0.313125F, 13.5F, 11.5F);
		builder.addVertex(0.414166F, 0.506294F, 0.313125F, 13.5F, 12.5F);
		builder.addVertex(0.295461F, 0.467128F, 0.313125F, 13F, 12.5F);

		builder.addVertex(0.256294F, 0.585834F, 0.688125F, 15.5F, 11.5F);
		builder.addVertex(0.295461F, 0.467128F, 0.688125F, 15.5F, 12.5F);
		builder.addVertex(0.414166F, 0.506294F, 0.688125F, 15F, 12.5F);
		builder.addVertex(0.375F, 0.625F, 0.688125F, 15F, 11.5F);

		builder.addVertex(0.295461F, 0.467128F, 0.313125F, 13.5F, 11.5F);
		builder.addVertex(0.414166F, 0.506294F, 0.313125F, 14F, 11.5F);
		builder.addVertex(0.414166F, 0.506294F, 0.688125F, 14F, 8.5F);
		builder.addVertex(0.295461F, 0.467128F, 0.688125F, 13.5F, 8.5F);

		builder.addVertex(0.256294F, 0.585834F, 0.313125F, 13F, 11.5F);
		builder.addVertex(0.256294F, 0.585834F, 0.688125F, 13F, 8.5F);
		builder.addVertex(0.375F, 0.625F, 0.688125F, 13.5F, 8.5F);
		builder.addVertex(0.375F, 0.625F, 0.313125F, 13.5F, 11.5F);

		builder.addVertex(0.375F, 0.625F, 0.313125F, 13.5F, 11.5F);
		builder.addVertex(0.375F, 0.625F, 0.688125F, 15F, 11.5F);
		builder.addVertex(0.414166F, 0.506294F, 0.688125F, 15F, 12.5F);
		builder.addVertex(0.414166F, 0.506294F, 0.313125F, 13.5F, 12.5F);

		builder.addVertex(0.256294F, 0.585834F, 0.313125F, 13F, 11.5F);
		builder.addVertex(0.295461F, 0.467128F, 0.313125F, 13F, 12.5F);
		builder.addVertex(0.295461F, 0.467128F, 0.688125F, 11.5F, 12.5F);
		builder.addVertex(0.256294F, 0.585834F, 0.688125F, 11.5F, 11.5F);

		//bottom_left
		builder.addVertex(0.570027F, 0.112263F, 0.313125F, 1.5F, 13.5F);
		builder.addVertex(0.68229F, 0.167236F, 0.313125F, 2F, 13.5F);
		builder.addVertex(0.737263F, 0.054973F, 0.313125F, 2F, 14.5F);
		builder.addVertex(0.625F, -0F, 0.313125F, 1.5F, 14.5F);

		builder.addVertex(0.570027F, 0.112263F, 0.688125F, 4F, 13.5F);
		builder.addVertex(0.625F, 0F, 0.688125F, 4F, 14.5F);
		builder.addVertex(0.737263F, 0.054973F, 0.688125F, 3.5F, 14.5F);
		builder.addVertex(0.68229F, 0.167236F, 0.688125F, 3.5F, 13.5F);

		builder.addVertex(0.625F, -0F, 0.313125F, 2F, 13.5F);
		builder.addVertex(0.737263F, 0.054973F, 0.313125F, 2.5F, 13.5F);
		builder.addVertex(0.737263F, 0.054973F, 0.688125F, 2.5F, 10.5F);
		builder.addVertex(0.625F, 0F, 0.688125F, 2F, 10.5F);

		builder.addVertex(0.570027F, 0.112263F, 0.313125F, 1.5F, 13.5F);
		builder.addVertex(0.570027F, 0.112263F, 0.688125F, 1.5F, 10.5F);
		builder.addVertex(0.68229F, 0.167236F, 0.688125F, 2F, 10.5F);
		builder.addVertex(0.68229F, 0.167236F, 0.313125F, 2F, 13.5F);

		builder.addVertex(0.68229F, 0.167236F, 0.313125F, 2F, 13.5F);
		builder.addVertex(0.68229F, 0.167236F, 0.688125F, 3.5F, 13.5F);
		builder.addVertex(0.737263F, 0.054973F, 0.688125F, 3.5F, 14.5F);
		builder.addVertex(0.737263F, 0.054973F, 0.313125F, 2F, 14.5F);

		builder.addVertex(0.570027F, 0.112263F, 0.313125F, 1.5F, 13.5F);
		builder.addVertex(0.625F, -0F, 0.313125F, 1.5F, 14.5F);
		builder.addVertex(0.625F, 0F, 0.688125F, 0F, 14.5F);
		builder.addVertex(0.570027F, 0.112263F, 0.688125F, 0F, 13.5F);

		//bottom_right
		builder.addVertex(0.31771F, 0.167236F, 0.313125F, 5.75F, 13.5F);
		builder.addVertex(0.429973F, 0.112263F, 0.313125F, 6.25F, 13.5F);
		builder.addVertex(0.375F, -0F, 0.313125F, 6.25F, 14.5F);
		builder.addVertex(0.262737F, 0.054973F, 0.313125F, 5.75F, 14.5F);

		builder.addVertex(0.31771F, 0.167236F, 0.688125F, 8.25F, 13.5F);
		builder.addVertex(0.262737F, 0.054973F, 0.688125F, 8.25F, 14.5F);
		builder.addVertex(0.375F, 0F, 0.688125F, 7.75F, 14.5F);
		builder.addVertex(0.429973F, 0.112263F, 0.688125F, 7.75F, 13.5F);

		builder.addVertex(0.262737F, 0.054973F, 0.313125F, 6.25F, 13.5F);
		builder.addVertex(0.375F, -0F, 0.313125F, 6.75F, 13.5F);
		builder.addVertex(0.375F, 0F, 0.688125F, 6.75F, 10.5F);
		builder.addVertex(0.262737F, 0.054973F, 0.688125F, 6.25F, 10.5F);

		builder.addVertex(0.31771F, 0.167236F, 0.313125F, 5.75F, 13.5F);
		builder.addVertex(0.31771F, 0.167236F, 0.688125F, 5.75F, 10.5F);
		builder.addVertex(0.429973F, 0.112263F, 0.688125F, 6.25F, 10.5F);
		builder.addVertex(0.429973F, 0.112263F, 0.313125F, 6.25F, 13.5F);

		builder.addVertex(0.429973F, 0.112263F, 0.313125F, 6.25F, 13.5F);
		builder.addVertex(0.429973F, 0.112263F, 0.688125F, 7.75F, 13.5F);
		builder.addVertex(0.375F, 0F, 0.688125F, 7.75F, 14.5F);
		builder.addVertex(0.375F, -0F, 0.313125F, 6.25F, 14.5F);

		builder.addVertex(0.31771F, 0.167236F, 0.313125F, 5.75F, 13.5F);
		builder.addVertex(0.262737F, 0.054973F, 0.313125F, 5.75F, 14.5F);
		builder.addVertex(0.262737F, 0.054973F, 0.688125F, 4.25F, 14.5F);
		builder.addVertex(0.31771F, 0.167236F, 0.688125F, 4.25F, 13.5F);

		//handle
		builder.addVertex(0.375F, 0.75297F, 0.36296F, 4.25F, 0F);
		builder.addVertex(0.625F, 0.75297F, 0.36296F, 5.25F, 0F);
		builder.addVertex(0.625F, 0.625F, 0.5F, 5.25F, 1.5F);
		builder.addVertex(0.375F, 0.625F, 0.5F, 4.25F, 1.5F);

		builder.addVertex(0.375F, 0.75297F, 0.36296F, 6.25F, 0F);
		builder.addVertex(0.375F, 0.625F, 0.5F, 6.25F, 1.5F);
		builder.addVertex(0.625F, 0.625F, 0.5F, 5.25F, 1.5F);
		builder.addVertex(0.625F, 0.75297F, 0.36296F, 5.25F, 0F);

		//top_mid
		builder.addVertex(0.375F, 0.625F, 0.3125F, 6.75F, 9F);
		builder.addVertex(0.625F, 0.625F, 0.3125F, 7.75F, 9F);
		builder.addVertex(0.625F, 0.5F, 0.3125F, 7.75F, 10F);
		builder.addVertex(0.375F, 0.5F, 0.3125F, 6.75F, 10F);

		builder.addVertex(0.375F, 0.625F, 0.6875F, 10.25F, 9F);
		builder.addVertex(0.375F, 0.5F, 0.6875F, 10.25F, 10F);
		builder.addVertex(0.625F, 0.5F, 0.6875F, 9.25F, 10F);
		builder.addVertex(0.625F, 0.625F, 0.6875F, 9.25F, 9F);

		builder.addVertex(0.375F, 0.5F, 0.3125F, 7.75F, 9F);
		builder.addVertex(0.625F, 0.5F, 0.3125F, 8.75F, 9F);
		builder.addVertex(0.625F, 0.5F, 0.6875F, 8.75F, 6F);
		builder.addVertex(0.375F, 0.5F, 0.6875F, 7.75F, 6F);

		builder.addVertex(0.375F, 0.625F, 0.3125F, 6.75F, 9F);
		builder.addVertex(0.375F, 0.625F, 0.6875F, 6.75F, 6F);
		builder.addVertex(0.625F, 0.625F, 0.6875F, 7.75F, 6F);
		builder.addVertex(0.625F, 0.625F, 0.3125F, 7.75F, 9F);

		builder.addVertex(0.625F, 0.625F, 0.3125F, 7.75F, 9F);
		builder.addVertex(0.625F, 0.625F, 0.6875F, 9.25F, 9F);
		builder.addVertex(0.625F, 0.5F, 0.6875F, 9.25F, 10F);
		builder.addVertex(0.625F, 0.5F, 0.3125F, 7.75F, 10F);

		builder.addVertex(0.375F, 0.625F, 0.3125F, 6.75F, 9F);
		builder.addVertex(0.375F, 0.5F, 0.3125F, 6.75F, 10F);
		builder.addVertex(0.375F, 0.5F, 0.6875F, 5.25F, 10F);
		builder.addVertex(0.375F, 0.625F, 0.6875F, 5.25F, 9F);

		//top_left
		builder.addVertex(0.625F, 0.625F, 0.313125F, 10F, 13.5F);
		builder.addVertex(0.743706F, 0.585834F, 0.313125F, 10.5F, 13.5F);
		builder.addVertex(0.704539F, 0.467128F, 0.313125F, 10.5F, 14.5F);
		builder.addVertex(0.585834F, 0.506294F, 0.313125F, 10F, 14.5F);

		builder.addVertex(0.625F, 0.625F, 0.688125F, 12.5F, 13.5F);
		builder.addVertex(0.585834F, 0.506294F, 0.688125F, 12.5F, 14.5F);
		builder.addVertex(0.704539F, 0.467128F, 0.688125F, 12F, 14.5F);
		builder.addVertex(0.743706F, 0.585834F, 0.688125F, 12F, 13.5F);

		builder.addVertex(0.585834F, 0.506294F, 0.313125F, 10.5F, 13.5F);
		builder.addVertex(0.704539F, 0.467128F, 0.313125F, 11F, 13.5F);
		builder.addVertex(0.704539F, 0.467128F, 0.688125F, 11F, 10.5F);
		builder.addVertex(0.585834F, 0.506294F, 0.688125F, 10.5F, 10.5F);

		builder.addVertex(0.625F, 0.625F, 0.313125F, 10F, 13.5F);
		builder.addVertex(0.625F, 0.625F, 0.688125F, 10F, 10.5F);
		builder.addVertex(0.743706F, 0.585834F, 0.688125F, 10.5F, 10.5F);
		builder.addVertex(0.743706F, 0.585834F, 0.313125F, 10.5F, 13.5F);

		builder.addVertex(0.743706F, 0.585834F, 0.313125F, 10.5F, 13.5F);
		builder.addVertex(0.743706F, 0.585834F, 0.688125F, 12F, 13.5F);
		builder.addVertex(0.704539F, 0.467128F, 0.688125F, 12F, 14.5F);
		builder.addVertex(0.704539F, 0.467128F, 0.313125F, 10.5F, 14.5F);

		builder.addVertex(0.625F, 0.625F, 0.313125F, 10F, 13.5F);
		builder.addVertex(0.585834F, 0.506294F, 0.313125F, 10F, 14.5F);
		builder.addVertex(0.585834F, 0.506294F, 0.688125F, 8.5F, 14.5F);
		builder.addVertex(0.625F, 0.625F, 0.688125F, 8.5F, 13.5F);

		//bottom_mid
		builder.addVertex(0.375F, 0.125F, 0.3125F, 1.5F, 9F);
		builder.addVertex(0.625F, 0.125F, 0.3125F, 2.5F, 9F);
		builder.addVertex(0.625F, -0F, 0.3125F, 2.5F, 10F);
		builder.addVertex(0.375F, -0F, 0.3125F, 1.5F, 10F);

		builder.addVertex(0.375F, 0.125F, 0.6875F, 5F, 9F);
		builder.addVertex(0.375F, 0F, 0.6875F, 5F, 10F);
		builder.addVertex(0.625F, 0F, 0.6875F, 4F, 10F);
		builder.addVertex(0.625F, 0.125F, 0.6875F, 4F, 9F);

		builder.addVertex(0.375F, -0F, 0.3125F, 2.5F, 9F);
		builder.addVertex(0.625F, -0F, 0.3125F, 3.5F, 9F);
		builder.addVertex(0.625F, 0F, 0.6875F, 3.5F, 6F);
		builder.addVertex(0.375F, 0F, 0.6875F, 2.5F, 6F);

		builder.addVertex(0.375F, 0.125F, 0.3125F, 1.5F, 9F);
		builder.addVertex(0.375F, 0.125F, 0.6875F, 1.5F, 6F);
		builder.addVertex(0.625F, 0.125F, 0.6875F, 2.5F, 6F);
		builder.addVertex(0.625F, 0.125F, 0.3125F, 2.5F, 9F);

		builder.addVertex(0.625F, 0.125F, 0.3125F, 2.5F, 9F);
		builder.addVertex(0.625F, 0.125F, 0.6875F, 4F, 9F);
		builder.addVertex(0.625F, 0F, 0.6875F, 4F, 10F);
		builder.addVertex(0.625F, -0F, 0.3125F, 2.5F, 10F);

		builder.addVertex(0.375F, 0.125F, 0.3125F, 1.5F, 9F);
		builder.addVertex(0.375F, -0F, 0.3125F, 1.5F, 10F);
		builder.addVertex(0.375F, 0F, 0.6875F, 0F, 10F);
		builder.addVertex(0.375F, 0.125F, 0.6875F, 0F, 9F);

	}
}
