package org.javabuilders.swing.handler.event;

import java.util.List;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.javabuilders.*;
import org.javabuilders.event.ObjectMethod;
import org.javabuilders.handler.AbstractPropertyHandler;
import org.javabuilders.util.BuilderUtils;

/**
 * JList.getSelectionModel().addListSelectionListener() handler, e.g.
 * <code>JList(onSelection=tableSelectionChanged)</code>
 * 
 * @author Sebastien Gollion
 */
public class JListSelectionDataListenerHandler extends AbstractPropertyHandler implements IPropertyList {
	private static final String ON_SELECTION_DATA = "onSelectionData";
	private static final List<ValueListDefinition> defs = ValueListDefinition
			.getCommonEventDefinitions(ListSelectionDataEvent.class);

	private static final JListSelectionDataListenerHandler singleton = new JListSelectionDataListenerHandler();

	/**
	 * @return Singleton
	 */
	public static JListSelectionDataListenerHandler getInstance() {
		return singleton;
	}

	/**
	 * @param consumedKeys
	 */
	public JListSelectionDataListenerHandler() {
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
		final JList target = (JList) node.getMainObject();
		final Values<String, ObjectMethod> values = (Values<String, ObjectMethod>) node.getProperty(key);

		if (values.size() > 0) {
			target.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() == false) {
						ListSelectionDataEvent dataEvent = new ListSelectionDataEvent(e.getSource(), target.getSelectedValues());
						BuilderUtils.invokeCallerEventMethods(process.getBuildResult(), target, values.values(), dataEvent);
					}
				}
			});
		}
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
