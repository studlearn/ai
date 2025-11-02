package stud.g01.runner;

import core.problem.Problem;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.g01.problem.npuzzle.NPuzzleProblem;
import stud.g01.problem.npuzzle.PuzzleBoard;
import stud.problem.pathfinding.GridType;
import stud.problem.pathfinding.PathFinding;
import stud.problem.pathfinding.Position;
import stud.queue.ListFrontier;

import java.util.ArrayList;


/*
* 数据提供文件
* 继承EnginerFeeder.java
* 创建问题在本文件
*
* */
public class PuzzleFeeder extends EngineFeeder {
    @Override
    //参照WalkerFeeder.java  ->它的代码是寻路问题
    //这个代码是Npuzzle问题
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        ArrayList<Problem> problems = new ArrayList<>();
        for (String line : problemLines){
            //把每一行抽出来作为一个问题看待
            NPuzzleProblem problem = getPuzzle(line);
            problems.add(problem);
        }
        return problems;
    }

    private NPuzzleProblem getPuzzle(String problemLine) {

        //读取
        String[] nums = problemLine.split(" ");
        //第一个是阶数
        int size = Integer.parseInt(nums[0]);

        //初始状态和目标状态的二维数组
        byte[][] puzzle_board_init = new byte[size][size];
        byte[][] puzzle_board_goal = new byte[size][size];
        //读入初始状态
        int k = 1;
        for(int i = 0 ;i < size; i++)
        {
            for (int j = 0; j < size; j++) {
                puzzle_board_init[i][j] = (byte) Integer.parseInt(nums[k++]);
            }
        }
        //创建棋盘状态
        PuzzleBoard initialState = new PuzzleBoard(puzzle_board_init,size);

        //读入目标状态
        k = 1 + size * size;
        for(int i = 0 ;i < size; i++)
        {
            for (int j = 0; j < size; j++) {
                puzzle_board_goal[i][j] = (byte) Integer.parseInt(nums[k++]);
            }
        }
        PuzzleBoard goal = new PuzzleBoard(puzzle_board_goal,size);

        //构建问题
        return new NPuzzleProblem(initialState, goal, size);
    }


    //参照WalkerFeeder的代码
    @Override
    public Frontier getFrontier(EvaluationType type) {
        return new ListFrontier(Node.evaluator(type));
    }

    @Override
    public Predictor getPredictor(HeuristicType type) {
        return Position.predictor(type);
    }
}
