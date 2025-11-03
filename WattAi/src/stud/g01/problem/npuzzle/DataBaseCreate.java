package stud.g01.problem.npuzzle;

import javax.swing.*;
import java.io.*;
import java.util.*;

/*
启发值>=manhattan
不考虑0的位置
* 通过预计算部分滑块（模式）到目标位置的最小步数，
*为启发式搜索（如 A * 算法）提供高效的启发式函数估值。
* */
public class DataBaseCreate {
    //初始化移动方向
    public final static int[] dx={0,1,0,-1};
    public final static int[] dy={1,0,-1,0};

    //问题规模
    public final static int Size=4;

    //存储状态与对应cost（距离）的映射，临时用于BFS计算
    public static TreeMap<Integer,Integer>stateCost;
    //已加载的数据库表：每个元素对应一个block的数据库
    public static ArrayList<ArrayList<Integer>> Table = new ArrayList<>() {{
        add(DataBaseCreate.readFromFile("resources/663_0.txt"));
        add(DataBaseCreate.readFromFile("resources/663_1.txt"));
        add(DataBaseCreate.readFromFile("resources/663_2.txt"));
        System.out.println("数据库读取成功");
    }};

    //从文件读取与计算的“状态-距离”数据，存储到ArrayList中
    //用状态编码（一个整数）作为索引，直接通过temp.get（state）获取距离，实现O（1）查询效率
    private static ArrayList<Integer> readFromFile(String filePath) {
        Scanner sc;
        try{
            sc=new Scanner(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int state;
        int cost;

        ArrayList<Integer> temp=new ArrayList<>();
        while(sc.hasNext()){
            state=sc.nextInt();//状态编码
            cost=sc.nextInt();//从状态到目标的最短距离
            // 用状态作为索引，直接存储代价（确保列表大小足够）
            while(temp.size()<state)
                temp.add(0);
            temp.add(cost);
        }
        return temp;
    }
    public static void create(String filePreName,Block... blocks) throws FileNotFoundException {
        int i=0;
        for(Block b:blocks){
            System.out.println("this the "+i+"block");
            BreadthFirstSearch(b);//对当前Block执行BFS计算距离
            writeToFile(filePreName,i++);//将结果写入文件
        }
    }
    //计算给定状态的启发式距离
    public static int getDistance(byte[][] state,ArrayList<Block>blocks){
        int distance=0;
        ArrayList<State> temp=new ArrayList<>();
        //初始化每个Block的目标状态容器
        for(int i=0;i<blocks.size();i++){
            temp.add(State.slidesToState(blocks.get(i).getSlides()));
        }

        //遍历当前状态的每个位置，更新对应的Block的子状态
        for(int i=0;i<Size;i++)
        {
            for(int j=0;j<Size;j++)
            {
                if(state[i][j]==0)continue;//不计算空格
                //确定当前滑块属于哪个Block
                int blockIndex=Block.belongs.get(state[i][j]);
                //找到该滑块在Block中的索引
                int index=blocks.get(blockIndex).getIndex(state[i][j]);
                //计算当前滑块的位置编码（1-16，对应4*4的格子）
                int value=i*Size+j+1;
                //更新该Block的子状态（记录滑块当前位置）
                temp.get(blockIndex).setStateSlide(index,value);
            }
        }
        //累加每个Block的子状态距离（从数据库中查询）
        for(int i=0;i<blocks.size();i++){
            distance+= Table.get(i).get(temp.get(i).getState());
        }
        return distance;
    }
    private static int makeMove(State state,int dx,int dy)
    {
        //找到空格的当前位置，状态中最后一个元素是空格
        int blankIndex=state.stateToSlide(state.getNum()-1);
        int blankX=(blankIndex-1)/Size;//行 0-3
        int blankY=(blankIndex-1)%Size;//列0-3

        //计算移动后的空格的位置
        int newX=blankX+dx;
        int newY=blankY+dy;
        if(newX<0||newX>=Size||newY<0||newY>=Size)return -1;//越界无效

        int newIndex=newX*Size+newY+1;//新位置编码  1-16
        State newState =new State(state);//复制当前的值
        int costInc=0;//代价增量  0 or 1

        for(int i=0;i<state.getNum();i++)
        {
            int x=(state.stateToSlide(i)-1)/Size;
            int y=(state.stateToSlide(i)-1)%Size;
            if(x==newX&&y==newY){//移动的是当前block的滑块
                newState.setStateSlide(i,blankIndex);//更新滑块位置与空格交换
                costInc=1;
                break;

            }
        }
        newState.setStateSlide(state.getNum()-1,newIndex);//更新空格位置

        //当前状态未被访问，记录其代价
        int newCost=stateCost.get(state.getState())+costInc;
        if(stateCost.containsKey(newState.getState()))
        {
            return -1;//已访问，无需处理
        }
        stateCost.put(newState.getState(),newCost);
        state.setState(newState.getState());//更新当前状态
        return costInc;


    }
    //BFS计算距离
    private static void BreadthFirstSearch(Block b) {
        stateCost=new TreeMap<>();//存储“状态-代价”映射
        Deque<Integer> queue=new LinkedList<>();//双端队列。用于0-1 bfs

        //初始化：block的目标状态
        ArrayList<Integer> slides=b.getSlides();
        slides.add(16);//加入空格的目标位置
        State temp=State.slidesToState(slides);//编码为整数

        //初始状态：代价0，加入队列
        stateCost.put(temp.getState(),0);
        queue.addLast(temp.getState());
        int stateNow,exitFlag,epoch=0;
        while(!queue.isEmpty())
        {
            epoch++;
            if(epoch%1000000==0) System.out.println("epoch:"+epoch);
            stateNow=queue.removeFirst();

            for(int i=0;i<4;i++)
            {
                temp.setState(stateNow);
                exitFlag=makeMove(temp,dx[i],dy[i]);

                if(exitFlag==0)
                {
                    queue.addLast(temp.getState());//0代价，队首，优先处理
                } else if (exitFlag==1) {
                    queue.addLast(temp.getState());//1代价：队尾
                }
            }
        }
        System.out.println("finished all epoch is"+epoch);
    }
    private static void writeToFile(String filePreName, int index) throws FileNotFoundException {
        /*TreeMap<Integer,Integer> temp=new TreeMap<>();//去重并保留最小代价
        String path="./"+filePreName+"_"+index+".txt";
        File file=new File(path);

        //处理stateCost:去掉空格信息（右移4位），保留最小代价
        Iterator iter =stateCost.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry=(Map.Entry)iter.next();
            int state=((int)entry.getKey())>>4;//右移4位
            int cost=(int)entry.getValue();
            temp.put(state,temp.containsKey(state)?Math.min(cost,temp.get(state)):cost);
        }
        //写文件
        try(FileOutputStream fos=new FileOutputStream(file))
        {
            Iterator it=temp.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry entry=(Map.Entry)it.next();
                int s=(int)entry.getKey();
                int c=(int)entry.getValue();
                fos.write((s+" "+c+"\n").getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stateCost.clear();//清空临时数据*/
        TreeMap<Integer, Integer> tmp = new TreeMap<>();
        String path = "./" + filePreName + "_" + index + ".txt";
        File file = new File(path);
        FileOutputStream fileOut;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOut = new FileOutputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int state, cost;
        Iterator iter = DataBaseCreate.stateCost.entrySet().iterator();
        Map.Entry entry;

        while (iter.hasNext()) {
            entry = (Map.Entry) iter.next();
            state = ((int) entry.getKey()) >> 4;
            cost = (int) entry.getValue();
            if (tmp.containsKey(state)) {
                tmp.put(state, Math.min(cost, tmp.get(state)));
            } else {
                tmp.put(state, cost);
            }
        }

        Iterator it = tmp.entrySet().iterator();
        while (it.hasNext()) {
            entry = (Map.Entry) it.next();
            state = (int) entry.getKey();
            cost = (int) entry.getValue();
            try {
                fileOut.write(state);
                fileOut.write(' ');
                fileOut.write(cost);
                fileOut.write('\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // stateCost映射清空
        stateCost.clear();
        try {
            fileOut.close();
            System.out.println("file closed successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


}


}
