package org.javabuilders.swing.handler.event;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.javabuilders.*;
import org.javabuilders.event.ObjectMethod;
import org.javabuilders.handler.AbstractPropertyHandler;
import org.javabuilders.util.BuilderUtils;

import ca.odell.glazedlists.TreeList;

/**
 * JTree.getSelectionModel().addTreeSelectionListener() handler, e.g.
 * <code>JTree(onSelection=treeSelectionChanged)</code>
 * 
 * @author Sebastien Gollion
 */
public class JTreeSelectionDataListenerHandler extends AbstractPropertyHandler implements IPropertyList {
	public static final String ON_SELECTION_DATA = "onSelectionData";
	private static final Logger LOGGER = Logger.getLogger(JTreeSelectionDataListenerHandler.class.getSimpleName());
	private static final List<ValueListDefinition> defs = ValueListDefinition
			.getCommonEventDefinitions(ListSelectionDataEvent.class);

	private static final JTreeSelectionDataListenerHandler singleton = new JTreeSelectionDataListenerHandler();

	/**
	 * @return Singleton
	 */
	public static JTreeSelectionDataListenerHandler getInstance() {
		return singleton;
	}

	/**
	 * Constructor
	 */
	private JTreeSelectionDataListenerHandler(String... consumedKeys) {
		super(ON_SELECTION_DATA);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javabuilders.handler.IPropertyHandler#handle(org.javabuilders.BuilderConfig,
	 *      org.javabuilders.BuildProcess, org.javabuilders.Node, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public void handle(BuilderConfig config, final BuildProcess process, Node node, String key) throws BuildException {
		final JTree target = (JTree) node.getMainObject();
		final Values<String, ObjectMethod> values = (Values<String, ObjectMethod>) node.getProperty(key);

		if (values.size() > 0) {
			target.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					final List<Object> elements = new ArrayList<Object>();
					final List<TreePath> selectionPaths = Arrays.asList(target.getSelectionPaths());
					for (final TreePath treePath : selectionPaths) {
						final Object lastPathComponent = treePath.getLastPathComponent();
						if (lastPathComponent instanceof TreeNode) {
							final TreeNode node = (TreeNode) lastPathComponent;
							System.out.println("node=" + node.toString());
							LOGGER.warning("The node of the JTree " + target.getName() + " is not yet implemented");
						} else if (lastPathComponent instanceof TreeList.Node<?>) {
							final TreeList.Node<?> node = (TreeList.Node<?>) lastPathComponent;
							if (node.isLeaf()) {
								elements.add(node.getElement());
							}
						} else {
							LOGGER.warning("The node of the JTree " + target.getName() + " is not of type TreeNode or TreeList.Node");
						}
					}

					ListSelectionDataEvent dataEvent = new ListSelectionDataEvent(e.getSource(), elements
							.toArray(new Object[elements.size()]));
					BuilderUtils.invokeCallerEventMethods(process.getBuildResult(), target, values.values(), dataEvent);
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javabuilders.IApplicable#getApplicableClass()
	 */
	public Class<?> getApplicableClass() {
		return JTree.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javabuilders.IPropertyList#getValueListDefinitions(java.lang.String)
	 */
	public List<ValueListDefinition> getValueListDefinitions(final String propertyName) {
		return defs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javabuilders.IPropertyList#isList(java.lang.String)
	 */
	public boolean isList(final String propertyName) {
		return true;
	}
}
