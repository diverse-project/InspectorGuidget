class B {
	javax.swing.JButton but1;
	javax.swing.JButton but2;
	B() {
		but1 = new javax.swing.JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(e -> java.lang.System.out.println("coucou1"));
		but2 = new javax.swing.JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(e -> java.lang.System.out.println("coucou2"));
	}
}

