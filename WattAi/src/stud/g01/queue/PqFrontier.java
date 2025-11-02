package stud.g01.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.queue.QueueFrontier;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import core.problem.State;

/*
* Frontier是用来存储“已经生成但尚未探索”的节点
* 继承java自带的PriorityQueue<Node>
* */

public class PqFrontier extends PriorityQueue<Node> implements Frontier {



    /*
    * evaluation  f=g+h
    *
    * */
    private final Comparator<Node> evaluator;
    public PqFrontier(Comparator<Node> evaluator){
        super(evaluator);
        this.evaluator = evaluator;
    }
    private final HashMap<Integer,Node> hashMap = new HashMap<>();
    private boolean checkReplace(Node oldNode,Node newNode){
        if(evaluator.compare(oldNode,newNode)>0){
            hashMap.put((oldNode.getState()).hashCode(),newNode);
            super.offer(newNode);
            return true;
        }
        return false;
    }
        /*
        * super代表当前对象的父类
        * Node是定义的类
        * Comparator<Node>是一个接口，用于定义对象之间的比较规则
        * lambda表达式
        * getHeuristic()对应启发函数值h(n)
        * A*算法
        *
        * */
    private Node getNode(State state)
    {
        return hashMap.get(state.hashCode());
    }
    @Override
    //判断某个节点是否已经存在
    public boolean contains(Node node) {
       return getNode(node.getState())!=null;
    }
    //队列出队
    public Node poll(){
        Node node = super.poll();
        hashMap.remove((node.getState()).hashCode());
        return node;
    }
    @Override
    //节点入队
    public boolean offer(Node node) {
        //调用父类的offer函数，添加成功返回true不成功返回false
       Node oldNode=getNode(node.getState());
        //当节点不存在是
        if(oldNode==null){
            super.offer(node);
            hashMap.put((node.getState()).hashCode(),node);
            return true;
        }else {
            return checkReplace(oldNode,node);
        }
    }
}
