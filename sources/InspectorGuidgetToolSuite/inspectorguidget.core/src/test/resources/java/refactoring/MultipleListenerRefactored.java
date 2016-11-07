
package foo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
class MultipleListener {
	JButton button;
	JComboBox cb1;
	JComboBox cb2;
	MultipleListener() {
		button = new JButton();
		button.addActionListener((ActionEvent evt) -> System.out.println("fooo!"));
		cb1 = new JComboBox();
		cb1.addItemListener((ItemEvent evt) -> System.out.println("fooo"));
		cb2 = new JComboBox();
		cb2.addItemListener((ItemEvent evt) -> System.out.println("barrrr"));
	}
}

