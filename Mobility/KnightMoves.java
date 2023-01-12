package Mobility;

public class KnightMoves extends Moves{
    public KnightMoves(){
        //Total number of moves a Knight can have sitting at any location is 8
        super(8);
        verticalMoves = new int[totalNumberOfMoves];
        horizontalMoves = new int[totalNumberOfMoves];
        initializeMoves();
    }

    @Override
    void initializeMoves(){
        verticalMoves[0] = -1;
        verticalMoves[1] = -2;
        verticalMoves[2] = -2;
        verticalMoves[3] = -1;
        verticalMoves[4] = 1;
        verticalMoves[5] = 2;
        verticalMoves[6] = 2;
        verticalMoves[7] = 1;

        horizontalMoves[0] = 2;
        horizontalMoves[1] = 1;
        horizontalMoves[2] = -1;
        horizontalMoves[3] = -2;
        horizontalMoves[4] = -2;
        horizontalMoves[5] = -1;
        horizontalMoves[6] = 1;
        horizontalMoves[7] = 2;
    }
}
