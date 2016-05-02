import java.util.ArrayList;

public class ArrayList2D<Type>
{
	ArrayList<ArrayList<Type>>	array;
	
	public ArrayList2D()
	{
		array = new ArrayList<ArrayList<Type>>();
	}
	
	public void ensureCapacity(int num)
	{
		array.ensureCapacity(num);
	}
	
	public void ensureCapacity(int row, int num)
	{
		ensureCapacity(row);
		while (row > getNumRows())
		{
			array.add(new ArrayList<Type>());
		}
		array.get(row).ensureCapacity(num);
	}
	
	public void add(int row, Type data)
	{
		ensureCapacity(row);
		while(row >= getNumRows())
		{
			array.add(new ArrayList<Type>());
		}
		array.get(row).add(data);
	}
	
	public Type get(int row, int col)
	{
		return array.get(row).get(col);
	}
	
	public void set(int row, int col, Type data)
	{
		array.get(row).set(col,data);
	}
	
	public void set(int row, ArrayList<Type> data)
	{
		array.set(row, data);
	}
	
	public void remove(int row, int col)
	{
		array.get(row).remove(col);
	}
	
	public void remove(int row,Type data)
	{
		array.get(row).remove(indexOf(row,data));
	}
	
	public boolean contains(Type data)
	{
		for (int i = 0; i < array.size(); i++)
		{
			if (array.get(i).contains(data))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(int row, Type data)
	{
		return array.get(row).contains(data);
	}
	
	public int getNumRows()
	{
		return array.size();
	}
	
	public int getNumCols(int row)
	{
		return array.get(row).size();
	}
	
	public ArrayList<Type> getRow(int row)
	{
		return array.get(row);
	}
	
	public void createRows(int row)
	{
		while(row > getNumRows())
		{
			array.add(new ArrayList<Type>());
		}
	}
	
	public int indexOf(int row, Type data)
	{
		return array.get(row).indexOf(data);
	}
	
	public void clearAll()
	{
		array.clear();
	}
}