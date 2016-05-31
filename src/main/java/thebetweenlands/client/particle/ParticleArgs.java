package thebetweenlands.client.particle;

public enum ParticleArgs {
	VX_VY_VZ(double.class, double.class, double.class) {
		@Override
		public Object[] getArguments(double motionX, double motionY, double motionZ, float scale) {
			return new Object[] { motionX, motionY, motionZ };
		}
	},
	VX_VY_VZ_SCALE(double.class, double.class, double.class, float.class) {
		@Override
		public Object[] getArguments(double motionX, double motionY, double motionZ, float scale) {
			return new Object[] { motionX, motionY, motionZ, scale };
		}
	},
	V0_V0_V0(double.class, double.class, double.class) {
		@Override
		public Object[] getArguments(double motionX, double motionY, double motionZ, float scale) {
			return new Object[] { 0, 0, 0 };
		}
	},
	SCALE(float.class) {
		@Override
		public Object[] getArguments(double motionX, double motionY, double motionZ, float scale) {
			return new Object[] { scale };
		}
	},
	NONE() {
		@Override
		public Object[] getArguments(double motionX, double motionY, double motionZ, float scale) {
			return new Object[0];
		}
	};

	private Class<?>[] argumentTypes;

	private int argumentCount;

	private ParticleArgs(Class<?>... argumentTypes) {
		this.argumentTypes = argumentTypes;
		argumentCount = argumentTypes.length;
	}

	public Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}

	public int getArgumentCount() {
		return argumentCount;
	}

	public abstract Object[] getArguments(double motionX, double motionY, double motionZ, float scale);
}
