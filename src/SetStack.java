import java.util.ArrayList;
import java.util.Stack;

public class SetStack
{
	Stack<Integer> stack = new Stack<Integer>();
	ArrayList<String> set = new ArrayList<String>();
	
	public SetStack()
	{
	}
	
	public boolean push(int in)
	{
		if(!set.contains(Integer.toHexString((in))))
		{
			stack.push(in);
			set.add(Integer.toHexString((in)));
			return true;
		}
		return false;
	}
	
	public boolean push(String in)
	{
		if(!set.contains(in))
		{
			stack.push(Integer.parseInt(in, 16));
			set.add(in);
			return true;
		}
		return false;
	}
	
	public int peek()
	{
		return stack.peek();
	}
	
	public int pop()
	{
		int ret = stack.pop();
		set.remove(Integer.toHexString(ret));
		return ret;
	}
	
	//Add getIndex here
	public int elementAt(int index)
	{
		return stack.elementAt(index);
	}
}
