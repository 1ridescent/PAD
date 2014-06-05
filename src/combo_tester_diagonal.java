import java.util.*;
import java.awt.*;

public class combo_tester_diagonal extends Canvas
{
	static Scanner sc = new Scanner(System.in);
	static RandBoard2[] list;
	static int[] prev;
	static int next;
	static PriorityQueue<Integer> Q;
	static HashSet<Integer> found;
	static int board_index;
	static String path;
	static void add_to_queue(RandBoard2 B, int prev_index)
	{
		if(B.num_moves > 20) return; // ** LIMIT ON NUMBER OF MOVES **
		int encode = B.encode();
		if(found.contains(encode)) return;
		found.add(encode);
		B.get_score();
		list[next] = B;
		prev[next] = prev_index;
		Q.add(next);
		next++;
	}
	static RandBoard2 current_board = new RandBoard2();
	static Stat before = new Stat();
	static Stat after = new Stat();
	static void run()
	{
		int num_iterations = 100000;
		int MAX = 8 * num_iterations + 100;
		list = new RandBoard2[MAX];
		prev = new int[MAX];
		next = 0;
		Q = new PriorityQueue<Integer>(MAX,
				new Comparator<Integer>()
				{
					public int compare(Integer p, Integer q)
					{
						return list[q].score - list[p].score;
					}
				} );
		found = new HashSet<Integer>(MAX);
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
			{
				RandBoard2 B = new RandBoard2(current_board);
				B.cur_r = i;
				B.cur_c = j;
				add_to_queue(B, -1);
			}
		int best_score = 0;
		int best_num_combos = 0;
		int best_board_index = 0;
		for(int iter = 0; iter < num_iterations; iter++)
		{
			board_index = Q.remove();
			int num_combos = list[board_index].num_combos;
			int score = list[board_index].score;
			if(num_combos > best_num_combos || num_combos == best_num_combos && score > best_score)
			{
				best_score = score;
				best_num_combos = num_combos;
				best_board_index = board_index;
			}
			if(list[board_index].cur_r > 0)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(-1, 0);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_r < 4)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(1, 0);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_c > 0)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(0, -1);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_c < 5)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(0, 1);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_r > 0 && list[board_index].cur_c > 0)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(-1, -1);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_r > 0 && list[board_index].cur_c < 5)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(-1, 1);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_r < 4 && list[board_index].cur_c > 0)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(1, -1);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_r < 4 && list[board_index].cur_c < 5)
			{
				RandBoard2 B = new RandBoard2(list[board_index]);
				B.move(1, 1);
				add_to_queue(B, board_index);
			}
		}
		System.out.println("num combos before cascades is " + list[best_board_index].num_combos);
		before.insert(list[best_board_index].num_combos);
		System.out.println("before cascades stats: sample size = " + before.data.size() + ", median = " + before.median() + ", mean = " + before.mean() + ", stdev = " + before.stdev());
		path = new String(); // this is the reverse path
		board_index = best_board_index;
		int prev_index = prev[board_index];
		while(prev_index != -1)
		{
			int dr = list[board_index].cur_r - list[prev_index].cur_r;
			int dc = list[board_index].cur_c - list[prev_index].cur_c;
			if(dr == -1 && dc == 0) path += '^';
			else if(dr == 1 && dc == 0) path += 'v';
			else if(dr == 0 && dc == -1) path += '<';
			else if(dr == 0 && dc == 1) path += '>';
			else if(dr == -1 && dc == -1) path += '`';
			else if(dr == -1 && dc == 1) path += '7';
			else if(dr == 1 && dc == -1) path += 'L';
			else if(dr == 1 && dc == 1) path += 'J';
			else path += '?';
			board_index = prev_index;
			prev_index = prev[prev_index];
		}
		found.clear();
		Q.clear();
	}
	public combo_tester_diagonal()
	{
		setSize(600, 500);
		setBackground(Color.white);
	}
	public static void main(String[] args)
	{
		combo_tester_diagonal graphics = new combo_tester_diagonal();
		Frame frame = new Frame();
		frame.setSize(600, 500);
		frame.add(graphics);
		frame.setVisible(true);
	}
	void grid(Graphics G)
	{
		G.setColor(Color.gray);
		for(int r = 0; r < 5; r++)
			for(int c = 0; c < 6; c++)
				G.drawRect(c * 100, r * 100, 100, 100);
	}
	Color purple = new Color(128, 0, 128);
	Color dark_red = new Color(160, 0, 0);
	Color dark_green = new Color(0, 160, 0);
	Color dark_yellow = new Color(160, 160, 0);
	void display(Graphics G, RandBoard2 B)
	{
		for(int r = 0; r < 5; r++)
			for(int c = 0; c < 6; c++)
			{
				char orb = B.board[B.index(r, c)];
				if(orb == 'r' || orb == '1')
					G.setColor(dark_red);
				else if(orb == 'b' || orb == '2')
					G.setColor(Color.blue);
				else if(orb == 'g' || orb == '3')
					G.setColor(dark_green);
				else if(orb == 'y' || orb == '4')
					G.setColor(dark_yellow);
				else if(orb == 'p' || orb == '5')
					G.setColor(purple);
				else if(orb == 'h' || orb == '6')
					G.setColor(Color.magenta);
				G.fillOval(c * 100, r * 100, 100, 100);
			}
	}
	int cur_x, cur_y;
	void travel(Graphics G, int dx, int dy)
	{
		G.drawLine(cur_x, cur_y, cur_x+dx, cur_y+dy);
		cur_x += dx;
		cur_y += dy;
	}
	public void paint(Graphics G)
	{
		while(true)
		{
			G.clearRect(0, 0, 600, 500);
			grid(G);
			display(G, current_board);
			System.out.println("calculating...");
			run();
			RandBoard2 B = new RandBoard2(list[board_index]);
			int start_x = B.cur_c * 100 + 25;
			int start_y = B.cur_r * 100 + 25;
			for(int i = path.length(); i >= 0; i--)
			{
				if(i < path.length())
				{
					if(path.charAt(i) == '^') B.move(-1, 0);
					else if(path.charAt(i) == 'v') B.move(1, 0);
					else if(path.charAt(i) == '<') B.move(0, -1);
					else if(path.charAt(i) == '>') B.move(0, 1);
					else if(path.charAt(i) == '`') B.move(-1, -1);
					else if(path.charAt(i) == '7') B.move(-1, 1);
					else if(path.charAt(i) == 'L') B.move(1, -1);
					else if(path.charAt(i) == 'J') B.move(1, 1);
					else return;
				}
				grid(G);
				display(G, B);
				G.setColor(Color.green);
				G.fillOval(start_x-10, start_y-10, 20, 20);
				cur_x = start_x;
				cur_y = start_y;
				G.setColor(Color.black);
				for(int j = path.length() - 1; j >= i; j--)
				{
					if(path.charAt(j) == '^') travel(G, 0, -100);
					else if(path.charAt(j) == 'v') travel(G, 0, 105);
					else if(path.charAt(j) == '<') travel(G, -100, 0);
					else if(path.charAt(j) == '>') travel(G, 105, 0);
					else if(path.charAt(j) == '`') travel(G, -100, -100);
					else if(path.charAt(j) == '7') travel(G, 105, -100);
					else if(path.charAt(j) == 'L') travel(G, -100, 105);
					else if(path.charAt(j) == 'J') travel(G, 105, 105);
					else return;
				}
				G.setColor(Color.yellow);
				G.fillOval(cur_x-10, cur_y-10, 20, 20);
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e)
				{
					return;
				}
			}
			G.setColor(Color.red);
			G.fillOval(cur_x-10, cur_y-10, 20, 20);
			B.num_combos = 0;
			do
			{
				G.setColor(Color.white);
				for(int r = 0; r < 5; r++)
					for(int c = 0; c < 6; c++)
						if(B.board[B.index(r, c)] == '.')
							G.fillOval(c * 100 + 25, r * 100 + 25, 50, 50);
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					return;
				}
				B.collapse();
				B.refresh();
				display(G, B);
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					return;
				}
			}
			while(B.find_matches());
			System.out.println("num combos after cascades is " + B.num_combos);
			after.insert(B.num_combos);
			System.out.println("after cascades stats: sample size = " + after.data.size() + ", median = " + after.median() + ", mean = " + after.mean() + ", stdev = " + after.stdev());
			current_board = new RandBoard2(B);
			current_board.num_combos = 0;
			current_board.num_moves = 0;
		}
	}
}
