package foo;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


class ShortcutsListener implements KeyListener {
	protected Object clone;
	private long oldTime;


	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if(key==KeyEvent.VK_SPACE)
		{
			System.out.println("coucou");
			return ;
		}

		if(key==KeyEvent.VK_A)
		{
			if(e.getModifiers()==InputEvent.CTRL_MASK)
			{
				System.out.println("coucou2");
				return ;
			}
			return ;
		}

		if(key==KeyEvent.VK_RIGHT)
		{
			if(e.getModifiers()==0)// Right arrow = move the horizontal scrollbar to the right.
			{
				System.out.println("coucou3");
				return ;
			}

			if(e.getModifiers()==InputEvent.CTRL_MASK)
			{
				System.out.println("coucou4");
				return ;
			}

			if(e.getModifiers()==InputEvent.ALT_MASK)
			{
				oldTime = e.getWhen();
				clone = new Object();
				return ;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(clone!=null && e.getWhen()-oldTime>100)
		{
			System.out.println("coucou");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
