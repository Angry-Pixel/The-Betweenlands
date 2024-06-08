package thebetweenlands.common.dimension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

public class BetweenlandsDimensionType extends DimensionType {

    /**
     * @param p_204471_
     * @param p_204472_
     * @param p_204473_
     * @param p_204474_
     * @param p_204475_
     * @param p_204476_
     * @param p_204477_
     * @param p_204478_
     * @param p_204479_
     * @param p_204480_
     * @param p_204481_
     * @param p_204482_
     * @param p_204483_
     * @param p_204484_
     * @param p_204485_
     * @param p_204486_
     * @param ambient
     * @deprecated
     */
    public BetweenlandsDimensionType(OptionalLong p_204471_, boolean p_204472_, boolean p_204473_, boolean p_204474_, boolean p_204475_, double p_204476_, boolean p_204477_, boolean p_204478_, boolean p_204479_, boolean p_204480_, boolean p_204481_, int p_204482_, int p_204483_, int p_204484_, TagKey<Block> p_204485_, ResourceLocation p_204486_, float ambient) {
        super(p_204471_, p_204472_, p_204473_, p_204474_, p_204475_, p_204476_, p_204477_, p_204478_, p_204479_, p_204480_, p_204481_, p_204482_, p_204483_, p_204484_, p_204485_, p_204486_, ambient);
    }
}
