package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class PeatFireTrigger extends BLTrigger<PeatFireTrigger.Instance, PeatFireTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "peat_fire");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public PeatFireTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new PeatFireTrigger.Listener(playerAdvancements);
    }

    @Override
    public PeatFireTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new PeatFireTrigger.Instance();
    }

    public void trigger(EntityPlayerMP player) {
        PeatFireTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger();
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        public Instance() {
            super(PeatFireTrigger.ID);
        }
    }

    static class Listener extends BLTrigger.Listener<PeatFireTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger() {
            this.listeners.stream().findFirst().ifPresent(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
