package stud.g01.problem.npuzzle;

import java.security.SecureRandom;
import java.util.Random;

public class Zobrist {
    /*
     *Zobrist哈希
     * zobrist[i][j][k]表示第(i,j)个位置值为k的情况的随机数
     * 当棋局的情况改变的时候，移除旧的情况的贡献，添加新的情况的贡献，
     * 做法是，对哈希值，异或原来的位置，然后异或现在的位置
     * 不计算0的位置，只需异或两次就可以
     */
   /* public static int[][][] getZobristHash(int size)
    {
        Random rand = new Random();
        int maxValue = size * size; // 数字范围为0到size?-1，共size?个
        int[][][] zobristHashTable = new int[size][size][maxValue];
        for(int i=0;i<size;i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < maxValue; k++) {
                    zobristHashTable[i][j][k] = rand.nextInt();
                }
            }
        }
        return zobristHashTable;
    }
    public static int [][][] zobristHash4=Zobrist.getZobristHash(4);
    public static int [][][] zobristHash3=Zobrist.getZobristHash(3);

    //获得hash值
    public static int getHash(int[][][] zobristHashTable,PuzzleBoard puzzleBoard)
    {
        byte[][] tempPuzzle=puzzleBoard.getPuzzle_board();//把棋局转换为二位数组的格式
        int zobristHash=0;
        int size=tempPuzzle.length;
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++) {
                int value=tempPuzzle[i][j];
                if(value!=0)
                {
                    zobristHash^=zobristHashTable[i][j][value];
                }
            }
        }
        return zobristHash;
    }
public static int updateHash(int currentHash,
                             int fromCol,int fromRow,
                             int toCol,int toRow,
                             int value,
                             int[][][]zobristHash)
{
currentHash^=zobristHash[fromRow][fromCol][value];
currentHash^=zobristHash[toRow][toCol][value];
return currentHash;
}*/

    public static long x=0;
    public static int[][] zobristHash4=Zobrist.getZobrist(4);
    public static int[][] zobristHash3=Zobrist.getZobrist(3);

    //生成强随机数
    public static int[][] getZobrist(int size) {
        SecureRandom random = new SecureRandom();
        int[][] zobrist = new int[size*size][];
        for(int i=0;i<size*size;i++)
        {
            zobrist[i]=new int[size*size];
            for(int j=0;j<size*size;j++)
            {
                zobrist[i][j]=random.nextInt();
                x++;
            }
        }
        return zobrist;
    }


}
