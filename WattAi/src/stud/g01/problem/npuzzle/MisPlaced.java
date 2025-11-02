package stud.g01.problem.npuzzle;

public class MisPlaced {
    public static int misPlacedNum=0;

    public static int getMisPlacedNum(PuzzleBoard initialStatus,PuzzleBoard finalStatus)
    {
        byte[][] initCon=initialStatus.getPuzzle_board();
        byte[][] finalCon=finalStatus.getPuzzle_board();
        int size=initialStatus.getSize();
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                //不计算0，然后计算目标状态和当前状态不同的个数
                if(initCon[i][j]!=0&&finalCon[i][j]==initCon[i][j])
                    misPlacedNum++;
            }
        }
        return misPlacedNum;
    }
}
