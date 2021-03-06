package Chess;
public class SelectSquareState implements MoveState
{
	private ChessBoard board;
	private ChessPiece piece;
	
	public SelectSquareState(ChessBoard board){this.board = board;}
	public void setSelectedPiece(ChessPiece piece){	this.piece = piece;}
	public void mouseClicked(int x, int y)
	{
		//System.out.println("In select square state");
		if(piece == null) return; //no piece is selected
		//System.out.println(piece);
		//get square selected
		SquareCenter piece_loc = piece.getCenterLocation();
		SquareCenter square_selected = board.getSquareClicked(x,y);
		
		
		
		boolean legal =piece.legalMoveSelected(square_selected);// board.legalMoveSelected(x,y);
		
		if(square_selected != null)
		{
			
			if(piece instanceof BlackKing)
			{
				if(board.doesWhiteAttackSquare(square_selected))
				{
					cancel();
					return;
				}
			}
			if(piece instanceof BlackRook || piece instanceof BlackPawn || piece instanceof BlackBishop || piece instanceof BlackKnight || piece instanceof BlackQueen)
			{
				System.out.println("It's a black piece");
				if(doesMovePutBlackKingInCheck(square_selected))
				{
					cancel(); return;
				}
			}
			
			if(piece instanceof WhiteKing)
			{
				if(board.doesBlackAttackSquare(square_selected))
				{
					
					cancel();
					return;
				}
			}
			
			if(piece instanceof WhiteRook || piece instanceof WhitePawn || piece instanceof WhiteBishop || piece instanceof WhiteKnight || piece instanceof WhiteQueen)
			{
				System.out.println("It's a white piece");
				if(doesMovePutWhiteKingInCheck(square_selected))
				{
					
					cancel(); return;
				}
			}
			//see if castle
			if(piece instanceof King)
			{
				if(((King)piece).castle(square_selected))
				{
					move(null);
					return;
				}
			}
			//see if move is capture
			ChessPiece to_capture = board.anyPieceOnSquare(x,y);
			if((board.whitePieceOnSquare(piece)&&board.whitePieceOnSquare(to_capture)) || (board.blackPieceOnSquare(piece)&&board.blackPieceOnSquare(to_capture)))
			{
				piece.setSelected(false);
				piece = to_capture;
				piece.highlightMoves();
				//return?
			}
			//if to_capture is an ally
			//set current piece selected to false
			//set currentpiece to to_capture

			//highlight moves
			//move
			String old_loc = piece.getCenterLocation().getID();
			//todo remove
			//board.removeAttacked(piece);
			board.updateAttackedForAllPieces();
			boolean res = piece.move(square_selected);
			//System.out.println(res);
			
			if(res)
			{
				move(to_capture);	//did pawn move forward two
				if(piece instanceof Pawn)
				{
					
					String id = piece.getCenterLocation().getID();
					//System.out.println(old_loc + " " + id);
					char[] blah = new char[2];
					blah[0]= old_loc.charAt(0);
					blah[1] = (piece instanceof WhitePawn)?(char)((int)old_loc.charAt(1)+2):(char)((int)old_loc.charAt(1)-2);
					String end = new String(blah);
					//System.out.println(end);
					if(id.equalsIgnoreCase(end))
					{
						//System.out.println("We did it");
						((Pawn)piece).setTwoForward(true);
					}
					else{((Pawn)piece).setTwoForward(false);}
				}
				if(piece instanceof Pawn)
				{
					//System.out.println("Can enpassant");
					ChessPiece cp  = ((Pawn) piece).enPassant();
					if(cp != null)board.removePiece(cp);
				}
				if(piece instanceof WhitePawn)
				{
					String id = piece.getCenterLocation().getID();
					if(id.equalsIgnoreCase("A8")||id.equalsIgnoreCase("B8")||id.equalsIgnoreCase("C8")||id.equalsIgnoreCase("D8")||id.equalsIgnoreCase("E8")||id.equalsIgnoreCase("F8")||id.equalsIgnoreCase("G8")||id.equalsIgnoreCase("H8"))
					{
						board.pawnPromotion((Pawn)piece);
					}
				}
				if(piece instanceof BlackPawn)
				{
					String id = piece.getCenterLocation().getID();
					if(id.equalsIgnoreCase("A1")||id.equalsIgnoreCase("B1")||id.equalsIgnoreCase("C1")||id.equalsIgnoreCase("D1")||id.equalsIgnoreCase("E1")||id.equalsIgnoreCase("F1")||id.equalsIgnoreCase("G1")||id.equalsIgnoreCase("H1"))
					{
						board.pawnPromotion((Pawn)piece);
					}
				}
				board.updateAttackedForAllPieces(); //just... curious
				board.setInCheck();
				board.alertPlayer();
			}
			//else{System.out.println(piece + " not moved");}
		}
		else
		{
			//change back, an illegal selections means they want to cancel action
			 
			cancel();
		}
			
	}
	
	public void cancel()	
	{
		piece.setSelected(false);
		board.changeState(board.getSelectPieceState());
		//System.out.println(piece + " not moved");
		piece = null;
	}
	
	public void move(ChessPiece to_capture)
	{
		
		//change back to selectPieceState, deselecting piece
		
		board.changeState(board.getSelectPieceState());
		if(to_capture != null)
		{
			//do stuff to capture piece
			to_capture.isCaptured();
			board.removePiece(to_capture);
		}
		piece.incrementMoveCount();
		piece.setSelected(false);
		board.changeTurns();
		
		//set the attacked pieces
		board.updateAttackedForAllPieces();//board.addAttacked(piece); // <-write a method to update master list of attacked pieces
		//board.cullIllegalMoves();
		
		//if king is attacked
		//put board in check
		
		//System.out.println(piece + "moved");
	}
	
	public boolean doesMovePutWhiteKingInCheck(SquareCenter loc)
	{
		if(piece.doesMovePutWhiteKingInCheck(loc))
		{
			SimpleDialogs.normalOutput("This move puts White King in check!", "HEY! Listen");
			return true;
		}
		
		return false;
	}
	
	public boolean doesMovePutBlackKingInCheck(SquareCenter loc)
	{
		if(piece.doesMovePutBlackKingInCheck(loc))
		{
			SimpleDialogs.normalOutput("This move puts Black King in check!", "HEY! Listen");
			return true;
		}
		
		return false;
	}
}