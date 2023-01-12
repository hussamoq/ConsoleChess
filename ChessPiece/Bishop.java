package ChessPiece;
import Mobility.Moves;
import Board.*;
import Utility.Pair;

public final class Bishop extends ChessPiece {
    private final ChessBoard chessBoard;

    public Bishop(Color color, Moves moves, int width, int height, Board board){
        super(color, moves, width, height, board);
        chessBoard = (ChessBoard)board;
    }

    @Override
    public String getName(){
        return "Bishop";
    }

    @Override
    public void move(int height, int width) throws IllegalAccessException{
        try{
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
    }

    @Override
    public void recalculateLegalMoves(){
        super.recalculateLegalMoves();

        int[] tempVerticalMoves = getMoves().getVerticalMoves();
        int[] tempHorizontalMoves = getMoves().getHorizontalMoves();
        for(int i = 0; i < 4; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = currentHeight + tempVerticalMoves[j];
                int horizontalDisplacement = currentWidth + tempHorizontalMoves[j];
                if(notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(isNotEmptySpace(verticalDisplacement, horizontalDisplacement))
                    if(isNotEnemy(verticalDisplacement, horizontalDisplacement)){
                        addPotentialAttack(j);
                        break;
                    }

                addToLegalMoves(verticalDisplacement, horizontalDisplacement);
                //Since we found an enemy, we can't skip him, we can only attack him and take his place
                if(isNotEmptySpace(verticalDisplacement, horizontalDisplacement))
                    if(isEnemy(verticalDisplacement, horizontalDisplacement)){
                        if(isKing(verticalDisplacement, horizontalDisplacement))
                            chessBoard.setCheckingPiece(this);
                        break;
                    }
            }
        }

        //Sorts it to the universal moves containers
        addToAllMoves(chessBoard);
    }
}
