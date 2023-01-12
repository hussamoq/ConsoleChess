package Mobility;

public class RookMoves extends Moves {
    public RookMoves(){
        //Total moves of 28 in all directions for the rook, 4 directions
        super(28);
        verticalMoves = new int[totalNumberOfMoves];
        horizontalMoves = new int[totalNumberOfMoves];
        initializeMoves();
    }

    @Override
    void initializeMoves(){
        //First half for the vertical moves leaving the horizontal moves as 0
        for(int i = 0; i < totalNumberOfMoves / 4; ++i){
            //Process vertically up and down
            verticalMoves[i] += i + 1;
            verticalMoves[i + 7] = verticalMoves[i + 7] - i - 1;
        }

        //Second half for the vertical moves leaving the vertical moves as 0
        for(int i = totalNumberOfMoves / 2; i < totalNumberOfMoves - 7; ++i){
            //Process horizontally left and right
            horizontalMoves[i] += (i - 14) + 1;
            horizontalMoves[i + 7] = horizontalMoves[i + 7] - (i - 14) - 1;
        }
    }
}
