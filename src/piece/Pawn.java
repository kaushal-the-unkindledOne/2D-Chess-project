package piece;

import main.Type;
import main.gamePanel;

public class Pawn extends Piece{

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		type = Type.PAWN;
		
		if(color == gamePanel.WHITE) {
			image = getImage("/piece/w-pawn");
		}
		else {
			image = getImage("/piece/b-pawn");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol,targetRow) == false) {
			//DEFINE ITS MOVES BASED ON COLOR
			int moveValue;
			if(color == gamePanel.WHITE) {
				moveValue = -1;
			}
			else {
				moveValue = 1;
			}
			//CHECK THE HITTING PIECE
			hittingP = getHittingP(targetCol, targetRow);
				//1 SQUARE MOVEMENT
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			
			//2 SQUARE MOVEMENT
			if(targetCol == preCol && targetRow == preRow + moveValue*2 && 
					hittingP == null && moved == false && pieceIsOnStraightLine(targetCol,targetRow) == false) {
				return true;
			}
			//DIAGONAL MOVEMENT AND CAPTURE IF IN DIAGONAL MOVE
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue
					&& hittingP != null && hittingP.color != color) {
				return true;
			}
			
			//EN PASSANT
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
				for(Piece piece : gamePanel.simPieces) {
					if(piece.col == targetCol && piece.row == preRow &&
							piece.twoStep == true) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		return false;
		
	}

}
