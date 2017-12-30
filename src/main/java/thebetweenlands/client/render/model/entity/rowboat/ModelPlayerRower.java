package thebetweenlands.client.render.model.entity.rowboat;

public class ModelPlayerRower extends ModelBipedRower {
    public ModelPlayerRower(float expand, boolean slimArms, BipedTextureUVs limbUVs) {
        this(expand, false, slimArms, limbUVs);
    }
    public ModelPlayerRower(float expand, boolean expandJointed, boolean slimArms, BipedTextureUVs limbUVs) {
        super(expand, expandJointed, slimArms,  64, 64, limbUVs);
    }
}