package stud.g01.problem.npuzzle;

import java.util.Random;

public class Zobrist {
    // 使用固定种子，确保每次运行哈希表一致
    private static final long SEED = 20241104L;

    // 静态初始化，只生成一次
    public static final int[][] zobristHash3 = generateZobrist(3);
    public static final int[][] zobristHash4 = generateZobrist(4);

    /**
     * 生成Zobrist哈希表
     * @param size 棋盘大小
     * @return zobrist[position][value]
     *         position: 0到size?-1的位置索引
     *         value: 0到size?-1的数字值
     */
    private static int[][] generateZobrist(int size) {
        Random random = new Random(SEED + size); // 不同size用不同种子
        int totalPositions = size * size;
        int[][] zobrist = new int[totalPositions][totalPositions];

        for (int pos = 0; pos < totalPositions; pos++) {
            for (int val = 1; val < totalPositions; val++) { // 跳过0（空格）
                zobrist[pos][val] = random.nextInt();
            }
        }
        return zobrist;
    }

    // 兼容旧代码的方法
    public static int[][] getZobrist(int size) {
        return size == 3 ? zobristHash3 : zobristHash4;
    }
}