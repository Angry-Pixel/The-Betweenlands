package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class NoCriteriaTrigger extends BLTrigger<AbstractCriterionInstance, NoCriteriaTrigger.Listener> {

    public final ResourceLocation id;

    public NoCriteriaTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public NoCriteriaTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new NoCriteriaTrigger.Listener(playerAdvancements);
    }

    @Override
    public AbstractCriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new AbstractCriterionInstance(id);
    }

    public void trigger(EntityPlayerMP player) {
        NoCriteriaTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger();
        }
    }

    static class Listener extends BLTrigger.Listener<AbstractCriterionInstance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger() {
            this.listeners.stream().findFirst().ifPresent(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
