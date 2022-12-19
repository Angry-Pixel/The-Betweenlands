package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class CavingRopePlacedTrigger extends BLTrigger<AbstractCriterionInstance, CavingRopePlacedTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "cavingrope_placed");

    public CavingRopePlacedTrigger() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CavingRopePlacedTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new CavingRopePlacedTrigger.Listener(playerAdvancements);
    }

    @Override
    public AbstractCriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new AbstractCriterionInstance(ID);
    }

    public void trigger(EntityPlayerMP player) {
        CavingRopePlacedTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

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
