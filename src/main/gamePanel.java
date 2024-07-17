package main;

import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.*;

import piece.Pawn;
import piece.Piece;
import piece.bishop;
import piece.king;
import piece.knight;
import piece.queen;
import piece.rook;

public class gamePanel extends JPanel implements Runnable{
	
	public static final int WIDTH = 1100;
	public static final int HEIGHT = 800;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	//PIECES
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece> promoPieces = new ArrayList<>();
	Piece activeP, checkingP;
	public static Piece castlingP;
	
	//COLOR
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;
	
	//MOVEMENT
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameover;
	boolean stalemate;
	
	public gamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		
		setPieces();
		//testPromotion();
		//illegalTest();
		//testStalemate();
		
		copyPieces(pieces, simPieces);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void setPieces() {
		
		//WHITE PLAYER
		pieces.add(new Pawn(WHITE,0,6));
		pieces.add(new Pawn(WHITE,1,6));
		pieces.add(new Pawn(WHITE,2,6));
		pieces.add(new Pawn(WHITE,3,6));
		pieces.add(new Pawn(WHITE,4,6));
		pieces.add(new Pawn(WHITE,5,6));
		pieces.add(new Pawn(WHITE,6,6));
		pieces.add(new Pawn(WHITE,7,6));
		pieces.add(new knight(WHITE,1,7));
		pieces.add(new knight(WHITE,6,7));
		pieces.add(new rook(WHITE,0,7));
		pieces.add(new rook(WHITE,7,7));
		pieces.add(new bishop(WHITE,2,7));
		pieces.add(new bishop(WHITE,5,7));
		pieces.add(new queen(WHITE,3,7));
		pieces.add(new king(WHITE,4,7));
		
		//BLACK PLAYER
		pieces.add(new Pawn(BLACK,0,1));
		pieces.add(new Pawn(BLACK,1,1));
		pieces.add(new Pawn(BLACK,2,1));
		pieces.add(new Pawn(BLACK,3,1));
		pieces.add(new Pawn(BLACK,4,1));
		pieces.add(new Pawn(BLACK,5,1));
		pieces.add(new Pawn(BLACK,6,1));
		pieces.add(new Pawn(BLACK,7,1));
		pieces.add(new knight(BLACK,1,0));
		pieces.add(new knight(BLACK,6,0));
		pieces.add(new rook(BLACK,0,0));
		pieces.add(new rook(BLACK,7,0));
		pieces.add(new bishop(BLACK,2,0));
		pieces.add(new bishop(BLACK,5,0));
		pieces.add(new queen(BLACK,3,0));
		pieces.add(new king(BLACK,4,0));
	}
	
	public void testPromotion() {
		pieces.add(new Pawn(WHITE,0,3));
		pieces.add(new Pawn(BLACK,5,4));
	}
	
	public void illegalTest() {
		pieces.add(new Pawn(WHITE,7,6));
		pieces.add(new king(WHITE,3,7));
		pieces.add(new king(BLACK,0,3));
		pieces.add(new bishop(BLACK,1,4));
		pieces.add(new queen(BLACK,4,5));
	}
	
	public void testStalemate() {
		pieces.add(new king(BLACK,0,3));
		pieces.add(new king(WHITE,2,4));
		pieces.add(new queen(WHITE,2,1));
	}
	
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		
		target.clear();
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		double lastTime = System.nanoTime();
		double currentTime;
      
