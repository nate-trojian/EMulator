import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class EMulator
{
	public static void main(String[] args)
	{
		EMulator em = new EMulator();
	}
	
	public EMulator()
	{
		//Start at 0x5000
		//Set-up
		registry.add("AX", "0");
		address.add( "0x5000", "AX");
		registry.add("AH", "0");
		address.add( "0x5001", "AH");
		registry.add("AL", "0");
		address.add( "0x5002", "AL");
		registry.add("BX", "0");
		address.add( "0x5003", "BX");
		registry.add("BH", "0");
		address.add( "0x5004", "BH");
		registry.add("BL", "0");
		address.add( "0x5005", "BL");
		registry.add("CX", "0");
		address.add( "0x5006", "CX");
		registry.add("CH", "0");
		address.add( "0x5007", "CH");
		registry.add("CL", "0");
		address.add( "0x5008", "CL");
		registry.add("DX", "0");
		address.add( "0x5009", "DX");
		registry.add("DH", "0");
		address.add( "0x5010", "DH");
		registry.add("DL", "0");
		address.add( "0x5011", "DL");
		
		registry.add("PC", "0"); //Program Counter
		address.add( "0x1000", "AX");
		registry.add("SP", "8000"); //Stack Pointer
		address.add( "0x8000", "SP");
		
		registry.add("CS", "0");
		address.add("0x5012", "CS");
		registry.add("DS", "0");
		address.add( "0x5014", "DS");
		registry.add("SS", "0");
		address.add( "0x5015", "SS");
		registry.add("ES", "0");
		address.add( "0x5016", "ES");
		
		//Added variables for user convenience
		registry.add("A", "0");
		address.add( "0x1001", "A");
		registry.add("B", "0");
		address.add( "0x1002", "B");
		registry.add("C", "0");
		address.add( "0x1003", "C");
		registry.add("X", "0");
		address.add( "0x1004", "X");
		registry.add("Y", "0");
		address.add( "0x1005", "Y");
		registry.add("Z", "0");
		address.add( "0x1006", "Z");
		registry.add("I", "0");
		address.add( "0x1007", "I");
		registry.add("J", "0");
		address.add( "0x1008", "J");
		
		/*
		 * Registry: needs name and value
		 * Value will be hex string
		 * Decide whether int or string when executing
		 * 
		 * Need List for which registers were updated
		 * Update Xregs, Hregs, and Lregs appropriately
		 * 
		 * Next: Running from console
		 */
		
		commands.add(":", -1);
		commands.add("INT", 0);
		commands.add("MOV", 1);
		commands.add("INC", 2);
		commands.add("DEC", 3);
		commands.add("ADD", 4);
		commands.add("SUB", 5);
		commands.add("IFE", 6);
		commands.add("IFN", 7);
		commands.add("IFL", 8);
		commands.add("IFG", 9);
		commands.add("JSR", 10);
		commands.add("MUL", 11);
		commands.add("DIV", 12);
		commands.add("MOD", 13);
		commands.add("SHL", 14);
		commands.add("SHR", 15);
		commands.add("AND", 16);
		commands.add("BOR", 17);
		commands.add("XOR", 18);
		commands.add("HSL", 19);
		commands.add("HSR", 20);
		
		
		//Running
		Scanner scan = new Scanner(System.in);
		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			if(skipNextLine)
			{
				skipNextLine = false;
				continue;
			}
			String command = "";
			line = removeTabs(line);
			if(line.startsWith(";") || line.equals(""))
			{
				continue;
			}
			else if(line.startsWith(":"))
			{
				command = ":";
				line = line.substring(1);
			}
			else
			{
				try
				{
					command = line.substring(0,line.indexOf(' '));
					line = line.substring(line.indexOf(' ')+1);
				}
				catch(Exception e)
				{
					
				}
			}
			if(commands.containsKey(command))
			{
				int com = commands.get(command);
				executeLine(line,com);
				if(updated.size() > 0)
				{
					handleUpdates();
				}
			}
			else
			{
				System.out.println("Invalid Command");
			}
			if(exit)
			{
				break;
			}
		}
		if(consoles.size() > 0)
		{
			for(int i =0; i< consoles.size(); i++)
				consoles.get(i).close();
		}
		if(monitors.size() > 0)
		{
			for(int i =0; i< monitors.size(); i++)
				monitors.get(i).close();
		}
	}

	public String removeTabs(String line)
	{
		String ret = "";
		char[] chars = line.toCharArray();
		for(char c : chars)
		{
			if(c!='\t')
				ret+=c + "";
		}
		return ret;
	}

	public void executeLine(String ln, int comm)
	{
		int first = 0;
		int second = 0;
		int third = 0;
		String fInput, sInput;
		switch(comm)
		{
			case -1:
				labels.add(ln, Integer.parseInt(registry.get("PC"), 16));
				break;
			case 0:
				try{
					first = intParseInput(ln);
				}
				catch(Exception e)
				{
					break;
				}
				if(first == 32)
				{
					//Here starts the huge if loop
					second = Integer.parseInt(registry.get("AH"),16);
					if(second == 0)
					{
						//AH = 0
						//exit = true
						exit = true;
					}
					else if(second == 1)
					{
						//AH = 1
						//Read next char - put into AL
						if(consoles.size() == 1)
						{
							third = consoles.get(0).getKey();
							System.out.println("Button " + (char) third);
							registry.changeValue("AL",Integer.toHexString(third));
							return;
						}
						else if(monitors.size() == 1)
						{
							third = monitors.get(0).getKey();
							System.out.println("Button " + (char) third);
							registry.changeValue("AL",Integer.toHexString(third));
							return;
						}
					}
					else if(second == 2)
					{
						//AH = 2
						//Print int, DL = data
						third = Integer.parseInt(registry.get("DL"), 16);
						System.out.println(third);
					}
					else if(second == 9)
					{
						//AH = 9
						//Print String, DS = data, DX = offset
						third = Integer.parseInt(registry.get("DX"),16);
						System.out.println(strParseInput(registry.get("DS").substring(third*2))); //Each character is two bits
					}
					else if(second == 61) //Integer.parseInt("3d", 16)
					{
						//AH = 3d, BX = 1
						//Open Console
						//AH = 3d, BX = 2
						//Open Monitor
						third = Integer.parseInt(registry.get("BX"),16);
						if(third == 1 && consoles.size() == 0)
						{
							consoles.add(new EMConsole());
						}
						if(third == 2 && monitors.size() == 0)
						{
							monitors.add(new EMMonitor());
						}
					}
					else if(second == 62) //Integer.parseInt("3e", 16)
					{
						//AH = 3e, BX = 1
						//Close Console
						//AH = 3e, BX = 2
						//Close Monitor
						third = Integer.parseInt(registry.get("BX"),16);
						if(third == 1 && consoles.size() == 1)
						{
							consoles.get(0).close();
							consoles.remove(0);
						}
						if(third == 2 && monitors.size() == 1)
						{
							monitors.get(0).close();
							monitors.remove(0);
						}
					}
					else if(second == 63) //Integer.parseInt("3f", 16)
					{
						//AH = 3f, BX = 1
						//Read from Console
						//if CX = 0 read all, else read line CX - 1
						third = Integer.parseInt(registry.get("BX"), 16);
						if(third == 1 && consoles.size() == 1)
						{
							//System.out.println("Got Here");
							executeConsole(consoles.get(0).getText(), Integer.parseInt(registry.get("CX"), 16)); //W00t it works
						}
						//BX = 2
						//Read from monitor
						//CH = row, CL = col
						//Store data in DS
						if(third == 2 && monitors.size() == 1)
						{
							registry.changeValue("DS", monitors.get(0).getPixel(Integer.parseInt(registry.get("CH"), 16), Integer.parseInt(registry.get("CL"), 16)));
						}
					}
					else if(second == 64) //Integer.parseInt("40", 16)
					{
						//AH = 40, BX = 2
						//Write to Monitor, DS = data
						//DH: row, DL: col
						//DS: First 8 chars CCRRGGBB, rest are ignored
						third = Integer.parseInt(registry.get("BX"), 16);
						if(third == 2 && monitors.size() > 0)
						{
							monitors.get(0).setPixel(Integer.parseInt(registry.get("DH"), 16), Integer.parseInt(registry.get("DL"), 16), registry.get("DS"));
						}
					}
				}
				break;
			case 1:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							registry.changeValue(fInput, registry.get(sInput));
							updated.add(fInput);
							return;
						}
						else if(labels.containsKey(sInput))
						{
							second = labels.get(sInput);
							//stack.push(registry.get("PC"));
							registry.changeValue(fInput, Integer.toHexString(second));
							updated.add(fInput);
							return;
						}
						else if(sInput.equals("POP"))
						{
							registry.changeValue(fInput, stack.pop());
							third = Integer.parseInt(registry.get("SP"), 16);
							third--;
							registry.changeValue("SP", Integer.toHexString(third));
							return;
						}
						else if(sInput.equals("PEEK"))
						{
							registry.changeValue(fInput, stack.peek());
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
								System.out.println("Error " + e);
							}
						}
					}
					else if((fInput.charAt(fInput.length()-1) == ']') && (fInput.charAt(0) == '['))
					{
						fInput = fInput.substring(1, fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								intVars.changeValue(fInput, registry.get(sInput));
								return;
							}
							else if(sInput.equals("POP"))
							{
								intVars.changeValue(fInput, stack.pop());
								third = Integer.parseInt(registry.get("SP"), 16);
								third--;
								registry.changeValue("SP", Integer.toHexString(third));
								return;
							}
							else if(sInput.equals("PEEK"))
							{
								intVars.changeValue(fInput, stack.peek());
								return;
							}
							else
							{
								try
								{
									second = intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
									System.out.println("Error " + e);
								}
							}
						}
					}
					else if(fInput.equals("PUSH"))
					{
						if(registry.containsKey(sInput))
						{
							stack.push(registry.get(sInput));
							third = Integer.parseInt(registry.get("SP"), 16);
							third++;
							registry.changeValue("SP", Integer.toHexString(third));
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								stack.push(Integer.toHexString(second));
								third = Integer.parseInt(registry.get("SP"), 16);
								third++;
								registry.changeValue("SP", Integer.toHexString(third));
								return;
							}
							catch(Exception e)
							{
								System.out.println("Error " + e);
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 2:
				if(registry.containsKey(ln))
				{
					second = Integer.parseInt(registry.get(ln),16);
					second++;
					registry.changeValue(ln, Integer.toHexString(second));
					updated.add(ln);
					return;
				}
				System.out.println("Invalid Input");
				break;
			case 3:
				if(registry.containsKey(ln))
				{
					second = Integer.parseInt(registry.get(ln),16);
					second--;
					registry.changeValue(ln, Integer.toHexString(second));
					updated.add(ln);
					return;
				}
				System.out.println("Invalid Input");
				break;
			case 4:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) + Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput) + Integer.parseInt(registry.get(fInput), 16);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) + Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = intParseInput(sInput) + Integer.parseInt(intVars.get(fInput), 16);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 5:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) - Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							return;
						}
						else
						{
							try
							{
								second = Integer.parseInt(registry.get(fInput), 16) - intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) - Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = Integer.parseInt(intVars.get(fInput), 16) - intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 6:
				//Do next line if they are equal
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							if(!registry.get(fInput).equals(registry.get(sInput)))
							{
								skipNextLine = true;
							}
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								first = Integer.parseInt(registry.get(fInput), 16);
								if(first != second)
								{
									skipNextLine = true;
								}
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 7:
				//Do next line if they are not equal
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							if(registry.get(fInput).equals(registry.get(sInput)))
							{
								skipNextLine = true;
							}
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								first = Integer.parseInt(registry.get(fInput), 16);
								if(first == second)
								{
									skipNextLine = true;
								}
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 8:
				//Do next line if they first < second
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							first = Integer.parseInt(registry.get(fInput), 16);
							second = Integer.parseInt(registry.get(sInput), 16);
							if(!(first < second))
							{
								skipNextLine = true;
							}
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								first = Integer.parseInt(registry.get(fInput), 16);
								if(!(first < second))
								{
									skipNextLine = true;
								}
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 9:
				//Do next line if first > second
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							first = Integer.parseInt(registry.get(fInput), 16);
							second = Integer.parseInt(registry.get(sInput), 16);
							if(!(first > second))
							{
								skipNextLine = true;
							}
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								first = Integer.parseInt(registry.get(fInput), 16);
								if(!(first > second))
								{
									skipNextLine = true;
								}
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 10:
				fInput = ln;
				if(labels.containsKey(fInput))
				{
					first = Integer.parseInt(registry.get("PC"), 16);
					first++;
					stack.push(Integer.toHexString(first));
					registry.changeValue("PC", Integer.toHexString(labels.get(fInput)));
					return;
				}
				else
				{
					try
					{
						first = Integer.parseInt(registry.get("PC"), 16);
						first++;
						stack.push(Integer.toHexString(first));
						second = intParseInput(fInput);
						registry.changeValue("PC", Integer.toHexString(second));
						return;
					}
					catch(Exception e)
					{
					}
				}
				System.out.println("Invalid Input");
				break;
			case 11:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) * Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							return;
						}
						else
						{
							try
							{
								second = Integer.parseInt(registry.get(fInput), 16) * intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) * Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = intParseInput(sInput) * Integer.parseInt(intVars.get(fInput), 16);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 12:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(sInput), 16);
							if(second != 0)
							{
								second = Integer.parseInt(registry.get(fInput), 16) / second;
							}
							registry.changeValue(fInput, Integer.toHexString(second));
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								if(second != 0)
								{
									second = Integer.parseInt(registry.get(fInput), 16) / second;
								}
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(registry.get(sInput), 16);
								if(second != 0)
								{
									second = Integer.parseInt(intVars.get(fInput), 16) / second;
								}
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = intParseInput(sInput);
									if(second != 0)
									{
										second = Integer.parseInt(intVars.get(fInput), 16) / second;
									}
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 13:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						first = Integer.parseInt(registry.get(fInput), 16);
						if(registry.containsKey(sInput))
						{
							second = first % Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = first % intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
								System.out.println("Error "+ e + " " + first + " " + second);
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) % Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = Integer.parseInt(intVars.get(fInput), 16) % intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 14:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) << Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = Integer.parseInt(registry.get(fInput), 16) << intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) << Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = Integer.parseInt(intVars.get(fInput), 16) << intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 15:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) >> Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = Integer.parseInt(registry.get(fInput), 16) >> intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) >> Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							else
							{
								try
								{
									second = Integer.parseInt(intVars.get(fInput), 16) >> intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									updated.add(fInput);
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 16:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) & Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = Integer.parseInt(registry.get(fInput), 16) & intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) & Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = Integer.parseInt(intVars.get(fInput), 16) & intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 17:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) | Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = Integer.parseInt(registry.get(fInput), 16) | intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) | Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = Integer.parseInt(intVars.get(fInput), 16) | intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 18:
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(fInput), 16) ^ Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, Integer.toHexString(second));
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = Integer.parseInt(registry.get(fInput), 16) ^ intParseInput(sInput);
								registry.changeValue(fInput, Integer.toHexString(second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
					else if(fInput.substring(0,1).equals("[") && fInput.substring(fInput.length()-1).equals("]"))
					{
						fInput = fInput.substring(1,fInput.length()-1);
						if(intVars.containsKey(fInput))
						{
							if(registry.containsKey(sInput))
							{
								second = Integer.parseInt(intVars.get(fInput), 16) ^ Integer.parseInt(registry.get(sInput), 16);
								intVars.changeValue(fInput, Integer.toHexString(second));
								return;
							}
							else
							{
								try
								{
									second = Integer.parseInt(intVars.get(fInput), 16) ^ intParseInput(sInput);
									intVars.changeValue(fInput, Integer.toHexString(second));
									return;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 19:
				//Hex shift left
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(sInput), 16);
							String zero = "";
							for(int i=0; i<second; i++)
							{
								zero += "0";
							}
							registry.changeValue(fInput, registry.get(fInput) + zero);
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								String zero = "";
								for(int i=0; i<second; i++)
								{
									zero += "0";
								}
								registry.changeValue(fInput, registry.get(fInput) + zero);
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			case 20:
				//Hex shift right
				if(ln.indexOf(", ") != -1)
				{
					fInput = ln.substring(0,ln.indexOf(", "));
					sInput = ln.substring(ln.indexOf(", ")+2);
					if(registry.containsKey(fInput))
					{
						if(registry.containsKey(sInput))
						{
							second = Integer.parseInt(registry.get(sInput), 16);
							registry.changeValue(fInput, registry.get(fInput).substring(0, registry.get(fInput).length()-second));
							updated.add(fInput);
							return;
						}
						else
						{
							try
							{
								second = intParseInput(sInput);
								registry.changeValue(fInput, registry.get(fInput).substring(0, registry.get(fInput).length()-second));
								updated.add(fInput);
								return;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
				System.out.println("Invalid Input");
				break;
			default:
				break;
		}
	}
	
	public void executeConsole(String cnLines, int lnNum)
	{
		Scanner scan = new Scanner(cnLines);
		ArrayList<String> lines = new ArrayList<String>();
		while(scan.hasNextLine())
		{
			lines.add(removeTabs(scan.nextLine()));
		}
		//Cycle through lines to get labels
		for(int i = 0; i<lines.size();i++)
		{
			//Remove blank lines
			if(lines.get(i).equals(""))
			{
				lines.remove(i);
				i--;
			}
			if(lines.get(i).startsWith(":"))
			{
				if(lines.get(i+1).contains("DAT"))
				{
					try
					{
						intVars.add(lines.get(i).substring(1), Integer.toHexString(intParseInput(lines.get(i+1).substring(lines.get(i+1).indexOf(' ')+1))));
						System.out.println("NAME " + lines.get(i).substring(1) + " DATA " + Integer.toHexString(intParseInput(lines.get(i+1).substring(lines.get(i+1).indexOf(' ')+1))));
					}
					catch(Exception e)
					{
						
					}
				}
				else
				{
					labels.add(lines.get(i).substring(1), i);
				}
			}
		}
		//System.out.println("Lines Size " + lines.size());
		String command = "";
		String line = "";
		//fix for CX value
		do
		{
			if(Integer.parseInt(registry.get("PC"),16) >= lines.size())
			{
				break;
			}
			if(skipNextLine)
			{
				skipNextLine = false;
				executeLine("PC", 2);
				handleUpdates();
				continue;
			}
			line = lines.get(Integer.parseInt(registry.get("PC"), 16));
			System.out.println(line + " " + Integer.parseInt(registry.get("PC"),16));
			if(line.startsWith(":"))
			{
				command = ":";
				line = line.substring(1);
			}
			else if(line.startsWith(";"))
			{
				command = "INC";
				line = "PC";
			}
			else
			{
				command = line.substring(0,line.indexOf(' '));
				line = line.substring(line.indexOf(' ')+1);
			}
			if(commands.containsKey(command))
			{
				int com = commands.get(command);
				//System.out.println("Line " + line + " Command " + com);
				executeLine(line,com);
				if(updated.size() > 0)
				{
					if(!updated.contains("PC"))
					{
						executeLine("PC", 2);
					}
				}
				else
				{
					executeLine("PC", 2);
				}
				handleUpdates();
			}
			else
			{
				System.out.println("Invalid Command");
			}
		} while(!exit);
		exit = false;
		skipNextLine = false;
	}
	
	public int intParseInput(String input)
	{
		if((input.charAt(input.length()-1) == ']') && (input.charAt(0) == '['))
		{
			//Is an address, so get literal or variable value 
			input = input.substring(1,input.length()-1);
			
			//System.out.println(input + " " + address.containsKey("0x" + input));
			if(registry.containsKey(input))
			{
				//Is in registry
				if(address.containsKey("0x" + registry.get(input)))
				{
					//Is in address
					return Integer.parseInt(registry.get(address.get("0x" + registry.get(input))), 16);
				}
				else if((Integer.parseInt(registry.get(input), 16) & 0x8000) == 0x8000)
				{
					return Integer.parseInt(getIndexStack(Integer.parseInt(registry.get(input), 16) & 0x000F), 16);
				}
			}
			else if(address.containsKey("0x" + input))
			{
				//System.out.println(input + " " + Integer.parseInt(registry.get(address.get("0x" + input)), 16));
				return Integer.parseInt(registry.get(address.get("0x" + input)), 16);
			}
			else if(intVars.containsKey(input))
			{
				return Integer.parseInt(intVars.get(input), 16);
			}
			else if((Integer.parseInt(input, 16) & 0xF000) == 0x8000)
			{
				return Integer.parseInt(getIndexStack(Integer.parseInt(input, 16) & 0x000F), 16);
			}
		}
		int radix = 10;
		if(input.charAt(input.length()-1) == 'h')
		{
			input = input.substring(0, input.length()-1);
			radix = 16;
		}
		return Integer.valueOf(input,radix);
	}
	
	public String strParseInput(String input)
	{
		String ret = "";
		if(input.length()%2 == 1)
		{
			input = input.substring(0, input.length()-1);
		}
		
		for(int i=0;i<input.length();i+=2)
		{
			ret += (char) Integer.parseInt(input.substring(i,i+2), 16);
		}
		
		return ret;
	}
	
	public void handleUpdates()
	{
		String nm = "";
		String val = "";
		for(int i=0; i<updated.size(); i++)
		{
			if(updated.get(i).endsWith("X"))
			{
				//Xreg
				nm = updated.get(i).substring(0,1) + "L";
				val = registry.get(updated.get(i));
				registry.changeValue(nm, val.substring(val.length()/2 + 1));
				nm = updated.get(i).substring(0,1) + "H";
				registry.changeValue(nm, val.substring(0,val.length()/2 + 1));
			}
			else if(updated.get(i).endsWith("H"))
			{
				//Hreg
				nm = updated.get(i).substring(0,1) + "X";
				val = registry.get(updated.get(i));
				registry.changeValue(nm, val + registry.get(updated.get(i).substring(0,1) + "L"));
			}
			else if(updated.get(i).endsWith("L"))
			{
				//Lreg
				nm = updated.get(i).substring(0,1) + "X";
				val = registry.get(updated.get(i));
				registry.changeValue(nm, registry.get(updated.get(i).substring(0,1) + "H") + val);
			}
		}
		
		updated.clear();
		
		for(int i=0;i<registry.size();i++)
		{
			if(registry.get(i) == null)
			{
				registry.changeValue(registry.getKey(i), "00");
			}
			else if(registry.get(i).equals(""))
			{
				registry.changeValue(registry.getKey(i), "00");
			}
			//System.out.println(registry.getKey(i) + " " +registry.get(i));
		}
	}
	
	public String getIndexStack(int index)
	{
		Stack<String> temp = new Stack<String>();
		String ret = "";
		for(int i=0;i<index;i++)
		{
			temp.push(stack.pop());
		}
		ret = stack.peek();
		while(!temp.empty())
		{
			stack.push(temp.pop());
		}
		return ret;
	}
	
	StringPairSet registry = new StringPairSet();
	PairSet<String,String> address = new PairSet<String,String>();
	PairSet<String,Integer> commands = new PairSet<String,Integer>();
	ArrayList<String> updated = new ArrayList<String>();
	
	ArrayList<EMConsole> consoles = new ArrayList<EMConsole>();
	ArrayList<EMMonitor> monitors = new ArrayList<EMMonitor>();
	
	PairSet<String,Integer> labels = new PairSet<String,Integer>();
	
	StringPairSet intVars = new StringPairSet();
	PairSet<String,String> strVars = new PairSet<String,String>();
	
	//organize data - ALMOST
	//Allow for different types - DONE
	
	boolean skipNextLine = false;
	boolean exit = false;
	Stack<String> stack = new Stack<String>();
}