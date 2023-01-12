package Mobility;

//This class gives each ChessPiece a certain set of moves
public abstract class Moves {
    protected int[] verticalMoves;
    protected int[] horizontalMoves;
    protected final int totalNumberOfMoves;

    public Moves(int totalNumberOfMoves){
        this.totalNumberOfMoves = totalNumberOfMoves;
    }
    public int[] getVerticalMoves(){
        return verticalMoves;
    }
    public int[] getHorizontalMoves(){
        return horizontalMoves;
    }
    public int getTotalNumberOfMoves() { return totalNumberOfMoves; }
    abstract void initializeMoves();
}
