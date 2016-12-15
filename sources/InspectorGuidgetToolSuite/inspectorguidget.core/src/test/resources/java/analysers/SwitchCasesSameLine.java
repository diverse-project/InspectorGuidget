import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;

class Foo implements ActionListener {
	private static enum MyEnum {
		ENUM1,
		ENUM2,
		ENUM3,
		ENUM4,
		ENUM5,
		ENUM6;
	};

	@Override
	public void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();

		switch (MyEnum.valueOf(command)) {
			case ENUM1:
				System.out.println("ENUM1");
				break;
			case ENUM2: case ENUM3: case ENUM4: case ENUM5:
				System.out.println("MULTI ENUM");
				break;
			case ENUM6:
				System.out.println("ENUM6");
				break;
		}
	}
}
