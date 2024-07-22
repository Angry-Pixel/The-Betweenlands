package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.SimulacrumEffect;

public class WisdomSimulacrumEffect implements SimulacrumEffect {
	//TODO
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
//		if (!level.isClientSide() && level.getGameTime() % 160 == 0 && level.getEntitiesOfClass(FalseXPOrb.class, new AABB(pos).inflate(16.0D)).isEmpty()) {
//			Player player = level.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 8.0D, false);
//
//			if (player != null) {
//				int xp = level.getRandom().nextInt((int) Math.min(Math.abs(level.getRandom().nextGaussian() * Math.min(player.totalExperience, 1200)), 2400) + 1);
//
//				if (xp < player.totalExperience) {
//					UUID playerUuid = player.getUUID();
//
//					int negativeXp = xp;
//
//					float multiplier = 1.0f + Mth.clamp((float) level.getRandom().nextGaussian(), -0.5f, 0.5f) - 0.025f;
//
//					xp = (int) (xp * multiplier);
//
//					List<Entity> orbs = new ArrayList<>();
//
//					for (int i = 0; i < 32; i++) {
//						int negativeOrbXp = 0;
//						if (negativeXp > 0) {
//							if (i != 31) {
//								negativeOrbXp = level.getRandom().nextInt(negativeXp / 8 + 1) + 1;
//								negativeXp -= negativeOrbXp;
//							} else {
//								negativeOrbXp = negativeXp;
//							}
//						}
//
//						int orbXp = 0;
//						if (xp > 0) {
//							if (i != 31) {
//								orbXp = level.getRandom().nextInt(xp / 8 + 1) + 1;
//								xp -= orbXp;
//							} else {
//								orbXp = xp;
//							}
//						}
//
//						Entity negativeOrb = null;
//						Entity orb = null;
//
//						if (level.getRandom().nextBoolean()) {
//							if (negativeOrbXp > 0) negativeOrb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, -negativeOrbXp, playerUuid);
//							if (orbXp > 0) orb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, orbXp, playerUuid);
//						} else {
//							orb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, orbXp, playerUuid);
//							if (orbXp > 0) if (negativeOrbXp > 0) negativeOrb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, -negativeOrbXp, playerUuid);
//						}
//
//						if (orb != null) {
//							orbs.add(orb);
//						}
//
//						if (negativeOrb != null) {
//							orbs.add(negativeOrb);
//						}
//					}
//
//					Collections.shuffle(orbs);
//
//					float stepH = Mth.PI * 2 / (float) orbs.size() * 6;
//					float stepV = Mth.PI * 2 / (float) orbs.size() / 4;
//
//					for (int i = 0; i < orbs.size(); i++) {
//						double hc = Math.cos(stepH * i);
//						double hs = Math.sin(stepH * i);
//
//						double vc = Math.cos(stepV * i);
//						double vs = Math.sin(stepV * i);
//
//						double dx = hs * vc;
//						double dz = hc * vc;
//
//						Entity orb = orbs.get(i);
//
//						orb.setDeltaMovement(dx * 0.25f, 0.1f + vs * 0.35f, dz * 0.25f);
//
//						orb.moveTo(orb.getX() + dx * 0.65f, orb.getY() + vs * 2f, orb.getZ() + dz * 0.65f, 0, 0);
//
//						level.addFreshEntity(orb);
//					}
//				}
//			}
//		}
	}
}
