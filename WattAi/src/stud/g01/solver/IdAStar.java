package stud.g01.solver;

import core.problem.Problem;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.*;

public class IdAStar extends AbstractSearcher {

    //
    Predictor predictor;
    private final Set<Integer> explored = new HashSet<>();
    private final Set<Integer> expanded = new HashSet<>();

    Problem problem;

    Deque<Node> path = new ArrayDeque<>();

    public IdAStar(Frontier frontier, Predictor predictor) {
        super(frontier);
        this.predictor = predictor;
    }

    @Override
    public Deque<Node> search(Problem p) {
        problem = p;
        if (!problem.solvable()) {
            return null;
        }
        Node root = problem.root(predictor); //起点
        int depth = root.getHeuristic(); //起点的f(n)启发函数值，超过该值即返回
        explored.clear();
        expanded.clear();
        //每次没有找到路径的话则f(n)++，即depth++
        while (!dfs(root, null, depth)) depth++;
        return path;
    }
    public boolean dfs(Node node, Node fa, int depth){
        if (node.getPathCost() > depth)  return false;
        if (problem.goal(node.getState())) //找到了终点
        {
            //生成解路径，从node一直往前找父节点
            path = generatePath(node);
            return true;
        }
        //没有找到终点，则加入扩展队列
        expanded.add(node.getState().hashCode());
        //生成node结点之后的所有合法结点
        List<Node> childNodes = problem.childNodes(node, predictor);

        for (var child : childNodes){
            explored.add(child.getState().hashCode());
            if (!(fa == null || !child.getState().equals(fa.getState())))  continue;
            if (child.evaluation() <= depth && dfs(child, node, depth))  return true;
        }
        return false;
    }
    public IdAStar(Frontier frontier) {
        super(frontier);
    }

    @Override
    public int nodesExpanded() { return expanded.size(); }//扩展的节点

    @Override
    //探索过的节点数
    public int nodesGenerated() { return explored.size(); }//探索过的节点
}
