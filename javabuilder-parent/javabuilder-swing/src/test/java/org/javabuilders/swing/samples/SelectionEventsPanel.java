package org.javabuilders.swing.samples;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;

import org.javabuilders.swing.SwingJavaBuilder;
import org.javabuilders.swing.handler.event.ListSelectionDataEvent;
import org.javabuilders.swing.samples.resources.Person;

import ca.odell.glazedlists.*;

/**
 * A SamplePanel to show onSelection and onSelectionData events on JList, JTable and JTree.
 * 
 * @author Sebastien Gollion
 */
@SuppressWarnings( { "serial", "unused" })
public class SelectionEventsPanel extends SamplePanel {
	// Selection events on JList
	private JList jlistSource;
	private JTextField jlistSelectionEventJTF;
	private JTextField jlistSelectionDataEventJTF;
	private JTable jlistJT;
	private final EventList<Person> jlistPeopleDetails = GlazedLists.threadSafeList(new BasicEventList<Person>());

	// Selection events on JTable
	private JTable jtableSource;
	private JTextField jtableSelectionEventJTF;
	private JTextField jtableSelectionDataEventJTF;
	private JTable jtableJT;
	private final EventList<Person> jtablePeopleDetails = GlazedLists.threadSafeList(new BasicEventList<Person>());

	// Selection events on JTree
	private JTree jtreeSource;
	private JTextField jtreeSelectionEventJTF;
	private JTextField jtreeSelectionDataEventJTF;
	private JTable jtreeJT;
	private final EventList<Person> jtreePeopleDetails = GlazedLists.threadSafeList(new BasicEventList<Person>());

	private final EventList<Person> peopleList = GlazedLists.threadSafeList(new BasicEventList<Person>());

	private final Random random = new Random();

	public SelectionEventsPanel() throws Exception {
		peopleList.add(new Person("John", "Doe", getCalendar()));
		peopleList.add(new Person("Jan", "Doenowvsky", getCalendar()));
		peopleList.add(new Person("John", "Doenowvsky", getCalendar()));
		peopleList.add(new Person("Jan", "Doe", getCalendar()));
		peopleList.add(new Person("Jazz", "Doezilla", getCalendar()));
		peopleList.add(new Person("Jan", "ab", getCalendar()));
		peopleList.add(new Person("Jan", "cd", getCalendar()));

		SwingJavaBuilder.build(this);
		jlistJT.setDefaultRenderer(Object.class, new EnhTableCellRenderer());
		jtableSource.setDefaultRenderer(Object.class, new EnhTableCellRenderer());
		jtableJT.setDefaultRenderer(Object.class, new EnhTableCellRenderer());
		jtreeJT.setDefaultRenderer(Object.class, new EnhTableCellRenderer());
	}

	private Calendar getCalendar() {
		final Calendar date = Calendar.getInstance();
		final long rand = random.nextInt(Integer.MAX_VALUE);
		date.setTimeInMillis(rand * 500L);
		return date;
	}

	private void onSelectionActionsJL(final JList list, final ListSelectionEvent aListSelectionEvent) {
		final StringBuilder sb = new StringBuilder("Index selected:");
		String sep = "";
		for (int i = list.getMinSelectionIndex(); i <= list.getMaxSelectionIndex(); i++) {
			if (list.isSelectedIndex(i)) {
				sb.append(sep).append(i);
				sep = ",";
			}
		}
		jlistSelectionEventJTF.setText(sb.toString());
	}

	private void onSelectionDataActionsJL(final JList list, final ListSelectionDataEvent aEvent) {
		jlistSelectionDataEventJTF.setText("Nb selected values:" + aEvent.getSelectedValuesSize());
		final List<?> lData = Arrays.asList(aEvent.getSelectedValues());
		jlistPeopleDetails.clear();
		for (final Object data : lData) {
			jlistPeopleDetails.add((Person) data);
		}
	}

	private void onSelectionActionsJT(final JTable table, final ListSelectionEvent aListSelectionEvent) {
		final ListSelectionModel lsm = table.getSelectionModel();
		final StringBuilder sb = new StringBuilder("Index selected:");
		String sep = "";
		for (int i = lsm.getMinSelectionIndex(); i <= lsm.getMaxSelectionIndex(); i++) {
			if (lsm.isSelectedIndex(i)) {
				sb.append(sep).append(i);
				sep = ",";
			}
		}
		jtableSelectionEventJTF.setText(sb.toString());
	}

	private void onSelectionDataActionsJT(final JTable table, final ListSelectionDataEvent aEvent) {
		jtableSelectionDataEventJTF.setText("Nb selected values:" + aEvent.getSelectedValuesSize());
		final List<?> lData = Arrays.asList(aEvent.getSelectedValues());
		jtablePeopleDetails.clear();
		for (final Object data : lData) {
			jtablePeopleDetails.add((Person) data);
		}
	}

	private void onSelectionActionsJTree(final JTree tree, final TreeSelectionEvent aTreeSelectionEvent) {
		final TreeSelectionModel lsm = tree.getSelectionModel();
		final StringBuilder sb = new StringBuilder("Index selected:");
		String sep = "";
		for (int i = lsm.getMinSelectionRow(); i <= lsm.getMaxSelectionRow(); i++) {
			if (lsm.isRowSelected(i)) {
				sb.append(sep).append(i);
				sep = ",";
			}
		}
		jtreeSelectionEventJTF.setText(sb.toString());
	}

	private void onSelectionDataActionsJTree(JTree tree, final ListSelectionDataEvent aEvent) {
		jtreeSelectionDataEventJTF.setText("Nb selected values:" + aEvent.getSelectedValuesSize());
		final List<?> lData = Arrays.asList(aEvent.getSelectedValues());
		jtreePeopleDetails.clear();
		for (final Object data : lData) {
			jtreePeopleDetails.add((Person) data);
		}
	}

	/**
	 * @return the peopleList.
	 */
	public List<Person> getPeopleList() {
		return peopleList;
	}
}
