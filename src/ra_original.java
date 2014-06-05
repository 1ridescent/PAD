import java.util.*;

class Board1
{
	int[] score_table = {0, 3, 8, 15, 24, 35};
	int index(int r, int c)
	{
		return r * 6 + c;
	}
	char[] board;
	int cur_r, cur_c;
	int num_colors;
	boolean[] cleared;
	int num_moves;
	int score;
	Board1()
	{
		board = new char[30];
		for(int i = 0; i < 30; i++) board[i] = '.';
		cur_r = cur_c = 0;
		num_colors = 0;
		cleared = new boolean[256];
		for(int i = 0; i < 256; i++) cleared[i] = false;
		num_moves = 0;
	}
	Board1(Board1 copy)
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
		return String.copyValueOf(board).hashCode() + cur_r * 100 + cur_c;
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
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				match[index(i, j)] = false;
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
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				if(match[index(i, j)])
				{
					if(!cleared[Character.getNumericValue(board[index(i, j)])])
					{
						cleared[Character.getNumericValue(board[index(i, j)])] = true;
						num_colors++;
					}
					board[index(i, j)] = '.';
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
		Board1 test_board = new Board1(this);
		test_board.process();
		score = score_table[test_board.num_colors] - num_moves;
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
		Scanner sc = new Scanner(System.in);
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

public class ra_original
{
	static Board1[] list;
	static int[] prev;
	static int next;
	static PriorityQueue<Integer> Q;
	static HashSet<Integer> found;
	static void add_to_queue(Board1 B, int prev_index)
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
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		Board1 initial_board = new Board1();
		initial_board.input();
		String excluded = sc.next();
		initial_board.exclude(excluded);
		int num_iterations = sc.nextInt();
		int MAX = 4 * num_iterations + 100;
		list = new Board1[MAX];
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
				Board1 B = new Board1(initial_board);
				B.cur_r = i;
				B.cur_c = j;
				add_to_queue(B, -1);
			}
		int best_score = 0;
		int best_board_index = 0;
		for(int iter = 0; iter < num_iterations; iter++)
		{
			int board_index = Q.remove();
			int score = list[board_index].score;
			if(score > best_score)
			{
				best_score = score;
				best_board_index = board_index;
			}
			if(list[board_index].cur_r > 0)
			{
				Board1 B = new Board1(list[board_index]);
				B.move(-1, 0);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_r < 4)
			{
				Board1 B = new Board1(list[board_index]);
				B.move(1, 0);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_c > 0)
			{
				Board1 B = new Board1(list[board_index]);
				B.move(0, -1);
				add_to_queue(B, board_index);
			}
			if(list[board_index].cur_c < 5)
			{
				Board1 B = new Board1(list[board_index]);
				B.move(0, 1);
				add_to_queue(B, board_index);
			}
		}
		System.out.println("best score is " + best_score);
		System.out.println("best board index is " + best_board_index);
		System.out.println("final board is");
		list[best_board_index].output();
		String path = new String();
		int board_index = best_board_index;
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
}
