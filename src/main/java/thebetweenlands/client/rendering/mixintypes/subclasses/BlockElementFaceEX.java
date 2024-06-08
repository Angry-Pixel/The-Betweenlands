package thebetweenlands.client.rendering.mixintypes.subclasses;

import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

// Extended BlockElementFace
public class BlockElementFaceEX extends BlockElementFace {
    public final boolean flatshade;
    public final Direction overrideLightingSide;

    public BlockElementFaceEX(@Nullable Direction p_111359_, int p_111360_, String p_111361_, BlockFaceUV p_111362_, boolean flatshade, Direction overrideLightingSide) {
        super(p_111359_, p_111360_, p_111361_, p_111362_);
        this.flatshade = flatshade;
        this.overrideLightingSide = overrideLightingSide;
    }

    // convert BlockElementFace to EX
    public BlockElementFaceEX(BlockElementFace face, boolean flatshade, Direction overrideLightingSide) {
        super(face.cullForDirection, face.tintIndex, face.texture, face.uv);
        this.flatshade = flatshade;
        this.overrideLightingSide = overrideLightingSide;
    }
}
