package thebetweenlands.core;

import thebetweenlands.common.lib.ModInfo;

public final class TheBetweenlandsPreconditions {
	private static final String MCT_MSG = ModInfo.IDE ?
			"\nHey there modder! It appears you're missing something rather important.\n" + 
			"Could it perhaps be TheBetweenlandsLoadingPlugin? Of course it is! All you\n" +
			"have to do is add this VM arg:\n" +
			"    -Dfml.coreMods.load=thebetweenlands.core.TheBetweenlandsLoadingPlugin\n" +
			"If you're unsure how to add a VM arg, follow this tutorial:\n" + 
			"    https://youtu.be/dQw4w9WgXcQ"
			:
				"\nLook's like somebody built The Betweenlands without its FMLCorePlugin\n" +
				"manifest definition or there is a major problem. Leave a comment on our forum:\n" +
				"    http://bit.ly/TheBetweenlands\n" +
				"stating \"The FMLCorePlugin is broken.\"";

	private TheBetweenlandsPreconditions() {}

	public static void check() {
		if (!TheBetweenlandsClassTransformer.constructed) {
			throw new MissingClassTransformation();
		}
	}

	private static class MissingClassTransformation extends RuntimeException {
		private static final long serialVersionUID = 946208971201883597L;

		public MissingClassTransformation() {
			super(MCT_MSG);
		}

		@Override
		public synchronized Throwable fillInStackTrace() {
			return this;
		}

		@Override
		public String toString() {
			return getMessage();
		}
	}
}
