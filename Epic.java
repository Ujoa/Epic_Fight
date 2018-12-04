// Juan Parra
// UCFID: 4079855
// Description: Stupid Dynamic Programming

import java.util.*;
import java.io.*;

class Moves
{
  int cost, damage;
  public Moves(int c, int d)
  {
    this.cost = c;
    this.damage = d;
  }

  public String toString()
  {
    return "Current moveset is expense of " + this.cost + " and deals this amount of damage " + this.damage;
  }
}

public class Epic
{
  public static void main(String [] args) throws Exception
  {
    Epic round = new Epic();
    //System.out.println(round.recursive(1, new Health(round.x_max, round.r_max)));
    System.out.println(round.fight(0, round.x_max, round.r_max));
  }

  // modified approach
  public static int IMPOSSIBLE = -1, MOD = 10007;
  public static int x_moves, r_moves;             // # of moves
  public static Moves[] xorvier, ruffus;          // moveset
  public static int target, r_max, x_max;         // state
  public static int[][] memo;                   // memo

  public Epic()
  {
    Scanner in = new Scanner(System.in);

    // saves the target time
    target = in.nextInt();

    // Xorvier moveset
    x_moves = in.nextInt();
    x_max = in.nextInt();
    xorvier = new Moves[x_moves];
    for(int i = 0; i < x_moves; i++)
      xorvier[i] = new Moves(in.nextInt(), in.nextInt());

    // Ruffus moveset
    r_moves = in.nextInt();
    r_max = in.nextInt();
    ruffus = new Moves[r_moves];
    for(int i = 0; i < r_moves; i++)
      ruffus[i] = new Moves(in.nextInt(), in.nextInt());

    // fill in memo table
    memo = new int[x_max+1][r_max+1];
    for(int[] a : memo)
      Arrays.fill(a, IMPOSSIBLE);
  }

  public static int fight(int time, int x_stamina, int r_stamina)
  {
    // base case if target is reached and winner is decided
    if(time > target && x_stamina > 0 && r_stamina <= 0)
      return 1;

    if(time > target)
      return 0;

    if(x_stamina <= 0 || r_stamina <= 0)
      return 0;

    if(memo[x_stamina][r_stamina] != IMPOSSIBLE)
      return memo[x_stamina][r_stamina];

    int answer = 0;
    // lets do cost only
    if(time == target)
    {
      for(int i = 0; i < x_moves; i++)
      {
        for(int j = 0; j < r_moves; j++)
        {
          // pick the moves
          Moves x = xorvier[i];
          Moves r = ruffus[j];

          // begin brute brute force
          int xCost = x_stamina - x.cost;
          int rCost = r_stamina - r.cost;

          //System.out.println("COST: recursive call going to time " + (time+2) + " using: " + xCost + " " + rCost);
          answer += fight(time + 1, xCost, rCost);
          answer %= MOD;

          //memoization step
          memo[x_stamina][r_stamina] = answer;
        }
      }
    }

    // lets execute entire move
    for(int i = 0; i < x_moves; i++)
    {
      for(int j = 0; j < r_moves; j++)
      {
        // pick the moves
        Moves x = xorvier[i];
        Moves r = ruffus[j];

        // begin brute brute force
        int xCost = (x_stamina - x.cost > x_max) ? x_max : x_stamina - x.cost;
        int rCost = (r_stamina - r.cost > r_max) ? r_max : r_stamina - r.cost;

        if(xCost <= 0 || rCost <= 0)
          continue;

        int xDamage = (xCost - r.damage > x_max) ? x_max : xCost - r.damage;
        int rDamage = (rCost - x.damage > r_max) ? r_max : rCost - x.damage;

        //System.out.println("MOVE: recursive call going to time " + (time+2) + " using: " + xDamage + " " + rDamage);
        answer += fight(time + 2, xDamage, rDamage);
        answer %= MOD;

        //memoization step
        //memo[xCost][rCost] = answer;
      }
      //memo[x_stamina][r_stamina] = answer;
    }

    return answer;
  }

}
