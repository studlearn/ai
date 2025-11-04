package stud.g01.problem.npuzzle;

import java.io.*;
import java.util.*;

public class DataBaseCreate {
    public final static int[] dx = {0, 1, 0, -1};
    public final static int[] dy = {1, 0, -1, 0};
    public final static int Size = 4;

    public static TreeMap<Integer, Integer> stateCost;

    // 延迟加载数据库
    public static ArrayList<ArrayList<Integer>> Table = null;
    private static boolean initialized = false;

    /**
     * 初始化数据库（懒加载）
     */
    public static synchronized void initialize(ArrayList<Block> blocks) {
        if (initialized) return;

        System.out.println("正在初始化模式数据库...");
        Table = new ArrayList<>();

        try {
            // 检查数据库文件是否存在
            boolean filesExist = true;
            for (int i = 0; i < blocks.size(); i++) {
                File file = new File("resources/663_" + i + ".txt");
                if (!file.exists()) {
                    filesExist = false;
                    System.out.println("数据库文件不存在: " + file.getPath());
                    break;
                }
            }

            // 如果文件不存在，生成数据库
            if (!filesExist) {
                System.out.println("开始生成模式数据库，这可能需要几分钟...");
                Block.reset();
                for (int i = 0; i < blocks.size(); i++) {
                    System.out.println("生成第 " + i + " 个模式数据库...");
                    BreadthFirstSearch(blocks.get(i));
                    writeToFile("resources/663", i);
                }
                System.out.println("数据库生成完成！");
            }

            // 读取数据库
            for (int i = 0; i < blocks.size(); i++) {
                Table.add(readFromFile("resources/663_" + i + ".txt"));
            }

            System.out.println("数据库读取成功，共 " + Table.size() + " 个模式");
            initialized = true;

        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
            Table = new ArrayList<>();
            // 添加空表作为fallback
            for (int i = 0; i < blocks.size(); i++) {
                Table.add(new ArrayList<>());
            }
        }
    }

