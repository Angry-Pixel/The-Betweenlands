package thebetweenlands.client.model.baked.paperlantern;

import thebetweenlands.util.QuadBuilder;

/**
 * BLLanternSiltGlass - TripleHeadedSheep
 * Created using Tabula 7.0.1
 * Procedurally generated geometry from the 1.12.2 Tabula
 */
public final class PaperLanternGeometry {

	private PaperLanternGeometry() {
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

	public static void build(QuadBuilder builder) {
		//lamp_base
		builder.addVertex(0.34375F, 0.375F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.65625F, 0.375F, 0.34375F, 5F, 2.5F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 5F, 5.5F);
		builder.addVertex(0.34375F, 0F, 0.34375F, 2.5F, 5.5F);

		builder.addVertex(0.34375F, 0.375F, 0.65625F, 10F, 2.5F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 10F, 5.5F);
		builder.addVertex(0.65625F, 0F, 0.65625F, 7.5F, 5.5F);
		builder.addVertex(0.65625F, 0.375F, 0.65625F, 7.5F, 2.5F);

		builder.addVertex(0.34375F, 0F, 0.34375F, 5F, 2.5F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 7.5F, 2.5F);
		builder.addVertex(0.65625F, 0F, 0.65625F, 7.5F, 0F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 5F, 0F);

		builder.addVertex(0.34375F, 0.375F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.34375F, 0.375F, 0.65625F, 2.5F, 0F);
		builder.addVertex(0.65625F, 0.375F, 0.65625F, 5F, 0F);
		builder.addVertex(0.65625F, 0.375F, 0.34375F, 5F, 2.5F);

		builder.addVertex(0.65625F, 0.375F, 0.34375F, 5F, 2.5F);
		builder.addVertex(0.65625F, 0.375F, 0.65625F, 7.5F, 2.5F);
		builder.addVertex(0.65625F, 0F, 0.65625F, 7.5F, 5.5F);
		builder.addVertex(0.65625F, -0F, 0.34375F, 5F, 5.5F);

		builder.addVertex(0.34375F, 0.375F, 0.34375F, 2.5F, 2.5F);
		builder.addVertex(0.34375F, 0F, 0.34375F, 2.5F, 5.5F);
		builder.addVertex(0.34375F, 0F, 0.65625F, 0F, 5.5F);
		builder.addVertex(0.34375F, 0.375F, 0.65625F, 0F, 2.5F);

		//top_mid
		builder.addVertex(0.375F, 0.5F, 0.3125F, 3F, 9F);
		builder.addVertex(0.625F, 0.5F, 0.3125F, 5F, 9F);
		builder.addVertex(0.625F, 0.375F, 0.3125F, 5F, 10F);
		builder.addVertex(0.375F, 0.375F, 0.3125F, 3F, 10F);

		builder.addVertex(0.375F, 0.5F, 0.6875F, 10F, 9F);
		builder.addVertex(0.375F, 0.375F, 0.6875F, 10F, 10F);
		builder.addVertex(0.625F, 0.375F, 0.6875F, 8F, 10F);
		builder.addVertex(0.625F, 0.5F, 0.6875F, 8F, 9F);

		builder.addVertex(0.375F, 0.375F, 0.3125F, 5F, 9F);
		builder.addVertex(0.625F, 0.375F, 0.3125F, 7F, 9F);
		builder.addVertex(0.625F, 0.375F, 0.6875F, 7F, 6F);
		builder.addVertex(0.375F, 0.375F, 0.6875F, 5F, 6F);

		builder.addVertex(0.375F, 0.5F, 0.3125F, 3F, 9F);
		builder.addVertex(0.375F, 0.5F, 0.6875F, 3F, 6F);
		builder.addVertex(0.625F, 0.5F, 0.6875F, 5F, 6F);
		builder.addVertex(0.625F, 0.5F, 0.3125F, 5F, 9F);

		builder.addVertex(0.625F, 0.5F, 0.3125F, 5F, 9F);
		builder.addVertex(0.625F, 0.5F, 0.6875F, 8F, 9F);
		builder.addVertex(0.625F, 0.375F, 0.6875F, 8F, 10F);
		builder.addVertex(0.625F, 0.375F, 0.3125F, 5F, 10F);

		builder.addVertex(0.375F, 0.5F, 0.3125F, 3F, 9F);
		builder.addVertex(0.375F, 0.375F, 0.3125F, 3F, 10F);
		builder.addVertex(0.375F, 0.375F, 0.6875F, 0F, 10F);
		builder.addVertex(0.375F, 0.5F, 0.6875F, 0F, 9F);

		//top_connection
		builder.addVertex(0.40625F, 0.5625F, 0.40625F, 11F, 1.5F);
		builder.addVertex(0.59375F, 0.5625F, 0.40625F, 12.5F, 1.5F);
		builder.addVertex(0.59375F, 0.5F, 0.40625F, 12.5F, 2F);
		builder.addVertex(0.40625F, 0.5F, 0.40625F, 11F, 2F);

		builder.addVertex(0.40625F, 0.5625F, 0.59375F, 15.5F, 1.5F);
		builder.addVertex(0.40625F, 0.5F, 0.59375F, 15.5F, 2F);
		builder.addVertex(0.59375F, 0.5F, 0.59375F, 14F, 2F);
		builder.addVertex(0.59375F, 0.5625F, 0.59375F, 14F, 1.5F);

		builder.addVertex(0.40625F, 0.5F, 0.40625F, 12.5F, 1.5F);
		builder.addVertex(0.59375F, 0.5F, 0.40625F, 14F, 1.5F);
		builder.addVertex(0.59375F, 0.5F, 0.59375F, 14F, 0F);
		builder.addVertex(0.40625F, 0.5F, 0.59375F, 12.5F, 0F);

		builder.addVertex(0.40625F, 0.5625F, 0.40625F, 11F, 1.5F);
		builder.addVertex(0.40625F, 0.5625F, 0.59375F, 11F, 0F);
		builder.addVertex(0.59375F, 0.5625F, 0.59375F, 12.5F, 0F);
		builder.addVertex(0.59375F, 0.5625F, 0.40625F, 12.5F, 1.5F);

		builder.addVertex(0.59375F, 0.5625F, 0.40625F, 12.5F, 1.5F);
		builder.addVertex(0.59375F, 0.5625F, 0.59375F, 14F, 1.5F);
		builder.addVertex(0.59375F, 0.5F, 0.59375F, 14F, 2F);
		builder.addVertex(0.59375F, 0.5F, 0.40625F, 12.5F, 2F);

		builder.addVertex(0.40625F, 0.5625F, 0.40625F, 11F, 1.5F);
		builder.addVertex(0.40625F, 0.5F, 0.40625F, 11F, 2F);
		builder.addVertex(0.40625F, 0.5F, 0.59375F, 9.5F, 2F);
		builder.addVertex(0.40625F, 0.5625F, 0.59375F, 9.5F, 1.5F);

		//top_left
		builder.addVertex(0.625F, 0.5F, 0.313125F, 3F, 13.5F);
		builder.addVertex(0.737263F, 0.445027F, 0.313125F, 4F, 13.5F);
		builder.addVertex(0.68229F, 0.332764F, 0.313125F, 4F, 14.5F);
		builder.addVertex(0.570027F, 0.387737F, 0.313125F, 3F, 14.5F);

		builder.addVertex(0.625F, 0.5F, 0.688125F, 8F, 13.5F);
		builder.addVertex(0.570027F, 0.387737F, 0.688125F, 8F, 14.5F);
		builder.addVertex(0.68229F, 0.332764F, 0.688125F, 7F, 14.5F);
		builder.addVertex(0.737263F, 0.445027F, 0.688125F, 7F, 13.5F);

		builder.addVertex(0.570027F, 0.387737F, 0.313125F, 4F, 13.5F);
		builder.addVertex(0.68229F, 0.332764F, 0.313125F, 5F, 13.5F);
		builder.addVertex(0.68229F, 0.332764F, 0.688125F, 5F, 10.5F);
		builder.addVertex(0.570027F, 0.387737F, 0.688125F, 4F, 10.5F);

		builder.addVertex(0.625F, 0.5F, 0.313125F, 3F, 13.5F);
		builder.addVertex(0.625F, 0.5F, 0.688125F, 3F, 10.5F);
		builder.addVertex(0.737263F, 0.445027F, 0.688125F, 4F, 10.5F);
		builder.addVertex(0.737263F, 0.445027F, 0.313125F, 4F, 13.5F);

		builder.addVertex(0.737263F, 0.445027F, 0.313125F, 4F, 13.5F);
		builder.addVertex(0.737263F, 0.445027F, 0.688125F, 7F, 13.5F);
		builder.addVertex(0.68229F, 0.332764F, 0.688125F, 7F, 14.5F);
		builder.addVertex(0.68229F, 0.332764F, 0.313125F, 4F, 14.5F);

		builder.addVertex(0.625F, 0.5F, 0.313125F, 3F, 13.5F);
		builder.addVertex(0.570027F, 0.387737F, 0.313125F, 3F, 14.5F);
		builder.addVertex(0.570027F, 0.387737F, 0.688125F, 0F, 14.5F);
		builder.addVertex(0.625F, 0.5F, 0.688125F, 0F, 13.5F);

		//top_back
		builder.addVertex(0.262737F, 0.445027F, 0.313125F, 11F, 12F);
		builder.addVertex(0.375F, 0.5F, 0.313125F, 12F, 12F);
		builder.addVertex(0.429973F, 0.387737F, 0.313125F, 12F, 13F);
		builder.addVertex(0.31771F, 0.332764F, 0.313125F, 11F, 13F);

		builder.addVertex(0.262737F, 0.445027F, 0.688125F, 16F, 12F);
		builder.addVertex(0.31771F, 0.332764F, 0.688125F, 16F, 13F);
		builder.addVertex(0.429973F, 0.387737F, 0.688125F, 15F, 13F);
		builder.addVertex(0.375F, 0.5F, 0.688125F, 15F, 12F);

		builder.addVertex(0.31771F, 0.332764F, 0.313125F, 12F, 12F);
		builder.addVertex(0.429973F, 0.387737F, 0.313125F, 13F, 12F);
		builder.addVertex(0.429973F, 0.387737F, 0.688125F, 13F, 9F);
		builder.addVertex(0.31771F, 0.332764F, 0.688125F, 12F, 9F);

		builder.addVertex(0.262737F, 0.445027F, 0.313125F, 11F, 12F);
		builder.addVertex(0.262737F, 0.445027F, 0.688125F, 11F, 9F);
		builder.addVertex(0.375F, 0.5F, 0.688125F, 12F, 9F);
		builder.addVertex(0.375F, 0.5F, 0.313125F, 12F, 12F);

		builder.addVertex(0.375F, 0.5F, 0.313125F, 12F, 12F);
		builder.addVertex(0.375F, 0.5F, 0.688125F, 15F, 12F);
		builder.addVertex(0.429973F, 0.387737F, 0.688125F, 15F, 13F);
		builder.addVertex(0.429973F, 0.387737F, 0.313125F, 12F, 13F);

		builder.addVertex(0.262737F, 0.445027F, 0.313125F, 11F, 12F);
		builder.addVertex(0.31771F, 0.332764F, 0.313125F, 11F, 13F);
		builder.addVertex(0.31771F, 0.332764F, 0.688125F, 8F, 13F);
		builder.addVertex(0.262737F, 0.445027F, 0.688125F, 8F, 12F);

	}
}
