package thebetweenlands.core;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.collect.ImmutableList;

import net.minecraft.launchwrapper.IClassTransformer;
import thebetweenlands.core.module.AddRainParticlesHookTransformer;
import thebetweenlands.core.module.PreRenderShadersHookTransformer;
import thebetweenlands.core.module.SplashPotionInstantHookTransformer;
import thebetweenlands.core.module.SplashPotionNotInstantHookTransformer;
import thebetweenlands.core.module.TransformerModule;

public class TheBetweenlandsClassTransformer implements IClassTransformer {
	public static boolean constructed;

	private static final List<TransformerModule> modules = new ArrayList<TransformerModule>();

	static {
		registerModule(new PreRenderShadersHookTransformer());
		registerModule(new SplashPotionInstantHookTransformer());
		registerModule(new SplashPotionNotInstantHookTransformer());
		registerModule(new AddRainParticlesHookTransformer());
	}

	public TheBetweenlandsClassTransformer() {
		constructed = true;
	}

	/**
	 * Registers a transformer module
	 * @param module
	 * @return
	 */
	public static void registerModule(TransformerModule module) {
		modules.add(module);
	}

	/**
	 * Returns all registered modules
	 * @return
	 */
	public static List<TransformerModule> getModules() {
		return ImmutableList.copyOf(modules);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] classBytes) {
		List<TransformerModule> currentClassModules = new ArrayList<TransformerModule>();

		for(TransformerModule module : modules) {
			if(module.acceptsClass(name)) {
				module.reset();
				currentClassModules.add(module);
			}
		}

		ClassNode classNode = null;

		if(!currentClassModules.isEmpty()) {
			classNode = readClass(classBytes);

			for(TransformerModule module : currentClassModules) {
				module.transformClass(classNode);
			}

			List<TransformerModule> currentMethodModules = new ArrayList<TransformerModule>();
			
			for(MethodNode method : classNode.methods) {
				currentMethodModules.clear();

				for(TransformerModule module : currentClassModules) {
					if(!module.wasSuccessful() && module.acceptsMethod(method)) {
						module.transformMethod(method);
						currentMethodModules.add(module);
					}
				}

				for (int i = 0; i < method.instructions.size(); i++) {
					AbstractInsnNode insnNode = method.instructions.get(i);

					for(TransformerModule module : currentMethodModules) {
						if(!module.wasSuccessful())
							module.transformMethodInstruction(method, insnNode, i);
					}
				}
			}
			
			checkModules(currentClassModules);
		}

		return classNode == null ? classBytes : writeClass(classNode);
	}

	/**
	 * Checks if all modules were successful and throws a {@link ClassTransformationException} otherwise.
	 */
	public static void checkModules() {
		checkModules(modules);
	}

	/**
	 * Checks if all modules in the specified list were successful and throws a {@link ClassTransformationException} otherwise.
	 * @param modules
	 */
	private static void checkModules(List<TransformerModule> modules) {
		for(TransformerModule module : modules) {
			if(!module.wasSuccessful()) {
				throw new ClassTransformationException(String.format("Transformer module %s failed", module.getName()));
			}
		}
	}

	private ClassNode readClass(byte[] classBytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(classBytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClass(ClassNode classNode) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}

	private static class ClassTransformationException extends RuntimeException {
		private static final long serialVersionUID = -6506110261102379179L;

		public ClassTransformationException(String str) {
			super(str);
		}
	}
}
