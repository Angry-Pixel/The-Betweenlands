package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class PyradAggroTrigger extends BLTrigger<PyradAggroTrigger.Instance, PyradAggroTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "pyrad_aggro");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public PyradAggroTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new PyradAggroTrigger.Listener(playerAdvancements);
    }

    @Override
    public PyradAggroTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new PyradAggroTrigger.Instance();
    }

    public void trigger(EntityPlayerMP player) {
        PyradAggroTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger();
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        public Instance() {
            super(PyradAggroTrigger.ID);
        }
    }

    static class Listener extends BLTrigger.Listener<PyradAggroTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger() {
            this.listeners.stream().findFirst().ifPresent(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
