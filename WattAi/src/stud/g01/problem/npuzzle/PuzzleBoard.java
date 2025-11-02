package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import static core.solver.algorithm.heuristic.HeuristicType.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;


/*
* 计算启发式值
* 判断目标状态
* 辅助功能
* */
public class PuzzleBoard extends State {
    public byte [][] puzzle_board;//状态
    public int size;//大小
    private int zeroCol=0;//空格的位置
    private int zeroRow=0;
    private int hashNum=0;
    private int manhattanDistance=0;//曼哈顿距离
    private int misplacedDistance=0;//错位数

    private static final EnumMap<HeuristicType, Predictor> predictors = new EnumMap<>(HeuristicType.class);

    public static Predictor predictor(HeuristicType type) {
        return predictors.get(type);
    }

    public int getZeroCol() {
        return zeroCol;
    }

    public void setZeroCol(int zeroCol) {
        this.zeroCol = zeroCol;
    }

    public int getZeroRow() {
        return zeroRow;
    }

    public void setZeroRow(int zeroRow) {
        this.zeroRow = zeroRow;
    }

    public int getHashNum() {
        return hashNum;
    }

    public void setHashNum(int hashNum) {
        this.hashNum = hashNum;
    }

    public int getManhattanDistance() {
        return manhattanDistance;
    }

    public void setManhattanDistance(int manhattanDistance) {
        this.manhattanDistance = manhattanDistance;
    }

    public int getMisplacedDistance() {
        return misplacedDistance;
    }

    public void setMisplacedDistance(int misplacedDistance) {
        this.misplacedDistance = misplacedDistance;
    }

    public PuzzleBoard(byte[][] puzzleBoard, int size) {
        puzzle_board = puzzleBoard;
        this.size = size;
    }

    public byte[][] getPuzzle_board() {
        return puzzle_board;
    }

    public int getSize() {
        return size;
    }

    //深拷贝，创建一个与传入的对象一样的对象
    public PuzzleBoard(PuzzleBoard state)
    {
        this.size = state.getSize();
        this.puzzle_board = new byte[size][];
        for (int i = 0; i < state.getSize(); i++) {
            this.puzzle_board[i] = new byte[size];
            for (int j = 0; j < state.getSize(); j++) {
                this.puzzle_board[i][j] = state.getPuzzle_board()[i][j];
            }
        }
    }

