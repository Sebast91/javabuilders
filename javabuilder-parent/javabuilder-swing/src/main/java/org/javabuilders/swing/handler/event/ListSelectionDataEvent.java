package org.javabuilders.swing.handler.event;

import java.util.Arrays;
import java.util.EventObject;

import javax.swing.event.ListSelectionEvent;

/**
 * An event that characterizes a change in the current selection. The change is defined by a list of data.
 * 
 * @author Sebastien Gollion
 * @see ListSelectionEvent
 */
public class ListSelectionDataEvent extends EventObject {
	private static final long serialVersionUID = 6589403589075651228L;
	private final Object[] selectedValues;

	/**
	 * Represents a change in selection status between <code>firstIndex</code> and <code>lastIndex</code> inclusive (</code>firstIndex</code>
	 * is less than or equal to <code>lastIndex</code>). At least one of the rows within the range will have changed, a
	 * good <code>ListSelectionModel</code> implementation will keep the range as small as possible.
	 * 
	 * @param aSelectedValues
	 *          the selected values; not null.
	 */
	public ListSelectionDataEvent(final Object source, final Object[] aSelectedValues) {
		super(source);
		this.selectedValues = aSelectedValues;
	}

	/**
	 * Returns the selected values.
	 * 
	 * @return the selected values.
	 */
	public Object[] getSelectedValues() {
		return Arrays.copyOf(this.selectedValues, this.selectedValues.length);
	}

	/**
	 * Returns the number of selected values.
	 * 
	 * @return the number of selected values.
	 */
	public int getSelectedValuesSize() {
		return this.selectedValues.length;
	}

	/**
	 * Returns a string that displays and identifies this object's properties.
	 * 
	 * @return a String representation of this object
	 */
	@Override
	public String toString() {
		String properties = "source=" + getSource() + ",selectedValues=" + Arrays.asList(selectedValues);
		return getClass().getSimpleName() + "[" + properties + "]";
	}
}
