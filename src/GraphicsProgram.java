import java.awt.*;
import java.io.*;
import java.util.*;

class GraphicsProgram extends Canvas
{
	Scanner sc = new Scanner(System.in);
	public GraphicsProgram()
	{
		System.out.print(1);
		setSize(200, 1000);
		setBackground(Color.white);
	}
	public static void main(String[] args)
	{
		System.out.print(2);
		GraphicsProgram GP = new GraphicsProgram();
		System.out.print(3);
		Frame aFrame = new Frame();
		aFrame.setSize(300, 300);
		
		aFrame.add(GP);
		System.out.print(4);
		aFrame.setVisible(true);
		System.out.print(5);
	}
	public void paint(Graphics g)
	{
		for(int i=0;i<5;i++){
		int x;
		x = sc.nextInt();
		g.clearRect(0,0,300,300);
		System.out.print(6);
		g.setColor(Color.blue);
		g.drawRect(0,0,x,x);
		}
	}
}