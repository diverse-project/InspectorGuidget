class B implements java.awt.event.ActionListener {
	javax.swing.JButton but1;

	javax.swing.JButton but2;

	B() {
		but1 = new javax.swing.JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(java.refactoring.B.this);
		but2 = new javax.swing.JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				java.lang.System.out.println("coucou2");
			}
		});
	}

	@java.lang.Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		if (e.getActionCommand().equals("FOO")) {
			java.lang.System.out.println("coucou1");
			return ;
		}
	}
}