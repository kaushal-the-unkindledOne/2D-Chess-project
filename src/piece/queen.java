package piece;

import main.Type;
import main.gamePanel;

public class queen extends Piece{

	public queen(int color, int col, int row) {
		super(color, col, row);
		type = Type.QUEEN;
		
		if(color == gamePanel.WHITE) {
			image = getImage("/piece/w-queen");
		}
		else {
			image = getImage("/piece/b-queen");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol , targetRow) 
		&& isSameSquare(targetCol, targetRow) == false) {
			//VERTICAL AND HORIZONTAL
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && 
						pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		//DIAGONAL
			if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if(isValidSquare(targetCol, targetRow) && 
						pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}
}