    public static ArrayList<Block>blocks_663=new ArrayList<>(){
        {
            add(new Block(1,5,6,9,10,13));
            add(new Block(7,8,11,12,14,15));
            add(new Block(2,3,4));
        }
    };
    public PuzzleBoard(int size,byte [] board,boolean isRoot) {
        this.size = size;
        this.puzzle_board = new byte[size][];
        for (int i = 0; i < size; i++) {
            this.puzzle_board[i] = new byte[size];
            for (int j = 0; j < size; j++) {
                this.puzzle_board[i][j] = board[i * size + j];
                if (this.puzzle_board[i][j] == 0) {   // 找到空格
                    zeroRow = i;
                    zeroCol = j;
                }
            }
        }
        if (isRoot) {
            // 计算曼哈顿距离
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (puzzle_board[i][j] == 0) continue;
                    manhattanDistance += Math.abs((puzzle_board[i][j] - 1) / size - i) +
                            Math.abs((puzzle_board[i][j] - 1) % size - j);
                }
            }
            // 计算错位数量
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (puzzle_board[i][j] == 0 && i * size + j + 1 == size * size) continue;
                    if (puzzle_board[i][j] != i * size + j + 1) misplacedDistance++;
                }
            }
        }
        // 计算 Zobrist 哈希
        for (int i = 0; i < size * size; i++) {
            if (puzzle_board[i / size][i % size] != 0) {
                if (size == 4) {
                    hashNum ^= Zobrist.zobristHash4[i][puzzle_board[i / size][i % size]];
                } else {
                    hashNum ^= Zobrist.zobristHash3[i][puzzle_board[i / size][i % size]];
                }
            }
        }
    }

    @Override
    //打印当前的棋局
    public void draw() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (puzzle_board[i][j] != 0) {
                    System.out.print(puzzle_board[i][j] + " ");
                } else {
                    System.out.print("# ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    //当前状态移动action之后的状态
    //根据传入的动作，生成当前状态的下一个状态，并更新它的空格位置，启发式函数值，哈希值等信息
    public State next(Action action) {
        /*Direction direction = ((Move) action).getDirection();
        int[] offsets = Direction.offset(direction);
        int newRow = zeroRow + offsets[0]; // 空格移动后的新行
        int newCol = zeroCol + offsets[1]; // 空格移动后的新列

        PuzzleBoard newState = new PuzzleBoard(this); // 复制当前状态
        byte val = puzzle_board[newRow][newCol]; // 被移动的数字

        // 将val移到原空格位置（行zeroRow，列zeroCol）
        newState.puzzle_board[zeroRow][zeroCol] = val;
        // 新位置设为空格（0）
        newState.puzzle_board[newRow][newCol] = 0;
        // 更新新状态的空格坐标
        newState.setZeroCol(newCol);
        newState.setZeroRow(newRow);

        // 计算新状态的曼哈顿距离（保持不变）
        int oldManhattan = Math.abs((val - 1) / size - newRow) +
                Math.abs((val - 1) % size - newCol);
        int newManhattan = Math.abs((val - 1) / size - zeroRow) +
                Math.abs((val - 1) % size - zeroCol);
        newState.setManhattanDistance(manhattanDistance - oldManhattan + newManhattan);

        // 计算新状态的错位数（保持不变）
        int v = 0;
        if (zeroRow * size + zeroCol + 1 == val) v--;
        if (zeroRow * size + zeroCol + 1 == 9) v++;
        if (newRow * size + newCol + 1 == val) v++;
        if (newRow * size + newCol + 1 == 9) v--;
        newState.setMisplacedDistance(misplacedDistance + v);

        // 修正哈希计算：使用行和列作为独立索引，移除旧位置贡献并添加新位置贡献
        if (size == 4) { // 15数码（4×4）
            newState.setHashNum(
                    hashNum
                            ^ Zobrist.zobristHash4[newRow][newCol][val]  // 移除val在原位置(newRow, newCol)的贡献
                            ^ Zobrist.zobristHash4[zeroRow][zeroCol][val]  // 添加val在新位置(zeroRow, zeroCol)的贡献
            );
        } else { // 8数码（3×3）
            newState.setHashNum(
                    hashNum
                            ^ Zobrist.zobristHash3[newRow][newCol][val]
                            ^ Zobrist.zobristHash3[zeroRow][zeroCol][val]
            );
        }

        return newState; // 返回新状态*/
        Direction direction = ((Move) action).getDirection();
        int[] offsets = Direction.offset(direction);
        int newRow = zeroRow + offsets[0];
        int newCol =   zeroCol + offsets[1];

        PuzzleBoard newState = new PuzzleBoard(this);
        byte val = puzzle_board[newRow][newCol];
        newState.puzzle_board[zeroRow][zeroCol] = val;
        newState.puzzle_board[newRow][newCol] = 0;
        newState.setZeroCol(newCol);
        newState.setZeroRow(newRow);

        // 增量更新曼哈顿距离
        int old = Math.abs((val - 1) / size - newRow) + Math.abs((val - 1) % size - newCol);
        int nw = Math.abs((val - 1) / size - zeroRow) + Math.abs((val - 1) % size - zeroCol);
        newState.setManhattanDistance(manhattanDistance - old + nw);

        // 增量更新错位数量
        int v = 0;
        if (zeroRow * size + zeroCol + 1 == val) v--;
        if (zeroRow * size + zeroCol + 1 == 9) v++;
        if (newRow * size + newCol + 1 == val) v++;
        if (newRow * size + newCol + 1 == 9) v--;
        newState.setMisplacedDistance(misplacedDistance + v);

        // 增量更新 Zobrist 哈希
        if (size == 4) {
            newState.setHashNum(hashNum ^
                    Zobrist.zobristHash4[newRow * size + newCol][val] ^
                    Zobrist.zobristHash4[zeroRow * size + zeroCol][val]);
        } else {
            newState.setHashNum(hashNum ^
                    Zobrist.zobristHash3[newRow * size + newCol][val] ^
                    Zobrist.zobristHash3[zeroRow * size + zeroCol][val]);
        }
        return newState;
    }
    @Override
    //返回当前状态先所有可能的动作的（Action）的集合
    public Iterable<? extends Action> actions() {
        Collection<Move> moves = new ArrayList<>();
        for (Direction d : Direction.FOUR_DIRECTIONS)
            moves.add(new Move(d));
        return moves;
    }
    //重写equals方法判断两个棋盘的状态是否完全相同
    public boolean equals(Object obj) {
        PuzzleBoard another = (PuzzleBoard) obj;
        for(int i = 0; i < this.size; i++)
            for(int j = 0;j < this.size; j++)
                if (this.puzzle_board[i][j] != another.puzzle_board[i][j])
                    return false;
        return true;
    }

    /*
    * 计算线性冲突的数量的函数
    * 优化启发式评估
    * 线性冲突：两个数字在同一行或同一列，他门处在自己的行列中，当前顺序与目标顺序完全相反，互相
    * 绕路才能到达目标位置
    *   总启发值 = 曼哈顿距离 + 2 × 线性冲突数
    * */
    public int nLinearConflicts(){
        int conflicts = 0;
        int[] pR = new int[size * size + 1];
        int[] pC = new int[size * size + 1];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                pR[puzzle_board[r][c]] = r;
                pC[puzzle_board[r][c]] = c;
            }
        }
        for (int r = 0; r < size; r++) {
            for (int cl = 0; cl < size; cl++) {
                for (int cr = cl + 1; cr < size; cr++) {
                    if ( (r*size + cl +1)!=0 && (r*size + cr +1)!=0 && r == pR[(r*size + cl +1)]
                            && pR[(r*size + cl +1)] == pR[(r*size + cr +1)] &&
                            pC[(r*size + cl +1)] > pC[(r*size + cr +1)]) {
                        conflicts++;
                    }
                    if ((cl*size +  r +1)!=0 && (cr*size +  r +1)!=0 &&  r == pC[(cl*size +  r +1)]
                            && pC[(cl*size +  r +1)] == pC[(cr*size +  r +1)] &&
                            pR[(cl*size +  r +1)] > pR[(cr*size + r +1)]) {
                        conflicts++;
                    }
                }
            }
        }
        return conflicts;
    }
    public int getLinearConflictDistance() {
        return manhattanDistance + 2 * nLinearConflicts();
    }
    /*
    * 一个静态初始化块，作用是为一个存储 “启发式评估函数” 的映射（predictors）注册多种评估方法
    * */
    static {
        predictors.put(LINEAR_CONFLICT, (state, goal) -> ((PuzzleBoard) state).getLinearConflictDistance());
        predictors.put(MANHATTAN, (state, goal) -> ((PuzzleBoard) state).getManhattanDistance());
        predictors.put(MISPLACED, (state, goal) -> ((PuzzleBoard) state).getMisplacedDistance());
        //predictors.put(DISJOINT_PATTERN, (state, goal) -> ((PuzzleBoard) state).getDataBaseDistance663());
    }
}
