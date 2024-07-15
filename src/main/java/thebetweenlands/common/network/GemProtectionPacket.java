package thebetweenlands.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.circlegem.CircleGem;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;

public record GemProtectionPacket(int entityID, CircleGem gem) implements CustomPacketPayload {

	public static final Type<GemProtectionPacket> TYPE = new Type<>(TheBetweenlands.prefix("gem_protection"));
	public static final StreamCodec<RegistryFriendlyByteBuf, GemProtectionPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, GemProtectionPacket::entityID,
		ByteBufCodecs.fromCodec(CircleGem.CODEC), GemProtectionPacket::gem,
		GemProtectionPacket::new);


	public GemProtectionPacket(int entityID, boolean offensive, CircleGemType gem) {
		this(entityID, new CircleGem(gem, offensive ? CircleGem.CombatType.OFFENSIVE : CircleGem.CombatType.DEFENSIVE));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(GemProtectionPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			CircleGem gem = packet.gem();
			Entity entityHit = context.player().level().getEntity(packet.entityID());
			if (entityHit != null) {
				RandomSource rnd = entityHit.level().getRandom();
				for (int i = 0; i < 40; i++) {
					double x = entityHit.getX() + rnd.nextFloat() * entityHit.getBbWidth() * 2.0F - entityHit.getBbWidth();
					double y = entityHit.getBoundingBox().minY + rnd.nextFloat() * entityHit.getBbHeight();
					double z = entityHit.getZ() + rnd.nextFloat() * entityHit.getBbWidth() * 2.0F - entityHit.getBbWidth();
					double dx = x - entityHit.getX();
					double dy = y - (entityHit.getY() + entityHit.getBbHeight() / 2.0F);
					double dz = z - entityHit.getZ();
					double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
//					ParticleArgs<?> args = ParticleArgs.get();
//
//					switch (gem.combatType()) {
//						case OFFENSIVE:
//						default:
//							args.withMotion(dx / len, dy / len, dz / len);
//							break;
//						case DEFENSIVE:
//							args.withMotion(-dx / len, -dy / len, -dz / len);
//							break;
//					}
//
//					switch (gem.gemType()) {
//						default:
//						case AQUA:
//							args.withColor(0.35F, 0.35F, 1, 1);
//							break;
//						case CRIMSON:
//							args.withColor(1, 0, 0, 1);
//							break;
//						case GREEN:
//							args.withColor(0.3F, 1.0F, 0.3F, 1.0F);
//							break;
//					}
//
//					BLParticles.GEM_PROC.spawn(entityHit.level(), x, y, z, args);
				}
			}
		});
	}
}
