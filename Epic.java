// Juan Parra
// Description: Using Dynamic programming, determine the number of unique
// outcomes that can be established in a game where one person wins and loses

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

    // recursive call with memo (dp)
    //System.out.println(round.rec(0, round.x_max, round.r_max));

    // iterative call with dp
    System.out.print(round.iterative());
  }

  // modified approach
  public static int[][][] memo;                   // memo
  public static int x_moves, r_moves;             // # of moves
  public static Moves[] xorvier, ruffus;          // moveset
  public static int target, r_max, x_max;         // state
  public static int IMPOSSIBLE = -1, MOD = 10007;

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
    memo = new int[target+1][x_max+1][r_max+1];
    for(int[][] a : memo)
      for(int[] b : a)
        Arrays.fill(b, IMPOSSIBLE);
  }

  public static int rec(int time, int x_stamina, int r_stamina)
  {
    // base case if target is reached and winner is decided
    if(time > target && x_stamina > 0 && r_stamina <= 0)
      return 1;

    // base case if fight exceeded time
    if(time > target)
      return 0;

    // base case if fight ended early
    if(x_stamina <= 0 || r_stamina <= 0)
      return 0;

    // base case if memo contained a value
    if(memo[time][x_stamina][r_stamina] != IMPOSSIBLE)
      return memo[time][x_stamina][r_stamina];

    int ret_val = 0;

    // lets do cost only (only when fight ends on even seconds)
    if(time == target)
    {
      for(int i = 0; i < x_moves; i++)
      {
        // pick xorvier current move
        Moves x = xorvier[i];

        for(int j = 0; j < r_moves; j++)
        {
          // pick ruffus current move
          Moves r = ruffus[j];

          // begin calculations (neither fighter can go beyond max)
          int xCost = (x_stamina - x.cost >= x_max) ? x_max : x_stamina - x.cost;
          int rCost = (r_stamina - r.cost >= r_max) ? r_max : r_stamina - r.cost;

          // dp with recursion
          ret_val += rec(time + 1, xCost, rCost);
          ret_val %= MOD;
        }
      }

      return ret_val;
    }

    // lets execute entire move
    for(int i = 0; i < x_moves; i++)
    {
      // pick xorvier current move
      Moves x = xorvier[i];

      for(int j = 0; j < r_moves; j++)
      {
        // pick ruffus current move
        Moves r = ruffus[j];

        // begin calculations (neither fighter can go beyond max)
        int xCost = (x_stamina - x.cost >= x_max) ? x_max : x_stamina - x.cost;
        int rCost = (r_stamina - r.cost >= r_max) ? r_max : r_stamina - r.cost;

        // if the cost forces one of the fighters to die, that moveset combination
        // cannot be used at all during that specific iteration
        if(xCost <= 0 || rCost <= 0)
          continue;

        int xDamage = (xCost - r.damage >= x_max) ? x_max : xCost - r.damage;
        int rDamage = (rCost - x.damage >= r_max) ? r_max : rCost - x.damage;

        // recursive call
        ret_val += rec(time + 2, xDamage, rDamage);
        ret_val %= MOD;
      }
    }

    //memoization step
    memo[time][x_stamina][r_stamina] = ret_val;

    return ret_val;
  }

  public static int iterative()
  {
    int[][] current = new int[x_max+1][r_max+1];
    int[][] next = new int[x_max+1][r_max+1];
    current[x_max][r_max] = 1;

    // lets execute entire move
    for(int i = 0; i <= target; i+=2)
    {
      // fill next array with 0 values after every moveset is complete
      for(int[] b : next)
        Arrays.fill(b, 0);

      for(int xs = x_max; xs > 0; xs--)
      {
        for(int rs = r_max; rs >= 0; rs--)
        {
          // skip if there was no value at that current state
          if(current[xs][rs] == 0)
            continue;

          // execute movesets (choose a combination)
          for(int j = 0; j < x_moves; j++)
          {
            // pick xorvier current move
            Moves x = xorvier[j];

            for(int k = 0; k < r_moves; k++)
            {
              // pick ruffus current move
              Moves r = ruffus[k];

              // begin calculations (neither fighter can go beyond max)
              int xCost = (xs - x.cost >= x_max) ? x_max : xs - x.cost;
              int rCost = (rs - r.cost >= r_max) ? r_max : rs - r.cost;

              // if costs are below 0 before final time, skip that moveset
              if(i != target && (xCost <= 0 || rCost <= 0))
                continue;

              int xDamage = (xCost - r.damage >= x_max) ? x_max : xCost - r.damage;
              int rDamage = (rCost - x.damage >= r_max) ? r_max : rCost - x.damage;

              // if fight ended on an even second
              if(i == target && xCost > 0 && rCost <= 0)
              {
                next[0][0] += current[xs][rs];
                next[0][0] %= MOD;
              }

              // if fight ended on an odd second
              else if(i == (target -1) && xDamage > 0 && rDamage <= 0)
              {
                next[0][0] += current[xs][rs];
                next[0][0] %= MOD;
              }

              // fight isnt done yet and make sure both fights have valid staminas
              else if(xDamage > 0 && rDamage > 0 && i < target)
              {
                next[xDamage][rDamage] += current[xs][rs];
                next[xDamage][rDamage] %= MOD;
              }
            }
          }
        }
      }

      int[][] tmp = current;
      current = next;
      next = tmp;
    }

    return current[0][0];
  }
}
