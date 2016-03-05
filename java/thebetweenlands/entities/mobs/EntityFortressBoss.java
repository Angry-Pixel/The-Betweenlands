package thebetweenlands.entities.mobs;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class EntityFortressBoss extends EntityMob implements IEntityBL {
	private double X = 0.525731112119133606;
	private double Z = 0.850650808352039932;

	private double[][] vertices = new double[][] {
		{-X, 0.0D, Z}, {-X, 0.0D, Z}, {-X, 0.0, -Z}, {X, 0.0D, -Z},
		{0.0D, Z, X}, {0.0D, Z, -X}, {0.0D, -Z, X}, {0.0D, -Z, -X},
		{Z, X, 0.0D}, {-Z, X, 0.0D}, {Z, -X, 0.0D}, {-Z, -X, 0.0D}
	};

	private int[][] indices = new int[][] {
		{0,4,1}, {0,9,4}, {9,5,4}, {4,5,8}, {4,8,1},
		{8,10,1}, {8,3,10},{5,3,8}, {5,2,3}, {2,7,3},
		{7,10,3}, {7,6,10}, {7,11,6}, {11,0,6}, {0,1,6},
		{6,1,10}, {9,0,11}, {9,11,2}, {9,2,5}, {7,2,11}
	};

	public EntityFortressBoss(World world) {
		super(world);
	}

	@Override
	public String pageName() {
		return "fortressBoss";
	}
}
