package Mobility;

public class KingMoves extends Moves{
    public KingMoves(){
        //King moves to the adjacent 8 blocks making up 8 total moves
        super(8);
        verticalMoves = new int[totalNumberOfMoves];
        horizontalMoves = new int[totalNumberOfMoves];
        initializeMoves();
    }

    @Override
    void initializeMoves(){
        //give the king the 8 adjacent blocks
        int index = 0;
        for(int i = -1; i < 2; ++i){
            for(int j = -1; j < 2; ++j){
                if(j == 0 && i == 0)
                    continue;

                verticalMoves[index] = i;
                horizontalMoves[index] = j;
                ++index;
            }
        }
    }
}
