import java.util.ArrayList;

public class StringPairSet
{
	ArrayList<String> key;
	ArrayList<String> object;
	
	public StringPairSet()
	{
		key = new ArrayList<String>();
		object = new ArrayList<String>();
	}
	
	public void add(String k, String o)
	{
		if(!key.contains(k))
		{
			key.add(k);
			object.add(format(o));
		}
	}
	
	public void remove(String k)
	{
		if(key.contains(k))
		{
			object.remove(key.indexOf(k));
			key.remove(k);
		}
	}
	
	public void changeValue(String k, String o)
	{
		if(key.contains(k))
		{
			object.set(key.indexOf(k), format(o));
		}
	}
	
	public void changeKey(String k, String o)
	{
		if(object.contains(format(o)))
		{
			key.set(object.indexOf(format(o)), k);
		}
	}
	
	public String get(String k)
	{
		if(key.contains(k))
			return object.get(key.indexOf(k));
		return null;
	}
	
	public String get(int i)
	{
		if(i<object.size())
			return object.get(i);
		return null;
	}
	
	public String getKey(String o)
	{
		if(object.contains(format(o)))
		{
			return key.get(object.indexOf(format(o)));
		}
		return null;
	}
	
	public String getKey(int i)
	{
		if(i<key.size())
			return key.get(i);
		return null;
	}
	
	public boolean containsKey(String k)
	{
		return key.contains(k);
	}
	
	private String format(String in)
	{
		if(in.length()%2 ==1)
		{
			in = "0" + in;
		}
		return in;
	}
	
	public int size()
	{
		return key.size();
	}
}
