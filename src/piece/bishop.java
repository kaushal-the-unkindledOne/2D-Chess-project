package piece;

import main.Type;
import main.gamePanel;

public class bishop extends Piece{

	public bishop(int color, int col, int row) {
		super(color, col, row);
		type = Type.BISHOP;
		
		if(color == gamePanel.WHITE) {
			image = getImage("/piece/w-bishop");
		}
		else {
			image = getImage("/piece/b-bishop");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {//DIAGIONALLY
		if(isWithinBoard(targetCol , targetRow) 
		&& isSameSquare(targetCol, targetRow) == false) {
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
