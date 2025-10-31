package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;


/*
* 计算启发式值
* 判断目标状态
* 辅助功能
* */
public class PuzzleBoard extends State {
    private byte [][] puzzle_board;//状态
    private int size;//大小
    private int zeroCol=0;//空格的位置
    private int zeroRow=0;
    private int hashNum=0;
    private int manhattanDistance=0;//曼哈顿距离
    private int misplacedDistance=0;//错位数

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
    //计算启发值(曼哈顿距离，错位数)，实现Zobrist哈希
    public PuzzleBoard(int size,byte [] board,boolean isRoot) {
        //把一维的数组转换为二维的棋盘
        this.size = size;
        this.puzzle_board = new byte[size][];
        for (int i = 0; i < puzzle_board.length; i++) {
            this.puzzle_board[i] = new byte[size];
            for (int j = 0; j < puzzle_board[i].length; j++) {
                this.puzzle_board[i][j] = board[i * size + j];
                if (this.puzzle_board[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                }
            }
        }
    }
    @Override
    public void draw() {

    }

    @Override
    public State next(Action action) {
        return null;
    }

    @Override
    public Iterable<? extends Action> actions() {
        return null;
    }
}
