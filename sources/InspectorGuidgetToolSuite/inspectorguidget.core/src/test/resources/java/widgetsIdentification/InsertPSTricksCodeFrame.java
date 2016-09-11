import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

abstract class AbstractParametersFrame extends JDialog {//} implements ActionListener {
	public static final String NAME_BUTTON_OK = "BUTTON_OK";//$NON-NLS-1$
	public static final String NAME_BUTTON_CANCEL = "BUTTON_CANCEL";//$NON-NLS-1$
	public static final String LABEL_OK = "azaz";
	public static final String LABEL_CANCEL = "trtt";

//	public JPanel createButtonsPanel()
//	{
//		JPanel panel = new JPanel();
//
//		JButton buttonOk = new JButton(AbstractParametersFrame.LABEL_OK),
//			buttonCancel = new JButton(AbstractParametersFrame.LABEL_CANCEL);
//
//		buttonOk.setActionCommand(NAME_BUTTON_OK);
//		buttonCancel.setActionCommand(NAME_BUTTON_CANCEL);
//		buttonOk.addActionListener(this);
//		buttonCancel.addActionListener(this);
//
//		return panel;
//	}
//
//	public void actionPerformed(ActionEvent e) {
//		Object o = e.getSource();
//
//		if(o instanceof JButton || o instanceof JCheckBox) {
//			String label = ((AbstractButton) o).getActionCommand();
//
//			if(label.equals(NAME_BUTTON_CANCEL)) {
//				super.setVisible(false);
//				return;
//			}
//
//			if(label.equals(NAME_BUTTON_OK)) {
//				setVisible(false);
//				return;
//			}
//		}
//	}
}

//class LaTeXDrawFrame {
//	public void insertPSTricksCode(String str) {}
//}

//class CopyPasteMenu extends JPopupMenu implements ActionListener, MouseListener{
//	public static final String LABEL_COPY 	= "rere";
//	public static final String LABEL_CUT	= "ytyty";
//	public static final String LABEL_PASTE 	= "uyuyuy";
//
//	protected JMenuItem copyM;
//	protected JMenuItem cutM;
//	protected JMenuItem pasteM;
//	protected JEditorPane editor;
//
//	public CopyPasteMenu(JEditorPane edit)
//	{
//		editor = edit;
//		copyM = new JMenuItem("ezze");
//		copyM.setActionCommand(LABEL_COPY);
//		copyM.addActionListener(this);
//		add(copyM);
//		cutM = new JMenuItem("ytyt");
//		cutM.setActionCommand(LABEL_CUT);
//		cutM.addActionListener(this);
//		add(cutM);
//		pasteM = new JMenuItem("_èuyhg");
//		pasteM.setActionCommand(LABEL_PASTE);
//		pasteM.addActionListener(this);
//		add(pasteM);
//	}
//
//	public void actionPerformed(ActionEvent e)
//	{
//	}
//
//	public void mouseClicked(MouseEvent e)
//	{
//	}
//
//
//	public void mouseEntered(MouseEvent e)
//	{
//	}
//
//	public void mouseExited(MouseEvent e)
//	{
//	}
//
//	public void mousePressed(MouseEvent e)
//	{
//	}
//
//	public void mouseReleased(MouseEvent e)
//	{
//	}
//}

public class InsertPSTricksCodeFrame extends JFrame implements ActionListener
{
	public static final String LABEL_FRAME_INSERT_OK = "INSERT_CODE_OK";//$NON-NLS-1$
	public static final String LABEL_FRAME_INSERT_CANCEL = "INSERT_CODE_CANCEL";//$NON-NLS-1$

//	private LaTeXDrawFrame mainFrame;
//	private JEditorPane editor;
//	protected CopyPasteMenu copyMenu;


	public InsertPSTricksCodeFrame()//LaTeXDrawFrame frame)
	{
//		super("InsertPSTricksCodeFrame.0"); //$NON-NLS-1$
//		try
//		{
//			mainFrame 	= frame;
//			setIconImage(null);

			JPanel  pButton = new JPanel();
			JButton buttonOk = new JButton(AbstractParametersFrame.LABEL_OK),
				buttonCancel = new JButton(AbstractParametersFrame.LABEL_CANCEL);

//			editor 		= new JEditorPane();
//			copyMenu	= new CopyPasteMenu(editor);

			buttonOk.setActionCommand(LABEL_FRAME_INSERT_OK);
			buttonCancel.setActionCommand(LABEL_FRAME_INSERT_CANCEL);
			buttonOk.addActionListener(this);
			buttonCancel.addActionListener(this);

			// The scroller of the editor
//			JScrollPane scrollPane = new JScrollPane(editor);
//			scrollPane.setMinimumSize(new Dimension(450, 250));
//			scrollPane.setPreferredSize(new Dimension(450, 250));
//			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

//			JLabel label = new JLabel("LaTeXDrawFrame.16", SwingConstants.CENTER); //$NON-NLS-1$
//			label.setAlignmentX(Component.CENTER_ALIGNMENT);

//			editor.setText("");//$NON-NLS-1$
			pButton.add(buttonOk);
			pButton.add(buttonCancel);
//			pButton.setPreferredSize(new Dimension(280, 40));
//			pButton.setMaximumSize(new Dimension(280, 40));
//			getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//			getContentPane().add(label);
//			getContentPane().add(scrollPane);
//			getContentPane().add(pButton);

//			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//			pack();
//			setLocation(dim.width/2-getWidth()/2, dim.height/2-getHeight()/2);
//			setVisible(false);
//			editor.addMouseListener(copyMenu);
//		}catch(Exception e)
//		{
//			e.printStackTrace();
////			ExceptionFrameDialog.showExceptionDialog(e);
//		}
	}




