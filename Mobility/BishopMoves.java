package Mobility;

public class BishopMoves extends Moves {
    public BishopMoves(){
        super(28);
        verticalMoves = new int[totalNumberOfMoves];
        horizontalMoves = new int[totalNumberOfMoves];
        initializeMoves();
    }

    @Override
    void initializeMoves(){
        //Total number of moves in all directions for a bishop is 28
        for(int i = totalNumberOfMoves / 2; i < totalNumberOfMoves - 7; ++i){
            verticalMoves[i] += (i - 14) + 1;
            horizontalMoves[i] = horizontalMoves[i + 7] - (i - 14) - 1;

            verticalMoves[i + 7] = verticalMoves[i + 7] - (i - 14) - 1;
            horizontalMoves[i + 7] += (i - 14) + 1;
        }

        for(int i = 0; i < totalNumberOfMoves / 4; ++i){
            verticalMoves[i] += i + 1;
            horizontalMoves[i] += i + 1;

            verticalMoves[i + 7] = verticalMoves[i + 7] - i - 1;
            horizontalMoves[i + 7] = horizontalMoves[i + 7] - i - 1;
        }
    }
}