    /**
     * 从文件读取状态-距离映射
     */
    private static ArrayList<Integer> readFromFile(String filePath) {
        System.out.println("读取数据库文件: " + filePath);
        ArrayList<Integer> temp = new ArrayList<>();

        try (Scanner sc = new Scanner(new FileInputStream(filePath))) {
            int maxState = 0;

            // 第一遍：找到最大状态编码
            while (sc.hasNextInt()) {
                int state = sc.nextInt();
                if (sc.hasNextInt()) {
                    int cost = sc.nextInt();
                    maxState = Math.max(maxState, state);
                }
            }

            // 初始化数组，大小为maxState+1，默认值为999（表示不可达）
            for (int i = 0; i <= maxState; i++) {
                temp.add(999);
            }

            // 第二遍：填充实际数据
            sc.close();
            Scanner sc2 = new Scanner(new FileInputStream(filePath));
            while (sc2.hasNextInt()) {
                int state = sc2.nextInt();
                if (sc2.hasNextInt()) {
                    int cost = sc2.nextInt();
                    temp.set(state, cost);  // ? 使用set而不是add
                }
            }
            sc2.close();

            System.out.println("  读取完成，共 " + temp.size() + " 个状态");
            return temp;

        } catch (FileNotFoundException e) {
            System.err.println("文件不存在: " + filePath);
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("读取文件出错: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 计算给定状态的启发式距离
     */
    public static int getDistance(byte[][] state, ArrayList<Block> blocks) {
        // 确保数据库已初始化
        if (Table == null || Table.isEmpty()) {
            System.err.println("警告：数据库未初始化，返回曼哈顿距离");
            return getManhattanDistance(state);
        }

        int distance = 0;
        ArrayList<State> temp = new ArrayList<>();

        // 初始化每个Block的目标状态
        for (int i = 0; i < blocks.size(); i++) {
            temp.add(State.slidesToState(blocks.get(i).getSlides()));
        }

        // 遍历当前状态的每个位置
        for (int i = 0; i < Size; i++) {
            for (int j = 0; j < Size; j++) {
                if (state[i][j] == 0) continue;

                int blockIndex = Block.belongs.get(state[i][j]);

                // ? 边界检查
                if (blockIndex < 0 || blockIndex >= blocks.size()) {
                    System.err.println("警告：滑块 " + state[i][j] + " 的blockIndex=" + blockIndex + " 越界");
                    continue;
                }

                int index = blocks.get(blockIndex).getIndex(state[i][j]);
                int value = i * Size + j + 1;
                temp.get(blockIndex).setStateSlide(index, value);
            }
        }

        // 累加每个Block的子状态距离
        for (int i = 0; i < blocks.size(); i++) {
            int stateCode = temp.get(i).getState();

            // ? 边界检查
            if (stateCode < 0 || stateCode >= Table.get(i).size()) {
                System.err.println("警告：Block " + i + " 的状态编码 " + stateCode +
                        " 超出范围 [0, " + Table.get(i).size() + ")");
                // 使用曼哈顿距离作为fallback
                distance += getManhattanDistanceForBlock(state, blocks.get(i));
            } else {
                int dist = Table.get(i).get(stateCode);
                if (dist == 999) {
                    // 不可达状态，使用曼哈顿距离
                    distance += getManhattanDistanceForBlock(state, blocks.get(i));
                } else {
                    distance += dist;
                }
            }
        }

        return distance;
    }

    /**
     * 计算整个棋盘的曼哈顿距离（fallback）
     */
    private static int getManhattanDistance(byte[][] state) {
        int distance = 0;
        for (int i = 0; i < Size; i++) {
            for (int j = 0; j < Size; j++) {
                if (state[i][j] == 0) continue;
                int targetRow = (state[i][j] - 1) / Size;
                int targetCol = (state[i][j] - 1) % Size;
                distance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
            }
        }
        return distance;
    }

    /**
     * 计算特定Block的曼哈顿距离（fallback）
     */
    private static int getManhattanDistanceForBlock(byte[][] state, Block block) {
        int distance = 0;
        for (int i = 0; i < Size; i++) {
            for (int j = 0; j < Size; j++) {
                if (state[i][j] == 0) continue;
                if (!block.isSlideIn(state[i][j])) continue;

                int targetRow = (state[i][j] - 1) / Size;
                int targetCol = (state[i][j] - 1) % Size;
                distance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
            }
        }
        return distance;
    }

    /**
     * BFS计算距离
     */
    private static void BreadthFirstSearch(Block b) {
        stateCost = new TreeMap<>();
        Deque<Integer> queue = new LinkedList<>();

        ArrayList<Integer> slides = b.getSlides();
        slides.add(16);
        State temp = State.slidesToState(slides);

        stateCost.put(temp.getState(), 0);
        queue.addLast(temp.getState());

        int stateNow, exitFlag, epoch = 0;
        while (!queue.isEmpty()) {
            epoch++;
            if (epoch % 100000 == 0) {
                System.out.println("  处理进度: " + epoch + " 个状态");
            }

            stateNow = queue.removeFirst();

            for (int i = 0; i < 4; i++) {
                temp.setState(stateNow);
                exitFlag = makeMove(temp, dx[i], dy[i]);

                if (exitFlag == 0) {
                    queue.addFirst(temp.getState());
                } else if (exitFlag == 1) {
                    queue.addLast(temp.getState());
                }
            }
        }
        System.out.println("  完成，共处理 " + epoch + " 个状态，生成 " + stateCost.size() + " 个映射");
    }

    /**
     * 执行移动操作
     */
    private static int makeMove(State state, int dx, int dy) {
        int blankIndex = state.stateToSlide(state.getNum() - 1);
        int blankX = (blankIndex - 1) / Size;
        int blankY = (blankIndex - 1) % Size;

        int newX = blankX + dx;
        int newY = blankY + dy;
        if (newX < 0 || newX >= Size || newY < 0 || newY >= Size) return -1;

        int newIndex = newX * Size + newY + 1;
        State newState = new State(state);
        int costInc = 0;

        for (int i = 0; i < state.getNum(); i++) {
            int x = (state.stateToSlide(i) - 1) / Size;
            int y = (state.stateToSlide(i) - 1) % Size;
            if (x == newX && y == newY) {
                newState.setStateSlide(i, blankIndex);
                costInc = 1;
                break;
            }
        }
        newState.setStateSlide(state.getNum() - 1, newIndex);

        int newCost = stateCost.get(state.getState()) + costInc;
        if (stateCost.containsKey(newState.getState())) {
            return -1;
        }
        stateCost.put(newState.getState(), newCost);
        state.setState(newState.getState());
        return costInc;
    }

    /**
     * 写入文件（修复版）
     */
    private static void writeToFile(String filePreName, int index) throws IOException {
        TreeMap<Integer, Integer> tmp = new TreeMap<>();
        String path = filePreName + "_" + index + ".txt";
        File file = new File(path);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        // 处理状态（右移4位去除空格信息）
        for (Map.Entry<Integer, Integer> entry : stateCost.entrySet()) {
            int state = entry.getKey() >> 4;
            int cost = entry.getValue();
            tmp.put(state, tmp.containsKey(state) ? Math.min(cost, tmp.get(state)) : cost);
        }

        // ? 使用PrintWriter正确写入
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<Integer, Integer> entry : tmp.entrySet()) {
                pw.println(entry.getKey() + " " + entry.getValue());
            }
        }

        stateCost.clear();
        System.out.println("  文件写入成功: " + path);
    }

    /**
     * 生成数据库的主方法
     */
    public static void create(String filePreName, Block... blocks) throws IOException {
        Block.reset();
        for (int i = 0; i < blocks.length; i++) {
            System.out.println("生成第 " + i + " 个Block的数据库");
            BreadthFirstSearch(blocks[i]);
            writeToFile(filePreName, i);
        }
    }
}