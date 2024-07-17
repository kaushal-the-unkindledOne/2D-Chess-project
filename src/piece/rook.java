package piece;

import main.Type;
import main.gamePanel;

public class rook extends Piece{

	public rook(int color, int col, int row) {
		super(color, col, row);
		type = Type.ROOK;
		
		if(color == gamePanel.WHITE) {
			image = getImage("/piece/w-rook");
		}
		else {
			image = getImage("/piece/b-rook");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol , targetRow) 
		&& isSameSquare(targetCol, targetRow) == false) {
			//CAN MOVE ANY NO OF SQUARE VERTICALLY OR HORIZONTALLY
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && 
						pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}

}
