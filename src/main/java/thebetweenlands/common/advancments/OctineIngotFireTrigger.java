package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class OctineIngotFireTrigger extends BLTrigger<OctineIngotFireTrigger.Instance, OctineIngotFireTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "octine_ingot_fire");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public OctineIngotFireTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new Listener(playerAdvancements);
    }

    @Override
    public OctineIngotFireTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new OctineIngotFireTrigger.Instance();
    }

    public void trigger(EntityPlayerMP player) {
        OctineIngotFireTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger();
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        public Instance() {
            super(OctineIngotFireTrigger.ID);
        }
    }

    static class Listener extends BLTrigger.Listener<OctineIngotFireTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger() {
            this.listeners.stream().findFirst().ifPresent(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
