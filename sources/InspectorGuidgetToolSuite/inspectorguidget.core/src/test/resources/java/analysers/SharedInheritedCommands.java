import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class FOOOO implements ActionListener {
	protected static final String OK = "OK";

	@Override
	public void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();
		if (OK.equals(command)) {
			System.out.println("OKKK");
		}
	}
}

class Foo2 extends FOOOO {
	protected static final String OKKKK = "OKKKK";

	@Override
	public void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();
		if (OKKKK.equals(command)) {
			System.out.println("OKKKK");
		} else {
			super.actionPerformed(ae);
		}
	}
}


class Foo extends FOOOO {
	private static enum NewPanelAction {
		OK,
		CANCEL
	};

	@Override
	public void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();

		switch (NewPanelAction.valueOf(command)) {
			case OK:
				System.out.println("K");
				break;
			case CANCEL:
				System.out.println("cancel");
				break;
			default:
				super.actionPerformed(ae);
				break;
		}
	}
}

