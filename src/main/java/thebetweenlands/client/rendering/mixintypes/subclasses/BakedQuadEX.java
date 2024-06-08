package thebetweenlands.client.rendering.mixintypes.subclasses;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

// Extended BakedQuad
public class BakedQuadEX extends BakedQuad {

    public final boolean flatShade;
    public final Direction shadeSide;

    public BakedQuadEX(int[] p_111298_, int p_111299_, Direction p_111300_, TextureAtlasSprite p_111301_, boolean p_111302_, boolean flatShade, Direction shadeSide) {
        super(p_111298_, p_111299_, p_111300_, p_111301_, p_111302_);
        this.flatShade = flatShade;
        this.shadeSide = shadeSide;
    }

    // convert BakedQuad to EX
    public BakedQuadEX(BakedQuad quad, boolean flatShade, Direction shadeSide) {
        super(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isTinted());
        this.flatShade = flatShade;
        this.shadeSide = shadeSide;
    }
}
