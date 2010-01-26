package org.javabuilders.swing.samples;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A TableCellRenderer to defines defaul cell renderer behaviour.
 * 
 * @author Sebastien Gollion
 */
public class EnhTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -1449029834911194736L;
	private DateFormat formatter;

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
	 *      boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value instanceof Calendar) {
			Calendar calendar = (Calendar) value;
			if (formatter == null) {
				formatter = DateFormat.getDateInstance();
			}
			setText(formatter.format(calendar.getTime()));
		}

		return comp;
	}
}
