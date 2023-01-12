package Mobility;

public class QueenMoves extends Moves{

    public QueenMoves(){
        //Queen moves is a mix between Rook and Bishop with a total of 28 + 28 moves
        super(56);
        verticalMoves = new int[totalNumberOfMoves];
        horizontalMoves = new int[totalNumberOfMoves];
        initializeMoves();
    }

    @Override
    void initializeMoves(){
        //Add all moves to Queen from bishop and rook
        BishopMoves tempBishopObj = new BishopMoves();
        RookMoves tempRookObj = new RookMoves();
        int[] tempVerticalBishopMoves = tempBishopObj.getVerticalMoves();
        int[] tempHorizontalBishopMoves = tempBishopObj.getHorizontalMoves();
        int[] tempVerticalRookMoves = tempRookObj.getVerticalMoves();
        int[] tempHorizontalRookMoves = tempRookObj.getHorizontalMoves();

        for(int i = 0; i < tempVerticalRookMoves.length; ++i){
            verticalMoves[i] = tempVerticalBishopMoves[i];
            horizontalMoves[i] = tempHorizontalBishopMoves[i];

            verticalMoves[i + 28] = tempVerticalRookMoves[i];
            horizontalMoves[i + 28] = tempHorizontalRookMoves[i];
        }
    }
}
