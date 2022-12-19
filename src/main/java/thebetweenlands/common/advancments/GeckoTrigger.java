package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class GeckoTrigger extends BLTrigger<GeckoTrigger.Instance, GeckoTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "gecko");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public GeckoTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new Listener(playerAdvancements);
    }

    @Override
    public GeckoTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        boolean test = JsonUtils.getBoolean(json, "test", false);
        boolean release = JsonUtils.getBoolean(json, "release", false);
        return new GeckoTrigger.Instance(test, release);
    }

    public void trigger(EntityPlayerMP player, boolean test, boolean release) {
        GeckoTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(test, release);
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final boolean test;
        private final boolean release;

        public Instance(boolean test, boolean release) {
            super(GeckoTrigger.ID);
            this.test = test;
            this.release = release;
        }

        public boolean test(boolean test, boolean release) {
            return (this.test && test) || (this.release && release);
        }
    }

    static class Listener extends BLTrigger.Listener<GeckoTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(boolean test, boolean release) {
            List<ICriterionTrigger.Listener<GeckoTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<GeckoTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(test, release)) {
                    list.add(listener);
                }
            }

            for (ICriterionTrigger.Listener<GeckoTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
