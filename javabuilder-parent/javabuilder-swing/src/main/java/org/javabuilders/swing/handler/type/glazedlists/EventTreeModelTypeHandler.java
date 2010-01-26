package org.javabuilders.swing.handler.type.glazedlists;

import java.awt.Component;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.*;

import javax.swing.JTree;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;
import org.javabuilders.*;
import org.javabuilders.handler.AbstractTypeHandler;
import org.javabuilders.handler.ITypeChildrenHandler;
import org.javabuilders.util.BuilderUtils;
import org.javabuilders.util.PropertyUtils;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.swing.EventTreeModel;

/**
 * GlazedLists EventTreeModel support
 * 
 * @author Sebastien Gollion
 */
public class EventTreeModelTypeHandler extends AbstractTypeHandler implements ITypeChildrenHandler {
	public static final String SOURCE = "source";
	public static final String PARAM_NODES = "nodes";

	public EventTreeModelTypeHandler() {
		super(SOURCE, PARAM_NODES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javabuilders.handler.ITypeHandler#createNewInstance(org.javabuilders .BuilderConfig,
	 *      org.javabuilders.BuildProcess, org.javabuilders.Node, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Node createNewInstance(BuilderConfig config, BuildProcess process, Node parent, String key,
			Map<String, Object> typeDefinition) throws BuildException {

		String source = (String) typeDefinition.get(SOURCE);

		List<Map<String, Object>> cols = parent.getParent().getContentData(TableColumn.class);
		JTree tree = (JTree) parent.getParentObject(JTree.class);

		if (source == null) {
			throw new BuildException("EventTreeModel.source property must be specified: {0}", typeDefinition);
		} else {
			Field field = BuilderUtils.getField(process.getCaller(), source, EventList.class);
			if (field == null) {
				throw new BuildException(
						"EventTreeModel.source property does not point to a valid instance of GlazedLists EventList: {0}",
						typeDefinition);
			} else {
				try {
					EventList list = (EventList) field.get(process.getCaller());
					Class<?> type = BuilderUtils.getGenericsTypeFromCollectionField(field);
					if (type == null) {
						throw new BuildException("Unable to use generics to find type of object stored in source: {0}", source);
					}

					EventTreeModel instance = setupModel(process, typeDefinition, tree, list, cols, type);
					return useExistingInstance(config, process, parent, key, typeDefinition, instance);
				} catch (BuildException ex) {
					throw ex;
				} catch (Exception e) {
					throw new BuildException(e, "Unable to create instance of EventTreeModel: {0}.\n{1}", typeDefinition, e
							.getMessage());
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javabuilders.handler.ITypeHandler#useExistingInstance(org.javabuilders .BuilderConfig,
	 *      org.javabuilders.BuildProcess, org.javabuilders.Node, java.lang.String, java.util.Map, java.lang.Object)
	 */
	public Node useExistingInstance(BuilderConfig config, BuildProcess process, Node parent, String key,
			Map<String, Object> typeDefinition, Object instance) throws BuildException {
		Node node = new Node(parent, key, typeDefinition, instance);
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javabuilders.IApplicable#getApplicableClass()
	 */
	@SuppressWarnings("unchecked")
	public Class<EventTreeModel> getApplicableClass() {
		return EventTreeModel.class;
	}

	@SuppressWarnings( { "unchecked" })
	private EventTreeModel setupModel(BuildProcess process, Map<String, Object> typeDefinition, JTree tree,
			EventList source, List<Map<String, Object>> cols, Class<?> type) {
		List<String> nodesList = (List<String>) typeDefinition.get(PARAM_NODES);
		TreeList.Format<Object> treeListFormat = null;
		Comparator<Object> compSource = null;
		if (nodesList != null && nodesList.size() > 0) {
			treeListFormat = createTreeListFormat(type, nodesList);
			compSource = createFieldsComparator(type, nodesList);
		}

		DefaultExternalExpansionModel<Object> expansionProvider = new DefaultExternalExpansionModel<Object>(
				TreeList.NODES_START_COLLAPSED);

		// put it all together, in the right order for GlazedLists to work
		EventList sortedSource = new SortedList(source, compSource);
		TreeList<Object> treeList = new TreeList(sortedSource, treeListFormat, expansionProvider);

		tree.setCellRenderer(new TreeListNodeRenderer());
		EventTreeModel<Object> model = new EventTreeModel<Object>(treeList);
		return model;
	}

	// compiles a fields comparator using Janino
	@SuppressWarnings("unchecked")
	private Comparator createFieldsComparator(Class<?> type, List<String> fields) {
		Comparator c = null;
		StringBuilder bld = new StringBuilder();

		bld.append("public int compare(Object o1, Object o2) {\n");
		bld.append("\t").append(type.getName()).append(" val1 = (").append(type.getName()).append(") o1;\n");
		bld.append("\t").append(type.getName()).append(" val2 = (").append(type.getName()).append(") o2;\n");
		bld.append("\tint compare = 0;\n");

		// create the comparison for each field
		for (String field : fields) {
			bld.append("\tif (compare == 0) {\n");
			String getter = PropertyUtils.getGetterName(field);
			Class<?> returnType = PropertyUtils.verifyGetter(type, getter, short.class, Short.class, int.class,
					Integer.class, long.class, Long.class, double.class, Double.class, String.class, char.class, Character.class,
					Comparable.class);
			bld.append("\t\t").append(returnType.getName()).append("\t\tcolVal1 = val1.").append(getter).append("();\n");
			bld.append("\t\t").append(returnType.getName()).append("\t\tcolVal2 = val2.").append(getter).append("();\n");
			if (returnType.isPrimitive()) {
				bld.append("\t\tcompare = colVal1 - colVal2;\n");
			} else {
				bld.append("\t\tcompare = colVal1.compareTo(colVal2);\n");
			}

			bld.append("\t}\n");
		}
		bld.append("return compare;");
		bld.append("}");

		try {
			// compile in-memory using Janino
			c = (Comparator) ClassBodyEvaluator.createFastClassBodyEvaluator( //
					new Scanner(null, new StringReader(bld.toString())), //
					Comparator.class, // Base type to extend/implement
					(ClassLoader) null // Use current thread's context class loader
					);
			return c;
		} catch (Exception e) {
			throw new BuildException("Failed to compile Comparator for GlazedLists sorting: {0}\n{1}", e.getMessage(), bld
					.toString());
		}
	}

	// compiles a TreeList.Format using Janino
	@SuppressWarnings("unchecked")
	private TreeList.Format<Object> createTreeListFormat(Class<?> type, List<String> nodesList) {
		StringBuilder bld = new StringBuilder();

		bld.append("public void getPath(List path, Object element) {\n");
		bld.append("	if (element == null)\n");
		bld.append("		return;\n");
		bld.append("	if (element instanceof <LeafType>) {\n");
		bld.append("		<LeafType> person = (<LeafType>) element;\n");
		// add each node level
		for (String node : nodesList) {
			String getter = PropertyUtils.getGetterName(node);
			bld.append("		path.add(person.").append(getter).append("());\n");
		}
		bld.append("	}\n");
		bld.append("	path.add(element);\n");
		bld.append("}\n");

		bld.append("public boolean allowsChildren(Object element) {\n");
		bld.append("	return true;\n");
		bld.append("}\n");

		bld.append("public Comparator getComparator(int depth) {\n");
		bld.append("	return null;\n");
		bld.append("}\n");

		String bldStr = bld.toString().replaceAll("<LeafType>", type.getName());

		try {
			// compile in-memory using Janino
			final ClassBodyEvaluator cbe = new ClassBodyEvaluator();
			cbe.setImplementedTypes(new Class[] { TreeList.Format.class });
			cbe.setDefaultImports(new String[] { "java.util.*", "java.util.Comparator" });
			cbe.cook(new Scanner(null, new StringReader(bldStr)));
			final TreeList.Format<Object> ret = (TreeList.Format<Object>) cbe.getClazz().newInstance();
			return ret;
		} catch (Exception e) {
			throw new BuildException("Failed to compile TreeList.Format for GlazedLists TreeList: {0}\n{1}", e.getMessage(),
					bldStr);
		}
	}

	/**
	 * Render a TreeList node in a JTree.
	 */
	private static class TreeListNodeRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			Object renderValue;
			if (value instanceof TreeList.Node) {
				TreeList.Node<?> node = (TreeList.Node<?>) value;
				renderValue = node.getElement();
			} else {
				// sometimes JTree inexplicably wants to render the root
				renderValue = null;
			}
			return super.getTreeCellRendererComponent(tree, renderValue, selected, expanded, leaf, row, hasFocus);
		}
	}
}
