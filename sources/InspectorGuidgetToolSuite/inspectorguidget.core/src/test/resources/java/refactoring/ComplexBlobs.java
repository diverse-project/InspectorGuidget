import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

class A extends JPanel implements ActionListener {
	private JComboBox<String> sel;
	private JTextField input;
	private final JButton add;
	protected final JButton remove;
	private JButton up;
	private JButton down;

	A(boolean foo, boolean bar) {
		add = new JButton();
		remove = new JButton();

		add.addActionListener(this);
		remove.addActionListener(this);

		if(bar) {
			input = new JTextField(20);
			input.addActionListener(this);
		}else {
			sel = new JComboBox<>();
			sel.setEditable(true);
		}

		if(foo) {
			up = new JButton();
			down = new JButton();
			up.addActionListener(this);
			down.addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (Objects.equals(src, add)) {
			if ((sel != null) && (sel.getSelectedItem() != null)) {
				String s = sel.getSelectedItem().toString();
				addField(s);
			} else if ((input != null) && !"".equals(input.getText())) {
				addField(input.getText());
			}
		} else if (Objects.equals(src, input)) {
			addField(input.getText());
		} else if (Objects.equals(src, remove)) {
			removeSelected();
		} else if (Objects.equals(src, sel)) {
			if ("comboBoxChanged".equals(e.getActionCommand()) && (e.getModifiers() == 0)) {
				return;
			}
			String s = sel.getSelectedItem().toString();
			addField(s);
			sel.getEditor().selectAll();
		} else if (Objects.equals(src, up)) {
			move(-1);
		} else if (Objects.equals(src, down)) {
			move(1);
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