package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class ShockwaveKillTrigger extends BLTrigger<ShockwaveKillTrigger.Instance, ShockwaveKillTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "shockwave_kill");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Listener createListener(PlayerAdvancements playerAdvancements) {
        return new ShockwaveKillTrigger.Listener(playerAdvancements);
    }

    @Override
    public ShockwaveKillTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new ShockwaveKillTrigger.Instance();
    }

    public void trigger(EntityPlayerMP player) {
        Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger();
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        public Instance() {
            super(ShockwaveKillTrigger.ID);
        }
    }

    static class Listener extends BLTrigger.Listener<ShockwaveKillTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger() {
            this.listeners.stream().findFirst().ifPresent(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
