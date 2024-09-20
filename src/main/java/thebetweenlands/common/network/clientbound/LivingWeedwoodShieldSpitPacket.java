package thebetweenlands.common.network.clientbound;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.shield.LivingWeedwoodShieldItem;
import thebetweenlands.common.registries.ItemRegistry;

public record LivingWeedwoodShieldSpitPacket(int entityID, boolean mainHand, int ticks) implements CustomPacketPayload {
	public static final Type<LivingWeedwoodShieldSpitPacket> TYPE = new Type<>(TheBetweenlands.prefix("living_weedwood_shield_spit"));
	public static final StreamCodec<RegistryFriendlyByteBuf, LivingWeedwoodShieldSpitPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, LivingWeedwoodShieldSpitPacket::entityID,
		ByteBufCodecs.BOOL, LivingWeedwoodShieldSpitPacket::mainHand,
		ByteBufCodecs.INT, LivingWeedwoodShieldSpitPacket::ticks,
		LivingWeedwoodShieldSpitPacket::new);

	public LivingWeedwoodShieldSpitPacket(LivingEntity entity, boolean mainHand, int ticks) {
		this(entity.getId(), mainHand, ticks);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(LivingWeedwoodShieldSpitPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			Entity owner = level.getEntity(packet.entityID());
			if(owner instanceof LivingEntity living) {
				ItemStack held = living.getItemInHand(packet.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);

				if (!held.isEmpty() && held.getItem() instanceof LivingWeedwoodShieldItem shield) {
					shield.setSpitTicks(held, packet.ticks());

					float yaw = -(180 - living.yBodyRot);
					Vec3 lookVec = living.getLookAngle();
					Vec3 bodyForward = new Vec3(Mth.sin(-yaw * 0.017453292F - Mth.PI), 0, Mth.cos(-yaw * 0.017453292F - Mth.PI));
					Vec3 up = new Vec3(0, 1, 0);
					Vec3 right = bodyForward.cross(up);
					Vec3 offset = new Vec3(bodyForward.x * 0.5F, owner.getEyeHeight(), bodyForward.z * 0.5F).add(right.scale(packet.mainHand() ? 0.35D : -0.35D).add(0, lookVec.y * 0.5D - 0.4D, 0).add(bodyForward.scale(-0.1D)));

					for (int i = 0; i < 20; i++) {
						double dx = living.level().getRandom().nextDouble() * 0.2D - 0.1D + bodyForward.x * 0.1D;
						double dy = living.level().getRandom().nextDouble() * 0.2D - 0.1D + bodyForward.y * 0.1D + 0.08D;
						double dz = living.level().getRandom().nextDouble() * 0.2D - 0.1D + bodyForward.z * 0.1D;
						living.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ItemRegistry.SAP_SPIT.get())), living.getX() + offset.x + dx, living.getY() + offset.y + dy, living.getZ() + offset.z + dz, dx * 1.25D, dy, dz * 1.25D);
					}
				}
			}
		});
	}
}
