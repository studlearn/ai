package stud.g01.problem.npuzzle;

import core.problem.Action;

public class Move extends Action{
    //在 n-puzzle（数字谜题）问题中，封装一次具体的 “移动动作”
    //初始化
    public Move(Direction direction){
        this.direction = direction;
    }
    private static int count = 1;
    @Override
    public int stepCost() {
        return 1;  //cost固定是1
    }
    @Override
    //判断两个Move对象是否相同，只要他们的移动方向相同，就认为是相同的动作
    public boolean equals(Object obj) {
        if (obj == this) return true;
        Move another = (Move) obj;
        //两个Node对象的状态相同，则认为是相同的
        return this.direction.equals(another.direction);
    }
    @Override
    //打印  ↑
    public void draw() {
        System.out.println(this);
    }

    private Direction direction;

    public Direction getDirection() {    //取方向
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public static int getCount() {    //取数
        return count;
    }

    public static void setCount(int count) {  Move.count = count; }

}
