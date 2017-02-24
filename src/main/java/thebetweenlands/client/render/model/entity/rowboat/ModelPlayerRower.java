package thebetweenlands.client.render.model.entity.rowboat;

public class ModelPlayerRower extends ModelBipedRower {
    private boolean smallArms;

    public ModelPlayerRower(float expand, boolean slimArms) {
        super(expand, 64, 64);
        this.smallArms = slimArms;
    }
}