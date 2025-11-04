package stud.g01.solver;

import core.problem.Problem;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.*;

public class IdAStar extends AbstractSearcher {

    private Predictor predictor;
    private Problem problem;
    private Deque<Node> path = new ArrayDeque<>();

    private int totalNodesGenerated = 0;
    private int totalNodesExpanded = 0;

    // IDA*的关键：记录路径上的状态，避免成环
    private Set<Integer> pathStates = new HashSet<>();

    public IdAStar(Frontier frontier, Predictor predictor) {
        super(frontier);
        this.predictor = predictor;
    }

    public IdAStar(Frontier frontier) {
        super(frontier);
    }

    @Override
    public Deque<Node> search(Problem p) {
        problem = p;
        if (!problem.solvable()) {
            return null;
        }

        totalNodesGenerated = 0;
        totalNodesExpanded = 0;
        pathStates.clear();

        Node root = problem.root(predictor);
        int bound = root.getHeuristic(); // f(n) = g(n) + h(n)

        // 最大迭代次数保护
        final int MAX_ITERATIONS = 100;
        int iterations = 0;

        while (iterations < MAX_ITERATIONS) {
            iterations++;
            pathStates.clear();

            // 搜索并返回新的界限
            int newBound = search(root, bound);

            if (newBound == -1) {
                return path; // 找到解
            }
            if (newBound == Integer.MAX_VALUE) {
                return null; // 无解
            }

            bound = newBound; // 更新界限

            // 内存检查
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            if (usedMemory > runtime.maxMemory() * 0.85) {
                System.err.println("警告：内存使用超过85%，停止搜索");
                return null;
            }
        }

        System.err.println("超过最大迭代次数");
        return null;
    }

    /**
     * IDA*标准实现：返回新的cost界限
     * @param node 当前节点
     * @param bound 当前cost界限
     * @return -1表示找到解，Integer.MAX_VALUE表示无解，否则返回新的最小cost
     */
    private int search(Node node, int bound) {
        totalNodesGenerated++;

        int f = node.evaluation(); // f(n) = g(n) + h(n)

        if (f > bound) {
            return f; // 超过界限，返回当前f值
        }

        if (problem.goal(node.getState())) {
            path = generatePath(node);
            return -1; // 找到解
        }

        totalNodesExpanded++;

        int min = Integer.MAX_VALUE;
        int stateHash = node.getState().hashCode();

        // 标记当前状态在路径上
        pathStates.add(stateHash);

        List<Node> childNodes = problem.childNodes(node, predictor);

        for (Node child : childNodes) {
            int childHash = child.getState().hashCode();

            // 避免成环：检查是否在当前路径上
            if (pathStates.contains(childHash)) {
                continue;
            }

            int t = search(child, bound);

            if (t == -1) {
                return -1; // 找到解，立即返回
            }

            if (t < min) {
                min = t; // 记录最小的超出界限值
            }
        }

        // 回溯：移除当前状态
        pathStates.remove(stateHash);

        return min;
    }

    @Override
    public int nodesExpanded() {
        return totalNodesExpanded;
    }

    @Override
    public int nodesGenerated() {
        return totalNodesGenerated;
    }
}