package org.javabuilders.swing.handler.event;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.javabuilders.*;
import org.javabuilders.event.ObjectMethod;
import org.javabuilders.handler.AbstractPropertyHandler;
import org.javabuilders.util.BuilderUtils;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * JTable.getSelectionModel().addListSelectionListener() handler, e.g.
 * <code>JTable(onSelection=tableSelectionChanged)</code>
 * 
 * @author Sebastien Gollion
 */
public class JTableSelectionDataListenerHandler extends AbstractPropertyHandler implements IPropertyList {
	public static final String ON_SELECTION_DATA = "onSelectionData";
	private static final Logger LOGGER = Logger.getLogger(JTableSelectionDataListenerHandler.class.getSimpleName());
	private static final List<ValueListDefinition> defs = ValueListDefinition
			.getCommonEventDefinitions(ListSelectionDataEvent.class);

	private static final JTableSelectionDataListenerHandler singleton = new JTableSelectionDataListenerHandler();

	/**
	 * @return Singleton
	 */
	public static JTableSelectionDataListenerHandler getInstance() {
		return singleton;
	}

	/**
	 * @param consumedKeys
	 */
	public JTableSelectionDataListenerHandler() {
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
		final JTable target = (JTable) node.getMainObject();
		final Values<String, ObjectMethod> values = (Values<String, ObjectMethod>) node.getProperty(key);

		if (values.size() > 0) {
			target.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() == false) {
						final TableModel tm = target.getModel();
						if (tm instanceof EventTableModel<?>) {
							ListSelectionDataEvent dataEvent = new ListSelectionDataEvent(e.getSource(), getSelectedValues(target
									.getSelectionModel(), (EventTableModel<?>) tm));
							BuilderUtils.invokeCallerEventMethods(process.getBuildResult(), target, values.values(), dataEvent);
						} else {
							LOGGER.warning("The tablemodel of the table " + target.getName() + " is not of type "
									+ EventTableModel.class.getSimpleName());
						}
					}
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
		return JTable.class;
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

	/**
	 * Returns an array of all the selected values, in increasing order based on their indices in the list.
	 * 
	 * @param lsm
	 * @param etm
	 * @return the selected values, or an empty array if nothing is selected
	 */
	private Object[] getSelectedValues(final ListSelectionModel lsm, final EventTableModel<?> etm) {
		int iMin = lsm.getMinSelectionIndex();
		int iMax = lsm.getMaxSelectionIndex();

		if ((iMin < 0) || (iMax < 0)) {
			return new Object[0];
		}

		Object[] rvTmp = new Object[1 + (iMax - iMin)];
		int n = 0;
		for (int i = iMin; i <= iMax; i++) {
			if (lsm.isSelectedIndex(i)) {
				rvTmp[n++] = etm.getElementAt(i);
			}
		}
		Object[] rv = new Object[n];
		System.arraycopy(rvTmp, 0, rv, 0, n);
		return rv;
	}
}
