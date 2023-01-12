package Mobility;

public class PawnMoves extends Moves {
    public PawnMoves(){
        super(2);
        initializeMoves();
    }

    @Override
    void initializeMoves(){
        //First value refers to white pawns and the second refers to black
        verticalMoves = new int[]{-1, 1};
        horizontalMoves = new int[]{0, 0};
    }
}
