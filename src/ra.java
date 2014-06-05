import java.util.*;
import java.awt.*;

class Board
{
	int[] score_table = {0, 3, 8, 15, 24, 35};
	int index(int r, int c)
	{
		return r * 6 + c;
	}
	char[] board = new char[30];
	int cur_r = 0, cur_c = 0;
	int num_colors = 0;
	boolean[] cleared = new boolean[256];
	int num_moves = 0;
	int score;
	Board()
	{
		for(int i = 0; i < 30; i++) board[i] = '.';
		for(int i = 0; i < 256; i++) cleared[i] = false;
	}
	Board(Board copy)
	{
		board = copy.board.clone();
		cur_r = copy.cur_r;
		cur_c = copy.cur_c;
		num_colors = copy.num_colors;
		cleared = copy.cleared.clone();
		num_moves = copy.num_moves;
	}
	void exclude(String S)
	{
		for(int i = 0; i < S.length(); i++)
			cleared[Character.getNumericValue(S.charAt(i))] = true;
	}
	int encode()
	{
		return String.copyValueOf(board).hashCode() + cur_r * 100000007 + cur_c * 10007;
	}
	void move(int dr, int dc)
	{
		char temp = board[index(cur_r, cur_c)];
		board[index(cur_r, cur_c)] = board[index(cur_r + dr, cur_c + dc)];
		board[index(cur_r + dr, cur_c + dc)] = temp;
		cur_r += dr;
		cur_c += dc;
		num_moves++;
	}
	void unmove(int dr, int dc)
	{
		char temp = board[index(cur_r, cur_c)];
		board[index(cur_r, cur_c)] = board[index(cur_r - dr, cur_c - dc)];
		board[index(cur_r - dr, cur_c - dc)] = temp;
		cur_r -= dr;
		cur_c -= dc;
		num_moves--;
	}
	boolean find_matches()
	{
		boolean found_match = false;
		boolean[] match = new boolean[30];
		for(int i = 0; i < 30; i++) match[i] = false;
		// horizontal
		for(int i = 0; i < 5; i++)
		{
			for(int j = 1; j < 5; j++)
			{
				if(board[index(i, j)] != '.' && board[index(i, j-1)] == board[index(i, j)] && board[index(i, j)] == board[index(i, j+1)])
					match[index(i, j-1)] = match[index(i, j)] = match[index(i, j+1)] = true;
			}
		}
		// vertical
		for(int i = 1; i < 4; i++)
		{
			for(int j = 0; j < 6; j++)
			{
				if(board[index(i, j)] != '.' && board[index(i-1, j)] == board[index(i, j)] && board[index(i, j)] == board[index(i+1, j)])
					match[index(i-1, j)] = match[index(i, j)] = match[index(i+1, j)] = true;
			}
		}
		for(int i = 0; i < 30; i++)
			if(match[i])
			{
				if(!cleared[Character.getNumericValue(board[i])])
				{
					cleared[Character.getNumericValue(board[i])] = true;
					num_colors++;
				}
				board[i] = '.';
				found_match = true;
			}
		return found_match;
	}
	void collapse()
	{
		for(int j = 0; j < 6; j++)
		{
			int next = 4;
			for(int i = 4; i >= 0; i--)
			{
				if(board[index(i, j)] != '.')
				{
					board[index(next, j)] = board[index(i, j)];
					if(next != i) board[index(i, j)] = '.';
					next--;
				}
			}
		}
	}
	void process()
	{
		while(find_matches()) collapse();
	}
	void get_score()
	{
		num_colors = 0;
		Board test_board = new Board(this);
		test_board.process();
		num_colors = test_board.num_colors;
		score = score_table[num_colors] - num_moves;
	}
	void input()
	{
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < 5; i++)
		{
			String S = sc.next();
			for(int j = 0; j < 6; j++)
			{
				board[index(i, j)] = S.charAt(j);
			}
		}
	}
	void output()
	{
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 6; j++)
			{
				System.out.print(board[index(i, j)]);
			}
			System.out.println();
		}
	}
}

public class ra extends Canvas
{
	static Scanner sc = new Scanner(System.in);
	static Board[] list;
	static int[] prev;
	static int next;
	static PriorityQueue<Integer> Q;
	static HashSet<Integer> found;
	static int board_index;
	static String path;
	static void add_to_queue(Board B, int prev_index)
	{
		int encode = B.encode();
		if(found.contains(encode)) return;
		found.add(encode);
		B.get_score();
		list[next] = B;
		prev[next] = prev_index;
		Q.add(next);
		next++;
	}
	static void run()
	{
		Board initial_board = new Board();
		initial_board.input();
		String excluded = sc.next();
		initial_board.exclude(excluded);
		int num_iterations = sc.nextInt();
		int MAX = 4 * num_iterations + 100;
		list = new Board[MAX];
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
				Board B = new Board(initial_board);
				B.cur_r = i;
				B.cur_c = j;
				add_to_queue(B, -1);
			}
		int best_score = 0;
		int best_board_index = 0;
		for(int iter = 0; iter < num_iterations; iter++)
		{
			board_index = Q.remove();
			int score = list[board_index].score;
			if(score > best_score)
			{
				best_score = score;
				best_board_index = board_index;
			}
			if(list[board_index].cur_r > 0)
			{
				Board B = new Board(list[board_index]);
				B.move(-1, 0);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_r < 4)
			{
				Board B = new Board(list[board_index]);
				B.move(1, 0);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_c > 0)
			{
				Board B = new Board(list[board_index]);
				B.move(0, -1);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_c < 5)
			{
				Board B = new Board(list[board_index]);
				B.move(0, 1);
				add_to_queue(B, board_index);
			}
		}
		System.out.println("best score is " + best_score);
		System.out.println("num colors is " + list[best_board_index].num_colors);
		System.out.println("best board index is " + best_board_index);
		System.out.println("final board is");
		list[best_board_index].output();
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
			else path += '?';
			board_index = prev_index;
			prev_index = prev[prev_index];
		}
		System.out.print("best path starts at (r=" + list[board_index].cur_r + ", c=" + list[board_index].cur_c + ") and goes ");
		for(int i = path.length() - 1; i >= 0; i--) System.out.print(path.charAt(i));
		System.out.println();
		found.clear();
		Q.clear();
	}
	public ra()
	{
		setSize(600, 500);
		setBackground(Color.white);
	}
	public static void main(String[] args)
	{
		run();
		ra graphics = new ra();
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
	void display(Graphics G, Board B)
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
			Board B = new Board(list[board_index]);
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
			System.out.println("press any key to replay, or q to quit");
			String query = sc.next();
			if(query.charAt(0) == 'q') System.exit(0);
			G.clearRect(0, 0, 600, 500);
		}
	}
}
