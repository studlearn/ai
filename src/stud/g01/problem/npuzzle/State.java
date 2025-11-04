package stud.g01.problem.npuzzle;

import java.util.ArrayList;

/*
* 用一个整数（int类型的state）紧凑地存储多个滑块(slide)的信息
* 通过位运算实现滑块状态的编码，解码，更新和打印
* */
public class State {
    // 用一个整数存储多个滑块的状态（通过位运算编码）
    private int state;
    // 状态存储的滑块个数
    private int num;

    //默认构造，初始状态为0
    public State() {
        state = 0;
    }

    //带参构造：直接指定state和滑块数量num
    public State(int state, int num) {
        this.state = state;
        this.num = num;
    }

    //拷贝构造
    public State(State a){
        this.state = a.state;
        this.num = a.num;
    }

    //获取滑块的数量和编码后的整数状态
    public void setNum(int num) {
        this.num =num;
    }
    public void setState(int state){
        this.state = state;
    }
    public int getNum(){
        return this.num;
    }
    public int getState(){
        return this.state;
    }
    //整数编码
    //输入：ArrayList<Integer> slides，存储了一组滑块的编号（例如[2,5,9]表示这三个滑块）。
    //输出：State对象，构造参数为编码后的整数res和滑块数量（slides.size()）。
    //
    public static State slidesToState(ArrayList<Integer> slides) {
        //滑块最终编码的整数
        int res = 0;
        //每个滑块用4位存储
        for (int i = 0; i < 4 * slides.size(); i++) {
            //当前处理的滑块在列表中的索引
            int j = slides.size() - i / 4 - 1;  //0-3处理的都是第一个滑块  因为此时i/4==0
            //编码的核心步骤，作用是提取当前滑块的第(i%4)位二进制值（0 或 1）：
            if (((slides.get(j) - 1) >> (i % 4) & 1) == 1) {
                res |= 1 << i;
            }
        }
        return new State(res,slides.size());
    }
    //整数编码计算当单个滑块的值
    public int stateToSlide(int index) {
        int res = (this.state >> ((this.num - index - 1) * 4)) & 15;
        return res + 1;
    }
    //修改编码中指定滑块的值
    public  void setStateSlide(int index,int value){
        value--;
        int base = this.num - index - 1;
        for(int i = 0; i < 4;i++){
            if(((value >> i) & 1) == 1){
                this.state |= 1<<(base * 4 + i);
            }else{
                this.state &= ~(1<<(base * 4 + i));
            }
        }
    }

    public void slidePrint(){
        for(int i = 0; i < this.num; i++){
            System.out.print(this.stateToSlide(i)+" ");
        }
        System.out.println();
    }
}
