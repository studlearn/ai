package stud.g01.problem.npuzzle;

public class ManhattanDistance {
    public static int manhattanDistance=0;
    public int getManhattanDistance(PuzzleBoard initialStatus,PuzzleBoard finalStatus)
    {
        byte[][] initCon=initialStatus.getPuzzle_board();
        byte[][] finalCon=finalStatus.getPuzzle_board();
        int size=initialStatus.getSize();
        int[][] positionMap=new int[size*size][2];
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                int values=finalCon[i][j];
                positionMap[values][0]=i;
                positionMap[values][1]=j;
            }
        }

        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                int value=initCon[i][j];
                if(value==0)
                continue;
                int targetRow=positionMap[value][0];
                int targetCol=positionMap[value][1];

                manhattanDistance+=Math.abs(targetRow-i)+Math.abs(targetCol-j);
            }
        }
        return manhattanDistance;
    }
}
