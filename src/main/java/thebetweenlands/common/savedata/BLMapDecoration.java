package thebetweenlands.common.savedata;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.common.world.util.MathUtils;

public record BLMapDecoration(Location location, byte x, byte y, byte rotation) implements Comparable<BLMapDecoration> {

	private static final ResourceLocation MAP_ICONS = TheBetweenlands.prefix("textures/gui/map_icon_sheet.png");

	public boolean render(int index, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.translate(0.0F + this.x() / 2.0F + 64.0F, 0.0F + this.y() / 2.0F + 64.0F, -0.02F);
		stack.mulPose(Axis.ZP.rotationDegrees(this.rotation() * 360 / 16.0F));
		stack.scale(this.location.scale, this.location.scale, 1.0F);

		//We don't care about depth, just the rendering order which is already sorted out
		RenderSystem.depthMask(false);

		float f1 = this.location.x;
		float f2 = this.location.y;
		float f3 = this.location.x2;
		float f4 = this.location.y2;
		Matrix4f matrix4f = stack.last().pose();
		VertexConsumer mapIconVertices = buffer.getBuffer(RenderType.text(MAP_ICONS));
		mapIconVertices.addVertex(matrix4f, -1.0F, 1.0F, 0).setUv(f3, f2).setLight(light);
		mapIconVertices.addVertex(matrix4f, 1.0F, 1.0F, 0).setUv(f1, f2).setLight(light);
		mapIconVertices.addVertex(matrix4f, 1.0F, -1.0F, 0).setUv(f1, f4).setLight(light);
		mapIconVertices.addVertex(matrix4f, -1.0F, -1.0F, 0).setUv(f3, f4).setLight(light);

		RenderSystem.depthMask(true);

		stack.popPose();

		return true;
	}

	@Override
	public int compareTo(BLMapDecoration o) {
		return Integer.compare(this.location.ordinal(), o.location.ordinal());
	}

	public enum Location {
		NONE(0, 0, 0, 0, 0, 4.0F),
		SMALL_MARKER(12, 32, 16, 16, 16, 4.0F),
		PORTAL(1, 0, 0, 16, 16, 4.0F),
		SPAWN(2, 16, 0, 16, 16, 4.0F),
		SHRINE(3, 32, 0, 16, 16, 4.0F),
		GIANT_TREE(4, 48, 0, 16, 16, 4.0F),
		RUINS(5, 64, 0, 16, 16, 4.0F),
		TOWER(6, 80, 0, 16, 16, 4.0F),
		IDOL(7, 96, 0, 16, 16, 4.0F),
		WAYSTONE(8, 112, 0, 16, 16, 4.0F),
		BURIAL_MOUND(9, 0, 16, 16, 16, 4.0F),
		SPIRIT_TREE(10, 16, 16, 16, 16, 4.0F),
		FORTRESS(11, 0, 104, 24, 24, 6.0F),
		SLUDGE_WORM_DUNGEON(13, 24, 96, 32, 32, 8.0F),
		FLOATING_ISLAND(14, 48, 16, 16, 16, 4.0F),
		CHECK(127, 0, 32, 16, 16, 4.0F);

		private final byte id;
		private final float x;
		private final float y;
		private final float x2;
		private final float y2;
		private final float scale;

		public static final ImmutableList<Location> VALUES = ImmutableList.copyOf(values());

		Location(int id, int x, int y, int width, int height, float scale) {
			this.id = (byte) id;
			this.x = MathUtils.linearTransformf(x, 0, 128, 0, 1);
			this.y = MathUtils.linearTransformf(y, 0, 128, 0, 1);
			this.x2 = MathUtils.linearTransformf(width + x, 0, 128, 0, 1);
			this.y2 = MathUtils.linearTransformf(height + y, 0, 128, 0, 1);
			this.scale = scale;
		}

		public byte getId() {
			return this.id;
		}

		public static Location byId(int id) {
			for (Location location : VALUES) {
				if (location.id == id) {
					return location;
				}
			}
			return NONE;
		}

		public static Location getLocation(LocationStorage storage) {
			if (storage instanceof LocationPortal) {
				return PORTAL;
			}
			if (storage.getType() == EnumLocationType.WAYSTONE) {
				return WAYSTONE;
			}
			String name = storage.getName();
			return switch (name) {
				case "small_dungeon" -> SHRINE;
				case "giant_tree" -> GIANT_TREE;
				case "abandoned_shack", "ruins" -> RUINS;
				case "cragrock_tower" -> TOWER;
				case "idol_head" -> IDOL;
				case "wight_tower" -> FORTRESS;
				case "spirit_tree" -> SPIRIT_TREE;
				case "sludge_worm_dungeon" -> SLUDGE_WORM_DUNGEON;
				case "floating_island" -> FLOATING_ISLAND;
				default -> NONE;
			};
		}
	}
}