	public void actionPerformed(ActionEvent e)
	{
		Object o = e.getSource();

		if(o instanceof JButton)
		{
			String label = ((JButton)o).getActionCommand();

			if(label == LABEL_FRAME_INSERT_CANCEL)
			{
				super.setVisible(false);
				return ;
			}

			if(label == LABEL_FRAME_INSERT_OK)
			{
//				mainFrame.insertPSTricksCode(editor.getText());
				super.setVisible(false);

				return;
			}
		}
	}
//
//
//
//
//	@Override
//	public void setVisible(boolean e)
//	{
//		if(e)
//			editor.setText("");//$NON-NLS-1$
//
//		super.setVisible(e);
//	}
}




class InsertPSTricksCodeFrame2 extends JFrame implements ActionListener
{
	public static final String LABEL_FRAME_INSERT_OK = "INSERT_CODE_OK";//$NON-NLS-1$
	public static final String LABEL_FRAME_INSERT_CANCEL = "INSERT_CODE_CANCEL";//$NON-NLS-1$

//	private LaTeXDrawFrame mainFrame;
//	private JEditorPane editor;
//	protected CopyPasteMenu copyMenu;


	public InsertPSTricksCodeFrame2()//LaTeXDrawFrame frame)
	{
//		super("InsertPSTricksCodeFrame.0"); //$NON-NLS-1$
//		try
//		{
//			mainFrame 	= frame;
//			setIconImage(null);

			JPanel  pButton = new JPanel();
			JButton buttonOk = new JButton(AbstractParametersFrame.LABEL_OK),
				buttonCancel = new JButton(AbstractParametersFrame.LABEL_CANCEL);

//			editor 		= new JEditorPane();
//			copyMenu	= new CopyPasteMenu(editor);

			buttonOk.setActionCommand(LABEL_FRAME_INSERT_OK);
			buttonCancel.setActionCommand(LABEL_FRAME_INSERT_CANCEL);
			buttonOk.addActionListener(this);
			buttonCancel.addActionListener(this);

			// The scroller of the editor
//			JScrollPane scrollPane = new JScrollPane(editor);
//			scrollPane.setMinimumSize(new Dimension(450, 250));
//			scrollPane.setPreferredSize(new Dimension(450, 250));
//			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

//			JLabel label = new JLabel("LaTeXDrawFrame.16", SwingConstants.CENTER); //$NON-NLS-1$
//			label.setAlignmentX(Component.CENTER_ALIGNMENT);

//			editor.setText("");//$NON-NLS-1$
			pButton.add(buttonOk);
			pButton.add(buttonCancel);
//			pButton.setPreferredSize(new Dimension(280, 40));
//			pButton.setMaximumSize(new Dimension(280, 40));
//			getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//			getContentPane().add(label);
//			getContentPane().add(scrollPane);
//			getContentPane().add(pButton);

//			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//			pack();
//			setLocation(dim.width/2-getWidth()/2, dim.height/2-getHeight()/2);
//			setVisible(false);
//			editor.addMouseListener(copyMenu);
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//			//			ExceptionFrameDialog.showExceptionDialog(e);
//		}
	}




	public void actionPerformed(ActionEvent e)
	{
		Object o = e.getSource();

		if(o instanceof JButton)
		{
			String label = ((JButton)o).getActionCommand();

			if(label == LABEL_FRAME_INSERT_CANCEL)
			{
				super.setVisible(false);
				return ;
			}

			if(label == LABEL_FRAME_INSERT_OK)
			{
//				mainFrame.insertPSTricksCode(editor.getText());
				super.setVisible(false);

				return;
			}
		}
	}
//
//
//
//
//	@Override
//	public void setVisible(boolean e)
//	{
//		if(e)
//			editor.setText("");//$NON-NLS-1$
//
//		super.setVisible(e);
//	}
}

