package org.javabuilders.swing.handler.type;

import java.util.Map;
import java.util.Set;

import javax.swing.JTree;

import org.javabuilders.BuildException;
import org.javabuilders.BuildProcess;
import org.javabuilders.BuilderConfig;
import org.javabuilders.Node;
import org.javabuilders.handler.ITypeHandlerFinishProcessor;

import ca.odell.glazedlists.swing.EventTreeModel;

/**
 * Handles property creating a JTree
 * 
 * @author Sebastien Gollion
 */
public class JTreeFinishProcessor implements ITypeHandlerFinishProcessor {
	private static final JTreeFinishProcessor singleton = new JTreeFinishProcessor();

	/**
	 * @return Singleton
	 */
	public static JTreeFinishProcessor getInstance() {
		return singleton;
	}

	private JTreeFinishProcessor() {
	}

	/* (non-Javadoc)
	 * @see org.javabuilders.handler.ITypeHandlerFinishProcessor#finish(org.javabuilders.BuilderConfig, org.javabuilders.BuildProcess, org.javabuilders.Node, java.lang.String, java.util.Map)
	 */
	public void finish(BuilderConfig config, BuildProcess process, Node current, String key, Map<String, Object> typeDefinition)
			throws BuildException {
		Set<EventTreeModel> models = current.getChildObjects(EventTreeModel.class);
		JTree list = (JTree) current.getMainObject();
		for (EventTreeModel<?> model : models) {
			list.setModel(model);
		}
	}
}
