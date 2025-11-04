package stud.g01.queue;

import core.problem.State;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

//初始化优先队列，指定节点的优先级比较器
public class PqFrontier extends PriorityQueue<Node> implements Frontier {
    //比较器，会按照Node内部的方法进行排序
    private final Comparator<Node> evaluator;
    //构造函数
    public PqFrontier(Comparator<Node> evaluator) {
        super(evaluator);
        this.evaluator = evaluator;
    }
    //哈希表
    private final HashMap<Integer, Node> hashMap = new HashMap<>();

    private boolean checkReplace(Node oldNode, Node node) {
        if (evaluator.compare(oldNode, node) > 0) {
            hashMap.put((oldNode.getState()).hashCode(), node);
            super.offer(node);
            return true;
        }
        return false;
    }
    //通过哈希值获取Node结点
    private Node getNode(State state) {
        return hashMap.get(state.hashCode());
    }
    @Override
    //判断是否包含结点node
    public boolean contains(Node node) {
        return getNode(node.getState()) != null;
    }
    //队列出队
    public Node poll() {
        Node node = super.poll();
        hashMap.remove((node.getState()).hashCode());
        return node;
    }
    @Override
    //结点入队
    public boolean offer(Node node) {
        Node oldNode = getNode(node.getState());
        if (oldNode == null) {
            super.offer(node);
            hashMap.put((node.getState()).hashCode(), node);
            return true;
            //当结点存在时，需要比较丢弃哪一个
        } else {
            return checkReplace(oldNode, node);
        }
    }
}
