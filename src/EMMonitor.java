import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EMMonitor extends JFrame implements KeyListener
{
	private static final long	serialVersionUID	= 1L;
	static final String newline = System.getProperty("line.separator");
	private SetStack key = new SetStack();
	
	private ArrayList2D<JLabel> monitor = new ArrayList2D<JLabel>();
	private int rowSize = 32;
	private int colSize = 32;
	private int lblwidth = 10;
	private int lblheight = 15;
	
	private GridLayout grid = new GridLayout(rowSize,colSize);
	JPanel panel = new JPanel();

	public EMMonitor()
	{
		createAndShowGUI();
	}
	
	public void createAndShowGUI()
	{
		/*JLabel label = new JLabel();
		label.setBackground(new Color(125,125,125));
		label.setSize(5, 15);*/
		panel.setLayout(grid);
		panel.setPreferredSize(new Dimension(lblheight*rowSize,colSize*lblheight));
		for(int row=0;row<rowSize;row++)
		{
			for(int col=0;col<colSize;col++)
			{
				monitor.add(row, new JLabel());
				monitor.get(row, col).setSize(lblwidth, lblheight);
				monitor.get(row, col).setOpaque(true);
				monitor.get(row, col).setBackground(new Color(125,125,125));
				monitor.get(row, col).setHorizontalAlignment(JLabel.CENTER); //CENTER
				monitor.get(row, col).setVerticalAlignment(JLabel.CENTER); //CENTER
				panel.add(monitor.get(row, col));
			}
		}
		panel.addKeyListener(this);
		setTitle("MONITOR");
        setSize(colSize*lblwidth,lblheight*rowSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().add(panel);
		
		pack();
        setVisible(true);
	}
	
	public void setPixel(int row, int col, String data)
	{
		String text = "";
		if(data.length() >= 8)
		{
			if(!data.substring(0,2).equals("00"))
				text = "" + (char)Integer.parseInt(data.substring(0, 2),16);
			int red = Integer.parseInt(data.substring(2,4), 16);
			int green = Integer.parseInt(data.substring(4,6), 16);
			int blue = Integer.parseInt(data.substring(6,8), 16);
			monitor.get(row, col).setBackground(new Color(red,green,blue));
		}
		else if(data.length() == 6)
		{
			int red = Integer.parseInt(data.substring(0,2), 16);
			int green = Integer.parseInt(data.substring(2,4), 16);
			int blue = Integer.parseInt(data.substring(4,6), 16);
			monitor.get(row, col).setBackground(new Color(red,green,blue));
		}
		else if(data.length() >= 2)
		{
			if(!data.substring(0,2).equals("00"))
				text = data.substring(0, 2);
		}
		else
		{
			return;
		}
		monitor.get(row, col).setText(text);
	}
	
	public String getPixel(int row, int col)
	{
		try
		{
			String text = "";
			if(row>rowSize-1 || row<0 || col<0 || col>colSize-1)
				throw new RuntimeException("Out of Bounds");
			if(!monitor.get(row, col).getText().equals(""))
				text += Integer.toHexString(monitor.get(row, col).getText().charAt(0));
			text += Integer.toHexString(monitor.get(row, col).getBackground().getRed());
			text += Integer.toHexString(monitor.get(row, col).getBackground().getGreen());
			text += Integer.toHexString(monitor.get(row, col).getBackground().getBlue());
			return text;
		}
		catch(Exception e)
		{
			return "";
		}
	}
	
	public void close()
	{
		dispose();
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