        while(gameThread != null){
        
        	currentTime = System.nanoTime();
        	delta += (currentTime - lastTime)/drawInterval;
        	lastTime = currentTime;
        	
        	if(delta >= 1) {
        		update();
        		repaint();
        		delta--;
        	}
        }
	}
	
	public void update() {
		
		//CHECK FOR PROMOTION
		if(promotion) {
			promoting();
		}
		
		else if(gameover == false && stalemate == false){
			if(mouse.pressed) {
				if(activeP == null) {
				//IF ACTIVEP IS NULL,CHECK IF CAN PICK UP A PIECE
					for(Piece piece : simPieces) {
				//IF MOUSE ON ALLY PIECE, PICK IT UP
						if(piece.color == currentColor &&
								piece.col == mouse.x/Board.SQUARE_SIZE &&
								piece.row == mouse.y/Board.SQUARE_SIZE) {
							activeP = piece;
						}
					}
				}
				else {
				//IF PLAYER IS HOLDING A PIECE, SIMULATE THE MOVEMENT
					simulate();
					
				}
			}
			//MOUSE BUTTON RELEASED
			if(mouse.pressed == false) {
				if(activeP != null) {
					if(validSquare) {
						
						//UPDATE PIECE LIST IN PIECE CASE
						
						copyPieces(simPieces, pieces);
						activeP.updatePosition();
						if(castlingP != null) {
							castlingP.updatePosition();
						}
						
						if(isKingInCheck() && isCheckMate()) {
							gameover = true;
						}
						
						else if(isStalemate() && isKingInCheck() == false) {
							stalemate = true;
						}
						
						else {
							if(canPromote()) {
								promotion = true;
							}
							else {
								changePlayer();
							}
						}
					}
					
					else {
						copyPieces(pieces, simPieces);
						activeP.resetPosition();
						activeP = null;
					}
				}
			}
		}
	}
	
	private void simulate() {
		
		canMove = false;
		validSquare = false;
		
		//RESET PIECE IN EVERY LOOP
		copyPieces(pieces, simPieces);
		
		//RESET THE CASTLING
		if(castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.col);
			castlingP = null;
		}
		
		activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
		activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);
		
		//CHECK IF PIECE IS OVER MOVEABLE SQUARE
		if(activeP.canMove(activeP.col, activeP.row)) {
			canMove = true;
			
			//IF HITTING A PIECE , REMOVE IT
			if(activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			checkCastling();
			if(isIllegal(activeP)== false && opponentCanCaptureKing() == false) {
				validSquare = true;
			}
		}
	}
	
	private boolean isIllegal(Piece king) {
		if(king.type == Type.KING) {
			for(Piece piece : simPieces) {
				if(piece != king && piece.color != king.color && 
						piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean opponentCanCaptureKing() {
		Piece king = getKing(false);
		for(Piece piece : simPieces) {
			if(piece.color != king.color && piece.canMove(king.col, king.row)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isKingInCheck() {
		Piece king = getKing(true);
		if(activeP.canMove(king.col, king.row)) {
			checkingP = activeP;
			return true;
		}
		else {
			checkingP = null;
		}
		return false;
	}
	
	private Piece getKing(boolean opponent) {
		Piece king = null;
		for(Piece piece : simPieces) {
			if(opponent) {
				if(piece.type == Type.KING && piece.color != currentColor) {
					king = piece;
				}
			}
			
			else {
				if(piece.type == Type.KING && piece.color == currentColor){
					king = piece;
				}
			}
		}
		return king;
	}
	
	private boolean isCheckMate() {
		Piece king = getKing(true);
		
		if(kingCanMove(king)) {
			return false;
		}
		
		else {
			//CANNOT MOVE TO SAFE SQUARE , CHECK IF CAN BLOCK ATTACK WITH PIECE
			//CHECK POSITION OF CHECKING PIECE AND KING IN CHECK
			int colDiff = Math.abs(checkingP.col - king.col);
			int rowDiff = Math.abs(checkingP.row - king.row); 
			
			if(colDiff == 0) {
				//GETTING ATTACK VERTICALLY
				if(checkingP.row < king.row) {
					//CHECKING PIECE IS ABOVE THE KING
					for(int row = checkingP.row; row < king.row; row++) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor &&
									piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.row > king.row) {
					//CHECKING PIECE IS BELOW THE KING
					for(int row = checkingP.row; row > king.row; row--) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor &&
									piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
			}
			
			else if(rowDiff == 0){
				//GETTING ATTACK HORIZONTALLY
				if(checkingP.col < king.col) {
					//CHECKING PIECE IS LEFT THE KING
					for(int col = checkingP.row; col < king.col; col++) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor &&
									piece.canMove(col,checkingP.row)) {
								return false;
							}
						}
					}
				}
				
				if(checkingP.col > king.col) {
					//CHECKING PIECE IS RIGHT THE KING
					for(int col = checkingP.col; col > king.col; col--) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor &&
									piece.canMove(col,checkingP.row)) {
								return false;
							}
						}
					}
				}
			}
			
			else if(colDiff == rowDiff) {
				//GETTING ATTACK DIAGONALLY
				if(checkingP.row < king.row) {
					//CHECKING PIECE IS ABOVE THE KING
					if(checkingP.col < king.col) {
						//checking piece is in upper left
						for(int col = checkingP.col, row = checkingP.row;
								col < king.col; col++, row++) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor 
										&& piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					
					if(checkingP.col > king.col) {
						//checking piece is in upper right
						for(int col = checkingP.col, row = checkingP.row;
								col > king.col; col--, row++) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor 
										&& piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
				
				if(checkingP.row > king.row) {
					//CHECKING PIECE IS BELOW THE KING
					if(checkingP.col < king.col) {
						//checking piece is in lower left
						for(int col = checkingP.col, row = checkingP.row;
								col < king.col; col++, row--) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor 
										&& piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					
					if(checkingP.col > king.col) {
						//checking piece is in lower right
						for(int col = checkingP.col, row = checkingP.row;
								col > king.col; col--, row--) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor 
										&& piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	private boolean kingCanMove(Piece king) {
		//SIMUALTE ANY SQUARE WHERE KING CAN MOVE
		if(isValidMove(king,-1,-1)) {return true;}
		if(isValidMove(king,0,-1)) {return true;}
		if(isValidMove(king,1,-1)) {return true;}
		if(isValidMove(king,-1,0)) {return true;}
		if(isValidMove(king,1,0)) {return true;}
		if(isValidMove(king,-1,1)) {return true;}
		if(isValidMove(king,0,1)) {return true;}
		if(isValidMove(king,1,1)) {return true;}
		
		return false;
	}
	
	private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
		
		boolean isValidMove = false;
		//UPDATE KING'S POSITION FOR A SECOND
		king.col += colPlus;
		king.row += rowPlus;
		
		if(king.canMove(king.col,  king.row)) {
			if(king.hittingP != null) {
				simPieces.remove(king.hittingP.getIndex());
			}
			if(isIllegal(king) == false) {
				isValidMove = true;
			}
		}
		//RESET KING POSITION AND RESTORE PIECES
		king.resetPosition();
		copyPieces(pieces, simPieces);
		
		return isValidMove;
	}
	
    private boolean isStalemate() {
    	
    	int count = 0;
    	//COUNT NUMBER OF PIECES
    	for(Piece piece : simPieces) {
    		if(piece.color != currentColor) {
    			count++;
    		}
    	}
    	//IF ONLY KING IS LEFT
    	if(count == 1) {
    		if(kingCanMove(getKing(true)) == false) {
    			return true;
    		}
    	}
    	return false;
    }
	
	private void checkCastling() {
		if(castlingP != null) {
			if(castlingP.col == 0) {
				castlingP.col += 3;
			}
			else if(castlingP.col == 7) {
				castlingP.col -= 2;
			}
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}
	
	private void changePlayer() {
		if(currentColor == WHITE) {
			currentColor = BLACK;
			//RESET BACK 2 STEP STATUS
			for(Piece piece : pieces) {
				if(piece.color == BLACK) {
					piece.twoStep = false;
				}
			}
		}
		else {
			currentColor = WHITE;
			//RESET 2 STEP STATUS
			for(Piece piece : pieces) {
				if(piece.color == WHITE) {
					piece.twoStep = false;
				}
			}
		}
		activeP = null;
	}
	
	private boolean canPromote() {
		if(activeP.type == Type.PAWN) {
			if(currentColor == WHITE && activeP.row == 0 ||
					currentColor == BLACK && activeP.row == 7) {
				promoPieces.clear();
				promoPieces.add(new rook(currentColor,9,2));
				promoPieces.add(new knight(currentColor,9,3));
				promoPieces.add(new bishop(currentColor,9,4));
				promoPieces.add(new queen(currentColor,9,5));
				return true;
			}
		}
		return false;
	}
	
	private void promoting() {
		if(mouse.pressed) {
			for(Piece piece : promoPieces) {
				if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
					switch(piece.type) {
					case ROOK : simPieces.add(new rook(currentColor,activeP.col, activeP.row)); break;
					case KNIGHT : simPieces.add(new knight(currentColor,activeP.col, activeP.row)); break;
					case BISHOP : simPieces.add(new bishop(currentColor,activeP.col, activeP.row)); break;
					case QUEEN : simPieces.add(new queen(currentColor,activeP.col, activeP.row)); break;
					default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces, pieces);
					activeP = null;
					promotion = false;
					changePlayer();
				}
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		//BOARD
		board.draw(g2);
		
		//PIECES
		for(Piece p : simPieces) {
			p.draw(g2);
		}
		
		//SQUARE
		if(activeP != null) {
			if(canMove) {
				if(isIllegal(activeP) || opponentCanCaptureKing()) {
					g2.setColor(Color.red);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				else {
					g2.setColor(Color.green);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}
			
			//DARW ACTIVE PIECE IN THE END
			activeP.draw(g2);
		}
		//STATUS INFO
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.WHITE);
		
		if(promotion) {
			g2.drawString("Promote to:", 840, 150);
			for(Piece piece : promoPieces) {
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
						Board.SQUARE_SIZE, Board.SQUARE_SIZE,null);
			}
		}
		else {
			if(currentColor == WHITE) {
				g2.drawString("White's Turn", 840, 550);
				if(checkingP != null && checkingP.color == BLACK) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 650);
					g2.drawString("is in check!!", 840, 700);
				}
			}
			else {
				g2.drawString("Black's Turn", 840, 250);
				if(checkingP != null && checkingP.color == WHITE) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 100);
					g2.drawString("is in check!!", 840, 150);
				}
			}
		}
		if(gameover) {
			String s = "";
			if(currentColor == WHITE) {
				s = "White wins!!!";
			}
			else {
				s = "Black wins!!!";
			}
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.green);
			g2.drawString(s, 200, 420);
		}
		if(stalemate) {
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.black);
			g2.drawString("Stalemate!!", 200, 420);
		}
	}
}
