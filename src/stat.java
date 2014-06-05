import java.util.*;
import java.awt.*;

class Stat
{
	ArrayList<Integer> data;
	Stat()
	{
		data = new ArrayList<Integer>();
	}
	void insert(int x)
	{
		data.add(x);
	}
	double mean()
	{
		int total = 0;
		for(int i = 0; i < data.size(); i++)
			total += data.get(i);
		return ((double)(total)) / data.size();
	}
	double median()
	{
		Collections.sort(data);
		if(data.size() % 2 == 1)
		{
			return (double)(data.get(data.size()/2));
		}
		else
		{
			return ((double)(data.get(data.size()/2-1) + data.get(data.size()/2))) / 2.0;
		}
	}
	double stdev()
	{
		double m = mean();
		double total = 0;
		for(int i = 0; i < data.size(); i++)
		{
			total += (data.get(i) - m) * (data.get(i) - m);
		}
		total = total / data.size();
		return Math.sqrt(total);
	}
}

public class stat
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		int N = sc.nextInt();
		Stat S = new Stat();
		for(int i = 0; i < N; i++)
		{
			int x = sc.nextInt();
			S.insert(x);
		}
		System.out.println("mean = " + S.mean() + ", median = " + S.median() + ", stdev = " + S.stdev());
	}
}