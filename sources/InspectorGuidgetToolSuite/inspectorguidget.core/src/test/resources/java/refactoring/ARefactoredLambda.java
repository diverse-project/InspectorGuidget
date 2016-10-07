class A {
	javax.swing.JButton but;

	A() {
		but = new javax.swing.JButton("foo");
		but.setActionCommand("FOO");
		but.addActionListener((java.awt.event.ActionEvent e) -> java.lang.System.out.println("coucou"));
	}
}
