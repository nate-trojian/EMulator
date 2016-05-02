import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class EMConsole extends JFrame implements KeyListener
{
	private static final long	serialVersionUID	= 1L;
	static final String newline = System.getProperty("line.separator");
	private JTextArea textArea;
	private SetStack key = new SetStack();

	public EMConsole() {
        createAndShowGUI();
    }
	
	private void createAndShowGUI() {
        setTitle("CONSOLE");
        setSize(300,350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        textArea = new JTextArea(30,35);
        textArea.setEditable(true);
        textArea.addKeyListener(this);
        //textArea.getDocument().addDocumentListener(this);
        JScrollPane scrollPane = new JScrollPane(textArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
 
        pack();
        setVisible(true);
    }
	
	public void close()
	{
		dispose();
	}
	
	public String getText()
	{
		return textArea.getText();
	}

    public void keyTyped(KeyEvent e) {
    	//key.push((int)e.getKeyChar());
        //displayInfo(e, "KEY TYPED: ");
    }
    
    public void keyPressed(KeyEvent e) {
    	boolean temp = key.push(e.getKeyCode());
    	//System.out.println("Pressed " + e.getKeyCode() + " " + temp);
        //displayInfo(e, "KEY PRESSED: ");
    }
    
    public void keyReleased(KeyEvent e)
    {
    	//System.out.println("Released " + e.getKeyCode());
    	ArrayList<Integer> temp = new ArrayList<Integer>();
    	while(e.getKeyCode() != key.peek())
    	{
    		temp.add(key.pop());
    	}
    	key.pop();
    	for(int i=temp.size()-1;i>=0;i--)
    	{
    		key.push(temp.get(i));
    	}
    }
	
	private void displayInfo(KeyEvent e, String keyStatus)
	{     
        //You should only rely on the key char if the event
        //is a key typed event.
        int id = e.getID();
        String keyString;
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            int keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode
                    + " ("
                    + KeyEvent.getKeyText(keyCode)
                    + ")";
        }
        
        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }
        
        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }
        
        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }
        
        System.out.println(keyStatus + newline
                + "    " + keyString + newline
                + "    " + modString + newline
                + "    " + actionString + newline
                + "    " + locationString + newline);
	 }
	
	public int getKey()
	{
		try
		{
			return key.peek();
		}
		catch(Exception e)
		{
			
		}
		return 0;
	}
}
