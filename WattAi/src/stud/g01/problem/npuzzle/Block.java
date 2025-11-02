package stud.g01.problem.npuzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


/*
* 这个Block类主要用于在拼图问题（如 15 数码 puzzle，4x4 网格）中定义 “块” 的概念，
* 用于管理由多个格子组成的集合及其归属关系。
* */
public class Block {
    public static int num = 0;//计数器，用于生成块号
    public int order;//当前块的唯一编号
    public static ArrayList<Integer> belongs;//通过order表示记录每个格子属于哪个块
    //静态代码块，初始化belongs
    static {
        belongs = new ArrayList<Integer>();
        //4*4网格共16个格子，编号0-15
        for (int i = 0; i < 4 * 4; i++) {
            belongs.add(0);//初始时都属于编号为0的块
        }
    }
    private final int size;//块包含的格子数
    private final ArrayList<Integer> slides;//组成当前块的所有格子的索引
    private final HashMap<Integer, Integer> Index;//快速查询，key索引，value为格子在slides中的位置

    public int getSize() {
        return size;
    }

    public int getSlide(int index) {
        return slides.get(index);
    }

    public int getIndex(int slide) {
        return Index.get(slide);
    }

    public ArrayList<Integer> getSlides() {
        return new ArrayList<>(this.slides);
    }

    public boolean isSlideIn(int slide) {
        return Index.containsKey(slide);
    }

    public void Print() {//打印包含所有格子的索引
        for (int i = 0; i < size; i++) {
            System.out.print(slides.get(i) + " ");
        }
    }

    public Block(int... slides) {//可变参数，接收组成块的格子的索引
        this.order = Block.num;//为当前块分配唯一的编号
        Block.num++;//计数器自增，确保下一个块的编号唯一
        size = slides.length;//块的大小为传入的格子数
        this.slides = new ArrayList<>();
        this.Index = new HashMap<>();

        for (int i = 0; i < size; i++) {
            this.slides.add(slides[i]);//将传入的格子索引添加到slides列表
            this.Index.put(slides[i], i);//记录格子在slides中的位置（用于快速查询）
            Block.belongs.set(slides[i], this.order);//更新belongs：格子属于当前块order
        }
    }
}