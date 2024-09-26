package thebetweenlands.compat.jade;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.phys.HitResult;
import snownee.jade.addon.vanilla.MobSpawnerProvider;
import snownee.jade.addon.vanilla.VanillaPlugin;
import snownee.jade.api.*;
import thebetweenlands.common.block.structure.MirageBlock;
import thebetweenlands.common.block.structure.MobSpawnerBlock;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

@WailaPlugin
public class BetweenlandsJadeCompat implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.addRayTraceCallback(this::registerBlockOverrides);
		registration.registerBlockComponent(MobSpawnerProvider.getBlock(), MobSpawnerBlock.class);
	}

	@Nullable
	public Accessor<?> registerBlockOverrides(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor) {
		if (accessor instanceof BlockAccessor target) {
			Player player = accessor.getPlayer();
			if (player.isCreative() || player.isSpectator()) {
				return accessor;
			}
			IWailaClientRegistration client = VanillaPlugin.CLIENT_REGISTRATION;
			if (target.getBlock() == BlockRegistry.POSSESSED_BLOCK.get()) {
				return client.blockAccessor().from(target).blockState(BlockRegistry.BETWEENSTONE_BRICKS.get().defaultBlockState()).build();
			} else if (target.getBlock() instanceof MirageBlock mirage) {
				return client.blockAccessor().from(target).blockState(mirage.getMirageBlock().defaultBlockState()).build();
			}
		}
		return accessor;
	}
}
