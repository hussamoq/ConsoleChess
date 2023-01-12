package ChessPiece;
import Mobility.Moves;
import Board.*;

public final class Knight extends ChessPiece {
    private final ChessBoard chessBoard;

    public Knight(Color color, Moves moves, int width, int height, Board board){
        super(color, moves, width, height, board);
        calculateLegalInitialMoves();
        chessBoard = (ChessBoard)board;
    }

    //Deliberately started with n to distinguish from a King
    @Override
    public String getName(){
        return "Night";
    }

    @Override
    public void recalculateLegalMoves(){
        super.recalculateLegalMoves();

        int[] tempVerticalMoves = getMoves().getVerticalMoves();
        int[] tempHorizontalMoves = getMoves().getHorizontalMoves();
        for(int i = 0; i < tempVerticalMoves.length; ++i){
            int verticalDisplacement = currentHeight + tempVerticalMoves[i];
            int horizontalDisplacement = currentWidth + tempHorizontalMoves[i];

            if(inBound(verticalDisplacement, horizontalDisplacement) &&
                    isNotEmptySpace(verticalDisplacement, horizontalDisplacement) &&
            isNotEnemy(verticalDisplacement, horizontalDisplacement)){
                addPotentialAttack(i);
            }

            if(inBound(verticalDisplacement, horizontalDisplacement)){
                if(isEmptySpace(verticalDisplacement, horizontalDisplacement) ||
                isEnemy(verticalDisplacement, horizontalDisplacement)){
                    addToLegalMoves(verticalDisplacement, horizontalDisplacement);
                }
            }
        }

        //Sorts it to the universal moves containers
        addToAllMoves((ChessBoard)board);
    }

    @Override
    public void move(int height, int width) throws IllegalAccessException{
        try{
            //The super function checks move legality
            super.move(height, width);
        }catch(IllegalAccessException e){
            throw e;
        }catch(Exception e){
            System.out.println("Unknown error occurred");
            System.exit(1);
        }

        board.getGrid()[height][width] = this;
        board.getGrid()[currentHeight][currentWidth] = null;

        currentHeight = height;
        currentWidth = width;

        chessBoard.setLastMovedPiece(this);
    }

    void calculateLegalInitialMoves(){
        int[] tempVerticalMoves = getMoves().getVerticalMoves();
        int[] tempHorizontalMoves = getMoves().getHorizontalMoves();
        for(int i = 0; i < tempVerticalMoves.length; ++i){
            int verticalDisplacement = currentHeight + tempVerticalMoves[i];
            int horizontalDisplacement = currentWidth + tempHorizontalMoves[i];
            //Make sure the knight's moves are well within range before adding to legalMoves
            if(inBound(verticalDisplacement, horizontalDisplacement))
                if(isEmptySpace(verticalDisplacement, horizontalDisplacement)){
                    addToLegalMoves(verticalDisplacement,  horizontalDisplacement);
                }
        }
    }
}
