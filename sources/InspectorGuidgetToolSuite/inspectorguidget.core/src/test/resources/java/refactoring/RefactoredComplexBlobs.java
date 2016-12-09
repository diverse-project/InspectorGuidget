import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

class A extends JPanel {
	private JComboBox<String> sel;
	private JTextField input;
	private final JButton add;
	protected final JButton remove;
	private JButton up;
	private JButton down;

	A(boolean foo, boolean bar) {
		add = new JButton();
		remove = new JButton();

		add.addActionListener((ActionEvent e) -> {
			if ((sel != null) && (sel.getSelectedItem() != null)) {
				String s = sel.getSelectedItem().toString();
				addField(s);
			} else if ((input != null) && !"".equals(input.getText())) {
				addField(input.getText());
			}
		});
		remove.addActionListener((ActionEvent e) -> removeSelected());

		if(bar) {
			input = new JTextField(20);
			input.addActionListener((ActionEvent e) -> addField(input.getText()));
		}else {
			sel = new JComboBox<>();
			sel.setEditable(true);
		}

		if(foo) {
			up = new JButton();
			down = new JButton();
			up.addActionListener((ActionEvent e) -> move(-1));
			down.addActionListener((ActionEvent e) -> move(1));
		}
	}

	protected void removeSelected() {
		System.out.println("coucouc");
	}

	protected void addField(String str) {
		System.out.println(str);
	}

	private void move(int dy) {
		System.out.println(dy);
	}
}