package thebetweenlands.mods;

import thebetweenlands.lib.ModInfo;

import java.util.Arrays;

import static thebetweenlands.mods.RecurrentComplexModule.DimensionDictionary.*;

/**
 * Created by lukas on 09.09.15.
 */
public class RecurrentComplexModule
{
    public static void init()
    {
        if (RecurrentComplex.isLoaded())
        {
            RecurrentComplex.registerDimensionInDictionary(ModInfo.DIMENSION_ID, Arrays.asList(REAL, NO_TOP_LIMIT, BOTTOM_LIMIT, INFINITE, PLANET_SURFACE, BETWEENLANDS, ALWAYS_NIGHT));
        }
    }

    public static class DimensionDictionary
    {
        // Default Types ----------------

        /**
         * Dimensions that are deemed to be situated in physical reality.
         */
        public static final String REAL = "REAL";

        /**
         * Dimensions without a top limitation (e.g. bedrock), conveying that there is no terrain (void) beyond.
         */
        public static final String NO_TOP_LIMIT = "NO_TOP_LIMIT";
        /**
         * Dimensions with a bottom limitation (e.g. bedrock), conveying that there is terrain beyond.
         */
        public static final String BOTTOM_LIMIT = "BOTTOM_LIMIT";

        /**
         * Dimensions that are not limited in space, e.g. any dynamically generated world.
         */
        public static final String INFINITE = "INFINITE";

        /**
         * Dimensions that are deemed to be on the surface of a planet.
         */
        public static final String PLANET_SURFACE = "PLANET_SURFACE";


        // Custom Types -----------------

        public static final String BETWEENLANDS = "BETWEENLANDS";
        public static final String ALWAYS_NIGHT = "ALWAYS_NIGHT";
    }
}
