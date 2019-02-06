package thebetweenlands.core.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerModule {
	private boolean successful = false;
	private final List<Pair<String, String>> acceptedClasses = new ArrayList<>();
	private boolean obfuscated = false;
	protected static boolean isBukkitServer = false;

	static {
		try {
			Class.forName("org.bukkit.Bukkit");
			isBukkitServer = true;
		} catch (ClassNotFoundException e) { }
	}

	/**
	 * Adds an accepted class
	 * @param obfName Obfuscated name of the class
	 * @param deobfName Deobfuscated name of the class
	 */
	protected final void addAcceptedClass(String obfName, String deobfName) {
		this.acceptedClasses.add(Pair.of(obfName, deobfName));
	}

	/**
	 * Returns the name of this module
	 * @return
	 */
	public abstract String getName();

	/**
	 * Resets the module to the initial state
	 */
	public void reset() {
		this.successful = false;
	}

	/**
	 * Returns whether the specified class should be accepted
	 * @param className
	 * @return
	 */
	public final boolean acceptsClass(String className) {
		for(Pair<String, String> acceptedClass : this.acceptedClasses) {
			if(acceptedClass.getKey().equals(className)) {
				this.obfuscated = true;
				return true;
			} else if(acceptedClass.getValue().equals(className)) {
				this.obfuscated = false;
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the specified method should be accepted
	 * @param method
	 * @return
	 */
	public boolean acceptsMethod(MethodNode method) {
		return false;
	}

	/**
	 * Transforms a class
	 * @param classNode
	 */
	public void transformClass(ClassNode classNode) {

	}

	/**
	 * Transforms a method
	 * @param method
	 */
	public void transformMethod(MethodNode method) {

	}

	/**
	 * Transforms one or more method instructions
	 * @param method
	 * @param node
	 * @param index
	 */
	public void transformMethodInstruction(MethodNode method, AbstractInsnNode node, int index) {

	}

	/**
	 * Marks the transformation as successful.
	 * If this is not called the game will stop with an exception
	 */
	protected final void setSuccessful() {
		this.successful = true;
	}

	/**
	 * Returns whether the transformation was successful
	 * @return
	 */
	public final boolean wasSuccessful() {
		return this.successful;
	}

	/**
	 * Returns whether the code is obfuscated
	 * @return
	 */
	public final boolean isObfuscated() {
		return this.obfuscated;
	}

	/**
	 * Returns the obfuscated name if the code is obfuscated and if not it returns the deobfuscated name
	 * @param obfName
	 * @param deofName
	 * @return
	 */
	protected final String getMappedName(String obfName, String deofName) {
		return this.isObfuscated() ? obfName : deofName;
	}

	/**
	 * Inserts a list of instructions before the specified instruction
	 * @param method
	 * @param insn
	 * @param toInsert
	 */
	protected final void insertBefore(MethodNode method, AbstractInsnNode insn, Collection<? extends AbstractInsnNode> toInsert) {
		for(AbstractInsnNode insertedInsn : toInsert) {
			method.instructions.insertBefore(insn, insertedInsn);
		}
	}

	/**
	 * Inserts a list of instructions after the specified instruction
	 * @param method
	 * @param insn
	 * @param toInsert
	 */
	protected final void insertAfter(MethodNode method, AbstractInsnNode insn, List<? extends AbstractInsnNode> toInsert) {
		for(int i = toInsert.size() - 1; i >= 0; i--) {
			method.instructions.insert(insn, toInsert.get(i));
		}
	}
}
