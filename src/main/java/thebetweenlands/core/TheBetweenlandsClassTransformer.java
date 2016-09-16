package thebetweenlands.core;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import thebetweenlands.core.module.PreRenderShadersHookTransformer;
import thebetweenlands.core.module.TransformerModule;

public class TheBetweenlandsClassTransformer implements IClassTransformer {
	public static boolean constructed;

	private final List<TransformerModule> modules = new ArrayList<TransformerModule>();
	private final List<TransformerModule> currentClassModules = new ArrayList<TransformerModule>();
	private final List<TransformerModule> currentMethodModules = new ArrayList<TransformerModule>();

	public TheBetweenlandsClassTransformer() {
		constructed = true;

		this.registerModule(new PreRenderShadersHookTransformer());
	}

	/**
	 * Registers a transformer module
	 * @param module
	 * @return
	 */
	public TheBetweenlandsClassTransformer registerModule(TransformerModule module) {
		this.modules.add(module);
		return this;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] classBytes) {
		this.currentClassModules.clear();

		for(TransformerModule module : this.modules) {
			if(module.acceptsClass(name)) {
				module.reset();
				this.currentClassModules.add(module);
			}
		}

		ClassNode classNode = null;

		if(!this.currentClassModules.isEmpty()) {
			classNode = readClass(classBytes);

			for(TransformerModule module : this.currentClassModules) {
				module.transformClass(classNode);
			}

			for(MethodNode method : classNode.methods) {
				this.currentMethodModules.clear();

				for(TransformerModule module : this.currentClassModules) {
					if(module.acceptsMethod(method)) {
						if(!module.wasSuccessful()) {
							module.transformMethod(method);
							this.currentMethodModules.add(module);
						}
					}
				}

				for (int i = 0; i < method.instructions.size(); i++) {
					AbstractInsnNode insnNode = method.instructions.get(i);

					for(TransformerModule module : this.currentClassModules) {
						if(!module.wasSuccessful())
							i += module.transformMethodInstruction(method, insnNode, i);
					}
				}
			}
		}

		for(TransformerModule module : this.currentClassModules) {
			if(!module.wasSuccessful()) {
				throw new RuntimeException(String.format("Transformer module %s failed", module.getName()));
			}
		}

		return classNode == null ? classBytes : writeClass(classNode);
	}

	private ClassNode readClass(byte[] classBytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(classBytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClass(ClassNode classNode) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}
}
