package org.javabuilders.swing.samples;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;

import org.javabuilders.swing.SwingJavaBuilder;
import org.javabuilders.swing.samples.resources.Person;
import org.javabuilders.swing.samples.resources.PersonDialog;
import org.jdesktop.observablecollections.ObservableCollections;

public class ListBindingPanel extends SamplePanel {

	private List<Person> people = ObservableCollections.observableList(new LinkedList<Person>());
	private JList list;
	
	public ListBindingPanel() throws Exception {
		super();
		
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, 1970);
		
		people.add(new Person("John","Doe", date));
		people.add(new Person("Jan","Doenowvsky", date));
		people.add(new Person("Jazz","Doezilla", date));
		
		SwingJavaBuilder.build(this);
	}

	/**
	 * @return the people
	 */
	public List<Person> getPeople() {
		return people;
	}
	
	@SuppressWarnings("unused")
	private void addPerson() {
		PersonDialog dialog = new PersonDialog();
		dialog.setVisible(true);
		if (dialog.getPerson().getFirstName() != null) {
			people.add(dialog.getPerson());
		}
	}
	
	@SuppressWarnings("unused")
	private void deleteFromList() {
		Person person = (Person) list.getSelectedValue();
		people.remove(person);
	}
	
}
