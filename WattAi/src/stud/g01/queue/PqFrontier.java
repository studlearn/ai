package stud.g01.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.queue.QueueFrontier;

import java.util.Comparator;
import java.util.PriorityQueue;


/*
* Frontier是用来存储“已经生成但尚未探索”的节点
* 继承java自带的PriorityQueue<Node>
* */

public class PqFrontier extends PriorityQueue<Node> implements Frontier {



    /*
    * evaluation  f=g+h
    *
    * */
    public PqFrontier() {
        /*
        * super代表当前对象的父类
        * Node是定义的类
        * Comparator<Node>是一个接口，用于定义对象之间的比较规则
        * lambda表达式
        * getHeuristic()对应启发函数值h(n)
        * A*算法
        *
        * */
        super((Comparator<Node>)(node1,node2)->
                node1.evaluation()!=node2.evaluation()?
                        node1.evaluation()-node2.evaluation():
                        node1.evaluation()-node2.evaluation()
        );
    }
    @Override
    //判断某个节点是否已经存在
    public boolean contains(Node node) {
        // 遍历队列中的所有节点
        for (Node n : this) {
            // 比较状态是否相同
            if (n.getState().equals(node.getState()))
            {
                // 存在相同状态的节点
                return true;
            }
        }
        // 不存在
        return false;
    }
    //
    @Override
    //节点入队
    public boolean offer(Node node) {
        //调用父类的offer函数，添加成功返回true不成功返回false
        return super.offer(node);
    }
}
