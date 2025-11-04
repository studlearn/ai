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
import stud.g01.queue.ListFrontier;
import stud.g01.queue.PqFrontier;

import java.util.ArrayList;
//Fix Me   //Fix Me
public class PuzzleFeeder extends EngineFeeder {
    @Override
    //从给定的字符串中构造出各个问题并返回一个问题的列表
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        ArrayList<Problem> problems = new ArrayList<>();
        for (String line: problemLines) {
            NPuzzleProblem problem = createNPuzzle(line);
            problems.add(problem);
        }
        return problems;
    }

    @Override
    //返回PqFrontier
    public Frontier getFrontier(EvaluationType type) {
        return new PqFrontier(Node.evaluator(type));
    }

    @Override
    public Predictor getPredictor(HeuristicType type) {
        //返回启发函数的计算方式，如：曼哈顿距离、不在位将牌等等
        return PuzzleBoard.predictor(type);
    }
    //构造出一个NPuzzleProblem
    private NPuzzleProblem createNPuzzle(String line) {
        String[] nums = line.split(" ");
        int size = Integer.parseInt(nums[0]);
        //开始和目标状态
        byte[] start = new byte[size * size], goal = new byte[size * size];

        for (int i = 0; i < size * size; i++) {
            //存储的索引从0开始，但nums从1开始
            start[i] = (byte)Integer.parseInt(nums[i + 1]);
            goal[i] = (byte)Integer.parseInt(nums[i + 1 + size * size]);
        }
        //返回创建的NP问题，参数为：State initialState, State goal, int size
        return new NPuzzleProblem(new PuzzleBoard(size, start, true), new PuzzleBoard(size, goal, false), size);
    }
}
