package thebetweenlands.client.extensions.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public class InvisibleEffectRegistration implements IClientMobEffectExtensions {

	public static final InvisibleEffectRegistration INSTANCE = new InvisibleEffectRegistration();

	@Override
	public boolean isVisibleInInventory(MobEffectInstance instance) {
		return false;
	}

	@Override
	public boolean isVisibleInGui(MobEffectInstance instance) {
		return false;
	}
}
