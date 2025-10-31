package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class NPuzzleProblem extends Problem {

    //initialState和goal继承自父类Problem
    public NPuzzleProblem(State initialState, State goal) {
        super(initialState, goal);
    }
    public NPuzzleProblem(State initialState, State goal, int size) {
        //将参数传递给父类，让父类完成初始化对象工作
        super(initialState, goal, size);
    }

    //计算逆序数对
   public static int getReverseNumberCount(PuzzleBoard status)
   {
       byte [][] temp= status.getPuzzle_board();
       int size=status.getSize();
       List<Byte> tempList=new ArrayList<>();
       //提取非零数，非空格
       for(int i=0;i<size;i++)
       {
           for(int j=0;j<size;j++)
           {
               byte tempNum=temp[i][j];
               if(tempNum!=0)//排除空格
                {
                   tempList.add(tempNum);
                }
           }
       }
       //计算逆序数
       int ans=0;
       int len=tempList.size();
       for(int i=0;i<len;i++)
       {
           byte currentNum=tempList.get(i);
           for(int j=i+1;j<len;j++)
           {
               if(tempList.get(j)<currentNum)
               {
                   ans++;
               }
           }
       }
       return ans;
   }
    //返回初始状态State
    public State getInitialState() {
        return initialState;
    }
    //返回目标状态State
    public State getGoalState() {
        return goal;
    }

    @Override
    //通过逆序数对判断是否可解
    public boolean solvable() {
        PuzzleBoard initialPuzzle=(PuzzleBoard)getInitialState();
        byte[][] initStatus=initialPuzzle.getPuzzle_board();
        PuzzleBoard goalPuzzle=(PuzzleBoard)getGoalState();
        byte[][] goalStatus=goalPuzzle.getPuzzle_board();
        int initialStatusRN=getReverseNumberCount(initialPuzzle);
        int goalStatusRN=getReverseNumberCount(goalPuzzle);
        int initOdd=initialStatusRN%2;
        int goalOdd=goalStatusRN%2;
        return initOdd==goalOdd;
    }

    @Override
    public int stepCost(State state, Action action) {
        return 1;
    }

    @Override
    //在state上的操作是否可行
    public boolean applicable(State state, Action action) {
        return false;
    }
public static void printNPuzzleBoard(State state)
{
    byte[][] board = ((PuzzleBoard)state).getPuzzle_board();
    int size = ((PuzzleBoard)state).getSize();

    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (board[i][j] == 0)
                System.out.print("# ");
            else
                System.out.print(board[i][j] + " ");
        }
        System.out.println();
    }
    System.out.println();
}
    @Override
    //路径的可视化，使用文件保存求解结果
    //对于PuzzleBoard.结束之后进行
    public void showSolution(Deque<Node> path) throws IOException {

        //try-with-resources 自动关闭 FileWriter 和 PrintWriter，无资源泄漏
    try(FileWriter fw=new FileWriter("resources/solution.txt",true);
    PrintWriter pw=new PrintWriter(fw)
    ){
    //边界判断：若路径为空，直接返回
        if (path.isEmpty())
        {
            pw.println("No Solution");
            return;
        }
        //路径中的第一个状态
        PuzzleBoard first=(PuzzleBoard)path.getFirst().getState();
        pw.print(path.size()+" ");
        for(Node node:path)
        {
            //当前的节点的状态
            PuzzleBoard currentNP=(PuzzleBoard)node.getState();
            int x=first.getZeroRow()-currentNP.getZeroRow();
            int y=first.getZeroCol()-currentNP.getZeroCol();
            if(x==0&&y==1)//空格向下移动 编码1
            {
                pw.print("1 ");
            }
            else if (x==0&&y==-1)//空格向上移动 编码3
            {
                pw.print("3 ");
            }
            else if (x==1&&y==0)//空格向左移动 编码0
            {
                pw.print("0 ");
            }
            else if (x==-1&&y==0)//空格向右移动  编码2
            {
                pw.print("2 ");
            }
            first=currentNP;
        }
    }

    }
}
