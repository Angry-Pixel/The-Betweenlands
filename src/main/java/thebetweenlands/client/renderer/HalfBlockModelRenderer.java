package thebetweenlands.client.renderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HalfBlockModelRenderer {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final BlockState DUMMY_STATE = Blocks.AIR.defaultBlockState();

    public static List<BakedQuad> getHalfQuads(BakedModel fullModel, boolean topHalf, float splitYInPixels) {
        List<BakedQuad> filteredQuads = new ArrayList<>();
        Set<BakedQuad> uniqueQuads = new HashSet<>();
        float splitYNormalized = splitYInPixels / 16.0f;
        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        directions.add(null);

        for (Direction dir : directions) {
            List<BakedQuad> quads = fullModel.getQuads(DUMMY_STATE, dir, RANDOM, ModelData.EMPTY, null);
            for (BakedQuad quad : quads) {
                if (!uniqueQuads.add(quad))
                    continue;

                int[] vertices = quad.getVertices();
                float y0 = Float.intBitsToFloat(vertices[1]);
                float y1 = Float.intBitsToFloat(vertices[9]);
                float y2 = Float.intBitsToFloat(vertices[17]);
                float y3 = Float.intBitsToFloat(vertices[25]);
                float midpointY = (y0 + y1 + y2 + y3) / 4.0f;

                if (topHalf && midpointY >= splitYNormalized)
                    filteredQuads.add(quad);
                else if (!topHalf && midpointY < splitYNormalized)
                    filteredQuads.add(quad);
            }
        }
        return filteredQuads;
    }
}
