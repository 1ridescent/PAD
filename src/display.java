import java.util.*;
import java.awt.*;

class display extends Canvas
{
	static String path;
	public display()
	{
		setSize(600, 500);
		setBackground(Color.white);
	}
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		path = sc.next();
		display D = new display();
		Frame frame = new Frame();
		frame.setSize(600, 500);
		frame.add(D);
		frame.setVisible(true);
	}
	public void paint(Graphics g)
	{
		g.drawRect(10,10,100,100);
	}
}