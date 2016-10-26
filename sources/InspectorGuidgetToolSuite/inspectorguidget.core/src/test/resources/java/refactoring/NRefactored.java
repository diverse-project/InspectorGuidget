
package java.refactoring;
class N {
	protected javax.swing.JFileChooser fileChooser;
	javax.swing.JButton but1;
	javax.swing.JButton but2;
	N() {
		but1 = new javax.swing.JButton("foo1");
		fileChooser = new javax.swing.JFileChooser();
		fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		but1.addActionListener((java.awt.event.ActionEvent e) -> {
			int id = fileChooser.showDialog(null, "foo");
			if (id == (javax.swing.JFileChooser.APPROVE_OPTION)) {
				java.lang.System.out.println("foo");
			}
		});
		but2 = new javax.swing.JButton("foo2");
		but2.addActionListener((java.awt.event.ActionEvent e) -> {
			int id = fileChooser.showDialog(null, "bar");
			if (id == (javax.swing.JFileChooser.APPROVE_OPTION)) {
				java.lang.System.out.println("bar");
			}
		});
	}
}
class NListener {
	public NListener() {
	}
}

