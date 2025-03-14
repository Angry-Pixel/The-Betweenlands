package thebetweenlands.compat.flan;

import javax.annotation.Nullable;

import org.apache.maven.artifact.versioning.VersionRange;

import com.google.auto.service.AutoService;
import com.google.common.base.Objects;
import com.machinezoo.noexception.optional.OptionalBoolean;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.data.IPermissionContainer;
import io.github.flemmli97.flan.api.data.IPermissionStorage;
import io.github.flemmli97.flan.api.permission.BuiltinPermission;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thebetweenlands.compat.BLClaimCompatHelper;
import thebetweenlands.compat.IBetweenlandsModCompat;

public interface BetweenlandsFlanCompat extends IBetweenlandsModCompat {
	public static final String MOD_ID = "flan";

	public static final BetweenlandsFlanCompat INSTANCE = IBetweenlandsModCompat.getService(BetweenlandsFlanCompat.class, MOD_ID).orElseGet(BetweenlandsFlanCompatUnloaded::new);
	
	@Override
	public default String getModId() {
		return MOD_ID;
	}

	public boolean claimRestrictsBlockBreak(ServerLevel level, BlockPos pos, Entity entity);

	public boolean claimRestrictsBlockPlace(ServerLevel level, BlockPos pos, Entity entity);

	public boolean areSameClaim(ServerLevel level, BlockPos pos1, BlockPos pos2);

	public OptionalBoolean canInteract(ServerLevel level, BlockPos pos, Entity entity, ResourceLocation permission);
	
	// Static methods
	public static boolean restrictBlockBreak(Level level, BlockPos pos, @Nullable Entity entity) {
		if(level instanceof ServerLevel serverLevel) {
			return BetweenlandsFlanCompat.INSTANCE.isModLoaded() && BetweenlandsFlanCompat.INSTANCE.claimRestrictsBlockBreak(serverLevel, pos, entity);
		} else {
			return BLClaimCompatHelper.RESTRICT_BLOCK_BREAK_DEFAULT;
		}
	}

	public static boolean restrictBlockPlace(Level level, BlockPos pos, @Nullable Entity entity) {
		if(level instanceof ServerLevel serverLevel) {
			return BetweenlandsFlanCompat.INSTANCE.isModLoaded() && BetweenlandsFlanCompat.INSTANCE.claimRestrictsBlockPlace(serverLevel, pos, entity);
		} else {
			return BLClaimCompatHelper.RESTRICT_BLOCK_PLACE_DEFAULT;
		}
	}

	public static boolean areSameClaim(Level level, BlockPos pos1, BlockPos pos2) {
		if(level instanceof ServerLevel serverLevel) {
			return BetweenlandsFlanCompat.INSTANCE.isModLoaded() && BetweenlandsFlanCompat.INSTANCE.areSameClaim(serverLevel, pos1, pos2);
		} else {
			return BLClaimCompatHelper.ARE_SAME_CLAIM_DEFAULT;
		}
	}

	/**
	 * Checks whether or not the the entity has a permission, in the form of a {@link OptionalBoolean}. If the optional is empty, Flan can't currently be accessed.
	 * @param level The level that contains {@code pos}
	 * @param pos The position of the interaction
	 * @param entity The entity initiating the interaction
	 * @param permission The permission to check whether the entity has
	 * @return an optional representing whether or not the entity has the permission
	 */
	public static OptionalBoolean getEntityPermission(Level level, BlockPos pos, Entity entity, ResourceLocation permission) {
		if(level instanceof ServerLevel serverLevel && BetweenlandsFlanCompat.INSTANCE.isModLoaded()) {
			return BetweenlandsFlanCompat.INSTANCE.canInteract(serverLevel, pos, entity, permission);
		} else {
			return OptionalBoolean.empty();
		}
	}
	
	// Different class to avoid issues with ClassNotFoundException/NoClassDefFoundError
	@AutoService(BetweenlandsFlanCompat.class)
	public static class BetweenlandsFlanCompatImpl implements BetweenlandsFlanCompat {
		@Override
		public VersionRange supportedModVersions() {
			return IBetweenlandsModCompat.createVersionRangeFromSpec("*");
		}
		
		@Override
		public boolean claimRestrictsBlockBreak(ServerLevel level, BlockPos pos, Entity entity) {
			return !ClaimHandler.canInteract(entity instanceof ServerPlayer player ? player : null, pos, BuiltinPermission.BREAK);
		}

		@Override
		public boolean claimRestrictsBlockPlace(ServerLevel level, BlockPos pos, Entity entity) {
			return !ClaimHandler.canInteract(entity instanceof ServerPlayer player ? player : null, pos, BuiltinPermission.PLACE);
		}

		@Override
		public boolean areSameClaim(ServerLevel level, BlockPos pos1, BlockPos pos2) {
			IPermissionStorage storage = ClaimHandler.getPermissionStorage(level);
			IPermissionContainer container1 = storage.getForPermissionCheck(pos1);
			IPermissionContainer container2 = storage.getForPermissionCheck(pos2);
			return Objects.equal(container1, container2);
		}

		@Override
		public OptionalBoolean canInteract(ServerLevel level, BlockPos pos, Entity entity, ResourceLocation permission) {
			return OptionalBoolean.of(ClaimHandler.canInteract(entity instanceof ServerPlayer player ? player : null, pos, permission));
		}
	}

	// Shouldn't be necessary
	public static class BetweenlandsFlanCompatUnloaded implements BetweenlandsFlanCompat, IBetweenlandsModCompat.Unloaded {
		@Override
		public VersionRange supportedModVersions() {
			return IBetweenlandsModCompat.createVersionRangeFromSpec("*");
		}
		
		@Override
		public boolean claimRestrictsBlockBreak(ServerLevel level, BlockPos pos, Entity entity) {
			return BLClaimCompatHelper.RESTRICT_BLOCK_BREAK_DEFAULT;
		}

		@Override
		public boolean claimRestrictsBlockPlace(ServerLevel level, BlockPos pos, Entity entity) {
			return BLClaimCompatHelper.RESTRICT_BLOCK_PLACE_DEFAULT;
		}

		@Override
		public boolean areSameClaim(ServerLevel level, BlockPos pos1, BlockPos pos2) {
			return BLClaimCompatHelper.ARE_SAME_CLAIM_DEFAULT;
		}
		
		@Override
		public OptionalBoolean canInteract(ServerLevel level, BlockPos pos, Entity entity, ResourceLocation permission) {
			return OptionalBoolean.empty();
		}
	}
}
