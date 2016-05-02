import java.util.ArrayList;

public class PairSet<Type1,Type2>
{
	ArrayList<Type1> key;
	ArrayList<Type2> object;
	
	public PairSet()
	{
		key = new ArrayList<Type1>();
		object = new ArrayList<Type2>();
	}
	
	public void add(Type1 typ1, Type2 typ2)
	{
		if(!key.contains(typ1))
		{
			key.add(typ1);
			object.add(typ2);
		}
	}
	
	public void remove(Type1 typ1)
	{
		if(key.contains(typ1))
		{
			object.remove(key.indexOf(typ1));
			key.remove(typ1);
		}
	}
	
	public void changeValue(Type1 typ1, Type2 typ2)
	{
		if(key.contains(typ1))
		{
			object.set(key.indexOf(typ1), typ2);
		}
	}
	
	public void changeKey(Type2 typ2, Type1 typ1)
	{
		if(object.contains(typ2))
		{
			key.set(object.indexOf(typ2), typ1);
		}
	}
	
	public Type2 get(Type1 typ)
	{
		return object.get(key.indexOf(typ));
	}
	
	public Type2 getValue(int i)
	{
		if(i<object.size()-1)
			return object.get(i);
		return null;
	}
	
	public Type1 getKey(Type2 typ2)
	{
		if(object.contains(typ2))
		{
			return key.get(object.indexOf(typ2));
		}
		return null;
	}
	
	public boolean containsKey(Type1 typ1)
	{
		return key.contains(typ1);
	}
	
	public int size()
	{
		return key.size();
	}
}
