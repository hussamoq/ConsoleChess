package ChessPiece;
import Mobility.Moves;
import java.lang.Exception;
import Board.*;
import Utility.Pair;

public final class Pawn extends ChessPiece{
    private final ChessBoard chessBoard;
    boolean firstMove = true;
    public Pawn(Color color, Moves moves, int width, int height, Board board){
        super(color, moves, width, height, board);
        calculateLegalInitialMoves();
        chessBoard = (ChessBoard)board;
    }

    @Override
    public void recalculateLegalMoves(){
        super.recalculateLegalMoves();

        //A pawn can only move forward, so movement differs from white to black chess pieces
        if(color == Color.WHITE){
            //A pawn can only move one step diagonally if it can attack another piece
            if(topLeftIsNotClear() && isEnemy(currentHeight - 1, currentWidth - 1)){
                addToLegalMoves(currentHeight - 1, currentWidth - 1);
            }

            if(topRightIsNotClear() && isEnemy(currentHeight - 1, currentWidth + 1)){
                addToLegalMoves( currentHeight - 1, currentWidth + 1);
            }

            if(topLeftIsNotClear() && isNotEnemy(currentHeight - 1, currentWidth - 1)){
                addToAllPotentialMoves(new Pair<Integer, Integer>(currentWidth - 1, currentHeight - 1));
            }

            if(topRightIsNotClear() && isNotEnemy(currentHeight - 1, currentWidth + 1)){
                addToAllPotentialMoves(new Pair<Integer, Integer>(currentWidth + 1, currentHeight - 1));
            }

            //Sorts it to the universal moves containers
            addToAllMoves((ChessBoard)board);

            //As a pawn, we need to check our front, and both first left and right diagonals
            if(frontIsClear()){
                addToLegalMoves(currentHeight + getMoves().getVerticalMoves()[0],
                        currentWidth + getMoves().getHorizontalMoves()[0]);

                //in case the pawn has still not moved then it's still eligible to move forward twice
                if(firstMove){
                    //While this may be the case, we also need to make sure no other chess piece is occupying
                    if(frontFrontIsClear())
                        addToLegalMoves( currentHeight - 2, currentWidth);
                }
            }

        }else{
            if(topLeftIsNotClear() && isEnemy(currentHeight + 1, currentWidth + 1)){
                addToLegalMoves(currentHeight + 1,currentWidth + 1);
            }

            if(topRightIsNotClear() && isEnemy(currentHeight + 1, currentWidth - 1)){
                addToLegalMoves(currentHeight + 1,currentWidth - 1);
            }

            if(topLeftIsNotClear() && isNotEnemy(currentHeight + 1, currentWidth + 1)){
                addToAllPotentialMoves(new Pair<Integer, Integer>(currentWidth + 1, currentHeight + 1));
            }

            if(topRightIsNotClear() && isNotEnemy(currentHeight + 1, currentWidth - 1)){
                addToAllPotentialMoves(new Pair<Integer, Integer>(currentWidth - 1, currentHeight + 1));
            }

            //Sorts it to the universal moves containers
            addToAllMoves((ChessBoard)board);

            if(frontIsClear()){
                addToLegalMoves(currentHeight + getMoves().getVerticalMoves()[1],
                        currentWidth + getMoves().getHorizontalMoves()[1]);

                if(firstMove){
                    if(frontFrontIsClear())
                        addToLegalMoves(currentHeight + 2, currentWidth);
                }
            }
        }
    }

    @Override
    public String getName(){
        return "Pawn";
    }

    @Override
    public void move(int height, int width) throws IllegalAccessException {
        try{
            super.move(height, width);
        }catch(IllegalAccessException e){
            throw e;
        }catch(Exception e){
            System.out.println("Unknown error occurred");
            System.exit(1);
        }

        //Since the pawn made a move, it no longer posses the first move property
        firstMove = false;

        //Set the new location to THIS PAWN and replace the old location with null
        board.getGrid()[height][width] = this;
        board.getGrid()[currentHeight][currentWidth] = null;

        //Update coordinates
        currentHeight = height;
        currentWidth = width;

        chessBoard.setLastMovedPiece(this);
    }

    public boolean isAtLastSquare(){
        if(color == Color.WHITE)
            return currentHeight == 0;

        return currentHeight == 7;
    }

    void calculateLegalInitialMoves(){
        if(color == Color.WHITE){
            //Initially we can either take 1 or 2 steps forward as a pawn
            addToLegalMoves(currentHeight - 1, currentWidth);
            addToLegalMoves(currentHeight - 2, currentWidth);
        }else{
            addToLegalMoves(currentHeight + 1, currentWidth);
            addToLegalMoves(currentHeight + 2, currentWidth);
        }
    }

    boolean frontIsClear(){
        if(color == Color.WHITE) {
            return board.getGrid()[currentHeight + getMoves().getVerticalMoves()[0]]
                    [currentWidth + getMoves().getHorizontalMoves()[0]] == null;
        }else{
            return board.getGrid()[currentHeight + getMoves().getVerticalMoves()[1]]
                    [currentWidth + getMoves().getHorizontalMoves()[1]] == null;
        }
    }

    boolean frontFrontIsClear(){
        if(color == Color.WHITE){
            return board.getGrid()[currentHeight - 2][currentWidth] == null;
        }else{
            return board.getGrid()[currentHeight + 2][currentWidth] == null;
        }
    }

    boolean topLeftIsClear(){
        if(color == Color.WHITE){
            if(currentWidth - 1 < 0)
                return true;

            return board.getGrid()[currentHeight - 1][currentWidth - 1] == null;
        }else{
            if(currentWidth + 1 >= board.getGrid()[0].length)
                return true;

            return board.getGrid()[currentHeight + 1][currentWidth + 1] == null;
        }
    }

    boolean topRightIsClear(){
        if(color == Color.WHITE){
            if(currentWidth + 1 >= board.getGrid()[0].length)
                return true;

            return board.getGrid()[currentHeight - 1][currentWidth + 1] == null;
        }else{
            if(currentWidth - 1 < 0)
                return true;

            return board.getGrid()[currentHeight + 1][currentWidth - 1] == null;
        }
    }


    boolean topLeftIsNotClear(){
        return !topLeftIsClear();
    }

    boolean topRightIsNotClear(){
        return !topRightIsClear();
    }

}
