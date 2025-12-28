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
	String MODID = "flan";

	BetweenlandsFlanCompat INSTANCE = IBetweenlandsModCompat.getService(BetweenlandsFlanCompat.class, MODID).orElseGet(BetweenlandsFlanCompatFallback::new);

	static ResourceLocation prefix(String path) { return ResourceLocation.fromNamespaceAndPath(MODID, path); }


	@Override
	default String getModId() {
		return MODID;
	}

	boolean claimRestrictsBlockBreak(ServerLevel level, BlockPos pos, Entity entity);

	boolean claimRestrictsBlockPlace(ServerLevel level, BlockPos pos, Entity entity);

	boolean areSameClaim(ServerLevel level, BlockPos pos1, BlockPos pos2);

	OptionalBoolean canInteract(ServerLevel level, BlockPos pos, @Nullable Entity entity, ResourceLocation permission);

	// Static methods
	static boolean restrictBlockBreak(Level level, BlockPos pos, @Nullable Entity entity) {
		if(level instanceof ServerLevel serverLevel) {
			return BetweenlandsFlanCompat.INSTANCE.isModLoaded() && BetweenlandsFlanCompat.INSTANCE.claimRestrictsBlockBreak(serverLevel, pos, entity);
		} else {
			return BLClaimCompatHelper.RESTRICT_BLOCK_BREAK_DEFAULT;
		}
	}

	static boolean restrictBlockPlace(Level level, BlockPos pos, @Nullable Entity entity) {
		if(level instanceof ServerLevel serverLevel) {
			return BetweenlandsFlanCompat.INSTANCE.isModLoaded() && BetweenlandsFlanCompat.INSTANCE.claimRestrictsBlockPlace(serverLevel, pos, entity);
		} else {
			return BLClaimCompatHelper.RESTRICT_BLOCK_PLACE_DEFAULT;
		}
	}

	static OptionalBoolean areSameClaimOptional(Level level, BlockPos pos1, BlockPos pos2) {
		if(level instanceof ServerLevel serverLevel && BetweenlandsFlanCompat.INSTANCE.isModLoaded()) {
			return OptionalBoolean.of(BetweenlandsFlanCompat.INSTANCE.areSameClaim(serverLevel, pos1, pos2));
		} else {
			return OptionalBoolean.empty();
		}
	}

	static boolean areSameClaim(Level level, BlockPos pos1, BlockPos pos2) {
		return areSameClaimOptional(level, pos1, pos2).orElse(BLClaimCompatHelper.ARE_SAME_CLAIM_DEFAULT);
	}

	/**
	 * Checks whether or not the the entity has a permission, in the form of a {@link OptionalBoolean}. If the optional is empty, Flan can't currently be accessed.
	 * @param level The level that contains {@code pos}
	 * @param pos The position of the interaction
	 * @param entity The entity initiating the interaction
	 * @param permission The permission to check whether the entity has
	 * @return an optional representing whether or not the entity has the permission
	 */
	static OptionalBoolean getEntityPermission(Level level, BlockPos pos, @Nullable Entity entity, ResourceLocation permission) {
		if(level instanceof ServerLevel serverLevel && BetweenlandsFlanCompat.INSTANCE.isModLoaded()) {
			return BetweenlandsFlanCompat.INSTANCE.canInteract(serverLevel, pos, entity, permission);
		} else {
			return OptionalBoolean.empty();
		}
	}

	// Different class to avoid issues with ClassNotFoundException/NoClassDefFoundError
	@AutoService(BetweenlandsFlanCompat.class)
	class BetweenlandsFlanCompatImpl implements BetweenlandsFlanCompat {
		@Override
		public VersionRange supportedModVersions() {
			return ALL_VERSIONS; // Flan uses the same API in 1.21+ as it does in 1.16, so all versions *should* be compatible
		}

		public static boolean canInteract(ServerLevel level, Entity entity, BlockPos pos, ResourceLocation permission) {
			return ClaimHandler.getPermissionStorage(level).getForPermissionCheck(pos).canInteract(entity instanceof ServerPlayer player ? player : null, permission, pos);
		}

		@Override
		public boolean claimRestrictsBlockBreak(ServerLevel level, BlockPos pos, Entity entity) {
			return !canInteract(level, entity, pos, BuiltinPermission.BREAK);
		}

		@Override
		public boolean claimRestrictsBlockPlace(ServerLevel level, BlockPos pos, Entity entity) {
			return !canInteract(level, entity, pos, BuiltinPermission.PLACE);
		}

		@Override
		public boolean areSameClaim(ServerLevel level, BlockPos pos1, BlockPos pos2) {
			IPermissionStorage storage = ClaimHandler.getPermissionStorage(level);
			IPermissionContainer container1 = storage.getForPermissionCheck(pos1);
			IPermissionContainer container2 = storage.getForPermissionCheck(pos2);
			return Objects.equal(container1, container2);
		}

		@Override
		public OptionalBoolean canInteract(ServerLevel level, BlockPos pos, @Nullable Entity entity, ResourceLocation permission) {
			return OptionalBoolean.of(canInteract(level, entity, pos, permission));
		}
	}

	// Typically shouldn't be necessary with AutoService handling the registering of BetweenlandsFlanCompatImpl, but it's here for futureproofing anyways
	class BetweenlandsFlanCompatFallback implements BetweenlandsFlanCompat, IBetweenlandsModCompat.IFallbackModCompat {
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
		public OptionalBoolean canInteract(ServerLevel level, BlockPos pos, @Nullable Entity entity, ResourceLocation permission) {
			return OptionalBoolean.empty();
		}
	}
}
